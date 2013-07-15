/*
 * Copyright (c) 2008-2010 Ronald Brill
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
 * The interface for listeners of the wetator run
 * progress. Register a listener to be informed.
 * 
 * @author rbri
 */
public interface WetProgressListener {

  /**
   * This is called before the setup is done
   * and before the test are starting.<br>
   * Only the configuration path is available.
   * The listener can dump the version and the
   * configuration file used (if any).
   * 
   * @param aWetEngine the engine
   */
  public void init(WetEngine aWetEngine);

  /**
   * This is called after the setup is done
   * and before the test are starting.
   * The listener can dump the setup
   * 
   * @param aWetEngine the engine
   */
  public void start(WetEngine aWetEngine);

  public void testCaseStart(String aTestName);

  public void testRunStart(String aBrowserName);

  public void testFileStart(String aFileName);

  public void executeCommandStart(WetContext aWetContext, WetCommand aCommand);

  public void executeCommandSuccess();

  public void executeCommandFailure(AssertionFailedException anAssertionFailedException);

  public void executeCommandError(Throwable aThrowable);

  public void executeCommandEnd();

  public void testFileEnd();

  public void testRunEnd();

  public void testCaseEnd();

  /**
   * This is called after all tests are finished.
   * 
   * @param aWetEngine the engine
   */
  public void end(WetEngine aWetEngine);

  public void responseStored(String aResponseFileName);

  public void warn(String aMessageKey, String[] aParameterArray);

  public void info(String aMessageKey, String[] aParameterArray);
}
