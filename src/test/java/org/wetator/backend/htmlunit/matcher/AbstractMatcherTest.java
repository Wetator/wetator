/*
 * Copyright (c) 2008-2025 wetator.org
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.htmlunit.html.HtmlElement;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public abstract class AbstractMatcherTest {

  protected List<MatchResult> match(final String aHtmlCode, final SecretString aSearch,
      final String... anHtmlElementIds) throws IOException, InvalidInputException {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    final WetatorConfiguration tmpConfig = new WetatorConfiguration(new File("."), tmpProperties, new Properties(),
        null);

    final List<MatchResult> tmpMatches = new ArrayList<>();

    PageUtil.consumeHtmlPage(aHtmlCode, tmpHtmlPage -> {
      final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

      for (String tmpHtmlElementId : anHtmlElementIds) {
        final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

        final WPath tmpPath = new WPath(aSearch, tmpConfig);

        final SearchPattern tmpSearchPattern = tmpPath.getLastNode().getSearchPattern();
        SearchPattern tmpPathSearchPattern = null;
        FindSpot tmpPathSpot = null;
        if (!tmpPath.getPathNodes().isEmpty()) {
          tmpPathSearchPattern = SearchPattern.createFromList(tmpPath.getPathNodes());
          tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);
        }

        tmpMatches.addAll(createMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
            .matches(tmpHtmlElement));
      }
    });
    return tmpMatches;
  }

  protected abstract AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern);

  protected static void assertMatchEquals(final String anExpectedId, final FoundType anExpectedFoundType,
      final int anExpectedDeviation, final int anExpectedDistance, final int anExpectedStart,
      final MatchResult anActualMatch) {
    assertEquals("htmlElement.id", anExpectedId, anActualMatch.getHtmlElement().getId());
    assertEquals("foundType", anExpectedFoundType, anActualMatch.getFoundType());
    assertEquals("deviation", anExpectedDeviation, anActualMatch.getDeviation());
    assertEquals("distance", anExpectedDistance, anActualMatch.getDistance());
    assertEquals("start", anExpectedStart, anActualMatch.getStart());
  }
}