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


package org.wetator.backend.htmlunit.matcher;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlTextArea;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

/**
 * This matcher checks if the attribute 'placeholder' of the given element matches the criteria.
 *
 * @author rbri
 */
public class ByPlaceholderAttributeMatcher extends AbstractByAttributeMatcher {

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByPlaceholderAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_PLACEHOLDER);
    matchType = MatchType.CONTAINS;
  }

  @Override
  protected String getAttributeValue(final HtmlElement anHtmlElement) {
    if (anHtmlElement instanceof HtmlInput) {
      final String tmpValue = ((HtmlInput) anHtmlElement).getValue();
      if (StringUtils.isEmpty(tmpValue)) {
        return anHtmlElement.getAttribute("placeholder");
      }
    } else if (anHtmlElement instanceof HtmlTextArea) {
      final String tmpText = ((HtmlTextArea) anHtmlElement).getText();
      if (StringUtils.isEmpty(tmpText)) {
        return anHtmlElement.getAttribute("placeholder");
      }
    }
    return null;
  }
}
