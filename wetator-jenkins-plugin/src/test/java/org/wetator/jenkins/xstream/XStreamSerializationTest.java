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


package org.wetator.jenkins.xstream;

import hudson.util.XStream2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wetator.jenkins.PluginImpl;
import org.wetator.jenkins.WetatorBuildReport;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestError;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.util.GZIPXMLFile;

import com.thoughtworks.xstream.XStream;

/**
 * Tests the XStream serialization.<br/>
 * As the implementation is mainly private there is a copy in this test class. The original code is in
 * {@link WetatorBuildReport}.
 *
 * @author frank.danek
 */
public class XStreamSerializationTest {

  private static final XStream XSTREAM = new XStream2();
  static {
    WetatorBuildReport.initializeXStream(XSTREAM);
  }

  private static final File RESULT_FILE = new File("work", PluginImpl.TEST_RESULTS_FILE_NAME);
  private static final String OUTPUT_DIR = "src/test/resources/org/wetator/jenkins/xstream/output/";

  private GZIPXMLFile getDataFile() {
    return new GZIPXMLFile(XSTREAM, RESULT_FILE);
  }

  public synchronized void write(TestResults aResults) {
    aResults.tally();

    // persist the data
    RESULT_FILE.getParentFile().mkdirs();
    try {
      getDataFile().write(aResults);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String unzip() throws IOException {
    InputStream tmpInputStream = new GZIPInputStream(new FileInputStream(RESULT_FILE));
    ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
    IOUtils.copy(tmpInputStream, tmpOutputStream);
    tmpInputStream.close();
    tmpOutputStream.close();

    return tmpOutputStream.toString("UTF-8");
  }

  private String read(String aFileName) throws IOException {
    InputStream tmpInputStream = new FileInputStream(OUTPUT_DIR + aFileName);
    ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
    IOUtils.copy(tmpInputStream, tmpOutputStream);
    tmpInputStream.close();
    tmpOutputStream.close();

    return tmpOutputStream.toString("UTF-8");
  }

  @Test
  public void oneTestFileOneBrowser() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    write(tmpResults);

    assertResult("oneTestFileOneBrowser.xml");
  }

  @Test
  public void oneTestFileTwoBrowser() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);

    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("FF3.6");
    tmpBrowser.setFullName("Test.wet[FF3.6]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    write(tmpResults);

    assertResult("oneTestFileTwoBrowser.xml");
  }

  @Test
  public void twoTestFileOneBrowser() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test1.wet");
    tmpTestFile.setFullName("/public/Test1.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test1.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test2.wet");
    tmpTestFile.setFullName("/public/Test2.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    tmpBrowserResults = new ArrayList<>();
    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test2.wet[IE8]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    write(tmpResults);

    assertResult("twoTestFileOneBrowser.xml");
  }

  @Test
  public void oneTestFileOneBrowserOneStepError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.ERROR);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileOneBrowserOneStepError.xml");
  }

  @Test
  public void oneTestFileTwoBrowserOneStepError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);

    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("FF3.6");
    tmpBrowser.setFullName("Test.wet[FF3.6]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.ERROR);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileTwoBrowserOneStepError.xml");
  }

  @Test
  public void twoTestFileOneBrowserOneStepError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test1.wet");
    tmpTestFile.setFullName("/public/Test1.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test1.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test2.wet");
    tmpTestFile.setFullName("/public/Test2.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    tmpBrowserResults = new ArrayList<>();
    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test2.wet[IE8]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.ERROR);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("twoTestFileOneBrowserOneStepError.xml");
  }

  @Test
  public void oneTestFileOneBrowserOneStepFailure() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.FAILURE);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileOneBrowserOneStepFailure.xml");
  }

  @Test
  public void oneTestFileTwoBrowserOneStepFailure() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);

    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("FF3.6");
    tmpBrowser.setFullName("Test.wet[FF3.6]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.FAILURE);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileTwoBrowserOneStepFailure.xml");
  }

  @Test
  public void twoTestFileOneBrowserOneStepFailure() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test1.wet");
    tmpTestFile.setFullName("/public/Test1.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test1.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test2.wet");
    tmpTestFile.setFullName("/public/Test2.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    tmpBrowserResults = new ArrayList<>();
    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test2.wet[IE8]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    StepError tmpError = new StepError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpError.setCauseType(CauseType.FAILURE);
    tmpError.setLine(2);
    tmpError.setCommand("open-url");
    List<String> tmpParameters = new ArrayList<>();
    tmpParameters.add("param1");
    tmpParameters.add("param2");
    tmpError.setParameters(tmpParameters);
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("twoTestFileOneBrowserOneStepFailure.xml");
  }

  @Test
  public void oneTestFileOneBrowserOneTestError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    TestError tmpError = new TestError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileOneBrowserOneTestError.xml");
  }

  @Test
  public void oneTestFileTwoBrowserOneTestError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test.wet");
    tmpTestFile.setFullName("/public/Test.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);

    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("FF3.6");
    tmpBrowser.setFullName("Test.wet[FF3.6]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    TestError tmpError = new TestError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("oneTestFileTwoBrowserOneTestError.xml");
  }

  @Test
  public void twoTestFileOneBrowserOneTestError() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    TestResult tmpTest = new TestResult();
    tmpTest.setName("123456789");
    tmpTest.setDuration(10);
    tmpResults.getTestResults().add(tmpTest);

    TestFileResult tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test1.wet");
    tmpTestFile.setFullName("/public/Test1.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    List<BrowserResult> tmpBrowserResults = new ArrayList<>();
    BrowserResult tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test1.wet[IE8]");
    tmpBrowser.setDuration(2);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    tmpTestFile = new TestFileResult();
    tmpTestFile.setName("Test2.wet");
    tmpTestFile.setFullName("/public/Test2.wet");
    tmpTest.getTestFileResults().add(tmpTestFile);

    tmpBrowserResults = new ArrayList<>();
    tmpBrowser = new BrowserResult();
    tmpBrowser.setName("IE8");
    tmpBrowser.setFullName("Test2.wet[IE8]");
    tmpBrowser.setDuration(3);
    tmpBrowserResults.add(tmpBrowser);
    tmpTestFile.setBrowserResults(tmpBrowserResults);

    TestError tmpError = new TestError();
    tmpError.setFile("/public/Test.wet");
    tmpError.setError("error");
    tmpBrowser.setError(tmpError);

    write(tmpResults);

    assertResult("twoTestFileOneBrowserOneTestError.xml");
  }

  @Test
  public void zeroReportFile() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    write(tmpResults);

    assertResult("zeroReportFile.xml");
  }

  @Test
  public void oneReportFile() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    tmpResults.getReportFiles().add("/public/run_report.xsl.html");

    write(tmpResults);

    assertResult("oneReportFile.xml");
  }

  @Test
  public void twoReportFile() throws Exception {
    TestResults tmpResults = new TestResults("TestResults");

    tmpResults.getReportFiles().add("/public/run_report.xsl.html");
    tmpResults.getReportFiles().add("/private/run_report.xsl.html");

    write(tmpResults);

    assertResult("twoReportFile.xml");
  }

  private void assertResult(String aFileName) throws IOException {
    String tmpExpectedResult = read(aFileName);
    String tmpActualResult = unzip();

    tmpExpectedResult = normalize(tmpExpectedResult);
    tmpActualResult = normalize(tmpActualResult);

    Assert.assertEquals(tmpExpectedResult, tmpActualResult);
  }

  private String normalize(String aString) {
    String tmpResult = aString;
    tmpResult = tmpResult.replaceAll("\r\n", "\n");
    return tmpResult;
  }
}
