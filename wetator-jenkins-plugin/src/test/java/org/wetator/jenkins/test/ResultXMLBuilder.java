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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.progresslistener.XMLResultWriter;

/**
 * Builder for Wetator result XMLs.
 *
 * @author frank.danek
 */
public class ResultXMLBuilder {

  private static String LOGS_FOLDER = "target/logs";
  public static String RESULT_LOG = LOGS_FOLDER + "/wetresult.xml";

  public static String IE11 = "IE11";
  public static String FF38 = "Firefox38";

  public static String COMMAND_NAME = "command";

  public static String START_TIME = "10.12.2015 23:19:17";
  public static int OVERALL_EXECUTION_TIME = 777;
  public static int COMMAND_EXECUTION_TIME = 10;

  private WetatorConfiguration configuration = mock(WetatorConfiguration.class);
  private WetatorEngine engine = mock(WetatorEngine.class);
  private WetatorContext context = mock(WetatorContext.class);

  public IProgressListener progressListener;

  private int testNo;
  private int lineNo;

  private String testFileName;
  private String absoluteTestFileName;

  public ResultXMLBuilder() {
    File tmpLogsFolder = new File(LOGS_FOLDER);
    tmpLogsFolder.mkdirs();

    when(configuration.getOutputDir()).thenReturn(tmpLogsFolder);
    when(engine.getConfiguration()).thenReturn(configuration);
    when(context.replaceVariables(any(String.class))).thenCallRealMethod();

    progressListener = new XMLResultWriter();

    testNo = 1;
  }

  public void startEngine() {
    progressListener.init(engine);
    progressListener.start(engine);
  }

  public void endEngine() {
    progressListener.end(engine);
  }

  public TestCase startTestCase() {
    return startTestCase("test%TESTNO%.wet");
  }

  public TestCase startTestCase(String aName) {
    String tmpName = aName.replace("%TESTNO%", Integer.toString(testNo++));
    File tmpFile = new File("/Test/" + tmpName);
    TestCase tmpTestCase = new TestCase(tmpName, tmpFile);
    testFileName = tmpTestCase.getName();
    absoluteTestFileName = tmpFile.getAbsolutePath();
    progressListener.testCaseStart(tmpTestCase);
    return tmpTestCase;
  }

  public void endTestCase() {
    progressListener.testCaseEnd();
  }

