/*
 * Copyright (c) 2008-2025 wetator.org
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlunit.ScriptException;
import org.htmlunit.corejs.javascript.WrappedException;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlLabel;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;

/**
 * This is the implementation of the HTML element 'input checkbox' (&lt;input type="checkbox"&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlCheckBoxInput.class)
@IdentifiedBy(HtmlUnitInputCheckBoxIdentifier.class)
public class HtmlUnitInputCheckBox extends HtmlUnitBaseControl<HtmlCheckBoxInput>
    implements IDeselectable, IHtmlUnitDisableable<HtmlCheckBoxInput>, IHtmlUnitFocusable<HtmlCheckBoxInput> {

  private static final Logger LOG = LogManager.getLogger(HtmlUnitInputCheckBox.class);

  private HtmlLabel htmlLabel;

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlCheckBoxInput} from the backend
   */
  public HtmlUnitInputCheckBox(final HtmlCheckBoxInput anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    final StringBuilder tmpText = new StringBuilder(
        HtmlElementUtil.getDescribingTextForHtmlCheckBoxInput(tmpHtmlCheckBoxInput));
    if (htmlLabel != null) {
      tmpText.append(" by ").append(HtmlElementUtil.getDescribingTextForHtmlLabel(htmlLabel));
    }
    return tmpText.toString();
  }

  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    if (tmpHtmlCheckBoxInput.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlCheckBoxInput.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      if (tmpHtmlCheckBoxInput.isChecked()) {
        aWetatorContext.informListenersWarn("elementAlreadySelected", getDescribingText());
      } else {
        if (htmlLabel == null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Select - HtmlUnitInputCheckBox.click() '" + tmpHtmlCheckBoxInput + "'");
          }
          tmpHtmlCheckBoxInput.click();
        } else {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Select - HtmlUnitInputCheckBox.click() '" + htmlLabel + "'");
          }
          htmlLabel.click();
        }
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
  public boolean isSelected(final WetatorContext aWetatorContext) {
    return getHtmlElement().isChecked();
  }

  @Override
  public void deselect(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlCheckBoxInput tmpHtmlCheckBoxInput = getHtmlElement();

    if (tmpHtmlCheckBoxInput.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlCheckBoxInput.isReadOnly()) {
      final String tmpMessage = Messages.getMessage("elementReadOnly", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      if (tmpHtmlCheckBoxInput.isChecked()) {
        if (htmlLabel == null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Deselect - HtmlUnitInputCheckBox.click() '" + tmpHtmlCheckBoxInput + "'");
          }
          tmpHtmlCheckBoxInput.click();
        } else {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Deselect - HtmlUnitInputCheckBox.click() '" + htmlLabel + "'");
          }
          htmlLabel.click();
        }
      } else {
        aWetatorContext.informListenersWarn("elementAlreadyDeselected", getDescribingText());
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

  /**
   * @param aHtmlLabel the {@link HtmlLabel} the control was found by
   */
  public void setHtmlLabel(final HtmlLabel aHtmlLabel) {
    htmlLabel = aHtmlLabel;
  }
}
