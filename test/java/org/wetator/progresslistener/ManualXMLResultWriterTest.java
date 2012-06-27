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


package org.wetator.progresslistener;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;

/**
 * Manual test for creating result files and according reports.
 * 
 * @author tobwoerk
 * @author frank.danek
 */
public class ManualXMLResultWriterTest {

  private static final String COMMAND_NAME = "command";
  private static final String IE6 = "IE6";
  private static final String IE7 = "IE7";
  private static final String IE8 = "IE8";
  private static final String FF3 = "Firefox3";
  private static final String FF36 = "Firefox3.6";

  private XMLResultWriter resultWriter = new XMLResultWriter();
  private WetatorEngine engine = mock(WetatorEngine.class);
  private WetatorContext context = mock(WetatorContext.class);

  private int testNo;
  private int lineNo;

  @Before
  public void setupEnvironment() {
    WetatorConfiguration tmpConfiguration = mock(WetatorConfiguration.class);
    when(engine.getConfiguration()).thenReturn(tmpConfiguration);
    when(tmpConfiguration.getOutputDir()).thenReturn(new File("logs"));
    when(tmpConfiguration.getXslTemplates()).thenReturn(Arrays.asList("xsl/run_report.xsl"));

    when(context.replaceVariables(any(String.class))).thenCallRealMethod();
  }

  @Before
  public void resetNos() {
    testNo = 1;
    lineNo = 1;
  }

  @Test
  public void homepage() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase("wetator_google");
    resultWriter.testCaseStart(tmpTestCase);

    lineNo = 1;
    resultWriter.testRunStart(IE8);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand(createCommand("open-url", "http://www.google.com"));
    writeCommand(createCommand("set", "search", "Wetator"));
    writeCommand(createCommand("click-on", "Google Search"));
    writeCommand(createCommand("assert-content", "www.wetator.org"));
    writeCommand(createCommand("click-on", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-title", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-content", "WETATOR IN A NUTSHELL Wetator is a tool"));
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();
    resultWriter.end(engine);
  }

  @Test
  public void green() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE6);
    writeGreenTestRun(tmpTestCase, IE7);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF3);
    writeGreenTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  @Test
  public void red() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE6);
    writeErrorTestRun(tmpTestCase, IE7);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF3);
    writeErrorTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  @Test
  public void blue() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE6);
    writeFailureTestRun(tmpTestCase, IE7);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF3);
    writeFailureTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  @Test
  public void mixed() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureAndErrorTestRun(tmpTestCase, IE8);
    writeFailureAndErrorTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF36);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  @Test
  public void invalidInputDuringCommandExecution() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);

    resultWriter.testRunStart(IE8);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithError(createCommand("invalid-command", null), new InvalidInputException("Command in TestCase "
        + tmpTestCase.getName() + " is very invalid."));
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testRunStart(FF36);
    resultWriter.testRunIgnored();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  @Test
  public void invalidInputWhileReadingCommands() {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);

    resultWriter.testRunStart(IE8);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    resultWriter.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testRunStart(FF36);
    resultWriter.testRunIgnored();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();

    resultWriter.end(engine);
  }

  private void writeGreenTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeFailureTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeErrorTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    writeComment();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeFailureAndErrorTestRun(TestCase aTestCase, String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeCommand() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommand(tmpCommand);
  }

  private void writeCommandWithFailure() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandFailure(new AssertionException("test failure"));
    resultWriter.executeCommandEnd();
  }

  private void writeCommandWithError() {
    writeCommandWithError(new ActionException("test error"));
  }

  private void writeCommandWithError(Exception anException) {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommandWithError(tmpCommand, anException);
  }

  private void writeCommandWithError(Command aCommand, Exception anException) {
    resultWriter.executeCommandStart(context, aCommand);
    resultWriter.executeCommandError(anException);
    resultWriter.executeCommandEnd();
  }

  private void writeCommandIgnored() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandIgnored();
    resultWriter.executeCommandEnd();
  }

  private void writeComment() {
    Command tmpComment = createCommand(null, "comment value", true);
    writeCommand(tmpComment);
  }

  private void writeCommand(Command aCommand) {
    resultWriter.executeCommandStart(context, aCommand);
    resultWriter.executeCommandEnd();
  }

  private Command createCommand(String aCommandName, String aParameterValue) {
    return createCommand(aCommandName, aParameterValue, false);
  }

  private Command createCommand(String aCommandName, String aParameterValue, boolean anIsComment) {
    Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    lineNo++;
    return tmpCommand;
  }

  private Command createCommand(String aCommandName, String aParameterValue, String aSecondParameterValue) {
    Command tmpCommand = new Command(aCommandName, false);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    tmpCommand.setSecondParameter(new Parameter(aSecondParameterValue));
    lineNo++;
    return tmpCommand;
  }

  private TestCase createTestCase() {
    String tmpName = "test" + testNo++ + ".wet";
    return createTestCase(tmpName);
  }

  private TestCase createTestCase(String aName) {
    return new TestCase(aName, new File("/Test/" + aName));
  }
}
