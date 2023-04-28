/*
 * Copyright (c) 2008-2021 wetator.org
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

import org.htmlunit.ScriptException;
import org.htmlunit.corejs.javascript.WrappedException;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlUrlInput;
import org.htmlunit.html.Keyboard;
import org.htmlunit.javascript.host.event.KeyboardEvent;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.ISettable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputUrlIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * This is the implementation of the HTML element 'input Url' (&lt;input type="Url"&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 */
@ForHtmlElement(HtmlUrlInput.class)
@IdentifiedBy(HtmlUnitInputUrlIdentifier.class)
public class HtmlUnitInputUrl extends HtmlUnitBaseControl<HtmlUrlInput>
    implements ISettable, IHtmlUnitDisableable<HtmlUrlInput>, IHtmlUnitFocusable<HtmlUrlInput> {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlUrlInput} from the backend
   */
  public HtmlUnitInputUrl(final HtmlUrlInput anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlUrlInput(getHtmlElement());
  }

  @Override
  public void setValue(final WetatorContext aWetatorContext, final SecretString aValue, final File aDirectory)
      throws ActionException {
    final HtmlUrlInput tmpHtmlUrlInput = getHtmlElement();

    if (tmpHtmlUrlInput.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlUrlInput.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlUrlInput.getPage();
      DomElement tmpFocusedElement = tmpHtmlPage.getFocusedElement();
      if (tmpFocusedElement == null || tmpHtmlUrlInput != tmpFocusedElement) {
        tmpHtmlUrlInput.click();

        // onXXXX events are synchronous but the richfaces placeholder
        // introduces some asynchronous activity
        // e.g. window.setTimeout( function () { $input.select(); }, 1)
        // we will wait some time (usually the user needs a moment too)
        aWetatorContext.getBrowser().waitForImmediateJobs(100);

        tmpFocusedElement = tmpHtmlPage.getFocusedElement();
        if (tmpHtmlUrlInput != tmpFocusedElement) {
          final IControl tmpFocusedControl = aWetatorContext.getBrowser().getFocusedControl();

          if (tmpFocusedControl == null) {
            aWetatorContext.informListenersInfo("focusRemoved", getDescribingText());
            throw new ActionException(
                "After clicking on the control '" + getDescribingText() + "' the focus was removed.");
          }

          final String tmpDesc = tmpFocusedControl.getDescribingText();
          aWetatorContext.informListenersInfo("focusChanged", getDescribingText(), tmpDesc);

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
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final ActionException e) {
      throw e;
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }

    try {
      final String tmpValue = aValue.getValue();
      tmpHtmlUrlInput.select();

      if (tmpValue.length() > 0) {
        final long tmpDelay = 1000L / (aWetatorContext.getConfiguration().getTypingSpeedInKeystrokesPerMinute() / 60);

        tmpHtmlUrlInput.type(tmpValue.charAt(0));

        for (int i = 1; i < tmpValue.length(); i++) {
          aWetatorContext.getBrowser().waitForImmediateJobs(tmpDelay);

          final char tmpChar = tmpValue.charAt(i);
          tmpHtmlUrlInput.type(tmpChar);
        }
      } else {
        // simulate delete key
        final Keyboard tmpKeyboard = new Keyboard();
        tmpKeyboard.press(KeyboardEvent.DOM_VK_DELETE);
        tmpHtmlUrlInput.type(tmpKeyboard);
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
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }
  }

  @Override
  public void assertValue(final WetatorContext aWetatorContext, final SecretString anExpectedValue)
      throws AssertionException {
    Assert.assertEquals(anExpectedValue, getHtmlElement().getValue(), "expectedValueNotFound");
  }

  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlUrlInput tmpHtmlUrlInput = getHtmlElement();

    return tmpHtmlUrlInput.isDisabled() || tmpHtmlUrlInput.isReadOnly();
  }
}
