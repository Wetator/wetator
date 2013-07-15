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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitControlFinderGetAllClickablesTest {

  @Test
  public void testGetAllClickables_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_Hidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe' style='visibility: hidden;'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_SubmitInputId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='submit' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_SubmitInputLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_SubmitInputName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_SubmitInputNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ResetInputId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='reset' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ResetInputLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlResetInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlResetInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ResetInputName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ResetInputNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ButtonInputId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='button' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ButtonInputLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonInputName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ButtonInputNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ImageId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageAlt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageTitle_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='web/picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='web/picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ImageFileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithAnotherText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_ButtonLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllClickables_ButtonLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("tonWithT", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 6 distance: 14",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_Button_ImageName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Button_ImageAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png'] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Button_ImageTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png'] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Button_ImageFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png'] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Button_ImageFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: web/picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'web/picture.png'] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_AnchorId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlAnchor 'TestAnchor' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_AnchorText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TestAnchor", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_AnchorText_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("TestAnchor", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_AnchorName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_AnchorName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_AnchorAlt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>" + "<img src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageTitle_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='web/picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: web/picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'web/picture.png' (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageFileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllClickables_Anchor_ImageName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }
}
