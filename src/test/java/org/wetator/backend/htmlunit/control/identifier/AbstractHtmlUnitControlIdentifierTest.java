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


package org.wetator.backend.htmlunit.control.identifier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifierTest {

  protected AbstractHtmlUnitControlIdentifier identifier;
  protected WetatorConfiguration config;
  protected WebClient webClient;

  /**
   * Creates a Wetator configuration.
   */
  @Before
  public void createWetatorConfigurationAndWebClient() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, new Properties(), null);

    webClient = new WebClient(BrowserVersion.FIREFOX_ESR);
  }

  /**
   * Closes the WebClient.
   */
  @After
  public void closeWebClient() {
    webClient.close();
  }

  protected boolean supported(final String aHtmlCode, final String anHtmlElementId) throws IOException {
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(anHtmlElementId);

    identifier.initialize(tmpHtmlPageIndex);
    return identifier.isHtmlElementSupported(tmpHtmlElement);
  }

  protected List<Entry> identify(final String aHtmlCode, final String aWPath, final String... anHtmlElementIds)
      throws IOException, InvalidInputException {
    final HtmlPage tmpHtmlPage = webClient.loadHtmlCodeIntoCurrentWindow(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final WeightedControlList tmpControls = new WeightedControlList();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      identifier.initialize(tmpHtmlPageIndex);
      tmpControls.addAll(identifier.identify(new WPath(new SecretString(aWPath), config), tmpHtmlElement));
    }
    return tmpControls.getEntriesSorted();
  }
}