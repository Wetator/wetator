/*
 * Copyright (c) 2008-2025 wetator.org
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
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.InvalidInputException;
import org.wetator.test.AbstractWebServerTest;
import org.wetator.test.junit.BrowserRunner;
import org.wetator.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class XmlOtherTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "src/test/resources/xml/";

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void ajaxJquery() throws InvalidInputException {
    executeTestFile("ajax_jquery.wet");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void ajaxPrototype() throws InvalidInputException {
    executeTestFile("ajax_prototype.wet");

    Assert.assertEquals(10, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void jsAnchorInsidePage() throws InvalidInputException {
    executeTestFile("js_anchor_inside_page.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void flowSimpleLogin() throws InvalidInputException {
    executeTestFile("flow_simple_login.wet");

    Assert.assertEquals(7, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void flowSimpleSearch() throws InvalidInputException {
    executeTestFile("flow_simple_search.wet");

    Assert.assertEquals(12, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void htmlunitJavascript() throws InvalidInputException {
    executeTestFile("htmlunit_javascript.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void iframe() throws InvalidInputException {
    executeTestFile("iframe.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void jquery() throws InvalidInputException {
    executeTestFile("jquery.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void richfaces() throws InvalidInputException {
    executeTestFile("richfaces.wet");

    Assert.assertEquals(36, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void jsError() throws InvalidInputException {
    executeTestFile("js_error.wet");

    Assert.assertEquals(34, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void jsLibs() throws InvalidInputException {
    executeTestFile("js_libs.wet");

    Assert.assertEquals(13, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void normalizeCommand() throws InvalidInputException {
    executeTestFile("normalize_command.wet");

    Assert.assertEquals(6, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusOnblur() throws InvalidInputException {
    executeTestFile("onfocus_onblur.wet");

    Assert.assertEquals(56, getSteps());
    Assert.assertEquals(11, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusChangeFocus() throws InvalidInputException {
    executeTestFile("onfocus_changefocus.wet");

    Assert.assertEquals(24, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusDropFocusText() throws InvalidInputException {
    executeTestFile("onfocus_dropfocus_text.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusDropFocusPassword() throws InvalidInputException {
    executeTestFile("onfocus_dropfocus_password.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusDropFocusTextArea() throws InvalidInputException {
    executeTestFile("onfocus_dropfocus_textarea.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onfocusDropFocusFileUpload() throws InvalidInputException {
    executeTestFile("onfocus_dropfocus_fileupload.wet");

    Assert.assertEquals(9, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(1, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void onkey() throws InvalidInputException {
    executeTestFile("onkey.wet");

    Assert.assertEquals(65, getSteps());
    Assert.assertEquals(13, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void wait1() throws InvalidInputException {
    executeTestFile("wait.wet");

    Assert.assertEquals(4, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void responseStore() throws InvalidInputException {
    executeTestFile("response_store.wet");

    Assert.assertEquals(14, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(final String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
