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
public class HtmlUnitInputRadioButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputRadioButtonIdentifier();
  }

  @Test
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' value='value' type='radio'>RadioButton"
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
        + "<input id='myId' name='myName' value='value' type='radio'>RadioButton"
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
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "<input id='myId2' name='myName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId2", "myId1", "myId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] found by: BY_ID deviation: 0 distance: 12 start: 12 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelingTextAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "<input id='myId2' name='myName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "RadioButton1", "myId1", "myId2");

    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value1' (id='myId1') (name='myName')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] found by: BY_TEXT deviation: 0 distance: 12 start: 12 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1' for='myId1'>FirstLabelText</label>"
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "<label id='labelId2' for='myId2'>SecondLabelText</label>"
        + "<input id='myId2' name='myName' value='value2' type='radio'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 27 start: 43 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1' for='myId1'>FirstLabelText</label>"
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "<label id='labelId2' for='myId2'>SecondLabelText</label>"
        + "<input id='myId2' name='myName' value='value2' type='radio' style='display: none;'>RadioButton2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] by [HtmlLabel 'SecondLabelText' (id='labelId2') (for='myId2')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 27 start: 43 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1'>FirstLabelText"
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "</label>"
        + "<label id='labelId2'>SecondLabelText"
        + "<input id='myId2' name='myName' value='value2' type='radio'>RadioButton2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] found by: BY_LABEL_ELEMENT deviation: 13 distance: 27 start: 43 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId1'>FirstLabelText"
        + "<input id='myId1' name='myName' value='value1' type='radio'>RadioButton1"
        + "</label>"
        + "<label id='labelId2'>SecondLabelText"
        + "<input id='myId2' name='myName' value='value2' type='radio' style='display: none;'>RadioButton2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondLabelText", "labelId1", "labelId2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value2' (id='myId2') (name='myName')] by [HtmlLabel 'SecondLabelTextuncheckedRadioButton2' (id='labelId2')] found by: BY_LABEL_ELEMENT deviation: 12 distance: 27 start: 43 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' value='otherValue' type='submit'>"
        + "<p>Some text ...</p>"
        + "<input id='myId' name='myName' value='myValue' type='radio'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'myValue' (id='myId') (name='myName')] found by: BY_TEXT deviation: 25 distance: 31 start: 31 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' value='myValue' type='radio'>"
        + "<p>Some text ...</p>"
        + "<input id='otherId' name='otherName' value='otherValue' type='radio'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker > ", "myId", "otherId");

    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'myValue' (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlRadioButtonInput 'otherValue' (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 hierarchy: 0>1>3>4>10 index: 10",
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
        + "          <td id='cell_1_2'><input id='myId_1_2' name='myName_1_2' value='value_1_2' type='radio'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' name='myName_1_3' value='value_1_3' type='radio'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' name='myName_2_2' value='value_2_2' type='radio'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' name='myName_2_3' value='value_2_3' type='radio'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2]", "myId_1_2", "myId_1_3", "myId_2_2",
        "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlRadioButtonInput 'value_2_3' (id='myId_2_3') (name='myName_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }
}
