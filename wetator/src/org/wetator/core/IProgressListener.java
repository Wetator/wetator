/*
 * Copyright (c) 2008-2018 wetator.org
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

import org.wetator.exception.AssertionException;

/**
 * The interface for listeners of the Wetator run progress. Register a listener to be informed.
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public interface IProgressListener {

  /**
   * This is called before the setup is done and before the test are starting.<br>
   * Only the configuration path is available. The listener can dump the version and the configuration file used (if
   * any).
   *
   * @param aWetatorEngine the engine
   */
  void init(WetatorEngine aWetatorEngine);

  /**
   * This is called after the setup is done and before the test are starting.<br>
   * The listener can dump the setup.
   *
   * @param aWetatorEngine the engine
   */
  void start(WetatorEngine aWetatorEngine);

  /**
   * This is called before a test case (grouping the browser runs) is started.<br>
   *
   * @param aTestCase the test case
   */
  void testCaseStart(TestCase aTestCase);

  /**
   * This is called before a run of a test case for one browser is started.<br>
   *
   * @param aBrowserName the name of the browser
   */
  void testRunStart(String aBrowserName);

  /**
   * This is called before a test file is started.<br>
   * This happens for a top level test file as well as for included modules.
   *
   * @param aFileName the file name of the test file
   */
  void testFileStart(String aFileName);

  /**
   * This is called before a command is executed.
   *
   * @param aContext the {@link WetatorContext}
   * @param aCommand the {@link Command}
   */
  void executeCommandStart(WetatorContext aContext, Command aCommand);

  /**
   * This is called if a command was executed successfully.
   */
  void executeCommandSuccess();

  /**
   * This is called if a command was ignored.
   */
  void executeCommandIgnored();

  /**
   * This is called if the execution of a command resulted in a failure.
   *
   * @param anAssertionException the failure
   */
  void executeCommandFailure(AssertionException anAssertionException);

  /**
   * This is called if the execution of a command resulted in an error.
   *
   * @param aThrowable the error
   */
  void executeCommandError(Throwable aThrowable);

  /**
   * This is called after a command is executed.
   */
  void executeCommandEnd();

  /**
   * This is called after a test file is finished.
   */
  void testFileEnd();

  /**
   * This is called after a run of a test case for one browser was ignored.
   */
  void testRunIgnored();

  /**
   * This is called after a run of a test case for one browser is finished.
   */
  void testRunEnd();

  /**
   * This is called after a test case (grouping the browser runs) is finished.
   */
  void testCaseEnd();

  /**
   * This is called after all tests are finished.
   *
   * @param aWetatorEngine the engine
   */
  void end(WetatorEngine aWetatorEngine);

  /**
   * This is called for writing html description.
   *
   * @param aHtmlDescription the html source of the description
   */
  void htmlDescribe(String aHtmlDescription);

  /**
   * This is called after a response was stored in disk.
   *
   * @param aResponseFileName the file name of the stored response
   */
  void responseStored(String aResponseFileName);

  /**
   * This is called to save the link to an highlighted response.
   *
   * @param aResponseFileName the file name of the stored response
   */
  void highlightedResponse(String aResponseFileName);

  /**
   * This is called to log an error.
   *
   * @param aThrowable the exception thrown
   */
  void error(Throwable aThrowable);

  /**
   * This is called to log a warning.
   *
   * @param aMessageKey the message key
   * @param aParameters the parameters for the message
   * @param aDetails the optional reason (with stacktrace) of the warning
   */
  void warn(String aMessageKey, Object[] aParameters, String aDetails);

  /**
   * This is called to log an information.
   *
   * @param aMessageKey the message key
   * @param aParameters the parameters for the message
   */
  void info(String aMessageKey, Object... aParameters);

}
