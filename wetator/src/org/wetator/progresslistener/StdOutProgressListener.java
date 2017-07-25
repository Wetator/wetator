/*
 * Copyright (c) 2008-2017 wetator.org
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
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
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
 * @author tobwoerk
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
  private int testCaseCout;
  private int processedTestCaseCount;
  private int processedTestRunCount;
  private int testRunErrorCout;
  private int testRunIgnoredCount;

  /**
   * The constructor.
   */
  public StdOutProgressListener() {
    // use the default charset when writing to the console
    output = new Output(new OutputStreamWriter(System.out, Charset.defaultCharset()), "  ");
  }

  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    output.indent();
    println("using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    final File tmpConfigFile = aWetatorEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("Config:     '" + FilenameUtils.normalize(tmpConfigFile.getAbsolutePath()) + "'");
    }
  }

  @Override
  public void start(final WetatorEngine aWetatorEngine) {
    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    ignoredCount = 0;

    processedTestRunCount = 0;
    testRunErrorCout = 0;
    testRunIgnoredCount = 0;

    processedTestCaseCount = 0;

    final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();
    if (tmpConfiguration != null) {
      if (StringUtils.isNotEmpty(tmpConfiguration.getProxyHost())) {
        println("    proxy:  '" + tmpConfiguration.getProxyHost() + ":" + tmpConfiguration.getProxyPort() + "'");

        final Set<String> tmpNonProxyHosts = tmpConfiguration.getProxyHostsToBypass();
        boolean tmpNotFirst = false;
        final StringBuilder tmpProxies = new StringBuilder("    bypass: ");
        for (final String tmpString : tmpNonProxyHosts) {
          final String tmpHostsToProxyBypass = tmpString.trim();
          if (tmpNotFirst) {
            tmpProxies.append(", ");
          }
          tmpProxies.append('\'').append(tmpHostsToProxyBypass).append('\'');
          tmpNotFirst = true;
        }
        println(tmpProxies.toString());
      }

      println("OutputDir:  '" + FilenameUtils.normalize(tmpConfiguration.getOutputDir().getAbsolutePath()) + "'");

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
    testCaseCout = tmpTestCases.size();

    if (tmpTestCases.isEmpty()) {
      println("TestFiles: none");
      return;
    }

    boolean tmpFirst = true;
    for (final TestCase tmpTestCase : tmpTestCases) {
      if (tmpFirst) {
        print("TestFiles:  '");
        print(tmpTestCase.getName());
        print("' (");
        print(FilenameUtils.normalize(tmpTestCase.getFile().getAbsolutePath()));
        println(")");
        tmpFirst = false;
        output.indent().indent().indent().indent().indent().indent();
      } else {
        print("'");
        print(tmpTestCase.getName());
        print("' (");
        print(FilenameUtils.normalize(tmpTestCase.getFile().getAbsolutePath()));
        println(")");
      }
      if (!tmpFirst) {
        output.unindent().unindent().unindent().unindent().unindent().unindent();
      }
    }
  }

  @Override
  public void testCaseStart(final TestCase aTestCase) {
    processedTestCaseCount++;
    println("TestCase: '" + aTestCase.getName() + "' (" + processedTestCaseCount + "/" + testCaseCout + ")");
  }

  @Override
  public void testRunStart(final String aBrowserName) {
    processedTestRunCount++;

    output.indent();
    println(aBrowserName);
    dotCount = 1;
  }

  @Override
  public void testFileStart(final String aFileName) {
  }

  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aCommand) {
  }

  @Override
  public void executeCommandSuccess() {
    stepsCount++;
    printProgressSign(".");
  }

  @Override
  public void executeCommandIgnored() {
    stepsCount++;
    ignoredCount++;
    printProgressSign("i");
  }

  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    stepsCount++;
    failureCount++;
    printProgressSign("F");
  }

  @Override
  public void executeCommandError(final Throwable aThrowable) {
    stepsCount++;
    errorCount++;
    printProgressSign("E");
  }

  @Override
  public void executeCommandEnd() {
  }

  @Override
  public void testFileEnd() {
  }

  @Override
  public void testRunIgnored() {
    testRunIgnoredCount++;
  }

  @Override
  public void testRunEnd() {
    println("");
    output.unindent();
  }

  @Override
  public void testCaseEnd() {
  }

  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    // print summary
    println("");
    if (testRunErrorCout > 0) {
      println("Failure");
      println("  " + testRunErrorCout + " erroneous Test Run(s)");
      println("");
    } else if (failureCount > 0 || errorCount > 0) {
      println("Failure");
      final StringBuilder tmpMsg = new StringBuilder("  ");
      if (failureCount > 0) {
        tmpMsg.append(Long.toString(failureCount));
        tmpMsg.append(" failing step(s)");
        if (errorCount > 0) {
          tmpMsg.append(" and ");
        }
      }
      if (errorCount > 0) {
        tmpMsg.append(Long.toString(errorCount));
        tmpMsg.append(" erroneous step(s)");
      }
      println(tmpMsg.toString());
      println("");
    } else {
      println("Success");
    }
    println("  Test Runs: " + processedTestRunCount + ",  Errors: " + testRunErrorCout + ",  Ignored: "
        + testRunIgnoredCount);
    println("  Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount + ",  Ignored: "
        + ignoredCount);
  }

  @Override
  public void responseStored(final String aResponseFileName) {
  }

  @Override
  public void highlightedResponse(final String aResponseFileName) {
  }

  @Override
  public void error(final Throwable aThrowable) {
    testRunErrorCout++;
    aThrowable.printStackTrace();
  }

  @Override
  public void warn(final String aMessageKey, final Object[] aParameterArray, final String aDetails) {
  }

  @Override
  public void info(final String aMessageKey, final Object[] aParameterArray) {
  }

  @Override
  public void htmlDescribe(final String aHtmlDescription) {
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

  /**
   * @return the testRunIgnoredCout
   */
  public long getTestRunIgnoredCout() {
    return testRunIgnoredCount;
  }

  /**
   * @return the testRunErrorCout
   */
  public long getTestRunErrorCount() {
    return testRunErrorCout;
  }
}
