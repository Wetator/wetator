/*
 * Copyright (c) 2008-2015 wetator.org
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

import java.applet.Applet;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.HtmlUnitBrowser;
import org.wetator.core.Command;
import org.wetator.core.ICommandImplementation;
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

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
    registerCommand("assert-applet", new CommandAssertApplet());
    registerCommand("exec-js", new CommandExecJs());

    registerCommand("confirm-next", new CommandConfirmNext());

    // still there to solve some strange situations
    registerCommand("wait", new CommandWait());
  }

  /**
   * Command 'Assert Focus'.
   */
  public final class CommandAssertFocus implements ICommandImplementation {
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
      final boolean tmpHasFocus = tmpControl.hasFocus(aContext);
      Assert.assertTrue(tmpHasFocus, "elementNotFocused", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Open Bookmark'.
   */
  public final class CommandOpenBookmark implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
      final SecretString tmpBookmarkName = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final URL tmpUrl = tmpBrowser.getBookmark(tmpBookmarkName.getValue());
      if (tmpUrl == null) {
        final String tmpMessage = Messages.getMessage("unknownBookmark", new String[] { tmpBookmarkName.getValue() });
        throw new ActionException(tmpMessage);
      }

      aContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });
      tmpBrowser.openUrl(tmpUrl);
      tmpBrowser.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Save Bookmark'.
   */
  public final class CommandSaveBookmark implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
      final SecretString tmpBookmarkName = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      tmpBrowser.bookmarkPage(tmpBookmarkName.getValue());
    }
  }

  /**
   * Command 'Wait'.
   */
  public final class CommandWait implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
      final SecretString tmpWaitTimeString = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      long tmpWaitTime = 0;
      try {
        final BigDecimal tmpValue = new BigDecimal(tmpWaitTimeString.getValue());
        tmpWaitTime = tmpValue.longValueExact();
      } catch (final NumberFormatException e) {
        // TODO i18n
        final String tmpMessage = Messages.getMessage("integerParameterExpected", new String[] { "wait",
            tmpWaitTimeString.toString(), "1" });
        throw new InvalidInputException(tmpMessage);
      } catch (final ArithmeticException e) {
        final String tmpMessage = Messages.getMessage("integerParameterExpected", new String[] { "wait",
            tmpWaitTimeString.toString(), "1" });
        throw new InvalidInputException(tmpMessage);
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      try {
        Thread.sleep(tmpWaitTime * 1000L);
        tmpBrowser.saveCurrentWindowToLog();
      } catch (final InterruptedException e) {
        final String tmpMessage = Messages.getMessage("waitError", null);
        throw new ActionException(tmpMessage, e);
      }
    }
  }

  /**
   * Command 'exec-js'.
   */
  public final class CommandExecJs implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
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
        final String tmpMessage = Messages.getMessage("commandBackendError", new String[] { e.getMessage() });
        throw new AssertionException(tmpMessage, e);
      }
    }
  }

  /**
   * Command 'confirm-next'.
   */
  public final class CommandConfirmNext implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
      final SecretString tmpButton = aCommand.getRequiredFirstParameterValue(aContext);
      if (!"ok".equalsIgnoreCase(tmpButton.getValue()) && !"cancel".equalsIgnoreCase(tmpButton.getValue())) {
        final String tmpMessage = Messages.getMessage("confirmationOkOrCancel", new String[] { tmpButton.toString() });
        throw new InvalidInputException(tmpMessage);
      }

      final ContentPattern tmpPattern = new ContentPattern(aCommand.getRequiredSecondParameterValue(aContext));

      final IBrowser tmpBrowser = getBrowser(aContext);
      if (tmpBrowser instanceof HtmlUnitBrowser) {
        final HtmlUnitBrowser tmpHtmlUnitBrowser = (HtmlUnitBrowser) tmpBrowser;

        if ("ok".equalsIgnoreCase(tmpButton.getValue())) {
          tmpHtmlUnitBrowser.chooseOkOnNextConfirmFor(tmpPattern);
        } else {
          tmpHtmlUnitBrowser.chooseCancelOnNextConfirmFor(tmpPattern);
        }
      }
    }
  }

  /**
   * Command 'AssertApplet'.
   * Checks that an applet is runnable.
   */
  public final class CommandAssertApplet implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandException,
        InvalidInputException {
      final SecretString tmpAppletName = aCommand.getFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      boolean tmpAppletTested = false;
      String tmpAppletNameValue = "";
      if (null != tmpAppletName) {
        tmpAppletNameValue = tmpAppletName.getValue();
      }
      try {
        final IBrowser tmpBrowser = getBrowser(aContext);
        if (tmpBrowser instanceof HtmlUnitBrowser) {
          final HtmlUnitBrowser tmpHtmlUnitBrowser = (HtmlUnitBrowser) tmpBrowser;

          final HtmlPage tmpHtmlPage = tmpHtmlUnitBrowser.getCurrentHtmlPage();
          final DomNodeList<DomElement> tmpAppletElements = tmpHtmlPage.getElementsByTagName("applet");
          for (final DomElement tmpAppletElement : tmpAppletElements) {
            final HtmlApplet tmpHtmlApplet = (HtmlApplet) tmpAppletElement;
            if (StringUtils.isEmpty(tmpAppletNameValue) || tmpAppletNameValue.equals(tmpHtmlApplet.getNameAttribute())) {
              aContext.informListenersInfo("assertApplet", new String[] { tmpAppletNameValue });
              tmpAppletTested = true;
              try {
                final Applet tmpApplet = tmpHtmlApplet.getApplet();
                tmpApplet.stop();
                tmpApplet.destroy();
              } catch (final Exception e) {
                // do a bit more and verify if all the jars are available
                aContext.informListenersWarn("stacktrace", new String[] { ExceptionUtils.getStackTrace(e) });
                checkArchiveAvailability(aContext, tmpHtmlApplet);

                final String tmpMessage = Messages.getMessage("assertAppletFailed",
                    new String[] { tmpHtmlApplet.getNameAttribute(), e.getMessage() });
                throw new AssertionException(tmpMessage, e);
              }
            }
          }
          if (!tmpAppletTested) {
            final String tmpMessage = Messages.getMessage("assertAppletNotFound", new String[] { tmpAppletNameValue });
            throw new AssertionException(tmpMessage);
          }
        }
      } catch (final BackendException e) {
        final String tmpMessage = Messages.getMessage("commandBackendError", new String[] { e.getMessage() });
        throw new AssertionException(tmpMessage, e);
      }
    }

    /**
     * Check, if the defined applet archives are available for download.
     * This is only done in case of an applet start error; this may create
     * a hint, why the applet start call failed.
     * 
     * @param aContext
     * @param aHtmlApplet
     */
    private void checkArchiveAvailability(final WetatorContext aContext, final HtmlApplet aHtmlApplet) {
      aContext.informListenersWarn("assertAppletArchives", new String[] { aHtmlApplet.getArchiveAttribute() });
      final List<URL> tmpJarUrls = aHtmlApplet.getArchiveUrls();
      if (null != tmpJarUrls) {
        for (final URL tmpJarUrl : tmpJarUrls) {
          try {
            final InputStream tmpIs = tmpJarUrl.openStream();
            tmpIs.close();
          } catch (final Exception eUrl) {
            aContext.informListenersWarn("assertAppletUnreachableJar",
                new String[] { tmpJarUrl.toString(), eUrl.toString() });
          }
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
