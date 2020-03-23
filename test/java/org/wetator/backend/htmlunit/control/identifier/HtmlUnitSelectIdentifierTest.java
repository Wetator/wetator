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
        + "<select id='myId' size='2'>"
        + "<option value='o_red'>red</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' value='value' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_HtmlLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>LabelText</label>"
        + "<select id='myId' size='2'>"
        + "<option value='o_red'>red</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "labelId"));
  }

  @Test
  public void isHtmlElementSupported_HtmlLabel_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>LabelText</label>"
        + "<input id='myId' value='value' type='text'>"
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
        + "<select id='myId' name='myName' size='2'>"
        + "<option id='optionId' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
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
        + "FirstSelectLabelText"
        + "<select id='myId1' name='myName1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='myId2' name='myName2' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName1", "myId1", "myId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId1') (name='myName1')] found by: BY_NAME deviation: 0 distance: 20 start: 20 hierarchy: 0>1>3>4>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelingTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='myId1' name='myName1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='myId2' name='myName2' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectLabelText", "myId1", "myId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId2') (name='myName2')] found by: BY_LABELING_TEXT deviation: 0 distance: 45 start: 66 hierarchy: 0>1>3>4>14 index: 14",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1' for='myId1'>FirstSelectLabelText</label>"
        + "<select id='myId1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId2' for='myId2'>SecondSelectLabelText</label>"
        + "<select id='myId2' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId2')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 44 start: 66 hierarchy: 0>1>3>4>16 index: 16",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1' for='myId1'>FirstSelectLabelText</label>"
        + "<select id='myId1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId2' for='myId2'>SecondSelectLabelText</label>"
        + "<select id='myId2' size='2' style='display: none;'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectLabelText", "labelId1", "labelId2");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1'>FirstSelectLabelText"
        + "<select id='myId1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "<label id='labelId2'>SecondSelectLabelText"
        + "<select id='myId2' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId2')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 44 start: 66 hierarchy: 0>1>3>4>14>16 index: 16",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1'>FirstSelectLabelText"
        + "<select id='myId1' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "<label id='labelId2'>SecondSelectLabelText"
        + "<select id='myId2' size='2' style='display: none;'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectLabelText", "labelId1", "labelId2");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' type='submit'>"
        + "<p>Some text ...</p>"
        + "<select id='myId' name='myName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_TEXT deviation: 14 distance: 20 start: 20 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<select id='myId' name='myName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<p>Some text ...</p>"
        + "<select id='otherId' name='otherName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker > ", "myId", "otherId");

    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSelect (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlSelect (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 38 start: 44 hierarchy: 0>1>3>4>16 index: 16",
        tmpEntriesSorted.get(1).toString());
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
        + "          <td id='cell_1_2'><select id='myId_1_2' size='2'>"
        + "            <option id='optionId' value='o_value1'>option1</option>"
        + "            <option value='o_value2'>option2</option>"
        + "            <option value='o_value3'>option3</option>"
        + "          </select></td>"
        + "          <td id='cell_1_3'><select id='myId_1_3' size='2'>"
        + "            <option id='optionId' value='o_value1'>option1</option>"
        + "            <option value='o_value2'>option2</option>"
        + "            <option value='o_value3'>option3</option>"
        + "          </select></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><select id='myId_2_2' size='2'>"
        + "            <option id='optionId' value='o_value1'>option1</option>"
        + "            <option value='o_value2'>option2</option>"
        + "            <option value='o_value3'>option3</option>"
        + "          </select></td>"
        + "          <td id='cell_2_3'><select id='myId_2_3' size='2'>"
        + "            <option id='optionId' value='o_value1'>option1</option>"
        + "            <option value='o_value2'>option2</option>"
        + "            <option value='o_value3'>option3</option>"
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
        "[HtmlSelect (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 110 start: 110 hierarchy: 0>1>3>5>22>48>62>63 index: 63",
        tmpEntriesSorted.get(0).toString());
  }
}