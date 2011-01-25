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


package org.wetator.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    String tmpContent = ContentUtil.getPdfContentAsString(new FileInputStream(
        "webpages/testcases/download/wet_test.pdf"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }

  @Test
  public void testGetXlsContentAsString() throws FileNotFoundException, IOException {
    StringBuilder tmpExpected = new StringBuilder();
    tmpExpected.append("[Tab1] Wetator Page 1");
    tmpExpected.append(" ");
    tmpExpected.append("[Tab2] Wetator Test Page2 Web application testing is fun");

    String tmpContent = ContentUtil.getXlsContentAsString(new FileInputStream(
        "webpages/testcases/download/wet_test.xls"));
    org.junit.Assert.assertEquals(tmpExpected.toString(), tmpContent);
  }
}
