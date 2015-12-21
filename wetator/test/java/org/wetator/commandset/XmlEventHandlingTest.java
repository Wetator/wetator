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
public class XmlEventHandlingTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "test/xml/";

  @Test
  @Browsers({ BrowserType.FIREFOX_38 })
  public void eventClickOnFF38() throws InvalidInputException {
    executeTestFile("ff38/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_11 })
  public void eventClickOnIE11() throws InvalidInputException {
    executeTestFile("ie11/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_38 })
  public void eventDeselectFF38() throws InvalidInputException {
    executeTestFile("ff38/event_deselect.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_11 })
  public void eventDeselectIE1() throws InvalidInputException {
    executeTestFile("ie11/event_deselect.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_38 })
  public void eventMouseOverFF38() throws InvalidInputException {
    executeTestFile("ff38/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_11 })
  public void eventMouseOverIE11() throws InvalidInputException {
    executeTestFile("ie11/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_38 })
  public void eventSelectFF38() throws InvalidInputException {
    executeTestFile("ff38/event_select.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_11 })
  public void eventSelectIE11() throws InvalidInputException {
    executeTestFile("ie11/event_select.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_38 })
  public void eventSetFF38() throws InvalidInputException {
    executeTestFile("ff38/event_set.wet");

    Assert.assertEquals(20, getSteps());
    Assert.assertEquals(2, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(final String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
