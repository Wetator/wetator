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

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.rbri.wet.backend.control.Deselectable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitOptionInSelectIdentifier;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * XXX add class jdoc
 * 
 * @author rbri
 * @author frank.danek
 */
@IdentifiedBy({ HtmlUnitOptionInSelectIdentifier.class, HtmlUnitOptionIdentifier.class })
public class HtmlUnitOption extends HtmlUnitBaseControl<HtmlOption> implements Deselectable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlOption} from the backend
   */
  public HtmlUnitOption(HtmlOption anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlOption(getHtmlElement());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Selectable#select(org.rbri.wet.core.WetContext)
   */
  @Override
  public void select(WetContext aWetContext) throws AssertionFailedException {
    HtmlOption tmpHtmlOption = getHtmlElement();

    Assert.assertTrue(!tmpHtmlOption.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      Assert.assertTrue(!tmpHtmlOption.getEnclosingSelect().isDisabled(), "elementDisabled",
          new String[] { getDescribingText() });

      if (!tmpHtmlOption.isSelected()) {
        tmpHtmlOption.click();
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
   * @see org.rbri.wet.backend.control.Selectable#isSelected(org.rbri.wet.core.WetContext)
   */
  @Override
  public boolean isSelected(WetContext aWetContext) throws AssertionFailedException {
    HtmlOption tmpHtmlOption = getHtmlElement();

    return tmpHtmlOption.isSelected();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Deselectable#deselect(org.rbri.wet.core.WetContext)
   */
  @Override
  public void deselect(WetContext aWetContext) throws AssertionFailedException {
    HtmlOption tmpHtmlOption = getHtmlElement();

    Assert.assertTrue(!tmpHtmlOption.isDisabled(), "elementDisabled", new String[] { getDescribingText() });

    try {
      HtmlSelect tmpHtmpSelect = tmpHtmlOption.getEnclosingSelect();
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
}
