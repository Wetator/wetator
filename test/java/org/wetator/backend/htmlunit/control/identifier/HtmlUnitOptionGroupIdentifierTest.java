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
public class HtmlUnitOptionGroupIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionGroupIdentifier();
  }

  @Test
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='selectId'>"
        + "<optgroup id='myId' label='group'>"
        + "</select>"
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
        + "<select id='selectId'>"
        + "<option id='myId'>option</option>"
        + "</select>"
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
        + "<select id='selectId'>"
        + "<optgroup id='myId' label='group'>"
        + "<option id='optionId'>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOptionGroup 'group' (id='myId') part of [HtmlSelect (id='selectId')]] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='selectId'>"
        + "<optgroup id='myId' label='group'>"
        + "<option id='optionId'>option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "group", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOptionGroup 'group' (id='myId') part of [HtmlSelect (id='selectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
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
        + "          <td id='cell_1_2'><select id='selectId_1_2'>"
        + "            <optgroup id='myId_1_2' label='group'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_1_3'><select id='selectId_1_3'>"
        + "            <optgroup id='myId_1_3' label='group'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><select id='selectId_2_2'>"
        + "            <optgroup id='myId_2_2' label='group'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "          <td id='cell_2_3'><select id='selectId_2_3'>"
        + "            <optgroup id='myId_2_3' label='group'>"
        + "            <option>option</option>"
        + "          </select></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > group", "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOptionGroup 'group' (id='myId_2_3') part of [HtmlSelect (id='selectId_2_3')]] found by: BY_LABEL deviation: 0 distance: 77 start: 77 hierarchy: 0>1>3>5>22>42>53>54>55 index: 55",
        tmpEntriesSorted.get(0).toString());
  }
}
