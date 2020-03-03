/*
 * Copyright (c) 2008-2020 wetator.org
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

import java.util.List;

import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * The base class for all matchers.<br>
 * A matcher checks if a {@link HtmlElement} matches one or multiple criteria. If it matches it is added to the result
 * list. A control may be added multiple times if it matches multiple criteria.
 *
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitElementMatcher {

  /**
   * The {@link HtmlPageIndex} of the page the match is based on.
   */
  protected HtmlPageIndex htmlPageIndex;
  /**
   * The {@link SearchPattern} describing the path to the element.
   */
  protected SearchPattern pathSearchPattern;
  /**
   * The {@link FindSpot} the path was found first.
   */
  protected FindSpot pathSpot;
  /**
   * The {@link SearchPattern} describing the element.
   */
  protected SearchPattern searchPattern;

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
  public AbstractHtmlUnitElementMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    htmlPageIndex = aHtmlPageIndex;
    pathSearchPattern = aPathSearchPattern;
    pathSpot = aPathSpot;
    searchPattern = aSearchPattern;
  }

  /**
   * @param aHtmlElement the element to match
   * @return a list containing the {@link MatchResult}s or an empty list if the given element does not match
   */
  public abstract List<MatchResult> matches(HtmlElement aHtmlElement);

  /**
   * This is a container for the result of a match.
   *
   * @author frank.danek
   */
  public static class MatchResult {

    private HtmlElement htmlElement;
    private FoundType foundType;
    private int deviation;
    private int distance;
    private int start;

    /**
     * @param aHtmlElement the matching {@link HtmlElement}
     * @param aFoundType the {@link FoundType}
     * @param aDeviation the deviation
     * @param aDistance the distance
     * @param aStart the starting position
     */
    public MatchResult(final HtmlElement aHtmlElement, final FoundType aFoundType, final int aDeviation,
        final int aDistance, final int aStart) {
      super();
      htmlElement = aHtmlElement;
      foundType = aFoundType;
      deviation = aDeviation;
      distance = aDistance;
      start = aStart;
    }

    /**
     * @return the htmlElement
     */
    public HtmlElement getHtmlElement() {
      return htmlElement;
    }

    /**
     * @return the foundType
     */
    public FoundType getFoundType() {
      return foundType;
    }

    /**
     * @return the deviation
     */
    public int getDeviation() {
      return deviation;
    }

    /**
     * @return the distance
     */
    public int getDistance() {
      return distance;
    }

    /**
     * @return the start
     */
    public int getStart() {
      return start;
    }
  }
}
