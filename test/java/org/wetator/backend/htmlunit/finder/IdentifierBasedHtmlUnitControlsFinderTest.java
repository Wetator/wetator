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


package org.wetator.backend.htmlunit.finder;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class IdentifierBasedHtmlUnitControlsFinderTest {

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

  @Test
  public void empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("Name");

    final IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex,
        null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void hidden() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe' style='visibility: hidden;'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("ClickMe");

    final IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex,
        null);
    tmpFinder.addIdentifier(HtmlUnitInputSubmitIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void visible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("ClickMe");

    final IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex,
        null);
    tmpFinder.addIdentifier(HtmlUnitInputSubmitIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void label_byTextAndTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input type='checkbox' name='check' id='myCheckbox' title='checker title'>"
        + "<label for='myCheckbox'>checker</label>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex,
        null);
    tmpFinder.addIdentifier(HtmlUnitInputCheckBoxIdentifier.class);

    SecretString tmpSearch = new SecretString("check");
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));
    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='myCheckbox') (name='check')] found by: BY_NAME deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpSearch = new SecretString("checker title");
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));
    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='myCheckbox') (name='check')] found by: BY_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());

    tmpSearch = new SecretString("checker");
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='myCheckbox') (name='check')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
