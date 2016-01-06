/*
 * Copyright (c) 2008-2016 wetator.org
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
 * Tests all pages of the plugin (project, build, report, test, browser) for different Wetator results using modules.
 *
 * @author frank.danek
 */
// TODO assert anchors
public class ResultWithModuleTest extends AbstractPluginTest {

  public void testOk() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "moduleOk.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.SUCCESS, 100, 0, 1, tmpBuild);

    assertProjectAndBuildPage("no failures", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(0, 1, tmpReportPage);
    // all tests table: all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, "moduleOk.wet", "0 ±0 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("moduleOk.wet").click();
    assertTestPage("moduleOk.wet", "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleOk.wet",
        tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Passed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Passed", "moduleOk.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleOk.wet", tmpBrowserPage);
  }

  public void testFailure() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "moduleFailure.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "moduleFailure.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "moduleFailure.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("moduleFailure.wet").click();
    assertTestPage("moduleFailure.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleFailure.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "moduleFailure.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleFailure.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\failure.wet", "Failure",
        "2 assert-content failure",
        "Expected content(s) {not found} or [in wrong order]: '{failure}' (content: 'Wetator / Index').",
        tmpBrowserPage);
  }

  public void testError() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "moduleError.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "moduleError.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "moduleError.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("moduleError.wet").click();
    assertTestPage("moduleError.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleError.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "moduleError.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleError.wet", tmpBrowserPage);
    assertBrowserError("E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\error.wet", "Error",
        "1 assert-content", "The command 'assert-content' requires a first parameter.", tmpBrowserPage);
  }

  public void testNotFound() throws Exception {
    FreeStyleProject tmpProject = createProject(WETATOR_RESULT_PATH + "moduleNotFound.xml");
    FreeStyleBuild tmpBuild = runBuild(tmpProject);

    assertBuild(Result.UNSTABLE, 0, 1, 1, tmpBuild);

    assertProjectAndBuildPage("1 failure", tmpBuild);

    HtmlPage tmpReportPage = webClient.getPage(tmpProject, tmpBuild.getNumber() + "/wetatorReport");
    assertReportPage(1, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, "moduleNotFound.wet[Firefox3.6]");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, "moduleNotFound.wet", "1 +1 0 ±0 1 +1");

    HtmlPage tmpTestPage = tmpReportPage.getAnchorByText("moduleNotFound.wet").click();
    assertTestPage("moduleNotFound.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleNotFound.wet", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, "Firefox3.6", "Failed");

    HtmlPage tmpBrowserPage = tmpTestPage.getAnchorByText("Firefox3.6").click();
    assertBrowserPage("Firefox3.6", "Failed", "moduleNotFound.wet",
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleNotFound.wet", tmpBrowserPage);
    assertBrowserError(
        "E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\moduleNotFound.wet",
        "Error",
        "1 use-module notFound.wet",
        "The module file 'E:\\Java\\workspaces\\wetator\\wetator\\test\\forJenkinsPlugin\\notFound.wet' does not exist.",
        tmpBrowserPage);
  }
}
