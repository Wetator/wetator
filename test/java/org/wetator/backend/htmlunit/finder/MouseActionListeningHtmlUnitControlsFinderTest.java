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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.MouseAction;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
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
  private HtmlUnitControlRepository repository;

  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  @Before
  public void createControlRepository() {
    repository = new HtmlUnitControlRepository();
    repository.add(HtmlUnitAnchor.class);
    repository.add(HtmlUnitInputText.class);
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
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(), config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void clickable() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId'>some text</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlAnchor 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void clickable_withListener() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId' onclick='alert(\"test\");'>some text</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlAnchor 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_not() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_knownControl_hidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' onclick='alert(\"test\");' style='visibility: hidden;' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_knownControl_click() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' onclick='alert(\"test\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_clickDouble() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' ondblclick='alert(\"test\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_DOUBLE, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_clickRight() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' oncontextmenu='alert(\"test\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_RIGHT, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_mouseOver() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "some text <input type='text' id='myId' onmouseover='alert(\"test\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.MOUSE_OVER, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABELING_TEXT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_withLabel() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label for='myId'>some text</label><input type='text' id='myId' onclick='alert(\"clicked\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_knownControl_withLabelHavingListener() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label for='myId' onclick='alert(\"clicked\");'>some text</label><input type='text' id='myId' onclick='alert(\"clicked\");' />"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlLabel 'some text' (for='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_LABEL_ELEMENT deviation: 0 distance: 0 start: 9 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void listener_unknownControl_not() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_unknownControl_hidden() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"test\");' style='visibility: hidden;'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void listener_unknownControl_click() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onclick='alert(\"test\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_unknownControl_clickDouble() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' ondblclick='alert(\"test\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_DOUBLE, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_unknownControl_clickRight() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' oncontextmenu='alert(\"test\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_RIGHT, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_unknownControl_mouseOver() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<span id='myId' onmouseover='alert(\"test\");'>some text</span>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.MOUSE_OVER, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void listener_unknownControl_anchorChild() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='otherId' href='#'><span id='myId'>some text</span></a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlAnchor 'some text' (id='otherId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void listener_unknownControl_buttonChild() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<button id='otherId'><span id='myId'>some text</button></a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    tmpFinder.addIdentifier(HtmlUnitButtonIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlButton 'some text' (id='otherId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void listener_unknownControl_label() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<label id='myId' onclick='alert(\"clicked\");'>some text</label>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("some text"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlLabel 'some text' (id='myId')] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void page_click() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='myId'>"
        + "<a id='otherId'>$PAGE</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("$PAGE"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void page_clickDouble() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='myId'>"
        + "<a id='otherId'>$PAGE</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_DOUBLE, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("$PAGE"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void page_clickRight() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='myId'>"
        + "<a id='otherId'>$PAGE</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.CLICK_RIGHT, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("$PAGE"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void page_mouseOver() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = "<html><body id='myId'>"
        + "<a id='otherId'>$PAGE</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final MouseActionListeningHtmlUnitControlsFinder tmpFinder = new MouseActionListeningHtmlUnitControlsFinder(
        tmpHtmlPageIndex, null, MouseAction.MOUSE_OVER, repository);
    tmpFinder.addIdentifier(HtmlUnitAnchorIdentifier.class);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString("$PAGE"), config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals("[HtmlBody (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }
}
