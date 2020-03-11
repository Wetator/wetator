/*
 * Copyright (c) 2008-2020 wetator.org
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
import org.wetator.backend.MouseAction;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public class MouseActionListeningHtmlUnitControlsFinderTest {

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

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_not() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_hidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"clicked\");' style='visibility: hidden;'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_click() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void listener_clickDouble() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' ondblclick='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_DOUBLE);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void listener_clickRight() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' oncontextmenu='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_RIGHT);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void listener_mouseOver() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseover='alert(\"clicked\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.MOUSE_OVER);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
