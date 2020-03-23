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
        + "<select id='selectId' size='2'>"
        + "<optgroup label='colors' id='myId'>"
        + "</select>"
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
        + "<select id='selectId' size='2'>"
        + "<option id='myId' value='o_red'>red</option>"
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
        + "<select id='selectId' size='2'>"
        + "<optgroup label='colors' id='myId'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myId", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOptionGroup 'colors' (id='myId') part of [HtmlSelect (id='selectId')]] found by: BY_ID deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void byLabel() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='selectId' size='2'>"
        + "<optgroup label='colors' id='myId'>"
        + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>"
        + "<option value='o_blue'>blue</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "colors", "myId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOptionGroup 'colors' (id='myId') part of [HtmlSelect (id='selectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 0 hierarchy: 0>1>3>4>5>6 index: 6",
        tmpEntriesSorted.get(0).toString());
  }
}
