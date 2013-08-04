/*
 * Copyright (c) 2008-2013 wetator.org
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
public class ByLabelAttributeMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("not");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("colors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("colo*");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("*lors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("olor");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 2, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > colors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > colo*");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > *lors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > olor");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("optgroup_colors", FoundType.BY_LABEL_TEXT, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("wrong text, colors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("wrong text, colors");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "optgroup_colors");

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
    return new ByLabelAttributeMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }
}
