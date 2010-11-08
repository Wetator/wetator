/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend.htmlunit.control;

import java.io.File;
import java.io.IOException;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.rbri.wet.backend.control.Settable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.gargoylesoftware.htmlunit.javascript.host.KeyboardEvent;

/**
 * This is the implementation of the HTML element 'input text' (&lt;input type="text"&gt;) using HtmlUnit as
 * backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@IdentifiedBy(HtmlUnitInputTextIdentifier.class)
public class HtmlUnitInputText extends HtmlUnitBaseControl<HtmlTextInput> implements Settable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlTextInput} from the backend
   */
  public HtmlUnitInputText(HtmlTextInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlTextInput(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Settable#setValue(org.rbri.wet.core.WetContext, org.rbri.wet.util.SecretString,
   *      java.io.File)
   */
  @Override
  public void setValue(WetContext aWetContext, SecretString aValue, File aDirectory) throws AssertionFailedException {
    HtmlTextInput tmpHtmlTextInput = getHtmlElement();

    Assert.assertTrue(!tmpHtmlTextInput.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      tmpHtmlTextInput.click();
    } catch (IOException e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      String tmpValue = aValue.getValue();
      tmpHtmlTextInput.select();

      int tmpMaxLength = -1;
      try {
        String tmpMaxLengthString = tmpHtmlTextInput.getMaxLengthAttribute();
        tmpMaxLength = Integer.parseInt(tmpMaxLengthString);
      } catch (NumberFormatException e) {
        // TODO warn
      }

      if (tmpMaxLength > -1) {
        tmpValue = tmpValue.substring(0, Math.min(tmpMaxLength, tmpValue.length()));
      }
      if (tmpValue.length() > 0) {
        tmpHtmlTextInput.type(tmpValue);
      } else {
        // no way to simulate type of the del key
        char tmpDel = (char) 46;

        Event tmpKeyDownEvent = new KeyboardEvent(tmpHtmlTextInput, Event.TYPE_KEY_DOWN, tmpDel, false, false, false);
        ScriptResult tmpKeyDownResult = tmpHtmlTextInput.fireEvent(tmpKeyDownEvent);

        Event tmpKeyPressEvent = new KeyboardEvent(tmpHtmlTextInput, Event.TYPE_KEY_PRESS, tmpDel, false, false, false);
        ScriptResult tmpKeyPressResult = tmpHtmlTextInput.fireEvent(tmpKeyPressEvent);

        if (!tmpKeyDownEvent.isAborted(tmpKeyDownResult) && !tmpKeyPressEvent.isAborted(tmpKeyPressResult)) {
          // do it this way to not trigger the onChange handler
          tmpHtmlTextInput.setAttribute("value", "");
        }

        Event tmpKeyUpEvent = new KeyboardEvent(tmpHtmlTextInput, Event.TYPE_KEY_UP, tmpDel, false, false, false);
        tmpHtmlTextInput.fireEvent(tmpKeyUpEvent);
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Settable#assertValue(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.util.SecretString)
   */
  @Override
  public void assertValue(WetContext aWetContext, SecretString anExpectedValue) throws AssertionFailedException {
    Assert.assertEquals(anExpectedValue, getHtmlElement().getValueAttribute(), "expectedValueNotFound", null);
  }
}
