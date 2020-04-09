/*
 * Copyright (c) 2008-2020 wetator.org
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
public class ByLabelingTextBeforeAsTextMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void notLabelingText_before() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Marker</p>"
        + "<input id='otherId' type='text'>"
        + "<p>Some Text .... </p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void notLabelingText_after() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some Text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<input id='myId' type='text'>"
        + "<p>Marker</p>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 15, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Mark*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 15, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*rker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 17, 21, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("arke");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 2, 16, 21, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void empty_textBefore_notLabelingText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void empty_textBefore_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 0, 21, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Mark* > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 2, 21, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*rker > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 0, 21, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("arke > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 1, 21, tmpMatches.get(0));
  }

  @Test
  public void full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 6, 21, tmpMatches.get(0));
  }

  @Test
  public void full_textBefore_insideLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>text .... </p>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 5, 6, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Mark*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 6, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *rker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 0, 8, 21, tmpMatches.get(0));
  }

  @Test
  public void part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > arke");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_TEXT, 2, 7, 21, tmpMatches.get(0));
  }

  @Test
  public void full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId1' type='text'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId2' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' type='text'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected List<MatchResult> match(final String aHtmlCode, final SecretString aSearch,
      final String... anHtmlElementIds) throws IOException, InvalidInputException {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    final WetatorConfiguration tmpConfig = new WetatorConfiguration(new File("."), tmpProperties, null);

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final List<MatchResult> tmpMatches = new ArrayList<>();
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

      tmpMatches.addAll(new ByLabelingTextBeforeAsTextMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
          tmpSearchPattern, tmpPath).matches(tmpHtmlElement));
    }
    return tmpMatches;
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return null;
  }
}
