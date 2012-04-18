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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.ResourceException;

/**
 * Tests for {@link WetatorEngine#executeTests()}.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class WetatorEngineExecuteTestsTest {

  private TestCase testCase1;
  private TestCase testCase2;

  private WetatorConfiguration configuration;

  private BrowserType browserType1;
  private BrowserType browserType2;

  private IBrowser browser;

  private WetatorEngine engine;

  private WetatorContext context;

  /**
   * The setting.
   * <ul>
   * <li>2 TestCases</li>
   * <li>2 Browsers (IE8, FF3.6)</li>
   * </ul>
   */
  @Before
  public void setupMocks() {
    testCase1 = new TestCase("testCase1", new File("file1"));
    testCase2 = new TestCase("testCase2", new File("file2"));

    configuration = mock(WetatorConfiguration.class);

    browserType1 = BrowserType.INTERNET_EXPLORER_8;
    browserType2 = BrowserType.FIREFOX_3_6;

    browser = mock(IBrowser.class);

    context = mock(WetatorContext.class);

    engine = mock(WetatorEngine.class);

    when(configuration.getBrowserTypes()).thenReturn(Arrays.asList(browserType1, browserType2));

    when(engine.getConfiguration()).thenReturn(configuration);
    when(engine.getBrowser()).thenReturn(browser);
    when(engine.getTestCases()).thenReturn(Arrays.asList(testCase1, testCase2));
    when(engine.createWetatorContext(isA(File.class), isA(BrowserType.class))).thenReturn(context);
    doCallRealMethod().when(engine).executeTests();
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If everything is ok, the commands for all browsers of all tests should be executed.
   */
  @Test
  public void ok() {
    // setup
    doReturn(Boolean.TRUE).when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1);
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} starting a new session, the run for the current browser
   * should be aborted. The commands for the other browser of this test should be executed. The command for all browsers
   * of the other test should be executed.
   */
  @Test
  public void browserStartNewSessionRuntimeException() {
    // setup
    doReturn(Boolean.TRUE).when(context).execute();
    doThrow(new RuntimeException("mocker")).doNothing().when(browser).startNewSession(browserType1);

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1);
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).informListenersError(isA(RuntimeException.class));
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link ResourceException} executing a test file, the run for the current browser
   * should be aborted. The commands for the other browser of this test should be executed. The command for all browsers
   * of the other test should be executed.
   */
  @Test
  public void contextExecuteResourceException() {
    // setup
    Exception tmpException = new ResourceException("mocker");
    doThrow(tmpException).doReturn(Boolean.TRUE).when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1);
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).createWetatorContext(testCase1.getFile(), browserType1);
    tmpInOrder.verify(context).execute();
    tmpInOrder.verify(engine).informListenersError(tmpException);
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} executing a test file, the run for the current browser
   * should be aborted. The commands for the other browser of this test should be executed. The command for all browsers
   * of the other test should be executed.
   */
  @Test
  public void contextExecuteRuntimeException() {
    // setup
    Exception tmpException = new RuntimeException("mocker");
    doThrow(tmpException).doReturn(Boolean.TRUE).when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1);
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).createWetatorContext(testCase1.getFile(), browserType1);
    tmpInOrder.verify(context).execute();
    tmpInOrder.verify(engine).informListenersError(tmpException);
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(isA(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If the execution of a test file fails due to invalid input, the run for the current browser
   * should be aborted. The commands for the other browser of this test should be ignored. The command for all browsers
   * of the other test should be executed.
   */
  @Test
  public void contextExecuteInvalidInputException() {
    // setup
    doReturn(Boolean.FALSE).doReturn(Boolean.TRUE).when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1);
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).createWetatorContext(testCase1.getFile(), browserType1);
    tmpInOrder.verify(context).execute();
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType2.getLabel());
    tmpInOrder.verify(engine).informListenersTestRunIgnored();
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, never()).informListenersError(isA(Throwable.class));
  }

  private void assertTestRun(InOrder anInOrder, File aFile, BrowserType aBrowserType) {
    anInOrder.verify(engine).informListenersTestRunStart(aBrowserType.getLabel());
    anInOrder.verify(browser).startNewSession(aBrowserType);
    anInOrder.verify(engine).createWetatorContext(aFile, aBrowserType);
    anInOrder.verify(context).execute();
    anInOrder.verify(engine).informListenersTestRunEnd();
  }
}