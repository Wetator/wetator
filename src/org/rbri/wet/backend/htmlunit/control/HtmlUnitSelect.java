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

import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitSelectIdentifier;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;

import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * XXX add class jdoc
 * 
 * @author rbri
 * @author frank.danek
 */
@IdentifiedBy(HtmlUnitSelectIdentifier.class)
public class HtmlUnitSelect extends HtmlUnitBaseControl<HtmlSelect> {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlSelect} from the backend
   */
  public HtmlUnitSelect(HtmlSelect anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlSelect(getHtmlElement());
  }
}
