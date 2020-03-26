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

import org.wetator.backend.control.IDisableable;
import org.wetator.core.WetatorContext;

import com.gargoylesoftware.htmlunit.html.DisabledElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This interface marks a {@link IDisableable} {@link IHtmlUnitControl}.<br>
 * It provides a default implementation of {@link #isDisabled(WetatorContext)}.
 *
 * @param <T> the type of the {@link DisabledElement}
 * @author frank.danek
 */
public interface IHtmlUnitDisableable<T extends HtmlElement & DisabledElement>
    extends IHtmlUnitControl<T>, IDisableable {

  @Override
  default boolean isDisabled(final WetatorContext aWetatorContext) {
    return getHtmlElement().isDisabled();
  }
}
