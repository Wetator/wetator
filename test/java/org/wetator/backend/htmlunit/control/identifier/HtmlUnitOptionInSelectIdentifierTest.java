/*
 * Copyright (c) 2008-2015 wetator.org
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
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("not, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelect* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*SecondSelectId > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectId, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > MySecondSelectId > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > MySecondSelect* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > *SecondSelectId > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText, ySecondSelectI, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, MySecondSelectId, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, MySecondSelectId, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectName > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectNa* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*SecondSelectName > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectName, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > MySecondSelectName > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > MySecondSelectNa* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > *SecondSelectName > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText, ySecondSelectNam, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, MySecondSelectName, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNameFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, MySecondSelectName, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelTextBeforeFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*condSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 46 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 46 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > *condSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 46 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforePart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 46 start: 82 index: 19",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelTextBeforeFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_TextFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*condSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > *condSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_TextFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_TextFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*condSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextFull_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelText > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardRight_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > SecondSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcardLeft_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > *condSelectLabelTe* > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextPart_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("FirstSelectLabelText > econdSelectLabelTex > o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82 index: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextFull_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "labelId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_TextFull_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text, SecondSelectLabelText, o_value3");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "labelId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void option_not() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId, not");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void option_byTextFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > option2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>1option</option>"
        + "<option id='1_2' value='o_value2'>2option</option>"
        + "<option id='1_3' value='o_value3'>3option</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>1option</option>"
        + "<option id='2_2' value='o_value2'>2option</option>"
        + "<option id='2_3' value='o_value3'>3option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > 2opti*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption '2option' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > *tion2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > tion2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextFormated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>op<b>t</b>ion2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > option2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > o_label2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > o_labe*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > *label2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' label='o_label2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > label2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValueFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > o_value2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValueWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='1o_value'>option1</option>"
        + "<option id='1_2' value='2o_value'>option2</option>"
        + "<option id='1_3' value='3o_value'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='1o_value'>option1</option>"
        + "<option id='2_2' value='2o_value'>option2</option>"
        + "<option id='2_3' value='3o_value'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > 2o_val*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValueWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > *value2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValuePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>"
        + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MySecondSelectId > value2");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyFirstSelectId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74 index: 17",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>option1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("option1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option value='o_value1'>1option</option>"
        + "<option value='o_value2'>2option</option>"
        + "<option value='o_value3'>3option</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("1opti*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption '1option' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*tion1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("tion1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextFormated() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>op<b>t</b>ion1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("option1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyLabel");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyLab*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelPart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Label");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValueFull() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("o_value1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValueWildcardRight() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='1o_value'>option1</option>"
        + "<option value='2o_value'>option2</option>"
        + "<option value='3o_value'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("1o_val*");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValueWildcardLeft() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("*value1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValuePart() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("value1");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
    .assertEquals(
        "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0 index: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
