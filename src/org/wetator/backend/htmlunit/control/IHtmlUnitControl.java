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

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * The common interface for a control using HtmlUnit as backend.
 *
 * @param <T> the type of the {@link HtmlElement}
 * @author frank.danek
 */
public interface IHtmlUnitControl<T extends HtmlElement> extends IControl {

  /**
   * @return the backing {@link HtmlElement} from HtmlUnit
   */
  T getHtmlElement();
}
