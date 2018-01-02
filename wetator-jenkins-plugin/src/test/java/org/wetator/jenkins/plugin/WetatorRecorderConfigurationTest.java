/*
 * Copyright (c) 2008-2018 wetator.org
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wetator.jenkins.WetatorRecorder;

import com.gargoylesoftware.htmlunit.html.HtmlForm;

import hudson.model.FreeStyleProject;

/**
 * Tests for the configuration page of the {@link WetatorRecorder}.
 *
 * @author frank.danek
 */
public class WetatorRecorderConfigurationTest extends AbstractPluginTest {

  @Test
  public void roundTrip() throws Exception {
    FreeStyleProject tmpProject = jenkins.createFreeStyleProject();
    WetatorRecorder tmpBefore = new WetatorRecorder("a", "b", "12", "23");
    tmpProject.getPublishersList().add(tmpBefore);

    jenkins.submit(webClient.getPage(tmpProject, "configure").getFormByName("config"));

    WetatorRecorder tmpAfter = tmpProject.getPublishersList().get(WetatorRecorder.class);

    jenkins.assertEqualBeans(tmpBefore, tmpAfter, "testResults,testReports,unstableThreshold,failureThreshold");
  }

  @Test
  public void unstableThresholdEmpty() throws Exception {
    FreeStyleProject tmpProject = jenkins.createFreeStyleProject();
    WetatorRecorder tmpBefore = new WetatorRecorder("a", "b", "12", "23");
    tmpProject.getPublishersList().add(tmpBefore);

    HtmlForm tmpConfigForm = webClient.getPage(tmpProject, "configure").getFormByName("config");
    tmpConfigForm.getInputByName("_.unstableThreshold").setValueAttribute("");
    jenkins.submit(tmpConfigForm);

    WetatorRecorder tmpAfter = tmpProject.getPublishersList().get(WetatorRecorder.class);

    assertEquals("0", tmpAfter.getUnstableThreshold());
  }
}
