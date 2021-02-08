/*
 * Copyright (c) 2008-2021 wetator.org
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

import static org.mockito.ArgumentMatchers.*;
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

  protected static final String IE11 = "IE11";
  protected static final String FF68 = "Firefox68";
  protected static final String CHROME = "CHROME";

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
    when(context.replaceVariables(any())).thenCallRealMethod();

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

    final TestCase tmpTestCase = createTestCase("wetator_google.wet");
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE11);
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

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE11);
    writeGreenTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void greenModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
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

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redWithIgnoredModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedWithIgnoredModule(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModule(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void redAfterModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
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

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeFailureTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void blueModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeBlueModule(tmpTestCase, FF68);
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
    writeGreenTestRun(tmpTestCase, IE11);
    writeGreenTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeFailureTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureAndErrorTestRun(tmpTestCase, IE11);
    writeFailureAndErrorTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void invalidInputDuringCommandExecution() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE11);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithError(createCommand("invalid-command", null),
        new InvalidInputException("Command in TestCase " + tmpTestCase.getName() + " is very invalid."));
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF68);
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

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE11);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    progressListener.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF68);
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

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModuleNotFound(tmpTestCase, FF68);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void errorOneTestCaseOneBrowser() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.error(new ClassNotFoundException("test error"));
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void errorOneTestCaseManyBrowsers() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.error(new ClassNotFoundException("test error"));
    progressListener.testRunEnd();
    progressListener.testRunStart(IE11);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testRunStart(CHROME);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void errorTwoTestCasesOneBrowser() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.error(new ClassNotFoundException("test error"));
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  @Test
  public void errorTwoTestCasesManyBrowsers() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.error(new ClassNotFoundException("test error"));
    progressListener.testRunEnd();
    progressListener.testRunStart(IE11);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testRunStart(CHROME);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF68);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testRunStart(IE11);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testRunStart(CHROME);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertResult();
  }

  protected void writeRedWithIgnoredModule(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeRedModule(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeRedModuleNotFound(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeBlueModule(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeGreenTestRun(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    progressListener.testRunStart(aBrowser);
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
  }

  protected void writeFailureTestRun(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeErrorTestRun(final TestCase aTestCase, final String aBrowser) {
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

  protected void writeFailureAndErrorTestRun(final TestCase aTestCase, final String aBrowser) {
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
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommand(tmpCommand);
  }

  private void writeCommandWithFailure() {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener
        .executeCommandFailure(new AssertionException("test failure", new RuntimeException("failure cause")));
    progressListener.executeCommandEnd();
  }

  protected void writeCommandWithError() {
    writeCommandWithError(new ActionException("test error"));
  }

  private void writeCommandWithError(final Exception anException) {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommandWithError(tmpCommand, anException);
  }

  protected void writeCommandWithError(final Command aCommand, final Exception anException) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandError(anException);
    progressListener.executeCommandEnd();
  }

  protected void writeCommandIgnored() {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    progressListener.executeCommandStart(context, tmpCommand);
    progressListener.executeCommandIgnored();
    progressListener.executeCommandEnd();
  }

  private void writeComment() {
    final Command tmpComment = createCommand(null, "comment value", true);
    writeCommand(tmpComment);
  }

  protected void writeCommand(final Command aCommand) {
    progressListener.executeCommandStart(context, aCommand);
    progressListener.executeCommandSuccess();
    progressListener.executeCommandEnd();
  }

  protected Command createCommand(final String aCommandName, final String aParameterValue) {
    return createCommand(aCommandName, aParameterValue, false);
  }

  private Command createCommand(final String aCommandName, final String aParameterValue, final boolean anIsComment) {
    final Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    lineNo++;
    return tmpCommand;
  }

  protected Command createCommand(final String aCommandName, final String aParameterValue,
      final String aSecondParameterValue) {
    final Command tmpCommand = new Command(aCommandName, false);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    tmpCommand.setSecondParameter(new Parameter(aSecondParameterValue));
    lineNo++;
    return tmpCommand;
  }

  protected void startModule(final TestCase aTestCase) {
    progressListener.executeCommandStart(context, createCommand("use-module", "module"));
    progressListener.testFileStart(aTestCase.getFile().getAbsolutePath() + "module");
  }

  protected void endModule() {
    progressListener.testFileEnd();
    progressListener.executeCommandEnd();
  }

  protected TestCase createTestCase() {
    final String tmpName = "test" + testNo++ + ".wet";
    return createTestCase(tmpName);
  }

  protected TestCase createTestCase(final String aName) {
    return new TestCase(aName, new File("/Test/" + aName));
  }

  protected void assertResult() throws Exception {
    final InputStream tmpExpectedStream = getClass().getClassLoader().getResourceAsStream(getExpectedFilename());
    String tmpExpectedResult = IOUtils.toString(tmpExpectedStream, "UTF-8");

    String tmpActualResult = getActualResult();

    tmpActualResult = normalizeResult(tmpActualResult);
    tmpExpectedResult = normalizeResult(tmpExpectedResult);

    Assert.assertEquals(tmpExpectedResult, tmpActualResult);
  }

  protected abstract String getExpectedFilename();

  protected abstract String getActualResult() throws Exception;

  protected String normalizeResult(final String aResult) {
    String tmpResult = aResult;
    // unify line breaks
    tmpResult = tmpResult.replace("\r\n", "\n");
    // replace tabs
    tmpResult = tmpResult.replace("\t", "    ");
    // unify path delimiter
    tmpResult = tmpResult.replace('\\', '/');
    // unify htmlunit version
    tmpResult = tmpResult.replaceAll("HtmlUnit version 2.\\d+(.\\d+)?(-SNAPSHOT)?", "HtmlUnit");
    // comments
    tmpResult = tmpResult.replaceAll("(?s)<!--.*?-->", "<!-- ... -->");
    // paths
    tmpResult = tmpResult.replaceAll("##PATH##", "");
    return tmpResult;
  }

  protected String replaceLines(final String aResult, final String aPrefixLine, final String aSuffixLine,
      final String aKeepPattern, final String aReplacement) {
    String tmpResult = aResult;
    final Pattern tmpPrefixLinePattern = Pattern.compile("(?m)" + aPrefixLine + "$");
    final Pattern tmpSuffixLinePattern = Pattern.compile("(?m)^" + aSuffixLine);

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

      final String tmpPartBefore = tmpResult.substring(0, tmpLinesStart + 1);
      final String tmpPart = tmpResult.substring(tmpLinesStart + 1, tmpLinesEnd);
      final String tmpPartAfter = tmpResult.substring(tmpLinesEnd);
      StringBuilder tmpNormalizedPart = new StringBuilder();
      if (aKeepPattern != null) {
        final String[] tmpLines = tmpPart.split("\\n");
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
