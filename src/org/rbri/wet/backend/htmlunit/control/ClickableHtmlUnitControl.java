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

import org.rbri.wet.backend.Control.SupportedAction;
import org.rbri.wet.backend.Control.SupportedActions;
import org.rbri.wet.backend.htmlunit.HtmlUnitControl;
import org.rbri.wet.backend.htmlunit.HtmlUnitControl.Identifiers;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlAnchorIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlButtonIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlImageIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlInputButtonIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlInputImageIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlInputResetIdentifier;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlInputSubmitIdentifier;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This is the generic {@link HtmlUnitControl} for all clickables.
 * 
 * @author frank.danek
 */
@SupportedActions(SupportedAction.CLICKABLE)
@Identifiers({ HtmlInputSubmitIdentifier.class, HtmlInputResetIdentifier.class, HtmlInputButtonIdentifier.class,
    HtmlInputImageIdentifier.class, HtmlButtonIdentifier.class, HtmlAnchorIdentifier.class, HtmlImageIdentifier.class })
public class ClickableHtmlUnitControl extends HtmlUnitControl {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlElement} from the backend
   */
  public ClickableHtmlUnitControl(HtmlElement anHtmlElement) {
    super(anHtmlElement);
  }
}
