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


package org.wetator.jenkins;

import hudson.util.HeapSpaceStringConverter;
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
import org.junit.Assert;
import org.junit.Test;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
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
    XSTREAM.alias("testResults", TestResults.class);
    XSTREAM.alias("testResult", TestResult.class);
    XSTREAM.alias("testFileResult", TestFileResult.class);
    XSTREAM.alias("browserResult", BrowserResult.class);
    XSTREAM.alias("stepError", StepError.class);
    XSTREAM.registerConverter(new HeapSpaceStringConverter(), 100);
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

    ResultAssert.assertTestResults("TestResults", 10, 1, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 0, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 2, 1, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, true, tmpBrowser);
  }

  @Test
  public void oneTestFileTwoBrowser() throws Exception {
    gzip("oneTestFileTwoBrowser.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 2, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 0, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 5, 2, 0, 2, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, true, tmpBrowser);

    tmpBrowser = tmpTestFile.getBrowserResults().get(1);
    ResultAssert.assertBrowserResult("FF3.6", "Test.wet[FF3.6]", 3, 1, 0, true, tmpBrowser);
  }

  @Test
  public void twoTestFileOneBrowser() throws Exception {
    gzip("twoTestFileOneBrowser.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 2, 0, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 2, 0, 2, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test1.wet", "/public/Test1.wet", 2, 1, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test1.wet[IE8]", 2, 1, 0, true, tmpBrowser);

    tmpTestFile = tmpTest.getTestFileResults().get(1);
    ResultAssert.assertTestFileResult("Test2.wet", "/public/Test2.wet", 3, 1, 0, 1, tmpTestFile);

    tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test2.wet[IE8]", 3, 1, 0, true, tmpBrowser);
  }

  @Test
  public void oneTestFileOneBrowserOneError() throws Exception {
    gzip("oneTestFileOneBrowserOneError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 0, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 0, 1, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 2, 0, 1, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 0, 1, false, tmpBrowser);

    StepError tmpError = tmpBrowser.getError();
    ResultAssert.assertStepError(2, "open-url", 2, "error", tmpError);
    Assert.assertEquals("param1", tmpError.getParameters().get(0));
    Assert.assertEquals("param2", tmpError.getParameters().get(1));
  }

  @Test
  public void oneTestFileTwoBrowserOneError() throws Exception {
    gzip("oneTestFileTwoBrowserOneError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 1, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 0, 1, 1, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test.wet", "/public/Test.wet", 5, 1, 1, 2, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test.wet[IE8]", 2, 1, 0, true, tmpBrowser);

    tmpBrowser = tmpTestFile.getBrowserResults().get(1);
    ResultAssert.assertBrowserResult("FF3.6", "Test.wet[FF3.6]", 3, 0, 1, false, tmpBrowser);

    StepError tmpError = tmpBrowser.getError();
    ResultAssert.assertStepError(2, "open-url", 2, "error", tmpError);
    Assert.assertEquals("param1", tmpError.getParameters().get(0));
    Assert.assertEquals("param2", tmpError.getParameters().get(1));
  }

  @Test
  public void twoTestFileOneBrowserOneError() throws Exception {
    gzip("twoTestFileOneBrowserOneError.xml");

    TestResults tmpResults = load();

    ResultAssert.assertTestResults("TestResults", 10, 1, 1, 1, tmpResults);

    TestResult tmpTest = tmpResults.getTestResults().get(0);
    ResultAssert.assertTestResult("123456789", 10, 1, 1, 2, tmpTest);

    TestFileResult tmpTestFile = tmpTest.getTestFileResults().get(0);
    ResultAssert.assertTestFileResult("Test1.wet", "/public/Test1.wet", 2, 1, 0, 1, tmpTestFile);

    BrowserResult tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test1.wet[IE8]", 2, 1, 0, true, tmpBrowser);

    tmpTestFile = tmpTest.getTestFileResults().get(1);
    ResultAssert.assertTestFileResult("Test2.wet", "/public/Test2.wet", 3, 0, 1, 1, tmpTestFile);

    tmpBrowser = tmpTestFile.getBrowserResults().get(0);
    ResultAssert.assertBrowserResult("IE8", "Test2.wet[IE8]", 3, 0, 1, false, tmpBrowser);

    StepError tmpError = tmpBrowser.getError();
    ResultAssert.assertStepError(2, "open-url", 2, "error", tmpError);
    Assert.assertEquals("param1", tmpError.getParameters().get(0));
    Assert.assertEquals("param2", tmpError.getParameters().get(1));
  }
}
