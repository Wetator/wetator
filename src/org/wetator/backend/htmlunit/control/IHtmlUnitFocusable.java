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

import org.wetator.backend.control.IFocusable;
import org.wetator.core.WetatorContext;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This interface marks a {@link IFocusable} {@link IHtmlUnitControl}.<br>
 * It provides a default implementation of {@link #hasFocus(WetatorContext)}.
 *
 * @param <T> the type of the {@link HtmlElement}
 * @author frank.danek
 */
public interface IHtmlUnitFocusable<T extends HtmlElement> extends IHtmlUnitControl<T>, IFocusable {

  @Override
  default boolean hasFocus(final WetatorContext aWetatorContext) {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    final HtmlPage tmpHtmlPage = (HtmlPage) tmpHtmlElement.getPage();
    return tmpHtmlElement.equals(tmpHtmlPage.getFocusedElement());
  }
}
