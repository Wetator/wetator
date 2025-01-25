/*
 * Copyright (c) 2008-2025 wetator.org
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlPage;
import org.wetator.backend.ControlFeature;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IFocusable;
import org.wetator.backend.control.KeySequence;
import org.wetator.backend.htmlunit.HtmlUnitBrowser;
import org.wetator.core.Command;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.Variable;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.exception.CommandException;
import org.wetator.exception.InvalidInputException;
import org.wetator.gui.InputDialog;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * The implementation of all experimental commands that Wetator
 * supports at the moment.<br>
 * We are not sure, that these commands are useful extension of
 * the current command set. So we have this set to play a bit.
 *
 * @author rbri
 * @author frank.danek
 */
public final class IncubatorCommandSet extends AbstractCommandSet {

  @Override
  protected void registerCommands() {
    registerCommand("assert-focus", new CommandAssertFocus());
    registerCommand("save-bookmark", new CommandSaveBookmark());
    registerCommand("open-bookmark", new CommandOpenBookmark());
    registerCommand("exec-js", new CommandExecJs());

    registerCommand("type", new CommandType());

    // still there to solve some strange situations
    registerCommand("wait", new CommandWait());

    // for the moment only a strange hack
    registerCommand("enter-variable", new CommandEnterVariable());
  }

  /**
   * Command 'assert-focus'.
   */
  public final class CommandAssertFocus implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValue(aContext), aContext.getConfiguration());

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final IControlFinder tmpControlFinder = getControlFinder(tmpBrowser);

      final WeightedControlList tmpFoundElements = tmpControlFinder.findControls(ControlFeature.FOCUS, tmpWPath);

      final IFocusable tmpControl = (IFocusable) getFirstRequiredHtmlElementFrom(aContext, tmpFoundElements, tmpWPath,
          "noHtmlElementFound");

      tmpBrowser.markControls(tmpControl);
      final boolean tmpHasFocus = tmpControl.hasFocus(aContext);
      Assert.assertTrue(tmpHasFocus, "elementNotFocused", tmpControl.getDescribingText());
    }
  }

  /**
   * Command 'open-bookmark'.
   */
  public final class CommandOpenBookmark implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpBookmarkName = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final URL tmpUrl = tmpBrowser.getBookmark(tmpBookmarkName.getValue());
      if (tmpUrl == null) {
        final String tmpMessage = Messages.getMessage("unknownBookmark", tmpBookmarkName.getValue());
        throw new ActionException(tmpMessage);
      }

      aContext.informListenersInfo("openUrl", tmpUrl.toString());
      tmpBrowser.openUrl(tmpUrl);
      tmpBrowser.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'save-bookmark'.
   */
  public final class CommandSaveBookmark implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpBookmarkName = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      tmpBrowser.bookmarkPage(tmpBookmarkName.getValue());
    }
  }

  /**
   * Command 'wait'.
   */
  public final class CommandWait implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpWaitTimeString = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final long tmpWaitTime;
      try {
        final BigDecimal tmpValue = new BigDecimal(tmpWaitTimeString.getValue());
        tmpWaitTime = tmpValue.longValueExact();
      } catch (final NumberFormatException | ArithmeticException e) {
        final String tmpMessage = Messages.getMessage("integerParameterExpected", "wait", tmpWaitTimeString.toString(),
            "1");
        throw new InvalidInputException(tmpMessage);
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      try {
        Thread.sleep(tmpWaitTime * 1000L);
        tmpBrowser.saveCurrentWindowToLog();
      } catch (final InterruptedException e) {
        final String tmpMessage = Messages.getMessage("waitError");
        throw new ActionException(tmpMessage, e);
      }
    }
  }

  /**
   * Command 'enter-variable'.
   * For the moment only a hack - see https://github.com/Wetator/wetator/issues/19
   */
  public static final class CommandEnterVariable implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      aCommand.checkNoUnusedThirdParameter(aContext);

      final SecretString tmpVariable = aCommand.getRequiredFirstParameterValue(aContext);
      String tmpVariableName = tmpVariable.getValue();
      if (!tmpVariableName.startsWith(WetatorConfiguration.VARIABLE_PREFIX)) {
        throw new InvalidInputException("Variable name has to start with " + WetatorConfiguration.VARIABLE_PREFIX);
      }

      final SecretString tmpHint = aCommand.getSecondParameterValue(aContext);
      String tmpHintText = tmpHint.getValue();
      if (StringUtils.isBlank(tmpHintText)) {
        tmpHintText = "Please enter a value for " + tmpVariableName;
      }

      // ok it is a variable
      tmpVariableName = tmpVariableName.substring(1);
      final boolean tmpIsSecret = tmpVariableName.startsWith(WetatorConfiguration.SECRET_PREFIX);

      try {
        final String tmpVariableValue = InputDialog.captureInput(tmpHintText, tmpIsSecret);
        if (tmpIsSecret) {
          aContext.addVariable(new Variable(tmpVariableName, new SecretString(tmpVariableValue)));
        } else {
          aContext.addVariable(new Variable(tmpVariableName, tmpVariableValue));
        }
      } catch (final BackendException e) {
        final String tmpMessage = Messages.getMessage("commandBackendError", e.getMessage());
        throw new AssertionException(tmpMessage, e);
      }

    }
  }

  /**
   * Command 'exec-js'.
   */
  public final class CommandExecJs implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final SecretString tmpJsString = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      try {
        final IBrowser tmpBrowser = getBrowser(aContext);
        if (tmpBrowser instanceof HtmlUnitBrowser) {
          final HtmlUnitBrowser tmpHtmlUnitBrowser = (HtmlUnitBrowser) tmpBrowser;

          final HtmlPage tmpHtmlPage = tmpHtmlUnitBrowser.getCurrentHtmlPage();
          tmpHtmlPage.executeJavaScript(tmpJsString.getValue());

          tmpHtmlUnitBrowser.waitForImmediateJobs();
          tmpBrowser.saveCurrentWindowToLog();
        }
      } catch (final BackendException e) {
        final String tmpMessage = Messages.getMessage("commandBackendError", e.getMessage());
        throw new AssertionException(tmpMessage, e);
      }
    }
  }

  /**
   * Command 'type'.
   */
  public final class CommandType implements ICommandImplementation {
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      final String tmpKeys = aCommand.getRequiredFirstParameterValue(aContext).getValue();

      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      try {
        final IControl tmpFocusedControl = tmpBrowser.getFocusedControl();

        tmpBrowser.markControls(tmpFocusedControl);
        tmpFocusedControl.type(aContext, KeySequence.parse(tmpKeys));

        tmpBrowser.saveCurrentWindowToLog(tmpFocusedControl);
      } catch (final BackendException e) {
        final String tmpMessage = Messages.getMessage("commandBackendError", e.getMessage());
        throw new ActionException(tmpMessage, e);
      }
    }

  }

  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do at the moment
  }

  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}
