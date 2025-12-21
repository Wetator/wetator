/*
 * Copyright (c) 2008-2025 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
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
 * @author rbri
 */
@RunWith(BrowserRunner.class)
public class XmlEventHandlingTest extends AbstractWebServerTest {

  private static final String BASE_FOLDER = "src/test/resources/xml/";

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.FIREFOX, BrowserType.CHROME, BrowserType.EDGE })
  public void eventClickOn() throws InvalidInputException {
    executeTestFile("event_clickOn.wet");

    Assert.assertEquals(70, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.FIREFOX, BrowserType.CHROME, BrowserType.EDGE })
  public void eventDeselect() throws InvalidInputException {
    executeTestFile("event_deselect.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.FIREFOX, BrowserType.CHROME, BrowserType.EDGE })
  public void eventMouseOver() throws InvalidInputException {
    executeTestFile("event_mouseOver.wet");

    Assert.assertEquals(74, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.FIREFOX, BrowserType.CHROME, BrowserType.EDGE })
  public void eventSelect() throws InvalidInputException {
    executeTestFile("event_select.wet");

    Assert.assertEquals(18, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.CHROME, BrowserType.EDGE })
  public void eventSet() throws InvalidInputException {
    executeTestFile("event_set.wet");

    Assert.assertEquals(27, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.FIREFOX })
  public void eventSetFF() throws InvalidInputException {
    executeTestFile("ff/event_set.wet");

    Assert.assertEquals(27, getSteps());
    Assert.assertEquals(0, getFailures());
    Assert.assertEquals(0, getErrors());
  }

  private void executeTestFile(final String aTestFileName) throws InvalidInputException {
    executeTestFile(new File(BASE_FOLDER + aTestFileName));
  }
}
