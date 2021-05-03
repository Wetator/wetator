/*
 * Copyright (c) 2008-2021 wetator.org
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * This matcher checks if the given {@link HtmlLabel} matches the criteria and labels the needed type of element.
 *
 * @author frank.danek
 */
public class ByHtmlLabelMatcher extends AbstractHtmlUnitElementMatcher {

  private Class<? extends HtmlElement> clazz;
  private boolean matchInvisible;

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aClass the class of the {@link HtmlElement} the matching label labels
   */
  public ByHtmlLabelMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern, final Class<? extends HtmlElement> aClass) {
    this(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, aClass, false);
  }

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aClass the class of the {@link HtmlElement} the matching label labels
   * @param aMatchInvisible if <code>true</code> also invisible (= not displayed) elements match
   */
  public ByHtmlLabelMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern, final Class<? extends HtmlElement> aClass,
      final boolean aMatchInvisible) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
    clazz = aClass;
    matchInvisible = aMatchInvisible;
  }

  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    if (!(aHtmlElement instanceof HtmlLabel)) {
      return Collections.emptyList();
    }

    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return Collections.emptyList();
    }

    // has the search pattern something to match
    if (searchPattern.getMinLength() == 0) {
      return Collections.emptyList();
    }

    // has the node the text before
    FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (pathSpot == null || pathSpot.getEndPos() <= tmpNodeSpot.getStartPos()) {
      final HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      final String tmpText = htmlPageIndex.getAsTextWithoutFormControls(tmpLabel);
      final int tmpDeviation = searchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpDeviation > -1) {
        final HtmlElement tmpLabeledElement = tmpLabel.getLabeledElement();
        if (tmpLabeledElement != null && clazz.isAssignableFrom(tmpLabeledElement.getClass())
            && (htmlPageIndex.isVisible(tmpLabeledElement) || matchInvisible)) {
          tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
          final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
          final int tmpDistance;
          if (pathSearchPattern != null) {
            tmpDistance = pathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
          } else {
            tmpDistance = tmpTextBefore.length();
          }
          return Arrays.asList(new ByHtmlLabelMatchResult(tmpLabeledElement, tmpLabel, FoundType.BY_LABEL_ELEMENT,
              tmpDeviation, tmpDistance, tmpNodeSpot.getStartPos()));
        }
      }
    }

    return Collections.emptyList();
  }

  /**
   * Special {@link AbstractHtmlUnitElementMatcher.MatchResult} for transporting
   * the {@link HtmlLabel} the element was found by.
   *
   * @author frank.danek
   */
  public static class ByHtmlLabelMatchResult extends MatchResult {

    private HtmlLabel label;

    /**
     * @param aHtmlElement the matching {@link HtmlElement}
     * @param aLabel the {@link HtmlLabel} the element was found by
     * @param aFoundType the {@link FoundType}
     * @param aDeviation the deviation
     * @param aDistance the distance
     * @param aStart the starting position
     */
    public ByHtmlLabelMatchResult(final HtmlElement aHtmlElement, final HtmlLabel aLabel, final FoundType aFoundType,
        final int aDeviation, final int aDistance, final int aStart) {
      super(aHtmlElement, aFoundType, aDeviation, aDistance, aStart);

      label = aLabel;
    }

    /**
     * @return the {@link HtmlLabel} the element was found by
     */
    public HtmlLabel getLabel() {
      return label;
    }
  }
}
