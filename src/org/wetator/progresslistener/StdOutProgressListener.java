/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.progresslistener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.Version;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.AssertionException;
import org.wetator.util.Output;

/**
 * Simple progress listener that writes to stdout.
 * 
 * @author rbri
 * @author frank.danek
 */
public class StdOutProgressListener implements IProgressListener {

  private static final int DOTS_PER_LINE = 100;

  /** The output used. */
  protected Output output;
  private long stepsCount;
  private long errorCount;
  private long failureCount;
  private long ignoredCount;
  private int dotCount;
  private int testFileCout;
  private int processedTestFileCout;

  /**
   * The constructor.
   */
  public StdOutProgressListener() {
    // use the default charset when writing to the consol
    output = new Output(new OutputStreamWriter(System.out, Charset.defaultCharset()), "  ");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#init(WetatorEngine)
   */
  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    output.indent();
    println("using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    final File tmpConfigFile = aWetatorEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("Config:     '" + tmpConfigFile.getAbsolutePath() + "'");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#start(WetatorEngine)
   */
  @Override
  public void start(final WetatorEngine aWetatorEngine) {
    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    ignoredCount = 0;
    processedTestFileCout = 0;

    final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();
    if (tmpConfiguration != null) {
      if (StringUtils.isNotEmpty(tmpConfiguration.getProxyHost())) {
        println("    proxy:  '" + tmpConfiguration.getProxyHost() + ":" + tmpConfiguration.getProxyPort() + "'");
      }

      println("OutputDir:  '" + tmpConfiguration.getOutputDir().getAbsolutePath() + "'");

      boolean tmpFirst = true;
      for (final String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        if (tmpFirst) {
          println("Templates:  '" + tmpTemplate + "'");
          tmpFirst = false;
          output.indent().indent().indent().indent().indent().indent();
        } else {
          println("'" + tmpTemplate + "'");
        }
      }
      if (!tmpFirst) {
        output.unindent().unindent().unindent().unindent().unindent().unindent();
      }
    }

    final List<TestCase> tmpTestCases = aWetatorEngine.getTestCases();
    testFileCout = tmpTestCases.size();

    if (tmpTestCases.isEmpty()) {
      println("TestFiles: none");
      return;
    }

    boolean tmpFirst = true;
    for (final TestCase tmpTestCase : tmpTestCases) {
      if (tmpFirst) {
        println("TestFiles:  '" + tmpTestCase.getName() + "' (" + tmpTestCase.getFile().getAbsolutePath() + ")");
        tmpFirst = false;
        output.indent().indent().indent().indent().indent().indent();
      } else {
        println("'" + tmpTestCase.getName() + "' (" + tmpTestCase.getFile().getAbsolutePath() + ")");
      }
      if (!tmpFirst) {
        output.unindent().unindent().unindent().unindent().unindent().unindent();
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseStart(org.wetator.core.TestCase)
   */
  @Override
  public void testCaseStart(final TestCase aTestCase) {
    processedTestFileCout++;
    println("TestCase: '" + aTestCase.getName() + "' (" + processedTestFileCout + "/" + testFileCout + ")");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(final String aBrowserName) {
    output.indent();
    println(aBrowserName);
    dotCount = 1;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(final String aFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandStart(org.wetator.core.WetatorContext,
   *      org.wetator.core.Command)
   */
  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aWommand) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    stepsCount++;
    printProgressSign(".");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandIgnored()
   */
  @Override
  public void executeCommandIgnored() {
    stepsCount++;
    ignoredCount++;
    printProgressSign("i");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandFailure(org.wetator.exception.AssertionException)
   */
  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    stepsCount++;
    failureCount++;
    printProgressSign("F");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(final Throwable aThrowable) {
    stepsCount++;
    errorCount++;
    printProgressSign("E");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunIgnored()
   */
  @Override
  public void testRunIgnored() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    println("");
    output.unindent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#end(WetatorEngine)
   */
  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    // print summary
    println("");
    println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount + ",  Ignored: "
        + ignoredCount);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(final String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#error(java.lang.Throwable)
   */
  @Override
  public void error(final Throwable aThrowable) {
    aThrowable.printStackTrace();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#warn(String, String[], Throwable)
   */
  @Override
  public void warn(final String aMessageKey, final String[] aParameterArray, final Throwable aThrowable) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(final String aMessageKey, final String[] aParameterArray) {
  }

  /**
   * The worker that does the real output.
   * 
   * @param aString the output
   */
  protected void println(final String aString) {
    try {
      output.println(aString);
      output.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The worker that does the real output.
   * 
   * @param aString the output
   */
  protected void print(final String aString) {
    try {
      output.print(aString);
      output.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The printing of the progress output.
   * 
   * @param aProgressSign the output
   */
  protected void printProgressSign(final String aProgressSign) {
    if (dotCount == DOTS_PER_LINE) {
      println(aProgressSign);
      dotCount = 1;
      return;
    }
    print(aProgressSign);
    dotCount++;
  }

  /**
   * @return the errorCount
   */
  public long getErrorCount() {
    return errorCount;
  }

  /**
   * @return the failureCount
   */
  public long getFailureCount() {
    return failureCount;
  }

  /**
   * @return the ignoredCount
   */
  public long getIgnoredCount() {
    return ignoredCount;
  }
}
