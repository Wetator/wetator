/*
 * Copyright (c) 2008-2012 wetator.org
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


package org.wetator.backend.htmlunit.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlPageIndexTest {

  private void asText(final String anExpected, final String anHtmlCode) throws IOException {
    asText(anExpected, anExpected, anHtmlCode);
  }

  @SuppressWarnings("deprecation")
  private void asText(final String anExpected, final String anExpectedWithoutFC, final String anHtmlCode)
      throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.INTERNET_EXPLORER_6, anHtmlCode);
    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals(anExpected, tmpResult.getText());
    Assert.assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());

    tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.INTERNET_EXPLORER_7, anHtmlCode);
    tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals(anExpected, tmpResult.getText());
    Assert.assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());

    tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.INTERNET_EXPLORER_8, anHtmlCode);
    tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals(anExpected, tmpResult.getText());
    Assert.assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());

    tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.FIREFOX_3, anHtmlCode);
    tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals(anExpected, tmpResult.getText());
    Assert.assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());

    tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.FIREFOX_3_6, anHtmlCode);
    tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals(anExpected, tmpResult.getText());
    Assert.assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_EmptyPage() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";

    asText("", tmpHtmlCode);
  }

  @Test
  public void asText_SimplePage() throws IOException {
    String tmpHtmlCode = "<html>" + "<head>" + "<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
        + "<title>Page Title</title>" + "</head>" + "<body>" + "<p>Paragraph 1</p>" + "<p>Paragraph 2</p>"
        + "</body></html>";

    asText("Paragraph 1 Paragraph 2", tmpHtmlCode);
  }

  @Test
  public void asText_Paragraph() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>Paragraph 1</p>" + "<p>Paragraph 2</p>" + "</body></html>";

    asText("Paragraph 1 Paragraph 2", tmpHtmlCode);
  }

  @Test
  public void asText_Font() throws IOException {
    String tmpHtmlCode = "<html><body><p>" + "<font color='red'>red</font> <font color='green'>green</font>"
        + "</p></body></html>";

    asText("red green", tmpHtmlCode);
  }

  @Test
  public void asText_Span() throws IOException {
    String tmpHtmlCode = "<html><body><p>" + "<span> 17.11 </span> mg" + "</p></body></html>";

    asText("17.11 mg", tmpHtmlCode);
  }

  @Test
  public void asText_Formatting() throws IOException {
    String tmpHtmlCode = "<html><body><p>" + "<b>1</b> <big>2</big> <em>3</em><i>4</i> <small>5</small> "
        + "<strong>6</strong> <sub>7</sub> <sup>8</sup> <ins>9</ins> <del>10</del>" + "</p></body></html>";

    asText("1 2 3 4 5 6 7 8 9 10", tmpHtmlCode);
  }

  @Test
  public void asText_ComputerOutput() throws IOException {
    String tmpHtmlCode = "<html><body><p>"
        + "<code>1</code> <kbd>2</kbd> <samp>3</samp> <tt>4</tt> <var>5</var> <pre>6</pre>" + "</p></body></html>";
    asText("1 2 3 4 5 6", tmpHtmlCode);
  }

  @Test
  public void asText_CitationQuotationDefinition() throws IOException {
    String tmpHtmlCode = "<html><body><p>"
        + "<abbr title='a'>1</abbr> <acronym title='b'>2</acronym> <q>3</q> <cite>4</cite> <dfn>5</dfn>"
        + "</p></body></html>";

    asText("1 2 \"3\" 4 5", tmpHtmlCode);
  }

  @Test
  public void asText_Mix() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>This t<font color='red'>ext</font> is <b>styled</b>.</p>"
        + "</body></html>";

    asText("This text is styled.", tmpHtmlCode);
  }

  @Test
  public void asText_Mix2() throws IOException {
    String tmpHtmlCode = "<html><body><table><tr>"
        + "<td style='color:#222288'>Table C<font color='red'>lickable</font> <b>forma<i>ted</i> t</b>ext</td>"
        + "</tr></table></body></html>";

    asText("Table Clickable formated text", tmpHtmlCode);
  }

  @Test
  public void asText_Mix3() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>Fi<font color='red'>eld</font>4</p>" + "</p></body></html>";

    asText("Field4", tmpHtmlCode);
  }

  @Test
  public void asText_AllControls() throws IOException {
    String tmpHtmlCode = "<html><body>" //
        + "<p>PageStart</p>" //
        + "<form action='test'>" //
        + "<p> </p>" //
        + "<fieldset>" //
        + "<legend id='idLegend'>LegendLabel</legend>" //
        + "</fieldset>" //
        + "<p> </p>" //
        + "<label id='idLabel' for='TextInput'>LabelLabel</label>" //
        + "<p> </p>" //
        + "<input id='idTextInput' name='TextInput' type='text' value='inputValue'>" //
        + "<p> </p>" //
        + "<input name='PasswordInput' type='password' value='secretInputValue'>" //
        + "<p> </p>" //
        + "<input name='HiddenInput' type='hidden' value='hiddenInputValue'>" //
        + "<p> </p>" //
        + "<textarea name='TextArea'>textAreaValue</textarea>" //
        + "<p> </p>" //
        + "<input name='FileInput' type='file'>" //
        + "<p> </p>" //
        + "<select id='idSingleSelect' name='SingleSelect'>" //
        + "<option selected>Option1Value" //
        + "<option>Option2Value" //
        + "</select>" //
        + "<p> </p>" //
        + "<select name='MultipleSelect' multiple>" //
        + "  <option selected>Option1Value" //
        + "  <option>Option2Value" //
        + "  <option selected>Option3Value" //
        + "</select>" //
        + "<p> </p>" //
        + "<select name='SingleOptgroupSelect'>" //
        + "  <optgroup label='SingleOptgroupLabel1'>" //
        + "    <option>SingleOptgroup1Option1Value" //
        + "    <option>SingleOptgroup1Option2Value" //
        + "    <option>SingleOptgroup1Option3Value" //
        + "  </optgroup>" //
        + "  <optgroup label='SingleOptgroupLabel2'>" //
        + "    <option>SingleOptgroup2Option1Value" //
        + "    <option selected>SingleOptgroup2Option2Value" //
        + "    <option>SingleOptgroup2Option3Value" //
        + "  </optgroup>" //
        + "</select>" //
        + "<p> </p>" //
        + "<select name='MultipleOptgroupSelect' multiple>" //
        + "  <optgroup label='MultipleOptgroupLabel1'>" //
        + "    <option selected>MultipleOptgroup1Option1Value" //
        + "    <option>MultipleOptgroup1Option2Value" //
        + "    <option selected>MultipleOptgroup1Option3Value" //
        + "  </optgroup>" //
        + "  <optgroup label='MultipleOptgroupLabel2'>" //
        + "    <option>MultipleOptgroup2Option1Value" //
        + "    <option selected>MultipleOptgroup2Option2Value" //
        + "    <option>MultipleOptgroup2Option3Value" //
        + "  </optgroup>" //
        + "</select>" //
        + "<p> </p>" //
        + "<input name='RadioInput' type='radio' value='radioInputValue1'>radioInputLabel1" //
        + "<input name='RadioInput' type='radio' value='radioInputValue2' checked>radioInputLabel2" //
        + "<p> </p>" //
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue1' checked>checkboxInputLabel1" //
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue2'>checkboxInputLabel2" //
        + "<p> </p>" //
        + "<button name='ButtonButton' type='button' value='buttonButtonValue'>buttonButtonLabel</button>" //
        + "<p> </p>" //
        + "<input name='ButtonInput' type='button' value='buttonInputValue'>" //
        + "<p> </p>" //
        + "<input name='SubmitInput' type='submit' value='submitInputValue'>" //
        + "<p> </p>" //
        + "<input name='ResetInput' type='reset' value='resetInputValue'>" //
        + "</form>" //
        + "</body></html>";

    String tmpExpected = "PageStart " //
        + "LegendLabel " //
        + "LabelLabel " //
        + "inputValue " //
        + "secretInputValue " //
        + "textAreaValue " //
        + "Option1Value " //
        + "Option2Value " //
        + "Option1Value " //
        + "Option2Value " //
        + "Option3Value " //
        + "SingleOptgroupLabel1 " //
        + "SingleOptgroup1Option1Value " //
        + "SingleOptgroup1Option2Value " //
        + "SingleOptgroup1Option3Value " //
        + "SingleOptgroupLabel2 " //
        + "SingleOptgroup2Option1Value " //
        + "SingleOptgroup2Option2Value " //
        + "SingleOptgroup2Option3Value " //
        + "MultipleOptgroupLabel1 " //
        + "MultipleOptgroup1Option1Value " //
        + "MultipleOptgroup1Option2Value " //
        + "MultipleOptgroup1Option3Value " //
        + "MultipleOptgroupLabel2 " //
        + "MultipleOptgroup2Option1Value " //
        + "MultipleOptgroup2Option2Value " //
        + "MultipleOptgroup2Option3Value " //
        + "radioInputLabel1 radioInputLabel2 " //
        + "checkboxInputLabel1 checkboxInputLabel2 " //
        + "buttonButtonLabel " //
        + "buttonInputValue " //
        + "submitInputValue " //
        + "resetInputValue";

    String tmpExpected2 = "PageStart " //
        + "LegendLabel " //
        + "LabelLabel " //
        + "radioInputLabel1 radioInputLabel2 " //
        + "checkboxInputLabel1 checkboxInputLabel2";

    asText(tmpExpected, tmpExpected2, tmpHtmlCode);

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("PageStart", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLegend")));
    Assert.assertEquals("PageStart LegendLabel", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLabel")));

    Assert.assertEquals("inputValue", tmpResult.getAsText(tmpHtmlPage.getElementById("idTextInput")));
    Assert.assertEquals("Option1Value Option2Value", tmpResult.getAsText(tmpHtmlPage.getElementById("idSingleSelect")));

  }

  @Test
  public void asText_Heading() throws IOException {
    String tmpHtmlCode = "<html><body>before" + "<h1>Heading1</h1>" + "<h2>Heading2</h2>" + "<h3>Heading3</h3>"
        + "<h4>Heading4</h4>" + "<h5>Heading5</h5>" + "<h6>Heading6</h6>" + "after</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    String tmpExpected = "before Heading1 Heading2 Heading3 Heading4 Heading5 Heading6 after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_Table() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table id='idTable'>" + "<tr id='idTr1'>"
        + "  <th id='idTh1'>header1</th><th id='idTh2'>header2</th>" + "</tr>" + "<tr id='idTr2'>"
        + "  <td id='idTd1'>data1</td><td id='idTd2'>data2</td>" + "</tr>" + "<tr id='idTr3'>"
        + "  <td id='idTd3'>data3</td><td id='idTd4'>data4</td>" + "</tr>" + "<tr id='idTr4'>"
        + "  <td colspan='2' id='idTd5'>data5</td>" + "</tr>" + "</table>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    String tmpExpected = "header1 header2 data1 data2 data3 data4 data5";
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());

    Assert.assertEquals("header1 header2 data1 data2 data3 data4 data5",
        tmpResult.getAsText(tmpHtmlPage.getElementById("idTable")));
    Assert.assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTable")));

    Assert.assertEquals("header1 header2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr1")));
    Assert.assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr1")));

    Assert.assertEquals("header1", tmpResult.getAsText(tmpHtmlPage.getElementById("idTh1")));
    Assert.assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTh1")));

    Assert.assertEquals("header2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTh2")));
    Assert.assertEquals("header1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTh2")));

    Assert.assertEquals("data1 data2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr2")));
    Assert.assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr2")));

    Assert.assertEquals("data1", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd1")));
    Assert.assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd1")));

    Assert.assertEquals("data2", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd2")));
    Assert.assertEquals("header1 header2 data1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd2")));

    Assert.assertEquals("data3 data4", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr3")));
    Assert.assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr3")));

    Assert.assertEquals("data3", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd3")));
    Assert.assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd3")));

    Assert.assertEquals("data4", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd4")));
    Assert.assertEquals("header1 header2 data1 data2 data3",
        tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd4")));

    Assert.assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getElementById("idTr4")));
    Assert.assertEquals("header1 header2 data1 data2 data3 data4",
        tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTr4")));

    Assert.assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getElementById("idTd5")));
    Assert.assertEquals("header1 header2 data1 data2 data3 data4",
        tmpResult.getTextBefore(tmpHtmlPage.getElementById("idTd5")));
  }

  @Test
  public void asText_OrderedList() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<ol id='idOl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ol>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    Assert.assertEquals("before 1. Line1 2. Line2 after", tmpResult.getText());

    Assert.assertEquals("1. Line1 2. Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idOl")));
    Assert.assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idOl")));

    Assert.assertEquals("1. Line1", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi1")));
    Assert.assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi1")));

    Assert.assertEquals("2. Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi2")));
    Assert.assertEquals("before 1. Line1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi2")));
  }

  @Test
  public void asText_UnorderedList() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<ul id='idUl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ul>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    String tmpExpected = "before Line1 Line2 after";
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());

    Assert.assertEquals("Line1 Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idUl")));
    Assert.assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idUl")));

    Assert.assertEquals("Line1", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi1")));
    Assert.assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi1")));

    Assert.assertEquals("Line2", tmpResult.getAsText(tmpHtmlPage.getElementById("idLi2")));
    Assert.assertEquals("before Line1", tmpResult.getTextBefore(tmpHtmlPage.getElementById("idLi2")));
  }

  @Test
  public void asText_Select() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "red green blue";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals("", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_EmptySelect() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "</select>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_SelectWithText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "123<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>456"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "123 red green blue 456";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
  }

  @Test
  public void asText_SelectWithOptgroup() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<select>" + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>" + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>" + "</optgroup>" + "</select>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("colors red green blue", tmpResult.getText());
    Assert.assertEquals("", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_InputImageWithAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input type='image' id='image_id' src='src.img' alt='Test Image'>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before Test Image after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    tmpExpected = "before after";
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_ImageWithAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<img src='src.img' alt='test image'>" + "after"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before test image after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_SubmitInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MySubmitId' name='MySubmitName' value='Submit' type='submit'>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("before Submit after", tmpResult.getText());
    Assert.assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_ResetInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyResetId' name='MyResetName' value='Reset' type='reset'>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("before Reset after", tmpResult.getText());
    Assert.assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_ButtonInput() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyButtonId' name='MyButtonName' value='Button' type='button'>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("before Button after", tmpResult.getText());
    Assert.assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_Button() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<button id='MyButtonId' name='MyButtonName'>Button</button>"
        + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("before Button after", tmpResult.getText());
    Assert.assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_RadioButton() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio'>RadioButton" + " after"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before RadioButton after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_RadioButtonSelected() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' checked>RadioButton"
        + " after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before RadioButton after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_LabelWithEnclosedRadioButtonSelected() throws IOException {
    String tmpHtmlCode = "<html><body>" + "before" + "<label>LabelText"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' selected>RadioButton"
        + "</label>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before LabelText RadioButton after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_LabelWithEnclosedSelect() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<label>LabelText before"
        + "<select id='idSingleSelect' name='SingleSelect'>" //
        + "<option selected>Option1Value" //
        + "<option>Option2Value" //
        + "</select>" //
        + "</label>" + "after" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "LabelText before Option1Value Option2Value after";
    Assert.assertEquals(tmpExpected, tmpResult.getText());

    tmpExpected = "LabelText before after";
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_Javascript() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<script language='JavaScript' type='text/javascript'>" + "function foo() {}"
        + "</script>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    String tmpExpected = "";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void asText_TextTransform() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p style='text-transform: lowercase;'>LoWerCase</p>"
        + "<p style='text-transform: uppercase;'>uppErCase</p>"
        + "<p style='text-transform: capitalize;'>capiTalize</p>" + "<p style='text-transform: none;'>nOne</p>"
        + "<div style='text-transform: uppercase'><p>insideDiv</p></div>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    String tmpExpected = "lowercase UPPERCASE CapiTalize nOne INSIDEDIV";
    Assert.assertEquals(tmpExpected, tmpResult.getText());
    Assert.assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getLabelTextBefore_None() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("", tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_AtStart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MyLabel", tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_IgnoreHidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>MyLabel" + "<input value='hiddenValue' type='hidden'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MyLabel", tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_BeforeForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MoreText</p>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MoreText MyLabel",
        tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_IgnoreDifferentForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test2'><p>MoreText</p></form>" + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MyLabel", tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_UntilNext() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "Other<input id='MyOtherInputId' value='value2' type='text'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MyLabel", tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextBefore_ChainedControls() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "MyLabel <input id='MyOtherInputId' value='value2' type='text'> "
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("MyLabel value2",
        tmpHtmlPageIndex.getLabelTextBefore(tmpHtmlPage.getElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelTextAfter_None() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>" + "</form>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("", tmpHtmlPageIndex.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelTextAfter_AtEnd() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("CheckBox", tmpHtmlPageIndex.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelTextAfter_IgnoreHidden() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input value='hiddenValue' type='hidden'>part2" + "</form>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert
        .assertEquals("CheckBoxpart2", tmpHtmlPageIndex.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelTextAfter_IgnoreAfterForm() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "<p>MoreText</p>" + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("CheckBox", tmpHtmlPageIndex.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelTextAfter_UntilNext() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input name='MyOtherCheckboxName' value='value2' type='checkbox'>CheckBox2" + "</form>" + "<p>MoreText</p>"
        + "</body></html>";

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    Assert.assertEquals("CheckBox", tmpHtmlPageIndex.getLabelTextAfter(tmpHtmlPage.getElementById("MyCheckboxId")));
  }
}
