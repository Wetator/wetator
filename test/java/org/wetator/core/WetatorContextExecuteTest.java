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


package org.wetator.core;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.CommandExecutionException;
import org.wetator.exception.ResourceException;
import org.wetator.exception.WrongCommandUsageException;

/**
 * Tests for {@link WetatorContext}.
 * 
 * @author frank.danek
 */
public class WetatorContextExecuteTest {

  private File file1;
  private File file2;

  private Command command1;
  private Command command2;

  private ICommandImplementation commandImplementation1;
  private ICommandImplementation commandImplementation2;

  private IBrowser browser;
  private WetatorEngine engine;

  @Before
  public void setupMocks() {
    file1 = new File("file1");
    file2 = new File("file2");

    command1 = new Command("command1", false);
    command2 = new Command("command2", false);

    commandImplementation1 = mock(ICommandImplementation.class);
    commandImplementation2 = mock(ICommandImplementation.class);

    browser = mock(IBrowser.class);

    engine = mock(WetatorEngine.class);
    when(engine.getBrowser()).thenReturn(browser);
    when(engine.getCommandImplementationFor("command1")).thenReturn(commandImplementation1);
    when(engine.getCommandImplementationFor("command2")).thenReturn(commandImplementation2);
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If everything is ok, all commands should be executed.
   */
  @Test
  public void ok() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link AssertionFailedException}, all following commands should be executed.
   */
  @Test
  public void assertionFailedException() throws CommandExecutionException {
    // setup
    doThrow(new AssertionFailedException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link CommandExecutionException}, all following commands should be ignored.
   */
  @Test
  public void commandExecutionException() throws CommandExecutionException {
    // setup
    doThrow(new CommandExecutionException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, CommandExecutionException.class);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link WrongCommandUsageException}, all following commands should be ignored.
   */
  @Test
  public void wrongCommandUsageException() throws CommandExecutionException {
    // setup
    doThrow(new WrongCommandUsageException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, WrongCommandUsageException.class);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link RuntimeException}, all following commands should be ignored.
   */
  @Test
  public void runtimeException() throws CommandExecutionException {
    // setup
    doThrow(new RuntimeException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, RuntimeException.class);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If everything is ok in the context, all commands should be executed in the sub context.
   */
  @Test
  public void okBeforeSubContext() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was an {@link AssertionFailedException} in the context, all commands should be executed in the
   * sub context.
   */
  @Test
  public void assertionFailedExceptionBeforeSubContext() throws CommandExecutionException {
    // setup
    doThrow(new AssertionFailedException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link CommandExecutionException} in the context, all following commands should be
   * ignored in the sub context.
   */
  @Test
  public void commandExecutionExceptionBeforeSubContext() throws CommandExecutionException {
    // setup
    doThrow(new CommandExecutionException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, CommandExecutionException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link WrongCommandUsageException} in the context, all following commands should be
   * ignored in the sub context.
   */
  @Test
  public void wrongCommandUsageExceptionBeforeSubContext() throws CommandExecutionException {
    // setup
    doThrow(new WrongCommandUsageException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, WrongCommandUsageException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} in the context, all following commands should be ignored in the
   * sub context.
   */
  @Test
  public void runtimeExceptionBeforeSubContext() throws CommandExecutionException {
    // setup
    doThrow(new RuntimeException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, RuntimeException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If everything is ok in the sub context, all commands should be executed in the context.
   */
  @Test
  public void okInSubContext() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was an {@link AssertionFailedException} in the sub context, all commands should be executed in
   * the context.
   */
  @Test
  public void assertionFailedExceptionInSubContext() throws CommandExecutionException {
    // setup
    doThrow(new AssertionFailedException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpSubContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link CommandExecutionException} in the sub context, all following commands should be
   * ignored in the context.
   */
  @Test
  public void commandExecutionExceptionInSubContext() throws CommandExecutionException {
    // setup
    doThrow(new CommandExecutionException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, CommandExecutionException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link WrongCommandUsageException} in the sub context, all following commands should be
   * ignored in the context.
   */
  @Test
  public void wrongCommandUsageExceptionInSubContext() throws CommandExecutionException {
    // setup
    doThrow(new WrongCommandUsageException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, WrongCommandUsageException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the sub context.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} in the sub context, all following commands should be ignored in
   * the context.
   */
  @Test
  public void runtimeExceptionInSubContext() throws CommandExecutionException {
    // setup
    doThrow(new RuntimeException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    tmpSubContext.execute();
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, RuntimeException.class);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If no {@link ICommandImplementation} is found, all following commands should be ignored.
   */
  @Test
  public void commandImplementationNotFound() throws CommandExecutionException {
    // setup
    command1 = new Command("unknown", false);

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).informListenersExecuteCommandStart(tmpContext, command1);
    tmpInOrder.verify(engine).informListenersExecuteCommandError(any(CommandExecutionException.class));
    tmpInOrder.verify(engine).informListenersExecuteCommandEnd();
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link AssertionFailedException} stored in the browser, all following commands should be
   * executed.
   */
  @Test
  public void storedAssertionFailedException() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));
    when(browser.checkAndResetFailures()).thenReturn(new AssertionFailedException("mocker"),
        (AssertionFailedException) null);

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was an {@link CommandExecutionException}, all following commands annotated with
   * {@link ForceExecution} should be executed.
   */
  @Test
  public void forceExecution() throws CommandExecutionException {
    // setup
    ICommandImplementation tmpForceExecution = new ForceExecutionCommand(commandImplementation2);

    doThrow(new CommandExecutionException("mocker")).when(commandImplementation1).execute(any(WetatorContext.class),
        any(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));
    when(engine.getCommandImplementationFor("command2")).thenReturn(tmpForceExecution);

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    tmpContext.execute();

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, CommandExecutionException.class);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine).informListenersExecuteCommandError(any(Throwable.class));
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was a {@link ResourceException} reading the commands, no commands (can and) should be executed.
   */
  @Test
  public void readCommandsFromFileResourceException() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenThrow(new ResourceException("mocker"));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    try {
      tmpContext.execute();
      Assert.fail("ResourceException expected!");
    } catch (ResourceException e) {
      // ok
    }

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).readCommandsFromFile(file1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandStart(eq(tmpContext), any(Command.class));
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
    verify(engine, never()).informListenersExecuteCommandEnd();
    verify(commandImplementation1, never()).execute(tmpContext, command1);
    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(browser, never()).checkAndResetFailures();
  }

  /**
   * Test for the context.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} reading the commands, no commands (can and) should be executed.
   */
  @Test
  public void readCommandsFromFileRuntimeException() throws CommandExecutionException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenThrow(new RuntimeException("mocker"));

    // run
    WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_3_6);
    try {
      tmpContext.execute();
      Assert.fail("RuntimeException expected!");
    } catch (RuntimeException e) {
      // ok
    }

    // assert
    InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).readCommandsFromFile(file1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandStart(eq(tmpContext), any(Command.class));
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    verify(engine, never()).informListenersExecuteCommandError(any(Throwable.class));
    verify(engine, never()).informListenersExecuteCommandEnd();
    verify(commandImplementation1, never()).execute(tmpContext, command1);
    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(browser, never()).checkAndResetFailures();
  }

  private void assertCommandSuccess(InOrder anInOrder, WetatorContext aContext, Command aCommand,
      ICommandImplementation anImplementation) throws CommandExecutionException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandSuccess();
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandFailure(InOrder anInOrder, WetatorContext aContext, Command aCommand,
      ICommandImplementation anImplementation) throws CommandExecutionException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandFailure(any(AssertionFailedException.class));
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandError(InOrder anInOrder, WetatorContext aContext, Command aCommand,
      ICommandImplementation anImplementation, Class<? extends Throwable> aThrowableClass)
      throws CommandExecutionException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandError(any(aThrowableClass));
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandIgnored(InOrder anInOrder, WetatorContext aContext, Command aCommand) {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(engine).informListenersExecuteCommandIgnored();
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  /**
   * @author frank.danek
   */
  @ForceExecution
  private class ForceExecutionCommand implements ICommandImplementation {

    private ICommandImplementation wrappedImplementation;

    public ForceExecutionCommand(ICommandImplementation aWrappedImplementation) {
      wrappedImplementation = aWrappedImplementation;
    }

    @Override
    public void execute(WetatorContext aContext, Command aCommand) throws CommandExecutionException {
      wrappedImplementation.execute(aContext, aCommand);
    }
  }
}
