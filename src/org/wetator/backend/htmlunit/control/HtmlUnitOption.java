/*
 * Copyright (c) 2008-2021 wetator.org
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
import org.wetator.backend.IBrowser;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionInSelectIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

/**
 * This is the implementation of the HTML element 'option' (&lt;option&gt;) using HtmlUnit as backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlOption.class)
@IdentifiedBy({ HtmlUnitOptionInSelectIdentifier.class, HtmlUnitOptionIdentifier.class })
public class HtmlUnitOption extends HtmlUnitBaseControl<HtmlOption> implements IDeselectable {

  private static final Logger LOG = LogManager.getLogger(HtmlUnitOption.class);

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlOption} from the backend
   */
  public HtmlUnitOption(final HtmlOption anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlOption(getHtmlElement());
  }

  @Override
  public void select(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    if (tmpHtmlOption.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }
    if (tmpHtmlOption.getEnclosingSelect().isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      if (tmpHtmlOption.isSelected()) {
        aWetatorContext.informListenersWarn("elementAlreadySelected", getDescribingText());
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Select - HtmlUnitOption.click() '" + tmpHtmlOption + "'");
        }
        tmpHtmlOption.click(true, false, false);
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
    final HtmlOption tmpHtmlOption = getHtmlElement();

    return tmpHtmlOption.isSelected();
  }

  @Override
  public void deselect(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    if (tmpHtmlOption.isDisabled()) {
      final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
      throw new ActionException(tmpMessage);
    }

    try {
      final HtmlSelect tmpHtmlSelect = tmpHtmlOption.getEnclosingSelect();
      if (tmpHtmlSelect.isMultipleSelectEnabled()) {
        if (tmpHtmlSelect.isDisabled()) {
          final String tmpMessage = Messages.getMessage("elementDisabled", getDescribingText());
          throw new ActionException(tmpMessage);
        }

        if (tmpHtmlOption.isSelected()) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Deselect - HtmlUnitOption.click() '" + tmpHtmlOption + "'");
          }
          tmpHtmlOption.click(false, true, false);
        } else {
          aWetatorContext.informListenersWarn("elementAlreadyDeselected", getDescribingText());
        }
      } else {
        final String tmpMessage = Messages.getMessage("deselectNotSupported", getDescribingText());
        throw new ActionException(tmpMessage);
      }

      // wait for silence
      waitForImmediateJobs(aWetatorContext);
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final ActionException e) {
      throw e;
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }
  }

  @Override
  public void mouseOver(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      // simulate mouse move on the document (outside the element)
      ((HtmlPage) tmpHtmlElement.getPage()).getBody().mouseMove();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      final HtmlSelect tmpSelect = ((HtmlOption) tmpHtmlElement).getEnclosingSelect();

      final boolean tmpIsIE = aWetatorContext.getBrowserType() == IBrowser.BrowserType.INTERNET_EXPLORER;
      if (tmpIsIE) {
        // additional mouseMove event
        tmpSelect.mouseMove();
        tmpSelect.mouseOver();

        // simulate mouse move on the element
        tmpSelect.mouseMove();
      } else {
        // ff does this before reaching the option
        tmpSelect.mouseMove();
        tmpSelect.mouseOver();
        tmpSelect.mouseOut();

        // simulate mouse over on the element
        tmpHtmlElement.mouseOver();

        // simulate mouse move on the element
        tmpHtmlElement.mouseMove();
      }

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
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlOption tmpHtmlOption = getHtmlElement();

    final HtmlSelect tmpHtmlSelect = tmpHtmlOption.getEnclosingSelect();
    final HtmlOptionGroup tmpHtmlOptionGroup = (HtmlOptionGroup) tmpHtmlOption
        .getEnclosingElement(HtmlOptionGroup.TAG_NAME);
    boolean tmpOptionGroupDisabled = false;
    if (tmpHtmlOptionGroup != null) {
      tmpOptionGroupDisabled = tmpHtmlOptionGroup.isDisabled();
    }

    return tmpHtmlOption.isDisabled() || tmpOptionGroupDisabled || tmpHtmlSelect.isDisabled();
  }

  @Override
  public String getUniqueSelector() {
    // highlight the select instead of the option
    final HtmlOption tmpHtmlOption = getHtmlElement();
    final HtmlSelect tmpHtmlSelect = tmpHtmlOption.getEnclosingSelect();
    return getUniqueSelector(tmpHtmlSelect);
  }

  @Override
  public boolean canReceiveFocus(final WetatorContext aWetatorContext) {
    return !isDisabled(aWetatorContext);
  }
}
