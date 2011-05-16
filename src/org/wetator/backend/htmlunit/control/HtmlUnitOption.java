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

import org.wetator.backend.control.Deselectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionInSelectIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * This is the implementation of the HTML element 'option' (&lt;option&gt;) using HtmlUnit as backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlOption.class)
@IdentifiedBy({ HtmlUnitOptionInSelectIdentifier.class, HtmlUnitOptionIdentifier.class })
public class HtmlUnitOption extends HtmlUnitBaseControl<HtmlOption> implements Deselectable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlOption} from the backend
   */
  public HtmlUnitOption(final HtmlOption anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlOption(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Selectable#select(org.wetator.core.WetatorContext)
   */
  @Override
  public void select(final WetatorContext aWetatorContext) throws AssertionFailedException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    Assert.assertTrue(!tmpHtmlOption.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      Assert.assertTrue(!tmpHtmlOption.getEnclosingSelect().isDisabled(), "elementDisabled",
          new String[] { getDescribingText() });

      if (!tmpHtmlOption.isSelected()) {
        tmpHtmlOption.click();
      }

      // wait for silence
      aWetatorContext.getBrowser().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final AssertionFailedException e) {
      aWetatorContext.getBrowser().addFailure(e);
    } catch (final Throwable e) {
      aWetatorContext.getBrowser().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Selectable#isSelected(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isSelected(final WetatorContext aWetatorContext) throws AssertionFailedException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    return tmpHtmlOption.isSelected();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Deselectable#deselect(org.wetator.core.WetatorContext)
   */
  @Override
  public void deselect(final WetatorContext aWetatorContext) throws AssertionFailedException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    Assert.assertTrue(!tmpHtmlOption.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      final HtmlSelect tmpHtmpSelect = tmpHtmlOption.getEnclosingSelect();
      if (tmpHtmpSelect.isMultipleSelectEnabled()) {
        Assert.assertTrue(!tmpHtmpSelect.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

        if (tmpHtmlOption.isSelected()) {
          // TODO event support
          tmpHtmlOption.setSelected(false);
          // tmpHtmlOption.click(false, true, false);
        }
      } else {
        Assert.fail("deselectNotSupported", new String[] { getDescribingText() });
      }

      // wait for silence
      aWetatorContext.getBrowser().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final AssertionFailedException e) {
      aWetatorContext.getBrowser().addFailure(e);
    } catch (final Throwable e) {
      aWetatorContext.getBrowser().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) throws AssertionFailedException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    return tmpHtmlOption.isDisabled();
  }
}
