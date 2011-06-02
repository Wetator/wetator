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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.wetator.jenkins.Messages;

/**
 * @author frank.danek
 */
public class TestResults extends AbstractBaseResult {

  private static final long serialVersionUID = 5332974371295204003L;

  private List<TestResult> testResults = new ArrayList<TestResult>();
  private transient List<BrowserResult> passedTests = new ArrayList<BrowserResult>();
  private transient List<BrowserResult> failedTests = new ArrayList<BrowserResult>();
  private transient int totalCount;
  private transient int passCount;
  private transient int failCount;
  private transient Map<String, TestFileResult> testFileMap = new HashMap<String, TestFileResult>();

  /**
   * The constructor.
   * 
   * @param name the name to set
   */
  public TestResults(String name) {
    this.name = name;
  }

  @Override
  public String getDisplayName() {
    return Messages.WetatorBuildReport_DisplayName();
  }

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
   * @return the failedTests
   */
  public List<BrowserResult> getFailedTests() {
    return failedTests;
  }

  public void setFailedTests(List<BrowserResult> failedTests) {
    this.failedTests = failedTests;
  }

  /**
   * @return the passedTests
   */
  public List<BrowserResult> getPassedTests() {
    return passedTests;
  }

  public void setPassedTests(List<BrowserResult> passedTests) {
    this.passedTests = passedTests;
  }

  /**
   * @return the testResults
   */
  public List<TestResult> getTestResults() {
    return testResults;
  }

  public void setTestResults(List<TestResult> testResults) {
    this.testResults = testResults;
  }

