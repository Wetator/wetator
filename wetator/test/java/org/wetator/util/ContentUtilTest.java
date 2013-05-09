/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author rbri
 */
public class ContentUtilTest {

  @Test
  public void getPdfContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    String tmpContent = ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedPdfContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.pdf 11165]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_pdf.zip"), "UTF-8", Locale.getDefault());
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsStringError() {
    try {
      ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"));
      org.junit.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: Error: Header doesn't contain versioninfo", e.toString());
    }
  }

  @Test
  public void getZippedPdfContentAsStringError() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_pdf_error.zip"), "UTF-8",
          Locale.getDefault());
      org.junit.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: Can't convert the zipped pdf 'wet_test.pdf' into text.",
          e.toString());
    }
  }

  @Test
  public void getRtfContentAsString() throws FileNotFoundException, IOException, BadLocationException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Wetator is great.");

    String tmpContent = ContentUtil.getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedRtfContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.rtf 83]");
    tmpExpected.append(" ");
    tmpExpected.append("Wetator is great.");

    String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_rtf.zip"), "UTF-8", Locale.getDefault());
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getRtfContentAsStringError() throws FileNotFoundException, IOException, BadLocationException {
    String tmpContent = ContentUtil.getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"));
    org.junit.Assert.assertEquals("", tmpContent);
  }

  @Test
  public void getXlsContentAsStringDE() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency * 4,33 €");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    String tmpContent = ContentUtil.getXlsContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"),
        Locale.GERMAN);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsContentAsStringEN() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14.3");
    tmpExpected.append(" float (rounded) 1.70");
    tmpExpected.append(" currency * 4.33 €");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124.70");

    String tmpContent = ContentUtil.getXlsContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"),
        Locale.ENGLISH);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedXlsContentAsStringDE() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.xls 15360]");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency * 4,33 €");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_xls.zip"), "UTF-8", Locale.GERMAN);
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getXlsContentAsStringError() {
    try {
      ContentUtil.getXlsContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), Locale.getDefault());
      org.junit.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: "
          + "Invalid header signature; read 0x342E312D46445025, expected 0xE11AB1A1E011CFD0", e.toString());
    }
  }

  @Test
  public void getZippedXlsContentAsStringError() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_xls_error.zip"), "UTF-8",
          Locale.getDefault());
      org.junit.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: Can't convert the zipped xls 'wet_test.xls' into text.",
          e.toString());
    }
  }

  @Test
  public void getTxtContentAsString() {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Some content line two Hallo Wetator.");

    String tmpContent = ContentUtil.getTxtContentAsString("Some content\rline two\r\n\tHallo\tWetator.");
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZippedTxtContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[download.txt 79]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple text file. ");
    tmpExpected.append("This file is used to test WeT.");

    String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_txt.zip"), "UTF-8", Locale.getDefault());
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream() throws IOException {
    InputStream tmpInput = new FileInputStream("test/webpage/download/wet_test.pdf");
    String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, "UTF-8");
    org.junit.Assert.assertEquals(4004, tmpContent.length());
    org.junit.Assert.assertTrue(tmpContent, tmpContent.startsWith("%PDF-1.4"));

    tmpInput = new FileInputStream("test/webpage/download/wet_test.xls");
    tmpContent = ContentUtil.getTxtContentAsString(tmpInput, "UTF-8");
    org.junit.Assert.assertEquals(4004, tmpContent.length());

    StringWriter tmpWriter = new StringWriter();
    for (int i = 0; i < 44; i++) {
      tmpWriter.append("0123456789");
    }
    tmpInput = new ByteArrayInputStream(tmpWriter.toString().getBytes("UTF-8"));
    tmpContent = ContentUtil.getTxtContentAsString(tmpInput, "UTF-8");
    org.junit.Assert.assertEquals(440, tmpContent.length());
    org.junit.Assert.assertTrue(tmpContent, tmpContent.startsWith("0123456789"));
  }

  @Test
  public void isTxt() throws FileNotFoundException, IOException {
    String tmpText = "Some readable text for testing WETATOR.";
    org.junit.Assert.assertTrue(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.pdf"));
    org.junit.Assert.assertFalse(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.xls"));
    org.junit.Assert.assertFalse(ContentUtil.isTxt(tmpText));
  }

  @Test
  public void getZippedContentAsStringMixedContent() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[wet_test.csv 24]");
    tmpExpected.append(" ");
    tmpExpected.append("Col1, Col2 text1, text2");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.pdf 11165]");
    tmpExpected.append(" ");
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.xls 15360]");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");
    tmpExpected.append(" ");
    tmpExpected.append("[Data Test]");
    tmpExpected.append(" String plain text");
    tmpExpected.append(" String(int) 4711");
    tmpExpected.append(" int 123");
    tmpExpected.append(" float 14,3");
    tmpExpected.append(" float (rounded) 1,70");
    tmpExpected.append(" currency * 4,33 €");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    tmpExpected.append(" ");
    tmpExpected.append("[wet_test.xml 83]");
    tmpExpected.append(" ");
    tmpExpected.append("<?xml version=\"1.0\"?> <wetator> <Test>Simple xml content</Test> </wetator>");

    String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_mix.zip"), "UTF-8", Locale.getDefault());
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void determineLocaleFromRequestNull() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(null);
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de");
    org.junit.Assert.assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest2() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("DE");
    org.junit.Assert.assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestEmpty() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("");
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestUnknown() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("1234");
    org.junit.Assert.assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestMany() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da,en");
    org.junit.Assert.assertEquals(new Locale("da", ""), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestOperaStyle() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da;1.0,en;0.9");
    org.junit.Assert.assertEquals(new Locale("da", ""), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-nz,de;q=0.5");
    org.junit.Assert.assertEquals(new Locale("en", "NZ"), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle2() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-us");
    org.junit.Assert.assertEquals(new Locale("en", "US"), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequestIEStyle3() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-gb");
    org.junit.Assert.assertEquals(new Locale("en", "GB"), tmpLocale);
  }
}
