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

import org.rbri.wet.backend.control.Clickable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.Identifiers;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlInputButtonIdentifier;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;

import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;

/**
 * @author frank.danek
 */
@Identifiers(HtmlInputButtonIdentifier.class)
public class HtmlUnitInputButton extends HtmlUnitBaseControl<HtmlButtonInput> implements Clickable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlButtonInput} from the backend
   */
  public HtmlUnitInputButton(HtmlButtonInput anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlButtonInput(getHtmlElement());
  }
}
