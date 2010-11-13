/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputTextIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputTextIdentifier();
  }

  @Test
  public void byNameExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextInput", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "ti", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextI*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "ti", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_NAME coverage: 4 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameExact_AndPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>abc def</p>"
        + "<input id='id1' name='TextInput1' type='text'>" + "<p>abcdef</p>"
        + "<input id='id2' name='TextInput2' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abc*def", false));
    tmpSearch.add(new SecretString("TextInput1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "id1", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='id1') (name='TextInput1')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "id2", new WPath(tmpSearch));
    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());

    // not found
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abcdef", false));
    tmpSearch.add(new SecretString("TextInput1", false));

    tmpFound = identify(tmpHtmlCode, "id1", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ti", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "ti", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("t*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "ti", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 1 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelWrongId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='inputId' name='TextInput' type='text'>" + "</label>"
        + "abc<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "another", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_TEXT coverage: 3 distance: 8", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "another", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("La*l", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "another", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChildWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='inputId' name='TextInput' type='text'>" + "</label>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("La*l", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "another", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBeforeExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>Line1</td>" + "<td>Input1</td>" + "<td>"
        + "<input id='l1Txt1' name='l1Txt1' type='text' value='l1t1'/>" + "</td>" + "<td>Input2</td>" + "<td>"
        + "<input id='l1Txt2' name='l1Txt2' type='text' value='l1t2'/>" + "</td>" + "</tr>" + "<tr>" + "<td>Line2</td>"
        + "<td>Input1</td>" + "<td>" + "<input id='l2Txt1' name='l2Txt1' type='text' value='l2t1'/>" + "</td>"
        + "<td>Input2</td>" + "<td>" + "<input id='l2Txt2' name='l2Txt2' type='text' value='l2t2'/>" + "</td>"
        + "</tr>" + "</table>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line1", false));
    tmpSearch.add(new SecretString("Input2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "l1Txt2", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt2') (name='l1Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 13", tmpFound
            .getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "l2Txt2", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 43", tmpFound
            .getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "l2Txt1", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='l2Txt1') (name='l2Txt1')] found by: BY_TEXT coverage: 18 distance: 37",
        tmpFound.getEntriesSorted().get(0).toString());

    // ----
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line2", false));
    tmpSearch.add(new SecretString("Input1", false));

    tmpFound = identify(tmpHtmlCode, "l2Txt1", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt1') (name='l2Txt1')] found by: BY_LABEL_TEXT coverage: 0 distance: 1", tmpFound
            .getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "l2Txt2", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_TEXT coverage: 12 distance: 19",
        tmpFound.getEntriesSorted().get(0).toString());

    // ----
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line2", false));
    tmpSearch.add(new SecretString("Input2", false));

    tmpFound = identify(tmpHtmlCode, "l2Txt2", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 13", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "LongTextBefore Label"
        + "<input id='l1Txt1' name='l1Txt1' type='text' value='l1t1'/>" + "SecondLabel"
        + "<input id='l1Txt2' name='l1Txt2' type='text' value='l1t2'/>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("LongTextBefore", false));
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "l1Txt1", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt1') (name='l1Txt1')] found by: BY_LABEL_TEXT coverage: 0 distance: 1", tmpFound
            .getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "l1Txt2", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt2') (name='l1Txt2')] found by: BY_LABEL_TEXT coverage: 6 distance: 16", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdExactMany() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "<form action='test2'>" + "<input id='ti' name='TextInput2' type='text'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ti", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "ti", 0, new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "ti", 1, new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput2')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void bySameNameAndId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='TextInput' name='TextInput' type='text'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextInput", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "TextInput", 0, new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='TextInput') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
