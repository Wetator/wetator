/*
 * Copyright (c) 2008-2016 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.commandset;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.control.ISettable;
import org.wetator.core.Command;
import org.wetator.core.ForceExecution;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.Variable;
import org.wetator.core.WetatorContext;
import org.wetator.core.searchpattern.ContentPattern;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.exception.CommandException;
import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

import com.github.rjeschke.txtmark.Processor;

/**
 * The implementation of all build in command that Wetator
 * supports at the moment.
 *
 * @author rbri
 * @author frank.danek
 */
public final class DefaultCommandSet extends AbstractCommandSet {

  @Override
  protected void registerCommands() {
    registerCommand("describe", new CommandDescribe());

    registerCommand("open-url", new CommandOpenUrl());
    registerCommand("use-module", new CommandUseModule());
    registerCommand("close-window", new CommandCloseWindow());
    registerCommand("go-back", new CommandGoBack());

    registerCommand("click-on", new CommandClickOn());
    registerCommand("click-double-on", new CommandClickDoubleOn());
    registerCommand("click-right-on", new CommandClickRightOn());
    registerCommand("set", new CommandSet());
    registerCommand("select", new CommandSelect());
    registerCommand("deselect", new CommandDeselect());
    registerCommand("mouse-over", new CommandMouseOver());

    registerCommand("assert-title", new CommandAssertTitle());
    registerCommand("assert-content", new CommandAssertContent());
    registerCommand("assert-enabled", new CommandAssertEnabled());
    registerCommand("assert-disabled", new CommandAssertDisabled());
    registerCommand("assert-set", new CommandAssertSet());
    registerCommand("assert-selected", new CommandAssertSelected());
    registerCommand("assert-deselected", new CommandAssertDeselected());

    registerCommand("exec-java", new CommandExecJava());
  }

