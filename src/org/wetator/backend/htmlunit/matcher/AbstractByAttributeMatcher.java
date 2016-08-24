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


package org.wetator.backend.htmlunit.matcher;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This is a base class for all matchers checking if an attribute of a {@link HtmlElement} matches a criteria.
 *
 * @author frank.danek
 */
public abstract class AbstractByAttributeMatcher extends AbstractHtmlUnitElementMatcher {

  /**
   * The {@link MatchType} the matcher should use. It influences the way the criteria must be matched.
   */
  protected MatchType matchType = MatchType.CONTAINS;
  private FoundType foundType;

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or null if no path given
   * @param aPathSpot the {@link FindSpot} the path was found first or null if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aFoundType the {@link FoundType} the matcher should use when adding the element
   */
  public AbstractByAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern, final FoundType aFoundType) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
    foundType = aFoundType;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    final List<MatchResult> tmpMatches = new LinkedList<MatchResult>();

    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return tmpMatches;
    }

    // has the node the text before
    final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (pathSpot == null || pathSpot.getEndPos() <= tmpNodeSpot.getStartPos()) {
      final String tmpValue = getAttributeValue(aHtmlElement);

      if (StringUtils.isNotEmpty(tmpValue) && (MatchType.CONTAINS == matchType || MatchType.STARTS_WITH == matchType
          || MatchType.EXACT == matchType && searchPattern.matches(tmpValue)
          || MatchType.ENDS_WITH == matchType && searchPattern.matchesAtEnd(tmpValue))) {
        final int tmpCoverage;
        if (MatchType.ENDS_WITH == matchType) {
          tmpCoverage = searchPattern.noOfCharsBeforeLastOccurenceIn(tmpValue);
        } else if (MatchType.STARTS_WITH == matchType) {
          tmpCoverage = searchPattern.noOfCharsAfterLastOccurenceIn(tmpValue);
        } else {
          tmpCoverage = searchPattern.noOfSurroundingCharsIn(tmpValue);
        }
        if (tmpCoverage > -1) {
          String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
          tmpTextBefore = processTextForDistance(tmpTextBefore);
          final int tmpDistance;
          if (pathSearchPattern != null) {
            tmpDistance = pathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
          } else {
            tmpDistance = tmpTextBefore.length();
          }
          tmpMatches.add(new MatchResult(aHtmlElement, foundType, tmpCoverage, tmpDistance, tmpNodeSpot.getStartPos()));
        }
      }
    }
    return tmpMatches;
  }

  /**
   * @param aHtmlElement the {@link HtmlElement} to get the attribute value of
   * @return the value of the attribute to be matched
   */
  protected abstract String getAttributeValue(HtmlElement aHtmlElement);

  /**
   * Processed the text used to calculate the distance.<br>
   * The default implementation just returns the original text. Override to change this behavior.
   *
   * @param aTextBefore the text to process
   * @return the processed text
   */
  protected String processTextForDistance(final String aTextBefore) {
    return aTextBefore;
  }

  /**
   * This enum contains all types a match could be done.
   *
   * @author frank.danek
   */
  protected enum MatchType {
    /**
     * The attribute value contains the search pattern describing the element.
     */
    CONTAINS,
    /**
     * The attribute value matches exactly the search pattern describing the element.
     */
    EXACT,
    /**
     * The attribute value starts with the search pattern describing the element.
     */
    STARTS_WITH,
    /**
     * The attribute value ends with the search pattern describing the element.
     */
    ENDS_WITH
  }
}
