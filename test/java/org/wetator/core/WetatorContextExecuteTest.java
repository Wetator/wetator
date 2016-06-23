/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.CommandException;
import org.wetator.exception.InvalidInputException;
import org.wetator.exception.ResourceException;

/**
 * Tests for {@link WetatorContext#execute()}.
 *
 * @author frank.danek
 * @author tobwoerk
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
   * Test for the context.<br>
   * <br>
   * Assertion: If everything is ok, all commands should be executed.
   */
  @Test
  public void ok() throws CommandException, InvalidInputException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was an {@link AssertionException}, all following commands should be executed.
   */
  @Test
  public void assertionException() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new AssertionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was an {@link ActionException}, all following commands should be ignored.
   */
  @Test
  public void actionException() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new ActionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link InvalidInputException} during execution of a command, all following commands
   * should be ignored.
   */
  @Test
  public void invalidInputExceptionExecute() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new InvalidInputException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertFalse(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link CommandException}, all following commands should be ignored.
   */
  @Test
  public void commandException() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new CommandException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link RuntimeException}, all following commands should be ignored.
   */
  @Test
  public void runtimeException() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new RuntimeException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was an {@link InvalidInputException} (during reading the commands), no commands should be
   * executed and it should be thrown.
   */
  @Test
  public void invalidInputExceptionRead() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new InvalidInputException("mocker");
    doThrow(tmpException).when(engine).readCommandsFromFile(file1);

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertFalse(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).readCommandsFromFile(file1);
    tmpInOrder.verify(engine).informListenersError(tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation1, never()).execute(tmpContext, command1);
    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandStart(tmpContext, command1);
    verify(engine, never()).informListenersExecuteCommandStart(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersExecuteCommandEnd();
    verify(engine, times(1)).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If everything is ok in the context, all commands should be executed in the sub context.
   */
  @Test
  public void okBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link AssertionException} in the context, all commands should be executed in the sub
   * context.
   */
  @Test
  public void assertionExceptionBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new AssertionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link ActionException} in the context, all following commands should be ignored in the
   * sub context.
   */
  @Test
  public void actionExceptionBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new ActionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link InvalidInputException} in the context, all following commands should be
   * ignored in the sub context.
   */
  @Test
  public void invalidInputExceptionBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new InvalidInputException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertFalse(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was a {@link CommandException} in the context, all following commands should be
   * ignored in the sub context.
   */
  @Test
  public void commandExceptionBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new CommandException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was a {@link RuntimeException} in the context, all following commands should be ignored in the
   * sub context.
   */
  @Test
  public void runtimeExceptionBeforeSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new RuntimeException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpSubContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpSubContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If everything is ok in the sub context, all commands should be executed in the context.
   */
  @Test
  public void okInSubContext() throws CommandException, InvalidInputException {
    // setup
    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpSubContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link AssertionException} in the sub context, all commands should be executed in the
   * context.
   */
  @Test
  public void assertionExceptionInSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new AssertionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpSubContext, command1, commandImplementation1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link ActionException} in the sub context, all following commands should be ignored in
   * the context.
   */
  @Test
  public void actionExceptionInSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new ActionException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was an {@link InvalidInputException} in the sub context, all following commands should be
   * ignored in the context.
   */
  @Test
  public void invalidInputExceptionInSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new InvalidInputException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertFalse(tmpSubContext.execute());
    Assert.assertFalse(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was a {@link CommandException} in the sub context, all following commands should be
   * ignored in the context.
   */
  @Test
  public void commandExceptionInSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new CommandException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the sub context.<br>
   * <br>
   * Assertion: If there was a {@link RuntimeException} in the sub context, all following commands should be ignored in
   * the context.
   */
  @Test
  public void runtimeExceptionInSubContext() throws CommandException, InvalidInputException {
    // setup
    final Exception tmpException = new RuntimeException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file2)).thenReturn(Arrays.asList(command1));
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    final WetatorContext tmpSubContext = tmpContext.createSubContext(file2);
    Assert.assertTrue(tmpSubContext.execute());
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file2.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpSubContext, command1, commandImplementation1, tmpException);
    tmpInOrder.verify(engine).informListenersTestFileEnd();
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If no {@link ICommandImplementation} is found, all following commands should be ignored.
   */
  @Test
  public void commandImplementationNotFound() throws CommandException, InvalidInputException {
    // setup
    command1 = new Command("unknown", false);

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertFalse(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).informListenersExecuteCommandStart(tmpContext, command1);
    tmpInOrder.verify(engine).informListenersExecuteCommandError(isA(InvalidInputException.class));
    tmpInOrder.verify(engine).informListenersExecuteCommandEnd();
    assertCommandIgnored(tmpInOrder, tmpContext, command2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was an {@link AssertionException} stored in the browser, all following commands should be
   * executed.
   */
  @Test
  public void storedAssertionException() throws CommandException, InvalidInputException {
    // setup
    final AssertionException tmpException = new AssertionException("mocker");
    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));
    when(browser.checkAndResetFailures()).thenReturn(tmpException, (AssertionException) null);

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandFailure(tmpInOrder, tmpContext, command1, commandImplementation1);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link CommandException}, all following commands annotated with {@link ForceExecution}
   * should be executed anyway.
   */
  @Test
  public void forceExecution() throws CommandException, InvalidInputException {
    // setup
    final ICommandImplementation tmpForceExecution = new ForceExecutionCommand(commandImplementation2);

    final Exception tmpException = new CommandException("mocker");
    doThrow(tmpException).when(commandImplementation1).execute(isA(WetatorContext.class), isA(Command.class));

    when(engine.readCommandsFromFile(file1)).thenReturn(Arrays.asList(command1, command2));
    when(engine.getCommandImplementationFor("command2")).thenReturn(tmpForceExecution);

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    Assert.assertTrue(tmpContext.execute());

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    assertCommandError(tmpInOrder, tmpContext, command1, commandImplementation1, tmpException);
    assertCommandSuccess(tmpInOrder, tmpContext, command2, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine).informListenersExecuteCommandError(isA(Throwable.class));
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link ResourceException} reading the commands, no commands (can and) should be executed.
   */
  @Test
  public void readCommandsFromFileResourceException() throws CommandException, InvalidInputException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenThrow(new ResourceException("mocker"));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    try {
      tmpContext.execute();
      Assert.fail("ResourceException expected!");
    } catch (final ResourceException e) {
      // ok
    }

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).readCommandsFromFile(file1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandStart(eq(tmpContext), isA(Command.class));
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersExecuteCommandEnd();
    verify(commandImplementation1, never()).execute(tmpContext, command1);
    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(browser, never()).checkAndResetFailures();
  }

  /**
   * Test for the context.<br>
   * <br>
   * Assertion: If there was a {@link RuntimeException} reading the commands, no commands (can and) should be executed.
   */
  @Test
  public void readCommandsFromFileRuntimeException() throws CommandException, InvalidInputException {
    // setup
    when(engine.readCommandsFromFile(file1)).thenThrow(new RuntimeException("mocker"));

    // run
    final WetatorContext tmpContext = new WetatorContext(engine, file1, BrowserType.FIREFOX_45);
    try {
      tmpContext.execute();
      Assert.fail("RuntimeException expected!");
    } catch (final RuntimeException e) {
      // ok
    }

    // assert
    final InOrder tmpInOrder = inOrder(engine, browser, commandImplementation1, commandImplementation2);
    tmpInOrder.verify(engine).informListenersTestFileStart(file1.getAbsolutePath());
    tmpInOrder.verify(engine).readCommandsFromFile(file1);
    tmpInOrder.verify(engine).informListenersTestFileEnd();

    verify(engine, never()).informListenersExecuteCommandStart(eq(tmpContext), isA(Command.class));
    verify(engine, never()).informListenersExecuteCommandSuccess();
    verify(engine, never()).informListenersExecuteCommandIgnored();
    verify(engine, never()).informListenersExecuteCommandFailure(isA(AssertionException.class));
    verify(engine, never()).informListenersExecuteCommandError(isA(Throwable.class));
    verify(engine, never()).informListenersExecuteCommandEnd();
    verify(commandImplementation1, never()).execute(tmpContext, command1);
    verify(commandImplementation2, never()).execute(tmpContext, command2);
    verify(browser, never()).checkAndResetFailures();
  }

  private void assertCommandSuccess(final InOrder anInOrder, final WetatorContext aContext, final Command aCommand,
      final ICommandImplementation anImplementation) throws CommandException, InvalidInputException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandSuccess();
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandFailure(final InOrder anInOrder, final WetatorContext aContext, final Command aCommand,
      final ICommandImplementation anImplementation) throws CommandException, InvalidInputException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandFailure(isA(AssertionException.class));
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandError(final InOrder anInOrder, final WetatorContext aContext, final Command aCommand,
      final ICommandImplementation anImplementation, final Throwable aThrowable)
          throws CommandException, InvalidInputException {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(anImplementation).execute(aContext, aCommand);
    anInOrder.verify(browser).checkAndResetFailures();
    anInOrder.verify(engine).informListenersExecuteCommandError(aThrowable);
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  private void assertCommandIgnored(final InOrder anInOrder, final WetatorContext aContext, final Command aCommand) {
    anInOrder.verify(engine).informListenersExecuteCommandStart(aContext, aCommand);
    anInOrder.verify(engine).informListenersExecuteCommandIgnored();
    anInOrder.verify(engine).informListenersExecuteCommandEnd();
  }

  /**
   * @author frank.danek
   */
  @ForceExecution
  private static class ForceExecutionCommand implements ICommandImplementation {

    private ICommandImplementation wrappedImplementation;

    ForceExecutionCommand(final ICommandImplementation aWrappedImplementation) {
      wrappedImplementation = aWrappedImplementation;
    }

    @Override
    public void execute(final WetatorContext aContext, final Command aCommand)
        throws CommandException, InvalidInputException {
      wrappedImplementation.execute(aContext, aCommand);
    }
  }
}
