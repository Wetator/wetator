/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.jenkins.result;

import java.util.ArrayList;
import java.util.List;

/**
 * This class aggregates all {@link TestFileResult}s of one wetator run.
 *
 * @author frank.danek
 */
public class TestResult extends AbstractBaseResult {

  private static final long serialVersionUID = -3067231183241159976L;

  private List<TestFileResult> testFileResults = new ArrayList<>();

  private transient int passCount;
  private transient int skipCount;
  private transient int failCount;

  @Override
  public String getUrl() {
    return "";
  }

  /**
   * @return the testFileResults
   */
  public List<TestFileResult> getTestFileResults() {
    return testFileResults;
  }

  /**
   * @param testFileResults the testFileResults to set
   */
  public void setTestFileResults(List<TestFileResult> testFileResults) {
    this.testFileResults = testFileResults;
  }

  @Override
  public int getPassCount() {
    return passCount;
  }

  @Override
  public int getSkipCount() {
    return skipCount;
  }

  @Override
  public int getFailCount() {
    return failCount;
  }

  @Override
  public void tally() {
    passCount = 0;
    skipCount = 0;
    failCount = 0;
    for (TestFileResult tmpTestFileResult : testFileResults) {
      tmpTestFileResult.tally();
      passCount += tmpTestFileResult.getPassCount();
      skipCount += tmpTestFileResult.getSkipCount();
      failCount += tmpTestFileResult.getFailCount();
    }
  }
}
