/*
 * Copyright (c) 2008-2012 wetator.org
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
import java.util.ArrayList;
import java.util.List;

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
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("not", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*SecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*SecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("MySecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MySecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MySecondSelectName", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*condSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforePart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdSelectLabelTex", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBefore_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("FirstSelectLabelText", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 46 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBefore_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelTextBefore_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*condSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_TextPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdSelectLabelTex", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("FirstSelectLabelText", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabel_Text_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*condSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_TextPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("econdSelectLabelTex", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_TextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("FirstSelectLabelText", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 24 start: 82",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text_WrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "labelId",
        "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byHtmlLabelChild_Text_NoTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void option_not() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("not", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void option_byText() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("option2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("*tion2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("tion2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byTextFormated() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("option2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabel() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_label2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("*label2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byLabelPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("label2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValue() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValueWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("*value2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void option_byValuePart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
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

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("value2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyFirstSelectId", "MySecondSelectId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 2 distance: 66 start: 74",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byText() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>option1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("option1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*tion1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("tion1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byTextFormated() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>op<b>t</b>ion1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("option1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabel() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyLabel", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byLabelPart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValue() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("o_value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValueWildcard() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void noSelectPart_byValuePart() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>"
        + "</select>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
