/*
 * Copyright (c) 2008-2011 www.wetator.org
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
import org.wetator.util.SecretString;

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
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyRadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 12 start: 12",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*RadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 12 start: 12",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("RadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyRadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 18 start: 27",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyRadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byTextAfter() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("RadioButton1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfterWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("u*on1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 6 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfterPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("dioButton1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfter_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("RadioButton1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextAfter_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("RadioButton1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyRadioButtonId1", "MyRadioButtonId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabel_Text() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 0 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelTe*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 2 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdLabelTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 2 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_Text_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 0 distance: 33 start: 57",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_Text_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelChild_Text() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 13 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_TextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelTe*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 15 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_TextPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdLabelTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 15 distance: 27 start: 42",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_Text_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 13 distance: 33 start: 57",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_Text_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyLabelId1", "MyLabelId2");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
