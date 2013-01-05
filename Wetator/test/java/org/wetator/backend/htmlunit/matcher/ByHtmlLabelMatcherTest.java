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

import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author frank.danek
 */
public class ByHtmlLabelMatcherTest extends AbstractMatcherTest {

  @Test
  public void not() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("not", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Lab*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*bel", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void part() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abe", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void full_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Lab*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void wildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("*bel", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void part_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("abe", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void full_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void full_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childNot() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("not", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childFull() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void childWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Lab*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void childWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*bel", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void childPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abe", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 0, 0, tmpMatches.get(0));
  }

  @Test
  public void childFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void childWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Lab*", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void childWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("*bel", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 0, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void childPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("abe", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(1, tmpMatches.size());
    assertMatchEquals("myId", FoundType.BY_LABEL, 2, 5, 14, tmpMatches.get(0));
  }

  @Test
  public void childFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void childFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='text'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "labelId");

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
    return new ByHtmlLabelMatcher(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, HtmlTextInput.class);
  }
}
