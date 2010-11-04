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
import org.junit.Before;
import org.junit.Test;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitAnchor;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitImage;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputImage;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputReset;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitFinderDelegatorGetAllClickablesTest {

  private HtmlUnitControlRepository controlRepository;

  @Before
  public void setupControlFinder() {
    controlRepository = new HtmlUnitControlRepository();

    // add the default clickables
    controlRepository.add(HtmlUnitAnchor.class);
    controlRepository.add(HtmlUnitButton.class);
    controlRepository.add(HtmlUnitImage.class);
    controlRepository.add(HtmlUnitInputButton.class);
    controlRepository.add(HtmlUnitInputImage.class);
    controlRepository.add(HtmlUnitInputReset.class);
    controlRepository.add(HtmlUnitInputSubmit.class);
  }

  @Test
  public void empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void hidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe' style='visibility: hidden;'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='submit' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_Label() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Label_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Label_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_LabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void submitInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void submitInput_IdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='submit' name='MyName' value='MyName'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='reset' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void resetInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void resetInput_Label() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Label_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Label_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void resetInput_LabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='reset' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlResetInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlResetInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlResetInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void resetInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void resetInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='reset' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void resetInput_IdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='reset' name='MyName' value='MyName'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlResetInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "<p>Marker</p>" + "<input id='myId' type='button' value='ClickMeAlso'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMeAlso' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void buttonInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void buttonInput_Label() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Label_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Label_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ClickMe", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void buttonInput_LabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='button' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("lickM", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButtonInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void buttonInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void buttonInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='button' name='MyName' value='ClickMe'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void buttonInput_IdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='button' name='MyName' value='MyName'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButtonInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Alt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Alt_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_AltPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yAl", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 2 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_TitlePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yTitl", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='web/picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='web/picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_FileNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("icture.pn", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void imageInput_IdNameAltTitleFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='image' name='MyName' src='MyName' alt='MyName' title='MyName'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='MyName') (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithAnotherText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Sarker", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void button_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void button_Label() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Label_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Label_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void button_LabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("uttonWithTex", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void button_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void button_IdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='MyName' type='button' name='MyName'>"
        + "<p>MyName</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlButton 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void button_Image_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void button_Image_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void button_Image_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void button_Image_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void button_Image_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals("[HtmlAnchor 'TestAnchor' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void anchor_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void anchor_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void anchor_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void anchor_Text() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TestAnchor", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Text_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("TestAnchor", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Text_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("TestAnchor", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void anchor_FormatedText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a>My<b>T</b>ext</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlAnchor 'MyText'] found by: BY_LABEL_TEXT coverage: 0 distance: 0", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_TextPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("estAncho", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_IdNameText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myAnchor' name='myAnchor' href='snoopy.php'>myAnchor</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myAnchor", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'myAnchor' (id='myAnchor') (name='myAnchor')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getElementsSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_Alt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>" + "<img src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_Title_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='web/picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_FileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
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
  public void anchor_Image_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void anchor_Image_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllClickables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 5",
            tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlImage 'picture.png' (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 5",
        tmpFound.getElementsSorted().get(1).toString());
  }
}
