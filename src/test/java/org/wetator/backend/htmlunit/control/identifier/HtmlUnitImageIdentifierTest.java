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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.exception.InvalidInputException;

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
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<div id='myId'>picture.png</div>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byDataTestid() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' data-testid='myDataTestId' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myDataTestId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName') (data-testid='myDataTestId')] found by: BY_DATE_TESTID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png' alt='MyAlt'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyAlt", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png' title='MyTitle'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyTitle", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "picture.png", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='myName' src='picture.png' aria-label='myAria'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myAria", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byIdNameAltTitleSrcAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myName' name='myName' src='myName' alt='myName' title='myName' aria-label='myName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName", "myName");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'myName' (id='myName') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeAsText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='otherId1' name='otherName1' src='picture.png'>"
        + "<p>Marker</p>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "<p>Some text ...</p>"
        + "<img id='otherId2' name='otherName2' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker", "myId", "otherId1", "otherId2");

    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>8 index: 8",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlImage 'picture.png' (id='otherId2') (name='otherName2')] found by: BY_TEXT deviation: 14 distance: 0 start: 20 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(1).toString());
  }

  @Test
  public void byLabelingTextBeforeAsText_wildcardOnly() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='otherId1' name='otherName1' src='picture.png'>"
        + "<p>Marker</p>"
        + "<img id='myId' name='myName' src='picture.png'>"
        + "<p>Some text ...</p>"
        + "<img id='otherId2' name='otherName2' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Marker > ", "myId", "otherId1", "otherId2");

    assertEquals(2, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='myName')] found by: BY_TEXT deviation: 0 distance: 0 start: 6 hierarchy: 0>1>3>4>8 index: 8",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlImage 'picture.png' (id='otherId2') (name='otherName2')] found by: BY_TEXT deviation: 0 distance: 14 start: 20 hierarchy: 0>1>3>4>11 index: 11",
        tmpEntriesSorted.get(1).toString());
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

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2]", "myId_1_2", "myId_1_3", "myId_2_2",
        "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTable() throws IOException, InvalidInputException {
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

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > picture.png", "myId_1_2",
        "myId_1_3", "myId_2_2", "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myId_2_3')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inAnchor_byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='myName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myImageName", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inAnchor_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='myName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName' alt='MyAlt'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyAlt", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inAnchor_byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='myName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyImageTitle", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inAnchor_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='myName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName' title='MyTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "picture.png", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inAnchor_byAriaLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='myName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName' aria-label='myAria'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myAria", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inButton_byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='myName'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myImageName", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inButton_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='myName'>"
        + "<img id='myInnerId' src='picture.png' alt='MyImageAlt'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyImageAlt", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inButton_byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='myName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyImageTitle", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inButton_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='myName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "picture.png", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inButton_byAriaLabelAttribute() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='myName'>"
        + "<img id='myInnerId' src='picture.png' name='myImageName' aria-label='myAria'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myAria", "myInnerId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='myImageName')] found by: BY_ARIA_LABEL_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }
}
