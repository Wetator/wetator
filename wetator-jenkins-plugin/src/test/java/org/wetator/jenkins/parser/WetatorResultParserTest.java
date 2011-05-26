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

import java.io.File;
import java.net.URL;

import org.dom4j.DocumentException;
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

  @Test
  public void wetatorResultXML() throws DocumentException {
    String tmpFilename = "wetresult.xml";
    URL tmpResource = WetatorResultParserTest.class.getClassLoader().getResource(tmpFilename);
    Assert.assertNotNull(tmpResource);
    TestResults tmpTestResults = WetatorResultParser.parse(new File(tmpResource.getFile()));

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
  }
}
