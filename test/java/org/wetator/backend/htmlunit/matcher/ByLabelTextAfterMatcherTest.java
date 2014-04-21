/*
 * Copyright (c) 2008-2014 wetator.org
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
public class ByLabelTextAfterMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("not");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("CheckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("CheckB*");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("*eckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("heckBo");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 2, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > CheckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > CheckB*");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > *eckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > heckBo");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("MyCheckboxId", FoundType.BY_LABEL_TEXT, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("wrong text, CheckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("wrong text, CheckBox");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "MyCheckboxId");

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
  protected AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern) {
    return new ByLabelTextAfterMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
