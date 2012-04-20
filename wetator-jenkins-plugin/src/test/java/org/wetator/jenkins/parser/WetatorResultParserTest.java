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
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;

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
      assertTestResults(0, 0, 0, 0, tmpTestResults);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneCommand() throws XMLStreamException, IOException {
    String tmpResult = createHeader() //
        + "<testcase name=\"sample.wet\" file=\"/public/sample.wet\">" //
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
      assertTestResults(1, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 0, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);
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
      assertTestResults(1, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 0, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("test/sample.wet", "/public/test/sample.wet", 1, 0, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "test/sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);
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
      assertTestResults(1, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 0, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 35, true, tmpBrowserResult);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneError() throws XMLStreamException, IOException {
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 12, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(4, "Assert Title", 0, "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileOneFailure() throws XMLStreamException, IOException {
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 12, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(4, "Assert Title", 0, "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileTwoErrors() throws XMLStreamException, IOException {
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 35, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(4, "Assert Title", 0, "A really big problem", tmpStepError);
    } finally {
      tmpInputStream.close();
    }
  }

  @Test
  public void oneTestcaseOneBrowserOneTestfileTwoFailures() throws XMLStreamException, IOException {
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 35, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(4, "Assert Title", 0, "A really big problem", tmpStepError);
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
      assertTestResults(2, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 2, 0, 1234, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult("sample2.wet", "/public/sample2.wet", 1, 0, 23, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE6", "sample2.wet[IE6]", 1, 0, 23, true, tmpBrowserResult);
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
      assertTestResults(1, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 1, 1234, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult("sample2.wet", "/public/sample2.wet", 0, 1, 23, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE6", "sample2.wet[IE6]", 0, 1, 23, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Open Url", 0, "A really big problem", tmpStepError);
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
      assertTestResults(1, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 1, 1234, 2, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 12, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult("sample2.wet", "/public/sample2.wet", 0, 1, 23, 1, tmpTestFileResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE6", "sample2.wet[IE6]", 0, 1, 23, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Open Url", 0, "A really big problem", tmpStepError);
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
      assertTestResults(2, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 0, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 2, 0, 35, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("IE6", "sample.wet[IE6]", 1, 0, 23, true, tmpBrowserResult);
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
      assertTestResults(1, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 1, 35, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("IE6", "sample.wet[IE6]", 0, 1, 23, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Open Url", 0, "A really big problem", tmpStepError);
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
      assertTestResults(1, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 1, 35, 2, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 12, true, tmpBrowserResult);

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("IE6", "sample.wet[IE6]", 0, 1, 23, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Open Url", 0, "A really big problem", tmpStepError);
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
      assertTestResults(1, 0, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 1, 0, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 1, 0, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 1, 0, 35, true, tmpBrowserResult);
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 35, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Assert Content", 0, "A really big problem", tmpStepError);
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
      assertTestResults(0, 1, 1234, 1, tmpTestResults);

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      assertTestResult("20.12.2010 07:11:07", 0, 1, 1234, 1, tmpTestResult);

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("sample.wet", "/public/sample.wet", 0, 1, 35, 1, tmpTestFileResult);

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("Firefox3.6", "sample.wet[Firefox3.6]", 0, 1, 35, false, tmpBrowserResult);

      StepError tmpStepError = tmpBrowserResult.getError();
      assertStepError(5, "Assert Content", 0, "A really big problem", tmpStepError);
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

      assertTestResults(3, 7, 347, 1, tmpTestResults);
      Assert.assertEquals(5, tmpTestResults.getTestFileMap().size());
      Assert.assertEquals(3, tmpTestResults.getPassedTests().size());
      Assert.assertEquals(7, tmpTestResults.getFailedTests().size());

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      Assert.assertEquals("20.12.2010 07:11:07", tmpTestResult.getName());
      Assert.assertEquals(347, tmpTestResult.getDuration());
      Assert.assertEquals(5, tmpTestResult.getTestFileResults().size());

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      assertTestFileResult("test1.wet", "/public/test1.wet", 2, 0, 7, 2, tmpTestFileResult);
      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE8", "test1.wet[IE8]", 1, 0, 3, true, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("Firefox3.6", "test1.wet[Firefox3.6]", 1, 0, 4, true, tmpBrowserResult);

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(1);
      assertTestFileResult("test2.wet", "/public/test2.wet", 0, 2, 45, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE8", "test2.wet[IE8]", 0, 1, 18, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test failure", tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("Firefox3.6", "test2.wet[Firefox3.6]", 0, 1, 27, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test failure", tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(2);
      assertTestFileResult("test3.wet", "/public/test3.wet", 0, 2, 50, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE8", "test3.wet[IE8]", 0, 1, 23, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test error", tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("Firefox3.6", "test3.wet[Firefox3.6]", 0, 1, 27, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test error", tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(3);
      assertTestFileResult("test4.wet", "/public/test4.wet", 0, 2, 148, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE8", "test4.wet[IE8]", 0, 1, 66, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test failure", tmpBrowserResult.getError());
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("Firefox3.6", "test4.wet[Firefox3.6]", 0, 1, 82, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test failure", tmpBrowserResult.getError());

      tmpTestFileResult = tmpTestResult.getTestFileResults().get(4);
      assertTestFileResult("test5.wet", "/public/test5.wet", 1, 1, 72, 2, tmpTestFileResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      assertBrowserResult("IE8", "test5.wet[IE8]", 1, 0, 23, true, tmpBrowserResult);
      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      assertBrowserResult("Firefox3.6", "test5.wet[Firefox3.6]", 0, 1, 49, false, tmpBrowserResult);
      assertStepError(3, "command", 1, "test error", tmpBrowserResult.getError());
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

  private void assertTestResults(int aPassCount, int aFailCount, long aDuration, int aTestResultCount,
      TestResults aTestResults) {
    Assert.assertEquals("TotalCount", aPassCount + aFailCount, aTestResults.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, aTestResults.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResults.getFailCount());
    Assert.assertEquals("Duration", aDuration, aTestResults.getDuration());
    Assert.assertEquals("TestResults.size", aTestResultCount, aTestResults.getTestResults().size());
  }

  private void assertTestResult(String aName, int aPassCount, int aFailCount, long aDuration, int aTestFileResultCount,
      TestResult aTestResult) {
    Assert.assertEquals("Name", aName, aTestResult.getName());
    Assert.assertNull("FullName", aTestResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestResult.getDisplayName());
    Assert.assertEquals("PassCount", aPassCount, aTestResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResult.getFailCount());
    Assert.assertEquals("Duration", aDuration, aTestResult.getDuration());
    Assert.assertEquals("TestFileResults.size", aTestFileResultCount, aTestResult.getTestFileResults().size());
  }

  private void assertTestFileResult(String aName, String aFullName, int aPassCount, int aFailCount, long aDuration,
      int aBrowserResultCount, TestFileResult aTestFileResult) {
    Assert.assertEquals("Name", aName, aTestFileResult.getName());
    Assert.assertEquals("FullName", aFullName, aTestFileResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestFileResult.getDisplayName());
    Assert.assertEquals("PassCount", aPassCount, aTestFileResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestFileResult.getFailCount());
    Assert.assertEquals("Duration", aDuration, aTestFileResult.getDuration());
    Assert.assertEquals("BrowserResults.size", aBrowserResultCount, aTestFileResult.getBrowserResults().size());
  }

  private void assertBrowserResult(String aName, String aFullName, int aPassCount, int aFailCount, long aDuration,
      boolean aPassed, BrowserResult tmpBrowserResult) {
    Assert.assertEquals("Name", aName, tmpBrowserResult.getName());
    Assert.assertEquals("FullName", aFullName, tmpBrowserResult.getFullName());
    Assert.assertEquals("DisplayName", aName, tmpBrowserResult.getDisplayName());
    Assert.assertEquals("PassCount", aPassCount, tmpBrowserResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, tmpBrowserResult.getFailCount());
    Assert.assertEquals("Duration", aDuration, tmpBrowserResult.getDuration());
    if (aPassed) {
      Assert.assertTrue("Passed", tmpBrowserResult.isPassed());
      Assert.assertNull("Error", tmpBrowserResult.getError());
    } else {
      Assert.assertFalse("Passed", tmpBrowserResult.isPassed());
      Assert.assertNotNull("Error", tmpBrowserResult.getError());
    }
  }

  private void assertStepError(int aLine, String aCommand, int aParameterCount, String anError, StepError aStepError) {
    Assert.assertEquals("Line", aLine, aStepError.getLine());
    Assert.assertEquals("Command", aCommand, aStepError.getCommand());
    Assert.assertEquals("Parameters.size", aParameterCount, aStepError.getParameters().size());
    Assert.assertEquals("Error", anError, aStepError.getError());
  }
}
