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


package org.wetator.jenkins;

import hudson.Functions;
import hudson.model.BuildListener;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.AbstractBuild;
import hudson.tasks.test.TestObject;
import hudson.util.HeapSpaceStringConverter;
import hudson.util.XStream2;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.ExportedBean;
import org.wetator.jenkins.result.AbstractBaseResult;
import org.wetator.jenkins.result.BrowserResult;
import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.TestError;
import org.wetator.jenkins.result.TestFileResult;
import org.wetator.jenkins.result.TestResult;
import org.wetator.jenkins.result.TestResults;
import org.wetator.jenkins.util.ErrorConverter;
import org.wetator.jenkins.util.GZIPXMLFile;

import com.thoughtworks.xstream.XStream;

/**
 * The Wetator Report for one build.
 *
 * @author frank.danek
 */
@ExportedBean
public class WetatorBuildReport implements HealthReportingAction, StaplerProxy, Serializable {

  private static final long serialVersionUID = -7087201387669424587L;

  private static final Logger LOG = Logger.getLogger(WetatorBuildReport.class.getName());

  public final AbstractBuild<?, ?> build;

  private transient WeakReference<TestResults> results;
  private Integer failCount;
  private Integer totalCount;

  private Map<String, String> descriptions = new ConcurrentHashMap<>();

  /**
   * The constructor.
   *
   * @param build the build this report belongs to
   * @param results the {@link TestResults} to report
   */
  public WetatorBuildReport(AbstractBuild<?, ?> build, TestResults results, BuildListener listener) {
    // the method parameters must be raw (without leading a) to make stapler work
    this.build = build;
    setResults(results, listener);
  }

  @Override
  public String getIconFileName() {
    return PluginImpl.ICON_FILE_NAME;
  }

  @Override
  public String getDisplayName() {
    return Messages.WetatorBuildReport_DisplayName();
  }

  @Override
  public String getUrlName() {
    return PluginImpl.URL_NAME;
  }

  /**
   * @return the build this report belongs to
   */
  public AbstractBuild<?, ?> getBuild() {
    return build;
  }

  /**
   * @return the {@link TestResults}
   */
  public synchronized TestResults getResults() {
    TestResults tmpResults;
    if (results == null) {
      tmpResults = load();
      results = new WeakReference<TestResults>(tmpResults);
    } else {
      tmpResults = results.get();
    }

    if (tmpResults == null) {
      tmpResults = load();
      results = new WeakReference<TestResults>(tmpResults);
    }
    if (totalCount == null) {
      totalCount = Integer.valueOf(tmpResults.getTotalCount());
      failCount = Integer.valueOf(tmpResults.getFailCount());
    }
    return tmpResults;
  }

  /**
   * Overwrites the {@link TestResults} by a new data set.
   *
   * @param aResults the results to set
   * @param aListener the current {@link BuildListener}
   */
  public synchronized void setResults(TestResults aResults, BuildListener aListener) {
    aResults.tally();
    aResults.setOwner(build);

    totalCount = Integer.valueOf(aResults.getTotalCount());
    failCount = Integer.valueOf(aResults.getFailCount());

    // persist the data
    try {
      getDataFile().write(aResults);
    } catch (IOException e) {
      e.printStackTrace(aListener.fatalError("Failed to save the Wetator test result"));
    }

    results = new WeakReference<TestResults>(aResults);
  }

  /**
   * Convenience method to access the totalCount.
   *
   * @return the totalCount
   */
  public int getTotalCount() {
    if (totalCount == null) {
      // this will compute the result
      getResults();
    }
    if (totalCount == null) {
      return 0;
    }
    return totalCount.intValue();
  }

  /**
   * Convenience method to access the failCount.
   *
   * @return the failCount
   */
  public int getFailCount() {
    if (failCount == null) {
      // this will compute the result
      getResults();
    }
    if (failCount == null) {
      return 0;
    }
    return failCount.intValue();
  }

  /**
   * {@link AbstractBaseResult}s do not have their own persistence mechanism, so updatable data of
   * {@link AbstractBaseResult}s need to be persisted by the owning {@link WetatorBuildReport}, and this method and
   * {@link #setDescription(AbstractBaseResult, String)} provide that logic. <br/>
   * <br/>
   * The default implementation stores information in the 'this' object.
   *
   * @see TestObject#getDescription()
   */
  public String getDescription(AbstractBaseResult aResult) {
    return descriptions.get(aResult.getName());
  }

