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


package org.wetator.backend.htmlunit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.htmlunit.BrowserVersion;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.util.NormalizedString;

/**
 * @author rbri
 */
public class XHtmlOutputterXHtmlPageTest {

  private static final String LEADING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
  private static final String TRAILING = "\n</html>\n";

  // @formatter:off
  private static final String EXPECTED_LEADING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> "
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"> <!-- Browser URL: https://www.htmlunit.org/dummy.html --> "
      + "<head> "
      + "<script src='../../resources/" + XHtmlOutputter.JQUERY + "'></script> "
      + "<script src='../../resources/" + XHtmlOutputter.JQUERY_COLOR + "'></script> "
      + "<script src='../../resources/wetator_report.js'></script> "
      + "</head>";
  // @formatter:n

      private static final String EXPECTED_TRAILING = " </html>";

  private void testXHtmlOutput(final String anExpected, final String anXHtmlCode) throws IOException {
    PageUtil.consumeXHtmlPage(BrowserVersion.CHROME, anXHtmlCode, tmpXHtmlPage -> {
      final XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      final StringWriter tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);
      assertEquals(anExpected, new NormalizedString(tmpWriter.toString()).toString());
    });

    PageUtil.consumeXHtmlPage(BrowserVersion.FIREFOX_ESR, anXHtmlCode, tmpXHtmlPage -> {
      final XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      final StringWriter tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);
      assertEquals(anExpected, new NormalizedString(tmpWriter.toString()).toString());
    });
  }

  @Test
  public void testSimple() throws IOException {
    final String tmpXHtmlCode = LEADING + TRAILING;
    final String tmpExpected = EXPECTED_LEADING
        + "<body style=\"display: block\"> <script> highlight(); </script> </body>" + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpXHtmlCode);
  }

  @Test
  public void testSimpleWithJavascript() throws IOException {
    final String tmpXHtmlCode = LEADING + "<body><h1>Test</h1>"
        + "<script type=\"text/javascript\">alert('WETATOR');</script></body>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING
        + "<body style=\"display: block\"> <h1 style=\"display: block\">Test</h1> <script> highlight(); </script> </body>"
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpXHtmlCode);
  }

  @Test
  public void specialChars() throws IOException {
    final String tmpHtmlCode = LEADING + "<h1>1&#160;2</h1>" + TRAILING;
    final String tmpExpected = EXPECTED_LEADING
        + "<body style=\"display: block\"> <h1 style=\"display: block\">1&#160;2</h1> <script> highlight(); </script> </body>"
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpHtmlCode);
  }

  @Test
  public void select() throws IOException {
    // @formatter:off
    final String tmpHtmlCode =
            LEADING
            + "<select>"
              + "<option id='tst'>opt1</option>"
              + "<option selected>opt2</option>"
            + "</select>"
            + TRAILING;
    // @formatter:on

    PageUtil.consumeXHtmlPage(BrowserVersion.CHROME, tmpHtmlCode, tmpXHtmlPage -> {
      XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      StringWriter tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);

    // @formatter:off
    String tmpExpected =
            EXPECTED_LEADING
            + "<body style=\"display: block\"> "
              + "<select style=\"display: inline-block\"> "
                + "<option id=\"tst\">opt1</option> "
                + "<option selected=\"selected\">opt2</option> "
              + "</select> "
            + "<script> highlight(); </script> "
            + "</body>"
            + EXPECTED_TRAILING;
    // @formatter:on
      assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());

      final HtmlOption tmpOption = (HtmlOption) tmpXHtmlPage.getElementById("tst");
      tmpOption.setSelected(true);

      tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);

    // @formatter:off
    tmpExpected =
            EXPECTED_LEADING
            + "<body style=\"display: block\"> "
              + "<select style=\"display: inline-block\"> "
                + "<option selected=\"selected\" id=\"tst\">opt1</option> "
                + "<option>opt2</option> "
              + "</select> "
            + "<script> highlight(); </script> "
            + "</body>"
            + EXPECTED_TRAILING;
    // @formatter:on
      assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
    });
  }

  @Test
  public void radio() throws IOException {
    // @formatter:off
    final String tmpHtmlCode =
            LEADING
              + "<form>"
                + "<input id=\"tst\" type=\"radio\" name=\"gender\" value=\"male\"> Male"
                + "<input type=\"radio\" name=\"gender\" value=\"female\" checked> Female"
              + "</form>"
            + TRAILING;
    // @formatter:on

    PageUtil.consumeXHtmlPage(BrowserVersion.CHROME, tmpHtmlCode, tmpXHtmlPage -> {
      XHtmlOutputter tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      StringWriter tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);

    // @formatter:off
    String tmpExpected =
        EXPECTED_LEADING
        + "<body style=\"display: block\"> "
          + "<form style=\"display: block\" onsubmit=\"return false;\"> "
            + "<input id=\"tst\" type=\"radio\" name=\"gender\" value=\"male\" style=\"display: inline-block\"/> Male "
            + "<input checked=\"checked\" type=\"radio\" name=\"gender\" value=\"female\" style=\"display: inline-block\"/> Female "
          + "</form> "
        + "<script> highlight(); </script> "
        + "</body>"
        + EXPECTED_TRAILING;
    // @formatter:on
      // assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());

      final HtmlRadioButtonInput tmpRadio = (HtmlRadioButtonInput) tmpXHtmlPage.getElementById("tst");
      tmpRadio.setChecked(true);

      tmpXHtmlOutputter = new XHtmlOutputter(tmpXHtmlPage, null);
      tmpWriter = new StringWriter();
      tmpXHtmlOutputter.writeTo(tmpWriter);

    // @formatter:off
    tmpExpected =
        EXPECTED_LEADING
        + "<body style=\"display: block\"> "
          + "<form style=\"display: block\" onsubmit=\"return false;\"> "
            + "<input checked=\"checked\" id=\"tst\" name=\"gender\" type=\"radio\" value=\"male\" style=\"display: inline-block\"/> Male "
            + "<input name=\"gender\" type=\"radio\" value=\"female\" style=\"display: inline-block\"/> Female "
          + "</form> "
        + "<script> highlight(); </script> "
        + "</body>"
        + EXPECTED_TRAILING;
    // @formatter:on
      assertEquals(tmpExpected, new NormalizedString(tmpWriter.toString()).toString());
    });
  }

  @Test
  public void displayNone() throws IOException {
    final String tmpXHtmlCode = LEADING + "<div id='errorMessage' style=' display:none; width:290px;'>Wetator</div>"
        + TRAILING;
    final String tmpExpected = EXPECTED_LEADING
        + "<body style=\"display: block\"> <div id=\"errorMessage\" style=\"display: block; display:none; width:290px;\"> Wetator </div> <script> highlight(); </script> </body>"
        + EXPECTED_TRAILING;
    testXHtmlOutput(tmpExpected, tmpXHtmlCode);
  }
}
