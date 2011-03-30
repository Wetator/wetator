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


package org.wetator.commandset;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wetator.backend.WetBackend.Browser;
import org.wetator.test.AbstractWebServerTest;
import org.wetator.test.junit.BrowserRunner;
import org.wetator.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class XmlOtherTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/";

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void ajaxJquery() {
    executeTestFile("ajax_jquery.wet");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void ajaxPrototype() {
    executeTestFile("ajax_prototype.wet");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void jsAnchorInsidePage() {
    executeTestFile("js_anchor_inside_page.wet");

    Assert.assertEquals(8, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void flowSimpleLogin() {
    executeTestFile("flow_simple_login.wet");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void flowSimpleSearch() {
    executeTestFile("flow_simple_search.wet");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void htmlunitJavascript() {
    executeTestFile("htmlunit_javascript.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void iframe() {
    executeTestFile("iframe.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void jquery() {
    executeTestFile("jquery.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void jsError() {
    executeTestFile("js_error.wet");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void jsLibs() {
    executeTestFile("js_libs.wet");

    Assert.assertEquals(13, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void normalizeCommand() {
    executeTestFile("normalize_command.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void onfocusOnblur() {
    executeTestFile("onfocus_onblur.wet");

    Assert.assertEquals(56, getSteps());
    Assert.assertEquals(11, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void onkey() {
    executeTestFile("onkey.wet");

    Assert.assertEquals(65, getSteps());
    Assert.assertEquals(13, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void wait1() {
    executeTestFile("wait.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ Browser.FIREFOX_3_6, Browser.INTERNET_EXPLORER_6, Browser.INTERNET_EXPLORER_8 })
  public void responseStore() {
    executeTestFile("response_store.wet");

    Assert.assertEquals(14, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
