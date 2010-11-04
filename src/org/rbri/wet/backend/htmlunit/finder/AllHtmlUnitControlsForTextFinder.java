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


package org.rbri.wet.backend.htmlunit.finder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl;
import org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.rbri.wet.backend.htmlunit.matcher.ByIdMatcher;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public class AllHtmlUnitControlsForTextFinder extends AbstractHtmlUnitControlsFinder {

  /**
   * The constructor.
   * 
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aThreadPool the thread pool to use for worker threads
   */
  public AllHtmlUnitControlsForTextFinder(HtmlPage aHtmlPage, DomNodeText aDomNodeText, ThreadPoolExecutor aThreadPool) {
    super(aHtmlPage, aDomNodeText, aThreadPool);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder#find(java.util.List)
   */
  @Override
  public WeightedControlList find(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);
    if (null == tmpPathSpot) {
      return tmpFoundElements;
    }

    // search with id
    for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
      List<MatchResult> tmpMatches = new ByIdMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
          tmpFoundElements).matches(tmpHtmlElement);
      for (MatchResult tmpMatch : tmpMatches) {
        tmpFoundElements.add(new HtmlUnitBaseControl<HtmlElement>(tmpMatch.getHtmlElement()), tmpMatch.getFoundType(),
            tmpMatch.getCoverage(), tmpMatch.getDistance());
      }
    }

    FindSpot tmpHitSpot = domNodeText.firstOccurence(tmpSearchPattern, Math.max(0, tmpPathSpot.endPos));
    while ((null != tmpHitSpot) && (tmpHitSpot.endPos > -1)) {
      // found a hit

      // find the first element that surrounds this
      for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElementsBottomUp()) {
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if ((tmpNodeSpot.startPos <= tmpHitSpot.startPos) && (tmpHitSpot.endPos <= tmpNodeSpot.endPos)) {
          // found one
          String tmpTextBefore = domNodeText.getTextBeforeIncludingMyself(tmpHtmlElement);
          FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
          int tmpCoverage = tmpTextBefore.length() - tmpLastOccurence.endPos;

          tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.startPos);
          int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

          tmpFoundElements.add(new HtmlUnitBaseControl<HtmlElement>(tmpHtmlElement),
              WeightedControlList.FoundType.BY_TEXT, tmpCoverage, tmpDistance);
          break;
        }
      }

      tmpHitSpot = domNodeText.firstOccurence(tmpSearchPattern, tmpHitSpot.startPos + 1);
    }
    return tmpFoundElements;
  }
}
