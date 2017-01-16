/*
 * Copyright (c) 2008-2017 wetator.org
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
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

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public class ByWholeTextBeforeMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 21, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marke*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 21, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*arker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 21, 21, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("arke");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 1, 21, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker1</p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker2</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker1 > Marker2");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 8, 15, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker1</p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker2</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker1 > Marke*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 8, 15, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker1</p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker2</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker1 > *rker2");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 8, 15, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker1</p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker2</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker1 > arker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 1, 8, 15, tmpMatches.get(0));
  }

  @Test
  public void wildcardOnly_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker1</p>"
        + "<input id='myId' type='text'>"
        + "<p>Marker2</p>"
        + "<input id='otherId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker1 > *");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    Assert.assertEquals(2, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 0, 7, tmpMatches.get(0));
    assertMatchEquals("otherId", FoundType.BY_TEXT, 0, 8, 15, tmpMatches.get(1));
  }

  @Override
  protected List<MatchResult> match(final String aHtmlCode, final SecretString aSearch,
      final String... anHtmlElementIds) throws IOException, InvalidInputException {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    final WetatorConfiguration tmpConfig = new WetatorConfiguration(new File("."), tmpProperties, null);

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final List<MatchResult> tmpMatches = new ArrayList<MatchResult>();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      final WPath tmpPath = new WPath(aSearch, tmpConfig);

      final List<SecretString> tmpWholePath = new ArrayList<SecretString>(tmpPath.getPathNodes());
      tmpWholePath.add(tmpPath.getLastNode());
      final SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(tmpWholePath);
      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!tmpPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(tmpPath.getPathNodes());
        tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      tmpMatches.addAll(
          new ByWholeTextBeforeMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpWholePathSearchPattern)
              .matches(tmpHtmlElement));
    }
    return tmpMatches;
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return null;
  }
}
