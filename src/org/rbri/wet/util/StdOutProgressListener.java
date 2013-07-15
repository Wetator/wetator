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
import java.io.IOException;
import java.io.PrintWriter;

import org.rbri.wet.Version;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetProgressListener;
import org.rbri.wet.exception.AssertionFailedException;

/**
 * Simple progress listener that writes to stdout.
 * 
 * @author rbri
 * @author frank.danek
 */
public class StdOutProgressListener implements WetProgressListener {

  private static final int DOTS_PER_LINE = 100;

  /** the output used */
  protected Output output;
  private long stepsCount;
  private long errorCount;
  private long failureCount;
  private int dotCount;
  private int contextDeep;

  /**
   * Constructor
   */
  public StdOutProgressListener() {
    output = new Output(new PrintWriter(System.out), "  ");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#start(WetEngine)
   */
  @Override
  public void start(WetEngine aWetEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    output.indent();
    println("using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    contextDeep = 0;

    File tmpConfigFile = aWetEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("Config:     '" + tmpConfigFile.getAbsolutePath() + "'");
    }

    WetConfiguration tmpConfiguration = aWetEngine.getWetConfiguration();
    if (tmpConfiguration != null) {
      println("OutputDir:  '" + tmpConfiguration.getOutputDir().getAbsolutePath() + "'");

      boolean tmpFirst = true;
      for (String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        if (tmpFirst) {
          println("Templates:  '" + tmpTemplate + "'");
          tmpFirst = false;
          output.indent().indent().indent().indent().indent().indent();
        } else {
          println("'" + tmpTemplate + "'");
        }
      }
    }
    output.unindent().unindent().unindent().unindent().unindent().unindent();

    if (aWetEngine.getTestFiles().isEmpty()) {
      println("TestFiles: none");
      return;
    }

    boolean tmpFirst = true;
    for (File tmpTestFile : aWetEngine.getTestFiles()) {
      if (tmpFirst) {
        println("TestFiles:  '" + tmpTestFile.getAbsolutePath() + "'");
        tmpFirst = false;
        output.indent().indent().indent().indent().indent().indent();
      } else {
        println("'" + tmpTestFile.getAbsolutePath() + "'");
      }
    }
    output.unindent().unindent().unindent().unindent().unindent().unindent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseStart(String)
   */
  @Override
  public void testCaseStart(String aTestName) {
    println("Test: '" + aTestName + "'");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(String aBrowserName) {
    output.indent();
    println(aBrowserName);
    dotCount = 1;
    contextDeep = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(String aFileName) {
    contextDeep++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandStart(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.core.WetCommand)
   */
  @Override
  public void executeCommandStart(WetContext aWetContext, WetCommand aWommand) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(Throwable aThrowable) {
    stepsCount++;
    errorCount++;
    printProgressSign("E");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandFailure(org.rbri.wet.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(AssertionFailedException anAssertionFailedException) {
    stepsCount++;
    failureCount++;
    printProgressSign("F");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    stepsCount++;
    printProgressSign(".");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    contextDeep--;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    println("");
    output.unindent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#end(WetEngine)
   */
  @Override
  public void end(WetEngine aWetEngine) {
    // print summary
    println("");
    println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
   */
  @Override
  public void warn(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(String aMessageKey, String[] aParameterArray) {
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void println(String aString) {
    try {
      output.println(aString);
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The worker that does the real output
   * 
   * @param aString the output
   */
  protected void print(String aString) {
    try {
      output.print(aString);
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The printing of the progress output
   * 
   * @param aProgressSign the output
   */
  protected void printProgressSign(String aProgressSign) {
    if (dotCount == DOTS_PER_LINE) {
      println(aProgressSign);
      dotCount = 1;
      return;
    }
    print(aProgressSign);
    dotCount++;
  }
}
