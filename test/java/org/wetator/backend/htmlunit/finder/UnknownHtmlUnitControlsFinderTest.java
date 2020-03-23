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
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class UnknownHtmlUnitControlsFinderTest {

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
  public void noHtml() throws IOException, InvalidInputException {
    final String tmpHtmlCode = "";
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("Name");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void noBody() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html>"
        + "<head><title>MyTitle</title></head>"
        + "</html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("Name");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
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

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void text_byText_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "MyText"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("YourText");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void text_byText_exact() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "MyText"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyText");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void text_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "MyText"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyTe*");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT deviation: 2 distance: 0 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void text_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "MyText"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("*Text");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT deviation: 0 distance: 2 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void text_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "MyText"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("yTex");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT deviation: 1 distance: 1 start: 0 hierarchy: 0>1>3 index: 3",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byText_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("YourText");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void paragraph_byText_exact() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyText");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText'] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyTe*");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText'] found by: BY_TEXT deviation: 2 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("*Text");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText'] found by: BY_TEXT deviation: 0 distance: 2 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("yTex");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText'] found by: BY_TEXT deviation: 1 distance: 1 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byText_formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>My<b>T</b>ext</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyText");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText'] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byId_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId'>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("yourId");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void paragraph_byId_exact() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId'>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("myId");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byId_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId'>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("my*");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byId_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='myId'>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("*Id");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void paragraph_byId_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("yI");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void paragraph_byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p id='tester' title='my title'>MyText</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("my title");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'MyText' (id='tester')] found by: BY_TITLE_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void label_byTextAndTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<input type='checkbox' name='checkbox' id='myCheckbox' title='Checker'>"
        + "<label for='myCheckbox'>Checker</label>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("Checker");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlLabel 'Checker' (for='myCheckbox')] found by: BY_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>5 index: 5",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlCheckBoxInput (id='myCheckbox') (name='checkbox')] found by: BY_TITLE_TEXT deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4 index: 4",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void manyParagraphs() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>My<b>T</b>ext</p>"
        + "<p>line2</p>"
        + "<p>line3</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyText > ine3");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'line3'] found by: BY_TEXT deviation: 0 distance: 8 start: 12 hierarchy: 0>1>3>11 index: 11",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void manyParagraphs_matchInside() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>line2</p>"
        + "<p>line3</p>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("line2 li > ne3");

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlParagraph 'line3'] found by: BY_TEXT deviation: 0 distance: 0 start: 5 hierarchy: 0>1>3>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void ignoringElementForControl() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a>MyText</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final SecretString tmpSearch = new SecretString("MyText");

    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
    tmpRepository.add(HtmlUnitAnchor.class);

    final UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    final WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTablePlainById() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span id='id_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span id='id_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span id='id_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span id='id_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_3; row_2] > id_2_3
    SecretString tmpSearch = new SecretString("[header_3; row_2] > id_2_3");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3' (id='id_2_3')] found by: BY_ID deviation: 0 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    // [header_2; row_2] > id_2_3
    tmpSearch = new SecretString("[header_2; row_2] > id_2_3");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTablePlainByTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span title='title_1_2'>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span title='title_1_3'>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span title='title_2_2'>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span title='title_2_3'>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_3; row_2] > title_2_3
    SecretString tmpSearch = new SecretString("[header_3; row_2] > title_2_3");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TITLE_TEXT deviation: 0 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    // [header_2; row_2] > title_2_3
    tmpSearch = new SecretString("[header_2; row_2] > title_2_3");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTablePlainByText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_3; row_2] > Text_2_3
    SecretString tmpSearch = new SecretString("[header_3; row_2] > Text_2_3");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 0 distance: 66 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    // [header_2; row_2] > Text_2_3
    tmpSearch = new SecretString("[header_2; row_2] > Text_2_3");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTablePlainByCoordinates() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_3; row_2]
    SecretString tmpSearch = new SecretString("[header_3; row_2]");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 8 distance: 65 start: 65 hierarchy: 0>1>3>5>22>38>47>48 index: 48",
        tmpEntriesSorted.get(0).toString());

    // [header_2; row_1] > Text_2_3
    tmpSearch = new SecretString("[header_2; row_1]");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_1_2'] found by: BY_TEXT deviation: 8 distance: 32 start: 32 hierarchy: 0>1>3>5>22>24>29>30 index: 30",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTablePlainByCoordinatesHorizontalOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [; row_2]
    SecretString tmpSearch = new SecretString("[; row_2]");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlTableDataCell' (id='cell_2_1')] found by: BY_TEXT deviation: 5 distance: 50 start: 50 hierarchy: 0>1>3>5>22>39>41 index: 41",
        tmpEntriesSorted.get(0).toString());

    // [; row_1]
    tmpSearch = new SecretString("[; row_1]");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'row_1'] found by: BY_TEXT deviation: 5 distance: 26 start: 26 hierarchy: 0>1>3>5>22>24>26>27 index: 27",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTablePlainByCoordinatesHorizontalOnlyTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [; row_2] > Text_2_2 >
    SecretString tmpSearch = new SecretString("[; row_2] > Text_2_2 >");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    final List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_3'] found by: BY_TEXT deviation: 8 distance: 0 start: 65 hierarchy: 0>1>3>5>22>39>48>49 index: 49",
        tmpEntriesSorted.get(0).toString());

    // [; row_1] > Text_1_3 >
    tmpSearch = new SecretString("[; row_1] > Text_1_3 >");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void inTablePlainByCoordinatesVerticalOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_1;]
    SecretString tmpSearch = new SecretString("[header_1;]");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlTableHeaderCell' (id='header_1')] found by: BY_TEXT deviation: 8 distance: 0 start: 0 hierarchy: 0>1>3>5>7>9>11 index: 11",
        tmpEntriesSorted.get(0).toString());

    // [header_3;]
    tmpSearch = new SecretString("[header_3;]");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlTableHeaderCell' (id='header_3')] found by: BY_TEXT deviation: 8 distance: 17 start: 17 hierarchy: 0>1>3>5>7>9>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTablePlainByCoordinatesVerticalOnlyTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><span>row_1</span></td>"
        + "          <td id='cell_1_2'><span>Text_1_2</span></td>"
        + "          <td id='cell_1_3'><span>Text_1_3</span></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'><span>Text_2_1</span></td>"
        + "          <td id='cell_2_2'><span>Text_2_2</span></td>"
        + "          <td id='cell_2_3'><span>Text_2_3</span></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);
    final HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();

    // [header_1;] > Text_1_2
    SecretString tmpSearch = new SecretString("[header_1;] > Text_1_2 >");

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    List<Entry> tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_2_1'] found by: BY_TEXT deviation: 8 distance: 9 start: 50 hierarchy: 0>1>3>5>22>39>41>42 index: 42",
        tmpEntriesSorted.get(0).toString());

    // [header_2;] > row_1 >
    tmpSearch = new SecretString("[header_2;] > row_1 >");

    tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    tmpFound = tmpFinder.find(new WPath(tmpSearch, config));

    tmpEntriesSorted = tmpFound.getEntriesSorted();
    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlSpan 'Text_1_2'] found by: BY_TEXT deviation: 8 distance: 0 start: 32 hierarchy: 0>1>3>5>22>24>30>31 index: 31",
        tmpEntriesSorted.get(0).toString());
  }
}
