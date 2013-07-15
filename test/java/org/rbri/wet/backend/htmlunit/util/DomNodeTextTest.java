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


package org.rbri.wet.backend.htmlunit.util;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class DomNodeTextTest extends TestCase {

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(DomNodeTextTest.class);
  }

  public void testAsText_EmptyPage() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("", tmpResult.getText());
  }

  public void testAsText_SimplePage() throws IOException {
    String tmpHtmlCode = "<html>" + "<head>" + "<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
        + "<title>Page Title</title>" + "</head>" + "<body>" + "<p>Paragraph 1</p>" + "<p>Paragraph 2</p>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "Paragraph 1 Paragraph 2";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_PageHeader() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>Paragraph 1</p>" + "<p>Paragraph 2</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "Paragraph 1 Paragraph 2";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_AllControls() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>PageStart</p>" + "<form action='test'>" + "<p> </p>" + "<fieldset>"
        + "<legend id='idLegend'>LegendLabel</legend>" + "</fieldset>" + "<p> </p>"
        + "<label id='idLabel' for='TextInput'>LabelLabel</label>" + "<p> </p>"
        + "<input id='idTextInput' name='TextInput' type='text' value='inputValue'>" + "<p> </p>"
        + "<input name='PasswordInput' type='password' value='secretInputValue'>" + "<p> </p>"
        + "<input name='HiddenInput' type='hidden' value='hiddenInputValue'>" + "<p> </p>"
        + "<textarea name='TextArea'>textAreaValue</textarea>" + "<p> </p>" + "<input name='FileInput' type='file'>"
        + "<p> </p>" + "<select id='idSingleSelect' name='SingleSelect'>" + "<option selected>Option1Value"
        + "<option>Option2Value" + "</select>" + "<p> </p>" + "<select name='MultipleSelect' multiple>"
        + "  <option selected>Option1Value" + "  <option>Option2Value" + "  <option selected>Option3Value"
        + "</select>" + "<p> </p>" + "<select name='SingleOptgroupSelect'>"
        + "  <optgroup label='SingleOptgroupLabel1'>" + "    <option>SingleOptgroup1Option1Value"
        + "    <option>SingleOptgroup1Option2Value" + "    <option>SingleOptgroup1Option3Value" + "  </optgroup>"
        + "  <optgroup label='SingleOptgroupLabel2'>" + "    <option>SingleOptgroup2Option1Value"
        + "    <option selected>SingleOptgroup2Option2Value" + "    <option>SingleOptgroup2Option3Value"
        + "  </optgroup>" + "</select>" + "<p> </p>" + "<select name='MultipleOptgroupSelect' multiple>"
        + "  <optgroup label='MultipleOptgroupLabel1'>" + "    <option selected>MultipleOptgroup1Option1Value"
        + "    <option>MultipleOptgroup1Option2Value" + "    <option selected>MultipleOptgroup1Option3Value"
        + "  </optgroup>" + "  <optgroup label='MultipleOptgroupLabel2'>" + "    <option>MultipleOptgroup2Option1Value"
        + "    <option selected>MultipleOptgroup2Option2Value" + "    <option>MultipleOptgroup2Option3Value"
        + "  </optgroup>" + "</select>" + "<p> </p>"
        + "<input name='RadioInput' type='radio' value='radioInputValue1'>radioInputLabel1"
        + "<input name='RadioInput' type='radio' value='radioInputValue2' checked>radioInputLabel2" + "<p> </p>"
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue1' checked>checkboxInputLabel1"
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue2'>checkboxInputLabel2" + "<p> </p>"
        + "<button name='ButtonButton' type='button' value='buttonButtonValue'>buttonButtonLabel</button>" + "<p> </p>"
        + "<input name='ButtonInput' type='button' value='buttonInputValue'>" + "<p> </p>"
        + "<input name='SubmitInput' type='submit' value='submitInputValue'>" + "<p> </p>"
        + "<input name='ResetInput' type='reset' value='resetInputValue'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("PageStart " + "LegendLabel " + "LabelLabel " + "inputValue " + "secretInputValue " + "textAreaValue "
        + "Option1Value " + "Option2Value " + "Option1Value " + "Option2Value " + "Option3Value "
        + "SingleOptgroupLabel1 " + "SingleOptgroup1Option1Value " + "SingleOptgroup1Option2Value "
        + "SingleOptgroup1Option3Value " + "SingleOptgroupLabel2 " + "SingleOptgroup2Option1Value "
        + "SingleOptgroup2Option2Value " + "SingleOptgroup2Option3Value " + "MultipleOptgroupLabel1 "
        + "MultipleOptgroup1Option1Value " + "MultipleOptgroup1Option2Value " + "MultipleOptgroup1Option3Value "
        + "MultipleOptgroupLabel2 " + "MultipleOptgroup2Option1Value " + "MultipleOptgroup2Option2Value "
        + "MultipleOptgroup2Option3Value " + "radioInputLabel1 radioInputLabel2 "
        + "checkboxInputLabel1 checkboxInputLabel2 " + "buttonButtonLabel " + "buttonInputValue " + "submitInputValue "
        + "resetInputValue", tmpResult.getText());

    assertEquals("PageStart", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLegend")));
    assertEquals("PageStart LegendLabel", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLabel")));

    assertEquals("inputValue", tmpResult.getAsText(tmpHtmlPage.getElementById("idTextInput")));
    assertEquals("Option1Value Option2Value", tmpResult.getAsText(tmpHtmlPage.getElementById("idSingleSelect")));

  }

  public void testAsText_Heading() throws IOException {
    String tmpHtmlCode = "<html><body>before" + "<h1>Heading1</h1>" + "<h2>Heading2</h2>" + "<h3>Heading3</h3>"
        + "<h4>Heading4</h4>" + "<h5>Heading5</h5>" + "<h6>Heading6</h6>" + "after</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("before Heading1 Heading2 Heading3 Heading4 Heading5 Heading6 after", tmpResult.getText());
  }

  public void testAsText_Table() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table id='idTable'>" + "<tr id='idTr1'>"
        + "  <th id='idTh1'>header1</th><th id='idTh2'>header2</th>" + "</tr>" + "<tr id='idTr2'>"
        + "  <td id='idTd1'>data1</td><td id='idTd2'>data2</td>" + "</tr>" + "<tr id='idTr3'>"
        + "  <td id='idTd3'>data3</td><td id='idTd4'>data4</td>" + "</tr>" + "<tr id='idTr4'>"
        + "  <td colspan='2' id='idTd5'>data5</td>" + "</tr>" + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("header1 header2 " + "data1 data2 " + "data3 data4 " + "data5", tmpResult.getText());

    assertEquals("header1 header2 data1 data2 data3 data4 data5", tmpResult.getAsText(tmpHtmlPage
        .getElementById("idTable")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTable")));

    assertEquals("header1 header2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr1")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr1")));

    assertEquals("header1", tmpResult.getAsText(tmpHtmlPage.getElementById("idTh1")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTh1")));

    assertEquals("header2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTh2")));
    assertEquals("header1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTh2")));

    assertEquals("data1 data2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr2")));
    assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr2")));

    assertEquals("data1", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd1")));
    assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd1")));

    assertEquals("data2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd2")));
    assertEquals("header1 header2 data1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd2")));

    assertEquals("data3 data4", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr3")));
    assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr3")));

    assertEquals("data3", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd3")));
    assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd3")));

    assertEquals("data4", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd4")));
    assertEquals("header1 header2 data1 data2 data3", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd4")));

    assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr4")));
    assertEquals("header1 header2 data1 data2 data3 data4", tmpResult
        .getTextBefore(tmpHtmlPage.getElementById("idTr4")));

    assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd5")));
    assertEquals("header1 header2 data1 data2 data3 data4", tmpResult
        .getTextBefore(tmpHtmlPage.getElementById("idTd5")));
  }

  public void testAsText_OrderedList() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<ol id='idOl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ol>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("before 1. Line1 2. Line2 after", tmpResult.getText());

    assertEquals("1. Line1 2. Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idOl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idOl")));

    assertEquals("1. Line1", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi1")));

    assertEquals("2. Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi2")));
    assertEquals("before 1. Line1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi2")));
  }

  public void testAsText_UnorderedList() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<ul id='idUl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ul>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("before Line1 Line2 after", tmpResult.getText());

    assertEquals("Line1 Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idUl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idUl")));

    assertEquals("Line1", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi1")));

    assertEquals("Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi2")));
    assertEquals("before Line1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi2")));
  }

  public void testAsText_Select() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "red green blue";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_EmptySelect() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "</select>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_SelectWithText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "123<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>456"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "123 red green blue 456";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_SelectWithOptgroup() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>" + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>" + "</optgroup>" + "</select>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "colors red green blue";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_InputImageWithAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before "
        + "<input type='image' id='image_id' src='src.img' alt='Test Image'>" + " after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "before Test Image after";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_ImageWithAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before " + "<img src='src.img' alt='test image'>" + " after"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "before test image after";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_RadioButton() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before "
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio'>RadioButton" + " after"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "before RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_RadioButtonSelected() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before "
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' checked>RadioButton"
        + " after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "before RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_LabelWithEnclosedRadioButtonSelected() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before " + "<label>LabelText"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' selected>RadioButton"
        + "</label>" + " after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);

    String tmpExpected = "before LabelText RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  public void testAsText_Javascript() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<script language='JavaScript' type='text/javascript'>" + "function foo() {}"
        + "</script>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    DomNodeText tmpResult = new DomNodeText(tmpHtmlPage);
    assertEquals("", tmpResult.getText());
  }

  public void testGetLabelTextBefore_None() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_AtStart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MyLabel", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_IgnoreHidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>MyLabel" + "<input value='hiddenValue' type='hidden'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MyLabel", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_BeforeForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MoreText</p>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MoreText MyLabel", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_IgnoreDifferentForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test2'><p>MoreText</p></form>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MyLabel", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_UntilNext() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "Other<input id='MyOtherInputId' value='value2' type='text'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MyLabel", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextBefore_ChainedControls() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "MyLabel <input id='MyOtherInputId' value='value2' type='text'> "
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("MyLabel value2", tmpDomNodeText.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  public void testGetLabelTextAfter_None() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>" + "</form>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("", tmpDomNodeText.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  public void testGetLabelTextAfter_AtEnd() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("CheckBox", tmpDomNodeText.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  public void testGetLabelTextAfter_IgnoreHidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input value='hiddenValue' type='hidden'>part2" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("CheckBoxpart2", tmpDomNodeText.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  public void testGetLabelTextAfter_IgnoreAfterForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "<p>MoreText</p>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("CheckBox", tmpDomNodeText.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  public void testGetLabelTextAfter_UntilNext() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input name='MyOtherCheckboxName' value='value2' type='checkbox'>CheckBox2" + "</form>" + "<p>MoreText</p>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    DomNodeText tmpDomNodeText = new DomNodeText(tmpHtmlPage);

    assertEquals("CheckBox", tmpDomNodeText.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }
}
