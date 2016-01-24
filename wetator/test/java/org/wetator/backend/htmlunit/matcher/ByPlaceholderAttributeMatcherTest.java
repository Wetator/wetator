/*
 * Copyright (c) 2008-2016 wetator.org
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
import java.util.List;

import org.junit.Assert;
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
public class ByPlaceholderAttributeMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
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
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("my placeholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("my pl*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*holder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("place");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 9, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > my placeholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > my place*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *eholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ace");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_PLACEHOLDER, 11, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > my placeholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > my placeholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void valueHidesPlacehoder() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='text' value='my value' placeholder='my placeholder'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("my placeholder");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.wetator.backend.htmlunit.matcher.AbstractMatcherTest#createMatcher(org.wetator.backend.htmlunit.util.HtmlPageIndex,
   *      org.wetator.core.searchpattern.SearchPattern, org.wetator.util.FindSpot,
   *      org.wetator.core.searchpattern.SearchPattern)
   */
  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByPlaceholderAttributeMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
