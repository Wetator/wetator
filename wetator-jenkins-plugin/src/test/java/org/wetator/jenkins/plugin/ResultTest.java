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
 * Tests all pages of the plugin (project, build, report, test, browser) for different Wetator results.
 * 
 * @author frank.danek
 */
// TODO assert anchors
public class ResultTest extends AbstractPluginTest {

  public void testOk() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "ok.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.SUCCESS, 100, 0, 1, tmpBuild);

    assertProjectAndBuildPage("no failures", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(0, 1, tmpReportPage);
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, "ok.wet", "0 ±0 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("ok.wet").click();
    assertTestPage("ok.wet", "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\ok.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Passed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Passed", "ok.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\ok.wet", tmpBrowserPage);
  }

  public void testFailure() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "failure.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "failure.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "failure.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("failure.wet").click();
    assertTestPage("failure.wet", "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\failure.wet",
        tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "failure.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\failure.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\failure.wet", "Failure",
        "2 assert-content failure",
        "Expected content(s) {not found} or [in wrong order]: '{failure}' (content: 'Wetator / Index').",
        tmpBrowserPage);
  }

  public void testError() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "error.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "error.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "error.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("error.wet").click();
    assertTestPage("error.wet", "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\error.wet",
        tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "error.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\error.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\error.wet", "Error",
        "1 assert-content", "The command 'assert-content' requires a first parameter.", tmpBrowserPage);
  }
}
