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

import org.htmlunit.html.HtmlOptionGroup;
import org.htmlunit.html.HtmlSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionGroupIdentifier;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.core.WetatorContext;

/**
 * This is the implementation of the HTML element 'option group' (&lt;optgroup&gt;) using HtmlUnit as backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlOptionGroup.class)
@IdentifiedBy(HtmlUnitOptionGroupIdentifier.class)
public class HtmlUnitOptionGroup extends HtmlUnitBaseControl<HtmlOptionGroup>
    implements IHtmlUnitDisableable<HtmlOptionGroup>, IHtmlUnitFocusable<HtmlOptionGroup> {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlOptionGroup} from the backend
   */
  public HtmlUnitOptionGroup(final HtmlOptionGroup anHtmlElement) {
    super(anHtmlElement);
  }

  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlOptionGroup(getHtmlElement());
  }

  @Override
  public boolean isDisabled(final WetatorContext aWetatorContext) {
    final HtmlOptionGroup tmpHtmlOptionGroup = getHtmlElement();

    return tmpHtmlOptionGroup.isDisabled() || tmpHtmlOptionGroup.getEnclosingSelect().isDisabled();
  }

  @Override
  public String getUniqueSelector() {
    // highlight the select instead of the optgroup
    final HtmlOptionGroup tmpHtmlOptionGroup = getHtmlElement();
    final HtmlSelect tmpHtmlSelect = tmpHtmlOptionGroup.getEnclosingSelect();
    return getUniqueSelector(tmpHtmlSelect);
  }
}
