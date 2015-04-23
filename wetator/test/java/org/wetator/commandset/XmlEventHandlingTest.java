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
  @Browsers({ BrowserType.FIREFOX_24 })
  public void eventClickOnFF24() throws InvalidInputException {
    executeTestFile("ff24/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventClickOnIE8() throws InvalidInputException {
    executeTestFile("ie8/event_clickOn.wet");

    Assert.assertEquals(63, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_24 })
  public void eventDeselectFF24() throws InvalidInputException {
    executeTestFile("ff24/event_deselect.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventDeselectIE8() throws InvalidInputException {
    executeTestFile("ie8/event_deselect.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_24 })
  public void eventMouseOverFF24() throws InvalidInputException {
    executeTestFile("ff24/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventMouseOverIE8() throws InvalidInputException {
    executeTestFile("ie8/event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_24 })
  public void eventSelectFF24() throws InvalidInputException {
    executeTestFile("ff24/event_select.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.INTERNET_EXPLORER_8 })
  public void eventSelectIE8() throws InvalidInputException {
    executeTestFile("ie8/event_select.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(final String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
