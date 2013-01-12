/*
 * Copyright (c) 2008-2013 wetator.org
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
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This matcher checks if the label text before the given element matches the criteria.
 * 
 * @author frank.danek
 */
public class ByLabelTextBeforeMatcher extends AbstractByAttributeMatcher {

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or null if no path given
   * @param aPathSpot the {@link FindSpot} the path was found first or null if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByLabelTextBeforeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_LABEL_TEXT);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected String getAttributeValue(final HtmlElement aHtmlElement) {
    int tmpStartPosition = 0;
    if (pathSpot != null) {
      tmpStartPosition = pathSpot.getEndPos();
    }
    return htmlPageIndex.getLabelTextBefore(aHtmlElement, tmpStartPosition);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#processTextForDistance(java.lang.String)
   */
  @Override
  protected String processTextForDistance(final String aTextBefore) {
    // in this case the label is part of the text before
    // lets try to remove that
    return aTextBefore.substring(0, searchPattern.noOfCharsBeforeLastShortestOccurenceIn(aTextBefore));
  }
}
