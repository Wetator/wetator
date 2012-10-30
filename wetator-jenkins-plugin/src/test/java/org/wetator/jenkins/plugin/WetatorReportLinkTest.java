/*
 * Copyright (c) wetator.org
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

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests the links to the Wetator report (HTML).
 * 
 * @author frank.danek
 */
public class WetatorReportLinkTest extends AbstractPluginTest {

  public void testReport() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "ok.xml",
        "src/test/resources/org/wetator/jenkins/wetreport/dummy.html");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    WebAssert.assertTextPresent(tmpReportPage, "Test Report");
    WebAssert.assertLinkPresentWithText(tmpReportPage, WETATOR_REPORT_FILENAME);

    HtmlPage tmpWetatorReportPage = tmpReportPage.getAnchorByText(WETATOR_REPORT_FILENAME).click();
    WebAssert.assertTextPresent(tmpWetatorReportPage, "Dummy Report");
  }

  public void testNoReport() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "ok.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    WebAssert.assertTextNotPresent(tmpReportPage, "Test Report");
    WebAssert.assertLinkNotPresentWithText(tmpReportPage, WETATOR_REPORT_FILENAME);
  }
}
