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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.wetator.core.TestCase;
import org.wetator.exception.InvalidInputException;

/**
 * The tests in this class should be the same as in {@link XSLTransformerTest} except the tests with a comment
 * 'additional'.
 * 
 * @author rbri
 * @author frank.danek
 */
public class XMLResultWriterTest extends AbstractProgressListenerTest {

  private static final String LOGS_FOLDER = "logs";
  private static final String RESULT_LOG = LOGS_FOLDER + "/wetresult.xml";

  private static final String IE8 = "IE8";
  private static final String FF17 = "Firefox17";

  @Rule
  public TestName testName = new TestName();

  @Override
  public void setupEnvironment() {
    super.setupEnvironment();

    when(configuration.getOutputDir()).thenReturn(new File(LOGS_FOLDER));

    progressListener = new XMLResultWriter();
  }

  @Test
  public void homepage() throws Exception {
    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase("wetator_google");
    progressListener.testCaseStart(tmpTestCase);

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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
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

    assertResult();
  }

  /**
   * additional.
   */
  @Test
  public void javaProperties() throws Exception {
    System.setProperty("test.sysprop1", "value1");
    System.setProperty("$test.sysprop2", "value2");
    System.setProperty("$$test.sysprop3", "value3");

    progressListener.init(engine);
    progressListener.start(engine);

    TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE8);
    writeGreenTestRun(tmpTestCase, FF17);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    System.clearProperty("test.sysprop1");
    System.clearProperty("$test.sysprop2");
    System.clearProperty("$$test.sysprop3");

    assertResult();
  }

  private void assertResult() throws IOException {
    InputStream tmpExpectedStream = getClass().getClassLoader().getResourceAsStream(
        "org/wetator/test/resource/xmlresult/" + testName.getMethodName() + ".xml");
    String tmpExpectedResult = IOUtils.toString(tmpExpectedStream, "UTF-8");

    File tmpActualFile = new File(RESULT_LOG);
    String tmpActualResult = FileUtils.readFileToString(tmpActualFile);

    tmpActualResult = normalizeResult(tmpActualResult);
    tmpExpectedResult = normalizeResult(tmpExpectedResult);

    Assert.assertEquals(tmpExpectedResult, tmpActualResult);
  }

  private String normalizeResult(String aResult) {
    String tmpResult = aResult;
    // unify line breaks
    tmpResult = tmpResult.replace("\r\n", "\n");
    // replace tabs
    tmpResult = tmpResult.replace("\t", "    ");
    // unify path delimiter
    tmpResult = tmpResult.replace('\\', '/');
    // remove drive letter (windows)
    tmpResult = tmpResult.replaceAll("file=\"[^/]*", "file=\"");

    // replace ids
    tmpResult = tmpResult.replaceAll("id=\"\\d+\"", "id=\"##ID##\"");

    // replace library versions
    tmpResult = replaceElementContent(tmpResult, "library", "([A-Za-z \\-]*)", "$2");
    // replace non-test java properties
    int tmpJavaPropertiesStart = tmpResult.indexOf("<java id=\"##ID##\">\n") + "<java id=\"##ID##\">\n".length();
    int tmpJavaPropertiesEnd = tmpResult.indexOf("    </java>\n");
    String tmpPart1 = tmpResult.substring(0, tmpJavaPropertiesStart);
    String tmpJavaPropertiesPart = tmpResult.substring(tmpJavaPropertiesStart, tmpJavaPropertiesEnd);
    String tmpPart2 = tmpResult.substring(tmpJavaPropertiesEnd);
    String[] tmpJavaProperties = tmpJavaPropertiesPart.split("\\n");
    StringBuilder tmpNormalizedJavaProperties = new StringBuilder();
    for (String tmpJavaProperty : tmpJavaProperties) {
      if (tmpJavaProperty.matches(".*key=\".*test\\.[^\"]+\".*")) {
        tmpNormalizedJavaProperties.append(tmpJavaProperty).append("\n");
      }
    }
    tmpResult = tmpNormalizedJavaProperties.insert(0, tmpPart1).append(tmpPart2).toString();
    // replace output dir
    tmpResult = tmpResult.replaceAll("<property([^k]*) key=\"wetator.outputDir\" value=\"[^\"]*",
        "<property$1 key=\"wetator.outputDir\" value=\"##OUTPUT_DIR##");
    // replace start time
    tmpResult = replaceElementContent(tmpResult, "startTime", "##START_TIME##");
    // replace execution time
    tmpResult = replaceElementContent(tmpResult, "executionTime", "##EXECUTION_TIME##");
    // replace line numbers in stack traces
    tmpResult = tmpResult.replaceAll(getClass().getSimpleName() + "\\.java:.*\\)", getClass().getSimpleName()
        + ".java)");
    tmpResult = tmpResult.replaceAll(AbstractProgressListenerTest.class.getSimpleName() + "\\.java:.*\\)",
        AbstractProgressListenerTest.class.getSimpleName() + ".java)");
    return tmpResult;
  }

  private String replaceElementContent(String anXML, String anElement, String aNewContent) {
    return replaceElementContent(anXML, anElement, "", aNewContent);
  }

  private String replaceElementContent(String anXML, String anElement, String anOldContent, String aNewContent) {
    return anXML.replaceAll("<" + anElement + "([^>]*)>" + anOldContent + "[^<]*", "<" + anElement + "$1>"
        + aNewContent);
  }
}
