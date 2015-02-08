/*
 * Copyright (c) 2008-2015 wetator.org
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

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;

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
   * @see org.wetator.backend.htmlunit.control.HtmlUnitBaseControl#click(org.wetator.core.WetatorContext)
   */
  @Override
  public void click(final WetatorContext aWetatorContext) throws ActionException {
    super.click(aWetatorContext);

    try {
      final HtmlAnchor tmpHtmlAnchor = getHtmlElement();
      String tmpHref = tmpHtmlAnchor.getHrefAttribute();
      if (StringUtils.isNotBlank(tmpHref) && '#' == tmpHref.charAt(0)) {
        tmpHref = tmpHref.substring(1);
        PageUtil.checkAnchor(tmpHref, tmpHtmlAnchor.getPage());
      }
    } catch (final AssertionException e) {
      // we are in an action so build the correct exception
      throw new ActionException(e.getMessage(), e.getCause());
    }
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
