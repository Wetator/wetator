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

import org.htmlunit.html.HtmlResetInput;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputResetIdentifier;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;

/**
 * This is the implementation of the HTML element 'input reset' (&lt;input type="reset"&gt;) using HtmlUnit as
 * backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlResetInput.class)
@IdentifiedBy(HtmlUnitInputResetIdentifier.class)
public class HtmlUnitInputReset extends HtmlUnitBaseControl<HtmlResetInput>
    implements IClickable, IHtmlUnitDisableable<HtmlResetInput>, IHtmlUnitFocusable<HtmlResetInput> {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlResetInput} from the backend
   */
  public HtmlUnitInputReset(final HtmlResetInput anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlResetInput(getHtmlElement());
  }
}
