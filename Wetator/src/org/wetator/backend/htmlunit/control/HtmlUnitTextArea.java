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


package org.wetator.backend.htmlunit.control;

import java.io.File;
import java.io.IOException;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang.StringUtils;
import org.wetator.backend.control.Settable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitTextAreaIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * This is the implementation of the HTML element 'textarea' (&lt;textarea&gt;) using HtmlUnit as backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlTextArea.class)
@IdentifiedBy(HtmlUnitTextAreaIdentifier.class)
public class HtmlUnitTextArea extends HtmlUnitBaseControl<HtmlTextArea> implements Settable {

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
   * @see org.wetator.backend.control.Settable#setValue(org.wetator.core.WetContext, org.wetator.util.SecretString,
   *      java.io.File)
   */
  @Override
  public void setValue(final WetContext aWetContext, final SecretString aValue, final File aDirectory)
      throws AssertionFailedException {
    final HtmlTextArea tmpHtmlTextArea = getHtmlElement();

    Assert.assertTrue(!tmpHtmlTextArea.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    Assert.assertTrue(!tmpHtmlTextArea.isReadOnly(), "elementReadOnly", new String[] { getDescribingText() });

    try {
      tmpHtmlTextArea.click();
    } catch (final IOException e) {
      aWetContext.getBrowser().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      final String tmpValue = aValue.getValue();
      tmpHtmlTextArea.select();

      if (StringUtils.isNotBlank(tmpValue)) {
        tmpHtmlTextArea.type(aValue.getValue());
      } else {
        tmpHtmlTextArea.setText("");
      }

      // wait for silence
      aWetContext.getBrowser().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final AssertionFailedException e) {
      aWetContext.getBrowser().addFailure(e);
    } catch (final Throwable e) {
      aWetContext.getBrowser().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Settable#assertValue(org.wetator.core.WetContext, org.wetator.util.SecretString)
   */
  @Override
  public void assertValue(final WetContext aWetContext, final SecretString anExpectedValue)
      throws AssertionFailedException {
    Assert.assertEquals(anExpectedValue, getHtmlElement().getText(), "expectedValueNotFound", null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#isDisabled(org.wetator.core.WetContext)
   */
  @Override
  public boolean isDisabled(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlTextArea tmpHtmlTextArea = getHtmlElement();

    return tmpHtmlTextArea.isDisabled() || tmpHtmlTextArea.isReadOnly();
  }
}
