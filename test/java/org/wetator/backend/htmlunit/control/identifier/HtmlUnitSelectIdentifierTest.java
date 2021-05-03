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


package org.wetator.backend.htmlunit.control.identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.exception.InvalidInputException;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitSelectIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitSelectIdentifier();
  }

  @Test
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='myId'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_htmlLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>LabelText</label>"
        + "<select id='myId'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "labelId"));
  }

  @Test
  public void isHtmlElementSupported_htmlLabel_not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>LabelText</label>"
        + "<input id='myId' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "labelId"));
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelingTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='otherId1' name='otherName1'>"
        + "<option>option</option>"
        + "</select>"
        + "<p>Marker</p>"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "<p>Some text ...</p>"
        + "<select id='otherId2' name='otherName2'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "myId", "otherId1", "otherId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_LABELING_TEXT deviation: 0 distance: 7 start: 13 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Marker</label>"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "labelId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Marker</label>"
        + "<select id='myId' name='myName' style='display: none;'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "labelId");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void byHtmlLabelChild_text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Marker"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "labelId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>5>7 index: 7",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Marker"
        + "<select id='myId' name='myName' style='display: none;'>"
        + "<option>option</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "labelId");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void byLabelingTextBeforeAsText_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='otherId1' name='otherName1'>"
        + "<option>option</option>"
        + "</select>"
        + "<p>Marker</p>"
        + "<select id='myId' name='myName'>"
        + "<option>option</option>"
        + "</select>"
        + "<p>Some text ...</p>"
        + "<select id='otherId2' name='otherName2'>"
        + "<option>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker > ", "myId", "otherId1", "otherId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 13 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byTableCoordinates() throws IOException, InvalidInputException {
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
        + "          <td id='cell_1_2'><select id='myId_1_2'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_1_3'><select id='myId_1_3'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><select id='myId_2_2'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_2_3'><select id='myId_2_3'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2]", "myId_1_2", "myId_1_3", "myId_2_2",
        "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 59 start: 59 hierarchy: 0>1>3>5>22>40>50>51 index: 51",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTable() throws IOException, InvalidInputException {
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
        + "          <td id='cell_1_2'>Marker <select id='myId_1_2'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_1_3'>Marker <select id='myId_1_3'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'>Marker <select id='myId_2_2'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_2_3'>Marker <select id='myId_2_3'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > Marker", "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId_2_3')] found by: BY_LABELING_TEXT deviation: 0 distance: 81 start: 87 hierarchy: 0>1>3>5>22>42>53>55 index: 55",
        tmpEntriesSorted.get(0).toString());
  }
}