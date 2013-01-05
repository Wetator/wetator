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


package org.wetator.backend.htmlunit.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class ByLabelTextBeforeMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("not", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 0, 6, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Mark*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 0, 6, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*rker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    // TODO is distance 2 correct here? it's more distance plus coverage
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 2, 6, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("arke", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    // TODO is distance 1 correct here? it's more distance plus coverage
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 2, 1, 6, tmpMatches.get(0));
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Marker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 6, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Mark*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 6, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("*rker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    // TODO is distance 8 correct here? it's more distance plus coverage
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 0, 8, 21, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("arke", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(1, tmpMatches.size());
    // TODO is distance 7 correct here? it's more distance plus coverage
    assertMatchEquals("myId", FoundType.BY_LABEL_TEXT, 2, 7, 21, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Marker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='text'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Marker", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "otherId", "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractMatcherTest#createMatcher(org.wetator.backend.htmlunit.util.HtmlPageIndex,
   *      org.wetator.core.searchpattern.SearchPattern, org.wetator.core.searchpattern.FindSpot,
   *      org.wetator.core.searchpattern.SearchPattern)
   */
  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern) {
    return new ByLabelTextBeforeMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
