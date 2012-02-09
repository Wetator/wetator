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


package org.wetator.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.junit.Test;

/**
 * @author rbri
 */
public class ContentUtilTest {

  @Test
  public void testGetPdfContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("This is the content of a simple PDF file.");
    tmpExpected.append(" ");
    tmpExpected.append("This file is used to test WeT.");

    String tmpContent = ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void testGetPdfContentAsStringError() {
    try {
      ContentUtil.getPdfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"));
      junit.framework.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: Error: Header doesn't contain versioninfo", e.toString());
    }
  }

  @Test
  public void testGetRtfContentAsString() throws FileNotFoundException, IOException, BadLocationException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Wetator is great.");

    String tmpContent = ContentUtil.getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.rtf"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void testGetRtfContentAsStringError() throws FileNotFoundException, IOException, BadLocationException {
    String tmpContent = ContentUtil.getRtfContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"));
    org.junit.Assert.assertEquals("", tmpContent);
  }

  @Test
  public void testGetXlsContentAsString() throws FileNotFoundException, IOException {
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
    tmpExpected.append(" currency 4,33");
    tmpExpected.append(" percent 3%");
    tmpExpected.append(" date 7/14/11");
    tmpExpected.append(" date (formated) 14-Jul-11");
    tmpExpected.append(" formula 124,70");

    String tmpContent = ContentUtil.getXlsContentAsString(new FileInputStream("test/webpage/download/wet_test.xls"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void testGetXlsContentAsStringError() {
    try {
      ContentUtil.getXlsContentAsString(new FileInputStream("test/webpage/download/wet_test.pdf"));
      junit.framework.Assert.fail("IOException expected");
    } catch (Exception e) {
      org.junit.Assert.assertEquals("java.io.IOException: "
          + "Invalid header signature; read 0x342E312D46445025, expected 0xE11AB1A1E011CFD0", e.toString());
    }
  }

  @Test
  public void testGetTxtContentAsString() {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("Some content line two Hallo Wetator.");

    String tmpContent = ContentUtil.getTxtContentAsString("Some content\rline two\r\n\tHallo\tWetator.");
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }
}
