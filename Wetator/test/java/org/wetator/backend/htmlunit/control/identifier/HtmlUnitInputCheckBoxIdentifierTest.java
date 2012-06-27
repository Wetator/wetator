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
public class HtmlUnitInputCheckBoxIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputCheckBoxIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckbox*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckbox", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyCheckboxId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyCheckboxId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yCheckboxNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyCheckboxName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyCheckboxName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byTextAfter() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("CheckBox", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfterWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("e*Box", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfterPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("heckBo", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfter_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("CheckBox", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfter_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("CheckBox", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyCheckboxId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabel_Text() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 0 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelTe*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 2 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdLabelTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 2 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_Text_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 0 distance: 30 start: 55",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_Text_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1' for='MyCheckboxId1'>FirstLabelText</label>"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1"
        + "<label id='MyLabelId2' for='MyCheckboxId2'>SecondLabelText</label>"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelChild_Text() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 10 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_TextWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelTe*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 12 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_TextPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdLabelTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 12 distance: 24 start: 40",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_Text_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId2') (name='MyCheckboxIdName')] found by: BY_LABEL coverage: 10 distance: 30 start: 55",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_Text_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyCheckboxId1' name='MyCheckboxIdName' value='value1' type='checkbox'>CheckBox1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyCheckboxId2' name='MyCheckboxIdName' value='value2' type='checkbox'>CheckBox2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
