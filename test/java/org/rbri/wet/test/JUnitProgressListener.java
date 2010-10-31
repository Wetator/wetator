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


package org.rbri.wet.test;

import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetProgressListener;
import org.rbri.wet.exception.AssertionFailedException;

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
   * @see org.rbri.wet.core.WetProgressListener#end(org.rbri.wet.core.WetEngine)
   */
  @Override
  public void end(WetEngine aWetEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(Throwable aThrowable) {
    steps++;
    errors++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandFailure(org.rbri.wet.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(AssertionFailedException aAnAssertionFailedException) {
    steps++;
    failures++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandStart(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.core.WetCommand)
   */
  @Override
  public void executeCommandStart(WetContext aWetContext, WetCommand aCommand) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    steps++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(String aMessageKey, String[] aParameterArray) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(String aResponseFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#start(org.rbri.wet.core.WetEngine)
   */
  @Override
  public void start(WetEngine aWetEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseStart(java.lang.String)
   */
  @Override
  public void testCaseStart(String aTestName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileStart(java.lang.String)
   */
  @Override
  public void testFileStart(String aFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunStart(java.lang.String)
   */
  @Override
  public void testRunStart(String aBrowserName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
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
