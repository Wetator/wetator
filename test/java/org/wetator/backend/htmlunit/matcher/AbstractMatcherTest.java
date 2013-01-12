/*
 * Copyright (c) 2008-2013 wetator.org
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public abstract class AbstractMatcherTest {

  protected List<MatchResult> match(String aHtmlCode, List<SecretString> aSearch, String... anHtmlElementIds)
      throws IOException, InvalidInputException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<MatchResult> tmpMatches = new ArrayList<MatchResult>();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      WPath tmpPath = new WPath(aSearch);

      SearchPattern tmpSearchPattern = tmpPath.getLastNode().getSearchPattern();
      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!tmpPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(tmpPath.getPathNodes());
        tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      tmpMatches.addAll(createMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern).matches(
          tmpHtmlElement));
    }
    return tmpMatches;
  }

  protected abstract AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern);

  protected static void assertMatchEquals(String anExpectedId, FoundType anExpectedFoundType, int anExpectedCoverage,
      int anExpectedDistance, int anExpectedStart, MatchResult anActualMatch) {
    Assert.assertEquals("htmlElement.id", anExpectedId, anActualMatch.getHtmlElement().getId());
    Assert.assertEquals("foundType", anExpectedFoundType, anActualMatch.getFoundType());
    Assert.assertEquals("coverage", anExpectedCoverage, anActualMatch.getCoverage());
    Assert.assertEquals("distance", anExpectedDistance, anActualMatch.getDistance());
    Assert.assertEquals("start", anExpectedStart, anActualMatch.getStart());
  }
}