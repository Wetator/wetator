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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.wetator.jenkins.Messages;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;

/**
 * This parser parses some Wetator XML result files and generates a {@link TestResults} containing all results of all
 * files parsed.
 * 
 * @author frank.danek
 */
public class WetatorResultParser {

  /**
   * Starts a process on the <b>slave</b> parsing the test results.
   * 
   * @param aTestResultLocations the location of the test results
   * @param aBuild the build
   * @return a {@link TestResults} containing all results of all files parsed
   * @throws InterruptedException in case of problems with the slave
   * @throws IOException in case of problems with the slave
   */
  public TestResults parse(String aTestResultLocations, AbstractBuild<?, ?> aBuild) throws InterruptedException,
      IOException {
    TestResults tmpTestResults = aBuild.getWorkspace().act(new ParseResultCallable(aTestResultLocations));
    return tmpTestResults;
  }

  /**
   * <b>INTERNAL API</b>
   * 
   * @param anInputStream a stream containing the wetator result file to parse
   * @return the {@link TestResults}
   * @throws XMLStreamException if an error occurs during parsing the file
   */
  public static TestResults parse(InputStream anInputStream) throws XMLStreamException {
    XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    XMLStreamReader tmpReader = tmpFactory.createXMLStreamReader(anInputStream);

    try {
      TestResults tmpTestResults = new TestResults(UUID.randomUUID().toString());
      TestResult tmpTestResult = null;
      TestFileResult tmpTestFileResult = null;
      List<BrowserResult> tmpBrowserResults = null;
      BrowserResult tmpBrowserResult = null;
      long tmpDuration = 0;
      int tmpLine = 0;
      String tmpCommand = null;
      String tmpParam0 = null;
      String tmpParam1 = null;
      String tmpParam2 = null;
      String tmpParam3 = null;
      StepError tmpStepError = null;

      Path tmpPath = new Path();

      while (tmpReader.hasNext()) {
        int tmpEvent = tmpReader.next();
        if (tmpEvent == XMLStreamConstants.START_ELEMENT) {
          tmpPath.push(tmpReader.getLocalName());

          if (tmpPath.matches("/wet")) {
            tmpTestResult = new TestResult();
          } else if (tmpPath.matches("/wet/startTime")) {
            tmpTestResult.setName(tmpReader.getElementText());
            tmpPath.pop();
          } else if (tmpPath.matches("/wet/executionTime")) {
            tmpTestResult.setDuration(Long.valueOf(tmpReader.getElementText()));
            tmpPath.pop();
          } else if (tmpPath.matches("/wet/testcase")) {
            tmpTestFileResult = new TestFileResult();
            String tmpTestcaseName = tmpReader.getAttributeValue(null, "name");
            String tmpTestFileName = tmpReader.getAttributeValue(null, "file");
            tmpTestFileResult.setName(tmpTestcaseName);
            if (tmpTestFileName != null && !"".equals(tmpTestFileName)) {
              tmpTestFileResult.setFullName(tmpTestFileName);
            } else {
              tmpTestFileResult.setFullName(tmpTestcaseName);
            }
            tmpBrowserResults = new ArrayList<BrowserResult>();
          } else if (tmpPath.matches("/wet/testcase/testrun")) {
            tmpBrowserResult = new BrowserResult();
            String tmpTestrunBrowser = tmpReader.getAttributeValue(null, "browser");
            tmpBrowserResult.setName(tmpTestrunBrowser);
            tmpBrowserResult.setFullName(tmpTestFileResult.getName() + "[" + tmpTestrunBrowser + "]");
            tmpStepError = null;
            tmpDuration = 0;
          } else if (tmpPath.matches("/wet/testcase/testrun/testfile")) {
            tmpTestFileResult.setFullName(tmpReader.getAttributeValue(null, "file"));
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command")) {
            tmpLine = Integer.valueOf(tmpReader.getAttributeValue(null, "line")).intValue();
            tmpCommand = tmpReader.getAttributeValue(null, "name");
            tmpParam0 = null;
            tmpParam1 = null;
            tmpParam2 = null;
            tmpParam3 = null;
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command/param0")) {
            tmpParam0 = tmpReader.getElementText();
            tmpPath.pop();
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command/param1")) {
            tmpParam1 = tmpReader.getElementText();
            tmpPath.pop();
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command/param2")) {
            tmpParam2 = tmpReader.getElementText();
            tmpPath.pop();
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command/param3")) {
            tmpParam3 = tmpReader.getElementText();
            tmpPath.pop();
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile") && tmpPath.endsWith("/command/executionTime")) {
            tmpDuration += Long.valueOf(tmpReader.getElementText()).longValue();
            tmpPath.pop();
          } else if (tmpPath.startsWith("/wet/testcase/testrun/testfile")
              && (tmpPath.endsWith("/command/error/message") || tmpPath.endsWith("/command/failure/message"))) {
            if (tmpStepError == null) {
              // only save the first error or failure per browser run
              tmpStepError = new StepError();
              tmpStepError.setLine(tmpLine);
              tmpStepError.setCommand(tmpCommand);
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
              tmpStepError.setError(tmpReader.getElementText());
              tmpBrowserResult.setError(tmpStepError);
              tmpPath.pop();
            }
          }
        } else if (tmpEvent == XMLStreamConstants.END_ELEMENT) {
          if (tmpPath.matches("/wet/testcase/testrun")) {
            tmpBrowserResult.setDuration(tmpDuration);
            tmpBrowserResults.add(tmpBrowserResult);
            if (tmpStepError == null) {
              tmpTestResults.getPassedTests().add(tmpBrowserResult);
            } else {
              tmpTestResults.getFailedTests().add(tmpBrowserResult);
            }
          } else if (tmpPath.matches("/wet/testcase")) {
            tmpTestFileResult.setBrowserResults(tmpBrowserResults);
            tmpTestResult.getTestFileResults().add(tmpTestFileResult);
          } else if (tmpPath.matches("/wet")) {
            tmpTestResults.getTestResults().add(tmpTestResult);
          }

          tmpPath.pop();
        }
      }

      tmpTestResults.tally();

      return tmpTestResults;
    } finally {
      tmpReader.close();
    }
  }

