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
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;

/**
 * Asserts for the result objects.
 * 
 * @author frank.danek
 */
public class ResultAssert {

  public static void assertTestResults(long aDuration, int aPassCount, int aFailCount, int aTestResultCount,
      TestResults aTestResults) {
    assertTestResults(null, aDuration, aPassCount, aFailCount, aTestResultCount, aTestResults);
  }

  public static void assertTestResults(String aName, long aDuration, int aPassCount, int aFailCount,
      int aTestResultCount, TestResults aTestResults) {
    if (aName != null) {
      Assert.assertEquals("Name", aName, aTestResults.getName());
    }
    Assert.assertEquals("Duration", aDuration, aTestResults.getDuration());
    Assert.assertEquals("TotalCount", aPassCount + aFailCount, aTestResults.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, aTestResults.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResults.getFailCount());
    Assert.assertEquals("TestResults.size", aTestResultCount, aTestResults.getTestResults().size());
  }

  public static void assertTestResult(String aName, long aDuration, int aPassCount, int aFailCount,
      int aTestFileResultCount, TestResult aTestResult) {
    Assert.assertEquals("Name", aName, aTestResult.getName());
    Assert.assertNull("FullName", aTestResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, aTestResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, aTestResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestResult.getFailCount());
    Assert.assertEquals("TestFileResults.size", aTestFileResultCount, aTestResult.getTestFileResults().size());
  }

  public static void assertTestFileResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aFailCount, int aBrowserResultCount, TestFileResult aTestFileResult) {
    Assert.assertEquals("Name", aName, aTestFileResult.getName());
    Assert.assertEquals("FullName", aFullName, aTestFileResult.getFullName());
    Assert.assertEquals("DisplayName", aName, aTestFileResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, aTestFileResult.getDuration());
    Assert.assertEquals("TotalCount", aPassCount + aFailCount, aTestFileResult.getTotalCount());
    Assert.assertEquals("PassCount", aPassCount, aTestFileResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, aTestFileResult.getFailCount());
    Assert.assertEquals("BrowserResults.size", aBrowserResultCount, aTestFileResult.getBrowserResults().size());
  }

  public static void assertBrowserResult(String aName, String aFullName, long aDuration, int aPassCount,
      int aFailCount, boolean aPassed, BrowserResult tmpBrowserResult) {
    Assert.assertEquals("Name", aName, tmpBrowserResult.getName());
    Assert.assertEquals("FullName", aFullName, tmpBrowserResult.getFullName());
    Assert.assertEquals("DisplayName", aName, tmpBrowserResult.getDisplayName());
    Assert.assertEquals("Duration", aDuration, tmpBrowserResult.getDuration());
    Assert.assertEquals("PassCount", aPassCount, tmpBrowserResult.getPassCount());
    Assert.assertEquals("FailCount", aFailCount, tmpBrowserResult.getFailCount());
    if (aPassed) {
      Assert.assertTrue("Passed", tmpBrowserResult.isPassed());
      Assert.assertNull("Error", tmpBrowserResult.getError());
    } else {
      Assert.assertFalse("Passed", tmpBrowserResult.isPassed());
      Assert.assertNotNull("Error", tmpBrowserResult.getError());
    }
  }

  public static void assertStepError(int aLine, String aCommand, int aParameterCount, String anError,
      StepError aStepError) {
    Assert.assertEquals("Line", aLine, aStepError.getLine());
    Assert.assertEquals("Command", aCommand, aStepError.getCommand());
    Assert.assertEquals("Parameters.size", aParameterCount, aStepError.getParameters().size());
    Assert.assertEquals("Error", anError, aStepError.getError());
  }
}
