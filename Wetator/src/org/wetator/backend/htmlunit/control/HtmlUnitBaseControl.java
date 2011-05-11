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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.wetator.backend.control.Control;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetConfiguration;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;

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
 * This is the base implementation of a {@link Control} using HtmlUnit as backend.
 * 
 * @param <T> the type of the {@link HtmlElement}.
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitBaseControl<T extends HtmlElement> implements Control {

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
   * @see org.wetator.backend.control.Control#hasSameBackendControl(org.wetator.backend.control.Control)
   */
  @Override
  public boolean hasSameBackendControl(final Control aControl) {
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
   * @see org.wetator.backend.control.Control#click(WetContext)
   */
  @Override
  public void click(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.click();
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
   * @see org.wetator.backend.control.Control#clickDouble(WetContext)
   */
  @Override
  public void clickDouble(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.dblClick();
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
   * @see org.wetator.backend.control.Control#clickRight(WetContext)
   */
  @Override
  public void clickRight(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.rightClick();
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
   * @see org.wetator.backend.control.Control#mouseOver(WetContext)
   */
  @Override
  public void mouseOver(final WetContext aWetContext) throws AssertionFailedException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      // simulate mouse move on the document (outside the element)
      ((HtmlPage) tmpHtmlElement.getPage()).getBody().mouseMove();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      // simulate mouse over on the element
      tmpHtmlElement.mouseOver();
      // simulate mouse move on the element
      tmpHtmlElement.mouseMove();
      aWetContext.getBrowser().waitForImmediateJobs();
    } catch (final ScriptException e) {
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final Throwable e) {
      aWetContext.getBrowser().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#isDisabled(org.wetator.core.WetContext)
   */
  @Override
  public boolean isDisabled(final WetContext aWetContext) throws AssertionFailedException {
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
    if ((tmpHtmlElement instanceof HtmlTextInput) || (tmpHtmlElement instanceof HtmlPasswordInput)) {
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
      Assert.fail("disabledCheckNotSupported", new String[] { getDescribingText() });
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#isDisabled(org.wetator.core.WetContext)
   */
  @Override
  public boolean hasFocus(final WetContext aWetContext) throws AssertionFailedException {
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
   * @see org.wetator.backend.control.Control#getDescribingText()
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
    final StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[Unknown HtmlElement '");
    tmpResult.append(tmpHtmlElement.getClass());
    tmpResult.append("'");

    addId(tmpResult, tmpHtmlElement);
    addName(tmpResult, tmpHtmlElement);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.control.Control#addHighlightStyle(WetConfiguration)
   */
  @Override
  public void addHighlightStyle(final WetConfiguration aWetConfiguration) {
    final HtmlElement tmpHtmlElement = getHtmlElement();
    final StringBuilder tmpStyle = new StringBuilder(tmpHtmlElement.getAttribute("style"));

    tmpStyle.append("color: #000000;");
    tmpStyle.append("background-color: #EEEEEE;");
    tmpStyle.append("box-shadow: 0 0 2px 2px #E65212;");
    tmpStyle.append("-moz-box-shadow: 0 0 2px 2px #E65212;");
    tmpStyle.append("-webkit-box-shadow: 0 0 2px 2px #E65212;");
    tmpStyle.append("border-radius: 5px;");
    tmpStyle.append("-moz-border-radius: 5px;");
    tmpStyle.append("-webkit-border-radius: 5px;");

    tmpHtmlElement.setAttribute("style", tmpStyle.toString());
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpId = anHtmlElement.getAttribute("id");
    if ((null != tmpId) && (tmpId.length() > 0)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpName = anHtmlElement.getAttribute("name");
    if ((null != tmpName) && (tmpName.length() > 0)) {
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
