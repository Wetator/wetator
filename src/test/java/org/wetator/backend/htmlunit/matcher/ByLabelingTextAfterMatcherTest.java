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

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class ByLabelingTextAfterMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>Marker"
        + "<input id='otherId2' type='checkbox'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void notLabelingText_before() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>Marker"
        + "<input id='myId' type='checkbox'>"
        + "<input id='otherId2' type='checkbox'>"
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
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>"
        + "<input id='otherId2' type='checkbox'>Marker"
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
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>Marker"
        + "<input id='otherId2' type='checkbox'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>Marker"
        + "<input id='otherId2' type='checkbox'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Mark*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>Marker"
        + "<input id='otherId2' type='checkbox'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*rker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input id='otherId1' type='checkbox'>"
        + "<input id='myId' type='checkbox'>Marker"
        + "<input id='otherId2' type='checkbox'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("arke");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId1", "otherId2");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 2, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_textBefore_insideLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some</p>"
        + "<input id='myId' type='checkbox'>"
        + "<p>text ....</p>"
        + "Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > Mark*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *rker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > arke");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABELING_TEXT, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='otherId' type='checkbox'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input id='myId' type='checkbox'>Marker"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > Marker");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByLabelingTextAfterMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
