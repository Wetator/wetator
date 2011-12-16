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

import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;

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
public class HtmlUnitInputCheckBox extends HtmlUnitBaseControl<HtmlCheckBoxInput> implements IDeselectable {

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
   * @see org.wetator.backend.control.ISelectable#select(org.wetator.core.WetatorContext)
   */
  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    if (tmpHtmlCheckBoxInput.isDisabled()) {
      actionFailed("elementDisabled", new String[] { getDescribingText() });
    }
    if (tmpHtmlCheckBoxInput.isReadOnly()) {
      actionFailed("elementReadOnly", new String[] { getDescribingText() });
    }

    try {
      tmpHtmlCheckBoxInput.focus();
      if (!tmpHtmlCheckBoxInput.isChecked()) {
        tmpHtmlCheckBoxInput.click();
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
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    return tmpHtmlCheckBoxInput.isChecked();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IDeselectable#deselect(org.wetator.core.WetatorContext)
   */
  @Override
  public void deselect(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    if (tmpHtmlCheckBoxInput.isDisabled()) {
      actionFailed("elementDisabled", new String[] { getDescribingText() });
    }
    if (tmpHtmlCheckBoxInput.isReadOnly()) {
      actionFailed("elementReadOnly", new String[] { getDescribingText() });
    }

    try {
      tmpHtmlCheckBoxInput.focus();
      if (tmpHtmlCheckBoxInput.isChecked()) {
        tmpHtmlCheckBoxInput.click();
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
   * @see org.wetator.backend.control.IControl#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    return tmpHtmlCheckBoxInput.isDisabled();
  }
}
