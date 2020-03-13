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

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputCheckBoxIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputCheckBoxIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyCheckboxId");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyCheckboxName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' title='MyCheckboxTitle' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyCheckboxTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelTextAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId1' name='MyCheckboxName1' value='value1' type='checkbox'>CheckBox1"
        + "<input id='MyCheckboxId2' name='MyCheckboxName2' value='value1' type='checkbox'>CheckBox2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("CheckBox1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId1",
        "MyCheckboxId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId1') (name='MyCheckboxName1')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxName2')] found by: BY_TEXT deviation: 0 distance: 9 start: 9 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 24 start: 40 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox' style='display: none;'>CheckBox2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] by [HtmlLabel 'SecondLabelText' (id='MyLabelId2') (for='MyCheckboxId2')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 24 start: 40 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL_ELEMENT deviation: 10 distance: 24 start: 40 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_Invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox' style='display: none;'>CheckBox2"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondLabelText");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyLabelId1",
        "MyLabelId2");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] by [HtmlLabel 'SecondLabelTextuncheckedCheckBox2' (id='MyLabelId2')] found by: BY_LABEL_ELEMENT deviation: 9 distance: 24 start: 40 hierarchy: 0>1>3>4>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' type='submit'>"
        + "<p>Some text ...</p>"
        + "<input id='myId' name='myName' type='checkbox'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='myId') (name='myName')] found by: BY_TEXT deviation: 14 distance: 20 start: 20 hierarchy: 0>1>3>4>10 index: 10",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='checkbox'>"
        + "<p>Some text ...</p>"
        + "<input id='otherId' name='otherName' type='checkbox'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId", "otherId");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlCheckBoxInput (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 hierarchy: 0>1>3>4>10 index: 10",
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
        + "          <td id='cell_1_2'><input id='MyCheckboxId_1_2' value='value_1_2' type='checkbox'></td>"
        + "          <td id='cell_1_3'><input id='MyCheckboxId_1_3' value='value_1_3' type='checkbox'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='MyCheckboxId_2_2' value='value_2_2' type='checkbox'></td>"
        + "          <td id='cell_2_3'><input id='MyCheckboxId_2_3' value='value_2_3' type='checkbox'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId_1_2",
        "MyCheckboxId_1_3", "MyCheckboxId_2_2", "MyCheckboxId_2_3");

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }
}
