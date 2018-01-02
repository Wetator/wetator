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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.wetator.core.Command;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;

/**
 * Manual test for creating result files and according reports.<br>
 * This test is manual because it is (was?) unstable due to timing data in report (e.g. differs between 0s and 0.1s).
 *
 * @author tobwoerk
 * @author frank.danek
 */
public class ManualXMLResultWriterTest {

  private static final String LOGS_FOLDER = "logs";
  private static final String REPORT_LOG = LOGS_FOLDER + "/run_report.xsl.html";

  private static final String COMMAND_NAME = "command";
  private static final String IE11 = "IE11";
  private static final String FF52 = "Firefox52";

  @Rule
  public TestName testName = new TestName();

  private XMLResultWriter resultWriter = new XMLResultWriter();
  private WetatorEngine engine = mock(WetatorEngine.class);
  private WetatorContext context = mock(WetatorContext.class);

  private int testNo;
  private int lineNo;

  @Before
  public void setupEnvironment() {
    final WetatorConfiguration tmpConfiguration = mock(WetatorConfiguration.class);
    when(engine.getConfiguration()).thenReturn(tmpConfiguration);
    when(tmpConfiguration.getOutputDir()).thenReturn(new File(LOGS_FOLDER));
    when(tmpConfiguration.getXslTemplates()).thenReturn(Arrays.asList("xsl/run_report.xsl"));

    when(context.replaceVariables(any())).thenCallRealMethod();
  }

  @Before
  public void resetNos() {
    testNo = 1;
    lineNo = 1;
  }

  @Test
  public void homepage() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase("wetator_google.wet");
    resultWriter.testCaseStart(tmpTestCase);

