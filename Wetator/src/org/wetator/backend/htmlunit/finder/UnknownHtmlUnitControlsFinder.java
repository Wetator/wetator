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


package org.wetator.backend.htmlunit.finder;

import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This finder is a generic finder for all {@link HtmlElement}s not known by the {@link HtmlUnitControlRepository}. Only
 * instances of {@link HtmlUnitBaseControl} are
 * returned (so no specific subclasses). This finder supports just two find methods:
 * <ul>
 * <li>by id</li>
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

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder#find(WPath)
   */
  @Override
  public WeightedControlList find(final WPath aWPath) {
    final WeightedControlList tmpFoundControls = new WeightedControlList();

    if (aWPath.getLastNode() == null) {
      // no table coordinates supported so far
      // TODO implement table coordinate support for unknown controls
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

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

    // search with id
    for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
      if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
        final List<MatchResult> tmpMatches = new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            tmpSearchPattern).matches(tmpHtmlElement);
        for (MatchResult tmpMatch : tmpMatches) {
          tmpFoundControls.add(new HtmlUnitBaseControl<HtmlElement>(tmpMatch.getHtmlElement()),
              tmpMatch.getFoundType(), tmpMatch.getCoverage(), tmpMatch.getDistance(), tmpMatch.getStart());
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
      for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
        final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
        if ((tmpNodeSpot.getStartPos() <= tmpHitSpot.getStartPos())
            && (tmpHitSpot.getEndPos() <= tmpNodeSpot.getEndPos())) {
          // found one
          String tmpTextBefore = htmlPageIndex.getTextBeforeIncludingMyself(tmpHtmlElement);
          final FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
          final int tmpCoverage = tmpTextBefore.length() - tmpLastOccurence.getEndPos();

          tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.getStartPos());
          final int tmpDistance;
          if (tmpPathSearchPattern != null) {
            tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
          } else {
            tmpDistance = tmpTextBefore.length();
          }

          if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
            tmpFoundControls.add(new HtmlUnitBaseControl<HtmlElement>(tmpHtmlElement),
                WeightedControlList.FoundType.BY_TEXT, tmpCoverage, tmpDistance, tmpNodeSpot.getStartPos());
          }
          break;
        }
      }

      tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpHitSpot.getStartPos() + 1);
    }
    return tmpFoundControls;
  }
}
