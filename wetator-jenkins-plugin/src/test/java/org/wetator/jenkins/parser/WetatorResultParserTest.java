/*
 * Copyright (c) 2008-2015 wetator.org
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


package org.wetator.jenkins.parser;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.test.ResultAssert;
import org.wetator.progresslistener.XMLResultWriter;

/**
 * Tests for {@link WetatorResultParser}.
 *
 * @author frank.danek
 */
public class WetatorResultParserTest {

  private static String LOGS_FOLDER = "target/logs";
  private static String RESULT_LOG = LOGS_FOLDER + "/wetresult.xml";

  private static String IE11 = "IE11";
  private static String FF38 = "Firefox38";

  private static String COMMAND_NAME = "command";

  private static String START_TIME = "10.12.2015 23:19:17";
  private static int OVERALL_EXECUTION_TIME = 777;
  private static int COMMAND_EXECUTION_TIME = 10;

  protected WetatorConfiguration configuration = mock(WetatorConfiguration.class);
  protected WetatorEngine engine = mock(WetatorEngine.class);
  protected WetatorContext context = mock(WetatorContext.class);

  protected IProgressListener progressListener;

  private int testNo;
  private int lineNo;

  private String testFileName;

  @Before
  public void setupEnvironment() {
    File tmpLogsFolder = new File(LOGS_FOLDER);
    tmpLogsFolder.mkdirs();

    when(configuration.getOutputDir()).thenReturn(tmpLogsFolder);
    when(engine.getConfiguration()).thenReturn(configuration);
    when(context.replaceVariables(any(String.class))).thenCallRealMethod();

    progressListener = new XMLResultWriter();

    testNo = 1;
  }

  @Test(expected = XMLStreamException.class)
  public void empty() throws XMLStreamException, IOException {
    String tmpResult = "";
    try (InputStream tmpInputStream = getResultFromString(tmpResult)) {
      WetatorResultParser.parse(tmpInputStream);
    }
  }

