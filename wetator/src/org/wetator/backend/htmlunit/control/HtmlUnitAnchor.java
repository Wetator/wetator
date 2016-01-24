/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.wetator.backend.control.IClickable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

/**
 * This is the implementation of the HTML element 'anchor' (&lt;a&gt;) using HtmlUnit as backend.
 *
 * @author rbri
 * @author frank.danek
 */
@ForHtmlElement(HtmlAnchor.class)
@IdentifiedBy(HtmlUnitAnchorIdentifier.class)
public class HtmlUnitAnchor extends HtmlUnitBaseControl<HtmlAnchor> implements IClickable {

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlAnchor} from the backend
   */
  public HtmlUnitAnchor(final HtmlAnchor anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlAnchor(getHtmlElement());
  }
}
