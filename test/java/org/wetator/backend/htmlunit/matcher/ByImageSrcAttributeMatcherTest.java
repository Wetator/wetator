/*
 * Copyright (c) 2008-2021 wetator.org
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
public class ByImageSrcAttributeMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
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
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.p*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*cture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("icture.pn");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void withPath() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='web/picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 4, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
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
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > picture.p*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *ture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > icture.pn");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='otherId' src='picture.png'>"
        + "<p>Some text .... </p>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<img id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inputImage() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input type='image' id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    // FIXME [HTMLUNIT] in contrast to a 'normal' image the input image returns the FQDN as src
    // -> clarify with HtmlUnit if this is an intended behavior
    assertMatchEquals("myId", FoundType.BY_IMG_SRC_ATTRIBUTE, 23, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void otherControl() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<input type='text' id='myId' src='picture.png'>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByImageSrcAttributeMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
