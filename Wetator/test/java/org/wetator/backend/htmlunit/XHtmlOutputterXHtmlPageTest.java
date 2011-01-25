/*
 * Copyright (c) 2008-2011 www.wetator.org
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
public class XHtmlOutputterXHtmlPageTest {

  private static final String TRAILING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
  private static final String LEADING = "\n</html>\n";

  private static final String EXPECTED_TRAILING = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> "
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> "
      + "<!-- org doctype <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> --> "
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"> <body> ";
  private static final String EXPECTED_LEADING = "</body> </html>";

  @Test
  public void testSimple() throws IOException {
    String tmpXHtmlCode = TRAILING + LEADING;
    HtmlPage tmpXHtmlPage = PageUtil.constructXHtmlPage(tmpXHtmlCode);

    XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);

    StringWriter tmpWriter = new StringWriter();
    tmpXHtmlOutputter.writeTo(tmpWriter);

    String tmpExpected = EXPECTED_TRAILING + EXPECTED_LEADING;
    Assert.assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
  }

  // @Test public void testSimpleWithJavascript() throws IOException {
  // String tmpXHtmlCode = TRAILING + "<body><h1>Test</h1>"
  // + "<script type=\"text/javascript\"><![CDATA[ ]]></script></body>" + LEADING;
  // System.out.println(tmpXHtmlCode);
  // HtmlPage tmpXHtmlPage = PageUtil.constructXHtmlPage(tmpXHtmlCode);
  // XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
  // StringWriter tmpWriter = new StringWriter();
  // tmpXHtmlOutputter.writeTo(tmpWriter);
  //
  // String tmpExpected = EXPECTED_TRAILING + EXPECTED_LEADING;
  // Assert.assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
  // }
}
