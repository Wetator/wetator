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
 * As the {@link HtmlUnitOptionIdentifier} does not use pretested
 * {@link org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher}s we
 * have to test everything here.
 *
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitOptionIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionIdentifier();
  }

  @Test
  public void isHtmlElementSupported() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "myOptionId2_3"));
  }

  @Test
  public void isHtmlElementSupported_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='mySelectId' size='2'>"
        + "<optgroup label='colors' id='myId'>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "myId"));
  }

  @Test
  public void isHtmlElementSupported_HtmlLabel() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='mySelectId'>LabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertTrue(supported(tmpHtmlCode, "labelId"));
  }

  @Test
  public void isHtmlElementSupported_HtmlLabel_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>LabelText</label>"
        + "<input id='myId' value='value' type='text'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    assertFalse(supported(tmpHtmlCode, "labelId"));
  }

  @Test
  public void select_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "not > myText3", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byId_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > myText3", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingTe* > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*ySelectId > myText3", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectId > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byId_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > mySelectId > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > mySelectI* > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > *ySelectId > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byId_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > ySecondSelectI > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byId_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectId > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byId_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectId > myText3", "myOptionId2_1",
        "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byName_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectName > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectNa* > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*ySelectName > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "SecondSelectName > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byName_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > mySelectName > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > mySelectNa* > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > *ySelectName > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byName_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > ySecondSelectNam > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byName_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectName > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byName_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectName > myText3", "myOptionId2_1",
        "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byLabelingTextBefore_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelingTe* > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*ySelectLabelingText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "ySelectLabelingTex > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "otherSelectLabelingText > mySelectLabelingText > myText3", "myOptionId1_1", "myOptionId1_2", "myOptionId1_3",
        "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "otherSelectLabelingText > mySelectLabelingTe* > myText3", "myOptionId1_1", "myOptionId1_2", "myOptionId1_3",
        "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "otherSelectLabelingText > *ySelectLabelingText > myText3", "myOptionId1_1", "myOptionId1_2", "myOptionId1_3",
        "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > ySelectLabelingTex > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byLabelingTextBefore_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelingText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byLabelingTextBefore_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelingText > myText3",
        "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabel_text_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelTe* > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*ySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "ySelectLabelTex > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > mySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > mySelectLabelTe* > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > *ySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > ySelectLabelTex > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 81 hierarchy: 0>1>3>4>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabel_text_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabel_text_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelText > myText3", "labelId",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabel_text_full_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2' style='display: none;'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabelChild_text_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelTe* > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*ySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "ySelectLabelTex > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_full_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > mySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_wildcardRight_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > mySelectLabelTe* > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_wildcardLeft_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > *ySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 0 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_part_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "otherSelectLabelingText > ySelectLabelTex > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABEL deviation: 0 distance: 1 start: 81 hierarchy: 0>1>3>4>13>15>20 index: 20",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void select_byHtmlLabelChild_text_full_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelText > myText3",
        "myOptionId1_1", "myOptionId1_2", "myOptionId1_3", "labelId", "myOptionId2_1", "myOptionId2_2",
        "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabelChild_text_full_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > mySelectLabelText > myText3", "labelId",
        "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void select_byHtmlLabelChild_text_full_invisible() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "<label id='labelId'>mySelectLabelText"
        + "<select id='mySelectId' size='2' style='display: none;'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectLabelText > myText3", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "labelId");

    assertEquals(0, tmpEntriesSorted.size());
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
        + "          <td id='cell_1_2'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_1_2' name='mySelectName_1_2' size='2'>"
        + "              <option id='myOptionId1_2_1' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId1_2_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_1_3'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_1_3' name='mySelectName_1_3' size='2'>"
        + "              <option id='myOptionId1_3_1' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId1_3_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_2_2' name='mySelectName_2_2' size='2'>"
        + "              <option id='myOptionId2_2_1' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId2_2_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_2_3'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_2_3' name='mySelectName_2_3' size='2'>"
        + "              <option id='myOptionId2_3_1' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId2_3_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > SelectLabelText > myText2",
        "myOptionId1_2_1", "myOptionId1_2_2", "myOptionId1_3_1", "myOptionId1_3_2", "myOptionId2_2_1",
        "myOptionId2_2_2", "myOptionId2_3_1", "myOptionId2_3_2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_3_2') part of [HtmlSelect (id='mySelectId_2_3') (name='mySelectName_2_3')]] found by: BY_LABEL deviation: 0 distance: 0 start: 158 hierarchy: 0>1>3>5>22>48>62>64>67 index: 67",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > not", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void option_empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > ", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(3, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId2_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 68 hierarchy: 0>1>3>4>14>15 index: 15",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(1).toString());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void option_byId_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > myOptionId2_2", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byId_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='1_1myOptionId' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='1_2myOptionId' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='1_3myOptionId' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='2_1myOptionId' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='2_2myOptionId' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='2_3myOptionId' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > 2_2myOption*", "1_1myOptionId",
        "1_2myOptionId", "1_3myOptionId", "2_1myOptionId", "2_2myOptionId", "2_3myOptionId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='2_2myOptionId') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byId_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > *OptionId2_2", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byId_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > OptionId2_2'", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void option_byText_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > myText2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>1option</option>"
        + "<option id='myOptionId1_2' value='myValue2'>2option</option>"
        + "<option id='myOptionId1_3' value='myValue3'>3option</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>1option</option>"
        + "<option id='myOptionId2_2' value='myValue2'>2option</option>"
        + "<option id='myOptionId2_3' value='myValue3'>3option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > 2opti*", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption '2option' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > *Text2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > Text2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 2 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byText_formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'>my<b>T</b>ext2</option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > myText2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byText_empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' value='myValue2'></option>"
        + "<option id='myOptionId2_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > ", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(3, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption '' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId2_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 68 hierarchy: 0>1>3>4>14>15 index: 15",
        tmpEntriesSorted.get(1).toString());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 76 hierarchy: 0>1>3>4>14>18 index: 18",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void option_byLabel_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > myLabel2", "myOptionId1_1",
        "myOptionId1_2", "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byLabel_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > 2myLab*", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byLabel_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > *Label2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byLabel_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > Label2", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 2 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void option_byLabel_empty() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "otherSelectLabelingText"
        + "<select id='otherSelectId' name='otherSelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "mySelectLabelingText"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId2_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2_2' label='' value='myValue2'>myText2</option>"
        + "<option id='myOptionId2_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "mySelectId > ", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3", "myOptionId2_1", "myOptionId2_2", "myOptionId2_3");

    assertEquals(3, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 0 start: 76 hierarchy: 0>1>3>4>14>17 index: 17",
        tmpEntriesSorted.get(0).toString());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId2_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 68 hierarchy: 0>1>3>4>14>15 index: 15",
        tmpEntriesSorted.get(1).toString());
    assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 7 distance: 0 start: 84 hierarchy: 0>1>3>4>14>19 index: 19",
        tmpEntriesSorted.get(2).toString());
  }

  @Test
  public void noSelectPart_byId_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myOptionId1_1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byId_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='1_1myOptionId' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='1_2myOptionId' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='1_3myOptionId' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "1_1myOption*", "1_1myOptionId", "1_2myOptionId",
        "1_3myOptionId");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='1_1myOptionId') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byId_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*OptionId1_1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_ID deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byId_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "OptionId1_1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void noSelectPart_byText_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myText1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byText_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>1option</option>"
        + "<option id='myOptionId1_2' value='myValue2'>2option</option>"
        + "<option id='myOptionId1_3' value='myValue3'>3option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "1opti*", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption '1option' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byText_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<p>Some text .... </p>"
        + "<form action='test'>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*Text1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>6>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byText_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "Text1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 2 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byText_formated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' value='myValue1'>my<b>T</b>ext1</option>"
        + "<option id='myOptionId1_2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myText1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byLabel_full() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "myLabel1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byLabel_wildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='1myLabel' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='2myLabel' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='3myLabel' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "1myLab*", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byLabel_wildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "*Label1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 0 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_byLabel_part() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Some text .... </p>"
        + "<select id='mySelectId' name='mySelectName' size='2'>"
        + "<option id='myOptionId1_1' label='myLabel1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId1_2' label='myLabel2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId1_3' label='myLabel3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "bel1", "myOptionId1_1", "myOptionId1_2",
        "myOptionId1_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABEL deviation: 4 distance: 14 start: 14 hierarchy: 0>1>3>4>7>8 index: 8",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void noSelectPart_inTable() throws IOException, InvalidInputException {
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
        + "          <td id='cell_1_2'>"
        + "            <select id='mySelectId_1_2' name='mySelectName_1_2' size='2'>"
        + "              <option id='myOptionId1_2_1' label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId1_2_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_1_3'>"
        + "            <select id='mySelectId_1_3' name='mySelectName_1_3' size='2'>"
        + "              <option id='myOptionId1_3_1' label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId1_3_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'>"
        + "            <select id='mySelectId_2_2' name='mySelectName_2_2' size='2'>"
        + "              <option id='myOptionId2_2_1' label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId2_2_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_2_3'>"
        + "            <select id='mySelectId_2_3' name='mySelectName_2_3' size='2'>"
        + "              <option id='myOptionId2_3_1' label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option id='myOptionId2_3_2' value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > myText2", "myOptionId1_2_1",
        "myOptionId1_2_2", "myOptionId1_3_1", "myOptionId1_3_2", "myOptionId2_2_1", "myOptionId2_2_2",
        "myOptionId2_3_1", "myOptionId2_3_2");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_3_2') part of [HtmlSelect (id='mySelectId_2_3') (name='mySelectName_2_3')]] found by: BY_LABEL deviation: 0 distance: 86 start: 94 hierarchy: 0>1>3>5>22>48>62>64>67 index: 67",
        tmpEntriesSorted.get(0).toString());
  }
}
