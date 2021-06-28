/*
 * Copyright (c) 2008-2021 wetator.org
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

/**
 * Test for {@link ContentUtil}.
 *
 * @author rbri
 * @author frank.danek
 */
public class ContentUtilTest {

  @Test
  public void getTxtContentAsString_null() {
    final String tmpContent = ContentUtil.getTxtContentAsString(null, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getTxtContentAsString_empty() {
    final String tmpContent = ContentUtil.getTxtContentAsString("", 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getTxtContentAsString() {
    final String tmpContent = ContentUtil.getTxtContentAsString(" Some  content\rline two\r\n\tHallo\tWetator. ", 37);
    assertEquals("Some content line two Hallo Wetator.", tmpContent);
  }

  @Test
  public void getTxtContentAsString_equalToMaxLength() {
    final String tmpContent = ContentUtil.getTxtContentAsString(" Some  content\rline two\r\n\tHallo\tWetator. ", 36);
    assertEquals("Some content line two Hallo Wetator.", tmpContent);
  }

  @Test
  public void getTxtContentAsString_greaterThanMaxLength() {
    final String tmpContent = ContentUtil.getTxtContentAsString(" Some  content\rline two\r\n\tHallo\tWetator. ", 35);
    assertEquals("Some content line two Hallo Wetator ...", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_null() throws IOException {
    final String tmpContent = ContentUtil.getTxtContentAsString(null, StandardCharsets.UTF_8, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_empty() throws IOException {
    final InputStream tmpInput = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

    final String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream() throws IOException {
    final InputStream tmpInput = new ByteArrayInputStream(
        " Some  content\rline two\r\n\tHallo\tWetator. ".getBytes(StandardCharsets.UTF_8));

    final String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 37);
    assertEquals("Some content line two Hallo Wetator.", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_equalToMaxLength() throws IOException {
    final InputStream tmpInput = new ByteArrayInputStream(
        " Some  content\rline two\r\n\tHallo\tWetator. ".getBytes(StandardCharsets.UTF_8));

    final String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 36);
    assertEquals("Some content line two Hallo Wetator.", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_greaterThanMaxLength() throws IOException {
    final InputStream tmpInput = new ByteArrayInputStream(
        " Some  content\rline two\r\n\tHallo\tWetator. ".getBytes(StandardCharsets.UTF_8));

    final String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 35);
    assertEquals("Some content line two Hallo Wetator ...", tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_big() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder();
    for (int i = 0; i < 150; i++) {
      tmpExpected.append("0123456789");
    }

    final InputStream tmpInput = new ByteArrayInputStream(tmpExpected.toString().getBytes(StandardCharsets.UTF_8));

    final String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getTxtContentAsStringFromStream_notText() throws IOException {
    InputStream tmpInput = new FileInputStream("test/webpage/download/wet_test.pdf");
    String tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    assertEquals(4004, tmpContent.length());
    assertTrue(tmpContent, tmpContent.startsWith("%PDF-1.4"));

    tmpInput = new FileInputStream("test/webpage/download/wet_test.xls");
    tmpContent = ContentUtil.getTxtContentAsString(tmpInput, StandardCharsets.UTF_8, 4000);
    assertEquals(4004, tmpContent.length());
  }

  @Test
  public void getPdfContentAsString_null() throws IOException {
    final String tmpContent = ContentUtil.getPdfContentAsString(null, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getPdfContentAsString() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("This is the content of a simple PDF file.")
        .append(" ")
        .append("This file is used to test WeT.");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), 73);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsString_equalToMaxLength() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("This is the content of a simple PDF file.")
        .append(" ")
        .append("This file is used to test WeT.");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), 72);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsString_greaterThanMaxLength() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("This is the content of a simple PDF file.")
        .append(" ")
        .append("This file is used to test WeT ...");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), 71);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getPdfContentAsString_encryptedReadable() throws FileNotFoundException, IOException {
    final String tmpContent = ContentUtil
        .getPdfContentAsString(new FileInputStream("test/webpage/download/not_editable.pdf"), 40);
    assertEquals("WETATOR", tmpContent);
  }

  @Test
  public void getPdfContentAsString_encryptedNotReadable() {
    try {
      System.out.println(ContentUtil
          .getPdfContentAsString(new FileInputStream("test/webpage/download/can_not_extract_content.pdf"), 40));
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals("Content extraction forbidden for the given PDF document.", e.getMessage());
    }
  }

  @Test
  public void getPdfContentAsString_error() {
    try {
      ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals("java.io.IOException: Error: Header doesn't contain versioninfo", e.toString());
    }
  }

  @Test
  public void getPdfTitleAsString_null() throws IOException {
    final String tmpTitle = ContentUtil.getPdfTitleAsString(null);
    assertEquals("", tmpTitle);
  }

  @Test
  public void getPdfTitleAsString_empty() throws IOException {
    final String tmpTitle = ContentUtil.getPdfTitleAsString(new FileInputStream("test/webpage/download/wet_test.pdf"));
    assertEquals("", tmpTitle);
  }

  @Test
  public void getPdfTitleAsString() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/wet_test_title.pdf"));
    assertEquals("WETATOR Titel Test", tmpTitle);
  }

  @Test
  public void getPdfTitleAsString_encryptedReadable() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/not_editable.pdf"));
    assertEquals("WETATOR PDF Test", tmpTitle);
  }

  @Test
  public void getPdfTitleAsString_encryptedNotReadable() throws IOException {
    final String tmpTitle = ContentUtil
        .getPdfTitleAsString(new FileInputStream("test/webpage/download/can_not_extract_content.pdf"));
    assertEquals("WETATOR PDF Test", tmpTitle);
  }

  @Test
  public void getRtfContentAsString_null() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil.getRtfContentAsString(null, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getRtfContentAsString_empty() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil.getRtfContentAsString(new FileInputStream("test/webpage/download/empty.rtf"),
        4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getRtfContentAsString() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"), 18);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getRtfContentAsString_equalToMaxLength() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"), 17);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getRtfContentAsString_greaterThanMaxLength() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"), 16);
    assertEquals("Wetator is great ...", tmpContent);
  }

  @Test
  public void getRtfContentAsString_error() throws IOException, BadLocationException {
    final String tmpContent = ContentUtil
        .getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getWordContentAsString_null() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil.getWordContentAsString(null, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getWordContentAsString_docx_empty() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/empty.docx"), 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getWordContentAsString_docx() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.docx"), 18);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getWordContentAsString_docx_equalToMaxLength() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.docx"), 17);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getWordContentAsString_docx_greaterThanMaxLength() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.docx"), 16);
    assertEquals("Wetator is great ...", tmpContent);
  }

  @Test
  public void getWordContentAsString_doc_empty() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/empty.doc"),
        4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getWordContentAsString_doc() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.doc"), 18);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getWordContentAsString_doc_equalToMaxLength() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.doc"), 17);
    assertEquals("Wetator is great.", tmpContent);
  }

  @Test
  public void getWordContentAsString_doc_greaterThanMaxLength() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.doc"), 16);
    assertEquals("Wetator is great ...", tmpContent);
  }

  @Test
  public void getWordContentAsString_error() throws IOException {
    try {
      ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"), 4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException e) {
      assertEquals(
          "org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was neither an OLE2 stream, nor an OOXML stream, found type: RTF",
          e.toString());
    }

    try {
      ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), 4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException e) {
      assertEquals(
          "org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was not a Word document",
          e.toString());
    }

    try {
      ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), 4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException e) {
      assertEquals(
          "org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was not a Word document",
          e.toString());
    }

    try {
      ContentUtil.getWordContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), 4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException e) {
      assertEquals(
          "org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was neither an OLE2 stream, nor an OOXML stream, found type: PDF",
          e.toString());
    }
  }

  @Test
  public void getExcelContentAsString_null() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil.getExcelContentAsString(null, Locale.ENGLISH, 4000);
    assertEquals("", tmpContent);
  }

  @Test
  public void getExcelContentAsString_xlsx_empty() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/empty.xlsx"), Locale.ENGLISH, 4000);
    assertEquals("[Tab1]", tmpContent);
  }

  @Test
  public void getExcelContentAsString_xlsx_de() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14,3")
        .append(" float (rounded) 1,70")
        .append(" currency 4,33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124,70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.GERMAN, 247);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xlsx_en() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.ENGLISH, 247);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xlsx_equalToMaxLength() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.ENGLISH, 246);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xlsx_greaterThanMaxLength() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.7 ...");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xlsx"), Locale.ENGLISH, 245);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xls_empty() throws IOException, InvalidFormatException {
    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/empty.xls"), Locale.ENGLISH, 4000);
    assertEquals("[Tab1]", tmpContent);
  }

  @Test
  public void getExcelContentAsString_xls_de() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14,3")
        .append(" float (rounded) 1,70")
        .append(" currency 4,33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124,70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.GERMAN, 247);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xls_en() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.ENGLISH, 247);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xls_equalToMaxLength() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.70");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.ENGLISH, 246);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_xls_greaterThanMaxLength() throws IOException, InvalidFormatException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14.3")
        .append(" float (rounded) 1.70")
        .append(" currency 4.33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124.7 ...");
    // @formatter:on

    final String tmpContent = ContentUtil
        .getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"), Locale.ENGLISH, 245);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getExcelContentAsString_error() {
    try {
      ContentUtil.getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.doc"), Locale.ENGLISH,
          4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException | IOException e) {
      assertEquals("org.apache.poi.openxml4j.exceptions.InvalidFormatException: The document is really a DOC file",
          e.toString());
    }

    try {
      ContentUtil.getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.docx"), Locale.ENGLISH,
          4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException | IOException e) {
      assertEquals(
          "org.apache.poi.openxml4j.exceptions.InvalidFormatException: Element styles@http://schemas.openxmlformats.org/wordprocessingml/2006/main is not a valid styleSheet@http://schemas.openxmlformats.org/spreadsheetml/2006/main document or a valid substitution.",
          e.toString());
    }

    try {
      ContentUtil.getExcelContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"), Locale.ENGLISH,
          4000);
      fail("InvalidFormatException expected");
    } catch (final InvalidFormatException | IOException e) {
      assertEquals("java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream",
          e.toString());
    }
  }

  @Test
  public void getZipContentAsString_txt() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[download.txt]")
        .append(" ")
        .append("This is the content of a simple text file. ")
        .append("This file is used to test WeT.");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_txt.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_pdf() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.pdf]")
        .append(" ")
        .append("This is the content of a simple PDF file.")
        .append(" ")
        .append("This file is used to test WeT.");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_pdf.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_pdf_error() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_pdf_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals("java.io.IOException: Can't convert the zipped pdf 'wet_test.pdf' into text "
          + "(reason: java.io.IOException: Error: Header doesn't contain versioninfo).", e.toString());
    }
  }

  @Test
  public void getZipContentAsString_rtf() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.rtf]")
        .append(" ")
        .append("Wetator is great.");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_rtf.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_doc() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.doc]")
        .append(" ")
        .append("Wetator is great.");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_doc.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_doc_error() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_doc_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals("java.io.IOException: Can't convert the zipped doc 'wet_test.doc' into text "
          + "(reason: org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was neither an OLE2 stream, nor an OOXML stream, found type: PDF).",
          e.toString());
    }
  }

  @Test
  public void getZipContentAsString_docx() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.docx]")
        .append(" ")
        .append("Wetator is great.");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_docx.zip"), StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_docx_error() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_docx_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals("java.io.IOException: Can't convert the zipped doc 'wet_test.docx' into text "
          + "(reason: org.apache.poi.openxml4j.exceptions.InvalidFormatException: Your InputStream was neither an OLE2 stream, nor an OOXML stream, found type: PDF).",
          e.toString());
    }
  }

  @Test
  public void getZipContentAsString_xls() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.xls]")
        .append(" ")
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14,3")
        .append(" float (rounded) 1,70")
        .append(" currency 4,33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124,70");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_xls.zip"), StandardCharsets.UTF_8, Locale.GERMANY, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_xls_error() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_xls_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals(
          "java.io.IOException: Can't convert the zipped xls 'wet_test.xls' into text "
              + "(reason: java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream).",
          e.toString());
    }
  }

  @Test
  public void getZipContentAsString_xlsx() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.xlsx]")
        .append(" ")
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14,3")
        .append(" float (rounded) 1,70")
        .append(" currency 4,33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124,70");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_xlsx.zip"), StandardCharsets.UTF_8, Locale.GERMANY, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_xlsx_error() {
    try {
      ContentUtil.getZipContentAsString(new FileInputStream("test/webpage/download/wet_test_xlsx_error.zip"),
          StandardCharsets.UTF_8, Locale.ENGLISH, 4000);
      fail("IOException expected");
    } catch (final IOException e) {
      assertEquals(
          "java.io.IOException: Can't convert the zipped xls 'wet_test.xlsx' into text "
              + "(reason: java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream).",
          e.toString());
    }
  }

  @Test
  public void getZipContentAsString_mixed() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.csv]")
        .append(" ")
        .append("Col1, Col2 text1, text2")

        .append(" ")
        .append("[wet_test.pdf]")
        .append(" ")
        .append("This is the content of a simple PDF file.")
        .append(" ")
        .append("This file is used to test WeT.")

        .append(" ")
        .append("[wet_test.xls]")
        .append(" ")
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[Tab2] Wetator Test Page2 Web application testing is fun")
        .append(" ")
        .append("[Data Test]")
        .append(" String plain text")
        .append(" String(int) 4711")
        .append(" int 123")
        .append(" float 14,3")
        .append(" float (rounded) 1,70")
        .append(" currency 4,33 €")
        .append(" percent 3%")
        .append(" date 7/14/11")
        .append(" date (formated) 14-Jul-11")
        .append(" formula 124,70")

        .append(" ")
        .append("[wet_test.xml]")
        .append(" ")
        .append("<?xml version=\"1.0\"?> <wetator> <Test>Simple xml content</Test> </wetator>");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_mix.zip"), StandardCharsets.UTF_8, Locale.GERMANY, 4000);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_mixed_equalToMaxLength() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.csv]")
        .append(" ")
        .append("Col1, Col2 text1, text2")

        .append(" ")
        .append("[wet_test.pdf]")
        .append(" ")
        .append("This is the content of ...")

        .append(" ")
        .append("[wet_test.xls]")
        .append(" ")
        .append("[Tab1] Wetator Page 1")
        .append(" ")
        .append("[ ...")

        .append(" ")
        .append("[wet_test.xml]")
        .append(" ")
        .append("<?xml version=\"1.0\"?> < ...");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_mix.zip"), StandardCharsets.UTF_8, Locale.GERMANY, 23);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void getZipContentAsString_mixed_greaterThanMaxLength() throws IOException {
    final StringBuilder tmpExpected = new StringBuilder()
    // @formatter:off
        .append("[wet_test.csv]")
        .append(" ")
        .append("Col1, Col2 ...")

        .append(" ")
        .append("[wet_test.pdf]")
        .append(" ")
        .append("This is th ...")

        .append(" ")
        .append("[wet_test.xls]")
        .append(" ")
        .append("[Tab1] Wet ...")

        .append(" ")
        .append("[wet_test.xml]")
        .append(" ")
        .append("<?xml vers ...");
    // @formatter:on

    final String tmpContent = ContentUtil.getZipContentAsString(
        new FileInputStream("test/webpage/download/wet_test_mix.zip"), StandardCharsets.UTF_8, Locale.GERMANY, 10);
    assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void isTxt() throws IOException {
    String tmpText = "Some readable text for testing WETATOR.";
    assertTrue(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.pdf"), StandardCharsets.UTF_8);
    assertFalse(ContentUtil.isTxt(tmpText));

    tmpText = IOUtils.toString(new FileInputStream("test/webpage/download/wet_test.xls"), StandardCharsets.UTF_8);
    assertFalse(ContentUtil.isTxt(tmpText));
  }

  @Test
  public void determineLocaleFromRequest_null() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader(null);
    assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_empty() {
    Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("");
    assertNull(tmpLocale);

    tmpLocale = ContentUtil.determineLocaleFromRequestHeader(",");
    assertNull(tmpLocale);

    tmpLocale = ContentUtil.determineLocaleFromRequestHeader(";");
    assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de");
    assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_caseInsensitive() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("DE");
    assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_unknown() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("xy");
    assertNull(tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_withCountry() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de-DE");
    assertEquals(Locale.GERMANY, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_withCountry_caseInsensitive() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de-de");
    assertEquals(Locale.GERMANY, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_withCountry_unknown() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("de-xy");
    assertEquals(Locale.GERMAN, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_multiple() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da,en");
    assertEquals(new Locale("da", ""), tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_multiple_firstUnknown() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("xy,en");
    assertEquals(Locale.ENGLISH, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_multiple_withWeight() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("en-gb,de;q=0.5");
    assertEquals(Locale.UK, tmpLocale);
  }

  @Test
  public void determineLocaleFromRequest_operaStyle() {
    final Locale tmpLocale = ContentUtil.determineLocaleFromRequestHeader("da;1.0,en;0.9");
    assertEquals(new Locale("da", ""), tmpLocale);
  }
}
