/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.jenkins.xstream;

import hudson.util.XStream2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.wetator.jenkins.PluginImpl;
import org.wetator.jenkins.WetatorBuildReport;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.test.ResultAssert;
import org.wetator.jenkins.util.GZIPXMLFile;

import com.thoughtworks.xstream.XStream;

/**
 * Tests the XStream deserialization of legacy XML.<br/>
 * As the implementation is mainly private there is a copy in this test class. The original code is in
 * {@link WetatorBuildReport}.
 *
 * @author frank.danek
 */
public class XStreamLegacyDeserializationTest {

  private static final Logger LOG = Logger.getLogger(XStreamLegacyDeserializationTest.class.getName());

  private static final XStream XSTREAM = new XStream2();
  static {
    WetatorBuildReport.initializeXStream(XSTREAM);
  }

  private static final String INPUT_DIR = "src/test/resources/org/wetator/jenkins/xstream/input/0.2/";
  private static final File RESULT_FILE = new File("work", PluginImpl.TEST_RESULTS_FILE_NAME);

  private GZIPXMLFile getDataFile() {
    return new GZIPXMLFile(XSTREAM, RESULT_FILE);
  }

  public TestResults load() {
    TestResults tmpTestResults;
    try {
      tmpTestResults = (TestResults) getDataFile().read();
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Failed to load " + getDataFile());
      tmpTestResults = new TestResults("dummy"); // return a dummy
    }
    tmpTestResults.tally();
    tmpTestResults.setOwner(null);
    return tmpTestResults;
  }

  private void gzip(String aFileName) throws IOException {
    RESULT_FILE.delete();
    RESULT_FILE.getParentFile().mkdirs();
    InputStream tmpInputStream = new FileInputStream(INPUT_DIR + aFileName);
    OutputStream tmpOutputStream = new GZIPOutputStream(new FileOutputStream(RESULT_FILE));
    IOUtils.copy(tmpInputStream, tmpOutputStream);
    tmpInputStream.close();
    tmpOutputStream.close();
  }

  @Test
  public void oneTestFileOneBrowser() throws Exception {
    gzip("oneTestFileOneBrowser.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 1, 0, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 0, 0, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 2, 1, 0, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, 0, true, false, tmpBrowser);
  }

  @Test
  public void oneTestFileTwoBrowser() throws Exception {
    gzip("oneTestFileTwoBrowser.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 2, 0, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 2, 0, 0, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 5, 2, 0, 0, 2, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, 0, true, false, tmpBrowser);

    tmpBrowser = tmpTestFile.getBrowserResults().get(1);
    ResultAssert.assertBrowserResult("FF3.6", "Test.wet[FF3.6]", 3, 1, 0, 0, true, false, tmpBrowser);
  }

  @Test
  public void twoTestFileOneBrowser() throws Exception {
    gzip("twoTestFileOneBrowser.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 2, 0, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 2, 0, 0, 2, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test1.wet", "/public/Test1.wet", 2, 1, 0, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test1.wet[IE8]", 2, 1, 0, 0, true, false, tmpBrowser);

    tmpTestFile = tmpTest.getTestFileResults().get(1);
    ResultAssert.assertTestFileResult("Test2.wet", "/public/Test2.wet", 3, 1, 0, 0, 1, tmpTestFile);

    tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test2.wet[IE8]", 3, 1, 0, 0, true, false, tmpBrowser);
  }

  @Test
  public void oneTestFileOneBrowserOneStepError() throws Exception {
    gzip("oneTestFileOneBrowserOneStepError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 0, 0, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 0, 0, 1, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 2, 0, 0, 1, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 0, 0, 1, false, false, tmpBrowser);

    StepError tmpError = (StepError) tmpBrowser.getError();
    ResultAssert.assertStepError(null, 2, CauseType.ERROR, "open-url", "error", tmpError, "param1", "param2");
  }

  @Test
  public void oneTestFileTwoBrowserOneStepError() throws Exception {
    gzip("oneTestFileTwoBrowserOneStepError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 1, 0, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 0, 1, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 5, 1, 0, 1, 2, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, 0, true, false, tmpBrowser);

    tmpBrowser = tmpTestFile.getBrowserResults().get(1);
    ResultAssert.assertBrowserResult("FF3.6", "Test.wet[FF3.6]", 3, 0, 0, 1, false, false, tmpBrowser);

    StepError tmpError = (StepError) tmpBrowser.getError();
    ResultAssert.assertStepError(null, 2, CauseType.ERROR, "open-url", "error", tmpError, "param1", "param2");
  }

  @Test
  public void twoTestFileOneBrowserOneStepError() throws Exception {
    gzip("twoTestFileOneBrowserOneStepError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 1, 0, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 0, 1, 2, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test1.wet", "/public/Test1.wet", 2, 1, 0, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test1.wet[IE8]", 2, 1, 0, 0, true, false, tmpBrowser);

    tmpTestFile = tmpTest.getTestFileResults().get(1);
    ResultAssert.assertTestFileResult("Test2.wet", "/public/Test2.wet", 3, 0, 0, 1, 1, tmpTestFile);

    tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test2.wet[IE8]", 3, 0, 0, 1, false, false, tmpBrowser);

    StepError tmpError = (StepError) tmpBrowser.getError();
    ResultAssert.assertStepError(null, 2, CauseType.ERROR, "open-url", "error", tmpError, "param1", "param2");
  }

  @Test
  public void noReportFile() throws Exception {
    gzip("noReportFile.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 0, 0, 0, 0, 0, tmpResults);
    ResultAssert.assertReportFiles(tmpResults, (String[]) null);
  }
}
