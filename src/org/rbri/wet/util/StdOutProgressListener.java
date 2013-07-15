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


package org.rbri.wet.util;

import java.io.File;

import org.rbri.wet.Version;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetEngineProgressListener;
import org.rbri.wet.exception.AssertionFailedException;

/**
 * Simple progress listener that writes to stdout.
 * 
 * @author rbri
 */
public class StdOutProgressListener implements WetEngineProgressListener {

  private static final int DOTS_PER_LINE = 100;

  private long stepsCount;
  private long errorCount;
  private long failureCount;
  private int dotCount;
  private int contextDeep;

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#engineSetup(org.rbri.wet.core.WetEngine)
   */
  public void engineSetup(WetEngine aWetEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    println("  using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    contextDeep = 0;

    File tmpConfigFile = aWetEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("  Config:     '" + tmpConfigFile.getAbsolutePath() + "'");

      WetConfiguration tmpConfiguration = aWetEngine.getWetConfiguration();
      println("   OutputDir: '" + tmpConfiguration.getOutputDir().getAbsolutePath() + "'");
      boolean tmpFirst = true;
      for (String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        if (tmpFirst) {
          println("   Templates: '" + tmpTemplate + "'");
          tmpFirst = false;
        } else {
          println("              '" + tmpTemplate + "'");
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextExecuteCommandStart(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.core.WetCommand)
   */
  public void contextExecuteCommandStart(WetContext aWetContext, WetCommand aWommand) {
    stepsCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextExecuteCommandEnd()
   */
  public void contextExecuteCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextExecuteCommandError(java.lang.Throwable)
   */
  public void contextExecuteCommandError(Throwable aThrowable) {
    errorCount++;

    if (dotCount == DOTS_PER_LINE) {
      println("E");
      dotCount = 1;
      return;
    }
    print("E");
    dotCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextExecuteCommandFailure(org.rbri.wet.exception.AssertionFailedException)
   */
  public void contextExecuteCommandFailure(AssertionFailedException anAssertionFailedException) {
    failureCount++;

    if (dotCount == DOTS_PER_LINE) {
      println("F");
      dotCount = 1;
      return;
    }
    print("F");
    dotCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextExecuteCommandSuccess()
   */
  public void contextExecuteCommandSuccess() {
    if (dotCount == DOTS_PER_LINE) {
      println(".");
      dotCount = 1;
      return;
    }
    print(".");
    dotCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextTestEnd()
   */
  public void contextTestEnd() {
    contextDeep--;
    if (contextDeep > 0) {
      // subcontext
      print(">");
    } else {
      println("");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#contextTestStart(java.lang.String, java.lang.String)
   */
  public void contextTestStart(String aFileName, String aBrowserName) {
    if (contextDeep > 0) {
      // subcontext
      print("<");
    } else {
      println("Test: '" + aFileName + "' (" + aBrowserName + ")");
      dotCount = 1;
    }
    contextDeep++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#engineTestStart()
   */
  public void engineTestStart() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#engineResponseStored(java.lang.String)
   */
  public void engineResponseStored(String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#engineTestEnd()
   */
  public void engineTestEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#engineFinish()
   */
  public void engineFinish() {
    // print summary
    println("");
    println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#commandSetSetup(org.rbri.wet.commandset.WetCommandSet)
   */
  public void commandSetSetup(WetCommandSet aWetCommandSet) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#warn(java.lang.String, java.lang.String[])
   */
  public void warn(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetEngineProgressListener#info(java.lang.String, java.lang.String[])
   */
  public void info(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void println(String aString) {
    System.out.println(aString);
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void print(String aString) {
    System.out.print(aString);
  }
}
