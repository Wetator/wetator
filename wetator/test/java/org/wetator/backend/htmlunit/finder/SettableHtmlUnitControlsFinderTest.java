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


package org.wetator.backend.htmlunit.finder;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
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
public class SettableHtmlUnitControlsFinderTest {

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
    final String tmpHtmlCode = "<html><body>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void hidden() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='text' value='SetMe' style='visibility: hidden;'>" + "</form>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void firstInputFileOnPage() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='file' value='SetMe'>"
        + "</form>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlFileInput (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void firstInputPasswordOnPage() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='password' value='SetMe'>" + "</form>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlPasswordInput (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void firstInputTextOnPage() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='text' value='SetMe'>"
        + "</form>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextInput (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void firstTextAreaOnPage() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<textarea id='myId'>SetMe</textarea>"
        + "</form>" + "</body></html>";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlTextArea (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
