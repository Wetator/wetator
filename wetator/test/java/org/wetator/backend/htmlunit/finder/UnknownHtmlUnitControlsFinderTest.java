/*
 * Copyright (c) 2008-2016 wetator.org
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 0 distance: 0 start: 0 index: 3",
            tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 2 distance: 0 start: 0 index: 3",
            tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 0 distance: 2 start: 0 index: 3",
            tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 1 distance: 1 start: 0 index: 3",
            tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 2 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 2 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 1 distance: 1 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 4", tmpFound
        .getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 4", tmpFound
        .getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlParagraph 'MyText' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 4", tmpFound
        .getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlParagraph 'MyText' (id='tester')] found by: BY_TITLE_TEXT coverage: 0 distance: 0 start: 0 index: 4",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlLabel'] found by: BY_TEXT coverage: 0 distance: 0 start: 0 index: 5",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput' (id='myCheckbox') (name='checkbox')] found by: BY_TITLE_TEXT coverage: 0 distance: 0 start: 0 index: 4",
            tmpFound.getEntriesSorted().get(1).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 8 start: 12 index: 11",
        tmpFound.getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 0 start: 5 index: 6", tmpFound
        .getEntriesSorted().get(0).toString());
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

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
