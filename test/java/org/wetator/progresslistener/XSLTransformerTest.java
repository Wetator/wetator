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

import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.wetator.core.IProgressListener;

/**
 * @author tobwoerk
 * @author frank.danek
 */
public class XSLTransformerTest extends AbstractProgressListenerTest {

  private static final String XSLT = "run_report.xsl";
  private static final String RESULT_LOG = LOGS_FOLDER + "/" + XSLT + ".html";

  @Override
  public void setupEnvironment() {
    when(configuration.getXslTemplates()).thenReturn(Arrays.asList("xsl/" + XSLT));

    super.setupEnvironment();
  }

  @Override
  protected IProgressListener createProgressListener() {
    return new XMLResultWriter();
  }

  @Override
  protected String getExpectedFilename() {
    return "org/wetator/test/resource/result/report/" + testName.getMethodName() + ".html";
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
    tmpResult = tmpResult.replaceAll(">[^:/<]+:/", ">/");

    // replace ids
    tmpResult = tmpResult.replaceAll("(name|href|id)=\"([^\"\\d]*)\\d+\"", "$1=\"$2##ID##\"");
    tmpResult = tmpResult.replaceAll("'(testfile|log)_\\d+'", "'$1_##ID##'");
    // replace debug css
    tmpResult = tmpResult.replace("{display: none;}", "{}");

    // replace output dir
    tmpResult = tmpResult.replaceAll("<td>wetator.outputDir</td><td>&nbsp;&nbsp;</td><td>[^<]*",
        "<td>wetator.outputDir</td><td>&nbsp;&nbsp;</td><td>##OUTPUT_DIR##");
    // replace time
    tmpResult = tmpResult.replaceAll(">(\\d)+(\\.(\\d)+)+ (\\d)+(:(\\d)+)+<", ">##TIME##<");
    // replace durations
    tmpResult = tmpResult.replaceAll("><span title=\"\\d\\d:\\d\\d:\\d\\d:\\d\">[\\d]+(\\.[\\d])?s</span><",
        ">##DURATION##<");
    tmpResult = tmpResult.replaceAll("><td style=\"padding-left: 3px;\">[\\d]+(\\.[\\d])?s</td>",
        "><td style=\"padding-left: 3px;\">##DURATION##</td>");
    // replace stacktraces
    tmpResult = replaceLines(tmpResult, "<pre class=\"details\">[^\\n]*", "</pre>", null, "##DETAILS##");
    return tmpResult;
  }
}
