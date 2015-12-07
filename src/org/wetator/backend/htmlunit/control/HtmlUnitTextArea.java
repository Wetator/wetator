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


package org.wetator.backend.htmlunit.control;

import java.io.File;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.wetator.backend.control.IControl;
import org.wetator.backend.control.ISettable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitTextAreaIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import com.gargoylesoftware.htmlunit.javascript.host.event.KeyboardEvent;

/**
 * This is the implementation of the HTML element 'textarea' (&lt;textarea&gt;) using HtmlUnit as backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlTextArea.class)
@IdentifiedBy(HtmlUnitTextAreaIdentifier.class)
public class HtmlUnitTextArea extends HtmlUnitBaseControl<HtmlTextArea> implements ISettable {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlTextArea} from the backend
   */
  public HtmlUnitTextArea(final HtmlTextArea anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlTextArea(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.control.ISettable#setValue(org.wetator.core.WetatorContext, org.wetator.util.SecretString,
   *      java.io.File)
   */
  @Override
  public void setValue(final WetatorContext aWetatorContext, final SecretString aValue, final File aDirectory)
      throws ActionException {
    final HtmlTextArea tmpHtmlTextArea = getHtmlElement();

    if (tmpHtmlTextArea.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlTextArea.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }

    try {
      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlTextArea.getPage();
      DomElement tmpFocusedElement = tmpHtmlPage.getFocusedElement();
      if (tmpFocusedElement == null || tmpHtmlTextArea != tmpFocusedElement) {
        tmpHtmlTextArea.click();

        // onXXXX events are synchronous but the richfaces placeholder
        // introduces some asynchronous activity
        // e.g. window.setTimeout( function () { $input.select(); }, 1)
        // we will wait some time (usually the user needs a moment too)
        aWetatorContext.getBrowser().waitForImmediateJobs(100);

        tmpFocusedElement = tmpHtmlPage.getFocusedElement();
        if (tmpHtmlTextArea != tmpFocusedElement) {
          final IControl tmpFocusedControl = aWetatorContext.getBrowser().getFocusedControl();

          if (tmpFocusedControl == null) {
            aWetatorContext.informListenersInfo("focusRemoved", new String[] { getDescribingText() });
            throw new ActionException("After clicking on the control '" + getDescribingText()
                + "' the focus was removed.");
          }

          final String tmpDesc = tmpFocusedControl.getDescribingText();
          aWetatorContext.informListenersInfo("focusChanged", new String[] { getDescribingText(), tmpDesc });

          if (tmpFocusedControl instanceof ISettable) {
            ((ISettable) tmpFocusedControl).setValue(aWetatorContext, aValue, aDirectory);
            return;
          }
          throw new ActionException("Focused control '" + tmpDesc + "' is not settable.");
        }
      }
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("backendError",
          new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    } catch (final ActionException e) {
      throw e;
    } catch (final Throwable e) {
      final String tmpMessage = Messages
          .getMessage("serverError", new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    }

    try {
      final String tmpValue = aValue.getValue();
      tmpHtmlTextArea.select();

      if (tmpValue.length() > 0) {
        final long tmpDelay = 1000L / (aWetatorContext.getConfiguration().getTypingSpeedInKeystrokesPerMinute() / 60);

        tmpHtmlTextArea.type(tmpValue.charAt(0));

        for (int i = 1; i < tmpValue.length(); i++) {
          aWetatorContext.getBrowser().waitForImmediateJobs(tmpDelay);

          final char tmpChar = tmpValue.charAt(i);
          tmpHtmlTextArea.type(tmpChar);
        }
      } else {
        // TODO - do the same as in HtmlUnitInputText if HtmlUnit 2.20 is available
        final char tmpDel = (char) 46;

        final Event tmpKeyDownEvent = new KeyboardEvent(tmpHtmlTextArea, Event.TYPE_KEY_DOWN, tmpDel, false, false,
            false);
        final ScriptResult tmpKeyDownResult = tmpHtmlTextArea.fireEvent(tmpKeyDownEvent);

        final Event tmpKeyPressEvent = new KeyboardEvent(tmpHtmlTextArea, Event.TYPE_KEY_PRESS, tmpDel, false, false,
            false);
        final ScriptResult tmpKeyPressResult = tmpHtmlTextArea.fireEvent(tmpKeyPressEvent);

        if (!tmpKeyDownEvent.isAborted(tmpKeyDownResult) && !tmpKeyPressEvent.isAborted(tmpKeyPressResult)) {
          // do it this way to not trigger the onChange handler
          tmpHtmlTextArea.setText("");
        }

        final Event tmpKeyUpEvent = new KeyboardEvent(tmpHtmlTextArea, Event.TYPE_KEY_UP, tmpDel, false, false, false);
        tmpHtmlTextArea.fireEvent(tmpKeyUpEvent);
      }

      // wait for silence
      waitForImmediateJobs(aWetatorContext);
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("backendError",
          new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      final String tmpMessage = Messages
          .getMessage("serverError", new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.control.ISettable#assertValue(org.wetator.core.WetatorContext,
   *      org.wetator.util.SecretString)
   */
  @Override
  public void assertValue(final WetatorContext aWetatorContext, final SecretString anExpectedValue)
      throws AssertionException {
    Assert.assertEquals(anExpectedValue, getHtmlElement().getText(), "expectedValueNotFound", null);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.control.IControl#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlTextArea tmpHtmlTextArea = getHtmlElement();

    return tmpHtmlTextArea.isDisabled() || tmpHtmlTextArea.isReadOnly();
  }
}
