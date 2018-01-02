/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.wetator.core.TestCase;
import org.wetator.exception.InvalidInputException;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.test.ResultAssert;
import org.wetator.jenkins.test.ResultXMLBuilder;

/**
 * Tests for {@link WetatorResultParser}.
 *
 * @author frank.danek
 */
public class WetatorResultParserTest {

  private ResultXMLBuilder builder;

  @Before
  public void setupEnvironment() {
    builder = new ResultXMLBuilder();
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
    builder.startEngine();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 0, 0, tmpTestResult);
    }
  }

  @Test
  public void green() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 2, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void greenWithModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(5, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 5, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void inSubFolder() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase("sub/test%TESTNO%.wet");

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeComment();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(1, 0, 0, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 1, 0, 0, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 2, true, false, tmpBrowserResult);
    }
  }

  @Test
  public void blue() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure();
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult);
    }
  }

  @Test
  public void blueWithCommandParameter() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure("value1");
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult, "value1");
    }
  }

  @Test
  public void blueWithMultipleCommandParameters() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithFailure("value1", "value2", "value3");
    builder.writeCommand();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult, "value1", "value2", "value3");
    }
  }

  @Test
  public void blueBeforeModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(2, tmpBrowserResult);
    }
  }

  @Test
  public void blueInModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(builder.getTestFileName() + "module", 4, tmpBrowserResult);
    }
  }

  @Test
  public void blueAfterModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(5, tmpBrowserResult);
    }
  }

  @Test
  public void red() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError();
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);
    }
  }

  @Test
  public void redWithCommandParameter() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError("value1");
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult, "value1");
    }
  }

  @Test
  public void redWithMultipleCommandParameters() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommand();
    builder.writeCommandWithError("value1", "value2", "value3");
    builder.writeCommandIgnored();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult, "value1", "value2", "value3");
    }
  }

  @Test
  public void redBeforeModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);
    }
  }

  @Test
  public void redInModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(builder.getTestFileName() + "module", 4, tmpBrowserResult);
    }
  }

  @Test
  public void redAfterModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(6, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 6, false, false, tmpBrowserResult);
      assertStepErrorIsError(5, tmpBrowserResult);
    }
  }

  @Test
  public void blueRedPriority() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.FF38);
    builder.writeCommandWithFailure();
    builder.writeCommandWithError();
    builder.endTestRun();

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(2, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 2, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(1, tmpBrowserResult);
    }
  }

  @Test
  public void blueRedPriorityInModule() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.FF38, 3, false, false, tmpBrowserResult);
      assertStepErrorIsFailure(1, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputWhileReadingCommands() throws Exception {
    builder.startEngine();
    TestCase tmpTestCase = builder.startTestCase();

    builder.startTestRun(tmpTestCase, ResultXMLBuilder.IE11);
    builder.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    builder.endTestRun();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, 0, false, false, tmpBrowserResult);
      assertTestError("TestCase test1.wet is very invalid.", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputDuringCommandExecution() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, 3, false, false, tmpBrowserResult);
      assertStepErrorIsError(2, tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionInvalidInputModuleNotFound() throws Exception {
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

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(3, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, 3, false, false, tmpBrowserResult);
      assertTestError(builder.getTestFileName() + "module", "Module 'module.wet' not found.", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionJavaError() throws Exception {
    builder.startEngine();
    builder.startTestCase();

    builder.progressListener.testRunStart(ResultXMLBuilder.IE11);
    builder.error(new NoClassDefFoundError("test error"));
    builder.progressListener.testRunEnd();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);

    builder.endTestCase();
    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, 0, false, false, tmpBrowserResult);
      assertTestError("test error", tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, 0, false, true, tmpBrowserResult);
    }
  }

  @Test
  public void exceptionJavaErrorMultipleTestCases() throws Exception {
    builder.startEngine();

    TestCase tmpTestCase = builder.startTestCase();
    String tmpTestFileName1 = tmpTestCase.getName();
    builder.progressListener.testRunStart(ResultXMLBuilder.IE11);
    builder.error(new NoClassDefFoundError("test error"));
    builder.progressListener.testRunEnd();

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    tmpTestCase = builder.startTestCase();
    String tmpTestFileName2 = tmpTestCase.getName();
    builder.writeTestRunIgnored(ResultXMLBuilder.IE11);

    builder.writeTestRunIgnored(ResultXMLBuilder.FF38);
    builder.endTestCase();

    builder.endEngine();

    try (InputStream tmpInputStream = builder.getNormalizedResultStream()) {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      assertTestResults(0, 3, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult(0, 3, 1, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult(tmpTestFileName1, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, tmpTestFileName1, 0, false, false, tmpBrowserResult);
      assertTestError(tmpTestFileName1, "test error", tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, tmpTestFileName1, 0, false, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult(tmpTestFileName2, 0, 0, 2, 0, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult(ResultXMLBuilder.IE11, tmpTestFileName2, 0, false, true, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult(ResultXMLBuilder.FF38, tmpTestFileName2, 0, false, true, tmpBrowserResult);
    }
  }

  private InputStream getResultFromString(String aResult) throws UnsupportedEncodingException {
    return new ByteArrayInputStream(aResult.getBytes("utf-8"));
  }

  private void assertTestResults(int aBrowserPassCount, int aBrowserSkipCount, int aBrowserFailCount,
      TestResults anActualTestResults) {
    ResultAssert.assertTestResults(ResultXMLBuilder.OVERALL_EXECUTION_TIME, aBrowserPassCount, aBrowserSkipCount,
        aBrowserFailCount, 1, anActualTestResults);
  }

  private void assertTestResult(int aBrowserPassCount, int aBrowserSkipCount, int aBrowserFailCount, int aTestFileCount,
      TestResult anActualTestResult) {
    ResultAssert.assertTestResult(ResultXMLBuilder.START_TIME, ResultXMLBuilder.OVERALL_EXECUTION_TIME,
        aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount, aTestFileCount, anActualTestResult);
  }

  private void assertTestFileResult(long aStepCount, int aBrowserPassCount, int aBrowserSkipCount,
      int aBrowserFailCount, TestFileResult anActualTestFileResult) {
    assertTestFileResult(builder.getTestFileName(), aStepCount, aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount,
        anActualTestFileResult);
  }

  private void assertTestFileResult(String aTestFileName, long aStepCount, int aBrowserPassCount, int aBrowserSkipCount,
      int aBrowserFailCount, TestFileResult anActualTestFileResult) {
    ResultAssert.assertTestFileResult(aTestFileName, "/Test/" + aTestFileName,
        aStepCount * ResultXMLBuilder.COMMAND_EXECUTION_TIME, aBrowserPassCount, aBrowserSkipCount, aBrowserFailCount,
        aBrowserPassCount + aBrowserSkipCount + aBrowserFailCount, anActualTestFileResult);
  }

  private void assertBrowserResult(String aBrowserName, long aStepCount, boolean aPassed, boolean aSkipped,
      BrowserResult anActualBrowserResult) {
    assertBrowserResult(aBrowserName, builder.getTestFileName(), aStepCount, aPassed, aSkipped, anActualBrowserResult);
  }

  private void assertBrowserResult(String aBrowserName, String aTestFileName, long aStepCount, boolean aPassed,
      boolean aSkipped, BrowserResult anActualBrowserResult) {
    ResultAssert.assertBrowserResult(aBrowserName, aTestFileName + "[" + aBrowserName + "]",
        aStepCount * ResultXMLBuilder.COMMAND_EXECUTION_TIME, aPassed ? 1 : 0, aSkipped ? 1 : 0,
        !aPassed && !aSkipped ? 1 : 0, aPassed, aSkipped, anActualBrowserResult);
  }

  private void assertStepErrorIsFailure(int aLine, BrowserResult anActualBrowserResult, String... aParameterValues) {
    assertStepErrorIsFailure(builder.getTestFileName(), aLine, anActualBrowserResult, aParameterValues);
  }

  private void assertStepErrorIsFailure(String aTestFileName, int aLine, BrowserResult anActualBrowserResult,
      String... aParameterValues) {
    assertStepError(aTestFileName, aLine, CauseType.FAILURE, ResultXMLBuilder.COMMAND_NAME, "test failure",
        anActualBrowserResult, aParameterValues);
  }

  private void assertStepErrorIsError(int aLine, BrowserResult anActualBrowserResult, String... aParameterValues) {
    assertStepErrorIsError(builder.getTestFileName(), aLine, anActualBrowserResult, aParameterValues);
  }

  private void assertStepErrorIsError(String aTestFileName, int aLine, BrowserResult anActualBrowserResult,
      String... aParameterValues) {
    assertStepError(aTestFileName, aLine, CauseType.ERROR, ResultXMLBuilder.COMMAND_NAME, "test error",
        anActualBrowserResult, aParameterValues);
  }

  private void assertStepError(String aTestFileName, int aLine, CauseType aCauseType, String aCommand, String anError,
      BrowserResult anActualBrowserResult, String... aParameterValues) {
    ResultAssert.assertStepError("/Test/" + aTestFileName, aLine, aCauseType, aCommand, anError,
        (StepError) anActualBrowserResult.getError(), aParameterValues);
  }

  private void assertTestError(String anError, BrowserResult anActualBrowserResult) {
    assertTestError(builder.getTestFileName(), anError, anActualBrowserResult);
  }

  private void assertTestError(String aTestFileName, String anError, BrowserResult anActualBrowserResult) {
    ResultAssert.assertTestError("/Test/" + aTestFileName, anError, anActualBrowserResult.getError());
  }
}