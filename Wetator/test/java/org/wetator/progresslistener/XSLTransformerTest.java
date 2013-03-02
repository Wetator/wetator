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

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.wetator.core.TestCase;
import org.wetator.exception.InvalidInputException;

/**
 * The tests in this class should be the same as in {@link XMLResultWriterTest} except the tests with a comment
 * 'additional'.
 * 
 * @author tobwoerk
 * @author frank.danek
 */
public class XSLTransformerTest extends AbstractProgressListenerTest {

  private static final String LOGS_FOLDER = "logs";
  private static final String REPORT_LOG = LOGS_FOLDER + "/run_report.xsl.html";

  private static final String IE8 = "IE8";
  private static final String FF17 = "Firefox17";

  @Rule
  public TestName testName = new TestName();

  @Override
  public void setupEnvironment() {
    super.setupEnvironment();

    when(configuration.getOutputDir()).thenReturn(new File(LOGS_FOLDER));
    when(configuration.getXslTemplates()).thenReturn(Arrays.asList("xsl/run_report.xsl"));

    progressListener = new XMLResultWriter();
  }

  @Test
  public void homepage() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase("wetator_google");
    progressListener.testCaseStart(tmpTestCase);

    // TODO screenshots missing
    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand(createCommand("open-url", "http://www.google.com"));
    writeCommand(createCommand("set", "search", "Wetator"));
    writeCommand(createCommand("click-on", "Google Search"));
    writeCommand(createCommand("assert-content", "www.wetator.org"));
    writeCommand(createCommand("click-on", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-title", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-content", "WETATOR IN A NUTSHELL Wetator is a tool"));
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();
    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void green() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void greenModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF17);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    writeCommand();
    endModule();
    writeCommand();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void red() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void redWithIgnoredModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedWithIgnoredModule(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void redModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModule(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void redAfterModule() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    progressListener.testRunStart(FF17);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommandWithError();
    progressListener.testFileEnd();
    progressListener.testRunEnd();
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void blue() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void blueModules() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeBlueModule(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void mixed() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeFailureTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureAndErrorTestRun(tmpTestCase, IE8);
    writeFailureAndErrorTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE8);
    writeErrorTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void invalidInputDuringCommandExecution() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithError(createCommand("invalid-command", null), new InvalidInputException("Command in TestCase "
        + tmpTestCase.getName() + " is very invalid."));
    writeCommandIgnored();
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF17);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void invalidInputWhileReadingCommands() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);

    progressListener.testRunStart(IE8);
    progressListener.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    progressListener.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    progressListener.testFileEnd();
    progressListener.testRunEnd();

    progressListener.testRunStart(FF17);
    progressListener.testRunIgnored();
    progressListener.testRunEnd();

    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  @Test
  public void redModuleNotFound() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeRedModuleNotFound(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    assertReport();
  }

  private void assertReport() throws IOException {
    InputStream tmpExpectedStream = this.getClass().getClassLoader()
        .getResourceAsStream("org/wetator/test/resource/report/" + testName.getMethodName() + ".html");
    String tmpExpectedReport = IOUtils.toString(tmpExpectedStream, "UTF-8");

    File tmpActualFile = new File(REPORT_LOG);
    String tmpActualReport = FileUtils.readFileToString(tmpActualFile);

    tmpActualReport = normalizeReport(tmpActualReport);
    tmpExpectedReport = normalizeReport(tmpExpectedReport);

    Assert.assertEquals(tmpExpectedReport, tmpActualReport);
  }

  private String normalizeReport(String aReport) {
    String tmpReport = aReport;
    // unify line breaks
    tmpReport = tmpReport.replace("\r\n", "\n");
    // replace tabs
    tmpReport = tmpReport.replace("\t", "    ");
    // unify path delimiter
    tmpReport = tmpReport.replace('\\', '/');
    // remove drive letter (windows)
    tmpReport = tmpReport.replaceAll(">[^:/<]+:/", ">/");

    // replace ids
    tmpReport = tmpReport.replaceAll("(name|href|id)=\"([^\"\\d]*)\\d+\"", "$1=\"$2##ID##\"");
    tmpReport = tmpReport.replaceAll("'(testfile|log)_\\d+'", "$1_2##ID##\"");
    // replace debug css
    tmpReport = tmpReport.replace("{display: none;}", "{}");

    // replace output dir
    tmpReport = tmpReport.replaceAll("<td>wetator.outputDir</td><td>&nbsp;&nbsp;</td><td>[^<]*",
        "<td>wetator.outputDir</td><td>&nbsp;&nbsp;</td><td>##OUTPUT_DIR##");
    // replace line numbers in stack traces
    tmpReport = tmpReport.replaceAll(getClass().getSimpleName() + "\\.java:.*\\)", getClass().getSimpleName()
        + ".java)");
    tmpReport = tmpReport.replaceAll(AbstractProgressListenerTest.class.getSimpleName() + "\\.java:.*\\)",
        AbstractProgressListenerTest.class.getSimpleName() + ".java)");
    return tmpReport;
  }
}