    // TODO screenshots missing
    lineNo = 1;
    resultWriter.testRunStart(IE11);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand(createCommand("open-url", "http://www.google.com"));
    writeCommand(createCommand("set", "search", "Wetator"));
    writeCommand(createCommand("click-on", "Google Search"));
    writeCommand(createCommand("assert-content", "www.wetator.org"));
    writeCommand(createCommand("click-on", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-title", "Wetator / Smart Web Application Testing"));
    writeCommand(createCommand("assert-content", "WETATOR IN A NUTSHELL Wetator is a tool"));
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();
    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void green() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE11);
    writeGreenTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void greenModule() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    lineNo = 1;
    resultWriter.testRunStart(FF52);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    writeCommand();
    endModule();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void red() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void redWithIgnoredModule() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeRedWithIgnoredModule(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void redModules() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeRedModule(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void redAfterModule() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    lineNo = 1;
    resultWriter.testRunStart(FF52);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(tmpTestCase);
    writeCommand();
    endModule();
    writeCommandWithError();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void blue() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeFailureTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void blueModules() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeBlueModule(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void mixed() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE11);
    writeGreenTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeFailureTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeErrorTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureAndErrorTestRun(tmpTestCase, IE11);
    writeFailureAndErrorTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeFailureTestRun(tmpTestCase, IE11);
    writeErrorTestRun(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void invalidInputDuringCommandExecution() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);

    resultWriter.testRunStart(IE11);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithError(createCommand("invalid-command", null),
        new InvalidInputException("Command in TestCase " + tmpTestCase.getName() + " is very invalid."));
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testRunStart(FF52);
    resultWriter.testRunIgnored();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void invalidInputWhileReadingCommands() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);

    resultWriter.testRunStart(IE11);
    resultWriter.testFileStart(tmpTestCase.getFile().getAbsolutePath());
    resultWriter.error(new InvalidInputException("TestCase " + tmpTestCase.getName() + " is very invalid."));
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();

    resultWriter.testRunStart(FF52);
    resultWriter.testRunIgnored();
    resultWriter.testRunEnd();

    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  @Test
  public void redModuleNotFound() throws Exception {
    resultWriter.init(engine);
    resultWriter.start(engine);

    final TestCase tmpTestCase = createTestCase();
    resultWriter.testCaseStart(tmpTestCase);
    writeRedModuleNotFound(tmpTestCase, FF52);
    resultWriter.testCaseEnd();

    resultWriter.end(engine);

    assertReport();
  }

  private void writeRedWithIgnoredModule(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    startModule(aTestCase);
    writeCommandIgnored();
    writeCommandIgnored();
    writeCommandIgnored();
    endModule();
    writeComment();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeRedModule(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(aTestCase);
    writeCommandWithFailure();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    endModule();
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeRedModuleNotFound(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    writeCommandWithFailure();
    startModule(aTestCase);
    resultWriter.executeCommandError(new FileNotFoundException("Module 'module.wet' not found."));
    endModule();
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeBlueModule(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeCommand();
    startModule(aTestCase);
    writeCommandWithFailure();
    writeCommand();
    endModule();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeGreenTestRun(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeFailureTestRun(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeErrorTestRun(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    writeComment();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeFailureAndErrorTestRun(final TestCase aTestCase, final String aBrowser) {
    lineNo = 1;
    resultWriter.testRunStart(aBrowser);
    resultWriter.testFileStart(aTestCase.getFile().getAbsolutePath());
    writeComment();
    writeCommand();
    writeCommandWithFailure();
    writeCommand();
    writeCommandWithError();
    writeCommandIgnored();
    resultWriter.testFileEnd();
    resultWriter.testRunEnd();
  }

  private void writeCommand() {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommand(tmpCommand);
  }

  private void writeCommandWithFailure() {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandFailure(new AssertionException("test failure", new RuntimeException("failure cause")));
    resultWriter.executeCommandEnd();
  }

  private void writeCommandWithError() {
    writeCommandWithError(new ActionException("test error"));
  }

  private void writeCommandWithError(final Exception anException) {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    writeCommandWithError(tmpCommand, anException);
  }

  private void writeCommandWithError(final Command aCommand, final Exception anException) {
    resultWriter.executeCommandStart(context, aCommand);
    resultWriter.executeCommandError(anException);
    resultWriter.executeCommandEnd();
  }

  private void writeCommandIgnored() {
    final Command tmpCommand = createCommand(COMMAND_NAME, "command value");
    resultWriter.executeCommandStart(context, tmpCommand);
    resultWriter.executeCommandIgnored();
    resultWriter.executeCommandEnd();
  }

  private void writeComment() {
    final Command tmpComment = createCommand(null, "comment value", true);
    writeCommand(tmpComment);
  }

  private void writeCommand(final Command aCommand) {
    resultWriter.executeCommandStart(context, aCommand);
    resultWriter.executeCommandEnd();
  }

  private Command createCommand(final String aCommandName, final String aParameterValue) {
    return createCommand(aCommandName, aParameterValue, false);
  }

  private Command createCommand(final String aCommandName, final String aParameterValue, final boolean anIsComment) {
    final Command tmpCommand = new Command(aCommandName, anIsComment);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    lineNo++;
    return tmpCommand;
  }

  private Command createCommand(final String aCommandName, final String aParameterValue,
      final String aSecondParameterValue) {
    final Command tmpCommand = new Command(aCommandName, false);
    tmpCommand.setLineNo(lineNo);
    tmpCommand.setFirstParameter(new Parameter(aParameterValue));
    tmpCommand.setSecondParameter(new Parameter(aSecondParameterValue));
    lineNo++;
    return tmpCommand;
  }

  private void startModule(final TestCase aTestCase) {
    resultWriter.executeCommandStart(context, createCommand("use-module", "module"));
    resultWriter.testFileStart("module" + aTestCase.getFile().getAbsolutePath());
  }

  private void endModule() {
    resultWriter.testFileEnd();
    resultWriter.executeCommandEnd();
  }

  private TestCase createTestCase() {
    final String tmpName = "test" + testNo++ + ".wet";
    return createTestCase(tmpName);
  }

  private TestCase createTestCase(final String aName) {
    return new TestCase(aName, new File("/Test/" + aName));
  }

  private void assertReport() throws IOException {
    final InputStream tmpExpectedStream = this.getClass().getClassLoader()
        .getResourceAsStream("org/wetator/test/resource/result/report/" + testName.getMethodName() + ".html");
    String tmpExpectedReport = getString(tmpExpectedStream);

    final File tmpActualFile = new File(REPORT_LOG);
    String tmpActualReport = FileUtils.readFileToString(tmpActualFile, StandardCharsets.UTF_8);

    final String tmpWetatorPath = System.getProperty("user.dir");

    tmpActualReport = tmpActualReport.replaceAll("ManualXMLResultWriterTest\\.java:.*\\)",
        "ManualXMLResultWriterTest.java)");
    tmpActualReport = tmpActualReport.replace("{display: none;}", "{}");
    tmpActualReport = tmpActualReport.replace("\r\n", "\n");
    tmpActualReport = tmpActualReport.replace("\t", "    ");
    tmpActualReport = tmpActualReport.replaceAll("testspec_\\d+", "testspec_x");
    tmpActualReport = tmpActualReport.replaceAll("testfile_\\d+", "testfile_x");
    tmpActualReport = tmpActualReport.replaceAll("log_\\d+", "log_x");
    tmpActualReport = tmpActualReport.replaceAll("\"#\\d+\"", "\"#x\"");
    tmpActualReport = tmpActualReport.replaceAll("\"\\d+\"", "\"#x\"");
    tmpActualReport = tmpActualReport.replaceAll(">.*?\\.wet<", ">##FILE##<");
    tmpActualReport = tmpActualReport.replace(tmpWetatorPath, "");
    tmpActualReport = tmpActualReport.replace('\\', '/');

    tmpExpectedReport = tmpExpectedReport.replaceAll("ManualXMLResultWriterTest\\.java:.*\\)",
        "ManualXMLResultWriterTest.java)");
    tmpExpectedReport = tmpExpectedReport.replace("{display: none;}", "{}");
    tmpExpectedReport = tmpExpectedReport.replace("\r\n", "\n");
    tmpExpectedReport = tmpExpectedReport.replaceAll("testspec_\\d+", "testspec_x");
    tmpExpectedReport = tmpExpectedReport.replaceAll("testfile_\\d+", "testfile_x");
    tmpExpectedReport = tmpExpectedReport.replaceAll("log_\\d+", "log_x");
    tmpExpectedReport = tmpExpectedReport.replaceAll("\"#\\d+\"", "\"#x\"");
    tmpExpectedReport = tmpExpectedReport.replaceAll("\"\\d+\"", "\"#x\"");
    tmpExpectedReport = tmpExpectedReport.replaceAll(">.*?\\.wet<", ">##FILE##<");
    tmpExpectedReport = tmpExpectedReport.replace(tmpWetatorPath, "");

    // Assert.assertEquals(tmpExpectedReport, tmpActualReport);
    for (String tmpExpectedPart : tmpExpectedReport.split("##[A-Z]*##")) {
      Assert.assertTrue("'" + tmpExpectedPart + "' not found", tmpActualReport.contains(tmpExpectedPart));
    }
  }

  private String getString(final InputStream anExpectedStream) throws IOException {
    final StringWriter tmpWriter = new StringWriter();
    IOUtils.copy(anExpectedStream, tmpWriter, "UTF-8");
    return tmpWriter.toString();
  }
}
