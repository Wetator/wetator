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


package org.wetator.jenkins.plugin;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.tasks.Builder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;

/**
 * Abstract base test for all tests testing the plugin running within Jenkins.
 *
 * @author frank.danek
 */
public abstract class AbstractPluginTest extends HudsonTestCase {

  protected static final String WETATOR_RESULT_FILENAME = "wetresult.xml";
  protected static final String WETATOR_REPORT_FILENAME = "run_report.xsl.html";
  protected static final String WETATOR_RESULT_PATH = "src/test/resources/org/wetator/jenkins/wetresult/";

  protected WebClient webClient;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    webClient = createWebClient();
  }

  protected FreeStyleProject createProject(String aWetatorResultFile) throws Exception {
    return createProject(aWetatorResultFile, null);
  }

  protected FreeStyleProject createProject(String aWetatorResultFile, String aWetatorReportFile) throws Exception {
    FreeStyleProject tmpProject = createFreeStyleProject();

    HtmlForm tmpConfigForm = createWebClient().getPage(tmpProject, "configure").getFormByName("config");
    tmpConfigForm.getInputByName("org-wetator-jenkins-WetatorRecorder").setChecked(true);
    tmpConfigForm.getInputsByName("_.testResults").get(1).setValueAttribute(WETATOR_RESULT_FILENAME);
    tmpConfigForm.getInputByName("_.testReports").setValueAttribute(WETATOR_REPORT_FILENAME);
    submit(tmpConfigForm);

    tmpProject.getBuildersList().add(new CopyBuilder(aWetatorResultFile, aWetatorReportFile));

    System.out.println("Created project '" + tmpProject.getName() + "'.");
    return tmpProject;
  }

  protected FreeStyleBuild runBuild(FreeStyleProject aProject) throws InterruptedException, ExecutionException {
    FreeStyleBuild tmpBuild = aProject.scheduleBuild2(0).get();
    System.out.println("Project '" + aProject.getName() + "' Build '" + tmpBuild.getDisplayName() + "' completed.");
    return tmpBuild;
  }

  protected void assertBuild(Result anExpectedResult, int anExpectedScore, int anExpectedFailCount,
      int anExceptedTotalCount, FreeStyleBuild anActualBuild) {
    assertEquals(anExpectedResult, anActualBuild.getResult());

    assertEquals(anExpectedScore, anActualBuild.getProject().getBuildHealth().getScore());
    assertEquals(
        "Wetator Test Result: " + anExpectedFailCount + " test" + (anExpectedFailCount == 1 ? "" : "s")
            + " failing out of a total of " + anExceptedTotalCount + " test" + (anExceptedTotalCount == 1 ? "" : "s")
            + ".", anActualBuild.getProject().getBuildHealth().getDescription());
  }

  protected void assertProjectAndBuildPage(String anExpectedMessage, FreeStyleBuild anActualBuild) throws IOException,
      SAXException {
    HtmlPage tmpProjectPage = webClient.getPage(anActualBuild.getProject());
    WebAssert.assertTextPresent(tmpProjectPage, "Last Wetator Report");

    HtmlPage tmpBuildPage = webClient.getPage(anActualBuild.getProject(), "" + anActualBuild.getNumber());
    WebAssert.assertTextPresent(tmpBuildPage, "Wetator Test Result (" + anExpectedMessage + ")");
  }

  protected void assertReportPage(int anExpectedFailCount, int anExpectedTotalCount, HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, "Wetator Test Result");
    WebAssert.assertTextPresent(anActualPage, anExpectedFailCount + " failure" + (anExpectedFailCount == 1 ? "" : "s"));
    WebAssert.assertTextPresent(anActualPage, anExpectedTotalCount + " test" + (anExpectedTotalCount == 1 ? "" : "s"));
    if (anExpectedFailCount == 0) {
      WebAssert.assertTextNotPresent(anActualPage, "All Failed Tests");
    } else {
      WebAssert.assertTextPresent(anActualPage, "All Failed Tests");
    }
  }

  protected void assertTestPage(String anExpectedFile, String anExpectedAbsoluteFile, HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, anExpectedFile);
    WebAssert.assertTextPresent(anActualPage, anExpectedFile);
    WebAssert.assertTextPresent(anActualPage, anExpectedAbsoluteFile);
  }

  protected void assertBrowserPage(String anExpectedBrowser, String anExpectedResult, String anExpectedFile,
      String anExpectedAbsoluteFile, HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, anExpectedBrowser);
    WebAssert.assertTextPresent(anActualPage, anExpectedResult);
    WebAssert.assertTextPresent(anActualPage, anExpectedFile + "[" + anExpectedBrowser + "] from "
        + anExpectedAbsoluteFile);
  }

  protected void assertBrowserError(String anExpectedAbsoluteFile, String anExpectedCause,
      String anExpectedCommandLine, String anExpectedMessage, HtmlPage anActualPage) {
    WebAssert.assertTextPresent(anActualPage, "Failing for the past 1 build");
    WebAssert.assertTextPresent(anActualPage, "Error Message");
    WebAssert.assertTextPresent(anActualPage, "File: " + anExpectedAbsoluteFile);
    WebAssert.assertTextPresent(anActualPage, "Cause: " + anExpectedCause);
    assertPaneTableRowContains(anActualPage, 0, anExpectedCommandLine);
    assertPaneTableRowContains(anActualPage, 1, anExpectedMessage);
  }

  protected void assertPaneTableRowContains(HtmlPage aHtmlPage, int aRow, String... anExpectedContents) {
    assertPaneTableRowContains(aHtmlPage, 0, aRow, anExpectedContents);
  }

  protected void assertPaneTableRowContains(HtmlPage aHtmlPage, int aTable, int aRow, String... anExpectedContents) {
    String tmpContent = getPaneTableRowContent(aHtmlPage, aTable, aRow);
    for (String tmpExpectedContent : anExpectedContents) {
      assertContains(tmpExpectedContent, tmpContent);
    }
  }

  private String getPaneTableRowContent(HtmlPage aHtmlPage, int aTable, int aRow) {
    // We have to get the content of the table row on another way because of an HtmlUnit bug
    HtmlTableBody tmpBody = (HtmlTableBody) aHtmlPage.getByXPath(
        "//table[@class='pane sortable'][" + ++aTable + "]//tbody").get(1);
    String tmpText = tmpBody.getRows().get(aRow).asText();
    return tmpText.replaceAll("\\s+", " ");
  }

  private void assertContains(String anExpectedString, String anActualString) {
    Assert.assertTrue("[" + anActualString + "] does not contain [" + anExpectedString + "]",
        anActualString.contains(anExpectedString));
  }

  public static class CopyBuilder extends TestBuilder {

    private String wetatorResultFile;
    private String wetatorReportFile;

    public CopyBuilder(String aWetatorResultFile, String aWetatorReportFile) {
      wetatorResultFile = aWetatorResultFile;
      wetatorReportFile = aWetatorReportFile;
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
      return new Descriptor<Builder>() {
        @Override
        public String getDisplayName() {
          return getClass().getName();
        }
      };
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> aBuild, Launcher aLauncher, BuildListener aListener)
        throws InterruptedException, IOException {
      aBuild.getWorkspace().child(WETATOR_RESULT_FILENAME).copyFrom(new FilePath(new File(wetatorResultFile)));
      if (StringUtils.isNotBlank(wetatorReportFile)) {
        aBuild.getWorkspace().child(WETATOR_REPORT_FILENAME).copyFrom(new FilePath(new File(wetatorReportFile)));
      }
      return true;
    }
  }
}