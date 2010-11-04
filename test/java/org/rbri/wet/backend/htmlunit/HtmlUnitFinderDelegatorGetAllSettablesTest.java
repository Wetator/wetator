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
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputFile;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputPassword;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputText;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitTextArea;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitFinderDelegatorGetAllSettablesTest {

  private HtmlUnitControlRepository controlRepository;

  @Before
  public void setupControlFinder() {
    controlRepository = new HtmlUnitControlRepository();

    // add the default settables
    controlRepository.add(HtmlUnitInputFile.class);
    controlRepository.add(HtmlUnitInputPassword.class);
    controlRepository.add(HtmlUnitInputText.class);
    controlRepository.add(HtmlUnitTextArea.class);
  }

  @Test
  public void testConstructorNullPage() {
    try {
      new HtmlUnitFinderDelegator(null, controlRepository);
      Assert.fail("NullPointerException expected.");
    } catch (NullPointerException e) {
      Assert.assertEquals("HtmlPage can't be null", e.getMessage());
    }
  }

  @Test
  public void testGetAllSetables_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllSetables_OneByNameExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextInput", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByNameWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextI*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_NAME coverage: 4 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByNameExact_AndPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>abc def</p>"
        + "<input id='id1' name='TextInput1' type='text'>" + "<p>abcdef</p>"
        + "<input id='id2' name='TextInput2' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abc*def", false));
    tmpSearch.add(new SecretString("TextInput1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='id1') (name='TextInput1')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());

    // not found
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abcdef", false));
    tmpSearch.add(new SecretString("TextInput1", false));

    tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllSetables_OneByIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ti", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByIdWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("t*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 1 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_LabelWrongId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllSetables_TextInputByLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_TextInputByLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>Label"
        + "<input id='inputId' name='TextInput' type='text'>" + "</label>"
        + "abc<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_TEXT coverage: 3 distance: 8", tmpFound
            .getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_PasswordInputByLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<input id='inputId' name='PasswordInput' type='password'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='inputId') (name='PasswordInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_PasswordInputByLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>Label"
        + "<input id='inputId' name='PasswordInput' type='password'>" + "</label>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='inputId') (name='PasswordInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_TextAreaByLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<textarea id='inputId' name='TextAreaName' cols='50' rows='1'></textarea>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlTextArea (id='inputId') (name='TextAreaName')] found by: BY_LABEL coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_TextAreaByLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>Label"
        + "<textarea id='inputId' name='TextAreaName' cols='50' rows='1'></textarea>" + "</label>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlTextArea (id='inputId') (name='TextAreaName')] found by: BY_LABEL coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_FileInputByLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<input id='inputId' name='FileInput' type='file'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='inputId') (name='FileInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_FileInputByLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>Label"
        + "<input id='inputId' name='FileInput' type='file'>" + "</label>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='inputId') (name='FileInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByLabelExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_OneByLabelWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label for='inputId'>Label</label>"
        + "<input id='inputId' name='TextInput' type='text'>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("La*l", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_OneByLabelChildWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>Label"
        + "<input id='inputId' name='TextInput' type='text'>" + "</label>"
        + "<input id='another' name='AnotherTextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("La*l", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='inputId') (name='TextInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='another') (name='AnotherTextInput')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_OneByLabelTextBeforeExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>CWID</td>" + "<td>"
        + "<input id='userForm:cwidTxt' name='userForm:cwidTxt' type='text' value=''/>" + "</td>" + "</tr>" + "<tr>"
        + "<td>Passwort</td>" + "<td>"
        + "<input type='password' id='userForm:passwordTxt' name='userForm:passwordTxt'/>" + "</td>" + "</tr>"
        + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Passwort", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlPasswordInput (id='userForm:passwordTxt') (name='userForm:passwordTxt')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByLabelTextBeforeWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>CWID</td>" + "<td>"
        + "<input id='userForm:cwidTxt' name='userForm:cwidTxt' type='text' value=''/>" + "</td>" + "</tr>" + "<tr>"
        + "<td>Passwort</td>" + "<td>"
        + "<input type='password' id='userForm:passwordTxt' name='userForm:passwordTxt'/>" + "</td>" + "</tr>"
        + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Pass*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlPasswordInput (id='userForm:passwordTxt') (name='userForm:passwordTxt')] found by: BY_LABEL_TEXT coverage: 4 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByTextBeforeExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>Line1</td>" + "<td>Input1</td>" + "<td>"
        + "<input id='l1Txt1' name='l1Txt1' type='text' value='l1t1'/>" + "</td>" + "<td>Input2</td>" + "<td>"
        + "<input id='l1Txt2' name='l1Txt2' type='text' value='l1t2'/>" + "</td>" + "</tr>" + "<tr>" + "<td>Line2</td>"
        + "<td>Input1</td>" + "<td>" + "<input id='l2Txt1' name='l2Txt1' type='text' value='l2t1'/>" + "</td>"
        + "<td>Input2</td>" + "<td>" + "<input id='l2Txt2' name='l2Txt2' type='text' value='l2t2'/>" + "</td>"
        + "</tr>" + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line1", false));
    tmpSearch.add(new SecretString("Input2", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(3, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt2') (name='l1Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 13", tmpFound
            .getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 43", tmpFound
            .getEntriesSorted().get(1).toString());
    Assert.assertEquals("[HtmlTextInput (id='l2Txt1') (name='l2Txt1')] found by: BY_TEXT coverage: 18 distance: 37",
        tmpFound.getEntriesSorted().get(2).toString());

    // ----
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line2", false));
    tmpSearch.add(new SecretString("Input1", false));

    tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt1') (name='l2Txt1')] found by: BY_LABEL_TEXT coverage: 0 distance: 1", tmpFound
            .getEntriesSorted().get(0).toString());
    Assert.assertEquals("[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_TEXT coverage: 12 distance: 19",
        tmpFound.getEntriesSorted().get(1).toString());

    // ----
    tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Line2", false));
    tmpSearch.add(new SecretString("Input2", false));

    tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l2Txt2') (name='l2Txt2')] found by: BY_LABEL_TEXT coverage: 0 distance: 13", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_OneByTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "LongTextBefore Label"
        + "<input id='l1Txt1' name='l1Txt1' type='text' value='l1t1'/>" + "SecondLabel"
        + "<input id='l1Txt2' name='l1Txt2' type='text' value='l1t2'/>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("LongTextBefore", false));
    tmpSearch.add(new SecretString("Label", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt1') (name='l1Txt1')] found by: BY_LABEL_TEXT coverage: 0 distance: 1", tmpFound
            .getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlTextInput (id='l1Txt2') (name='l1Txt2')] found by: BY_LABEL_TEXT coverage: 6 distance: 16", tmpFound
            .getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_ManyByIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='ti' name='TextInput' type='text'>"
        + "</form>" + "<form action='test2'>" + "<input id='ti' name='TextInput2' type='text'>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ti", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals("[HtmlTextInput (id='ti') (name='TextInput2')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSetables_SameNameAndId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='TextInput' name='TextInput' type='text'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TextInput", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='TextInput') (name='TextInput')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_SortByDistance() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>Passwort</p>" + "<table>" + "<tr>" + "<td class='name'>CWID</td>"
        + "<td class='value'>" + "<input id='userForm:cwidTxt' name='userForm:cwidTxt' type='text' value=''/>"
        + "</td>" + "</tr>" + "<tr>" + "<td class='name'>Passwort</td>" + "<td class='value'>"
        + "<input type='password' id='userForm:passwordTxt' name='userForm:passwordTxt'/>" + "</td>" + "</tr>"
        + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Passwor*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSettables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlPasswordInput (id='userForm:passwordTxt') (name='userForm:passwordTxt')] found by: BY_LABEL_TEXT coverage: 1 distance: 14",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert
        .assertEquals(
            "[HtmlTextInput (id='userForm:cwidTxt') (name='userForm:cwidTxt')] found by: BY_LABEL_TEXT coverage: 6 distance: 0",
            tmpFound.getEntriesSorted().get(1).toString());
  }
}
