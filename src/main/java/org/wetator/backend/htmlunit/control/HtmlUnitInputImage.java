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

import org.htmlunit.html.HtmlImageInput;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputImageIdentifier;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;

/**
 * This is the implementation of the HTML element 'input image' (&lt;input type="image"&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlImageInput.class)
@IdentifiedBy(HtmlUnitInputImageIdentifier.class)
public class HtmlUnitInputImage extends HtmlUnitBaseControl<HtmlImageInput>
    implements IClickable, IHtmlUnitDisableable<HtmlImageInput>, IHtmlUnitFocusable<HtmlImageInput> {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlImageInput} from the backend
   */
  public HtmlUnitInputImage(final HtmlImageInput anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlImageInput(getHtmlElement());
  }
}
