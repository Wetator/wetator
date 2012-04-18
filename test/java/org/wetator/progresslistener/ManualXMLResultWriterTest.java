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

/**
 * @author tobwoerk
 * @author frank.danek
 */
public class ManualXMLResultWriterTest {

  private static final String COMMAND_NAME = "command";
  private static final String IE8 = "IE8";
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
  public void test() {
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
    writeGreenTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF36);
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
    writeCommentIgnored();
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
    Command tmpCommand = createCommand(COMMAND_NAME, "command value", false);
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandEnd();
  }

  private void writeCommandWithFailure() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value", false);
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandFailure(new AssertionException("test failure"));
    resultWriter.executeCommandEnd();
  }

  private void writeCommandWithError() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value", false);
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandError(new ActionException("test error"));
    resultWriter.executeCommandEnd();
  }

  private void writeCommandIgnored() {
    Command tmpCommand = createCommand(COMMAND_NAME, "command value", false);
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandIgnored();
    resultWriter.executeCommandEnd();
  }

  private void writeComment() {
    Command tmpComment = createCommand(null, "comment value", true);
    resultWriter.executeCommandStart(context, tmpComment);
    resultWriter.executeCommandEnd();
  }

  private void writeCommentIgnored() {
    Command tmpComment = createCommand(null, "comment value", true);
    resultWriter.executeCommandStart(context, tmpComment);
    resultWriter.executeCommandIgnored();
    resultWriter.executeCommandEnd();
  }

  private Command createCommand(String aCommandName, String aParameterValue, boolean anIsComment) {
    Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    lineNo++;
    return tmpCommand;
  }

  private TestCase createTestCase() {
    String tmpName = "test" + testNo++ + ".wet";
    return new TestCase(tmpName, new File("/path/" + tmpName));
  }
}
