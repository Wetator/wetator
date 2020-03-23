/*
 * Copyright (c) 2008-2020 wetator.org
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

import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IFocusable;
import org.wetator.backend.htmlunit.util.ExceptionUtil;
import org.wetator.core.WetatorContext;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

/**
 * This is the base implementation of a {@link IFocusable} {@link IControl} using HtmlUnit as backend.
 *
 * @param <T> the type of the {@link HtmlElement}.
 * @author frank.danek
 */
public abstract class HtmlUnitFocusableControl<T extends HtmlElement> extends HtmlUnitBaseControl<T>
    implements IFocusable {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlElement} from the backend
   */
  protected HtmlUnitFocusableControl(final T anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  protected void focus(final WetatorContext aWetatorContext) {
    if (canReceiveFocus(aWetatorContext)) {
      try {
        getHtmlElement().focus();
      } catch (final ScriptException e) {
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { e.getMessage() }, e);
      } catch (final WrappedException e) {
        final Exception tmpScriptException = ExceptionUtil.getScriptExceptionCauseIfPossible(e);
        aWetatorContext.getBrowser().addFailure("javascriptError", new String[] { tmpScriptException.getMessage() },
            tmpScriptException);
      }
    }
  }

  @Override
  public boolean hasFocus(final WetatorContext aContext) {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
    return tmpHtmlElement.equals(tmpHtmlPage.getFocusedElement());
  }
}
