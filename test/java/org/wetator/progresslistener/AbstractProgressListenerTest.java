/*
 * Copyright (c) 2008-2014 wetator.org
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


package org.wetator.progresslistener;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;

/**
 * Abstract test class for testing {@link IProgressListener}s.
 * 
 * @author frank.danek
 * @author rbri
 * @author tobwoerk
 */
public abstract class AbstractProgressListenerTest {

  protected static final String LOGS_FOLDER = "logs";

  protected static final String IE8 = "IE8";
  protected static final String FF31 = "Firefox31";

  private static final String COMMAND_NAME = "command";

  @Rule
  public TestName testName = new TestName();

  protected WetatorConfiguration configuration = mock(WetatorConfiguration.class);
  protected WetatorEngine engine = mock(WetatorEngine.class);
  protected WetatorContext context = mock(WetatorContext.class);

  protected IProgressListener progressListener;

  private int testNo;
  private int lineNo;

  @Before
  public void setupEnvironment() {
    when(configuration.getOutputDir()).thenReturn(new File(LOGS_FOLDER));
    when(engine.getConfiguration()).thenReturn(configuration);
    when(context.replaceVariables(any(String.class))).thenCallRealMethod();

    progressListener = createProgressListener();
  }

  protected abstract IProgressListener createProgressListener();

  @Before
  public void resetNos() {
    testNo = 1;
    lineNo = 1;
  }

  @Test
  public void homepage() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase("wetator_google");
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand(createCommand("open-url", "http://www.google.com"));
    writeCommand(createCommand("set", "search", "Wetator"));
    writeCommand(createCommand("click-on", "Google Search"));
    writeCommand(createCommand("assert-content", "www.wetator.org"));
    writeCommand(createCommand("click-on", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-title", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-content", "WETATOR IN A NUTSHELL Wetator is a tool"));
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();
    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void green() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void greenModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF31);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    writeCommand();
    endModule();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void red() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redWithIgnoredModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedWithIgnoredModule(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModule(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redAfterModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF31);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommandWithError();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void blue() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void blueModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeBlueModule(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void mixed() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureAndErrorTestRun(tmpTestCase, IE8);
    writeFailureAndErrorTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void invalidInputDuringCommandExecution() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithError(createCommand("invalid-command", null), new InvalidInputException("Command in TestCase "
        + tmpTestCase.getName() + " is very invalid."));
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF31);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void invalidInputWhileReadingCommands() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    progressListener.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF31);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redModuleNotFound() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModuleNotFound(tmpTestCase, FF31);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  protected void writeRedWithIgnoredModule(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    startModule(aTestCase);
    writeCommandIgnored();
    writeCommandIgnored();
    writeCommandIgnored();
    endModule();
    writeComment();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeRedModule(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(aTestCase);
    writeCommandWithFailure();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    endModule();
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeRedModuleNotFound(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithFailure();
    startModule(aTestCase);
    progressListener.executeCommandError(new FileNotFoundException("Module 'module.wet' not found."));
    endModule();
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeBlueModule(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(aTestCase);
    writeCommandWithFailure();
    writeCommand();
    endModule();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeGreenTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeFailureTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeErrorTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    writeComment();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeFailureAndErrorTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeCommand() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommand(tmpCommand);
  }

  private void writeCommandWithFailure() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener
        .executeCommandFailure(new AssertionException("test failure", new RuntimeException("failure cause")));
    progressListener.executeCommandEnd();
  }

  protected void writeCommandWithError() {
    writeCommandWithError(new ActionException("test error"));
  }

  private void writeCommandWithError(Exception anException) {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommandWithError(tmpCommand, anException);
  }

  protected void writeCommandWithError(Command aCommand, Exception anException) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandError(anException);
    progressListener.executeCommandEnd();
  }

  protected void writeCommandIgnored() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener.executeCommandIgnored();
    progressListener.executeCommandEnd();
  }

  private void writeComment() {
    Command tmpComment = createCommand(null, "comment value", true);
    writeCommand(tmpComment);
  }

  protected void writeCommand(Command aCommand) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandSuccess();
    progressListener.executeCommandEnd();
  }

  protected Command createCommand(String aCommandName, String aParameterValue) {
    return createCommand(aCommandName, aParameterValue, false);
  }

  private Command createCommand(String aCommandName, String aParameterValue, boolean anIsComment) {
    Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    lineNo++;
    return tmpCommand;
  }

  protected Command createCommand(String aCommandName, String aParameterValue, String aSecondParameterValue) {
    Command tmpCommand = new Command(aCommandName, false);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    tmpCommand.setSecondParameter(new Parameter(aSecondParameterValue));
    lineNo++;
    return tmpCommand;
  }

  protected void startModule(TestCase aTestCase) {
    progressListener.executeCommandStart(context, createCommand("use-module", "module"));
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath() + "module");
  }

  protected void endModule() {
    progressListener.testFileEnd();
    progressListener.executeCommandEnd();
  }

  protected TestCase createTestCase() {
    String tmpName = "test" + testNo++ + ".wet";
    return createTestCase(tmpName);
  }

  protected TestCase createTestCase(String aName) {
    return new TestCase(aName, new File("/Test/" + aName));
  }

  protected void assertResult() throws Exception {
    InputStream tmpExpectedStream = getClass().getClassLoader().getResourceAsStream(getExpectedFilename());
    String tmpExpectedResult = IOUtils.toString(tmpExpectedStream, "UTF-8");

    String tmpActualResult = getActualResult();

    tmpActualResult = normalizeResult(tmpActualResult);
    tmpExpectedResult = normalizeResult(tmpExpectedResult);

    Assert.assertEquals(tmpExpectedResult, tmpActualResult);
  }

  protected abstract String getExpectedFilename();

  protected abstract String getActualResult() throws Exception;

  protected String normalizeResult(String aResult) {
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
    return tmpResult;
  }

  protected String replaceLines(String aResult, String aPrefixLine, String aSuffixLine, String aKeepPattern,
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
}
