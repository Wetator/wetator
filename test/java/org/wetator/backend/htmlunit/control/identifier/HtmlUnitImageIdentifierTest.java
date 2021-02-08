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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitImageIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myId");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ID deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' alt='MyAlt'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyAlt");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' title='MyTitle'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' aria-label='myAria'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myAria");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdNameAltTitleSrcAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='MyName' name='MyName' src='MyName' alt='MyName' title='MyName' aria-label='MyName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyName");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'MyName' (id='MyName') (name='MyName')] found by: BY_ID deviation: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<input id='otherId' name='otherName' type='submit'>"
        + "<p>Some text ...</p>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_TEXT deviation: 14 distance: 20 start: 20 index: 10",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byWholeTextBefore_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker</p>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "<p>Some text ...</p>"
        + "<img id='otherId' name='otherName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Marker > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId", "otherId");

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 index: 7",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='otherId') (name='otherName')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 index: 10",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void byTableCoordinates() throws IOException, InvalidInputException {
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
        + "          <td id='cell_1_2'><img id='myId_1_2' src='picture.png'></td>"
        + "          <td id='cell_1_3'><img id='myId_1_3' src='picture.png'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><img id='myId_2_2' src='picture.png'></td>"
        + "          <td id='cell_2_3'><img id='myId_2_3' src='picture.png'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 index: 45",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyAlt");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' aria-label='myAria'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myAria");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' alt='MyImageAlt'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageAlt");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byAriaLabelAttribute() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' aria-label='myAria'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myAria");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
