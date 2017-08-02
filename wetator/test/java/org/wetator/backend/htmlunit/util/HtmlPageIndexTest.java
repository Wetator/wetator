/*
 * Copyright (c) 2008-2017 wetator.org
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlPageIndexTest {

  @Test
  public void getHtmlElementById() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1></body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals("myH1", tmpResult.getHtmlElementById("myH1").getId());
  }

  @Test(expected = ElementNotFoundException.class)
  public void getHtmlElementById_NotFound() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1></body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    tmpResult.getHtmlElementById("myH2").getId();
  }

  @Test
  public void getHtmlElementById_Duplicate() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1><h1 id='myH1'>Heading2</h1></body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals("myH1", tmpResult.getHtmlElementById("myH1").getId());
    assertEquals("Heading1", tmpResult.getHtmlElementById("myH1").getTextContent());
  }

  private void getText(final String anExpected, final String anHtmlCode) throws IOException {
    getText(anExpected, anExpected, anHtmlCode);
  }

  private void getText(final String anExpected, final String anExpectedWithoutFC, final String anHtmlCode)
      throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.INTERNET_EXPLORER, anHtmlCode);
    HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals(anExpected, tmpResult.getText());
    assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());

    tmpHtmlPage = PageUtil.constructHtmlPage(BrowserVersion.FIREFOX_52, anHtmlCode);
    tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals(anExpected, tmpResult.getText());
    assertEquals(anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_EmptyPage() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "</body></html>";

    getText("", tmpHtmlCode);
  }

  @Test
  public void getText_SimplePage() throws IOException {
    final String tmpHtmlCode = "<html>" + "<head>"
        + "<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>" + "<title>Page Title</title>"
        + "</head>" + "<body>" + "<p>Paragraph 1</p>" + "<p>Paragraph 2</p>" + "</body></html>";

    getText("Paragraph 1 Paragraph 2", tmpHtmlCode);
  }

  @Test
  public void getText_Paragraph() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<p>Paragraph 1</p>" + "<p>Paragraph <br>2</p>" + "</body></html>";

    getText("Paragraph 1 Paragraph 2", tmpHtmlCode);
  }

  @Test
  public void getText_Font() throws IOException {
    final String tmpHtmlCode = "<html><body><p>" + "<font color='red'>red</font> <font color='green'>green</font>"
        + "</p></body></html>";

    getText("red green", tmpHtmlCode);
  }

  @Test
  public void getText_Span() throws IOException {
    final String tmpHtmlCode = "<html><body><p>" + "<span> 17.11 </span> mg" + "</p></body></html>";

    getText("17.11 mg", tmpHtmlCode);
  }

  @Test
  public void getText_Formatting() throws IOException {
    final String tmpHtmlCode = "<html><body><p>" + "<b>1</b> <big>2</big> <em>3</em><i>4</i> <small>5</small> "
        + "<strong>6</strong> <sub>7</sub> <sup>8</sup> <ins>9</ins> <del>10</del>" + "</p></body></html>";

    getText("1 2 3 4 5 6 7 8 9 10", tmpHtmlCode);
  }

  @Test
  public void getText_ComputerOutput() throws IOException {
    final String tmpHtmlCode = "<html><body><p>"
        + "<code>1</code> <kbd>2</kbd> <samp>3</samp> <tt>4</tt> <var>5</var> <pre>6</pre>" + "</p></body></html>";
    getText("1 2 3 4 5 6", tmpHtmlCode);
  }

  @Test
  public void getText_CitationQuotationDefinition() throws IOException {
    final String tmpHtmlCode = "<html><body><p>"
        + "<abbr title='a'>1</abbr> <acronym title='b'>2</acronym> <q>3</q> <cite>4</cite> <dfn>5</dfn>"
        + "</p></body></html>";

    getText("1 2 \"3\" 4 5", tmpHtmlCode);
  }

  @Test
  public void getText_Mix() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>This t<font color='red'>ext</font> is <b>styled</b>.</p>"
        + "</body></html>";
    // @formatter:on

    getText("This text is styled.", tmpHtmlCode);
  }

  @Test
  public void getText_Mix2() throws IOException {
    final String tmpHtmlCode = "<html><body><table><tr>"
        + "<td style='color:#222288'>Table C<font color='red'>lickable</font> <b>forma<i>ted</i> t</b>ext</td>"
        + "</tr></table></body></html>";

    getText("Table Clickable formated text", tmpHtmlCode);
  }

  @Test
  public void getText_Mix3() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<p>Fi<font color='red'>eld</font>4</p>" + "</p></body></html>";

    getText("Field4", tmpHtmlCode);
  }

  @Test
  public void getText_AllControls() throws IOException {
    final String tmpHtmlCode = "<html><body>" //
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
        + "<input id='idTextInput' name='TextInput' type='text' value='inputValue' placeholder='my placeholder'>" //
        + "<p> </p>" //
        + "<input id='idTextInput' name='TextInput' type='text' value='' placeholder='my placeholder'>" //
        + "<p> </p>" //
        + "<input name='PasswordInput' type='password' value='secretInputValue'>" //
        + "<p> </p>" //
        + "<input name='PasswordInput' type='password' value='secretInputValue' placeholder='my pwd placeholder'>" //
        + "<p> </p>" //
        + "<input name='PasswordInput' type='password' value='' placeholder='my pwd placeholder'>" //
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

    final String tmpExpected = "PageStart " //
        + "LegendLabel " //
        + "LabelLabel " //
        + "inputValue " //
        + "inputValue " //
        + "my placeholder " //
        + "secretInputValue " //
        + "secretInputValue " //
        + "my pwd placeholder " //
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

    final String tmpExpected2 = "PageStart " //
        + "LegendLabel " //
        + "LabelLabel " //
        + "radioInputLabel1 radioInputLabel2 " //
        + "checkboxInputLabel1 checkboxInputLabel2";

    getText(tmpExpected, tmpExpected2, tmpHtmlCode);

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("PageStart", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLegend")));
    assertEquals("PageStart LegendLabel", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLabel")));

    assertEquals("inputValue", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTextInput")));
    assertEquals("Option1Value Option2Value", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idSingleSelect")));

  }

  @Test
  public void getText_Heading() throws IOException {
    final String tmpHtmlCode = "<html><body>before" + "<h1>Heading1</h1>" + "<h2>Heading2</h2>" + "<h3>Heading3</h3>"
        + "<h4>Heading4</h4>" + "<h5>Heading5</h5>" + "<h6>Heading6</h6>" + "after</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "before Heading1 Heading2 Heading3 Heading4 Heading5 Heading6 after";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_Table() throws IOException {
    // @formatter:off
    final String tmpHtmlCode =
        "<html><body>"
            + "<table id='idTable'>"
            + "<tr id='idTr1'>"
            + "  <th id='idTh1'>header1</th><th id='idTh2'>header2</th>"
            + "</tr>"
            + "<tr id='idTr2'>"
            + "  <td id='idTd1'>data1</td><td id='idTd2'>data2</td>"
            + "</tr>"
            + "<tr id='idTr3'>"
            + "  <td id='idTd3'>data3</td><td id='idTd4'>data4</td>"
            + "</tr>"
            + "<tr id='idTr4'>"
            + "  <td colspan='2' id='idTd5'>data5</td>"
            + "</tr>"
            + "</table>"
            + "</body></html>";
    // @formatter:off
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "header1 header2 data1 data2 data3 data4 data5";
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());

    assertEquals("header1 header2 data1 data2 data3 data4 data5",
        tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTable")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTable")));

    assertEquals("header1 header2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTr1")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTr1")));

    assertEquals("header1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTh1")));
    assertEquals("", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTh1")));

    assertEquals("header2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTh2")));
    assertEquals("header1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTh2")));

    assertEquals("data1 data2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTr2")));
    assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTr2")));

    assertEquals("data1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTd1")));
    assertEquals("header1 header2", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTd1")));

    assertEquals("data2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTd2")));
    assertEquals("header1 header2 data1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTd2")));

    assertEquals("data3 data4", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTr3")));
    assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTr3")));

    assertEquals("data3", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTd3")));
    assertEquals("header1 header2 data1 data2", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTd3")));

    assertEquals("data4", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTd4")));
    assertEquals("header1 header2 data1 data2 data3",
        tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTd4")));

    assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTr4")));
    assertEquals("header1 header2 data1 data2 data3 data4",
        tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTr4")));

    assertEquals("data5", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTd5")));
    assertEquals("header1 header2 data1 data2 data3 data4",
        tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idTd5")));
  }

  @Test
  public void getText_OrderedList() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before" + "<ol id='idOl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ol>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals("before 1. Line1 2. Line2 after", tmpResult.getText());

    assertEquals("1. Line1 2. Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idOl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idOl")));

    assertEquals("1. Line1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi1")));

    assertEquals("2. Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi2")));
    assertEquals("before 1. Line1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi2")));
  }

  @Test
  public void getText_UnorderedList() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before" + "<ul id='idUl'>" + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2" + "</ul>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "before Line1 Line2 after";
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());

    assertEquals("Line1 Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idUl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idUl")));

    assertEquals("Line1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi1")));

    assertEquals("Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi2")));
    assertEquals("before Line1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi2")));
  }

  @Test
  public void getText_Object() throws IOException {
    final String tmpClsid = "clsid:TESTING-CLASS-ID";
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<object id='idObj' classid='" + tmpClsid + "'>"
        + "Object tag not supported"
        + "</object>"
        + "after"
        + "</body></html>";
    // @formatter:on

    // FF
    final StringWebResponse tmpResponse = new StringWebResponse(tmpHtmlCode,
        new URL("http://www.wetator.org/test.html"));
    WebClient tmpWebClient = new WebClient(BrowserVersion.FIREFOX_52);
    try {
      final HtmlPage tmpHtmlPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("before Object tag not supported after", tmpResult.getText());

      assertEquals("Object tag not supported", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idObj")));
      assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idObj")));
    } finally {
      tmpWebClient.close();
    }

    // IE without support
    tmpWebClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
    try {
      final HtmlPage tmpHtmlPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("before Object tag not supported after", tmpResult.getText());

      assertEquals("Object tag not supported", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idObj")));
      assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idObj")));
    } finally {
      tmpWebClient.close();
    }

    // IE with support
    tmpWebClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
    final Map<String, String> tmpActiveXObjectMap = new HashMap<>();
    tmpActiveXObjectMap.put(tmpClsid, "org.wetator.backend.htmlunit.util.HtmlPageIndexTest");
    tmpWebClient.setActiveXObjectMap(tmpActiveXObjectMap);

    try {
      final HtmlPage tmpHtmlPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("before after", tmpResult.getText());

      assertEquals("", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idObj")));
      assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idObj")));
    } finally {
      tmpWebClient.close();
    }
  }

  @Test
  public void getText_Select() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>"
        + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "red green blue";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals("", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_EmptySelect() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<select>" + "</select>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_SelectWithText() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "123<select>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>456"
        + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "123 red green blue 456";
    assertEquals(tmpExpected, tmpResult.getText());
  }

  @Test
  public void getText_SelectWithOptgroup() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<select>" + "<optgroup label='colors' id='optgroup_colors'>"
        + "<option value='o_red'>red</option>" + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>" + "</optgroup>" + "</select>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("colors red green blue", tmpResult.getText());
    assertEquals("", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_InputImageWithAlt() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input type='image' id='image_id' src='src.img' alt='Test Image'>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "before Test Image after";
    assertEquals(tmpExpected, tmpResult.getText());
    tmpExpected = "before after";
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_ImageWithAlt() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before" + "<img src='src.img' alt='test image'>" + "after"
        + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "before test image after";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_SubmitInput() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MySubmitId' name='MySubmitName' value='Submit' type='submit'>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before Submit after", tmpResult.getText());
    assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_ResetInput() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyResetId' name='MyResetName' value='Reset' type='reset'>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before Reset after", tmpResult.getText());
    assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_ButtonInput() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyButtonId' name='MyButtonName' value='Button' type='button'>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before Button after", tmpResult.getText());
    assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_Button() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before" + "<button id='MyButtonId' name='MyButtonName'>Button</button>"
        + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before Button after", tmpResult.getText());
    assertEquals("before after", tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_RadioButton() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio'>RadioButton" + " after"
        + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "before RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_RadioButtonSelected() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' checked>RadioButton"
        + " after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "before RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_LabelWithEnclosedRadioButtonSelected() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "before" + "<label>LabelText"
        + "<input id='MyRadioButtonId' name='MyRadioButtonName' value='value' type='radio' selected>RadioButton"
        + "</label>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    final String tmpExpected = "before LabelText RadioButton after";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_LabelWithEnclosedSelect() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<label>LabelText before"
        + "<select id='idSingleSelect' name='SingleSelect'>" //
        + "<option selected>Option1Value" //
        + "<option>Option2Value" //
        + "</select>" //
        + "</label>" + "after" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    String tmpExpected = "LabelText before Option1Value Option2Value after";
    assertEquals(tmpExpected, tmpResult.getText());

    tmpExpected = "LabelText before after";
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_SpanWithBlank() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span>line 1 </span>"
        + "<span> line 2</span>"
        + "<span>line 3</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "line 1 line 2line 3";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_SpanWithBlock() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { display: block; } </style>"
        + "<span class='line'>line 1 </span>"
        + "<span class='line'> line 2</span>"
        + "<span class='line'>line 3</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "line 1 line 2 line 3";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_Javascript() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<script language='JavaScript' type='text/javascript'>"
        + "function foo() {}"
        + "</script>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_TextTransform() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p style='text-transform: lowercase;'>LoWerCase</p>"
        + "<p style='text-transform: uppercase;'>uppErCase</p>"
        + "<p style='text-transform: capitalize;'>capiTalize</p>"
        + "<p style='text-transform: none;'>nOne</p>"
        + "<div style='text-transform: uppercase'><p>insideDiv</p></div>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "lowercase UPPERCASE CapiTalize nOne INSIDEDIV";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getLabelingTextBefore_None() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("", tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_AtStart() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MyLabel", tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_IgnoreHidden() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>MyLabel"
        + "<input value='hiddenValue' type='hidden'>"
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MyLabel", tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_BeforeForm() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MoreText</p>"
        + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MoreText MyLabel",
        tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_IgnoreDifferentForm() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test2'><p>MoreText</p></form>"
        + "<form action='test'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MyLabel", tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_UntilNext() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "Other<input id='MyOtherInputId' value='value2' type='text'>"
        + "MyLabel<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MyLabel", tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_ChainedControls() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "MyLabel <input id='MyOtherInputId' value='value2' type='text'> "
        + "<input id='MyInputId' name='MyInputName' value='value1' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("MyLabel value2",
        tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("MyInputId"), 0));
  }

  @Test
  public void getLabelingTextBefore_InsideButton() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "before<button id='MyButton' type='button'>some button text<img id='myImg'>after</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before some button text",
        tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("myImg"), 0));
  }

  @Test
  public void getLabelingTextBefore_ButtonBeforeDirect() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "before<button id='MyButton' type='button'>some button text</button><img id='myImg'>after"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before some button text",
        tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("myImg"), 0));
  }

  @Test
  public void getLabelingTextAfter_None() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>" + "</form>"
        + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_AtEnd() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_IgnoreHidden() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input value='hiddenValue' type='hidden'>part2" + "</form>" + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBoxpart2",
        tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_IgnoreAfterForm() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "<p>MoreText</p>" + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_UntilNext() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input name='MyOtherCheckboxName' value='value2' type='checkbox'>CheckBox2" + "</form>" + "<p>MoreText</p>"
        + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_InsideButton() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button id='MyButton' type='button'>before<img id='myImg'>some button text</button>after" + "</form>"
        + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("some button text after",
        tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("myImg")));
  }

  @Test
  public void getLabelingTextAfter_ButtonAfterDirect() throws IOException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myImg'><button id='MyButton' type='button'>some button text</button>after" + "</form>"
        + "</body></html>";

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("myImg")));
  }
}
