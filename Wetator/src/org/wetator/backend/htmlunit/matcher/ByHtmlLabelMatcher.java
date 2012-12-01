/*
 * Copyright (c) 2008-2012 wetator.org
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
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * This matcher checks if the given {@link HtmlLabel} matches the criteria and labels the needed type of element.
 * 
 * @author frank.danek
 */
public class ByHtmlLabelMatcher extends AbstractHtmlUnitElementMatcher {

  private Class<? extends HtmlElement> clazz;

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aClass the class of the {@link HtmlElement} the matching label labels.
   */
  public ByHtmlLabelMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern, final Class<? extends HtmlElement> aClass) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
    clazz = aClass;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    final List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    if (!(aHtmlElement instanceof HtmlLabel)) {
      return tmpMatches;
    }

    // has the node the text before
    FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (null != pathSpot && pathSpot.getEndPos() <= tmpNodeSpot.getStartPos()) {

      final HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      final String tmpText = htmlPageIndex.getAsTextWithoutFormControls(tmpLabel);
      final int tmpCoverage = searchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        final String tmpForAttribute = tmpLabel.getForAttribute();
        // label contains a for-attribute => find corresponding element
        if (StringUtils.isNotEmpty(tmpForAttribute)) {
          try {
            final HtmlElement tmpElementForLabel = htmlPageIndex.getHtmlElementById(tmpForAttribute);
            if (clazz.isAssignableFrom(tmpElementForLabel.getClass())) {
              if (tmpElementForLabel.isDisplayed()) {
                tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
                final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
                final int tmpDistance = pathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                tmpMatches.add(new MatchResult(tmpElementForLabel, FoundType.BY_LABEL, tmpCoverage, tmpDistance,
                    tmpNodeSpot.getStartPos()));
              }
            }
          } catch (final ElementNotFoundException e) {
            // not found
          }
        }

        // Element must be a nested element of label
        final Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
        for (HtmlElement tmpChildElement : tmpChilds) {
          if (clazz.isAssignableFrom(tmpChildElement.getClass())) {
            if (tmpChildElement.isDisplayed()) {
              tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
              final int tmpDistance = pathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

              tmpMatches.add(new MatchResult(tmpChildElement, FoundType.BY_LABEL, tmpCoverage, tmpDistance, tmpNodeSpot
                  .getStartPos()));
            }
          }
        }
      }
    }
    return tmpMatches;
  }
}
