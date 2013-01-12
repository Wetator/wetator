/*
 * Copyright (c) 2008-2013 wetator.org
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

import org.wetator.backend.control.ISettable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
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
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;

/**
 * This is the implementation of the HTML element 'input hidden' (&lt;input type="hidden"&gt;) using HtmlUnit as
 * backend. There is no identifier for this control as it may not be set via wetator.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlHiddenInput.class)
public class HtmlUnitInputHidden extends HtmlUnitBaseControl<HtmlHiddenInput> implements ISettable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlHiddenInput} from the backend
   */
  public HtmlUnitInputHidden(final HtmlHiddenInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlHiddenInput(getHtmlElement());
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
    final HtmlHiddenInput tmpHtmlHiddenInput = getHtmlElement();

    if (tmpHtmlHiddenInput.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlHiddenInput.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", new String[] { getDescribingText() });
      throw new ActionException(tmpMessage);
    }

    try {
      final String tmpValue = aValue.getValue();
      tmpHtmlHiddenInput.setAttribute("value", tmpValue);

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
    Assert.assertEquals(anExpectedValue, getHtmlElement().getValueAttribute(), "expectedValueNotFound", null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlHiddenInput tmpHtmlHiddenInput = getHtmlElement();

    return tmpHtmlHiddenInput.isDisabled();
  }
}
