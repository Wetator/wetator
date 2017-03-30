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


package org.wetator.jenkins.plugin;

import org.junit.Test;
import org.wetator.core.TestCase;
import org.wetator.jenkins.test.ResultXMLBuilder;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests the links to the Wetator report (HTML).
 *
 * @author frank.danek
 */
public class WetatorReportLinkTest extends AbstractPluginTest {

  @Test
  public void report() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild("src/test/resources/org/wetator/jenkins/wetreport/dummy.html");

    HtmlPage tmpReportPage = openReportPage();
    WebAssert.assertTextPresent(tmpReportPage, "Test Report");
    WebAssert.assertLinkPresentWithText(tmpReportPage, WETATOR_REPORT_FILENAME);

    HtmlPage tmpWetatorReportPage = tmpReportPage.getAnchorByText(WETATOR_REPORT_FILENAME).click();
    WebAssert.assertTextPresent(tmpWetatorReportPage, "Dummy Report");
  }

  @Test
  public void noReport() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    HtmlPage tmpReportPage = openReportPage();
    WebAssert.assertTextNotPresent(tmpReportPage, "Test Report");
    WebAssert.assertLinkNotPresentWithText(tmpReportPage, WETATOR_REPORT_FILENAME);
  }
}
