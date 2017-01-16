/*
 * Copyright (c) 2008-2017 wetator.org
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
 * As the {@link HtmlUnitOptionInSelectIdentifier} does not use pretested
 * {@link org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher}s we
 * have to test everything here.
 *
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitOptionInSelectIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionInSelectIdentifier();
  }

  @Test
  public void not() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("not > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*ySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("SecondSelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > mySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardRight_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > mySelectI* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardLeft_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > *ySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > ySecondSelectI > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_WrongTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_NoTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectId > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectNa* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*ySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("SecondSelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > mySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardRight_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > mySelectNa* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardLeft_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > *ySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > ySecondSelectNam > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_WrongTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_NoTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectName > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelingTextBeforeFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelingTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*ySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforePart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("ySelectLabelingTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeFull_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeWildcardRight_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelingTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeWildcardLeft_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > *ySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforePart_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > ySelectLabelingTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelingTextBeforeFull_WrongTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelingTextBeforeFull_NoTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelingText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_TextFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*ySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("ySelectLabelTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextFull_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardRight_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardLeft_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > *ySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextPart_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > ySelectLabelTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextFull_WrongTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_TextFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='mySelectId'>mySelectLabelText</label>"
        + "<select id='mySelectId' size='2'>"
        + "<option id='myOptionId1' value='myValue1'>myText1</option>"
        + "<option id='myOptionId2' value='myValue2'>myText2</option>"
        + "<option id='myOptionId3' value='myValue3'>myText3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId", "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_TextFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectLabelTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*ySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("ySelectLabelTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextFull_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardRight_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > mySelectLabelTe* > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardLeft_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > *ySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextPart_TextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("otherSelectLabelingText > ySelectLabelTex > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId')]] found by: BY_LABELING_TEXT coverage: 0 distance: 1 start: 81 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextFull_WrongTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId", "labelId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_TextFull_NoTextBefore() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("wrong text > mySelectLabelText > myText3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId", "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
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
        + "              <option value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_1_3'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_1_3' name='mySelectName_1_3' size='2'>"
        + "              <option value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_2_2' name='mySelectName_2_2' size='2'>"
        + "              <option value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_2_3'>"
        + "            SelectLabelText"
        + "            <select id='mySelectId_2_3' name='mySelectName_2_3' size='2'>"
        + "              <option value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2] > SelectLabelText > myText2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "mySelectId_1_2",
        "mySelectId_1_3", "mySelectId_2_2", "mySelectId_2_3");
    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' part of [HtmlSelect (id='mySelectId_2_3') (name='mySelectName_2_3')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 158 index: 67",
        tmpFound.getEntriesSorted().get(0).toString());
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

    final SecretString tmpSearch = new SecretString("mySelectId > not");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void option_byTextFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > myText2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > 2opti*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption '2option' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > *Text2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > Text2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 2 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextFormated() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > myText2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextEmpty() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(3, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption '' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId2_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 7 distance: 0 start: 68 index: 15",
        tmpFound.getEntriesSorted().get(1).toString());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 7 distance: 0 start: 76 index: 18",
        tmpFound.getEntriesSorted().get(2).toString());
  }

  @Test
  public void option_byLabelFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > myLabel2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > 2myLab*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > *Label2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > Label2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 2 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelEmpty() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("mySelectId > ");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "otherSelectId",
        "mySelectId");

    Assert.assertEquals(3, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' (id='myOptionId2_2') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 0 start: 76 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId2_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 7 distance: 0 start: 68 index: 15",
        tmpFound.getEntriesSorted().get(1).toString());
    Assert.assertEquals(
        "[HtmlOption 'myText3' (id='myOptionId2_3') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 7 distance: 0 start: 84 index: 19",
        tmpFound.getEntriesSorted().get(2).toString());
  }

  @Test
  public void noSelectPart_byTextFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("myText1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("1opti*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption '1option' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*Text1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("Text1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 2 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextFormated() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("myText1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelFull() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("myLabel1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelWildcardRight() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("1myLab*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelWildcardLeft() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("*Label1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 0 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelPart() throws IOException, InvalidInputException {
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

    final SecretString tmpSearch = new SecretString("bel1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "mySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText1' (id='myOptionId1_1') part of [HtmlSelect (id='mySelectId') (name='mySelectName')]] found by: BY_LABELING_TEXT coverage: 4 distance: 14 start: 14 index: 8",
        tmpFound.getEntriesSorted().get(0).toString());
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
        + "              <option label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_1_3'>"
        + "            <select id='mySelectId_1_3' name='mySelectName_1_3' size='2'>"
        + "              <option label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'>"
        + "            <select id='mySelectId_2_2' name='mySelectName_2_2' size='2'>"
        + "              <option label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "          <td id='cell_2_3'>"
        + "            <select id='mySelectId_2_3' name='mySelectName_2_3' size='2'>"
        + "              <option label='MyLabel' value='myValue1'>myText1</option>"
        + "              <option value='myValue2'>myText2</option>"
        + "            </select>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2] > myText2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "mySelectId_1_2",
        "mySelectId_1_3", "mySelectId_2_2", "mySelectId_2_3");
    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlOption 'myText2' part of [HtmlSelect (id='mySelectId_2_3') (name='mySelectName_2_3')]] found by: BY_LABELING_TEXT coverage: 0 distance: 86 start: 94 index: 67",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
