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


package org.wetator.jenkins.test;

import org.junit.Assert;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestError;
import org.wetator.jenkins.result.TestError.ErrorType;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;

/**
 * Asserts for the result objects.
 *
 * @author frank.danek
 */
public class ResultAssert {

  public static void assertTestResults(long aDuration, int aPassCount, int aSkipCount, int aFailCount,
      int aTestResultCount, TestResults anActualTestResults) {
    assertTestResults(null, aDuration, aPassCount, aSkipCount, aFailCount, aTestResultCount, anActualTestResults);
  }

  public static void assertTestResults(String aName, long aDuration, int aPassCount, int aSkipCount, int aFailCount,
      int aTestResultCount, TestResults anActualTestResults) {
    if (aName != null) {
      Assert.assertEquals("Name", aName, anActualTestResults.getName());
    }
    Assert.assertEquals("Duration", aDuration, anActualTestResults.getDuration());
    Assert.assertEquals("PassCount", aPassCount, anActualTestResults.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, anActualTestResults.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, anActualTestResults.getFailCount());
    Assert.assertEquals("TotalCount", aPassCount + aSkipCount + aFailCount, anActualTestResults.getTotalCount());
    Assert.assertEquals("TestResults.size", aTestResultCount, anActualTestResults.getTestResults().size());
  }

  public static void assertTestResult(String aName, long aDuration, int aPassCount, int aSkipCount, int aFailCount,
      int aTestFileResultCount, TestResult anActualTestResult) {
    Assert.assertEquals("Name", aName, anActualTestResult.getName());
    Assert.assertNull("FullName", anActualTestResult.getFullName());
    Assert.assertEquals("DisplayName", aName, anActualTestResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, anActualTestResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, anActualTestResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, anActualTestResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, anActualTestResult.getFailCount());
    Assert.assertEquals("TestFileResults.size", aTestFileResultCount, anActualTestResult.getTestFileResults().size());
  }

  public static void assertTestFileResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aSkipCount, int aFailCount, int aBrowserResultCount, TestFileResult anActualTestFileResult) {
    Assert.assertEquals("Name", aName, anActualTestFileResult.getName());
    Assert.assertEquals("FullName", aFullName, anActualTestFileResult.getFullName());
    Assert.assertEquals("DisplayName", aName, anActualTestFileResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, anActualTestFileResult.getDuration());
    Assert.assertEquals("TotalCount", aPassCount + aSkipCount + aFailCount, anActualTestFileResult.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, anActualTestFileResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, anActualTestFileResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, anActualTestFileResult.getFailCount());
    Assert.assertEquals("BrowserResults.size", aBrowserResultCount, anActualTestFileResult.getBrowserResults().size());
  }

  public static void assertBrowserResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aSkipCount, int aFailCount, boolean aPassed, boolean aSkipped, BrowserResult anActualBrowserResult) {
    Assert.assertEquals("Name", aName, anActualBrowserResult.getName());
    Assert.assertEquals("FullName", aFullName, anActualBrowserResult.getFullName());
    Assert.assertEquals("DisplayName", aName, anActualBrowserResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, anActualBrowserResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, anActualBrowserResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, anActualBrowserResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, anActualBrowserResult.getFailCount());
    if (aPassed && aSkipped) {
      Assert.fail("wrong usage");
    } else if (aPassed) {
      Assert.assertTrue("Passed", anActualBrowserResult.isPassed());
      Assert.assertFalse("Skipped", anActualBrowserResult.isSkipped());
      Assert.assertNull("Error", anActualBrowserResult.getError());
    } else if (aSkipped) {
      Assert.assertFalse("Passed", anActualBrowserResult.isPassed());
      Assert.assertTrue("Skipped", anActualBrowserResult.isSkipped());
      Assert.assertNull("Error", anActualBrowserResult.getError());
    } else {
      Assert.assertFalse("Passed", anActualBrowserResult.isPassed());
      Assert.assertFalse("Skipped", anActualBrowserResult.isSkipped());
      Assert.assertNotNull("Error", anActualBrowserResult.getError());
    }
  }

  public static void assertTestError(String aFile, String anError, TestError anActualTestError) {
    Assert.assertEquals(TestError.class, anActualTestError.getClass());
    Assert.assertEquals("Type", ErrorType.TEST, anActualTestError.getType());
    Assert.assertEquals("File", aFile, anActualTestError.getFile());
    Assert.assertEquals("Error", anError, anActualTestError.getError());
  }

  public static void assertStepError(String aFile, int aLine, CauseType aCauseType, String aCommand, String anError,
      StepError anActualStepError, String... aParameterValues) {
    Assert.assertEquals(StepError.class, anActualStepError.getClass());
    Assert.assertEquals("Type", ErrorType.STEP, anActualStepError.getType());
    Assert.assertEquals("File", aFile, anActualStepError.getFile());
    Assert.assertEquals("Line", aLine, anActualStepError.getLine());
    Assert.assertEquals("CauseType", aCauseType, anActualStepError.getCauseType());
    Assert.assertEquals("Command", aCommand, anActualStepError.getCommand());
    if (aParameterValues == null) {
      Assert.assertNull("Parameters", anActualStepError.getParameters());
    } else {
      Assert.assertEquals("Parameters.size", aParameterValues.length, anActualStepError.getParameters().size());
      if (aParameterValues.length > 0) {
        Assert.assertEquals("Parameters.0", aParameterValues[0], anActualStepError.getParameters().get(0));
      }
      if (aParameterValues.length > 1) {
        Assert.assertEquals("Parameters.1", aParameterValues[1], anActualStepError.getParameters().get(1));
      }
      if (aParameterValues.length > 2) {
        Assert.assertEquals("Parameters.2", aParameterValues[2], anActualStepError.getParameters().get(2));
      }
    }
    Assert.assertEquals("Error", anError, anActualStepError.getError());
  }

  public static void assertReportFiles(TestResults aTestResults, String... aReportFiles) {
    if (aReportFiles == null) {
      Assert.assertNull(aTestResults.getReportFiles());
    } else {
      Assert.assertEquals(aReportFiles.length, aTestResults.getReportFiles().size());
      for (String tmpReportFile : aReportFiles) {
        Assert.assertTrue("ReportFile", aTestResults.getReportFiles().contains(tmpReportFile));
      }
    }
  }
}
