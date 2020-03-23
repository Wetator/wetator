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
public class HtmlUnitInputImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputImageIdentifier();
  }

  @Test
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='image' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='text' value='ClickMe'>"
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
        + "<input id='myId' type='image' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='image' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='myName')] found by: BY_NAME deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='image' name='myName' src='picture.png' alt='MyAlt'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyAlt", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='myName')] found by: BY_IMG_ALT_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='image' name='myName' src='picture.png' title='MyTitle'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "MyTitle", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='myName')] found by: BY_IMG_TITLE_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='image' name='myName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "picture.png", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='myName')] found by: BY_IMG_SRC_ATTRIBUTE deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byIdNameAltTitleSrc() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myName' type='image' name='myName' src='myName' alt='myName' title='myName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myName", "myName");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='myName') (id='myName') (name='myName')] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5 index: 5",
        tmpEntriesSorted.get(0).toString());
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
        + "          <td id='cell_1_2'><input id='myId_1_2' type='image' src='picture.png'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' type='image' src='picture.png'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' type='image' src='picture.png'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' type='image' src='picture.png'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2]", "myId_1_2", "myId_1_3", "myId_2_2",
        "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId_2_3')] found by: BY_TABLE_COORDINATE deviation: 0 distance: 38 start: 38 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }
}