  public void startTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
  }

  public void endTestRun() {
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  public void writeTestRunIgnored(String aBrowser) {
    progressListener.testRunStart(aBrowser);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
  }

  public void writeCommand() {
    Command tmpCommand = createCommand(COMMAND_NAME);
    writeCommand(tmpCommand);
  }

  public void writeCommandWithFailure(String... aParameterValues) {
    Command tmpCommand = createCommand(COMMAND_NAME, aParameterValues);
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener
        .executeCommandFailure(new AssertionException("test failure", new RuntimeException("failure cause")));
    progressListener.executeCommandEnd();
  }

  public void writeCommandWithError(String... aParameterValues) {
    writeCommandWithError(new ActionException("test error"), aParameterValues);
  }

  public void writeCommandWithError(Exception anException, String... aParameterValues) {
    Command tmpCommand = createCommand(COMMAND_NAME, aParameterValues);
    writeCommandWithError(tmpCommand, anException);
  }

  private void writeCommandWithError(Command aCommand, Exception anException) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandError(anException);
    progressListener.executeCommandEnd();
  }

  public void writeCommandIgnored() {
    Command tmpCommand = createCommand(COMMAND_NAME);
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener.executeCommandIgnored();
    progressListener.executeCommandEnd();
  }

  public void writeComment() {
    Command tmpComment = createCommand(null, true, "comment value");
    writeCommand(tmpComment);
  }

  private void writeCommand(Command aCommand) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandSuccess();
    progressListener.executeCommandEnd();
  }

  private Command createCommand(String aCommandName, String... aParameterValues) {
    return createCommand(aCommandName, false, aParameterValues);
  }

  private Command createCommand(String aCommandName, boolean anIsComment, String... aParameterValues) {
    Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    if (aParameterValues != null) {
      if (aParameterValues.length > 0) {
        tmpCommand.setFirstParameter(new Parameter(aParameterValues[0]));
      }
      if (aParameterValues.length > 1) {
        tmpCommand.setSecondParameter(new Parameter(aParameterValues[1]));
      }
      if (aParameterValues.length > 2) {
        tmpCommand.setThirdParameter(new Parameter(aParameterValues[2]));
      }
    }
    lineNo++;
    return tmpCommand;
  }

  public void startModule(TestCase aTestCase) {
    progressListener.executeCommandStart(context, createCommand("use-module", "module"));
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath() + "module");
  }

  public void endModule() {
    progressListener.testFileEnd();
    progressListener.executeCommandEnd();
  }

  public void error(Throwable aThrowable) {
    progressListener.error(aThrowable);
  }

  public String getNormalizedResult() throws Exception {
    File tmpActualFile = new File(RESULT_LOG);
    String tmpActualResult = FileUtils.readFileToString(tmpActualFile, "utf-8");
    return normalizeResult(tmpActualResult);
  }

  public InputStream getNormalizedResultStream() throws Exception {
    return new ByteArrayInputStream(getNormalizedResult().getBytes("utf-8"));
  }

  private String normalizeResult(String aResult) {
    String tmpResult = aResult;
    // unify line breaks
    tmpResult = tmpResult.replace("\r\n", "\n");
    // replace tabs
    tmpResult = tmpResult.replace("\t", "    ");
    // unify path delimiter
    tmpResult = tmpResult.replace('\\', '/');
    // unify htmlunit version
    tmpResult = tmpResult.replaceAll("HtmlUnit version 2.\\d+(-SNAPSHOT)?", "HtmlUnit");
    // comments
    tmpResult = tmpResult.replaceAll("(?s)<!--.*?-->", "<!-- ... -->");
    // paths
    tmpResult = tmpResult.replaceAll("##PATH##", "");

    // remove drive letter (windows)
    tmpResult = tmpResult.replaceAll("file=\"[^/]*", "file=\"");

    // replace ids
    tmpResult = tmpResult.replaceAll("id=\"\\d+\"", "id=\"##ID##\"");

    // replace library versions
    tmpResult = replaceElementContent(tmpResult, "library", "([A-Za-z \\-]*)", "$2");
    // replace non-test java properties
    tmpResult = replaceLines(tmpResult, "<java id=\"##ID##\">", "\\s*</java>$", ".*key=\".*test\\.[^\"]+\".*",
        "##JAVA_PROPERTIES##");
    // replace output dir
    tmpResult = tmpResult.replaceAll("<property([^k]*) key=\"wetator.outputDir\" value=\"[^\"]*",
        "<property$1 key=\"wetator.outputDir\" value=\"##OUTPUT_DIR##");
    // replace start time
    tmpResult = replaceElementContent(tmpResult, "startTime", START_TIME);
    // replace execution time
    tmpResult = replaceElementContent(tmpResult, "executionTime", Integer.toString(COMMAND_EXECUTION_TIME));
    // replace the last (overall) execution time
    tmpResult = replaceLast(tmpResult, "<executionTime([^>]*)>" + COMMAND_EXECUTION_TIME,
        "<executionTime$2>" + OVERALL_EXECUTION_TIME);
    // replace stacktraces
    tmpResult = replaceLines(tmpResult, "<details id=\"##ID##\">[^\\n]*", "</details>", null, "##DETAILS##");
    // System.out.println(tmpResult);
    return tmpResult;
  }

  private String replaceElementContent(String anXML, String anElement, String aNewContent) {
    return replaceElementContent(anXML, anElement, "", aNewContent);
  }

  private String replaceElementContent(String anXML, String anElement, String anOldContent, String aNewContent) {
    return anXML.replaceAll("<" + anElement + "([^>]*)>" + anOldContent + "[^<]*",
        "<" + anElement + "$1>" + aNewContent);
  }

  private String replaceLines(String aResult, String aPrefixLine, String aSuffixLine, String aKeepPattern,
      String aReplacement) {
    String tmpResult = aResult;
    Pattern tmpPrefixLinePattern = Pattern.compile("(?m)" + aPrefixLine + "$");
    Pattern tmpSuffixLinePattern = Pattern.compile("(?m)^" + aSuffixLine);

    Matcher tmpPrefixLineMatcher = tmpPrefixLinePattern.matcher(tmpResult);
    Matcher tmpSuffixLineMatcher = tmpSuffixLinePattern.matcher(tmpResult);
    int tmpLinesStart = 0;
    int tmpLinesEnd = 0;

    while (tmpPrefixLineMatcher.find(tmpLinesStart)) {
      tmpLinesStart = tmpPrefixLineMatcher.end();
      if (!tmpSuffixLineMatcher.find(tmpLinesStart)) {
        break;
      }
      tmpLinesEnd = tmpSuffixLineMatcher.start();

      String tmpPartBefore = tmpResult.substring(0, tmpLinesStart + 1);
      String tmpPart = tmpResult.substring(tmpLinesStart + 1, tmpLinesEnd);
      String tmpPartAfter = tmpResult.substring(tmpLinesEnd);
      StringBuilder tmpNormalizedPart = new StringBuilder();
      if (aKeepPattern != null) {
        String[] tmpLines = tmpPart.split("\\n");
        for (String tmpLine : tmpLines) {
          if (tmpLine.matches(aKeepPattern)) {
            tmpNormalizedPart.append(tmpLine).append("\n");
          }
        }
      }
      tmpNormalizedPart = tmpNormalizedPart.insert(0, aReplacement + "\n");
      tmpResult = tmpNormalizedPart.insert(0, tmpPartBefore).append(tmpPartAfter).toString();

      tmpPrefixLineMatcher = tmpPrefixLinePattern.matcher(tmpResult);
      tmpSuffixLineMatcher = tmpSuffixLinePattern.matcher(tmpResult);
    }
    return tmpResult;
  }

  private String replaceLast(String aText, String aRegex, String aReplacement) {
    return aText.replaceFirst("(?s)(.*)" + aRegex, "$1" + aReplacement);
  }

  /**
   * @return the testFileName
   */
  public String getTestFileName() {
    return testFileName;
  }

  /**
   * @return the absoluteTestFileName
   */
  public String getAbsoluteTestFileName() {
    return absoluteTestFileName;
  }
}
