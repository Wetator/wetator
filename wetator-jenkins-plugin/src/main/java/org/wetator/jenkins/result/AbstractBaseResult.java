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

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractModelObject;
import hudson.model.Run;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.wetator.jenkins.History;
import org.wetator.jenkins.WetatorBuildReport;

/**
 * The base class for all test results.
 * 
 * @author frank.danek
 */
public abstract class AbstractBaseResult extends AbstractModelObject implements Serializable {

  private static final long serialVersionUID = -139906426226400784L;

  private static final Logger LOG = Logger.getLogger(AbstractBaseResult.class.getName());

  protected transient AbstractBuild<?, ?> owner;
  protected String name;
  protected transient AbstractBaseResult parent;
  protected String fullName;
  protected long duration;

  /**
   * {@inheritDoc}
   * 
   * @see hudson.model.ModelObject#getDisplayName()
   */
  @Override
  public String getDisplayName() {
    return getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see hudson.search.SearchItem#getSearchUrl()
   */
  @Override
  public String getSearchUrl() {
    return safe(getName());
  }

  /**
   * @return the owning build
   */
  public AbstractBuild<?, ?> getOwner() {
    return owner;
  }

  /**
   * @param owner the owning build to set
   */
  public void setOwner(AbstractBuild<?, ?> owner) {
    this.owner = owner;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the parent result
   */
  public AbstractBaseResult getParent() {
    return parent;
  }

  /**
   * @param parent the parent result to set
   */
  public void setParent(AbstractBaseResult parent) {
    this.parent = parent;
  }

  /**
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * @param fullName the full name to set
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Replaces URL-unsafe characters.
   */
  public static String safe(String s) {
    // 3 replace calls is still 2-3x faster than a regex replaceAll
    return s.replace('/', '_').replace('\\', '_').replace(':', '_');
  }

  /**
   * @return URL relative to the {@link WetatorBuildReport}
   */
  public String getUrl() {
    return safe(getName());
  }

  /**
   * @return the duration
   */
  public long getDuration() {
    return duration;
  }

  /**
   * @param aDuration the duration to set
   */
  public void setDuration(long aDuration) {
    duration = aDuration;
  }

  /**
   * @return the string representation of the {@link #getDuration()} in a human readable format
   */
  public String getDurationString() {
    return Util.getTimeSpanString(getDuration());
  }

  /**
   * Gets the total number of passed tests.
   */
  public abstract int getPassCount();

  /**
   * Gets the total number of failed tests.
   */
  public abstract int getFailCount();

  /**
   * @return true if the test did not fail, false otherwise.
   */
  public boolean isPassed() {
    return (getFailCount() == 0);
  }

  /**
   * Tally the results.
   */
  public abstract void tally();

  /**
   * @return the history
   */
  public History getHistory() {
    return new History(this);
  }

  /**
   * @return the {@link WetatorBuildReport} that points to the top level test result includes
   *         this test result
   */
  public WetatorBuildReport getParentBuildReport() {
    return owner.getAction(WetatorBuildReport.class);
  }

  /**
   * Gets the counter part of this {@link AbstractBaseResult} in the previous run.
   * 
   * @return null if no such counter part exists.
   */
  public AbstractBaseResult getPreviousResult() {
    AbstractBuild<?, ?> tmpBuild = getOwner();
    if (tmpBuild == null) {
      return null;
    }
    while (true) {
      tmpBuild = tmpBuild.getPreviousBuild();
      if (tmpBuild == null) {
        return null;
      }
      WetatorBuildReport tmpBuildReport = tmpBuild.getAction(WetatorBuildReport.class);
      if (tmpBuildReport != null) {
        AbstractBaseResult tmpResult = tmpBuildReport.findCorrespondingResult(getName());
        if (tmpResult != null) {
          return tmpResult;
        }
      }
    }
  }

  /**
   * Gets the counter part of this {@link AbstractBaseResult} in the specified run.
   * 
   * @return null if no such counter part exists.
   */
  public AbstractBaseResult getResultInBuild(AbstractBuild<?, ?> build) {
    WetatorBuildReport tmpBuildReport = build.getAction(WetatorBuildReport.class);
    if (tmpBuildReport == null) {
      return null;
    }
    return tmpBuildReport.findCorrespondingResult(getName());
  }

  /**
   * @return the description (= annotation of the result)
   */
  public String getDescription() {
    WetatorBuildReport tmpReport = getParentBuildReport();
    if (tmpReport != null) {
      return tmpReport.getDescription(this);
    }
    return "";
  }

  /**
   * @param description the description (= annotation of the result) to set
   */
  public void setDescription(String description) {
    // the method parameters must be raw (without leading a) to make stapler work
    WetatorBuildReport tmpReport = owner.getAction(WetatorBuildReport.class);
    if (tmpReport != null) {
      tmpReport.setDescription(this, description);
    }
  }

  /**
   * Used by stabler.
   * 
   * @param description the description to save
   * @return the response
   * @throws IOException in case of problems
   */
  public synchronized HttpResponse doSubmitDescription(@QueryParameter String description) throws IOException {
    // the method parameters must be raw (without leading a) to make stapler work
    if (getOwner() == null) {
      LOG.severe("getOwner() is null, can't save description.");
    } else {
      getOwner().checkPermission(Run.UPDATE);
      setDescription(description);
      getOwner().save();
    }

    return new HttpRedirect(".");
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name;
  }
}
