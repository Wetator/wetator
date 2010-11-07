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
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.util.SecretString;

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
  public void buttonInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='button' value='ClickMeAlso'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", 1, tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void buttonInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void buttonInput_Label() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Label_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Label_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void buttonInput_LabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void buttonInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void buttonInput_IdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='button' name='MyName' value='MyName'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyName", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

}
