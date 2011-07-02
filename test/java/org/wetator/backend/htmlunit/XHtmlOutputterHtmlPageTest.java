/*
 * Copyright (c) 2008-2011 wetator.org
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
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.util.NormalizedString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class XHtmlOutputterHtmlPageTest {

  private static final String LEADING = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
      + "<html>\n<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"></head>\n";
  private static final String TRAILING = "\n</html>\n";

  private static final String EXPECTED_LEADING = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> "
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> "
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"> "
      + "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/> </head> ";
  private static final String EXPECTED_TRAILING = " </html>";

  @Test
  public void testSimple() throws IOException {
    String tmpHtmlCode = LEADING + TRAILING;
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpHtmlPage, null);

    StringWriter tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);

    String tmpExpected = EXPECTED_LEADING + "<body> </body>" + EXPECTED_TRAILING;
    Assert.assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
  }

  @Test
  public void testSimpleWithJavascript() throws IOException {
    String tmpHtmlCode = LEADING + "<body><h1>Test</h1>"
        + "<script type=\"text/javascript\">alert('WETATOR');</script></body>" + TRAILING;

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpHtmlPage, null);
    StringWriter tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);

    String tmpExpected = EXPECTED_LEADING + "<body> <h1>Test</h1> </body>" + EXPECTED_TRAILING;
    Assert.assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
  }

  @Test
  public void testSpecialChars() throws IOException {
    String tmpHtmlCode = LEADING
        + "<body><h1>&#956;g 1&ensp;2&emsp;3&thinsp;4</h1><ul><li>&#956;g</ul><select><option value='&#956;g'>&#956;g</option></body>"
        + TRAILING;

    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpHtmlPage, null);
    StringWriter tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);

    String tmpExpected = EXPECTED_LEADING + "<body> " + "<h1>&#956;g 1&#8194;2&#8195;3&#8201;4</h1>"
        + " <ul> <li> &#956;g </li> </ul> " + "<select> <option selected value=\"&#956;g\">&#956;g</option> </select> "
        + "</body>" + EXPECTED_TRAILING;
    Assert.assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
  }
}
