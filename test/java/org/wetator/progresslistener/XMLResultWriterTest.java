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


package org.wetator.progresslistener;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.wetator.core.IProgressListener;
import org.wetator.core.TestCase;

/**
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class XMLResultWriterTest extends AbstractProgressListenerTest {

  private static final String RESULT_LOG = LOGS_FOLDER + "/wetresult.xml";

  @Override
  protected IProgressListener createProgressListener() {
    return new XMLResultWriter();
  }

  @Test
  public void javaProperties() throws Exception {
    System.setProperty("test.sysprop1", "value1");
    System.setProperty("$test.sysprop2", "value2");
    System.setProperty("$$test.sysprop3", "value3");

    progressListener.init(engine);
    progressListener.start(engine);

    final TestCase tmpTestCase = createTestCase();
    progressListener.testCaseStart(tmpTestCase);
    writeGreenTestRun(tmpTestCase, IE11);
    writeGreenTestRun(tmpTestCase, FF78);
    progressListener.testCaseEnd();

    progressListener.end(engine);

    System.clearProperty("test.sysprop1");
    System.clearProperty("$test.sysprop2");
    System.clearProperty("$$test.sysprop3");

    assertResult();
  }

  @Override
  protected String getExpectedFilename() {
    return "org/wetator/test/resource/result/xml/" + testName.getMethodName() + ".xml";
  }

  @Override
  protected String getActualResult() throws Exception {
    final File tmpActualFile = new File(RESULT_LOG);
    return FileUtils.readFileToString(tmpActualFile, StandardCharsets.UTF_8);
  }

  @Override
  protected String normalizeResult(final String aResult) {
    String tmpResult = super.normalizeResult(aResult);

    // remove drive letter (windows)
    tmpResult = tmpResult.replaceAll("file=\"[^/]*", "file=\"");

    // replace ids
    tmpResult = tmpResult.replaceAll("id=\"\\d+\"", "id=\"##ID##\"");

    // replace library versions
    tmpResult = replaceElementContent(tmpResult, "library", "([A-Za-z \\-]*)", "$2");
    // replace non-test java properties
    tmpResult = replaceLines(tmpResult, "<java id=\"##ID##\">", "\\s*</java>$", ".*key=\".*test\\.[^\"]+\".*",
        "##JAVA_PROPERTIES##");
    // replace output dir
    tmpResult = tmpResult.replaceAll("<property([^k]*) key=\"wetator.outputDir\" value=\"[^\"]*",
        "<property$1 key=\"wetator.outputDir\" value=\"##OUTPUT_DIR##");
    // replace start time
    tmpResult = replaceElementContent(tmpResult, "startTime", "##START_TIME##");
    // replace execution time
    tmpResult = replaceElementContent(tmpResult, "executionTime", "##EXECUTION_TIME##");
    // replace stacktraces
    tmpResult = replaceLines(tmpResult, "<details id=\"##ID##\">[^\\n]*", "</details>", null, "##DETAILS##");
    return tmpResult;
  }

  private String replaceElementContent(final String anXML, final String anElement, final String aNewContent) {
    return replaceElementContent(anXML, anElement, "", aNewContent);
  }

  private String replaceElementContent(final String anXML, final String anElement, final String anOldContent,
      final String aNewContent) {
    return anXML.replaceAll("<" + anElement + "([^>]*)>" + anOldContent + "[^<]*",
        "<" + anElement + "$1>" + aNewContent);
  }
}
