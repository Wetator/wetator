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
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.test.AbstractWebServerTest;
import org.wetator.test.junit.BrowserRunner;
import org.wetator.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class XmlEventHandlingTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/";

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6 })
  public void eventClickOnFF36() {
    executeTestFile("ff36/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_6 })
  public void eventClickOnIE6() {
    executeTestFile("ie6/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventClickOnIE8() {
    executeTestFile("ie8/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6 })
  public void eventDeselectFF36() {
    executeTestFile("ff36/event_deselect.wet");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_6 })
  public void eventDeselectIE6() {
    executeTestFile("ie6/event_deselect.wet");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventDeselectIE8() {
    executeTestFile("ie8/event_deselect.wet");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6, BrowserType.INTERNET_EXPLORER_6, BrowserType.INTERNET_EXPLORER_8 })
  public void eventHandlerOLD() {
    executeTestFile("event_handler.wet");

    Assert.assertEquals(72, getSteps());
    Assert.assertEquals(14, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_6 })
  public void eventHandlerOLDIE6() {
    executeTestFile("ie6/event_handler.wet");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(15, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6 })
  public void eventHandlerOLDFF36() {
    executeTestFile("ff36/event_handler.wet");

    Assert.assertEquals(81, getSteps());
    Assert.assertEquals(18, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6 })
  public void eventMouseOverFF36() {
    executeTestFile("ff36/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(5, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_6 })
  public void eventMouseOverIE6() {
    executeTestFile("ie6/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventMouseOverIE8() {
    executeTestFile("ie8/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(16, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_3_6 })
  public void eventSelectFF36() {
    executeTestFile("ff36/event_select.wet");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_6 })
  public void eventSelectIE6() {
    executeTestFile("ie6/event_select.wet");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventSelectIE8() {
    executeTestFile("ie8/event_select.wet");

    Assert.assertEquals(30, getSteps());
    Assert.assertEquals(4, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(String aTestFileName) {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
