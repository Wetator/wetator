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
import org.rbri.wet.backend.htmlunit.control.ClickableHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.control.DeselectableHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.control.OtherHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.control.SelectableHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.control.SetableHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitFinderDelegatorGetAllOtherControlsTest {

  private HtmlUnitControlRepository controlRepository;

  @Before
  public void setupControlFinder() {
    controlRepository = new HtmlUnitControlRepository();

    controlRepository.add(ClickableHtmlUnitControl.class);
    controlRepository.add(DeselectableHtmlUnitControl.class);
    controlRepository.add(OtherHtmlUnitControl.class);
    controlRepository.add(SelectableHtmlUnitControl.class);
    controlRepository.add(SetableHtmlUnitControl.class);
  }

  @Test
  public void testGetAllOtherControls_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(0, tmpFound.getElementsSorted().size());
  }

  @Test
  public void testGetAllOtherControls_Select_ById() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyId", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MyId') (name='MySelectName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (name='MySecondSelectName')] found by: BY_LABEL_TEXT coverage: 0 distance: 45",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByTextBeforeWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("cond*elText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (name='MySecondSelectName')] found by: BY_LABEL_TEXT coverage: 2 distance: 47",
        tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByTextPathBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Fir", false));
    tmpSearch.add(new SecretString("tSelectLabelText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(2, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (name='MyFirstSelectName')] found by: BY_LABEL_TEXT coverage: 1 distance: 1",
        tmpFound.getElementsSorted().get(0).toString());
    Assert.assertEquals("[HtmlSelect (name='MySecondSelectName')] found by: BY_TEXT coverage: 46 distance: 63",
        tmpFound.getElementsSorted().get(1).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select name='MyFirstSelectName' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyFirstSelectName", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (name='MyFirstSelectName')] found by: BY_NAME coverage: 0 distance: 20", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByLabelText() throws IOException {
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

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MySecondSelectId')] found by: BY_LABEL coverage: 0 distance: 44", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_Select_ByLabelTextChild() throws IOException {
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

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MySecondSelectId')] found by: BY_LABEL coverage: 24 distance: 44", tmpFound
        .getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_OptionGroup_ByLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("colors", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert
        .assertEquals(
            "[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }

  @Test
  public void testGetAllOtherControls_OptionGroup_ById() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("optgroup_colors", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllOtherControls(tmpSearch);

    Assert.assertEquals(1, tmpFound.getElementsSorted().size());
    Assert
        .assertEquals(
            "[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]] found by: BY_ID coverage: 0 distance: 0",
            tmpFound.getElementsSorted().get(0).toString());
  }
}