  /**
   * @param aResult the result to set the description for
   * @param aDescription the description to set
   */
  public void setDescription(AbstractBaseResult aResult, String aDescription) {
    descriptions.put(aResult.getName(), aDescription);
  }

  /**
   * Used for deserialization (magic java method name).
   *
   * @return this
   */
  public Object readResolve() {
    if (descriptions == null) {
      descriptions = new ConcurrentHashMap<>();
    }
    return this;
  }

  /**
   * Gets the diff string of failures.<br/>
   * (for resultSummary.jelly)
   */
  public final String getFailureDiffString() {
    WetatorBuildReport tmpPrevious = getPreviousReport();
    if (tmpPrevious == null) {
      return ""; // no record
    }

    int tmpDiff = getFailCount() - tmpPrevious.getFailCount();
    return " " + Messages.WetatorBuildReport_FailureDiff(Functions.getDiffString(tmpDiff));
  }

  @Override
  public HealthReport getBuildHealth() {
    final int tmpTotalCount = getTotalCount();
    final int tmpFailCount = getFailCount();
    int tmpScore = 100;
    if (tmpTotalCount != 0) {
      tmpScore = (int) (100.0 * (1.0 - ((double) tmpFailCount) / tmpTotalCount));
    }
    Localizable tmpDisplayName = Messages._WetatorBuildReport_DisplayName();
    Localizable tmpDescription;
    if (tmpTotalCount == 0) {
      tmpDescription = Messages._WetatorBuildReport_zeroTestDescription(tmpDisplayName);
    } else {
      tmpDescription = Messages._WetatorBuildReport_TestsDescription(tmpDisplayName, tmpFailCount, tmpTotalCount);
    }
    return new HealthReport(tmpScore, tmpDescription);
  }

  /**
   * @return the previous result
   */
  public TestResults getPreviousResult() {
    WetatorBuildReport tmpPrevious = getPreviousReport();
    if (tmpPrevious == null) {
      return null;
    }
    return tmpPrevious.getResults();
  }

  /**
   * Finds the result for the given name.
   *
   * @param aName the name of the result
   * @return the result
   */
  public AbstractBaseResult findCorrespondingResult(String aName) {
    if (getResults() == null) {
      return null;
    }
    return getResults().findCorrespondingResult(aName);
  }

  private WetatorBuildReport getPreviousReport() {
    AbstractBuild<?, ?> tmpBuild = build;
    while (true) {
      tmpBuild = tmpBuild.getPreviousBuild();
      if (tmpBuild == null) {
        return null;
      }
      WetatorBuildReport tmpReport = tmpBuild.getAction(WetatorBuildReport.class);
      if (tmpReport != null) {
        return tmpReport;
      }
    }
  }

  private GZIPXMLFile getDataFile() {
    return new GZIPXMLFile(XSTREAM, new File(build.getRootDir(), PluginImpl.TEST_RESULTS_FILE_NAME));
  }

  /**
   * Loads a {@link TestResults} from disk.
   *
   * @return the loaded {@link TestResults}
   */
  private TestResults load() {
    TestResults tmpTestResults;
    try {
      tmpTestResults = (TestResults) getDataFile().read();
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Failed to load " + getDataFile());
      tmpTestResults = new TestResults("dummy"); // return a dummy
    }
    tmpTestResults.tally();
    tmpTestResults.setOwner(build);
    return tmpTestResults;
  }

  private static final XStream XSTREAM = new XStream2();

  static {
    initializeXStream(XSTREAM);
  }

  public static void initializeXStream(XStream anXStream) {
    anXStream.alias("testResults", TestResults.class);
    anXStream.alias("testResult", TestResult.class);
    anXStream.alias("testFileResult", TestFileResult.class);
    anXStream.alias("browserResult", BrowserResult.class);
    anXStream.alias("stepError", StepError.class);
    anXStream.alias("testError", TestError.class);
    anXStream.registerConverter(new ErrorConverter(anXStream.getMapper(), anXStream.getConverterLookup()
        .lookupConverterForType(TestError.class), anXStream.getReflectionProvider()));
    anXStream.registerConverter(new HeapSpaceStringConverter(), 100);
  }

  @Override
  public Object getTarget() {
    return getResults();
  }

  /**
   * Used by stapler.
   *
   * @param token the token to get
   * @param request the request
   * @param response the response
   * @return this if the token matches or null
   */
  public Object getDynamic(String token, StaplerRequest request, StaplerResponse response) {
    // the method parameters must be raw (without leading a) to make stapler work
    if (getResults() == null) {
      return null;
    }
    return getResults().getDynamic(token, request, response);
  }
}
