/*
 * Copyright (c) 2008-2025 wetator.org
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.htmlunit.BrowserVersion;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.util.FindSpot;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlPageIndexTest {
  protected WebClient webClient;

  /**
   * Creates a Wetator configuration.
   */
  @Before
  public void createWebClient() {
    webClient = new WebClient(BrowserVersion.FIREFOX_ESR);
  }

  /**
   * Closes the WebClient.
   */
  @After
  public void closeWebClient() {
    webClient.close();
  }

  @Test
  public void getHtmlElementById() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1></body></html>";
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals("myH1", tmpResult.getHtmlElementById("myH1").getId());
  }

  @Test(expected = ElementNotFoundException.class)
  public void getHtmlElementById_NotFound() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1></body></html>";
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    tmpResult.getHtmlElementById("myH2");
  }

  @Test
  public void getHtmlElementById_Duplicate() throws IOException {
    final String tmpHtmlCode = "<html><body><h1 id='myH1'>Heading1</h1><h1 id='myH1'>Heading2</h1></body></html>";
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    assertEquals("myH1", tmpResult.getHtmlElementById("myH1").getId());
    assertEquals("Heading1", tmpResult.getHtmlElementById("myH1").getTextContent());
  }

  private void getText(final String anExpected, final String anHtmlCode) throws IOException {
    getText(anExpected, anExpected, anHtmlCode);
  }

  private void getText(final String anExpected, final String anExpectedWithoutFC, final String anHtmlCode)
      throws IOException {
    PageUtil.consumeHtmlPage(BrowserVersion.FIREFOX, anHtmlCode, tmpHtmlPage -> {
      HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("getText[FF]", anExpected, tmpResult.getText());
      assertEquals("getTextWithoutFormControls[FF]", anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
    });

    PageUtil.consumeHtmlPage(BrowserVersion.FIREFOX_ESR, anHtmlCode, tmpHtmlPage -> {
      HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("getText[FF ESR]", anExpected, tmpResult.getText());
      assertEquals("getTextWithoutFormControls[FF ESR]", anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
    });

    PageUtil.consumeHtmlPage(BrowserVersion.CHROME, anHtmlCode, tmpHtmlPage -> {
      HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("getText[CHROME]", anExpected, tmpResult.getText());
      assertEquals("getTextWithoutFormControls[CHROME]", anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
    });

    PageUtil.consumeHtmlPage(BrowserVersion.EDGE, anHtmlCode, tmpHtmlPage -> {
      HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
      assertEquals("getText[EDGE]", anExpected, tmpResult.getText());
      assertEquals("getTextWithoutFormControls[EDGE]", anExpectedWithoutFC, tmpResult.getTextWithoutFormControls());
    });
  }

  @Test
  public void getText_EmptyPage() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on

    getText("", tmpHtmlCode);
  }

  @Test
  public void getText_TextOnly() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "a simple\ntext"
        + "</body></html>";
    // @formatter:on

    getText("a simple text", tmpHtmlCode);
  }

  @Test
  public void getText_LineBreak() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "a<br>simple <br> text"
        + "</body></html>";
    // @formatter:on

    getText("a simple text", tmpHtmlCode);
  }

  @Test
  public void getText_Paragraph() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<p>Paragraph 1</p>"
        + "between"
        + "<p>Paragraph 2</p>"
        + "<p> Paragraph 3</p>"
        + "<p>Paragraph 4 </p>"
        + " <p>Paragraph 5</p>"
        + "<p>Paragraph 6</p> "
        + "<p>Paragraph <br>7</p>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Paragraph 1 between Paragraph 2 Paragraph 3 Paragraph 4 Paragraph 5 Paragraph 6 Paragraph 7 after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Span() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<span>Span 1</span>"
        + "between"
        + "<span> Span 2</span>"
        + "between"
        + "<span>Span 3 </span>"
        + "between"
        + " <span>Span 4</span>"
        + "between"
        + "<span>Span 5</span> "
        + "between"
        + "<span>Span 6</span><span>Span 7</span>"
        + "between"
        + "<span>Span 8</span> <span>Span 9</span>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText(
        "beforeSpan 1between Span 2betweenSpan 3 between Span 4betweenSpan 5 betweenSpan 6Span 7betweenSpan 8 Span 9after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Division() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<div>Division 1</div>"
        + "between"
        + "<div>Division 2</div>"
        + "<div> Division 3</div>"
        + "<div>Division 4 </div>"
        + " <div>Division 5</div>"
        + "<div>Division 6</div> "
        + "<div>Division 7</div>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Division 1 between Division 2 Division 3 Division 4 Division 5 Division 6 Division 7 after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Font() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<font color='red'>Font 1</font>"
        + "<font color='green'>Font 2 </font>"
        + "<font color='blue'>Font 3 </font>"
        + "<font color='yellow'> Font 4</font>"
        + "<font color='green'>Font 5</font> "
        + "<font color='blue'>Font 6</font> "
        + " <font color='yellow'>Font 7</font>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforeFont 1Font 2 Font 3 Font 4Font 5 Font 6 Font 7after", tmpHtmlCode);
  }

  @Test
  public void getText_Formatting() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<b>1</b><big>2</big><em>3</em><i>4</i><small>5</small>"
        + "<strong>6</strong><sub>7</sub><sup>8</sup><ins>9</ins><del>10</del>"
        + "<b> 11</b><b>12 </b><b>13</b> <b>14</b>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before12345678910 1112 13 14after", tmpHtmlCode);
  }

  @Test
  public void getText_ComputerOutput() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<code>1</code><kbd>2</kbd><samp>3</samp><tt>4</tt><var>5</var><pre>6</pre>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before12345 6 after", tmpHtmlCode);
  }

  @Test
  public void getText_CitationQuotationDefinition() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<abbr title='a'>1</abbr><acronym title='b'>2</acronym><q>3</q><cite>4</cite><dfn>5</dfn>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before12\"3\"45after", tmpHtmlCode);
  }

  @Test
  public void getText_Heading() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<h1>Heading1</h1>"
        + "<h1>Heading1</h1>"
        + "<h1> Heading1</h1>"
        + "<h1>Heading1 </h1>"
        + " <h1>Heading1</h1>"
        + "<h1>Heading1</h1> "
        + "<h2>Heading2</h2>"
        + "<h2>Heading2</h2>"
        + "<h2> Heading2</h2>"
        + "<h2>Heading2 </h2>"
        + " <h2>Heading2</h2>"
        + "<h2>Heading2</h2> "
        + "<h3>Heading3</h3>"
        + "<h3>Heading3</h3>"
        + "<h3> Heading3</h3>"
        + "<h3>Heading3 </h3>"
        + " <h3>Heading3</h3>"
        + "<h3>Heading3</h3> "
        + "<h4>Heading4</h4>"
        + "<h4>Heading4</h4>"
        + "<h4> Heading4</h4>"
        + "<h4>Heading4 </h4>"
        + " <h4>Heading4</h4>"
        + "<h4>Heading4</h4> "
        + "<h5>Heading5</h5>"
        + "<h5>Heading5</h5>"
        + "<h5> Heading5</h5>"
        + "<h5>Heading5 </h5>"
        + " <h5>Heading5</h5>"
        + "<h5>Heading5</h5> "
        + "<h6>Heading6</h6>"
        + "<h6>Heading6</h6>"
        + "<h6> Heading6</h6>"
        + "<h6>Heading6 </h6>"
        + " <h6>Heading6</h6>"
        + "<h6>Heading6</h6> "
        + "after"
        + "</body></html>";
    // @formatter:on

    getText(
        "before Heading1 Heading1 Heading1 Heading1 Heading1 Heading1 Heading2 Heading2 Heading2 Heading2 Heading2 Heading2 "
            + "Heading3 Heading3 Heading3 Heading3 Heading3 Heading3 Heading4 Heading4 Heading4 Heading4 Heading4 Heading4 "
            + "Heading5 Heading5 Heading5 Heading5 Heading5 Heading5 Heading6 Heading6 Heading6 Heading6 Heading6 Heading6 after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Table() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<table>"
        + "<tr>"
        + "  <th>header1</th><th>header2</th>"
        + "</tr>"
        + "<tr>"
        + "  <td>data1</td><td> data2</td>"
        + "</tr>"
        + "<tr>"
        + "  <td>data3 </td><td>data4</td>"
        + "</tr>"
        + "<tr>"
        + "  <td colspan='2'>data5</td>"
        + "</tr>"
        + "<tr>"
        + "  <td rowspan='2'>data6</td><td>data7</td>"
        + "</tr>"
        + "<tr>"
        + "  <td>data8</td>"
        + "</tr>"
        + "</table>"
        + "after"
        + "</body></html>";
    // @formatter:off

    getText("before header1 header2 data1 data2 data3 data4 data5 data6 data7 data8 after", tmpHtmlCode);
  }

  @Test
  public void getText_OrderedList() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<ol>"
        + "  <li>Line1"
        + "  <li>Line2"
        + "  <li> Line3"
        + "  <li>Line4 "
        + "</ol>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before 1. Line1 2. Line2 3. Line3 4. Line4 after", tmpHtmlCode);
  }

  @Test
  public void getText_UnorderedList() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<ul>"
        + "  <li>Line1"
        + "  <li>Line2"
        + "  <li> Line3"
        + "  <li>Line4 "
        + "</ul>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Line1 Line2 Line3 Line4 after", tmpHtmlCode);
  }

  @Test
  public void getText_DefinitionList() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<dl>"
        + "  <dt>Term 1</dt>"
        + "  <dd>Definition 1</dd>"
        + "  <dt>Term 2.1</dt>"
        + "  <dt> Term 2.2</dt>"
        + "  <dt>Term 2.3 </dt>"
        + "  <dt>Term 2.4</dt>"
        + "  <dd>Defintion 2.1</dd>"
        + "  <dd> Defintion 2.2</dd>"
        + "  <dd>Defintion 2.3 </dd>"
        + "  <dd>Defintion 2.4</dd>"
        + "</dl>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText(
        "before Term 1 Definition 1 Term 2.1 Term 2.2 Term 2.3 Term 2.4 Defintion 2.1 Defintion 2.2 Defintion 2.3 Defintion 2.4 after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Image() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<img src='src.img'>"
        + "between"
        + "<img src='src.img' alt='test image1'>"
        + "between"
        + "<img src='src.img' title='test image2'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before between test image1 between after", tmpHtmlCode);
  }

  @Test
  public void getText_SelectEmpty() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<select>"
        + "</select>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_SelectWithOptions() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<select>"
        + "<option value='o_1'>color 1</option>"
        + "<option value='o_2'>color 2</option>"
        + "<option value='o_3' selected>color 3</option>"
        + "<option value='o_4'> color 4</option>"
        + "<option value='o_5'>color 5 </option>"
        + "</select>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before color 1 color 2 color 3 color 4 color 5 after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_SelectWithOptgroup() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<select>"
        + "<option value='o_1'>color 1</option>"
        + "<optgroup label='group 1'>"
        + "<option value='o_2'>color 2</option>"
        + "</optgroup>"
        + "<optgroup label=' group 2'>"
        + "<option value='o_3'>color 3</option>"
        + "</optgroup>"
        + "<optgroup label='group 3'>"
        + "<option value='o_4'>color 4</option>"
        + "</optgroup>"
        + "<option value='o_5'>color 5</option>"
        + "</select>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before color 1 group 1 color 2 group 2 color 3 group 3 color 4 color 5 after", "before after",
        tmpHtmlCode);
  }

  @Test
  public void getText_InputText() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='text' value=''>"
        + "between"
        + "<input type='text' value='v 2'>"
        + "between"
        + "<input type='text' value='v 3' placeholder='p 3'>"
        + "between"
        + "<input type='text' value='' placeholder='p 4'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenv 2betweenv 3betweenp 4after", "beforebetweenbetweenbetweenafter", tmpHtmlCode);
  }

  @Test
  public void getText_InputPassword() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='password' value=''>"
        + "between"
        + "<input type='password' value='v 2'>"
        + "between"
        + "<input type='password' value='v 3' placeholder='p 3'>"
        + "between"
        + "<input type='password' value='' placeholder='p 4'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenv 2betweenv 3betweenp 4after", "beforebetweenbetweenbetweenafter", tmpHtmlCode);
  }

  @Test
  public void getText_InputFile() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='file' value=''>"
        + "between"
        + "<input type='file' value='v 2'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenafter", "beforebetweenafter", tmpHtmlCode);
  }

  @Test
  public void getText_TextArea() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<textarea></textarea>"
        + "between"
        + "<textarea>v 2</textarea>"
        + "between"
        + "<textarea placeholder='p 3'>v 3</textarea>"
        + "between"
        + "<textarea placeholder='p 4'></textarea>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenv 2betweenv 3betweenp 4after", "beforebetweenbetweenbetweenafter", tmpHtmlCode);
  }

  @Test
  public void getText_InputImage() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='image' src='src.img'>"
        + "between"
        + "<input type='image' src='src.img' alt='test image1'>"
        + "between"
        + "<input type='image' src='src.img' title='test image2'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before between test image1 between after", "before between between after", tmpHtmlCode);
  }

  @Test
  public void getText_InputSubmit() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='submit'>"
        + "<input type='submit' value='Click Me'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Click Me after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_InputReset() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='reset'>"
        + "<input type='reset' value='Click Me'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Click Me after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_InputButton() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='button'>"
        + "<input type='button' value='Click Me'>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Click Me after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_Button() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<button></button>"
        + "<button>Click Me</button>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Click Me after", "before after", tmpHtmlCode);
  }

  @Test
  public void getText_InputRadio() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='radio'>"
        + "<input type='radio' value='value'>"
        + "<input type='radio' checked>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before after", tmpHtmlCode);
  }

  @Test
  public void getText_InputCheckbox() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<input type='checkbox'>"
        + "<input type='checkbox' value='value'>"
        + "<input type='checkbox' checked>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before after", tmpHtmlCode);
  }

  @Test
  public void getText_LabelWithEnclosedInputRadio() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<label>LabelBefore"
        + "<input type='radio'>LabelAfter"
        + "</label>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before LabelBefore LabelAfter after", tmpHtmlCode);
  }

  @Test
  public void getText_LabelWithEnclosedSelect() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<label>LabelBefore"
        + "<select>"
        + "<option>Option1Value"
        + "<option>Option2Value"
        + "</select>"
        + "LabelAfter</label>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before LabelBefore Option1Value Option2Value LabelAfter after", "before LabelBefore LabelAfter after",
        tmpHtmlCode);
  }

  @Test
  public void getText_Script() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<script language='JavaScript' type='text/javascript'>"
        + "function foo() {}"
        + "</script>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforeafter", tmpHtmlCode);
  }

  @Test
  public void getText_Style() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<style type='text/css'>"
        + ".line {}"
        + "</style>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforeafter", tmpHtmlCode);
  }

  @Test
  public void getText_CSSDisplayBlock() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { display: block; } </style>"
        + "before"
        + "<span class='line'>Span 1</span>"
        + "between"
        + "<span class='line'>Span 2</span>"
        + "<span class='line'> Span 3</span>"
        + "<span class='line'>Span 4 </span>"
        + " <span class='line'>Span 5</span>"
        + "<span class='line'>Span 6</span> "
        + "<span class='line'>Span 7</span>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Span 1 between Span 2 Span 3 Span 4 Span 5 Span 6 Span 7 after", tmpHtmlCode);
  }

  @Test
  public void getText_CSSDisplayInline() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { display: inline; } </style>"
        + "before"
        + "<div class='line'>Division 1</div>"
        + "between"
        + "<div class='line'> Division 2</div>"
        + "between"
        + "<div class='line'>Division 3 </div>"
        + "between"
        + " <div class='line'>Division 4</div>"
        + "between"
        + "<div class='line'>Division 5</div> "
        + "between"
        + "<div class='line'>Division 6</div><div class='line'>Division 7</div>"
        + "between"
        + "<div class='line'>Division 8</div> <div class='line'>Division 9</div>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText(
        "beforeDivision 1between Division 2betweenDivision 3 between Division 4betweenDivision 5 betweenDivision 6Division 7betweenDivision 8 Division 9after",
        tmpHtmlCode);
  }

  @Test
  public void getText_CSSDisplayNone() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { display: none; } </style>"
        + "before"
        + "<div class='line'>Division 1</div>"
        + "between"
        + "<span class='line'>Span 1</span>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenafter", tmpHtmlCode);
  }

  @Test
  public void getText_CSSVisibilityHidden() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { visibility: hidden; } </style>"
        + "before"
        + "<div class='line'>Division 1</div>"
        + "between"
        + "<div class='line'>D<span>Span 1</span>d</div>"
        + "between"
        + "<div class='line'><span style='visibility: visible;'>DSpan 2</span></div>"
        + "between"
        + "<span class='line'>Span 1</span>"
        + "between"
        + "<span class='line'><div>SDivision 1</div></span>"
        + "between"
        + "<span class='line'>S<div style='visibility: visible;'>Division 2</div>s</span>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("beforebetweenbetweenDSpan 2betweenbetweenbetween Division 2 after", tmpHtmlCode);
  }

  @Test
  public void getText_CSSOpacity0() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<style type='text/css'> .line { opacity: 0; } </style>"
        + "before"
        + "<div class='line'>Division 1</div>"
        + "between"
        + "<span class='line'>Span 1</span>"
        + "after"
        + "</body></html>";
    // @formatter:on

    getText("before Division 1 betweenSpan 1after", tmpHtmlCode);
  }

  @Test
  public void getText_CSSTextTransform() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p style='text-transform: lowercase;'>LoWerCase</p>"
        + "<p style='text-transform: uppercase;'>uppErCase</p>"
        + "<p style='text-transform: capitalize;'>capiTalize</p>"
        + "<p style='text-transform: none;'>nOne</p>"
        + "<div style='text-transform: uppercase'><p>insideDiv</p></div>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);
    final String tmpExpected = "lowercase UPPERCASE CapiTalize nOne INSIDEDIV";
    assertEquals(tmpExpected, tmpResult.getText());
    assertEquals(tmpExpected, tmpResult.getTextWithoutFormControls());
  }

  @Test
  public void getText_SimplePage() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html>"
        + "<head>"
        + "<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
        + "<title>Page Title</title>"
        + "</head>"
        + "<body>"
        + "<p>Paragraph 1</p>"
        + "<p>Paragraph 2</p>"
        + "</body></html>";
    // @formatter:on

    getText("Paragraph 1 Paragraph 2", tmpHtmlCode);
  }

  @Test
  public void getText_Mix1() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>This t<font color='red'>ext</font> is <b>styled</b>.</p>"
        + "</body></html>";
    // @formatter:on

    getText("This text is styled.", tmpHtmlCode);
  }

  @Test
  public void getText_Mix2() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body><table><tr>"
        + "<td style='color:#222288'>Table C<font color='red'>lickable</font> <b>forma<i>ted</i> t</b>ext</td>"
        + "</tr></table></body></html>";
    // @formatter:on

    getText("Table Clickable formated text", tmpHtmlCode);
  }

  @Test
  public void getText_Mix3() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Fi<font color='red'>eld</font>4</p>"
        + "</p></body></html>";
    // @formatter:on

    getText("Field4", tmpHtmlCode);
  }

  @Test
  public void getText_AllControls() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>PageStart</p>"
        + "<form action='test'>"
        + "<p> </p>"
        + "<fieldset>"
        + "<legend id='idLegend'>LegendLabel</legend>"
        + "</fieldset>"
        + "<p> </p>"
        + "<label id='idLabel' for='TextInput'>LabelLabel</label>"
        + "<p> </p>"
        + "<input id='idTextInput' name='TextInput' type='text' value='inputValue'>"
        + "<p> </p>"
        + "<input id='idTextInput' name='TextInput' type='text' value='inputValue' placeholder='my placeholder'>"
        + "<p> </p>"
        + "<input id='idTextInput' name='TextInput' type='text' value='' placeholder='my placeholder'>"
        + "<p> </p>"
        + "<input name='PasswordInput' type='password' value='secretInputValue'>"
        + "<p> </p>"
        + "<input name='PasswordInput' type='password' value='secretInputValue' placeholder='my pwd placeholder'>"
        + "<p> </p>"
        + "<input name='PasswordInput' type='password' value='' placeholder='my pwd placeholder'>"
        + "<p> </p>"
        + "<input name='HiddenInput' type='hidden' value='hiddenInputValue'>"
        + "<p> </p>"
        + "<textarea name='TextArea'>textAreaValue</textarea>"
        + "<p> </p>"
        + "<input name='FileInput' type='file'>"
        + "<p> </p>"
        + "<select id='idSingleSelect' name='SingleSelect'>"
        + "<option selected>Option1Value"
        + "<option>Option2Value"
        + "</select>"
        + "<p> </p>"
        + "<select name='MultipleSelect' multiple>"
        + "  <option selected>Option1Value"
        + "  <option>Option2Value"
        + "  <option selected>Option3Value"
        + "</select>"
        + "<p> </p>"
        + "<select name='SingleOptgroupSelect'>"
        + "  <optgroup label='SingleOptgroupLabel1'>"
        + "    <option>SingleOptgroup1Option1Value"
        + "    <option>SingleOptgroup1Option2Value"
        + "    <option>SingleOptgroup1Option3Value"
        + "  </optgroup>"
        + "  <optgroup label='SingleOptgroupLabel2'>"
        + "    <option>SingleOptgroup2Option1Value"
        + "    <option selected>SingleOptgroup2Option2Value"
        + "    <option>SingleOptgroup2Option3Value"
        + "  </optgroup>"
        + "</select>"
        + "<p> </p>"
        + "<select name='MultipleOptgroupSelect' multiple>"
        + "  <optgroup label='MultipleOptgroupLabel1'>"
        + "    <option selected>MultipleOptgroup1Option1Value"
        + "    <option>MultipleOptgroup1Option2Value"
        + "    <option selected>MultipleOptgroup1Option3Value"
        + "  </optgroup>"
        + "  <optgroup label='MultipleOptgroupLabel2'>"
        + "    <option>MultipleOptgroup2Option1Value"
        + "    <option selected>MultipleOptgroup2Option2Value"
        + "    <option>MultipleOptgroup2Option3Value"
        + "  </optgroup>"
        + "</select>"
        + "<p> </p>"
        + "<input name='RadioInput' type='radio' value='radioInputValue1'>radioInputLabel1"
        + "<input name='RadioInput' type='radio' value='radioInputValue2' checked>radioInputLabel2"
        + "<p> </p>"
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue1' checked>checkboxInputLabel1"
        + "<input name='CheckboxInput' type='checkbox' value='checkboxInputValue2'>checkboxInputLabel2"
        + "<p> </p>"
        + "<button name='ButtonButton' type='button' value='buttonButtonValue'>buttonButtonLabel</button>"
        + "<p> </p>"
        + "<input name='ButtonInput' type='button' value='buttonInputValue'>"
        + "<p> </p>"
        + "<input name='SubmitInput' type='submit' value='submitInputValue'>"
        + "<p> </p>"
        + "<input name='ResetInput' type='reset' value='resetInputValue'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    // @formatter:off
    final String tmpExpected = "PageStart "
        + "LegendLabel "
        + "LabelLabel "
        + "inputValue "
        + "inputValue "
        + "my placeholder "
        + "secretInputValue "
        + "secretInputValue "
        + "my pwd placeholder "
        + "textAreaValue "
        + "Option1Value "
        + "Option2Value "
        + "Option1Value "
        + "Option2Value "
        + "Option3Value "
        + "SingleOptgroupLabel1 "
        + "SingleOptgroup1Option1Value "
        + "SingleOptgroup1Option2Value "
        + "SingleOptgroup1Option3Value "
        + "SingleOptgroupLabel2 "
        + "SingleOptgroup2Option1Value "
        + "SingleOptgroup2Option2Value "
        + "SingleOptgroup2Option3Value "
        + "MultipleOptgroupLabel1 "
        + "MultipleOptgroup1Option1Value "
        + "MultipleOptgroup1Option2Value "
        + "MultipleOptgroup1Option3Value "
        + "MultipleOptgroupLabel2 "
        + "MultipleOptgroup2Option1Value "
        + "MultipleOptgroup2Option2Value "
        + "MultipleOptgroup2Option3Value "
        + "radioInputLabel1 radioInputLabel2 "
        + "checkboxInputLabel1 checkboxInputLabel2 "
        + "buttonButtonLabel "
        + "buttonInputValue "
        + "submitInputValue "
        + "resetInputValue";
    // @formatter:on

    // @formatter:off
    final String tmpExpected2 = "PageStart "
        + "LegendLabel "
        + "LabelLabel "
        + "radioInputLabel1 radioInputLabel2 "
        + "checkboxInputLabel1 checkboxInputLabel2";
    // @formatter:on

    getText(tmpExpected, tmpExpected2, tmpHtmlCode);

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("PageStart", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLegend")));
    assertEquals("PageStart LegendLabel", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLabel")));

    assertEquals("inputValue", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idTextInput")));
    assertEquals("Option1Value Option2Value", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idSingleSelect")));

  }

  private void getIndex(final int anExpected, final String anHtmlCode) throws Exception {
    PageUtil.consumeHtmlPage(anHtmlCode, tmpHtmlPage -> {
      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

      assertEquals(anExpected, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementById("myID")));
    });
  }

  @Test
  public void getIndex_EmptyPage() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><head></head><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals(1, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertEquals(2, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertEquals(3, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getIndex_EmptyPageNoHead() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals(1, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertEquals(2, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertEquals(3, tmpResult.getIndex((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getIndex_Single() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(4, tmpHtmlCode);
  }

  @Test
  public void getIndex_AfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_AfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_AfterComment() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<!-- comment -->"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideElementAfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "before"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(6, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideElementAfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(6, tmpHtmlCode);
  }

  @Test
  public void getIndex_CSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='display: none;'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(4, tmpHtmlCode);
  }

  @Test
  public void getIndex_AfterCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_CSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='visibility: hidden;'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(4, tmpHtmlCode);
  }

  @Test
  public void getIndex_AfterCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  @Test
  public void getIndex_InsideCSSVisibilityHiddenButVisible() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID' style='visibility: visible;'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getIndex(5, tmpHtmlCode);
  }

  private void getPosition(final int aStartPos, final int anEndPos, final String anHtmlCode) throws Exception {
    PageUtil.consumeHtmlPage(anHtmlCode, tmpHtmlPage -> {
      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

      assertFindSpot(aStartPos, anEndPos, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementById("myID")));
    });
  }

  private void assertFindSpot(final int aStartPos, final int anEndPos, final FindSpot aFindSpot) {
    assertEquals(new FindSpot(aStartPos, anEndPos), aFindSpot);
  }

  @Test
  public void getPosition_EmptyPage() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><head></head><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getPosition_EmptyPageNoHead() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertFindSpot(0, 0, tmpResult.getPosition((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getPosition_Single() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_Single_WithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID'>text</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 4, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(6, 6, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterElementWithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>text</div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(4, 4, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterComment() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<!-- comment -->"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideElementAfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "before"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(6, 6, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideElementAfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideElementAfterElementWithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div>text</div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(4, 4, tmpHtmlCode);
  }

  @Test
  public void getPosition_CSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='display: none;'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_CSSDisplayNone_WithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='display: none;'>text</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterCSSDisplayNoneWithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'>text</div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_CSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='visibility: hidden;'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_CSSVisibilityHidden_WithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='visibility: hidden;'>text</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_AfterCSSVisibilityHiddenWithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>text</div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideCSSVisibilityHidden_WithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID'>text</div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideCSSVisibilityHiddenButVisible() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID' style='visibility: visible;'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 0, tmpHtmlCode);
  }

  @Test
  public void getPosition_InsideCSSVisibilityHiddenButVisible_WithText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID' style='visibility: visible;'>text</div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getPosition(0, 4, tmpHtmlCode);
  }

  private void getHierarchy(final String anExpected, final String anHtmlCode) throws Exception {
    PageUtil.consumeHtmlPage(anHtmlCode, tmpHtmlPage -> {
      final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

      assertEquals(anExpected, tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementById("myID")));
    });
  }

  @Test
  public void getHierarchy_EmptyPage() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><head></head><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("0>1", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertEquals("0>1>2", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertEquals("0>1>3", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getHierarchy_EmptyPageNoHead() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("0>1", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("html").get(0)));
    assertEquals("0>1>2", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("head").get(0)));
    assertEquals("0>1>3", tmpResult.getHierarchy((HtmlElement) tmpHtmlPage.getElementsByTagName("body").get(0)));
  }

  @Test
  public void getHierarchy_Single() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_AfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_AfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_AfterComment() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<!-- comment -->"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideElementAfterText() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "before"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>6", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideElementAfterElement() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<div></div>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>6", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_CSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='display: none;'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_AfterCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideCSSDisplayNone() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='display: none;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_CSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='myID' style='visibility: hidden;'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_AfterCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'></div>"
        + "<div id='myID'></div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideCSSVisibilityHidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>5", tmpHtmlCode);
  }

  @Test
  public void getHierarchy_InsideCSSVisibilityHiddenButVisible() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div style='visibility: hidden;'>"
        + "<div id='myID' style='visibility: visible;'></div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on

    getHierarchy("0>1>3>4>5", tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
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

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("before some button text",
        tmpHtmlPageIndex.getLabelingTextBefore(tmpHtmlPage.getHtmlElementById("myImg"), 0));
  }

  @Test
  public void getLabelingTextAfter_None() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_AtEnd() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_IgnoreHidden() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input value='hiddenValue' type='hidden'>part2"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBoxpart2",
        tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_IgnoreAfterForm() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "</form>"
        + "<p>MoreText</p>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_UntilNext() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox"
        + "<input name='MyOtherCheckboxName' value='value2' type='checkbox'>CheckBox2"
        + "</form>"
        + "<p>MoreText</p>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("CheckBox", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("MyCheckboxId")));
  }

  @Test
  public void getLabelingTextAfter_InsideButton() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='MyButton' type='button'>before<img id='myImg'>some button text</button>after"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("some button text after",
        tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("myImg")));
  }

  @Test
  public void getLabelingTextAfter_ButtonAfterDirect() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myImg'><button id='MyButton' type='button'>some button text</button>after"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("", tmpHtmlPageIndex.getLabelingTextAfter(tmpHtmlPage.getHtmlElementById("myImg")));
  }

  @Test
  public void getAsText_Table() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
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
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

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
  public void getAsText_OrderedList() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<ol id='idOl'>"
        + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2"
        + "</ol>"
        + "after"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("1. Line1 2. Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idOl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idOl")));

    assertEquals("1. Line1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi1")));

    assertEquals("2. Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi2")));
    assertEquals("before 1. Line1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi2")));
  }

  @Test
  public void getAsText_UnorderedList() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "before"
        + "<ul id='idUl'>"
        + "  <li id='idLi1'>Line1"
        + "  <li id='idLi2'>Line2"
        + "</ul>"
        + "after"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);

    final HtmlPageIndex tmpResult = new HtmlPageIndex(tmpHtmlPage);

    assertEquals("Line1 Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idUl")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idUl")));

    assertEquals("Line1", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi1")));
    assertEquals("before", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi1")));

    assertEquals("Line2", tmpResult.getAsText(tmpHtmlPage.getHtmlElementById("idLi2")));
    assertEquals("before Line1", tmpResult.getTextBefore(tmpHtmlPage.getHtmlElementById("idLi2")));
  }

  @Test
  public void hasMouseActionListener_not() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_byOnEvent() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_byAddEventListener() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId'>some text</span>"
        + "<script>"
        + "document.getElementById('myId').addEventListener('click', function() { alert('clicked'); });"
        + "</script>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_byJSOnEvent() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId'>some text</span>"
        + "<script>"
        + "document.getElementById('myId').onclick = function() { alert('clicked'); };"
        + "</script>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_child_actionFromParent() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span onclick='alert(\"clicked\");'><span id='myId'>some text</span></span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_child_actionFromSelfAndParent() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span onclick='alert(\"clicked\");'><span id='myId' onmouseover='alert(\"clicked\");'>some text</span></span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.MOUSE_OVER, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_child_multipleActionsFromParents() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span onclick='alert(\"clicked\");'><span onmouseover='alert(\"clicked\");'><span id='myId'>some text</span></span></span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.MOUSE_OVER, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_child_noActionFromHtml() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html onclick='alert(\"clicked\");'><body>"
        + "<span id='myId'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_child_noActionFromBody() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body onclick='alert(\"clicked\");'>"
        + "<span id='myId'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_onClick() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_onMouseDown() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmousedown='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_onMouseUp() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseup='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_button() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<button id='myId'>some text</button>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_button_child() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<button><span id='myId'>some text</span></button>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_anchor() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId'>some text</a>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_anchor_child() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a><span id='myId'>some text</span></a>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_anchorWithHref() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' href='#'>some text</a>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertFalse(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_click_anchorWithHref_child() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a href='#'><span id='myId'>some text</span></a>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickDouble_onDblClick() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' ondblclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_DOUBLE, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickDouble_onClick() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_DOUBLE, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickDouble_onMouseDown() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmousedown='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_DOUBLE, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickDouble_onMouseUp() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseup='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_DOUBLE, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickRight_onContextMenu() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' oncontextmenu='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_RIGHT, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickRight_onMouseDown() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmousedown='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_RIGHT, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_clickRight_onMouseUp() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseup='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(
        tmpHtmlPageIndex.hasMouseActionListener(MouseAction.CLICK_RIGHT, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_mouseOver_onMouseOver() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseover='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.MOUSE_OVER, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_mouseOver_onMouseMove() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmousemove='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.MOUSE_OVER, tmpHtmlPage.getHtmlElementById("myId")));
  }

  @Test
  public void hasMouseActionListener_mouseOver_onMouseOut() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseout='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    assertTrue(tmpHtmlPageIndex.hasMouseActionListener(MouseAction.MOUSE_OVER, tmpHtmlPage.getHtmlElementById("myId")));
  }
}