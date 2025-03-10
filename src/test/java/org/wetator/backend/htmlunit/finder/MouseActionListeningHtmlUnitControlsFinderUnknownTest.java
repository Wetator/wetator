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


package org.wetator.backend.htmlunit.finder;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderUnknownTest {

  protected WetatorConfiguration config;
  private HtmlUnitControlRepository repository;

  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, new Properties(), null);
  }

  @Before
  public void createControlRepository() {
    repository = new HtmlUnitControlRepository();
  }

  @Test
  public void noHtml() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "";

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Name", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void noBody() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html>"
        + "<head><title>MyTitle</title></head>"
        + "</html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Name", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Name", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void textInBody_byText_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "not", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void textInBody_byText_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myTe*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 2 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "*Text", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 2 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "yTex", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 1 distance: 1 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "my<b>T</b>ext"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void textInBody_byText_empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > ", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void textInBody_byText_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 6 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myTe*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 2 distance: 6 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > *Text", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 8 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > yTex", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 1 distance: 7 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textInBody_byText_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText "
        + "Some text .... "
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong text > myText", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void textInBody_byText_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "myText"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong text > myText", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byText_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "not", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byText_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText'] found by: BY_TEXT deviation: 0 distance: 15 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myTe*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText'] found by: BY_TEXT deviation: 2 distance: 15 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "*Text", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText'] found by: BY_TEXT deviation: 0 distance: 17 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "yTex", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText'] found by: BY_TEXT deviation: 1 distance: 16 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p>my<b>T</b>ext</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText'] found by: BY_TEXT deviation: 0 distance: 15 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId1'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "<p id='otherId2'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byText_empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId1'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "<p id='otherId2'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > ", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 6 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myText", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 0 distance: 6 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myTe*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 2 distance: 6 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > *Text", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 0 distance: 8 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > yTex", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 1 distance: 7 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong text > myText", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byText_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong text > myText", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byText_many() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>my<b>T</b>ext</p>"
        + "<p>line2</p>"
        + "<p>line3</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText > ine3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'line3'] found by: BY_TEXT deviation: 0 distance: 8 start: 12 hierarchy: 0>1>3>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_many_matchInside() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>line2</p>"
        + "<p>line3</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "line2 li > ne3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'line3'] found by: BY_TEXT deviation: 0 distance: 0 start: 5 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byText_many_matchAcross() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>line2</p>"
        + "<p>line3</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "li > ne2 line3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "not", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byId_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myId", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "my*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "*Id", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "yI", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byId_empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId1'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "<p id='otherId2'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > ", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    // actually found by text and not by id
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 6 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myId", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > my*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > *Id", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byId_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > yI", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byId_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong Text > myId", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byId_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong Text > myId", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myTitle", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "not", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byTitle_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myTitle", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myTit*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "*Title", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "yTitl", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 2 distance: 14 start: 14 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId1' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "<p id='otherId2'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > ", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    // actually found by text and not by id
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TEXT deviation: 6 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myTitle", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > myTit*", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > *Title", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 0 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > yTitl", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'myText' (id='myId')] found by: BY_TITLE_TEXT deviation: 2 distance: 5 start: 21 hierarchy: 0>1>3>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTitle_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='otherId' title='myTitle'>myText</p>"
        + "<p>Some text .... </p>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong Text > myTitle", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byTitle_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId' title='myTitle'>myText</p>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "wrong Text > myTitle", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byTableCoordinates() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span><span>Text_2_3_b</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 8 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_2; row_1]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_1_2'] found by: BY_TEXT deviation: 8 distance: 32 start: 32 hierarchy: 0>1>3>5>22>24>29>30 index: 30",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTableCoordinates_horizontalOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[; row_2]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class org.htmlunit.html.HtmlTableDataCell' (id='cell_2_1')] found by: BY_TEXT deviation: 5 distance: 50 start: 50 hierarchy: 0>1>3>5>22>39>41 index: 41",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[; row_1]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'row_1'] found by: BY_TEXT deviation: 5 distance: 26 start: 26 hierarchy: 0>1>3>5>22>24>26>27 index: 27",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTableCoordinates_horizontalOnly_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[; row_2] > Text_2_2 >", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 8 distance: 0 start: 65 hierarchy: 0>1>3>5>22>39>48>49 index: 49",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[; row_1] > Text_1_3 >", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_byTableCoordinates_verticalOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_1;]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class org.htmlunit.html.HtmlTableHeaderCell' (id='header_1')] found by: BY_TEXT deviation: 8 distance: 0 start: 0 hierarchy: 0>1>3>5>7>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_3;]", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class org.htmlunit.html.HtmlTableHeaderCell' (id='header_3')] found by: BY_TEXT deviation: 8 distance: 17 start: 17 hierarchy: 0>1>3>5>7>9>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_byTableCoordinates_verticalOnly_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'><span>Text_2_1</span></td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_1;] > Text_1_2 >", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_1'] found by: BY_TEXT deviation: 8 distance: 9 start: 50 hierarchy: 0>1>3>5>22>39>41>42 index: 42",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_2;] > row_1 >", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_1_2'] found by: BY_TEXT deviation: 8 distance: 0 start: 32 hierarchy: 0>1>3>5>22>24>30>31 index: 31",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3' (id='id_2_3')] found by: BY_ID deviation: 0 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_2; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byId_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'>Some text .... <span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byId_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > id_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3' (id='id_2_3')] found by: BY_ID deviation: 0 distance: 71 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3' (id='id_2_3')] found by: BY_ID deviation: 0 distance: 71 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byId_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "wrong text > [header_3; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byId_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "wrong text > [header_3; row_2] > id_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_2; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byTitle_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'>Some text .... <span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > title_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 5 start: 80 hierarchy: 0>1>3>5>22>38>47>49 index: 49",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 5 start: 80 hierarchy: 0>1>3>5>22>38>47>49 index: 49",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byTitle_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > title_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 71 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 71 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byTitle_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > title_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "wrong text > [header_3; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byTitle_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > title_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > title_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 66 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "[header_2; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byText_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'>Some text .... <span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > Text_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 6 start: 80 hierarchy: 0>1>3>5>22>38>47>49 index: 49",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 6 start: 80 hierarchy: 0>1>3>5>22>38>47>49 index: 49",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byText_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > Some text > Text_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 72 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 72 start: 80 hierarchy: 0>1>3>8>25>41>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void unknown_inTable_byText_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > Text_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "wrong text > [header_3; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_byText_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "[header_3; row_2] > wrong text > Text_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = find(tmpHtmlCode, "wrong text > [header_3; row_2] > Text_2_3", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void unknown_inTable_empty_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='t1_header_1'>header_1</th>"
        + "          <th id='t1_header_2'>header_2</th>"
        + "          <th id='t1_header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='t1_cell_1_1'>row_1</td>"
        + "          <td id='t1_cell_1_2'><span id='t1_id_1_2'>Text_1_2</span></td>"
        + "          <td id='t1_cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='t1_cell_2_1'>row_2</td>"
        + "          <td id='t1_cell_2_2'><span id='t1_id_2_2'>Text_2_2</span></td>"
        + "          <td id='t1_cell_2_3'><span id='t1_id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='t2_header_1'>header_1</th>"
        + "          <th id='t2_header_2'>header_2</th>"
        + "          <th id='t2_header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='t2_cell_1_1'>row_1</td>"
        + "          <td id='t2_cell_1_2'><span id='t2_id_1_2'>Text_1_2</span></td>"
        + "          <td id='t2_cell_1_3'><span id='t2_id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='t2_cell_2_1'>row_2</td>"
        + "          <td id='t2_cell_2_2'><span id='t2_id_2_2'>Text_2_2</span></td>"
        + "          <td id='t2_cell_2_3'><span id='t2_id_2_3'>Text_2_3</span><span id='t2_id_2_3_b'>Text_2_3_b</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "Some text > [header_3; row_2] >", MouseAction.CLICK);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3' (id='t2_id_2_3')] found by: BY_TEXT deviation: 8 distance: 71 start: 155 hierarchy: 0>1>3>57>74>90>99>100 index: 100",
        tmpEntriesSorted.get(0).toString());

    tmpEntriesSorted = find(tmpHtmlCode, "Wrong text > [header_2; row_2] >", MouseAction.CLICK);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void known() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a>myText</a>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myText", MouseAction.CLICK, HtmlUnitAnchor.class);

    assertEquals(0, tmpEntriesSorted.size());
  }

  @SafeVarargs
  private final List<Entry> find(final String aHtmlCode, final String aWPath, final MouseAction aMouseAction,
      final Class<? extends HtmlUnitBaseControl<?>>... aKnownControls) throws IOException, InvalidInputException {
    final List<Entry> tmpResult = new ArrayList<>();

    PageUtil.consumeHtmlPage(aHtmlCode, tmpHtmlPage -> {
      final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

      if (aKnownControls.length > 0) {
        for (Class<? extends HtmlUnitBaseControl<?>> tmpControl : aKnownControls) {
          repository.add(tmpControl);
        }
      }

      final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
          tmpHtmlPageIndex, null, aMouseAction, repository);
      tmpFinder.setSupportUnknownControlsWithoutListener(true);
      final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(aWPath), config));

      tmpResult.addAll(tmpFound.getEntriesSorted());
    });

    return tmpResult;
  }
}
