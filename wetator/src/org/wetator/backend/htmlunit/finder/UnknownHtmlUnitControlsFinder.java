/*
 * Copyright (c) 2008-2018 wetator.org
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


package org.wetator.backend.htmlunit.finder;

import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByTitleAttributeMatcher;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This finder is a generic finder for all {@link HtmlElement}s not known by the {@link HtmlUnitControlRepository}. Only
 * instances of {@link HtmlUnitUnspecificControl} are returned. This finder supports just three find methods:
 * <ul>
 * <li>by id</li>
 * <li>by title attribute</li>
 * <li>by text (the first {@link HtmlElement} which matches the path and which's text contains the search pattern)</li>
 * </ul>
 *
 * @author rbri
 * @author frank.danek
 */
public class UnknownHtmlUnitControlsFinder extends AbstractHtmlUnitControlsFinder {

  private HtmlUnitControlRepository controlRepository;

  /**
   * The constructor.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aControlRepository the repository of known controls
   */
  public UnknownHtmlUnitControlsFinder(final HtmlPageIndex aHtmlPageIndex,
      final HtmlUnitControlRepository aControlRepository) {
    super(aHtmlPageIndex);

    controlRepository = aControlRepository;
  }

  @Override
  public WeightedControlList find(final WPath aWPath) {
    final WeightedControlList tmpFoundControls = new WeightedControlList();

    if (aWPath.getLastNode() == null && aWPath.getTableCoordinates().isEmpty()) {
      // we do not support this unspecific paths
      return tmpFoundControls;
    }

    SearchPattern tmpPathSearchPattern = null;
    FindSpot tmpPathSpot = null;
    if (!aWPath.getPathNodes().isEmpty()) {
      tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
      tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
    }

    if (tmpPathSpot == FindSpot.NOT_FOUND) {
      return tmpFoundControls;
    }

    if (aWPath.getLastNode() == null || aWPath.getLastNode().isEmpty()) {
      int tmpStartPos = 0;
      if (tmpPathSpot != null) {
        tmpStartPos = Math.max(0, tmpPathSpot.getEndPos());
      }

      for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
        final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
        if (tmpStartPos <= tmpNodeSpot.getStartPos()
            && (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null)
            && (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
                tmpHtmlElement, aWPath.getTableCoordinatesReversed(), htmlPageIndex, null))) {

          final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpHtmlElement);
          final int tmpDeviation = htmlPageIndex.getAsText(tmpHtmlElement).length();

          final int tmpDistance;
          if (tmpPathSearchPattern != null) {
            tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
          } else {
            tmpDistance = tmpTextBefore.length();
          }

          tmpFoundControls.add(new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlElement), FoundType.BY_TEXT,
              tmpDeviation, tmpDistance, tmpNodeSpot.getStartPos(), htmlPageIndex.getIndex(tmpHtmlElement));

          break;
        }
      }
      return tmpFoundControls;
    }

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

    // search with id / title
    final ByIdMatcher tmpIdMatcher = new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
        tmpSearchPattern);
    final ByTitleAttributeMatcher tmpTitleMatcher = new ByTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern,
        tmpPathSpot, tmpSearchPattern);
    for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
      if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
        // id
        List<MatchResult> tmpMatches = tmpIdMatcher.matches(tmpHtmlElement);
        for (final MatchResult tmpMatch : tmpMatches) {
          if (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
              tmpMatch.getHtmlElement(), aWPath.getTableCoordinatesReversed(), htmlPageIndex, tmpPathSpot)) {
            tmpFoundControls.add(new HtmlUnitUnspecificControl<HtmlElement>(tmpMatch.getHtmlElement()),
                tmpMatch.getFoundType(), tmpMatch.getDeviation(), tmpMatch.getDistance(), tmpMatch.getStart(),
                htmlPageIndex.getIndex(tmpMatch.getHtmlElement()));
          }
        }

        // title
        tmpMatches = tmpTitleMatcher.matches(tmpHtmlElement);
        for (final MatchResult tmpMatch : tmpMatches) {
          if (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
              tmpMatch.getHtmlElement(), aWPath.getTableCoordinatesReversed(), htmlPageIndex, null)) {
            tmpFoundControls.add(new HtmlUnitUnspecificControl<HtmlElement>(tmpMatch.getHtmlElement()),
                FoundType.BY_TITLE_TEXT, tmpMatch.getDeviation(), tmpMatch.getDistance(), tmpMatch.getStart(),
                htmlPageIndex.getIndex(tmpMatch.getHtmlElement()));
          }
        }
      }
    }

    int tmpStartPos = 0;
    if (tmpPathSpot != null) {
      tmpStartPos = Math.max(0, tmpPathSpot.getEndPos());
    }
    FindSpot tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpStartPos);
    while (tmpHitSpot != FindSpot.NOT_FOUND && tmpHitSpot.getEndPos() > -1) {
      // found a hit

      // find the first element that surrounds this
      for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
        final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
        if (tmpNodeSpot.getStartPos() <= tmpHitSpot.getStartPos()
            && tmpHitSpot.getEndPos() <= tmpNodeSpot.getEndPos()) {
          // found one
          if ((controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null)
              && (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
                  tmpHtmlElement, aWPath.getTableCoordinatesReversed(), htmlPageIndex, null))) {

            String tmpTextBefore = htmlPageIndex.getTextBeforeIncludingMyself(tmpHtmlElement);
            final FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
            final int tmpDeviation = tmpTextBefore.length() - tmpLastOccurence.getEndPos();

            tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.getStartPos());
            final int tmpDistance;
            if (tmpPathSearchPattern != null) {
              tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }

            tmpFoundControls.add(new HtmlUnitUnspecificControl<HtmlElement>(tmpHtmlElement), FoundType.BY_TEXT,
                tmpDeviation, tmpDistance, tmpNodeSpot.getStartPos(), htmlPageIndex.getIndex(tmpHtmlElement));
          }
          break;
        }
      }

      tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpHitSpot.getStartPos() + 1);
    }
    return tmpFoundControls;
  }
}
