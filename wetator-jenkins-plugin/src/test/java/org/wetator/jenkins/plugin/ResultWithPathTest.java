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
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
// TODO assert anchors
public class ResultWithPathTest extends AbstractPluginTest {

  private static final String WETATOR_RESULT_PATH = "src/test/resources/org/wetator/jenkins/wetresult/";

  public void testOk() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "pathOk.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.SUCCESS, 100, 0, 1, tmpBuild);

    assertProjectAndBuildPage("no failures", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(0, 1, tmpReportPage);
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, "forJenkinsPlugin\\pathOk.wet", "0 ±0 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("forJenkinsPlugin\\pathOk.wet").click();
    assertTestPage("forJenkinsPlugin\\pathOk.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathOk.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Passed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Passed", "forJenkinsPlugin\\pathOk.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathOk.wet", tmpBrowserPage);
  }

  public void testFailure() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "pathFailure.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "forJenkinsPlugin\\pathFailure.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "forJenkinsPlugin\\pathFailure.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("forJenkinsPlugin\\pathFailure.wet").click();
    assertTestPage("forJenkinsPlugin\\pathFailure.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathFailure.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "forJenkinsPlugin\\pathFailure.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathFailure.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathFailure.wet", "Failure",
        "2 assert-content failure",
        "Expected content(s) {not found} or [in wrong order]: '{failure}' (content: 'Wetator / Index').",
        tmpBrowserPage);
  }

  public void testError() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "pathError.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "forJenkinsPlugin\\pathError.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "forJenkinsPlugin\\pathError.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("forJenkinsPlugin\\pathError.wet").click();
    assertTestPage("forJenkinsPlugin\\pathError.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathError.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "forJenkinsPlugin\\pathError.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathError.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\pathError.wet", "Error",
        "1 assert-content", "The command 'assert-content' requires a first parameter.", tmpBrowserPage);
  }
}
