/*
 * Copyright (c) 2008-2021 wetator.org
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

  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    // nothing
  }

  @Override
  public void start(final WetatorEngine aWetatorEngine) {
    // nothing
  }

  @Override
  public void testCaseStart(final TestCase aTestCase) {
    // nothing
  }

  @Override
  public void testRunStart(final String aBrowserName) {
    // nothing
  }

  @Override
  public void testFileStart(final String aFileName) {
    // nothing
  }

  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aCommand) {
    // nothing
  }

  @Override
  public void executeCommandSuccess() {
    steps++;
  }

  @Override
  public void executeCommandIgnored() {
    steps++;
    ignored++;
  }

  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    steps++;
    failures++;
  }

  @Override
  public void executeCommandError(final Throwable aThrowable) {
    steps++;
    errors++;
  }

  @Override
  public void executeCommandEnd() {
    // nothing
  }

  @Override
  public void testFileEnd() {
    // nothing
  }

  @Override
  public void testRunIgnored() {
    // nothing
  }

  @Override
  public void testRunEnd() {
    // nothing
  }

  @Override
  public void testCaseEnd() {
    // nothing
  }

  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    // nothing
  }

  @Override
  public void responseStored(final String aResponseFileName) {
    // nothing
  }

  @Override
  public void highlightedResponse(final String aResponseFileName) {
    // nothing
  }

  @Override
  public void error(final Throwable aThrowable) {
    throw new RuntimeException(aThrowable);
  }

  @Override
  public void warn(final String aMessageKey, final Object[] aParameters, final String aDetails) {
    // nothing
  }

  @Override
  public void info(final String aMessageKey, final Object... aParameters) {
    // nothing
  }

  @Override
  public void htmlDescribe(final String aHtmlDescription) {
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