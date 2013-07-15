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


package org.wetator.backend.htmlunit.finder;

import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
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

    final SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
    final FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);

    if (null == tmpPathSpot) {
      return tmpFoundControls;
    }

    if (aWPath.getLastNode() == null) {
      // no table coordinates supported so far
      // TODO implement table coordinate support for unknown controls
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

    FindSpot tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, Math.max(0, tmpPathSpot.endPos));
    while ((null != tmpHitSpot) && (tmpHitSpot.endPos > -1)) {
      // found a hit

      // find the first element that surrounds this
      for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
        final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
        if ((tmpNodeSpot.startPos <= tmpHitSpot.startPos) && (tmpHitSpot.endPos <= tmpNodeSpot.endPos)) {
          // found one
          String tmpTextBefore = htmlPageIndex.getTextBeforeIncludingMyself(tmpHtmlElement);
          final FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
          final int tmpCoverage = tmpTextBefore.length() - tmpLastOccurence.endPos;

          tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.startPos);
          final int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

          if (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null) {
            tmpFoundControls.add(new HtmlUnitBaseControl<HtmlElement>(tmpHtmlElement),
                WeightedControlList.FoundType.BY_TEXT, tmpCoverage, tmpDistance, tmpNodeSpot.startPos);
          }
          break;
        }
      }

      tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpHitSpot.startPos + 1);
    }
    return tmpFoundControls;
  }
}
