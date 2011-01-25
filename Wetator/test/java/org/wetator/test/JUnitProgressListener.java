/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.test;

import org.wetator.core.WetCommand;
import org.wetator.core.WetContext;
import org.wetator.core.WetEngine;
import org.wetator.core.WetProgressListener;
import org.wetator.exception.AssertionFailedException;

/**
 * @author frank.danek
 */
public class JUnitProgressListener implements WetProgressListener {

  private int steps;
  private int errors;
  private int failures;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#end(org.wetator.core.WetEngine)
   */
  @Override
  public void end(WetEngine aWetEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(Throwable aThrowable) {
    steps++;
    errors++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandFailure(org.wetator.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(AssertionFailedException aAnAssertionFailedException) {
    steps++;
    failures++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandStart(org.wetator.core.WetContext,
   *      org.wetator.core.WetCommand)
   */
  @Override
  public void executeCommandStart(WetContext aWetContext, WetCommand aCommand) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    steps++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(String aMessageKey, String[] aParameterArray) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#init(org.wetator.core.WetEngine)
   */
  @Override
  public void init(WetEngine aWetEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(String aResponseFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#start(org.wetator.core.WetEngine)
   */
  @Override
  public void start(WetEngine aWetEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testCaseStart(java.lang.String)
   */
  @Override
  public void testCaseStart(String aTestName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testFileStart(java.lang.String)
   */
  @Override
  public void testFileStart(String aFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testRunStart(java.lang.String)
   */
  @Override
  public void testRunStart(String aBrowserName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
   */
  @Override
  public void warn(String aMessageKey, String[] aParameterArray) {
    // nothing
  }

  /**
   * @return the steps
   */
  public int getSteps() {
    return steps;
  }

  /**
   * @return the errors
   */
  public int getErrors() {
    return errors;
  }

  /**
   * @return the failures
   */
  public int getFailures() {
    return failures;
  }

}
