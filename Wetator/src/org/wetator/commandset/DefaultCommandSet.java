/*
 * Copyright (c) 2008-2011 wetator.org
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
import org.wetator.core.ICommandImplementation;
import org.wetator.core.Variable;
import org.wetator.core.WetatorContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

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
    registerCommand("assert-disabled", new CommandAssertDisabled());
    registerCommand("assert-set", new CommandAssertSet());
    registerCommand("assert-selected", new CommandAssertSelected());
    registerCommand("assert-deselected", new CommandAssertDeselected());

    registerCommand("exec-java", new CommandExecJava());
  }

  /**
   * Command 'Open Url'.
   */
  public final class CommandOpenUrl implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final SecretString tmpUrlParam = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.assertNoUnusedSecondParameter(aContext);

      try {
        // create the complete URL
        final String tmpUrlToLower = tmpUrlParam.toLowerCase(Locale.ENGLISH);
        if (tmpUrlToLower.startsWith("http://") || tmpUrlToLower.startsWith("https://")) {
          aContext.informListenersWarn("absoluteUrl", new String[] { tmpUrlParam.toString() });
        } else {
          if (!tmpUrlParam.startsWith("/")) {
            tmpUrlParam.prefixWith("/");
          }

          tmpUrlParam.prefixWith(aContext.getConfiguration().getBaseUrl());
        }

        final URL tmpUrl = new URL(tmpUrlParam.getValue());
        aContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });

        final IBrowser tmpBrowser = getBrowser(aContext);
        tmpBrowser.openUrl(tmpUrl);
      } catch (final MalformedURLException e) {
        Assert.fail("invalidUrl", new String[] { tmpUrlParam.toString(), e.getMessage() });
      }
      getBrowser(aContext).saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Use Module'.
   */
  public final class CommandUseModule implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final SecretString tmpModuleParam = aCommand.getRequiredFirstParameterValue(aContext);
      final List<SecretString> tmpModuleParameters = aCommand.getSecondParameterValues(aContext);

      final String tmpModule = tmpModuleParam.getValue();

      File tmpFile = new File(tmpModule);

      if (!tmpFile.isAbsolute()) {
        tmpFile = new File(aContext.getFile().getParent(), tmpModule);
        aContext.informListenersInfo("useModule", new String[] { tmpFile.getAbsolutePath() });
      }

      // setup the new context
      final WetatorContext tmpWetatorContext = aContext.createSubContext(tmpFile);

      int i = 1;
      for (SecretString tmpSecretString : tmpModuleParameters) {
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getFirstParameterValues(aContext));
      SecretString tmpValueParam = aCommand.getSecondParameterValue(aContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("", "");
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpElementFinder = tmpBrowser.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpElementFinder.getAllSettables(tmpWPath);

      ISettable tmpControl = null;
      // in case of no input use the first 'usable' field on the page
      if (tmpWPath.isEmpty()) {
        for (WeightedControlList.Entry tmpEntry : tmpFoundElements.getEntriesSorted()) {
          tmpControl = (ISettable) tmpEntry.getControl();
          if (!tmpControl.isDisabled(aContext)) {
            break;
          }
        }

        if (null == tmpControl) {
          Assert.fail("noSetableHtmlElmentFound", new String[] { tmpWPath.toString() });
        }
        aContext.informListenersWarn("firstElementUsed", new String[] { tmpControl.getDescribingText() });
      } else {
        tmpControl = (ISettable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
            "noSetableHtmlElmentFound");

      }

      tmpControl.setValue(aContext, tmpValueParam, aContext.getFile().getParentFile());
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // Buttons / Link / Image
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllClickables(tmpWPath);

      // Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noClickableHtmlElmentFound");
      tmpControl.click(aContext);
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "no2ClickableHtmlElmentFound");
      tmpControl.clickDouble(aContext);
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath, "7");
      tmpControl.clickRight(aContext);
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noSelectableHtmlElmentFound");
      tmpControl.select(aContext);
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // (Select)Options / Checkboxes
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllDeselectables(tmpWPath);

      final IDeselectable tmpControl = (IDeselectable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements,
          tmpWPath, "noDeselectableHtmlElmentFound");
      tmpControl.deselect(aContext);
      tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final SecretString tmpWindowNameParam = aCommand.getFirstParameterValue(aContext);
      aCommand.assertNoUnusedSecondParameter(aContext);

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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {

      final SecretString tmpStepsParam = aCommand.getFirstParameterValue(aContext);
      aCommand.assertNoUnusedSecondParameter(aContext);

      int tmpSteps = 1;
      if (null != tmpStepsParam) {
        try {
          tmpSteps = Integer.parseInt(tmpStepsParam.getValue());
        } catch (final Exception e) {
          aContext.informListenersWarn("stepsNotANumber",
              new String[] { tmpStepsParam.toString(), Integer.toString(tmpSteps) });
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final List<SecretString> tmpExpected = aCommand.getRequiredFirstParameterValues(aContext);
      Long tmpTimeout = aCommand.getSecondParameterLongValue(aContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final IBrowser tmpBrowser = getBrowser(aContext);
      final boolean tmpContentChanged = tmpBrowser.assertTitleInTimeFrame(tmpExpected, tmpTimeout);
      if (tmpContentChanged) {
        tmpBrowser.saveCurrentWindowToLog();
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final List<SecretString> tmpExpected = aCommand.getRequiredFirstParameterValues(aContext);
      Long tmpTimeout = aCommand.getSecondParameterLongValue(aContext);
      if (null == tmpTimeout) {
        tmpTimeout = Long.valueOf(0L);
      }

      tmpTimeout = Math.max(0, tmpTimeout.longValue());

      final IBrowser tmpBrowser = getBrowser(aContext);
      final boolean tmpContentChanged = tmpBrowser.assertContentInTimeFrame(tmpExpected, tmpTimeout);
      if (tmpContentChanged) {
        tmpBrowser.saveCurrentWindowToLog();
      }
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final IControl tmpControl = getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      SecretString tmpValueParam = aCommand.getSecondParameterValue(aContext);
      if (null == tmpValueParam) {
        tmpValueParam = new SecretString("", "");
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);

      final ISettable tmpControl = (ISettable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noSetableHtmlElmentFound");

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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noSelectableHtmlElmentFound");

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
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.assertNoUnusedSecondParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = tmpBrowser.getControlFinder();

      // (Select)Options / Checkboxes / Radiobuttons
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSelectables(tmpWPath);

      final ISelectable tmpControl = (ISelectable) getRequiredFirstHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noDeselectableHtmlElmentFound");

      final boolean tmpIsSelected = tmpControl.isSelected(aContext);
      Assert.assertFalse(tmpIsSelected, "elementNotDeselected", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Exec Java'.
   */
  public final class CommandExecJava implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(final WetatorContext aContext, final Command aCommand) throws AssertionFailedException {
      final SecretString tmpCall = aCommand.getRequiredFirstParameterValue(aContext);
      final List<SecretString> tmpMethodParameters = aCommand.getSecondParameterValues(aContext);

      final String tmpCallString = tmpCall.toString();
      final int tmpLastDotPos = tmpCallString.lastIndexOf('.');
      if (tmpLastDotPos < 0) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }

      String tmpClassName = tmpCallString.substring(0, tmpLastDotPos);
      if (StringUtils.isEmpty(tmpClassName)) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }
      if (tmpClassName.endsWith("()")) {
        tmpClassName = tmpClassName.substring(0, tmpClassName.length() - 2);
      }

      String tmpMethodName = tmpCallString.substring(tmpLastDotPos + 1);
      if (StringUtils.isEmpty(tmpMethodName)) {
        Assert.fail("javaExecSyntax", new String[] { tmpCallString });
      }
      if (tmpMethodName.endsWith("()")) {
        tmpMethodName = tmpMethodName.substring(0, tmpMethodName.length() - 2);
      }

      Object[] tmpParams = new String[tmpMethodParameters.size()];
      final Class<String>[] tmpParamTypes = new Class[tmpMethodParameters.size()];
      int i = 0;
      for (SecretString tmpSecret : tmpMethodParameters) {
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
          Assert.fail("javaExecMethodNotFound", new String[] { tmpClassName, tmpMethodLabel.toString() });
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
        aContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        Assert.fail("javaExecClassNotFound", new String[] { tmpClassName, e.toString() });
      } catch (final ClassNotFoundException e) {
        aContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        aContext.informListenersInfo("javaExecClasspath", new String[] { System.getProperty("java.class.path") });
        Assert.fail("javaExecClassNotFound", new String[] { tmpClassName, e.toString() });
      } catch (final IllegalArgumentException e) {
        Assert.fail("javaExecIllegalArgument", new String[] { tmpClassName, tmpMethodLabel.toString(),
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (final IllegalAccessException e) {
        aContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        Assert.fail("javaExecIllegalAccess", new String[] { tmpClassName, tmpMethodLabel.toString(),
            tmpMethodParameters.toString(), e.getMessage() });
      } catch (final InvocationTargetException e) {
        aContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInvocationTarget", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
      } catch (final InstantiationException e) {
        aContext.informListenersWarn("javaExecStacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
        if (null == e.getCause()) {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.toString() });
        } else {
          Assert.fail("javaExecInstantiation", new String[] { tmpClassName, tmpMethodLabel.toString(),
              tmpMethodParameters.toString(), e.getCause().toString() });
        }
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