  /**
   * The worker really parsing the results. It runs on the slave.
   * 
   * @author frank.danek
   */
  private static final class ParseResultCallable implements FilePath.FileCallable<TestResults> {

    private static final long serialVersionUID = -876970965386374113L;

    private String testResultLocations;

    /**
     * The constructor.
     * 
     * @param aTestResultLocations the location of the test results
     */
    private ParseResultCallable(String aTestResultLocations) {
      testResultLocations = aTestResultLocations;
    }

    /**
     * {@inheritDoc}
     * 
     * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
     */
    @Override
    public TestResults invoke(File aWorkspace, VirtualChannel aChannel) throws IOException {
      // compared to the junit parser we do not check the last modified of the result against the build time

      FileSet tmpFileSet = Util.createFileSet(aWorkspace, testResultLocations);
      DirectoryScanner tmpScanner = tmpFileSet.getDirectoryScanner();

      String[] tmpFiles = tmpScanner.getIncludedFiles();
      if (tmpFiles.length == 0) {
        // no test result. Most likely a configuration error or fatal problem
        throw new AbortException(Messages.WetatorRecorder_NoTestReportFound());
        // TODO make abortion for missing test results optional?
      }

      TestResults tmpAllResults = new TestResults(UUID.randomUUID().toString());

      File tmpBaseDir = tmpScanner.getBasedir();
      for (String tmpFile : tmpFiles) {
        File tmpReportFile = new File(tmpBaseDir, tmpFile);
        TestResults tmpTestResults;
        InputStream tmpInputStream = new FileInputStream(tmpReportFile);
        try {
          tmpTestResults = parse(tmpInputStream);
          if (tmpTestResults != null) {
            tmpAllResults.add(tmpTestResults);
          }
        } catch (XMLStreamException e) {
          // TODO exception handling
          e.printStackTrace();
        } finally {
          tmpInputStream.close();
        }
      }

      tmpAllResults.tally();

      return tmpAllResults;
    }
  }
}
