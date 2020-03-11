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
public class ByInnerImageMatcherTest extends AbstractMatcherTest {

  @Test
  public void byAltNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byAltFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myAlt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byAltWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myA*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byAltWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Alt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byAltPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yAl");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 2, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byAltEmpty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byAltFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myAlt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 5, 20, tmpMatches.get(0));
  }

  @Test
  public void byAltWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myA*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 5, 20, tmpMatches.get(0));
  }

  @Test
  public void byAltWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Alt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 0, 5, 20, tmpMatches.get(0));
  }

  @Test
  public void byAltPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yAl");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_ALT_ATTRIBUTE, 2, 5, 20, tmpMatches.get(0));
  }

  @Test
  public void byAltFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myAlt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byAltFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' alt='myAlt'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myAlt");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myName");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byNameWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myNa*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byNameWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Name");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yNam");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameEmpty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myName");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byNameWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myNa*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byNameWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Name");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_NAME, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byNamePart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yNam");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myName");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byNameFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' name='myName' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myName");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNameWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.p*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNameWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*cture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNamePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("icture.pn");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameWithPath() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='web/picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 4, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void empty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNameWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > picture.p*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNameWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *cture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byFileNamePart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > icture.pn");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byFileNameFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > picture.png");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byTitleNot() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byTitleFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myTitle");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myTit*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Title");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitlePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("yTitl");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 2, 14, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleEmpty_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > ");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byTitleFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myTitle");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > myTit*");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > *Title");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitlePart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > yTitl");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void byTitleFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='snoopy.php'>"
        + "<img id='otherImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "<p>Some text .... </p>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myTitle");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId", "otherId");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void byTitleFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='snoopy.php'>"
        + "<img id='myImageId' src='picture.png' title='myTitle'>"
        + "</a>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > myTitle");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "myId");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return new ByInnerImageMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
