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
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitOption;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitFinderDelegatorGetAllSelectablesTest {

  private HtmlUnitControlRepository controlRepository;

  @Before
  public void setupControlFinder() {
    controlRepository = new HtmlUnitControlRepository();

    // add the default selectables
    controlRepository.add(HtmlUnitInputCheckBox.class);
    controlRepository.add(HtmlUnitInputRadioButton.class);
    controlRepository.add(HtmlUnitOption.class);
  }

  @Test
  public void testGetAllSelectables_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllSelectables_Option_OneByIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyOptionId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='MyOptionId') part of [HtmlSelect (id='MyId') (name='MySelectName')]] found by: BY_ID coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("option1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("pt*n1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByLabelExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyLabel", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByLabelWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("y*el", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByValueExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("o_value1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Option_OneByValueWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("_*e1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ByTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabel", false));
    tmpSearch.add(new SecretString("option2", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ByName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyFirstSelectName", false));
    tmpSearch.add(new SecretString("tion3", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='1_3') part of [HtmlSelect (name='MyFirstSelectName')]] found by: BY_LABEL coverage: 2 distance: 20",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ById() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ByLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label for='MyFirstSelectId'>FirstSelectLabelText</label>" + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>"
        + "<label for='MySecondSelectId'>SecondSelectLabelText</label>" + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='2_1') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ByLabelTextChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "</label>" + "<label>SecondSelectLabelText" + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</label>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='2_1') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Select_ByLabelText_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label for='MyFirstSelectId'>FirstSelectLabelText</label>" + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "<p>before</p>"
        + "<label for='MySecondSelectId'>SecondSelectLabelText</label>" + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("before", false));
    tmpSearch.add(new SecretString("SelectLabelText", false));
    tmpSearch.add(new SecretString("option2", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Checkbox_OneByIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Checkbox_OneByTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("CheckBox", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Checkbox_OneByTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("e*Box", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_Checkbox_OneByName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_RadioButton_OneByIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyRadioButtonId2", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 12",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_RadioButton_OneByTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("RadioButton1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_RadioButton_OneByTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("u*on1", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 6 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSelectables_RadioButton_ByLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 0 distance: 27",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14",
            tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllSelectables_RadioButton_ByLabelTextChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 13 distance: 27",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14",
            tmpFound.getEntriesSorted().get(1).toString());
  }
}
