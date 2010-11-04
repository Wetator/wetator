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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

import org.rbri.wet.backend.control.Control;
import org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.rbri.wet.backend.htmlunit.util.ExceptionUtil;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.DisabledElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

/**
 * The base HtmlUnit control implementation.
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
   * @see org.rbri.wet.backend.control.Control#hasSameBackendControl(org.rbri.wet.backend.control.Control)
   */
  @Override
  public boolean hasSameBackendControl(final Control aControl) {
    if (aControl instanceof HtmlUnitBaseControl<?>) {
      HtmlUnitBaseControl<?> tmpHtmlUnitControl = (HtmlUnitBaseControl<?>) aControl;

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
   * @see org.rbri.wet.backend.control.Control#click(WetContext)
   */
  @Override
  public void click(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.click();
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
   * @see org.rbri.wet.backend.control.Control#mouseOver(WetContext)
   */
  @Override
  public void mouseOver(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      tmpHtmlElement.focus();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    }

    try {
      tmpHtmlElement.mouseOver();
      aWetContext.getWetBackend().waitForImmediateJobs();
    } catch (ScriptException e) {
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (WrappedException e) {
      Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetContext.getWetBackend().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Control#isDisabled(org.rbri.wet.core.WetContext)
   */
  @Override
  public boolean isDisabled(final WetContext aWetContext) throws AssertionFailedException {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof DisabledElement) {
      DisabledElement tmpDisabledElement = (DisabledElement) tmpHtmlElement;

      return tmpDisabledElement.isDisabled();
    } else if (tmpHtmlElement instanceof HtmlTableDataCell) {
      return true;
    }
    Assert.fail("disabledCheckNotSupported", new String[] { getDescribingText() });

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.control.Control#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlParagraph) {
      return HtmlElementUtil.getDescribingTextForHtmlParagraph((HtmlParagraph) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSpan) {
      return HtmlElementUtil.getDescribingTextForHtmlSpan((HtmlSpan) tmpHtmlElement);
    }

    // handle things that are not implemented at the moment
    StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[Unknown HtmlElement '");
    tmpResult.append(tmpHtmlElement.getClass());
    tmpResult.append("'");

    addId(tmpResult, tmpHtmlElement);
    addName(tmpResult, tmpHtmlElement);

    tmpResult.append("]");
    return tmpResult.toString();
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    String tmpId = anHtmlElement.getAttribute("id");
    if ((null != tmpId) && (tmpId.length() > 0)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    String tmpName = anHtmlElement.getAttribute("name");
    if ((null != tmpName) && (tmpName.length() > 0)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
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
