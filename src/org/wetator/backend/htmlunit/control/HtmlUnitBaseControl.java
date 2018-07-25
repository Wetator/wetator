/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.IBrowser;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.KeySequence;
import org.wetator.backend.control.KeySequence.Key;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.exception.UnsupportedOperationException;
import org.wetator.i18n.Messages;
import org.wetator.util.CssUtil;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.Keyboard;
import com.gargoylesoftware.htmlunit.javascript.host.event.KeyboardEvent;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

/**
 * This is the base implementation of a {@link IControl} using HtmlUnit as backend.
 *
 * @param <T> the type of the {@link HtmlElement}.
 * @author rbri
 * @author frank.danek
 */
public abstract class HtmlUnitBaseControl<T extends HtmlElement> implements IControl {

  private static final Logger LOG = LogManager.getLogger(HtmlUnitBaseControl.class);

  private T htmlElement;

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlElement} from the backend
   */
  protected HtmlUnitBaseControl(final T anHtmlElement) {
    htmlElement = anHtmlElement;
  }

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

  @Override
  public void click(final WetatorContext aWetatorContext) throws ActionException {
    mouseOver(aWetatorContext);

    final HtmlElement tmpHtmlElement = getHtmlElement();

    if (canReceiveFocus(aWetatorContext)) {
      try {
        tmpHtmlElement.focus();
      } catch (final ScriptException e) {
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
      } catch (final WrappedException e) {
        final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
            tmpScriptException);
      }
    }

    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Click - HtmlUnitBaseControl<T>.click() '" + tmpHtmlElement + "'");
      }

      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler((aHtmlPage, anX, anY) -> tmpHtmlElement);
      }

      tmpHtmlElement.click();
      waitForImmediateJobs(aWetatorContext);

      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler(null);
      }
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
  public void clickDouble(final WetatorContext aWetatorContext) throws ActionException {
    mouseOver(aWetatorContext);

    final HtmlElement tmpHtmlElement = getHtmlElement();

    if (canReceiveFocus(aWetatorContext)) {
      try {
        tmpHtmlElement.focus();
      } catch (final ScriptException e) {
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
      } catch (final WrappedException e) {
        final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
            tmpScriptException);
      }
    }

    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("ClickDouble - HtmlUnitBaseControl<T>.clickDouble() '" + tmpHtmlElement + "'");
      }

      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler((aHtmlPage, anX, anY) -> tmpHtmlElement);
      }

      tmpHtmlElement.dblClick();
      waitForImmediateJobs(aWetatorContext);

      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler(null);
      }
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
  public void clickRight(final WetatorContext aWetatorContext) throws ActionException {
    mouseOver(aWetatorContext);

    final HtmlElement tmpHtmlElement = getHtmlElement();

    if (canReceiveFocus(aWetatorContext)) {
      try {
        tmpHtmlElement.focus();
      } catch (final ScriptException e) {
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
      } catch (final WrappedException e) {
        final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
            tmpScriptException);
      }
    }

    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("ClickRight - HtmlUnitBaseControl<T>.clickRight() '" + tmpHtmlElement + "'");
      }

      final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler((aHtmlPage, anX, anY) -> tmpHtmlElement);
      }

      tmpHtmlElement.rightClick();
      waitForImmediateJobs(aWetatorContext);

      if (tmpHtmlPage != null) {
        tmpHtmlPage.setElementFromPointHandler(null);
      }
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
  public void type(final WetatorContext aWetatorContext, final KeySequence aKeySequence) throws ActionException {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    try {
      final Keyboard tmpKeyboard = new Keyboard();

      for (final Key tmpKey : aKeySequence.getKeys()) {
        if (Key.KEY_RETURN == tmpKey) {
          tmpKeyboard.press(KeyboardEvent.DOM_VK_RETURN);
        } else {
          tmpKeyboard.type(tmpKey.getChar());
        }
      }

      tmpHtmlElement.type(tmpKeyboard);

      waitForImmediateJobs(aWetatorContext);
    } catch (final ScriptException e) {
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
    } catch (final WrappedException e) {
      final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
      aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
          tmpScriptException);
    } catch (final BackendException | IOException e) {
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

    final boolean tmpMouseOver = tmpHtmlElement.isMouseOver();
    if (!tmpMouseOver) {
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
    }

    final boolean tmpIsIE = aWetatorContext.getBrowserType() == IBrowser.BrowserType.INTERNET_EXPLORER;
    if (tmpIsIE) {
      // additional mouseMove event
      tmpHtmlElement.mouseMove();
    }

    try {
      // simulate mouse over on the element
      if (!tmpMouseOver) {
        tmpHtmlElement.mouseOver();
      }

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
      final String tmpMessage = Messages.getMessage("backendError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    } catch (final Throwable e) {
      final String tmpMessage = Messages.getMessage("serverError", e.getMessage(), getDescribingText());
      throw new ActionException(tmpMessage, e);
    }
  }

  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final String tmpMessage = Messages.getMessage("disabledCheckNotSupported", getDescribingText());
    throw new UnsupportedOperationException(tmpMessage);
  }

  @Override
  public boolean canReceiveFocus(final WetatorContext aWetatorContext) {
    return false;
  }

  @Override
  public boolean hasFocus(final WetatorContext aWetatorContext) {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
    return tmpHtmlElement.equals(tmpHtmlPage.getFocusedElement());
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

  @Override
  public String getUniqueSelector() {
    final HtmlElement tmpHtmlElement = getHtmlElement();
    return getUniqueSelector(tmpHtmlElement);
  }

  /**
   * Helper that constructs the css selector for the given HtmlElement.
   *
   * @param aHtmlElement the element
   * @return the css selector
   */
  protected String getUniqueSelector(final HtmlElement aHtmlElement) {
    // at least an option might call this with null
    if (null == aHtmlElement) {
      return null;
    }

    HtmlElement tmpHtmlElement = aHtmlElement;
    HtmlElement tmpParent = (HtmlElement) tmpHtmlElement.getParentNode();
    if (null == tmpParent) {
      return null;
    }
    String tmpHtmlElementId = getUniqueElementId(tmpHtmlElement);
    StringBuilder tmpSelector = new StringBuilder();
    while (DomElement.ATTRIBUTE_NOT_DEFINED == tmpHtmlElementId) {
      // @formatter:off
      final StringBuilder tmpSel = new StringBuilder()
          .append('>')
          .append(tmpHtmlElement.getTagName())
          .append(":nth-of-type(")
          .append(childIndex(tmpParent, tmpHtmlElement))
          .append(')')

          .append(tmpSelector);
      // @formatter:on
      tmpSelector = tmpSel;

      if ("body".equalsIgnoreCase(tmpParent.getTagName())) {
        break;
      }

      tmpHtmlElement = tmpParent;
      tmpHtmlElementId = getUniqueElementId(tmpHtmlElement);
      tmpParent = (HtmlElement) tmpHtmlElement.getParentNode();
      if (null == tmpParent) {
        return null;
      }
    }

    if (DomElement.ATTRIBUTE_NOT_DEFINED != tmpHtmlElementId) {
      return "#" + CssUtil.escapeIdentifier(tmpHtmlElementId) + tmpSelector.toString();
    }

    return "body" + tmpSelector.toString();
  }

  private String getUniqueElementId(final HtmlElement aHtmlElement) {
    final String tmpHtmlElementId = aHtmlElement.getId();
    if (DomElement.ATTRIBUTE_NOT_DEFINED != tmpHtmlElementId) {
      final SgmlPage tmpSgmlPage = aHtmlElement.getPage();
      if (tmpSgmlPage instanceof HtmlPage && ((HtmlPage) tmpSgmlPage).getElementsById(tmpHtmlElementId).size() > 1) {
        return DomElement.ATTRIBUTE_NOT_DEFINED;
      }
    }
    return tmpHtmlElementId;
  }

  /**
   * Returns true if the control is part of the given page.
   *
   * @param aPage the page to check
   * @return true or false
   */
  public boolean isPartOf(final Page aPage) {
    final HtmlPage tmpPage = getHtmlElement().getHtmlPageOrNull();
    if (aPage == tmpPage) {
      return true;
    }

    // check frames
    if (aPage instanceof HtmlPage) {
      for (final FrameWindow tmpFrame : ((HtmlPage) aPage).getFrames()) {
        final Page tmpFramePage = tmpFrame.getEnclosedPage();
        if (tmpFramePage instanceof HtmlPage && isPartOf(tmpFramePage)) {
          return true;
        }
      }
    }

    return false;
  }

  private int childIndex(final HtmlElement aParent, final HtmlElement aChild) {
    int tmpRes = 1;
    for (final DomElement tmpDomElement : aParent.getChildElements()) {
      if (tmpDomElement == aChild) {
        return tmpRes;
      }
      if (aChild.getTagName().equalsIgnoreCase(tmpDomElement.getTagName())) {
        tmpRes++;
      }
    }
    return -1;
  }

  /**
   * This annotation contains the {@link HtmlElement} the HtmlUnit control is for.
   *
   * @author frank.danek
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ForHtmlElement {

    /**
     * @return the {@link HtmlElement}
     */
    Class<? extends HtmlElement> value();

    /**
     * @return the name of the discriminating attribute
     */
    String attributeName()

    default "";

    /**
     * @return the possible values of the discriminating attribute
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
  public @interface IdentifiedBy {

    /**
     * @return the identifiers
     */
    Class<? extends AbstractHtmlUnitControlIdentifier>[] value();
  }
}
