/*
 * Copyright (c) 2008-2021 wetator.org
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
import java.util.Properties;

import org.junit.Before;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifierTest {

  protected AbstractHtmlUnitControlIdentifier identifier;
  protected WetatorConfiguration config;

  /**
   * Creates a Wetator configuration.
   */
  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  protected WeightedControlList identify(final String aHtmlCode, final String anHtmlElementId, final WPath aWPath)
      throws IOException {
    return identify(aHtmlCode, aWPath, anHtmlElementId);
  }

  protected WeightedControlList identify(final String aHtmlCode, final WPath aWPath, final String... anHtmlElementIds)
      throws IOException {
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final WeightedControlList tmpControls = new WeightedControlList();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      identifier.initialize(tmpHtmlPageIndex);
      tmpControls.addAll(identifier.identify(aWPath, tmpHtmlElement));
    }
    return tmpControls;
  }
}