/*
 * Copyright (c) 2008-2013 wetator.org
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

import org.junit.Before;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;

/**
 * Abstract test class for testing {@link IProgressListener}s.
 * 
 * @author frank.danek
 * @author rbri
 * @author tobwoerk
 */
public class AbstractProgressListenerTest {

  private static final String COMMAND_NAME = "command";

  protected WetatorConfiguration configuration = mock(WetatorConfiguration.class);
  protected WetatorEngine engine = mock(WetatorEngine.class);
  protected WetatorContext context = mock(WetatorContext.class);

  protected IProgressListener progressListener;

  private int testNo;
  private int lineNo;

  @Before
  public void setupEnvironment() {
    when(engine.getConfiguration()).thenReturn(configuration);
    when(context.replaceVariables(any(String.class))).thenCallRealMethod();
  }

  @Before
  public void resetNos() {
    testNo = 1;
    lineNo = 1;
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
}
