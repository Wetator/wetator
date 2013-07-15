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

import org.wetator.exception.AssertionFailedException;

/**
 * The interface for listeners of the wetator run progress. Register a listener to be informed.
 * 
 * @author rbri
 * @author frank.danek
 */
public interface IProgressListener {

  /**
   * This is called before the setup is done and before the test are starting.<br/>
   * Only the configuration path is available. The listener can dump the version and the configuration file used (if
   * any).
   * 
   * @param aWetatorEngine the engine
   */
  public void init(WetatorEngine aWetatorEngine);

  /**
   * This is called after the setup is done and before the test are starting.<br/>
   * The listener can dump the setup.
   * 
   * @param aWetatorEngine the engine
   */
  public void start(WetatorEngine aWetatorEngine);

  /**
   * This is called before a test case (grouping the browser runs) is started.<br/>
   * 
   * @param aTestName the name of the test case
   */
  public void testCaseStart(String aTestName);

  /**
   * This is called before a run of a test case for one browser is started.<br/>
   * 
   * @param aBrowserName the name of the browser
   */
  public void testRunStart(String aBrowserName);

  /**
   * This is called before a test file is started.<br/>
   * This happens for a top level test file as well as for included modules.
   * 
   * @param aFileName the file name of the test file
   */
  public void testFileStart(String aFileName);

  /**
   * This is called before a command is executed.
   * 
   * @param aContext the {@link WetatorContext}
   * @param aCommand the {@link Command}
   */
  public void executeCommandStart(WetatorContext aContext, Command aCommand);

  /**
   * This is called if a command was executed successfully.
   */
  public void executeCommandSuccess();

  /**
   * This is called if a the execution of a command resulted in a failure.
   * 
   * @param anAssertionFailedException the failure
   */
  public void executeCommandFailure(AssertionFailedException anAssertionFailedException);

  /**
   * This is called if a the execution of a command resulted in an error.
   * 
   * @param aThrowable the error
   */
  public void executeCommandError(Throwable aThrowable);

  /**
   * This is called after a command is executed.
   */
  public void executeCommandEnd();

  /**
   * This is called after a test file is finished.
   */
  public void testFileEnd();

  /**
   * This is called after a run of a test case for one browser is finished.
   */
  public void testRunEnd();

  /**
   * This is called after a test case (grouping the browser runs) is finished.
   */
  public void testCaseEnd();

  /**
   * This is called after all tests are finished.
   * 
   * @param aWetatorEngine the engine
   */
  public void end(WetatorEngine aWetatorEngine);

  /**
   * This is called after a response was stored in disk.
   * 
   * @param aResponseFileName the file name of the stored response
   */
  public void responseStored(String aResponseFileName);

  /**
   * This is called to log a warning.
   * 
   * @param aMessageKey the message key
   * @param aParameterArray the parameters for the message
   */
  public void warn(String aMessageKey, String[] aParameterArray);

  /**
   * This is called to log an information.
   * 
   * @param aMessageKey the message key
   * @param aParameterArray the parameters for the message
   */
  public void info(String aMessageKey, String[] aParameterArray);
}
