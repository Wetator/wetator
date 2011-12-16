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

import java.applet.Applet;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.HtmlUnitBrowser;
import org.wetator.core.Command;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionFailedException;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.CommandExecutionException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlApplet;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
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
    // still there to solve some strange situations
    registerCommand("wait", new CommandWait());
    registerCommand("run-applet", new CommandRunApplet());
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandExecutionException {
      final WPath tmpWPath = new WPath(aCommand.getRequiredFirstParameterValues(aContext));
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

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

      try {
        final boolean tmpIsDisabled = tmpControl.hasFocus(aContext);
        Assert.assertTrue(tmpIsDisabled, "elementNotFocused", new String[] { tmpControl.getDescribingText() });
      } catch (final AssertionFailedException e) {
        assertionFailed(e);
      }
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandExecutionException {
      final SecretString tmpBookmarkName = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      final URL tmpUrl = tmpBrowser.getBookmark(tmpBookmarkName.getValue());
      if (tmpUrl == null) {
        actionFailed("unknownBookmark", new String[] { tmpBookmarkName.getValue() });
      }

      aContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });
      try {
        tmpBrowser.openUrl(tmpUrl);
        tmpBrowser.saveCurrentWindowToLog();
      } catch (final ActionFailedException e) {
        actionFailed(e);
      }
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandExecutionException {
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
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandExecutionException {
      final SecretString tmpWaitTimeString = aCommand.getRequiredFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      long tmpWaitTime = 0;
      try {
        final BigDecimal tmpValue = new BigDecimal(tmpWaitTimeString.getValue());
        tmpWaitTime = tmpValue.longValueExact();
      } catch (final NumberFormatException e) {
        wrongCommandUsage("integerParameterExpected", new String[] { "wait", tmpWaitTimeString.toString(), "1" });
      } catch (final ArithmeticException e) {
        wrongCommandUsage("integerParameterExpected", new String[] { "wait", tmpWaitTimeString.toString(), "1" });
      }

      final IBrowser tmpBrowser = getBrowser(aContext);
      try {
        Thread.sleep(tmpWaitTime * 1000L);
        tmpBrowser.saveCurrentWindowToLog();
      } catch (final InterruptedException e) {
        final String tmpMessage = Messages.getMessage("waitError", null);
        actionFailed(new ActionFailedException(tmpMessage, e));
      }
    }
  }

  /**
   * Command 'Run Applet'.
   */
  public final class CommandRunApplet implements ICommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.ICommandImplementation#execute(org.wetator.core.WetatorContext, org.wetator.core.Command)
     */
    @Override
    public void execute(final WetatorContext aContext, final Command aCommand) throws CommandExecutionException {
      final SecretString tmpAppletName = aCommand.getFirstParameterValue(aContext);
      aCommand.checkNoUnusedSecondParameter(aContext);
      aCommand.checkNoUnusedThirdParameter(aContext);

      final IBrowser tmpBrowser = getBrowser(aContext);
      if (tmpBrowser instanceof HtmlUnitBrowser) {
        final HtmlUnitBrowser tmpHtmlUnitBrowser = (HtmlUnitBrowser) tmpBrowser;
        final HtmlPage tmpHtmlPage = tmpHtmlUnitBrowser.getCurrentHtmlPage();
        final DomNodeList<HtmlElement> tmpAppletElements = tmpHtmlPage.getElementsByTagName("applet");
        for (HtmlElement tmpAppletElement : tmpAppletElements) {
          final HtmlApplet tmpHtmlApplet = (HtmlApplet) tmpAppletElement;
          if (null == tmpAppletName || StringUtils.isEmpty(tmpAppletName.getValue())
              || tmpAppletName.getValue().equals(tmpHtmlApplet.getNameAttribute())) {
            try {
              final Applet tmpApplet = tmpHtmlApplet.getApplet();
              tmpApplet.stop();
              tmpApplet.destroy();
            } catch (final Exception e) {
              // TODO is this an assertion or an action?
              assertionFailed(new AssertionFailedException("Applet usage failed (" + tmpHtmlApplet.getNameAttribute()
                  + ").", e));
            }
          }
        }
        // TODO warn if nothing found for name or no applet on the page
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
