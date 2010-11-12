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


package org.rbri.wet.backend.htmlunit.matcher;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.WeightedControlList.FoundType;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.core.searchpattern.SearchPattern;

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
  public ByHtmlLabelMatcher(HtmlPageIndex aHtmlPageIndex, SearchPattern aPathSearchPattern, FindSpot aPathSpot,
      SearchPattern aSearchPattern, Class<? extends HtmlElement> aClass) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
    clazz = aClass;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(HtmlElement aHtmlElement) {
    List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    if (!(aHtmlElement instanceof HtmlLabel)) {
      return tmpMatches;
    }

    // has the node the text before
    FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (null != pathSpot && pathSpot.endPos <= tmpNodeSpot.startPos) {

      HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      String tmpText = htmlPageIndex.getAsText(tmpLabel);
      int tmpCoverage = searchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        String tmpForAttribute = tmpLabel.getForAttribute();
        // label contains a for-attribute => find corresponding element
        if (StringUtils.isNotEmpty(tmpForAttribute)) {
          try {
            HtmlElement tmpElementForLabel = htmlPageIndex.getHtmlElementById(tmpForAttribute);
            if (clazz.isAssignableFrom(tmpElementForLabel.getClass())) {
              if (tmpElementForLabel.isDisplayed()) {
                String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
                int tmpDistance = pathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                tmpMatches.add(new MatchResult(tmpElementForLabel, FoundType.BY_LABEL, tmpCoverage, tmpDistance));
              }
            }
          } catch (ElementNotFoundException e) {
            // not found
          }
        }

        // Element must be a nested element of label
        Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
        for (HtmlElement tmpChildElement : tmpChilds) {
          if (clazz.isAssignableFrom(tmpChildElement.getClass())) {
            if (tmpChildElement.isDisplayed()) {
              String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
              int tmpDistance = pathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

              tmpMatches.add(new MatchResult(tmpChildElement, FoundType.BY_LABEL, tmpCoverage, tmpDistance));
            }
          }
        }
      }
    }
    return tmpMatches;
  }
}
