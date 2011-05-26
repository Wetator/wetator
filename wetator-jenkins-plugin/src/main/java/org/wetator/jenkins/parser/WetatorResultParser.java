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

import hudson.AbortException;
import hudson.FilePath;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.wetator.jenkins.Messages;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;

/**
 * @author frank.danek
 */
public class WetatorResultParser {

  public TestResults parse(String aTestResultLocations, AbstractBuild<?, ?> aBuild) throws InterruptedException,
      IOException {
    TestResults tmpTestResults = aBuild.getWorkspace().act(new ParseResultCallable(aTestResultLocations));
    return tmpTestResults;
  }

  @SuppressWarnings("unchecked")
  public static TestResults parse(File aFile) throws DocumentException {
    SAXReader tmpReader = new SAXReader();
    Document tmpDocument = tmpReader.read(aFile);
    Element tmpWet = tmpDocument.getRootElement();

    if (!"wet".equals(tmpWet.getName())) {
      return null;
    }

    TestResults tmpTestResults = new TestResults(UUID.randomUUID().toString() + "_WetatorResults");

    TestResult tmpTestResult = new TestResult();
    tmpTestResult.setName(tmpWet.elementText("startTime"));
    tmpTestResult.setDuration(Long.valueOf(tmpWet.elementText("executionTime")));
    tmpTestResults.getTestResults().add(tmpTestResult);

    List<Element> tmpTestcases = tmpWet.elements("testcase");
    for (Element tmpTestcase : tmpTestcases) {
      TestFileResult tmpTestFileResult = new TestFileResult();
      String tmpTestcaseName = tmpTestcase.attributeValue("name");
      tmpTestFileResult.setName(tmpTestcaseName);
      tmpTestFileResult.setFullName(tmpTestcaseName);
      tmpTestResult.getTestFileResults().add(tmpTestFileResult);

      List<BrowserResult> tmpBrowserResults = new ArrayList<BrowserResult>();
      List<Element> tmpTestruns = tmpTestcase.elements("testrun");
      for (Element tmpTestrun : tmpTestruns) {
        BrowserResult tmpBrowserResult = new BrowserResult();
        String tmpTestrunBrowser = tmpTestrun.attributeValue("browser");
        tmpBrowserResult.setName(tmpTestrunBrowser);
        tmpBrowserResult.setFullName(tmpTestcaseName + "[" + tmpTestrunBrowser + "]");

        Element tmpTestfile = tmpTestrun.element("testfile");
        tmpTestFileResult.setFullName(tmpTestfile.attributeValue("file"));

        long tmpDuration = 0;
        List<Node> tmpExecutionTimes = tmpTestfile.selectNodes("//testcase[@name='" + tmpTestcaseName
            + "']/testrun[@browser='" + tmpTestrunBrowser + "']//command/executionTime");
        for (Node tmpExecutionTime : tmpExecutionTimes) {
          tmpDuration += Long.valueOf(tmpExecutionTime.getText());
        }
        tmpBrowserResult.setDuration(tmpDuration);

        StepError tmpStepError = null;
        List<Node> tmpErrors = tmpTestfile.selectNodes("//testcase[@name='" + tmpTestcaseName + "']/testrun[@browser='"
            + tmpTestrunBrowser + "']//command/error/message");
        if (tmpErrors != null && !tmpErrors.isEmpty()) {
          Node tmpError = tmpErrors.get(0);
          Element tmpErrorCommand = tmpError.getParent().getParent();

          tmpStepError = new StepError();
          tmpStepError.setLine(Integer.valueOf(tmpErrorCommand.attributeValue("line")));
          tmpStepError.setCommand(tmpErrorCommand.attributeValue("name"));
          String tmpParam0 = tmpErrorCommand.elementText("param0");
          String tmpParam1 = tmpErrorCommand.elementText("param1");
          String tmpParam2 = tmpErrorCommand.elementText("param2");
          String tmpParam3 = tmpErrorCommand.elementText("param3");
          List<String> tmpParameters = new ArrayList<String>();
          if (tmpParam0 != null && !"".equals(tmpParam0)) {
            tmpParameters.add(tmpParam0);
          }
          if (tmpParam1 != null && !"".equals(tmpParam1)) {
            tmpParameters.add(tmpParam1);
          }
          if (tmpParam2 != null && !"".equals(tmpParam2)) {
            tmpParameters.add(tmpParam2);
          }
          if (tmpParam3 != null && !"".equals(tmpParam3)) {
            tmpParameters.add(tmpParam3);
          }
          tmpStepError.setParameters(tmpParameters);
          tmpStepError.setError(tmpError.getText());
          tmpBrowserResult.setError(tmpStepError);
        }

        tmpBrowserResults.add(tmpBrowserResult);
        if (tmpStepError == null) {
          tmpTestResults.getPassedTests().add(tmpBrowserResult);
        } else {
          tmpTestResults.getFailedTests().add(tmpBrowserResult);
        }
      }

      tmpTestFileResult.setBrowserResults(tmpBrowserResults);
    }

    tmpTestResults.tally();

    return tmpTestResults;
  }

  /**
   * The worker really parsing the results. It runs on the slave.
   * 
   * @author frank.danek
   */
  private static final class ParseResultCallable implements FilePath.FileCallable<TestResults> {

    private static final long serialVersionUID = -876970965386374113L;

    private String testResults;

    /**
     * The constructor.
     * 
     * @param aTestResults the location of the test results
     */
    private ParseResultCallable(String aTestResults) {
      testResults = aTestResults;
    }

    /**
     * {@inheritDoc}
     * 
     * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
     */
    @Override
    public TestResults invoke(File aWorkspace, VirtualChannel aChannel) throws IOException {
      // compared to the junit parser we do not check the last modified of the result against the build time

      FileSet tmpFileSet = Util.createFileSet(aWorkspace, testResults);
      DirectoryScanner tmpScanner = tmpFileSet.getDirectoryScanner();

      String[] tmpFiles = tmpScanner.getIncludedFiles();
      if (tmpFiles.length == 0) {
        // no test result. Most likely a configuration
        // error or fatal problem
        throw new AbortException(Messages.WetatorRecorder_NoTestReportFound());
      }

      TestResults tmpAllResults = new TestResults(UUID.randomUUID().toString() + "_WetatorResults");

      File tmpBaseDir = tmpScanner.getBasedir();
      for (String tmpFile : tmpFiles) {
        File tmpReportFile = new File(tmpBaseDir, tmpFile);
        TestResults tmpTestResults;
        try {
          tmpTestResults = parse(tmpReportFile);
          if (tmpTestResults != null) {
            tmpAllResults.add(tmpTestResults);
          }
        } catch (DocumentException e) {
          e.printStackTrace();
        }
      }

      tmpAllResults.tally();

      return tmpAllResults;
    }
  }
}
