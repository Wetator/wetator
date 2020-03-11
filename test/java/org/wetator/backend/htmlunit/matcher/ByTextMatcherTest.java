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
public class ByTextMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myText");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myTe*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Text");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yTex");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>my<b>T</b>ext</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myText");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void empty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myText");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myTe*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 21, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Text");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 21, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yTex");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 5, 21, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>myText</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myText");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='snoopy.php'>myText</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myText");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByTextMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
