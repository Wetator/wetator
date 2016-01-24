/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.backend.htmlunit;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.util.NormalizedString;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class XHtmlOutputterHtmlPageTest {

  private static final String LEADING = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
      + "<html>\n<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head><body>\n";
  private static final String TRAILING = "\n</body></html>\n";

  private static final String EXPECTED_LEADING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> "
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"> " + "<head> "
      + "<script src='../../resources/jquery-1.10.2.min.js'></script> "
      + "<script src='../../resources/wetator_report.js'></script> "
      + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/></head><body>";
  private static final String EXPECTED_TRAILING = "<script> highlight(); </script> </body> </html>";

  private void testXHtmlOutput(final String anExpected, final String anHtmlCode) throws IOException {
    BrowserVersion tmpBrowser = BrowserVersion.INTERNET_EXPLORER_11;
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpBrowser, anHtmlCode);
    XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpHtmlPage, null);
    StringWriter tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);
    Assert.assertEquals(tmpBrowser.getApplicationName(), anExpected,
        new NormalizedString(tmpWriter.toString()).toString());

    tmpBrowser = BrowserVersion.FIREFOX_38;
    tmpHtmlPage = PageUtil.constructHtmlPage(tmpBrowser, anHtmlCode);
    tmpXHtmlOutputter = new XHtmlOutputter(tmpHtmlPage, null);
    tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);
    Assert.assertEquals(tmpBrowser.getApplicationName(), anExpected,
        new NormalizedString(tmpWriter.toString()).toString());
  }

  @Test
  public void emptyPage() throws IOException {
    final String tmpHtmlCode = LEADING + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void simplePage() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>Paragraph 1</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>Paragraph 1</p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void paragraph() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>Paragraph 1</p><p>Paragraph 2</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>Paragraph 1</p> <p>Paragraph 2</p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void font() throws IOException {
    final String tmpHtmlCode = LEADING + "<p><font color='red'>red</font> <font color='green'>green</font></p>"
        + TRAILING;
    final String tmpExpected = EXPECTED_LEADING
        + " <p><font color=\"red\">red</font> <font color=\"green\">green</font></p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void span() throws IOException {
    final String tmpHtmlCode = LEADING + "<p><span> 17.11 </span> mg" + "</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p><span> 17.11 </span> mg</p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void formatting() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>" + "<b>1</b> <big>2</big> <em>3</em><i>4</i> <small>5</small> "
        + "<strong>6</strong> <sub>7</sub> <sup>8</sup> <ins>9</ins> <del>10</del>" + "</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>" + "<b>1</b> <big>2</big> <em>3</em><i>4</i> <small>5</small> "
        + "<strong>6</strong> <sub>7</sub> <sup>8</sup> <ins>9</ins> <del>10</del>" + "</p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void twoImages() throws IOException {
    final String tmpHtmlCode = LEADING + "<p><img/><img/></p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p><img/><img/></p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  @Ignore
  public void computerOutput() throws IOException {
    // TODO different Results in IE and FF

    final String tmpHtmlCode = LEADING + "<p>"
        + "<code>1</code> <kbd>2</kbd> <samp>3</samp> <tt>4</tt> <var>5</var> <pre>6</pre>" + "</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>"
        + "<code>1</code> <kbd>2</kbd> <samp>3</samp> <tt>4</tt> <var>5</var> <pre>6</pre>" + "</p> "
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void citationQuotationDefinition() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>"
        + "<abbr title='a'>1</abbr> <acronym title='b'>2</acronym> <q>3</q> <cite>4</cite> <dfn>5</dfn>" + "</p>"
        + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>"
        + "<abbr title=\"a\">1</abbr> <acronym title=\"b\">2</acronym> <q>3</q> <cite>4</cite> <dfn>5</dfn>" + "</p> "
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void mix() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>This t<font color='red'>ext</font> is <b>styled</b>.</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>This t<font color=\"red\">ext</font> is <b>styled</b>.</p> "
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  @Ignore
  public void mix2() throws IOException {
    // TODO different Results in IE and FF

    final String tmpHtmlCode = LEADING + "<table><tr>"
        + "<td>Table C<font color='red'>lickable</font> <b>forma<i>ted</i> t</b>ext</td>" + "</tr></table>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <table> <tbody align=\"left\"> <tr> "
        + "<td> Table C <font color=\"red\">lickable</font> <b>forma<i>ted</i> t</b>ext </td> "
        + "</tr> </tbody> </table> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void mix3() throws IOException {
    final String tmpHtmlCode = LEADING + "<p>Fi<font color='red'>eld</font>4</p>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <p>Fi<font color=\"red\">eld</font>4</p> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void simpleWithJavascript() throws IOException {
    final String tmpHtmlCode = LEADING + "<h1>Test</h1>"
        + "<script type=\"text/javascript\">alert('WETATOR');</script>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <h1>Test</h1> " + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void specialChars() throws IOException {
    final String tmpHtmlCode = LEADING
        + "<h1>&#956;g 1&nbsp;2&#160;3&ensp;4&emsp;5&thinsp;6</h1><ul><li>&#956;g</ul><select><option value='&#956;g'>&#956;g</option>"
        + TRAILING;
    final String tmpExpected = EXPECTED_LEADING + " <h1>&#956;g 1&#160;2&#160;3&#8194;4&#8195;5&#8201;6</h1>"
        + " <ul> <li> &#956;g </li> </ul> " + "<select> <option selected value=\"&#956;g\">&#956;g</option> </select>"
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }
}
