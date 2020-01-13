/*
 * Copyright (c) 2008-2018 wetator.org
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

  /** The output used. */
  protected Output output;

  private static final int DOTS_PER_LINE = 100;
  private int dotCount;

  // count the test cases (files)
  private int testCaseCountTotal;
  private int testCaseCountProcessed;

  // count the overall tests (runs)
  private int testCountProcessed;
  private int testCountError;
  private int testCountFailure;
  private int testCountIgnored;

  // count the overall test steps
  private long stepCountTotal;
  private long stepCountError;
  private long stepCountFailure;
  private long stepCountIgnore;

  // helper remembering the result per test (run)
  private TestResult testResult;

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
    testCaseCountProcessed = 0;

    testCountProcessed = 0;
    testCountError = 0;
    testCountFailure = 0;
    testCountIgnored = 0;

    stepCountTotal = 0;
    stepCountError = 0;
    stepCountFailure = 0;
    stepCountIgnore = 0;

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
    testCaseCountTotal = tmpTestCases.size();

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
    }
  }

  @Override
  public void testCaseStart(final TestCase aTestCase) {
    testCaseCountProcessed++;
    println("TestCase: '" + aTestCase.getName() + "' (" + testCaseCountProcessed + "/" + testCaseCountTotal + ")");
  }

  @Override
  public void testRunStart(final String aBrowserName) {
    testCountProcessed++;
    testResult = TestResult.SUCCESS;

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
    stepCountTotal++;
    printProgressSign(".");
  }

  @Override
  public void executeCommandIgnored() {
    stepCountTotal++;
    stepCountIgnore++;
    printProgressSign("i");
  }

  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    stepCountTotal++;
    stepCountFailure++;
    if (TestResult.ERROR != testResult) {
      testResult = TestResult.FAILURE;
    }
    printProgressSign("F");
  }

  @Override
  public void executeCommandError(final Throwable aThrowable) {
    stepCountTotal++;
    stepCountError++;
    testResult = TestResult.ERROR;
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
    testCountIgnored++;
  }

  @Override
  public void testRunEnd() {
    println("");
    output.unindent();

    switch (testResult) {
      case ERROR:
        testCountError++;
        break;
      case FAILURE:
        testCountFailure++;
        break;
      default:
        // nothing to do for successful and ignored tests
    }
  }

  @Override
  public void testCaseEnd() {
  }

  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    // print summary
    println("");
    final int tmpUnsuccessfulTestCount = testCountError + testCountFailure + testCountIgnored;
    if (tmpUnsuccessfulTestCount > 0) {
      println("Failure");
      println("  " + tmpUnsuccessfulTestCount + " unsuccessful Test" + (tmpUnsuccessfulTestCount > 1 ? "s" : ""));
      println("");
    } else {
      println("Success");
    }
    println("  Tests: " + testCountProcessed + ",  Errors: " + testCountError + ",  Failures: " + testCountFailure
        + ",  Ignored: " + testCountIgnored);
    println("  Steps: " + stepCountTotal + ",  Errors: " + stepCountError + ",  Failures: " + stepCountFailure
        + ",  Ignored: " + stepCountIgnore);
  }

  @Override
  public void responseStored(final String aResponseFileName) {
  }

  @Override
  public void highlightedResponse(final String aResponseFileName) {
  }

  @Override
  public void error(final Throwable aThrowable) {
    testResult = TestResult.ERROR;
    aThrowable.printStackTrace(); // NOPMD
  }

  @Override
  public void warn(final String aMessageKey, final Object[] aParameters, final String aDetails) {
  }

  @Override
  public void info(final String aMessageKey, final Object... aParameters) {
  }

  @Override
  public void htmlDescribe(final String aHtmlDescription) {
  }

  /**
   * @return the number of tests
   */
  public long getTestCountProcessed() {
    return testCountProcessed;
  }

  /**
   * @return the number of erroneous tests
   */
  public long getTestCountError() {
    return testCountError;
  }

  /**
   * @return the number of failing tests
   */
  public long getTestCountFailure() {
    return testCountFailure;
  }

  /**
   * @return the number of ignored tests
   */
  public long getTestCountIgnored() {
    return testCountIgnored;
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
      e.printStackTrace(); // NOPMD
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
      e.printStackTrace(); // NOPMD
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
   * Summarized result of a test (run).
   */
  private enum TestResult {
    SUCCESS,
    ERROR,
    FAILURE
  }
}