  @Test(expected = XMLStreamException.class)
  public void headerOnly() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    try (InputStream tmpInputStream = getResultFromString(tmpResult)) {
      WetatorResultParser.parse(tmpInputStream);
    }
  }

  @Test
  public void unknown() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><unknown/>";
    try (InputStream tmpInputStream = getResultFromString(tmpResult)) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(0, 0, 0, 0, 0, tmpTestResults);
    }
  }

  @Test
  public void noTests() throws Exception {
    startEngine();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 0, 0, tmpTestResult);
    }
  }

  @Test
  public void green() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeComment();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 2, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void greenWithModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    startModule(tmpTestCase);
    writeComment();
    writeCommand();
    endModule();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(5, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 5, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void inSubFolder() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase("sub/test" + testNo++ + ".wet");

    startTestRun(tmpTestCase, FF38);
    writeComment();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 2, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void blue() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult);
    }
  }

  @Test
  public void blueWithCommandParameter() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithFailure("value1");
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult, "value1");
    }
  }

  @Test
  public void blueWithMultipleCommandParameters() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithFailure("value1", "value2", "value3");
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult, "value1", "value2", "value3");
    }
  }

  @Test
  public void blueBeforeModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult);
    }
  }

  @Test
  public void blueInModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    endModule();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(testFileName + "module", 4, tmpBrowserResult);
    }
  }

  @Test
  public void blueAfterModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(5, tmpBrowserResult);
    }
  }

  @Test
  public void red() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);
    }
  }

  @Test
  public void redWithCommandParameter() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithError("value1");
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult, "value1");
    }
  }

  @Test
  public void redWithMultipleCommandParameters() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithError("value1", "value2", "value3");
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult, "value1", "value2", "value3");
    }
  }

  @Test
  public void redBeforeModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    startModule(tmpTestCase);
    writeCommandIgnored();
    endModule();
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);
    }
  }

  @Test
  public void redInModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    endModule();
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(testFileName + "module", 4, tmpBrowserResult);
    }
  }

  @Test
  public void redAfterModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(5, tmpBrowserResult);
    }
  }

  @Test
  public void blueRedPriority() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommandWithFailure();
    writeCommandWithError();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 2, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(1, tmpBrowserResult);
    }
  }

  @Test
  public void blueRedPriorityInModule() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, FF38);
    writeCommandWithFailure();
    startModule(tmpTestCase);
    writeCommandWithError();
    endModule();
    endTestRun();

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(1, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputWhileReadingCommands() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, IE11);
    progressListener.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    endTestRun();

    writeTestRunIgnored(FF38);

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, 0, false, false, tmpBrowserResult);
      assertTestError("TestCase test1.wet is very invalid.", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputDuringCommandExecution() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, IE11);
    writeCommand();
    writeCommandWithError(new InvalidInputException("test error"));
    writeCommandIgnored();
    endTestRun();

    writeTestRunIgnored(FF38);

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputModuleNotFound() throws Exception {
    startEngine();
    TestCase tmpTestCase = startTestCase();

    startTestRun(tmpTestCase, IE11);
    writeCommand();
    startModule(tmpTestCase);
    progressListener.executeCommandError(new FileNotFoundException("Module 'module.wet' not found."));
    endModule();
    writeCommandIgnored();
    endTestRun();

    writeTestRunIgnored(FF38);

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, 3, false, false, tmpBrowserResult);
      assertTestError(testFileName + "module", "Module 'module.wet' not found.", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionJavaError() throws Exception {
    startEngine();
    startTestCase();

    progressListener.testRunStart(IE11);
    progressListener.error(new NoClassDefFoundError("test error"));
    progressListener.testRunEnd();

    writeTestRunIgnored(FF38);

    endTestCase();
    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, 0, false, false, tmpBrowserResult);
      assertTestError("test error", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionJavaErrorMultipleTestCases() throws Exception {
    startEngine();

    TestCase tmpTestCase = startTestCase();
    String tmpTestFileName1 = tmpTestCase.getName();
    progressListener.testRunStart(IE11);
    progressListener.error(new NoClassDefFoundError("test error"));
    progressListener.testRunEnd();

    writeTestRunIgnored(FF38);
    endTestCase();

    tmpTestCase = startTestCase();
    String tmpTestFileName2 = tmpTestCase.getName();
    writeTestRunIgnored(IE11);

    writeTestRunIgnored(FF38);
    endTestCase();

    endEngine();

    try (InputStream tmpInputStream = getActualResult()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 3, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 3, 1, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(tmpTestFileName1, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, tmpTestFileName1, 0, false, false, tmpBrowserResult);
      assertTestError(tmpTestFileName1, "test error", tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, tmpTestFileName1, 0, false, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult(tmpTestFileName2, 0, 0, 2, 0, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(IE11, tmpTestFileName2, 0, false, true, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(FF38, tmpTestFileName2, 0, false, true, tmpBrowserResult);
    }
  }

  private void startEngine() {
    progressListener.init(engine);
    progressListener.start(engine);
  }

  private void endEngine() {
    progressListener.end(engine);
  }

  private TestCase startTestCase() {
    return startTestCase("test" + testNo++ + ".wet");
  }

  private TestCase startTestCase(String aName) {
    TestCase tmpTestCase = new TestCase(aName, new File("/Test/" + aName));
    testFileName = tmpTestCase.getName();
    progressListener.testCaseStart(tmpTestCase);
    return tmpTestCase;
  }

  private void endTestCase() {
    progressListener.testCaseEnd();
  }

  private void startTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
  }

  private void endTestRun() {
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  private void writeTestRunIgnored(String aBrowser) {
    progressListener.testRunStart(aBrowser);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
  }

  private void writeCommand() {
    Command tmpCommand = createCommand(COMMAND_NAME);
    writeCommand(tmpCommand);
  }

  private void writeCommandWithFailure(String... aParameterValues) {
    Command tmpCommand = createCommand(COMMAND_NAME, aParameterValues);
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener
        .executeCommandFailure(new AssertionException("test failure", new RuntimeException("failure cause")));
    progressListener.executeCommandEnd();
  }

  private void writeCommandWithError(String... aParameterValues) {
    writeCommandWithError(new ActionException("test error"), aParameterValues);
  }

  private void writeCommandWithError(Exception anException, String... aParameterValues) {
    Command tmpCommand = createCommand(COMMAND_NAME, aParameterValues);
    writeCommandWithError(tmpCommand, anException);
  }

  private void writeCommandWithError(Command aCommand, Exception anException) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandError(anException);
    progressListener.executeCommandEnd();
  }

  private void writeCommandIgnored() {
    Command tmpCommand = createCommand(COMMAND_NAME);
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener.executeCommandIgnored();
    progressListener.executeCommandEnd();
  }

  private void writeComment() {
    Command tmpComment = createCommand(null, true, "comment value");
    writeCommand(tmpComment);
  }

  private void writeCommand(Command aCommand) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandSuccess();
    progressListener.executeCommandEnd();
  }

  private Command createCommand(String aCommandName, String... aParameterValues) {
    return createCommand(aCommandName, false, aParameterValues);
  }

  private Command createCommand(String aCommandName, boolean anIsComment, String... aParameterValues) {
    Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    if (aParameterValues != null) {
      if (aParameterValues.length > 0) {
        tmpCommand.setFirstParameter(new Parameter(aParameterValues[0]));
      }
      if (aParameterValues.length > 1) {
        tmpCommand.setSecondParameter(new Parameter(aParameterValues[1]));
      }
      if (aParameterValues.length > 2) {
        tmpCommand.setThirdParameter(new Parameter(aParameterValues[2]));
      }
    }
    lineNo++;
    return tmpCommand;
  }

  private void startModule(TestCase aTestCase) {
    progressListener.executeCommandStart(context, createCommand("use-module", "module"));
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath() + "module");
  }

  private void endModule() {
    progressListener.testFileEnd();
    progressListener.executeCommandEnd();
  }

  private InputStream getResultFromString(String aResult) throws UnsupportedEncodingException {
    return new ByteArrayInputStream(aResult.getBytes("utf-8"));
  }

  private InputStream getActualResult() throws Exception {
    File tmpActualFile = new File(RESULT_LOG);
    String tmpActualResult = FileUtils.readFileToString(tmpActualFile);
    tmpActualResult = normalizeResult(tmpActualResult);
    return new ByteArrayInputStream(tmpActualResult.getBytes("utf-8"));
  }

  private String normalizeResult(String aResult) {
    String tmpResult = aResult;
    // unify line breaks
    tmpResult = tmpResult.replace("\r\n", "\n");
    // replace tabs
    tmpResult = tmpResult.replace("\t", "    ");
    // unify path delimiter
    tmpResult = tmpResult.replace('\\', '/');
    // unify htmlunit version
    tmpResult = tmpResult.replaceAll("HtmlUnit version 2.\\d+(-SNAPSHOT)?", "HtmlUnit");
    // comments
    tmpResult = tmpResult.replaceAll("(?s)<!--.*?-->", "<!-- ... -->");
    // paths
    tmpResult = tmpResult.replaceAll("##PATH##", "");

    // remove drive letter (windows)
    tmpResult = tmpResult.replaceAll("file=\"[^/]*", "file=\"");

    // replace ids
    tmpResult = tmpResult.replaceAll("id=\"\\d+\"", "id=\"##ID##\"");

    // replace library versions
    tmpResult = replaceElementContent(tmpResult, "library", "([A-Za-z \\-]*)", "$2");
    // replace non-test java properties
    tmpResult = replaceLines(tmpResult, "<java id=\"##ID##\">", "\\s*</java>$", ".*key=\".*test\\.[^\"]+\".*",
        "##JAVA_PROPERTIES##");
    // replace output dir
    tmpResult = tmpResult.replaceAll("<property([^k]*) key=\"wetator.outputDir\" value=\"[^\"]*",
        "<property$1 key=\"wetator.outputDir\" value=\"##OUTPUT_DIR##");
    // replace start time
    tmpResult = replaceElementContent(tmpResult, "startTime", START_TIME);
    // replace execution time
    tmpResult = replaceElementContent(tmpResult, "executionTime", Integer.toString(COMMAND_EXECUTION_TIME));
    // replace the last (overall) execution time
    tmpResult = replaceLast(tmpResult, "<executionTime([^>]*)>" + COMMAND_EXECUTION_TIME, "<executionTime$2>"
        + OVERALL_EXECUTION_TIME);
    // replace stacktraces
    tmpResult = replaceLines(tmpResult, "<details id=\"##ID##\">[^\\n]*", "</details>", null, "##DETAILS##");
    System.out.println(tmpResult);
    return tmpResult;
  }

  private String replaceElementContent(String anXML, String anElement, String aNewContent) {
    return replaceElementContent(anXML, anElement, "", aNewContent);
  }

  private String replaceElementContent(String anXML, String anElement, String anOldContent, String aNewContent) {
    return anXML.replaceAll("<" + anElement + "([^>]*)>" + anOldContent + "[^<]*", "<" + anElement + "$1>"
        + aNewContent);
  }

  private String replaceLines(String aResult, String aPrefixLine, String aSuffixLine, String aKeepPattern,
      String aReplacement) {
    String tmpResult = aResult;
    Pattern tmpPrefixLinePattern = Pattern.compile("(?m)" + aPrefixLine + "$");
    Pattern tmpSuffixLinePattern = Pattern.compile("(?m)^" + aSuffixLine);

    Matcher tmpPrefixLineMatcher = tmpPrefixLinePattern.matcher(tmpResult);
    Matcher tmpSuffixLineMatcher = tmpSuffixLinePattern.matcher(tmpResult);
    int tmpLinesStart = 0;
    int tmpLinesEnd = 0;

    while (tmpPrefixLineMatcher.find(tmpLinesStart)) {
      tmpLinesStart = tmpPrefixLineMatcher.end();
      if (!tmpSuffixLineMatcher.find(tmpLinesStart)) {
        break;
      }
      tmpLinesEnd = tmpSuffixLineMatcher.start();

      String tmpPartBefore = tmpResult.substring(0, tmpLinesStart + 1);
      String tmpPart = tmpResult.substring(tmpLinesStart + 1, tmpLinesEnd);
      String tmpPartAfter = tmpResult.substring(tmpLinesEnd);
      StringBuilder tmpNormalizedPart = new StringBuilder();
      if (aKeepPattern != null) {
        String[] tmpLines = tmpPart.split("\\n");
        for (String tmpLine : tmpLines) {
          if (tmpLine.matches(aKeepPattern)) {
            tmpNormalizedPart.append(tmpLine).append("\n");
          }
        }
      }
      tmpNormalizedPart = tmpNormalizedPart.insert(0, aReplacement + "\n");
      tmpResult = tmpNormalizedPart.insert(0, tmpPartBefore).append(tmpPartAfter).toString();

      tmpPrefixLineMatcher = tmpPrefixLinePattern.matcher(tmpResult);
      tmpSuffixLineMatcher = tmpSuffixLinePattern.matcher(tmpResult);
    }
    return tmpResult;
  }

  private String replaceLast(String aText, String aRegex, String aReplacement) {
    return aText.replaceFirst("(?s)(.*)" + aRegex, "$1" + aReplacement);
  }

  private void assertTestResults(int aBrowserPassCount, int aBrowserSkipCount, int aBrowserFailCount,
      TestResults anActualTestResults) {
    ResultAssert.assertTestResults(OVERALL_EXECUTION_TIME, aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount, 1,
        anActualTestResults);
  }

  private void assertTestResult(int aBrowserPassCount, int aBrowserSkipCount, int aBrowserFailCount,
      int aTestFileCount, TestResult anActualTestResult) {
    ResultAssert.assertTestResult(START_TIME, OVERALL_EXECUTION_TIME, aBrowserPassCount, aBrowserSkipCount,
        aBrowserFailCount, aTestFileCount, anActualTestResult);
  }

  private void assertTestFileResult(long aStepCount, int aBrowserPassCount, int aBrowserSkipCount,
      int aBrowserFailCount, TestFileResult anActualTestFileResult) {
    assertTestFileResult(testFileName, aStepCount, aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount,
        anActualTestFileResult);
  }

  private void assertTestFileResult(String aTestFileName, long aStepCount, int aBrowserPassCount,
      int aBrowserSkipCount, int aBrowserFailCount, TestFileResult anActualTestFileResult) {
    ResultAssert.assertTestFileResult(aTestFileName, "/Test/" + aTestFileName, aStepCount * COMMAND_EXECUTION_TIME,
        aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount, aBrowserPassCount + aBrowserSkipCount
            + aBrowserFailCount, anActualTestFileResult);
  }

  private void assertBrowserResult(String aBrowserName, long aStepCount, boolean aPassed, boolean aSkipped,
      BrowserResult anActualBrowserResult) {
    assertBrowserResult(aBrowserName, testFileName, aStepCount, aPassed, aSkipped, anActualBrowserResult);
  }

  private void assertBrowserResult(String aBrowserName, String aTestFileName, long aStepCount, boolean aPassed,
      boolean aSkipped, BrowserResult anActualBrowserResult) {
    ResultAssert.assertBrowserResult(aBrowserName, aTestFileName + "[" + aBrowserName + "]", aStepCount
        * COMMAND_EXECUTION_TIME, aPassed ? 1 : 0, aSkipped ? 1 : 0, !aPassed && !aSkipped ? 1 : 0, aPassed, aSkipped,
        anActualBrowserResult);
  }

  private void assertStepErrorIsFailure(int aLine, BrowserResult anActualBrowserResult, String... aParameterValues) {
    assertStepErrorIsFailure(testFileName, aLine, anActualBrowserResult, aParameterValues);
  }

  private void assertStepErrorIsFailure(String aTestFileName, int aLine, BrowserResult anActualBrowserResult,
      String... aParameterValues) {
    assertStepError(aTestFileName, aLine, CauseType.FAILURE, "command", "test failure", anActualBrowserResult,
        aParameterValues);
  }

  private void assertStepErrorIsError(int aLine, BrowserResult anActualBrowserResult, String... aParameterValues) {
    assertStepErrorIsError(testFileName, aLine, anActualBrowserResult, aParameterValues);
  }

  private void assertStepErrorIsError(String aTestFileName, int aLine, BrowserResult anActualBrowserResult,
      String... aParameterValues) {
    assertStepError(aTestFileName, aLine, CauseType.ERROR, "command", "test error", anActualBrowserResult,
        aParameterValues);
  }

  private void assertStepError(String aTestFileName, int aLine, CauseType aCauseType, String aCommand, String anError,
      BrowserResult anActualBrowserResult, String... aParameterValues) {
    ResultAssert.assertStepError("/Test/" + aTestFileName, aLine, aCauseType, aCommand, anError,
        (StepError) anActualBrowserResult.getError(), aParameterValues);
  }

  private void assertTestError(String anError, BrowserResult anActualBrowserResult) {
    assertTestError(testFileName, anError, anActualBrowserResult);
  }

  private void assertTestError(String aTestFileName, String anError, BrowserResult anActualBrowserResult) {
    ResultAssert.assertTestError("/Test/" + aTestFileName, anError, anActualBrowserResult.getError());
  }
}