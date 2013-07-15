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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitControlFinderGetAllSelectablesTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(HtmlUnitControlFinderGetAllSelectablesTest.class);
    }

    public void testGetAllSetables_Empty() throws IOException {
        String tmpHtmlCode = "<html><body>" + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("Name", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSetables(tmpSearch);

        assertEquals(0, tmpFound.getElementsSorted().size());
    }


    public void testGetAllSelectables_Option_OneByIdExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select id='MyId' name='MySelectName' size='2'>"
            + "<option id='MyOptionId' value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyOptionId", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1' (id='MyOptionId')] found by: BY_ID coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByTextExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("option1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByTextWildcard() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("pt*n1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 1 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByLabelExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option label='MyLabel' value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyLabel", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByLabelWildcard() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option label='MyLabel' value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("y*el", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 1 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByValueExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option label='MyLabel' value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("o_value1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Option_OneByValueWildcard() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<select name='MySelectName' size='2'>"
            + "<option label='MyLabel' value='o_value1'>option1</option>"
            + "<option value='o_value2'>option2</option>"
            + "<option value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("_*e1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1'] found by: BY_LABEL coverage: 1 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Select_ByTextBefore() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "FirstSelectLabelText"
            + "<select name='MyFirstSelectName' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "SecondSelectLabelText"
            + "<select name='MySecondSelectName' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("SecondSelectLabel", false));
        tmpSearch.add(new SecretString("option2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option2' (id='2_2')] found by: BY_LABEL coverage: 0 distance: 4", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Select_ByName() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "FirstSelectLabelText"
            + "<select name='MyFirstSelectName' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "SecondSelectLabelText"
            + "<select name='MySecondSelectName' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyFirstSelectName", false));
        tmpSearch.add(new SecretString("tion3", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option3' (id='1_3')] found by: BY_LABEL coverage: 2 distance: 20", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Select_ById() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "FirstSelectLabelText"
            + "<select id='MyFirstSelectId' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "SecondSelectLabelText"
            + "<select id='MySecondSelectId' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MySecondSelectId", false));
        tmpSearch.add(new SecretString("o_value3", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option3' (id='2_3')] found by: BY_LABEL coverage: 0 distance: 66", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Select_ByLabelText() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<label for='MyFirstSelectId'>FirstSelectLabelText</label>"
            + "<select id='MyFirstSelectId' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "<label for='MySecondSelectId'>SecondSelectLabelText</label>"
            + "<select id='MySecondSelectId' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("SecondSelectLabelText", false));
        tmpSearch.add(new SecretString("o_value1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1' (id='2_1')] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetAllSelectables_Select_ByLabelTextChild() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<label>FirstSelectLabelText"
            + "<select id='MyFirstSelectId' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</label>"
            + "<label>SecondSelectLabelText"
            + "<select id='MySecondSelectId' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</label>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("SecondSelectLabelText", false));
        tmpSearch.add(new SecretString("o_value1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option1' (id='2_1')] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetAllSelectables_Select_ByLabelText_TextBefore() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<label for='MyFirstSelectId'>FirstSelectLabelText</label>"
            + "<select id='MyFirstSelectId' size='2'>"
            + "<option id='1_1' value='o_value1'>option1</option>"
            + "<option id='1_2' value='o_value2'>option2</option>"
            + "<option id='1_3' value='o_value3'>option3</option>"
            + "</select>"
            + "<p>before</p>"
            + "<label for='MySecondSelectId'>SecondSelectLabelText</label>"
            + "<select id='MySecondSelectId' size='2'>"
            + "<option id='2_1' value='o_value1'>option1</option>"
            + "<option id='2_2' value='o_value2'>option2</option>"
            + "<option id='2_3' value='o_value3'>option3</option>"
            + "</select>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("before", false));
        tmpSearch.add(new SecretString("SelectLabelText", false));
        tmpSearch.add(new SecretString("option2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlOption 'option2' (id='2_2')] found by: BY_LABEL coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetAllSelectables_Checkbox_OneByIdExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyCheckboxId", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Checkbox_OneByTextExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("CheckBox", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Checkbox_OneByTextWildcard() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("e*Box", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_Checkbox_OneByName() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyCheckboxName", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetAllSelectables_RadioButton_OneByIdExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
            + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("MyRadioButtonId2", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 12", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_RadioButton_OneByTextExact() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
            + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("RadioButton1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }

    public void testGetAllSelectables_RadioButton_OneByTextWildcard() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
            + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("u*on1", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(1, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 6 distance: 0", tmpFound.getElementsSorted().get(0).toString());
    }


    public void testGetAllSelectables_RadioButton_ByLabelText() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<label for='MyRadioButtonId1'>FirstLabelText</label>"
            + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
            + "<label for='MyRadioButtonId2'>SecondLabelText</label>"
            + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("SecondLabelText", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(2, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 0 distance: 27", tmpFound.getElementsSorted().get(0).toString());
        assertEquals("[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14", tmpFound.getElementsSorted().get(1).toString());
    }


    public void testGetAllSelectables_RadioButton_ByLabelTextChild() throws IOException {
        String tmpHtmlCode =
            "<html><body>"
            + "<form action='test'>"
            + "<label>FirstLabelText"
            + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
            + "</label>"
            + "<label>SecondLabelText"
            + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2"
            + "</label>"
            + "</form>"
            + "</body></html>";
        HtmlPage tmpHtmlPage = PageUtil.constructPage(tmpHtmlCode);

        List<SecretString> tmpSearch = new ArrayList<SecretString>();
        tmpSearch.add(new SecretString("SecondLabelText", false));

        HtmlUnitControlFinder tmpFinder = new HtmlUnitControlFinder(tmpHtmlPage);
        WeightedControlList tmpFound = tmpFinder.getAllSelectables(tmpSearch);

        assertEquals(2, tmpFound.getElementsSorted().size());
        assertEquals("[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 13 distance: 27", tmpFound.getElementsSorted().get(0).toString());
        assertEquals("[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14", tmpFound.getElementsSorted().get(1).toString());
    }
}
