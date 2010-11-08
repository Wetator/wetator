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

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.control.Clickable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.rbri.wet.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.rbri.wet.backend.htmlunit.util.HtmlElementUtil;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

/**
 * This is the implementation of the HTML element 'anchor' (&lt;a&gt;) using HtmlUnit as backend.
 * 
 * @author rbri
 * @author frank.danek
 */
@IdentifiedBy(HtmlUnitAnchorIdentifier.class)
public class HtmlUnitAnchor extends HtmlUnitBaseControl<HtmlAnchor> implements Clickable {

  /**
   * The constructor.
   * 
   * @param anHtmlElement the {@link HtmlAnchor} from the backend
   */
  public HtmlUnitAnchor(HtmlAnchor anHtmlElement) {
    super(anHtmlElement);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#click(org.rbri.wet.core.WetContext)
   */
  @Override
  public void click(WetContext aWetContext) throws AssertionFailedException {
    super.click(aWetContext);

    try {
      HtmlAnchor tmpHtmlAnchor = getHtmlElement();
      String tmpHref = tmpHtmlAnchor.getHrefAttribute();
      if (StringUtils.isNotBlank(tmpHref) && tmpHref.startsWith("#")) {
        tmpHref = tmpHref.substring(1);
        PageUtil.checkAnchor(tmpHref, tmpHtmlAnchor.getPage());
      }
    } catch (AssertionFailedException e) {
      aWetContext.getWetBackend().addFailure(e);
    } catch (Throwable e) {
      aWetContext.getWetBackend().addFailure("serverError", new String[] { e.getMessage(), getDescribingText() }, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl#getDescribingText()
   */
  @Override
  public String getDescribingText() {
    return HtmlElementUtil.getDescribingTextForHtmlAnchor(getHtmlElement());
  }
}
