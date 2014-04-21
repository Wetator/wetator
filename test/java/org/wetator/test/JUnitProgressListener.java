/*
 * Copyright (c) 2008-2014 wetator.org
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

import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.AssertionException;

/**
 * @author frank.danek
 */
public class JUnitProgressListener implements IProgressListener {

  private int steps;
  private int errors;
  private int failures;
  private int ignored;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#init(org.wetator.core.WetatorEngine)
   */
  @Override
  public void init(WetatorEngine aWetatorEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#start(org.wetator.core.WetatorEngine)
   */
  @Override
  public void start(WetatorEngine aWetatorEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseStart(org.wetator.core.TestCase)
   */
  @Override
  public void testCaseStart(TestCase aTestCase) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunStart(java.lang.String)
   */
  @Override
  public void testRunStart(String aBrowserName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileStart(java.lang.String)
   */
  @Override
  public void testFileStart(String aFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandStart(org.wetator.core.WetatorContext,
   *      org.wetator.core.Command)
   */
  @Override
  public void executeCommandStart(WetatorContext aContext, Command aCommand) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    steps++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandIgnored()
   */
  @Override
  public void executeCommandIgnored() {
    steps++;
    ignored++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandFailure(org.wetator.exception.AssertionException)
   */
  @Override
  public void executeCommandFailure(AssertionException aAnAssertionException) {
    steps++;
    failures++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(Throwable aThrowable) {
    steps++;
    errors++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunIgnored()
   */
  @Override
  public void testRunIgnored() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#end(org.wetator.core.WetatorEngine)
   */
  @Override
  public void end(WetatorEngine aWetatorEngine) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(String aResponseFileName) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#error(java.lang.Throwable)
   */
  @Override
  public void error(Throwable aThrowable) {
    throw new RuntimeException(aThrowable);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#warn(java.lang.String, java.lang.String[], java.lang.Throwable)
   */
  @Override
  public void warn(String aMessageKey, String[] aParameterArray, Throwable aThrowable) {
    // nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(String aMessageKey, String[] aParameterArray) {
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

  /**
   * @return the ignored
   */
  public int getIgnored() {
    return ignored;
  }

}