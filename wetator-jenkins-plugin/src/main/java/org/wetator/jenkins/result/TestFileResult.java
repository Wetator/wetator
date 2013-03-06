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


package org.wetator.jenkins.result;

import hudson.model.AbstractBuild;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * This class represents the results of one wetator test file and aggregates all {@link BrowserResult}s for this file.
 * 
 * @author frank.danek
 */
public class TestFileResult extends AbstractBaseResult {

  private static final long serialVersionUID = -2934518518588582888L;

  private List<BrowserResult> browserResults = new ArrayList<BrowserResult>();

  private transient List<BrowserResult> passedTests = new ArrayList<BrowserResult>();
  private transient List<BrowserResult> skippedTests = new ArrayList<BrowserResult>();
  private transient List<BrowserResult> failedTests = new ArrayList<BrowserResult>();
  private transient int skipCount;
  private transient int failCount;
  private transient int totalCount;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getPassCount()
   */
  @Override
  public int getPassCount() {
    return totalCount - skipCount - failCount;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getSkipCount()
   */
  @Override
  public int getSkipCount() {
    return skipCount;
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
   * @return the totalCount
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * @param totalCount the totalCount to set
   */
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  /**
   * @return a new list containing all browser results
   */
  public List<BrowserResult> getBrowserResults() {
    List<BrowserResult> tmpList = new ArrayList<BrowserResult>();
    for (BrowserResult tmpBrowserResult : browserResults) {
      tmpList.add(tmpBrowserResult);
    }
    return tmpList;
  }

  /**
   * @param browserResults the browserResults to set
   */
  public void setBrowserResults(List<BrowserResult> browserResults) {
    this.browserResults = browserResults;
  }

  /**
   * @return the passedTests
   */
  public List<BrowserResult> getPassedTests() {
    return passedTests;
  }

  /**
   * @return the skippedTests
   */
  public List<BrowserResult> getSkippedTests() {
    return skippedTests;
  }

  /**
   * @return the failedTests
   */
  public List<BrowserResult> getFailedTests() {
    return failedTests;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#setOwner(hudson.model.AbstractBuild)
   */
  @Override
  public void setOwner(AbstractBuild<?, ?> owner) {
    super.setOwner(owner);

    for (BrowserResult tmpBrowserResult : this.browserResults) {
      tmpBrowserResult.setOwner(owner);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#tally()
   */
  @Override
  public void tally() {
    duration = 0;
    skipCount = 0;
    failCount = 0;
    totalCount = 0;
    passedTests = new ArrayList<BrowserResult>();
    skippedTests = new ArrayList<BrowserResult>();
    failedTests = new ArrayList<BrowserResult>();
    for (BrowserResult tmpBrowserResult : browserResults) {
      duration += tmpBrowserResult.getDuration();
      totalCount++;
      if (tmpBrowserResult.isPassed()) {
        passedTests.add(tmpBrowserResult);
      } else if (tmpBrowserResult.isSkipped()) {
        skippedTests.add(tmpBrowserResult);
        skipCount++;
      } else {
        failedTests.add(tmpBrowserResult);
        failCount++;

      }
      tmpBrowserResult.setParent(this);
    }
  }

  /**
   * @param aName the name of the {@link BrowserResult}
   * @return the {@link BrowserResult} for the given name or null if nothing found
   */
  public BrowserResult getBrowserResult(String aName) {
    for (BrowserResult tmpBrowserResult : browserResults) {
      if (tmpBrowserResult.getName().equals(aName)) {
        return tmpBrowserResult;
      }
    }
    return null;
  }

  /**
   * Used by stapler.
   * 
   * @param token the token to get
   * @param req the request
   * @param rsp the response
   * @return this or a child {@link BrowserResult} if the token matches or null
   */
  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
    // the method parameters must be raw (without leading a) to make stapler work
    if (token.equals("/" + safe(getName()))) {
      return this;
    }
    if (browserResults != null) {
      for (BrowserResult tmpBrowserResult : browserResults) {
        if (token.equals(safe(tmpBrowserResult.getName()))) {
          return tmpBrowserResult;
        }
      }
    }
    return null;
  }
}
