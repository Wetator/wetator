/*
 * Copyright (c) 2008-2011 wetator.org
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
import org.wetator.jenkins.result.BrowserResult.Status;
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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
  public void oneTestcaseOneBrowserOneTestfileTwoCommands() throws XMLStreamException, IOException {
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command>"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command>"
        + "</testfile></testrun></testcase><executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime><error><message>A really big problem</message></error></command>"
        + "</testfile></testrun></testcase><executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime><error><message>A really big problem</message></error></command>"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime><error><message>Another problem</message></error></command>"
        + "</testfile></testrun></testcase><executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command></testfile></testrun></testcase>"
        + "<testcase name=\"sample2.wet\"><testrun browser=\"IE6\"><testfile file=\"/public/sample2.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command></testfile></testrun></testcase>"
        + "<testcase name=\"sample2.wet\"><testrun browser=\"IE6\"><testfile file=\"/public/sample2.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime><error><message>A really big problem</message></error></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command></testfile></testrun>"
        + "<testrun browser=\"IE6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Assert Title\" line=\"4\"><executionTime>12</executionTime></command></testfile></testrun>"
        + "<testrun browser=\"IE6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Open Url\" line=\"5\"><executionTime>23</executionTime><error><message>A really big problem</message></error></command>"
        + "</testfile></testrun></testcase><executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Use Module\" line=\"4\">"
        + "<testfile file=\"/public/module.wet\"><command name=\"Assert Content\" line=\"5\"><executionTime>23</executionTime></command></testfile>"
        + "<executionTime>12</executionTime></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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
    String tmpResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<wet><startTime>20.12.2010 07:11:07</startTime>"
        + "<testcase name=\"sample.wet\"><testrun browser=\"Firefox3.6\"><testfile file=\"/public/sample.wet\">"
        + "<command name=\"Use Module\" line=\"4\">"
        + "<testfile file=\"/public/module.wet\"><command name=\"Assert Content\" line=\"5\"><executionTime>23</executionTime><error><message>A really big problem</message></error></command></testfile>"
        + "<executionTime>12</executionTime></command></testfile></testrun></testcase>"
        + "<executionTime>1234</executionTime></wet>";
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

      Assert.assertEquals(2215, tmpTestResults.getDuration());
      Assert.assertEquals(1, tmpTestResults.getPassCount());
      Assert.assertEquals(1, tmpTestResults.getFailCount());
      Assert.assertEquals(2, tmpTestResults.getTotalCount());
      Assert.assertEquals(1, tmpTestResults.getTestResults().size());
      Assert.assertEquals(1, tmpTestResults.getTestFileMap().size());
      Assert.assertEquals(1, tmpTestResults.getPassedTests().size());
      Assert.assertEquals(1, tmpTestResults.getFailedTests().size());

      TestResult tmpTestResult = tmpTestResults.getTestResults().get(0);
      Assert.assertEquals("20.12.2010 07:11:07", tmpTestResult.getName());
      Assert.assertEquals(2215, tmpTestResult.getDuration());
      Assert.assertEquals(1, tmpTestResult.getTestFileResults().size());

      TestFileResult tmpTestFileResult = tmpTestResult.getTestFileResults().get(0);
      Assert.assertEquals("sample.xls", tmpTestFileResult.getName());
      Assert.assertEquals("E:\\Java\\workspaces\\wetator\\wetator\\test\\ant\\sample.xls",
          tmpTestFileResult.getFullName());
      Assert.assertEquals(2403, tmpTestFileResult.getDuration());
      Assert.assertEquals(1, tmpTestFileResult.getFailCount());
      Assert.assertEquals(2, tmpTestFileResult.getTotalCount());
      Assert.assertEquals(2, tmpTestFileResult.getBrowserResults().size());

      BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(0);
      Assert.assertEquals("Firefox3.6", tmpBrowserResult.getName());
      Assert.assertEquals("sample.xls[Firefox3.6]", tmpBrowserResult.getFullName());
      Assert.assertEquals(1201, tmpBrowserResult.getDuration());
      Assert.assertNull(tmpBrowserResult.getError());
      Assert.assertEquals(Status.PASSED, tmpBrowserResult.getStatus());

      tmpBrowserResult = tmpTestFileResult.getBrowserResults().get(1);
      Assert.assertEquals("IE6", tmpBrowserResult.getName());
      Assert.assertEquals("sample.xls[IE6]", tmpBrowserResult.getFullName());
      Assert.assertEquals(1202, tmpBrowserResult.getDuration());
      Assert.assertNotNull(tmpBrowserResult.getError());
      Assert.assertEquals(Status.FAILED, tmpBrowserResult.getStatus());

      StepError tmpStepError = tmpBrowserResult.getError();
      Assert.assertEquals(4, tmpStepError.getLine());
      Assert.assertEquals("Assert Title", tmpStepError.getCommand());
      Assert.assertEquals(1, tmpStepError.getParameters().size());
      Assert.assertEquals("Wetator / Smarter web application testing", tmpStepError.getParameters().get(0));
      Assert.assertEquals("A really big problem", tmpStepError.getError());
    } finally {
      tmpInputStream.close();
    }
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
