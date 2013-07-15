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


package org.wetator.backend.htmlunit.control.identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class HtmlUnitInputButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputButtonIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("my*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='button' value='ClickMeAlso'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), 0, "myId");
    tmpFound.addAll(identify(tmpHtmlCode, new WPath(tmpSearch), 1, "myId"));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 14", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5 start: 14",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byText() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Click*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byText_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5 start: 14", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byText_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdNameText() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='button' name='MyName' value='MyName'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyName", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTablePlain() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTableNestedX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'><table><tr><td id='header_i_3'>header_3</td></tr></table></th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTableNestedY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'><table><tr><td id='cell_i_2_1'>row_2</td></tr></table></td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTableNestedCell() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTableNestedCellMultiple() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3_1'><input type='button' id='InputButton_2_3_1' value='Click'/></td><td id='cell_i_2_3_2'><input type='button' id='InputButton_2_3_2' value='Click'/></td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3_1", "InputButton_2_3_2");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButtonInput 'Click' (id='InputButton_2_3_1')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert
        .assertEquals(
            "[HtmlButtonInput 'Click' (id='InputButton_2_3_2')] found by: BY_LABEL_TEXT coverage: 0 distance: 62 start: 62",
            tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void inTableNestedTable() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inTableDifferentTableX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[cell_o_1_2; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_2')] found by: BY_LABEL_TEXT coverage: 0 distance: 83 start: 83",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 89 start: 89",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void inTableDifferentTableY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; cell_o_2_1]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_1_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 71 start: 71",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 89 start: 89",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void inTableOnlyX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_1_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 38 start: 38",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void inTableOnlyXWithPath() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("row_2", false));
    tmpSearch.add(new SecretString("[header_3]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTableOnlyY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_2')] found by: BY_LABEL_TEXT coverage: 0 distance: 50 start: 50",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 56 start: 56",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void inMultipleTable() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='button' id='InputButton_1_2' value='Click'/></td>" //
        + "          <td id='cell_1_3'><input type='button' id='InputButton_1_3' value='Click'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='button' id='InputButton_2_2' value='Click'/></td>" //
        + "          <td id='cell_2_3'><input type='button' id='InputButton_2_3' value='Click'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[cell_o_1_2; cell_o_2_1]", false));
    tmpSearch.add(new SecretString("[header_3; row_2]", false));
    tmpSearch.add(new SecretString("Click", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "InputButton_1_2", "InputButton_1_3",
        "InputButton_2_2", "InputButton_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'Click' (id='InputButton_2_3')] found by: BY_LABEL_TEXT coverage: 0 distance: 89 start: 89",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
