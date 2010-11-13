/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitOptionInSelectIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionInSelectIdentifier();
  }

  @Test
  public void byTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabel", false));
    tmpSearch.add(new SecretString("option2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')]] found by: BY_LABEL coverage: 0 distance: 66",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyFirstSelectName", false));
    tmpSearch.add(new SecretString("tion3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='1_3') part of [HtmlSelect (id='MyFirstSelectId') (name='MyFirstSelectName')]] found by: BY_LABEL coverage: 2 distance: 20",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "SecondSelectLabelText" + "<select id='MySecondSelectId' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MySecondSelectId", false));
    tmpSearch.add(new SecretString("o_value3", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option3' (id='2_3') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 66",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='LabelId1' for='MyFirstSelectId'>FirstSelectLabelText</label>"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "<label id='LabelId2' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>" + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>" + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "LabelId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "LabelId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='2_1') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='LabelId1'>FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "</label>" + "<label id='LabelId2'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>" + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>" + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));
    tmpSearch.add(new SecretString("o_value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "LabelId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "LabelId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='2_1') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 44",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelText_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='LabelId1' for='MyFirstSelectId'>FirstSelectLabelText</label>"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "<p>before</p>" + "<label id='LabelId2' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>" + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>" + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("before", false));
    tmpSearch.add(new SecretString("SelectLabelText", false));
    tmpSearch.add(new SecretString("option2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "LabelId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "LabelId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option2' (id='2_2') part of [HtmlSelect (id='MySecondSelectId')]] found by: BY_LABEL coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>option1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("option1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>" + "<option value='o_value1'>option1</option>"
        + "<option value='o_value2'>option2</option>" + "<option value='o_value3'>option3</option>" + "</select>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("pt*n1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyLabel", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("y*el", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byValueExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("o_value1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byValueWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<select id='MySelectId' name='MySelectName' size='2'>"
        + "<option label='MyLabel' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("_*e1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MySelectId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' part of [HtmlSelect (id='MySelectId') (name='MySelectName')]] found by: BY_LABEL coverage: 1 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