  /**
   * Command 'Describe'.
   */
  public final class CommandDescribe implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final SecretString tmpMarkdown = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final String tmpHtml = Processor.process(tmpMarkdown.toString());
      aContext.informListenersHtmlDocu(tmpHtml.trim());
    }
  }

  /**
   * Command 'Open Url'.
   */
  public final class CommandOpenUrl implements ICommandImplementation {
    private static final String SLASH = "/";

    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      SecretString tmpUrlParam = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      try {
        // create the complete URL
        final String tmpUrlToLower = tmpUrlParam.toLowerCase(Locale.ENGLISH);
        if (tmpUrlToLower.startsWith("http://") || tmpUrlToLower.startsWith("https://")
            || tmpUrlToLower.startsWith("file://")) {
          aContext.informListenersWarn("absoluteUrl", new String[] { tmpUrlParam.toString() });
        } else {
          final String tmpBaseUrl = aContext.getConfiguration().getBaseUrl();
          // a bit url cleanup - remove or add the slash before combining with the config
          if (tmpUrlParam.startsWith(SLASH) && tmpBaseUrl.endsWith(SLASH)) {
            tmpUrlParam = tmpUrlParam.substring(1);
          } else if (!tmpUrlParam.startsWith(SLASH) && !tmpBaseUrl.endsWith(SLASH)) {
            tmpUrlParam.prefixWith(SLASH);
          }
          tmpUrlParam.prefixWith(tmpBaseUrl);
        }

        final URL tmpUrl = new URL(tmpUrlParam.getValue());
        // TODO maybe there is an error
        // think about url's containing secrets
        aContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });

        final IBrowser tmpBrowser = getBrowser(aContext);
        tmpBrowser.openUrl(tmpUrl);
      } catch (final MalformedURLException e) {
        final String tmpMessage = Messages.getMessage("invalidUrl",
            new String[] { tmpUrlParam.toString(), e.getMessage() });
        throw new InvalidInputException(tmpMessage);
      }
      getBrowser(aContext).saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Use Module'.
   */
  @ForceExecution
  public static final class CommandUseModule implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final SecretString tmpModuleParam = aCommand.getRequiredFirstParameterValue(aContext);
      final List<SecretString> tmpModuleParameters = aCommand.getSecondParameterValues(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final String tmpModule = tmpModuleParam.getValue();

      File tmpFile = new File(tmpModule);

      if (!tmpFile.isAbsolute()) {
        tmpFile = new File(aContext.getFile().getParent(), tmpModule);
        aContext.informListenersInfo("useModule", new String[] { tmpFile.getAbsolutePath() });
      }

      // check file
      if (!tmpFile.exists() || !tmpFile.isFile()) {
        final String tmpMessage = Messages.getMessage("moduleFileNotFound",
            new String[] { FilenameUtils.normalize(tmpFile.getAbsolutePath()) });
        throw new InvalidInputException(tmpMessage);
      }

      // setup the new context
      final WetatorContext tmpWetatorContext = aContext.createSubContext(tmpFile);

      int i = 1;
      for (final SecretString tmpSecretString : tmpModuleParameters) {
        final Variable tmpVariable = new Variable(Integer.toString(i), tmpSecretString);
        tmpWetatorContext.addVariable(tmpVariable);
        i++;
      }

      tmpWetatorContext.execute();
    }
  }

  /**
   * Command 'Set'.
   */
  public final class CommandSet implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getFirstParameterValue(aContext), aContext.getConfiguration());

      SecretString tmpValueParam = aCommand.getSecondParameterValue(aContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("");
      }
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);

      ISettable tmpControl = null;
      if (tmpWPath.isEmpty()) {
        // if the wpath is empty first try the currently focused control
        try {
          final IControl tmpFocusedControl = tmpBrowser.getFocusedControl();
          if (tmpFocusedControl instanceof ISettable) {
            tmpControl = (ISettable) tmpFocusedControl;
          }
          if (tmpControl != null) {
            aContext.informListenersWarn("focusedElementUsed", new String[] { tmpControl.getDescribingText() });
          }
        } catch (final BackendException e) {
          final String tmpMessage = Messages.getMessage("commandBackendError", new String[] { e.getMessage() });
          throw new ActionException(tmpMessage, e);
        }
      }

      if (tmpControl == null) {
        final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

        // TextInputs / PasswordInputs / TextAreas / FileInputs
        final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);

        if (tmpWPath.isEmpty()) {
          // if the wpath is empty use the first 'usable' field on the page
          for (final WeightedControlList.Entry tmpEntry : tmpFoundElements.getEntriesSorted()) {
            tmpControl = (ISettable) tmpEntry.getControl();
            if (!tmpControl.isDisabled(aContext)) {
              break;
            }
          }

          if (null == tmpControl) {
            final String tmpMessage = Messages.getMessage("noSettableHtmlElmentFound",
                new String[] { tmpWPath.toString() });
            throw new ActionException(tmpMessage);
          }
          aContext.informListenersWarn("firstElementUsed", new String[] { tmpControl.getDescribingText() });
        } else {
          tmpControl = (ISettable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
              "noSettableHtmlElmentFound");
        }
      }

      tmpBrowser.markControls(tmpControl);
      tmpControl.setValue(aContext, tmpValueParam, aContext.getFile().getParentFile());
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Click On'.
   */
  public final class CommandClickOn implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // Buttons / Link / Image
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllClickables(tmpWPath);

      // Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noClickableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.click(aContext);
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Click Double On'.
   */
  public final class CommandClickDoubleOn implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "no2ClickableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.clickDouble(aContext);
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Click Right On'.
   */
  public final class CommandClickRightOn implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noRClickableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.clickRight(aContext);
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Select'.
   */
  public final class CommandSelect implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements,
          tmpWPath, "noSelectableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.select(aContext);
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Deselect'.
   */
  public final class CommandDeselect implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // (Select)Options / Checkboxes
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllDeselectables(tmpWPath);

      final IDeselectable tmpControl = (IDeselectable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements,
          tmpWPath, "noDeselectableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.deselect(aContext);
      tmpBrowser.saveCurrentWindowToLog(tmpControl);
    }
  }

  /**
   * Command 'Mouse Over'.
   */
  public final class CommandMouseOver implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.mouseOver(aContext);
      tmpBrowser.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'close Window'.
   */
  public final class CommandCloseWindow implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final SecretString tmpWindowNameParam = aCommand.getFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);

      tmpBrowser.closeWindow(tmpWindowNameParam);
      tmpBrowser.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Go Back'.
   */
  public final class CommandGoBack implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final SecretString tmpStepsParam = aCommand.getFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      int tmpSteps = 1;
      if (!tmpStepsParam.isEmpty()) {
        try {
          tmpSteps = Integer.parseInt(tmpStepsParam.getValue());
        } catch (final NumberFormatException e) {
          final String tmpMessage = Messages.getMessage("stepsNotANumber", new String[] { tmpStepsParam.toString(),
              Integer.toString(tmpSteps) });
          throw new InvalidInputException(tmpMessage);
        }
      }

      final IBrowser tmpBrowser = getBrowser(aContext);

      tmpBrowser.goBackInCurrentWindow(tmpSteps);
      tmpBrowser.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Assert Title'.
   */
  public final class CommandAssertTitle implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final ContentPattern tmpPattern = new ContentPattern(aCommand.getRequiredFirstParameterValue(aContext));
      Long tmpTimeout = aCommand.getSecondParameterLongValue(aContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }
      aCommand.checkNoUnusedThirdParameter(aContext);

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final IBrowser tmpBrowser = getBrowser(aContext);

      try {
        final boolean tmpContentChanged = tmpBrowser.assertTitleInTimeFrame(tmpPattern, tmpTimeout);
        if (tmpContentChanged) {
          tmpBrowser.saveCurrentWindowToLog();
        }
      } catch (final AssertionException e) {
        // save the current window if failed
        tmpBrowser.saveCurrentWindowToLog();
        throw e;
      }
    }
  }

  /**
   * Command 'Assert Content'.
   */
  public final class CommandAssertContent implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final ContentPattern tmpPattern = new ContentPattern(aCommand.getRequiredFirstParameterValue(aContext));
      Long tmpTimeout = aCommand.getSecondParameterLongValue(aContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }
      aCommand.checkNoUnusedThirdParameter(aContext);

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final IBrowser tmpBrowser = getBrowser(aContext);

      try {
        final boolean tmpContentChanged = tmpBrowser.assertContentInTimeFrame(tmpPattern, tmpTimeout);
        if (tmpContentChanged) {
          tmpBrowser.saveCurrentWindowToLog();
        }
      } catch (final AssertionException e) {
        // save the current window if failed
        tmpBrowser.saveCurrentWindowToLog();
        throw e;
      }
    }
  }

  /**
   * Command 'Assert Enabled'.
   */
  public final class CommandAssertEnabled implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

      tmpBrowser.markControls(tmpControl);
      final boolean tmpIsDisabled = tmpControl.isDisabled(aContext);
      Assert.assertFalse(tmpIsDisabled, "elementNotEnabled", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Disabled'.
   */
  public final class CommandAssertDisabled implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

      tmpBrowser.markControls(tmpControl);
      final boolean tmpIsDisabled = tmpControl.isDisabled(aContext);
      Assert.assertTrue(tmpIsDisabled, "elementNotDisabled", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Set'.
   */
  public final class CommandAssertSet implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      SecretString tmpValueParam = aCommand.getSecondParameterValue(aContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("");
      }
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);

      final ISettable tmpControl = (ISettable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noSettableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      tmpControl.assertValue(aContext, tmpValueParam);
    }
  }

  /**
   * Command 'Assert Selected'.
   */
  public final class CommandAssertSelected implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements,
          tmpWPath, "noSelectableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      final boolean tmpIsSelected = tmpControl.isSelected(aContext);
      Assert.assertTrue(tmpIsSelected, "elementNotSelected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Assert Deselected'.
   */
  public final class CommandAssertDeselected implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements,
          tmpWPath, "noDeselectableHtmlElmentFound");

      tmpBrowser.markControls(tmpControl);
      final boolean tmpIsSelected = tmpControl.isSelected(aContext);
      Assert.assertFalse(tmpIsSelected, "elementNotDeselected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Exec Java'.
   */
  @ForceExecution
  public static final class CommandExecJava implements ICommandImplementation {
    /**
     * {@inheritDoc}
     *
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
    InvalidInputException {
      final SecretString tmpCall = aCommand.getRequiredFirstParameterValue(aContext);
      final List<SecretString> tmpMethodParameters = aCommand.getSecondParameterValues(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final String tmpCallString = tmpCall.toString();
      final int tmpLastDotPos = tmpCallString.lastIndexOf('.');
      if (tmpLastDotPos < 0) {
        final String tmpMessage = Messages.getMessage("javaExecSyntax", new String[] { tmpCallString });
        throw new InvalidInputException(tmpMessage);
      }

      String tmpClassName = tmpCallString.substring(0, tmpLastDotPos);
      if (StringUtils.isEmpty(tmpClassName)) {
        final String tmpMessage = Messages.getMessage("javaExecSyntax", new String[] { tmpCallString });
        throw new InvalidInputException(tmpMessage);
      }
      if (tmpClassName.endsWith("()")) {
        tmpClassName = tmpClassName.substring(0, tmpClassName.length() - 2);
      }

      String tmpMethodName = tmpCallString.substring(tmpLastDotPos + 1);
      if (StringUtils.isEmpty(tmpMethodName)) {
        final String tmpMessage = Messages.getMessage("javaExecSyntax", new String[] { tmpCallString });
        throw new InvalidInputException(tmpMessage);
      }
      if (tmpMethodName.endsWith("()")) {
        tmpMethodName = tmpMethodName.substring(0, tmpMethodName.length() - 2);
      }

      Object[] tmpParams = new String[tmpMethodParameters.size()];
      final Class<String>[] tmpParamTypes = new Class[tmpMethodParameters.size()];
      int i = 0;
      for (final SecretString tmpSecret : tmpMethodParameters) {
        tmpParams[i] = tmpSecret.toString();
        tmpParamTypes[i] = String.class;
        i++;
      }

      final StringBuffer tmpMethodLabel = new StringBuffer(tmpMethodName);

      try {
        final Class<?> tmpClass = ClassUtils.getClass(tmpClassName);
        Method tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, tmpParamTypes);
        if (null == tmpMethod) {
          tmpMethod = MethodUtils.getMatchingAccessibleMethod(tmpClass, tmpMethodName, new Class[] { String[].class });
          tmpParams = new Object[] { tmpParams };
        }

        // build a more descriptive method name
        tmpMethodLabel.append('(');
        for (int j = 0; j < tmpParamTypes.length; j++) {
          if (j > 0) {
            tmpMethodLabel.append(", ");
          }
          tmpMethodLabel.append("String");
        }
        tmpMethodLabel.append(')');

        if (null == tmpMethod) {
          final String tmpMessage = Messages.getMessage("javaExecMethodNotFound", new String[] { tmpClassName,
              tmpMethodLabel.toString() });
          throw new InvalidInputException(tmpMessage);
        }

        Object tmpReceiver = null;
        if (!Modifier.isStatic(tmpMethod.getModifiers())) {
          tmpReceiver = tmpClass.newInstance();
        }

        // time to execute
        final Object tmpResult = tmpMethod.invoke(tmpReceiver, tmpParams);
        if (Void.TYPE != tmpMethod.getReturnType()) {
          if (null == tmpResult) {
            aContext.informListenersInfo("javaExecResult", new String[] { "null" });
          } else {
            aContext.informListenersInfo("javaExecResult", new String[] { tmpResult.toString() });
          }
        }
      } catch (final NoClassDefFoundError e) {
        aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        final String tmpMessage = Messages.getMessage("javaExecClassNotFound",
            new String[] { tmpClassName, e.toString() });
        throw new CommandException(tmpMessage);
      } catch (final ClassNotFoundException e) {
        aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        final String tmpMessage = Messages.getMessage("javaExecClassNotFound",
            new String[] { tmpClassName, e.toString() });
        throw new CommandException(tmpMessage);
      } catch (final IllegalArgumentException e) {
        final String tmpMessage = Messages.getMessage("javaExecIllegalArgument", new String[] { tmpClassName,
            tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.getMessage() });
        throw new CommandException(tmpMessage);
      } catch (final IllegalAccessException e) {
        aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        final String tmpMessage = Messages.getMessage("javaExecIllegalAccess", new String[] { tmpClassName,
            tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.getMessage() });
        throw new CommandException(tmpMessage);
      } catch (final InvocationTargetException e) {
        aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          final String tmpMessage = Messages.getMessage("javaExecInvocationTarget", new String[] { tmpClassName,
              tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.toString() });
          throw new CommandException(tmpMessage);
        }
        final String tmpMessage = Messages.getMessage("javaExecInvocationTarget", new String[] { tmpClassName,
            tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.getCause().toString() });
        throw new CommandException(tmpMessage);
      } catch (final InstantiationException e) {
        aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          final String tmpMessage = Messages.getMessage("javaExecInstantiation", new String[] { tmpClassName,
              tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.toString() });
          throw new CommandException(tmpMessage);
        }
        final String tmpMessage = Messages.getMessage("javaExecInstantiation", new String[] { tmpClassName,
            tmpMethodLabel.toString(), tmpMethodParameters.toString(), e.getCause().toString() });
        throw new CommandException(tmpMessage);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.core.ICommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.core.ICommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}
