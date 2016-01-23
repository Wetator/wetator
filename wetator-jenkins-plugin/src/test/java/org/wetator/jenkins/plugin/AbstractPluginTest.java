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


package org.wetator.jenkins.plugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;
import org.wetator.jenkins.test.ResultXMLBuilder;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.Builder;

/**
 * Abstract base test for all tests testing the plugin running within Jenkins.
 *
 * @author frank.danek
 */
public abstract class AbstractPluginTest extends HudsonTestCase {

  private static final String NL = System.lineSeparator();

  protected static final String WETATOR_RESULT_FILENAME = "wetresult.xml";
  protected static final String WETATOR_REPORT_FILENAME = "run_report.xsl.html";
  protected static final String WETATOR_RESULT_PATH = "src/test/resources/org/wetator/jenkins/wetresult/";

  protected ResultXMLBuilder builder;

  private FreeStyleProject project;
  private FreeStyleBuild build;

  protected WebClient webClient;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    builder = new ResultXMLBuilder();

    webClient = createWebClient();
  }

  protected void runBuild() throws Exception {
    runBuild((String) null);
  }

  protected void runBuild(String aWetatorReportFile) throws Exception {
    normalizeResultFile();

    project = createProject(ResultXMLBuilder.RESULT_LOG, aWetatorReportFile);
    build = runBuild(project);
  }

  private void normalizeResultFile() throws Exception {
    FileUtils.write(new File(ResultXMLBuilder.RESULT_LOG), builder.getNormalizedResult(), "utf-8");
  }

  private FreeStyleProject createProject(String aWetatorResultFile, String aWetatorReportFile) throws Exception {
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

  private FreeStyleBuild runBuild(FreeStyleProject aProject) throws InterruptedException, ExecutionException {
    FreeStyleBuild tmpBuild = aProject.scheduleBuild2(0).get();
    System.out.println("Project '" + aProject.getName() + "' Build '" + tmpBuild.getDisplayName() + "' completed.");
    return tmpBuild;
  }

  protected String normalizeAbsoluteTestFileName() {
    return normalizeAbsoluteTestFileName(builder.getAbsoluteTestFileName());
  }

  protected String normalizeAbsoluteTestFileName(String anAbsoluteTestFileName) {
    String tmpName = anAbsoluteTestFileName;
    tmpName = tmpName.replace('\\', '/');
    return tmpName.replaceFirst("^[^/]*", "");
  }

  protected HtmlPage openReportPage() throws IOException, SAXException {
    return webClient.getPage(project, build.getNumber() + "/wetatorReport");
  }

  protected HtmlPage openTestPage(HtmlPage aReportPage) throws IOException {
    return openTestPage(aReportPage, builder.getTestFileName());
  }

  protected HtmlPage openTestPage(HtmlPage aReportPage, String aTestFileName) throws IOException {
    return aReportPage.getAnchorByText(aTestFileName).click();
  }

  protected HtmlPage openBrowserPage(HtmlPage aTestPage, String aBrowser) throws IOException {
    return aTestPage.getAnchorByText(aBrowser).click();
  }

  protected void assertBuild(Result anExpectedResult, int anExpectedScore, int anExpectedFailCount,
      int anExpectedSkipCount, int anExceptedTotalCount) {
    assertBuild(anExpectedResult, anExpectedScore, anExpectedFailCount, anExpectedSkipCount, anExceptedTotalCount,
        build);
  }

  private void assertBuild(Result anExpectedResult, int anExpectedScore, int anExpectedFailCount,
      int anExpectedSkipCount, int anExceptedTotalCount, FreeStyleBuild anActualBuild) {
    assertEquals(anExpectedResult, anActualBuild.getResult());

    assertEquals(anExpectedScore, anActualBuild.getProject().getBuildHealth().getScore());
    assertEquals(
        "Wetator Test Result: " + anExpectedFailCount + " test" + (anExpectedFailCount == 1 ? "" : "s")
            + " failing out of a total of " + anExceptedTotalCount + " test" + (anExceptedTotalCount == 1 ? "" : "s")
            + " (" + anExpectedSkipCount + " test" + (anExpectedSkipCount == 1 ? "" : "s") + " skipped).",
        anActualBuild.getProject().getBuildHealth().getDescription());
  }

  protected void assertProjectAndBuildPage(String anExpectedMessage) throws IOException, SAXException {
    assertProjectAndBuildPage(anExpectedMessage, build);
  }

  private void assertProjectAndBuildPage(String anExpectedMessage, FreeStyleBuild anActualBuild)
      throws IOException, SAXException {
    HtmlPage tmpProjectPage = webClient.getPage(anActualBuild.getProject());
    WebAssert.assertTextPresent(tmpProjectPage, "Last Wetator Report");

    HtmlPage tmpBuildPage = webClient.getPage(anActualBuild.getProject(), "" + anActualBuild.getNumber());
    WebAssert.assertTextPresent(tmpBuildPage, "Wetator Test Result (" + anExpectedMessage + ")");
  }

  protected void assertReportPage(int anExpectedFailCount, int anExpectedSkipCount, int anExpectedTotalCount,
      HtmlPage anActualPage) {
    assertReportPage(anExpectedFailCount, anExpectedSkipCount, anExpectedTotalCount, "0.77 sec", anActualPage);
  }

  private void assertReportPage(int anExpectedFailCount, int anExpectedSkipCount, int anExpectedTotalCount,
      String anExpectedDuration, HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, "Wetator Test Result");
    if (anExpectedSkipCount == 0) {
      WebAssert.assertTextPresent(anActualPage,
          NL + anExpectedFailCount + " failure" + (anExpectedFailCount == 1 ? "" : "s"));
    } else {
      WebAssert.assertTextPresent(anActualPage, NL + anExpectedFailCount + " failure"
          + (anExpectedFailCount == 1 ? "" : "s") + " , " + anExpectedSkipCount + " skipped");
    }
    WebAssert.assertTextPresent(anActualPage,
        NL + anExpectedTotalCount + " test" + (anExpectedTotalCount == 1 ? "" : "s"));
    WebAssert.assertTextPresent(anActualPage, "Took " + anExpectedDuration + ".");
    if (anExpectedFailCount == 0) {
      WebAssert.assertTextNotPresent(anActualPage, "All Failed Tests");
    } else {
      WebAssert.assertTextPresent(anActualPage, "All Failed Tests");
    }
  }

  protected void assertTestPage(String anExpectedDuration, HtmlPage anActualPage) {
    assertTestPage(builder.getTestFileName(), normalizeAbsoluteTestFileName(), anExpectedDuration, anActualPage);
  }

  protected void assertTestPage(String anExpectedFile, String anExpectedAbsoluteFile, String anExpectedDuration,
      HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, anExpectedFile);
    WebAssert.assertTextPresent(anActualPage, "\t" + anExpectedFile + anExpectedAbsoluteFile + NL);
    WebAssert.assertTextPresent(anActualPage, "Took " + anExpectedDuration + ".");
  }

  protected void assertBrowserPage(String anExpectedBrowser, String anExpectedResult, String anExpectedDuration,
      HtmlPage anActualPage) {
    assertBrowserPage(anExpectedBrowser, anExpectedResult, builder.getTestFileName(), normalizeAbsoluteTestFileName(),
        anExpectedDuration, anActualPage);
  }

  protected void assertBrowserPage(String anExpectedBrowser, String anExpectedResult, String anExpectedFile,
      String anExpectedAbsoluteFile, String anExpectedDuration, HtmlPage anActualPage) {
    WebAssert.assertTitleContains(anActualPage, anExpectedBrowser);
    WebAssert.assertTextPresent(anActualPage, "\t" + anExpectedResult + NL);
    WebAssert.assertTextPresent(anActualPage,
        NL + anExpectedFile + "[" + anExpectedBrowser + "] from " + anExpectedAbsoluteFile + NL);
    WebAssert.assertTextPresent(anActualPage, "Took " + anExpectedDuration + ".");
  }

  protected void assertBrowserError(String anExpectedCause, String anExpectedCommandLine, String anExpectedMessage,
      HtmlPage anActualPage) {
    assertBrowserError(normalizeAbsoluteTestFileName(), anExpectedCause, anExpectedCommandLine, anExpectedMessage,
        anActualPage);
  }

  protected void assertBrowserError(String anExpectedAbsoluteFile, String anExpectedCause, String anExpectedCommandLine,
      String anExpectedMessage, HtmlPage anActualPage) {
    WebAssert.assertTextPresent(anActualPage, "Failing for the past 1 build (Since #1 )");
    WebAssert.assertTextPresent(anActualPage, "Error Message");
    WebAssert.assertTextPresent(anActualPage, "File: " + anExpectedAbsoluteFile + NL);
    WebAssert.assertTextPresent(anActualPage, "Cause: " + anExpectedCause + NL);
    assertPaneTableRowContains(anActualPage, 0, anExpectedCommandLine);
    assertPaneTableRowContains(anActualPage, 1, anExpectedMessage);
  }

  protected void assertBrowserError(String anExpectedMessage, HtmlPage anActualPage) {
    assertBrowserError(normalizeAbsoluteTestFileName(), anExpectedMessage, anActualPage);
  }

  protected void assertBrowserError(String anExpectedAbsoluteFile, String anExpectedMessage, HtmlPage anActualPage) {
    WebAssert.assertTextPresent(anActualPage, "Failing for the past 1 build (Since #1 )");
    WebAssert.assertTextPresent(anActualPage, "Error Message");
    WebAssert.assertTextPresent(anActualPage, "File: " + anExpectedAbsoluteFile + NL);
    WebAssert.assertTextPresent(anActualPage, NL + anExpectedMessage + NL);
  }

  protected void assertBrowserSkipped(HtmlPage anActualPage) {
    WebAssert.assertTextPresent(anActualPage, "Skipped for the past 1 build (Since #1 )");
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
    HtmlTableBody tmpBody = (HtmlTableBody) aHtmlPage
        .getByXPath("//table[@class='pane sortable'][" + ++aTable + "]//tbody").get(1);
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