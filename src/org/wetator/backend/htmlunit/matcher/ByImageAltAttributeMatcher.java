/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.backend.htmlunit.matcher;

import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;

/**
 * This matcher checks if the attribute 'alt' of the given image ({@link HtmlImage} or {@link HtmlImageInput}) matches
 * the criteria.
 * 
 * @author frank.danek
 */
public class ByImageAltAttributeMatcher extends AbstractByAttributeMatcher {

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByImageAltAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_IMG_ALT_ATTRIBUTE);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected String getAttributeValue(final HtmlElement aHtmlElement) {
    String tmpValue = null;
    if (aHtmlElement instanceof HtmlImage) {
      tmpValue = ((HtmlImage) aHtmlElement).getAltAttribute();
    }
    if (aHtmlElement instanceof HtmlImageInput) {
      tmpValue = ((HtmlImageInput) aHtmlElement).getAltAttribute();
    }
    return tmpValue;
  }
}