  /**
   * @return the totalCount
   */
  public int getTotalCount() {
    return totalCount;
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
   * @return the testFileMap
   */
  public Map<String, TestFileResult> getTestFileMap() {
    return testFileMap;
  }

  /**
   * @return the testFileNames
   */
  public Set<String> getTestFileNames() {
    return testFileMap.keySet();
  }

  /**
   * @param testFileMap the testFileMap to set
   */
  public void setTestFileMapp(Map<String, TestFileResult> testFileMap) {
    this.testFileMap = testFileMap;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#setOwner(hudson.model.AbstractBuild)
   */
  @Override
  public void setOwner(AbstractBuild<?, ?> owner) {
    super.setOwner(owner);
    for (TestResult tmpTest : testResults) {
      tmpTest.setOwner(owner);
    }
    for (String tmpClass : testFileMap.keySet()) {
      testFileMap.get(tmpClass).setOwner(owner);
    }
  }

  /**
   * Finds the result for the given name.
   * 
   * @param aName the name of the result
   * @return the result
   */
  public AbstractBaseResult findCorrespondingResult(String aName) {
    if (aName == null || getName().equals(aName)) {
      return this;
    }

    int tmpBraket = aName.indexOf("[");
    String tmpTestFileName = aName;
    String tmpBrowserName = null;
    if (tmpBraket >= 0) {
      // it's a browser
      tmpTestFileName = aName.substring(0, tmpBraket);
      tmpBrowserName = aName.substring(tmpBraket + 1, aName.length() - 1);
    }

    TestFileResult tmpTestFileResult = testFileMap.get(tmpTestFileName);
    if (tmpTestFileResult == null || tmpBrowserName == null) {
      return tmpTestFileResult;
    }

    BrowserResult tmpBrowserResult = tmpTestFileResult.getBrowserResult(tmpBrowserName);
    return tmpBrowserResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object anOther) {
    if (this == anOther) {
      return true;
    }
    if (anOther == null || getClass() != anOther.getClass()) {
      return false;
    }
    TestResults tmpOther = (TestResults) anOther;
    return name.equals(tmpOther.name) && !(owner != null ? !owner.equals(tmpOther.owner) : tmpOther.owner != null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int tmpResult;
    tmpResult = (owner != null ? owner.hashCode() : 0);
    tmpResult = 31 * tmpResult + name.hashCode();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TestResults{" + "name='" + name + '\'' + ", totalTests=" + totalCount + ", failedTests=" + failCount + '}';
  }

  // public String toSummary() {
  // // lets get the previous failed count
  // int previouseFailedTestCount = 0;
  // int previousTotalTestCount = 0;
  // List<TestResults> previousTestResults = TestResultHistoryUtil.getPreviousBuildTestResults(getOwner());
  // if (previousTestResults != null && previousTestResults.size() > 0) {
  // TestResults previousResult = previousTestResults.get(0);
  // previouseFailedTestCount = previousResult.getFailCount();
  // previousTotalTestCount = previousResult.getTotalCount();
  // }
  // return "<ul>" + diff(previousTotalTestCount, totalCount, "Total Tests")
  // + diff(previouseFailedTestCount, failCount, "Failed Tests") + printTestsUrls(getFailedTests()) + "</ul>";
  // }
  //
  // private static String diff(long prev, long curr, String name) {
  // if (prev <= curr) {
  // return "<li>" + name + ": " + curr + " (+" + (curr - prev) + ")</li>";
  // }
  // // if (a < b)
  // return "<li>" + name + ": " + curr + " (-" + (prev - curr) + ")</li>";
  // }
  //
  // public String printTestsUrls(List<BrowserResult> browserResults) {
  // StringBuffer htmlString = new StringBuffer();
  // htmlString.append("<OL>");
  // if (browserResults != null && browserResults.size() > 0) {
  // for (BrowserResult browserResult : browserResults) {
  // htmlString.append("<LI>");
  // if (browserResult.getParent() instanceof TestFileResult) {
  // // /${it.project.url}${_buildNumber}/${it.urlName}
  // htmlString.append("<a href=\"");
  // htmlString.append("/").append(getOwner().getProject().getUrl());
  // htmlString.append("/").append(getOwner().getNumber());
  // htmlString.append("/").append(getOwner().getProject().getAction(WetatorProjectReport.class).getUrlName());
  // htmlString.append("/").append(browserResult.getFullUrl());
  // htmlString.append("\">");
  // htmlString.append(browserResult.getFullName()).append("</a>");
  // } else {
  // htmlString.append(browserResult.getFullName());
  // }
  // htmlString.append("</LI>");
  // }
  //
  // }
  // htmlString.append("</OL>");
  // return htmlString.substring(0);
  // }
  //
  // public void set(TestResults that) {
  // this.failedTests = that.getFailedTests();
  // this.passedTests = that.getPassedTests();
  // this.testResults = that.getTestResults();
  // }
  //
  // public static TestResults total(Collection<TestResults>... results) {
  // Collection<TestResults> merged = merge(results);
  // TestResults total = new TestResults("");
  // for (TestResults individual : merged) {
  // total.add(individual, false);
  // }
  // total.tally();
  // return total;
  // }

  private void add(TestResults r, boolean tally) {
    testResults.addAll(r.getTestResults());
    failedTests.addAll(r.getFailedTests());
    passedTests.addAll(r.getPassedTests());
    if (tally) {
      // save cycles while getting total results
      tally();
    }
  }

  public void add(TestResults r) {
    add(r, true);
  }

  // private static Collection<TestResults> merge(Collection<TestResults>... aResults) {
  // Collection<TestResults> tmpNewResults = new ArrayList<TestResults>();
  // if (aResults.length == 0) {
  // return Collections.emptySet();
  // }
  // if (aResults.length == 1) {
  // return aResults[0];
  // }
  // List<String> tmpIndivNames = new ArrayList<String>();
  // for (Collection<TestResults> tmpResult : aResults) {
  // for (TestResults tmpIndividual : tmpResult) {
  // if (!tmpIndivNames.contains(tmpIndividual.name)) {
  // tmpIndivNames.add(tmpIndividual.name);
  // }
  // }
  // }
  // for (String tmpIndivName : tmpIndivNames) {
  // TestResults tmpIndivStat = new TestResults(tmpIndivName);
  // for (Collection<TestResults> tmpResult : aResults) {
  //
  // for (TestResults tmpIndividual : tmpResult) {
  // if (tmpIndivName.equals(tmpIndividual.name)) {
  // tmpIndivStat.add(tmpIndividual);
  // }
  // }
  // }
  // tmpNewResults.add(tmpIndivStat);
  // }
  // return tmpNewResults;
  // }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.jenkins.result.AbstractBaseResult#tally()
   */
  @Override
  public void tally() {
    duration = 0;
    passedTests = new ArrayList<BrowserResult>();
    failedTests = new ArrayList<BrowserResult>();
    testFileMap = new HashMap<String, TestFileResult>();
    for (TestResult tmpTestResult : testResults) {
      tmpTestResult.tally();
      duration += tmpTestResult.getDuration();

      for (TestFileResult tmpTestFileResult : tmpTestResult.getTestFileResults()) {
        String tmpFileName = tmpTestFileResult.getName();
        testFileMap.put(tmpFileName, tmpTestFileResult);
      }
    }
    for (String tmpFileName : testFileMap.keySet()) {
      TestFileResult tmpTestFileResult = testFileMap.get(tmpFileName);
      passedTests.addAll(tmpTestFileResult.getPassedTests());
      failedTests.addAll(tmpTestFileResult.getFailedTests());
    }
    failCount = failedTests.size();
    passCount = passedTests.size();
    totalCount = passCount + failCount;
  }

  /**
   * Used by stapler.
   * 
   * @param token the token to get
   * @param request the request
   * @param response the response
   * @return the {@link TestFileResult} if the token matches or null
   */
  public Object getDynamic(String token, StaplerRequest request, StaplerResponse response) {
    // the method parameters must be raw (without leading a) to make stapler work
    return testFileMap.get(token);
  }
}
