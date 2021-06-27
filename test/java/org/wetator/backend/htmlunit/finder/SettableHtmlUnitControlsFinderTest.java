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


package org.wetator.backend.htmlunit.finder;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputFileIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputPasswordIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitTextAreaIdentifier;
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

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void firstInputFileOnPage() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='file' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlFileInput (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inputFile() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' type='file' value='SetMe'>"
        + "<input id='myId' type='file' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myId", HtmlUnitInputFileIdentifier.class);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlFileInput (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void firstInputPasswordOnPage() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='password' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlPasswordInput (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inputPassword() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' type='password' value='SetMe'>"
        + "<input id='myId' type='password' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myId", HtmlUnitInputPasswordIdentifier.class);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlPasswordInput (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 5 hierarchy: 0>1>3>4>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void firstInputTextOnPage() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='text' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inputText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' type='text' value='SetMe'>"
        + "<input id='myId' type='text' value='SetMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myId", HtmlUnitInputTextIdentifier.class);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextInput (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 5 hierarchy: 0>1>3>4>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void firstTextAreaOnPage() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea id='myId'>SetMe</textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextArea (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void textArea() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<textarea id='otherId'>SetMe</textarea>"
        + "<textarea id='myId'>SetMe</textarea>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "myId", HtmlUnitTextAreaIdentifier.class);

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlTextArea (id='myId')] found by: BY_ID deviation: 0 distance: 5 start: 5 hierarchy: 0>1>3>4>7 index: 7",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void visibilityHidden() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='text' value='SetMe' style='visibility: hidden;'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void displayNone() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='text' value='SetMe' style='display: none;'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = find(tmpHtmlCode, "");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @SafeVarargs
  private final List<Entry> find(final String aHtmlCode, final String aWPath,
      final Class<? extends AbstractHtmlUnitControlIdentifier>... aKnownIdentifiers)
      throws IOException, InvalidInputException {
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SettableHtmlUnitControlsFinder tmpFinder = new SettableHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    if (aKnownIdentifiers != null) {
      tmpFinder.addIdentifiers(Arrays.asList(aKnownIdentifiers));
    }
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(new SecretString(aWPath), config));

    return tmpFound.getEntriesSorted();
  }
}
