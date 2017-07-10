/*
 * Copyright (c) 2008-2017 wetator.org
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

/**
 * This is the implementation of the HTML element 'label' (&lt;label&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlLabel.class)
public class HtmlUnitLabel extends HtmlUnitBaseControl<HtmlLabel> implements ISelectable {

  private static final Log LOG = LogFactory.getLog(HtmlUnitLabel.class);

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlLabel} from the backend
   */
  public HtmlUnitLabel(final HtmlLabel anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlLabel(getHtmlElement());
  }

  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlLabel tmpHtmlLabel = getHtmlElement();

    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Select - HtmlUnitLabel.click() '" + tmpHtmlLabel + "'");
      }
      tmpHtmlLabel.click();

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
      final String tmpMessage = Messages.getMessage("serverError",
          new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
    }
  }

  @Override
  public boolean isSelected(final WetatorContext aWetatorContext) {
    final HtmlLabel tmpHtmlLabel = getHtmlElement();

    final HtmlElement tmpReferencedElement = tmpHtmlLabel.getReferencedElement();
    if (tmpReferencedElement instanceof HtmlInput) {
      return ((HtmlInput) tmpReferencedElement).isChecked();
    }
    return false;
  }

  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    return false;
  }

  @Override
  public boolean canReceiveFocus(final WetatorContext aWetatorContext) {
    return false;
  }
}
