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
      int aTestResultCount, TestResults aTestResults) {
    assertTestResults(null, aDuration, aPassCount, aSkipCount, aFailCount, aTestResultCount, aTestResults);
  }

  public static void assertTestResults(String aName, long aDuration, int aPassCount, int aSkipCount, int aFailCount,
      int aTestResultCount, TestResults aTestResults) {
    if (aName != null) {
      Assert.assertEquals("Name", aName, aTestResults.getName());
    }
    Assert.assertEquals("Duration", aDuration, aTestResults.getDuration());
    Assert.assertEquals("TotalCount", aPassCount + aSkipCount + aFailCount, aTestResults.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, aTestResults.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, aTestResults.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResults.getFailCount());
    Assert.assertEquals("TestResults.size", aTestResultCount, aTestResults.getTestResults().size());
  }

  public static void assertTestResult(String aName, long aDuration, int aPassCount, int aSkipCount, int aFailCount,
      int aTestFileResultCount, TestResult aTestResult) {
    Assert.assertEquals("Name", aName, aTestResult.getName());
    Assert.assertNull("FullName", aTestResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, aTestResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, aTestResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, aTestResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResult.getFailCount());
    Assert.assertEquals("TestFileResults.size", aTestFileResultCount, aTestResult.getTestFileResults().size());
  }

  public static void assertTestFileResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aSkipCount, int aFailCount, int aBrowserResultCount, TestFileResult aTestFileResult) {
    Assert.assertEquals("Name", aName, aTestFileResult.getName());
    Assert.assertEquals("FullName", aFullName, aTestFileResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestFileResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, aTestFileResult.getDuration());
    Assert.assertEquals("TotalCount", aPassCount + aSkipCount + aFailCount, aTestFileResult.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, aTestFileResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, aTestFileResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, aTestFileResult.getFailCount());
    Assert.assertEquals("BrowserResults.size", aBrowserResultCount, aTestFileResult.getBrowserResults().size());
  }

  public static void assertBrowserResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aSkipCount, int aFailCount, boolean aPassed, boolean aSkipped, BrowserResult aBrowserResult) {
    Assert.assertEquals("Name", aName, aBrowserResult.getName());
    Assert.assertEquals("FullName", aFullName, aBrowserResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aBrowserResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, aBrowserResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, aBrowserResult.getPassCount());
    Assert.assertEquals("SkipCount", aSkipCount, aBrowserResult.getSkipCount());
    Assert.assertEquals("FailCount", aFailCount, aBrowserResult.getFailCount());
    if (aPassed && aSkipped) {
      Assert.fail("wrong usage");
    } else if (aPassed) {
      Assert.assertTrue("Passed", aBrowserResult.isPassed());
      Assert.assertFalse("Skipped", aBrowserResult.isSkipped());
      Assert.assertNull("Error", aBrowserResult.getError());
    } else if (aSkipped) {
      Assert.assertFalse("Passed", aBrowserResult.isPassed());
      Assert.assertTrue("Skipped", aBrowserResult.isSkipped());
      Assert.assertNull("Error", aBrowserResult.getError());
    } else {
      Assert.assertFalse("Passed", aBrowserResult.isPassed());
      Assert.assertFalse("Skipped", aBrowserResult.isSkipped());
      Assert.assertNotNull("Error", aBrowserResult.getError());
    }
  }

  public static void assertTestError(String aFile, String anError, TestError aTestError) {
    Assert.assertEquals(TestError.class, aTestError.getClass());
    Assert.assertEquals("Type", ErrorType.TEST, aTestError.getType());
    Assert.assertEquals("File", aFile, aTestError.getFile());
    Assert.assertEquals("Error", anError, aTestError.getError());
  }

  public static void assertStepError(String aFile, int aLine, CauseType aCauseType, String aCommand,
      int aParameterCount, String anError, StepError aStepError) {
    Assert.assertEquals(StepError.class, aStepError.getClass());
    Assert.assertEquals("Type", ErrorType.STEP, aStepError.getType());
    Assert.assertEquals("File", aFile, aStepError.getFile());
    Assert.assertEquals("Line", aLine, aStepError.getLine());
    Assert.assertEquals("CauseType", aCauseType, aStepError.getCauseType());
    Assert.assertEquals("Command", aCommand, aStepError.getCommand());
    if (aStepError.getParameters() == null) {
      Assert.assertEquals("Parameters.size", aParameterCount, 0);
    } else {
      Assert.assertEquals("Parameters.size", aParameterCount, aStepError.getParameters().size());
    }
    Assert.assertEquals("Error", anError, aStepError.getError());
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
