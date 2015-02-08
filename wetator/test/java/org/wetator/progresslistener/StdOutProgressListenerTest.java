/*
 * Copyright (c) 2008-2015 wetator.org
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.wetator.core.IProgressListener;

/**
 * @author frank.danek
 */
public class StdOutProgressListenerTest extends AbstractProgressListenerTest {

  private ByteArrayOutputStream outStream;
  private PrintStream originalSysErr;

  @Override
  protected IProgressListener createProgressListener() {
    outStream = new ByteArrayOutputStream();

    PrintStream tmpOriginalSysOut = System.out;
    originalSysErr = System.err;
    System.setOut(new PrintStream(outStream));
    System.setErr(new PrintStream(outStream));

    IProgressListener tmpProgressListener = new StdOutProgressListener();

    System.setOut(tmpOriginalSysOut);
    return tmpProgressListener;
  }

  @After
  public void resetSysErr() {
    System.setErr(originalSysErr);
  }

  @Override
  protected String getExpectedFilename() {
    return "org/wetator/test/resource/result/stdout/" + testName.getMethodName() + ".txt";
  }

  @Override
  protected String getActualResult() throws Exception {
    return outStream.toString("UTF-8");
  }

  @Override
  protected String normalizeResult(String aResult) {
    String tmpResult = super.normalizeResult(aResult);

    // replace blank-only lines
    tmpResult = tmpResult.replaceAll("(?m)^\\s*$", "");

    // replace output dir
    tmpResult = tmpResult.replaceAll("OutputDir:  '[^']*", "OutputDir:  '##OUTPUT_DIR##");
    // replace stacktraces
    tmpResult = tmpResult.replaceAll("    at [^\\n]*\\n", "");
    return tmpResult;
  }
}
