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
public class HtmlUnitSelectIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitSelectIdentifier();
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId' value='o_value1'>option1</option>" + "<option value='o_value2'>option2</option>"
        + "<option value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MyId') (name='MySelectName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
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
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')] found by: BY_LABEL_TEXT coverage: 0 distance: 45",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBeforeWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("cond*elText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')] found by: BY_LABEL_TEXT coverage: 2 distance: 47",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextPathBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "SecondSelectLabelText"
        + "<select id='MySecondSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Fir", false));
    tmpSearch.add(new SecretString("tSelectLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlSelect (id='MyFirstSelectId') (name='MyFirstSelectName')] found by: BY_LABEL_TEXT coverage: 1 distance: 1",
            tmpFound.getEntriesSorted().get(0).toString());
    Assert.assertEquals(
        "[HtmlSelect (id='MySecondSelectId') (name='MySecondSelectName')] found by: BY_TEXT coverage: 46 distance: 63",
        tmpFound.getEntriesSorted().get(1).toString());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "FirstSelectLabelText"
        + "<select id='MyFirstSelectId' name='MyFirstSelectName' size='2'>"
        + "<option id='1_1' value='o_value1'>option1</option>" + "<option id='1_2' value='o_value2'>option2</option>"
        + "<option id='1_3' value='o_value3'>option3</option>" + "</select>" + "SecondSelectLabelText"
        + "<select id='MyFirstSelectId' name='MySecondSelectName' size='2'>"
        + "<option id='2_1' value='o_value1'>option1</option>" + "<option id='2_2' value='o_value2'>option2</option>"
        + "<option id='2_3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyFirstSelectName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstSelectId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondSelectId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlSelect (id='MyFirstSelectId') (name='MyFirstSelectName')] found by: BY_NAME coverage: 0 distance: 20",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyFirstLabelId' for='MyFirstSelectId'>FirstSelectLabelText</label>"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "<label id='MySecondLabelId' for='MySecondSelectId'>SecondSelectLabelText</label>"
        + "<select id='MySecondSelectId' size='2'>" + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>" + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstLabelId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondLabelId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MySecondSelectId')] found by: BY_LABEL coverage: 0 distance: 44", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyFirstLabelId'>FirstSelectLabelText"
        + "<select id='MyFirstSelectId' size='2'>" + "<option id='1_1' value='o_value1'>option1</option>"
        + "<option id='1_2' value='o_value2'>option2</option>" + "<option id='1_3' value='o_value3'>option3</option>"
        + "</select>" + "</label>" + "<label id='MySecondLabelId'>SecondSelectLabelText"
        + "<select id='MySecondSelectId' size='2'>" + "<option id='2_1' value='o_value1'>option1</option>"
        + "<option id='2_2' value='o_value2'>option2</option>" + "<option id='2_3' value='o_value3'>option3</option>"
        + "</select>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondSelectLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyFirstLabelId", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MySecondLabelId", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlSelect (id='MySecondSelectId')] found by: BY_LABEL coverage: 24 distance: 44", tmpFound
        .getEntriesSorted().get(0).toString());
  }
}
