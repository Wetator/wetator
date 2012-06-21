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


package org.wetator.jenkins.parser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestError;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.test.ResultAssert;

/**
 * @author frank.danek
 */
public class WetatorResultParserTest {

  @Test(expected = XMLStreamException.class)
  public void empty() throws XMLStreamException, IOException {
    String tmpResult = "";
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      WetatorResultParser.parse(tmpInputStream);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test(expected = XMLStreamException.class)
  public void headerOnly() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      WetatorResultParser.parse(tmpInputStream);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void unknown() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><unknown/>";
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(0, 0, 0, 0, 0, tmpTestResults);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommand() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample1.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandFolderTestCase() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"test/sample.wet\" file=\"/public/test/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/test/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert
          .assertTestFileResult("test/sample.wet", "/public/test/sample.wet", 12, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "test/sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandOneError() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><error><message>A really big problem</message></error><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 4, CauseType.ERROR, "Assert Title", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandOneFailure() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><failure><message>A really big problem</message></failure><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 4, CauseType.FAILURE, "Assert Title", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileTwoCommands() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 1, 0, 0, true, false,
          tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileTwoCommandsTwoErrors() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><error><message>A really big problem</message></error><executionTime>12</executionTime></command>" //
        + "<command name=\"Open Url\" line=\"5\"><error><message>Another problem</message></error><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 4, CauseType.ERROR, "Assert Title", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileTwoCommandsTwoFailures() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><failure><message>A really big problem</message></failure><executionTime>12</executionTime></command>" //
        + "<command name=\"Open Url\" line=\"5\"><failure><message>Another problem</message></failure><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 4, CauseType.FAILURE, "Assert Title", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseTwoBrowsersOneTestfileOneCommand() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 2, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 2, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 2, 0, 0, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("IE6", "sample.wet[IE6]", 23, 1, 0, 0, true, false, tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseTwoBrowsersOneTestfileOneCommandOneError() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><error><message>A really big problem</message></error><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 1, 0, 1, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("IE6", "sample.wet[IE6]", 23, 0, 0, 1, false, false, tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 5, CauseType.ERROR, "Open Url", 0, "A really big problem",
          tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseTwoBrowsersOneTestfileOneCommandOneFailure() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><failure><message>A really big problem</message></failure><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 1, 0, 1, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("IE6", "sample.wet[IE6]", 23, 0, 0, 1, false, false, tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample.wet", 5, CauseType.FAILURE, "Open Url", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseTwoBrowsersOneTestfileOneErrorOneIgnored() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">" //
        + "<error><message>A really big problem</message></error>" //
        + "</testfile>" //
        + "</testrun>" //
        + "<testrun browser=\"IE6\">" //
        + "<ignored/>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 1, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 1, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 0, 0, 1, 1, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 0, 0, 1, false, false,
          tmpBrowserResult);

      TestError tmpTestError = tmpBrowserResult.getError();
      ResultAssert.assertTestError("/public/sample.wet", "A really big problem", tmpTestError);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("IE6", "sample.wet[IE6]", 0, 0, 1, 0, false, true, tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void twoTestcasesOneBrowserOneTestfileOneCommand() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + "<testcase name=\"sample2.wet\" file=\"/public/sample2.wet\">" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample2.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 2, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 2, 0, 0, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      ResultAssert.assertTestFileResult("sample2.wet", "/public/sample2.wet", 23, 1, 0, 0, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE6", "sample2.wet[IE6]", 23, 1, 0, 0, true, false, tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void twoTestcasesOneBrowserOneTestfileOneCommandOneError() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + "<testcase name=\"sample2.wet\" file=\"/public/sample2.wet\">" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample2.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><error><message>A really big problem</message></error><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 1, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      ResultAssert.assertTestFileResult("sample2.wet", "/public/sample2.wet", 23, 0, 0, 1, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE6", "sample2.wet[IE6]", 23, 0, 0, 1, false, false, tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample2.wet", 5, CauseType.ERROR, "Open Url", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void twoTestcasesOneBrowserOneTestfileOneCommandOneFailure() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + "<testcase name=\"sample2.wet\" file=\"/public/sample2.wet\">" //
        + "<testrun browser=\"IE6\">" //
        + "<testfile file=\"/public/sample2.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><failure><message>A really big problem</message></failure><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 1, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 12, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 12, 1, 0, 0, true, false,
          tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      ResultAssert.assertTestFileResult("sample2.wet", "/public/sample2.wet", 23, 0, 0, 1, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE6", "sample2.wet[IE6]", 23, 0, 0, 1, false, false, tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/sample2.wet", 5, CauseType.FAILURE, "Open Url", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandOneNestedTestfile() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">" //
        + "<command name=\"Use Module\" line=\"4\">" //
        + "<testfile file=\"/public/module.wet\">" //
        + "<command name=\"Assert Content\" line=\"5\"><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "<executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 1, 0, 0, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 1, 0, 0, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 1, 0, 0, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 1, 0, 0, true, false,
          tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandOneNestedTestfileOneError() throws XMLStreamException,
      IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">" //
        + "<command name=\"Use Module\" line=\"4\">" //
        + "<testfile file=\"/public/module.wet\">" //
        + "<command name=\"Assert Content\" line=\"5\"><error><message>A really big problem</message></error><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "<executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/module.wet", 5, CauseType.ERROR, "Assert Content", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommandOneNestedTestfileOneFailure() throws XMLStreamException,
      IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
        + "<testrun browser=\"Firefox3.6\">" //
        + "<testfile file=\"/public/sample.wet\">" //
        + "<command name=\"Use Module\" line=\"4\">" //
        + "<testfile file=\"/public/module.wet\">" //
        + "<command name=\"Assert Content\" line=\"5\"><failure><message>A really big problem</message></failure><executionTime>23</executionTime></command>" //
        + "</testfile>" //
        + "<executionTime>12</executionTime></command>" //
        + "</testfile>" //
        + "</testrun>" //
        + "</testcase>" //
        + createFooter();
    InputStream tmpInputStream = new ByteArrayInputStream(tmpResult.getBytes());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);
      ResultAssert.assertTestResults(1234, 0, 0, 1, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      ResultAssert.assertTestResult("20.12.2010 07:11:07", 1234, 0, 0, 1, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("sample.wet", "/public/sample.wet", 35, 0, 0, 1, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 35, 0, 0, 1, false, false,
          tmpBrowserResult);

      StepError tmpStepError = (StepError) tmpBrowserResult.getError();
      ResultAssert.assertStepError("/public/module.wet", 5, CauseType.FAILURE, "Assert Content", 0,
          "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void wetatorResultXML() throws XMLStreamException, IOException {
    String tmpFilename = "wetresult.xml";
    URL tmpResource = WetatorResultParserTest.class.getClassLoader().getResource(tmpFilename);
    Assert.assertNotNull(tmpResource);
    InputStream tmpInputStream = new FileInputStream(tmpResource.getFile());
    try {
      TestResults tmpTestResults = WetatorResultParser.parse(tmpInputStream);

      ResultAssert.assertTestResults(347, 3, 0, 7, 1, tmpTestResults);
      Assert.assertEquals(5, tmpTestResults.getTestFileMap().size());
      Assert.assertEquals(3, tmpTestResults.getPassedTests().size());
      Assert.assertEquals(7, tmpTestResults.getFailedTests().size());

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      Assert.assertEquals("20.12.2010 07:11:07", tmpTestResult.getName());
      Assert.assertEquals(347, tmpTestResult.getDuration());
      Assert.assertEquals(5, tmpTestResult.getTestFileResults().size());

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      ResultAssert.assertTestFileResult("test1.wet", "/public/test1.wet", 7, 2, 0, 0, 2, tmpTestFileResult);
      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE8", "test1.wet[IE8]", 3, 1, 0, 0, true, false, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert
          .assertBrowserResult("Firefox3.6", "test1.wet[Firefox3.6]", 4, 1, 0, 0, true, false, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      ResultAssert.assertTestFileResult("test2.wet", "/public/test2.wet", 45, 0, 0, 2, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE8", "test2.wet[IE8]", 18, 0, 0, 1, false, false, tmpBrowserResult);
      ResultAssert.assertStepError("/public/test2.wet", 3, CauseType.FAILURE, "command", 1, "test failure",
          (StepError) tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("Firefox3.6", "test2.wet[Firefox3.6]", 27, 0, 0, 1, false, false,
          tmpBrowserResult);
      ResultAssert.assertStepError("/public/test2.wet", 3, CauseType.FAILURE, "command", 1, "test failure",
          (StepError) tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(2);
      ResultAssert.assertTestFileResult("test3.wet", "/public/test3.wet", 50, 0, 0, 2, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE8", "test3.wet[IE8]", 23, 0, 0, 1, false, false, tmpBrowserResult);
      ResultAssert.assertStepError("/public/test3.wet", 3, CauseType.ERROR, "command", 1, "test error",
          (StepError) tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("Firefox3.6", "test3.wet[Firefox3.6]", 27, 0, 0, 1, false, false,
          tmpBrowserResult);
      ResultAssert.assertStepError("/public/test3.wet", 3, CauseType.ERROR, "command", 1, "test error",
          (StepError) tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(3);
      ResultAssert.assertTestFileResult("test4.wet", "/public/test4.wet", 148, 0, 0, 2, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE8", "test4.wet[IE8]", 66, 0, 0, 1, false, false, tmpBrowserResult);
      ResultAssert.assertStepError("/public/test4.wet", 3, CauseType.FAILURE, "command", 1, "test failure",
          (StepError) tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("Firefox3.6", "test4.wet[Firefox3.6]", 82, 0, 0, 1, false, false,
          tmpBrowserResult);
      ResultAssert.assertStepError("/public/test4.wet", 3, CauseType.FAILURE, "command", 1, "test failure",
          (StepError) tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(4);
      ResultAssert.assertTestFileResult("test5.wet", "/public/test5.wet", 72, 1, 0, 1, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      ResultAssert.assertBrowserResult("IE8", "test5.wet[IE8]", 23, 1, 0, 0, true, false, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      ResultAssert.assertBrowserResult("Firefox3.6", "test5.wet[Firefox3.6]", 49, 0, 0, 1, false, false,
          tmpBrowserResult);
      ResultAssert.assertStepError("/public/test5.wet", 3, CauseType.ERROR, "command", 1, "test error",
          (StepError) tmpBrowserResult.getError());
    } finally {
      tmpInputStream.close();
    }
  }

  private String createHeader() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" //
        + "<wet><startTime>20.12.2010 07:11:07</startTime>";
  }

  private String createFooter() {
    return "<executionTime>1234</executionTime></wet>";
  }
}
