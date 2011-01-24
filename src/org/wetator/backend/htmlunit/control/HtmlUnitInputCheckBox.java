/*
 * Copyright (c) 2008-2011 www.wetator.org
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

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.wetator.backend.control.Deselectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;

/**
 * This is the implementation of the HTML element 'input checkbox' (&lt;input type="checkbox"&gt;) using HtmlUnit as
 * backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlCheckBoxInput.class)
@IdentifiedBy(HtmlUnitInputCheckBoxIdentifier.class)
public class HtmlUnitInputCheckBox extends HtmlUnitBaseControl<HtmlCheckBoxInput> implements Deselectable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlCheckBoxInput} from the backend
   */
  public HtmlUnitInputCheckBox(final HtmlCheckBoxInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Selectable#select(org.wetator.core.WetContext)
   */
  @Override
  public void select(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    Assert.assertTrue(!tmpHtmlCheckBoxInput.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      tmpHtmlCheckBoxInput.focus();
      if (!tmpHtmlCheckBoxInput.isChecked()) {
        tmpHtmlCheckBoxInput.click();
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (final Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Selectable#isSelected(org.wetator.core.WetContext)
   */
  @Override
  public boolean isSelected(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    return tmpHtmlCheckBoxInput.isChecked();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Deselectable#deselect(org.wetator.core.WetContext)
   */
  @Override
  public void deselect(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    Assert.assertTrue(!tmpHtmlCheckBoxInput.isDisabled(), "elementDisabled", new String[] { getDescribingText() });
    Assert.assertTrue(!tmpHtmlCheckBoxInput.isReadOnly(), "elementReadOnly", new String[] { getDescribingText() });

    try {
      tmpHtmlCheckBoxInput.focus();
      if (tmpHtmlCheckBoxInput.isChecked()) {
        tmpHtmlCheckBoxInput.click();
      }

      // wait for silence
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (final Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#isDisabled(org.wetator.core.WetContext)
   */
  @Override
  public boolean isDisabled(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    return tmpHtmlCheckBoxInput.isDisabled();
  }
}
