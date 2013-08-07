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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.exception.UnsupportedOperationException;
import org.wetator.i18n.Messages;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.DisabledElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This is the base implementation of a {@link IControl} using HtmlUnit as backend.
 * 
 * @param <T> the type of the {@link HtmlElement}.
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitBaseControl<T extends HtmlElement> implements IControl {

  private static final Log LOG = LogFactory.getLog(HtmlUnitInputCheckBox.class);

  private T htmlElement;

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlElement} from the backend
   */
  public HtmlUnitBaseControl(final T anHtmlElement) {
    htmlElement = anHtmlElement;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#hasSameBackendControl(org.wetator.backend.control.IControl)
   */
  @Override
  public boolean hasSameBackendControl(final IControl aControl) {
    if (aControl instanceof HtmlUnitBaseControl<?>) {
      final HtmlUnitBaseControl<?> tmpHtmlUnitControl = (HtmlUnitBaseControl<?>) aControl;

      return getHtmlElement() == tmpHtmlUnitControl.getHtmlElement();
    }
    return false;
  }

  /**
   * @return the backing {@link HtmlElement} from HtmlUnit
   */
  protected T getHtmlElement() {
    return htmlElement;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#click(WetatorContext)
   */
  @Override
  public void click(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      LOG.debug("Click - HtmlUnitBaseControl<T>.click() '" + tmpHtmlElement + "'");
      tmpHtmlElement.click();
      waitForImmediateJobs(aWetatorContext);
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final FailingHttpStatusCodeException e) {
      final String tmpMessage = Messages.getMessage("serverError",
          new String[] { e.getMessage(), getDescribingText() });
      throw new ActionException(tmpMessage, e);
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
   * @see org.wetator.backend.control.IControl#clickDouble(WetatorContext)
   */
  @Override
  public void clickDouble(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      LOG.debug("ClickDouble - HtmlUnitBaseControl<T>.dblClick() '" + tmpHtmlElement + "'");
      tmpHtmlElement.dblClick();
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
   * @see org.wetator.backend.control.IControl#clickRight(WetatorContext)
   */
  @Override
  public void clickRight(final WetatorContext aWetatorContext) throws ActionException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      LOG.debug("ClickRight - HtmlUnitBaseControl<T>.rightClick() '" + tmpHtmlElement + "'");
      tmpHtmlElement.rightClick();
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
   * @see org.wetator.backend.control.IControl#mouseOver(WetatorContext)
   */
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
      // simulate mouse over on the element
      tmpHtmlElement.mouseOver();
      // simulate mouse move on the element
      tmpHtmlElement.mouseMove();
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
   * @see org.wetator.backend.control.IControl#isDisabled(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlElement tmpHtmlElement = getHtmlElement();
    boolean tmpSupported = false;

    if (tmpHtmlElement instanceof DisabledElement) {
      final DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;
      tmpSupported = true;

      if (tmpDisabledElement.isDisabled()) {
        return true;
      }
    }

    // check for text and password because setting readonly for the other inputs is nonsens
    if (tmpHtmlElement instanceof HtmlTextInput || tmpHtmlElement instanceof HtmlPasswordInput) {
      final HtmlInput tmpHtmlInputElement = (HtmlInput) tmpHtmlElement;
      tmpSupported = true;

      if (tmpHtmlInputElement.isReadOnly()) {
        return true;
      }
    }
    if (tmpHtmlElement instanceof HtmlTextArea) {
      final HtmlTextArea tmpHtmlHtmlTextArea = (HtmlTextArea) tmpHtmlElement;
      tmpSupported = true;

      if (tmpHtmlHtmlTextArea.isReadOnly()) {
        return true;
      }
    }

    if (tmpHtmlElement instanceof HtmlTableDataCell) {
      return true;
    }

    if (!tmpSupported) {
      final String tmpMessage = Messages.getMessage("disabledCheckNotSupported", new String[] { getDescribingText() });
      throw new UnsupportedOperationException(tmpMessage);
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#hasFocus(org.wetator.core.WetatorContext)
   */
  @Override
  public boolean hasFocus(final WetatorContext aWetatorContext) {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
    if (tmpHtmlElement.equals(tmpHtmlPage.getFocusedElement())) {
      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlParagraph) {
      return HtmlElementUtil.getDescribingTextForHtmlParagraph((HtmlParagraph) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSpan) {
      return HtmlElementUtil.getDescribingTextForHtmlSpan((HtmlSpan) tmpHtmlElement);
    }

    // handle things that are not implemented at the moment
    final StringBuilder tmpResult = new StringBuilder(42);

    tmpResult.append("[Unknown HtmlElement '");
    tmpResult.append(tmpHtmlElement.getClass());
    tmpResult.append('\'');

    addId(tmpResult, tmpHtmlElement);
    addName(tmpResult, tmpHtmlElement);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.IControl#addHighlightStyle(WetatorConfiguration)
   */
  @Override
  public void addHighlightStyle(final WetatorConfiguration aConfiguration) {
    final HtmlElement tmpHtmlElement = getHtmlElement();
    final StringBuilder tmpStyle = new StringBuilder(tmpHtmlElement.getAttribute("style"));

    tmpStyle
        .append("color: #000000;background-color: #EEEEEE;")
        .append(
            "box-shadow: 0 0 2px 2px #E65212;-moz-box-shadow: 0 0 2px 2px #E65212;-webkit-box-shadow: 0 0 2px 2px #E65212;")
        .append("border-radius: 5px;-moz-border-radius: 5px;-webkit-border-radius: 5px;");

    tmpHtmlElement.setAttribute("style", tmpStyle.toString());
  }

  /**
   * Wait until the 'immediate' JavaScript jobs are finished.
   * Additionally this informs all context listeners if not all jobs
   * finished in the time frame.
   * 
   * @param aContext the context
   * @throws BackendException in case of problems
   */
  protected void waitForImmediateJobs(final WetatorContext aContext) throws BackendException {
    aContext.getBrowser().waitForImmediateJobs();
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpId = anHtmlElement.getAttribute("id");
    if (StringUtils.isNotEmpty(tmpId)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpName = anHtmlElement.getAttribute("name");
    if (StringUtils.isNotEmpty(tmpName)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
  }

  /**
   * This annotation contains the {@link HtmlElement} the HtmlUnit control is for.
   * 
   * @author frank.danek
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ForHtmlElement {
    /**
     * The {@link HtmlElement}.
     */
    Class<? extends HtmlElement> value();

    /**
     * The name of the discriminating attribute.
     */
    String attributeName() default "";

    /**
     * The possible values of the discriminating attribute.
     */
    String[] attributeValues() default { };
  }

  /**
   * This annotation contains the identifiers for the HtmlUnit control.
   * 
   * @author frank.danek
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface IdentifiedBy {
    /**
     * The identifiers.
     */
    Class<? extends AbstractHtmlUnitControlIdentifier>[] value();
  }
}
