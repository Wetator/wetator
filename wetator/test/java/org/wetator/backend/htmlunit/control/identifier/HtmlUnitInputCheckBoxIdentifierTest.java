/*
 * Copyright (c) 2008-2016 wetator.org
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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("CheckBox");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 0 distance: 24 start: 40 index: 11",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 10 distance: 24 start: 40 index: 11",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId_2_3')] found by: BY_TABLE_COORDINATE coverage: 0 distance: 38 start: 38 index: 45",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
