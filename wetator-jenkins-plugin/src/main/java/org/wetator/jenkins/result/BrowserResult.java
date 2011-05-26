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

import hudson.model.AbstractBuild;
import hudson.model.Run;

import java.util.logging.Logger;

import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.wetator.jenkins.Messages;
import org.wetator.jenkins.WetatorBuildReport;

/**
 * This class represents the result of one browser run of one wetator test file.
 * 
 * @author frank.danek
 */
public class BrowserResult extends AbstractBaseResult {

  private static final long serialVersionUID = -1507641316487210033L;

  private static final Logger LOG = Logger.getLogger(BrowserResult.class.getName());

  private StepError error;

  /**
   * This test has been failing since this build number (not id.)
   * If {@link #isPassed() passing}, this field is left unused to 0.
   */
  private int failedSince;

  /**
   * @return the url relative to the parent {@link WetatorBuildReport}
   */
  @Override
  public String getUrl() {
    return getParent().getUrl() + "/" + getRelativeUrl();
  }

  /**
   * @return the url relative to the parent {@link TestFileResult}
   */
  public String getRelativeUrl() {
    return getName();
  }

  /**
   * @return the error if there was any
   */
  public StepError getError() {
    return error;
  }

  /**
   * @param error the error to set
   */
  public void setError(StepError error) {
    this.error = error;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getPassCount()
   */
  @Override
  public int getPassCount() {
    if (isPassed()) {
      return 1;
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getFailCount()
   */
  @Override
  public int getFailCount() {
    if (!isPassed()) {
      return 1;
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#isPassed()
   */
  @Override
  public boolean isPassed() {
    return error == null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#tally()
   */
  @Override
  public void tally() {
    // nothing to do here
  }

  /**
   * @return the status of this test
   */
  @Exported(name = "status", visibility = 9)
  // because stapler notices suffix 's' and remove it
  public Status getStatus() {
    BrowserResult tmpPreviousResult = getPreviousResult();
    if (tmpPreviousResult == null) {
      return isPassed() ? Status.PASSED : Status.FAILED;
    }

    if (tmpPreviousResult.isPassed()) {
      return isPassed() ? Status.PASSED : Status.REGRESSION;
    }
    return isPassed() ? Status.FIXED : Status.FAILED;
  }

  /**
   * @return the number of consecutive builds (including this) that this test case has been failing
   */
  @Exported(visibility = 9)
  public int getAge() {
    if (isPassed()) {
      return 0;
    }
    if (getOwner() != null) {
      return getOwner().getNumber() - getFailedSince() + 1;
    }
    LOG.fine("Trying to get age of a BrowserResult without an owner");
    return 0;
  }

  /**
   * @return the build number when this test started failing if this test failed
   */
  @Exported(visibility = 9)
  public int getFailedSince() {
    // If we haven't calculated failedSince yet, and we should,
    // do it now.
    if (failedSince == 0 && !isPassed()) {
      BrowserResult tmpPreviousResult = getPreviousResult();
      if (tmpPreviousResult != null && !tmpPreviousResult.isPassed()) {
        failedSince = tmpPreviousResult.failedSince;
      } else if (getOwner() != null) {
        failedSince = getOwner().getNumber();
      } else {
        LOG.warning("trouble calculating getFailedSince. We've got prev, but no owner.");
        // failedSince will be 0, which isn't correct.
      }
    }
    return failedSince;
  }

  /**
   * @return the {@link Run} since when this test is failing
   */
  public Run<?, ?> getFailedSinceRun() {
    return getOwner().getParent().getBuildByNumber(getFailedSince());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getResultInBuild(hudson.model.AbstractBuild)
   */
  @Override
  public AbstractBaseResult getResultInBuild(AbstractBuild<?, ?> build) {
    WetatorBuildReport tmpBuildReport = build.getAction(WetatorBuildReport.class);
    if (tmpBuildReport == null) {
      return null;
    }
    return tmpBuildReport.findCorrespondingResult(getFullName());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#getPreviousResult()
   */
  @Override
  public BrowserResult getPreviousResult() {
    if (parent == null) {
      return null;
    }
    TestFileResult tmpPreviousResult = (TestFileResult) parent.getPreviousResult();
    if (tmpPreviousResult == null) {
      return null;
    }
    return tmpPreviousResult.getBrowserResult(getName());
  }

  /**
   * Used by stapler.
   * 
   * @param token the token to get
   * @param req the request
   * @param rsp the response
   * @return this if the token matches or null
   */
  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
    if (token.equals("/" + getName())) {
      return this;
    }
    return null;
  }

  /**
   * Constants that represent the status of this test.
   */
  public enum Status {
    /**
     * This test runs OK, just like its previous run.
     */
    PASSED("result-passed", Messages._BrowserResult_Status_Passed(), true),
    /**
     * This test failed, just like its previous run.
     */
    FAILED("result-failed", Messages._BrowserResult_Status_Failed(), false),
    /**
     * This test has been failing, but now it runs OK.
     */
    FIXED("result-fixed", Messages._BrowserResult_Status_Fixed(), true),
    /**
     * This test has been running OK, but now it failed.
     */
    REGRESSION("result-regression", Messages._BrowserResult_Status_Regression(), false);

    private final String cssClass;
    private final Localizable message;
    public final boolean isOK;

    /**
     * The constructor.
     * 
     * @param aCssClass the corresponding CSS class
     * @param aMessage the localized message
     * @param anOk true if the build was successful
     */
    Status(String aCssClass, Localizable aMessage, boolean anOk) {
      cssClass = aCssClass;
      message = aMessage;
      isOK = anOk;
    }

    /**
     * @return the cssClass
     */
    public String getCssClass() {
      return cssClass;
    }

    /**
     * @return the message
     */
    public String getMessage() {
      return message.toString();
    }

    /**
     * @return true if the status is {@link #REGRESSION}
     */
    public boolean isRegression() {
      return this == REGRESSION;
    }
  }
}
