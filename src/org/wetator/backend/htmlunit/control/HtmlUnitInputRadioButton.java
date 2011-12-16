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

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.wetator.backend.control.ISelectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputRadioButtonIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;

/**
 * This is the implementation of the HTML element 'input radio' (&lt;input type="radio"&gt;) using HtmlUnit as
 * backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlRadioButtonInput.class)
@IdentifiedBy(HtmlUnitInputRadioButtonIdentifier.class)
public class HtmlUnitInputRadioButton extends HtmlUnitBaseControl<HtmlRadioButtonInput> implements ISelectable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlRadioButtonInput} from the backend
   */
  public HtmlUnitInputRadioButton(final HtmlRadioButtonInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlRadioButtonInput(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.ISelectable#select(org.wetator.core.WetatorContext)
   */
  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = getHtmlElement();

    if (tmpHtmlRadioButtonInput.isDisabled()) {
      actionFailed("elementDisabled", new String[] { getDescribingText() });
    }
    if (tmpHtmlRadioButtonInput.isReadOnly()) {
      actionFailed("elementReadOnly", new String[] { getDescribingText() });
    }

    try {
      if (!tmpHtmlRadioButtonInput.isChecked()) {
        tmpHtmlRadioButtonInput.click();
      }

      // wait for silence
      aWetatorContext.getBrowser().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException e) {
      throw e;
    } catch (final Throwable e) {
      actionFailed("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.ISelectable#isSelected(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isSelected(final WetatorContext aWetatorContext) {
    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = getHtmlElement();

    return tmpHtmlRadioButtonInput.isChecked();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlRadioButtonInput tmpHtmlRadioButtonInput = getHtmlElement();

    return tmpHtmlRadioButtonInput.isDisabled();
  }
}
