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

import java.io.FileNotFoundException;

import org.wetator.core.TestCase;
import org.wetator.exception.InvalidInputException;
import org.wetator.jenkins.test.ResultXMLBuilder;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import hudson.model.Result;

/**
 * Tests all pages of the plugin (project, build, report, test, browser) for different Wetator results.
 *
 * @author frank.danek
 */
// TODO assert anchors
public class ResultTest extends AbstractPluginTest {

  public void testGreen() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.SUCCESS, 100, 0, 0, 1);

    assertProjectAndBuildPage("no failures");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(0, 0, 1, tmpReportPage);
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, builder.getTestFileName() + " 20 ms 0 ±0 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("20 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 20 ms Passed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Passed", "20 ms", tmpBrowserPage);
  }

  public void testGreenWithModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeComment();
    builder.writeCommand();
    builder.endModule();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.SUCCESS, 100, 0, 0, 1);

    assertProjectAndBuildPage("no failures");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(0, 0, 1, tmpReportPage);
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, builder.getTestFileName() + " 50 ms 0 ±0 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("50 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 50 ms Passed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Passed", "50 ms", tmpBrowserPage);
  }

  public void testInSubFolder() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase("sub/test%TESTNO%.wet");

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.SUCCESS, 100, 0, 0, 1);

    assertProjectAndBuildPage("no failures");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(0, 0, 1, tmpReportPage);
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 0, builder.getTestFileName() + " 20 ms 0 ±0 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("20 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 20 ms Passed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Passed", "20 ms", tmpBrowserPage);
  }

  public void testBlue() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Failure", "2 " + ResultXMLBuilder.COMMAND_NAME, "test failure", tmpBrowserPage);
  }

  public void testBlueWithCommandParameter() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure("value1");
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Failure", "2 " + ResultXMLBuilder.COMMAND_NAME + " value1", "test failure", tmpBrowserPage);
  }

  public void testBlueWithMultipleCommandParameters() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure("value1", "value2", "value3");
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Failure", "2 " + ResultXMLBuilder.COMMAND_NAME + " value1 value2 value3", "test failure",
        tmpBrowserPage);
  }

  public void testBlueBeforeModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeCommand();
    builder.endModule();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError("Failure", "2 " + ResultXMLBuilder.COMMAND_NAME, "test failure", tmpBrowserPage);
  }

  public void testBlueInModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.endModule();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError(normalizeAbsoluteTestFileName() + "module", "Failure", "4 " + ResultXMLBuilder.COMMAND_NAME,
        "test failure", tmpBrowserPage);
  }

  public void testBlueAfterModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeCommand();
    builder.endModule();
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError("Failure", "5 " + ResultXMLBuilder.COMMAND_NAME, "test failure", tmpBrowserPage);
  }

  public void testRed() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Error", "2 " + ResultXMLBuilder.COMMAND_NAME, "test error", tmpBrowserPage);
  }

  public void testRedWithCommandParameter() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError("value1");
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Error", "2 " + ResultXMLBuilder.COMMAND_NAME + " value1", "test error", tmpBrowserPage);
  }

  public void testRedWithMultipleCommandParameters() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError("value1", "value2", "value3");
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Error", "2 " + ResultXMLBuilder.COMMAND_NAME + " value1 value2 value3", "test error",
        tmpBrowserPage);
  }

  public void testRedBeforeModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.startModule(tmpTestCase);
    builder.writeCommandIgnored();
    builder.endModule();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError("Error", "2 " + ResultXMLBuilder.COMMAND_NAME, "test error", tmpBrowserPage);
  }

  public void testRedInModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.endModule();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError(normalizeAbsoluteTestFileName() + "module", "Error", "4 " + ResultXMLBuilder.COMMAND_NAME,
        "test error", tmpBrowserPage);
  }

  public void testRedAfterModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.writeCommand();
    builder.endModule();
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 60 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 60 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("60 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 60 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "60 ms", tmpBrowserPage);
    assertBrowserError("Error", "5 " + ResultXMLBuilder.COMMAND_NAME, "test error", tmpBrowserPage);
  }

  public void testBlueRedPriority() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.writeCommandWithError();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 20 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 20 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("20 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 20 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "20 ms", tmpBrowserPage);
    assertBrowserError("Failure", "1 " + ResultXMLBuilder.COMMAND_NAME, "test failure", tmpBrowserPage);
  }

  public void testBlueRedPriorityInModule() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.startModule(tmpTestCase);
    builder.writeCommandWithError();
    builder.endModule();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 0, 1);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 0, 1, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.FF38 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 0 ±0 1 +1");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.FF38 + " 30 ms Failed");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Failure", "1 " + ResultXMLBuilder.COMMAND_NAME, "test failure", tmpBrowserPage);
  }

  public void testExceptionInvalidInputWhileReadingCommands() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    builder.endTestRun();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 1, 2);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 1, 2, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.IE11 + "] 0 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 0 ms 1 +1 1 +1 2 +2");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("0 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 0 ms Failed");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Failed", "0 ms", tmpBrowserPage);
    assertBrowserError("TestCase test1.wet is very invalid.", tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);
  }

  public void testExceptionInvalidInputDuringCommandExecution() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.writeCommand();
    builder.writeCommandWithError(new InvalidInputException("test error"));
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 1, 2);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 1, 2, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.IE11 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 1 +1 2 +2");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 30 ms Failed");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError("Error", "2 " + ResultXMLBuilder.COMMAND_NAME, "test error", tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);
  }

  public void testExceptionInvalidInputModuleNotFound() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.writeCommand();
    builder.startModule(tmpTestCase);
    builder.progressListener.executeCommandError(new FileNotFoundException("Module 'module.wet' not found."));
    builder.endModule();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 1, 2);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 1, 2, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.IE11 + "] 30 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 30 ms 1 +1 1 +1 2 +2");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("30 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 30 ms Failed");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Failed", "30 ms", tmpBrowserPage);
    assertBrowserError(normalizeAbsoluteTestFileName() + "module", "Module 'module.wet' not found.", tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);
  }

  public void testExceptionJavaError() throws Exception {
    builder.startEngine();
    builder.startTestCase();

    builder.progressListener.testRunStart(ResultXMLBuilder.IE11);
    builder.error(new NoClassDefFoundError("test error"));
    builder.progressListener.testRunEnd();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 1, 2);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 1, 2, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0,
        builder.getTestFileName() + "[" + ResultXMLBuilder.IE11 + "] 0 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, builder.getTestFileName() + " 0 ms 1 +1 1 +1 2 +2");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage);
    assertTestPage("0 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 0 ms Failed");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Failed", "0 ms", tmpBrowserPage);
    assertBrowserError("test error", tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);
  }

  public void testExceptionJavaErrorMultipleTestCases() throws Exception {
    builder.startEngine();

    TestCase tmpTestCase = builder.startTestCase();
    String tmpTestFileName1 = tmpTestCase.getName();
    String tmpAbsoluteTestFileName1 = tmpTestCase.getFile().getAbsolutePath();
    builder.progressListener.testRunStart(ResultXMLBuilder.IE11);
    builder.error(new NoClassDefFoundError("test error"));
    builder.progressListener.testRunEnd();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    tmpTestCase = builder.startTestCase();
    String tmpTestFileName2 = tmpTestCase.getName();
    String tmpAbsoluteTestFileName2 = tmpTestCase.getFile().getAbsolutePath();
    builder.writeTestRunIgnored(ResultXMLBuilder.IE11);

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    builder.endEngine();

    runBuild();

    assertBuild(Result.UNSTABLE, 0, 1, 3, 4);

    assertProjectAndBuildPage("1 failure");

    // report page
    HtmlPage tmpReportPage = openReportPage();
    assertReportPage(1, 3, 4, tmpReportPage);
    // failed tests table
    assertPaneTableRowContains(tmpReportPage, 0, 0, tmpTestFileName1 + "[" + ResultXMLBuilder.IE11 + "] 0 ms 1");
    // all tests table: fail, skip, total
    assertPaneTableRowContains(tmpReportPage, 1, 0, tmpTestFileName1 + " 0 ms 1 +1 1 +1 2 +2");
    assertPaneTableRowContains(tmpReportPage, 1, 1, tmpTestFileName2 + " 0 ms 0 ±0 2 +2 2 +2");

    // test page
    HtmlPage tmpTestPage = openTestPage(tmpReportPage, tmpTestFileName1);
    assertTestPage(tmpTestFileName1, normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName1), "0 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 0 ms Failed");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    HtmlPage tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Failed", tmpTestFileName1,
        normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName1), "0 ms", tmpBrowserPage);
    assertBrowserError(normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName1), "test error", tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", tmpTestFileName1,
        normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName1), "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);

    // test page
    tmpTestPage = openTestPage(tmpReportPage, tmpTestFileName2);
    assertTestPage(tmpTestFileName2, normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName2), "0 ms", tmpTestPage);
    assertPaneTableRowContains(tmpTestPage, 0, ResultXMLBuilder.IE11 + " 0 ms Skipped");
    assertPaneTableRowContains(tmpTestPage, 1, ResultXMLBuilder.FF38 + " 0 ms Skipped");

    // browser page
    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.IE11);
    assertBrowserPage(ResultXMLBuilder.IE11, "Skipped", tmpTestFileName2,
        normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName2), "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);

    tmpBrowserPage = openBrowserPage(tmpTestPage, ResultXMLBuilder.FF38);
    assertBrowserPage(ResultXMLBuilder.FF38, "Skipped", tmpTestFileName2,
        normalizeAbsoluteTestFileName(tmpAbsoluteTestFileName2), "0 ms", tmpBrowserPage);
    assertBrowserSkipped(tmpBrowserPage);
  }
}
