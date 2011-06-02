/*
 * Copyright (c) 2008-2011 wetator.org
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

  private List<TestFileResult> testFileResults = new ArrayList<TestFileResult>();

  private transient int passCount;
  private transient int failCount;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getUrl()
   */
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

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getPassCount()
   */
  @Override
  public int getPassCount() {
    return passCount;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getFailCount()
   */
  @Override
  public int getFailCount() {
    return failCount;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#tally()
   */
  @Override
  public void tally() {
    passCount = 0;
    failCount = 0;
    for (TestFileResult tmpTestFileResult : testFileResults) {
      tmpTestFileResult.tally();
      if (tmpTestFileResult.isPassed()) {
        passCount++;
      } else {
        failCount++;
      }
    }
  }
}
