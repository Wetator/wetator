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
public class HtmlUnitInputRadioButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputRadioButtonIdentifier();
  }

  @Test
  public void byIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyRadioButtonId2", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyRadioButtonId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyRadioButtonId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_ID coverage: 0 distance: 12",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("RadioButton1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyRadioButtonId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyRadioButtonId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("u*on1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyRadioButtonId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyRadioButtonId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 6 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<label id='MyLabelId1' for='MyRadioButtonId1'>FirstLabelText</label>"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1"
        + "<label id='MyLabelId2' for='MyRadioButtonId2'>SecondLabelText</label>"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyLabelId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyLabelId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 0 distance: 27",
            tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "MyRadioButtonId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyRadioButtonId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='MyLabelId1'>FirstLabelText"
        + "<input id='MyRadioButtonId1' name='MyRadioButtonName' value='value1' type='radio'>RadioButton1" + "</label>"
        + "<label id='MyLabelId2'>SecondLabelText"
        + "<input id='MyRadioButtonId2' name='MyRadioButtonName' value='value2' type='radio'>RadioButton2" + "</label>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("SecondLabelText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyLabelId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyLabelId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value2' (id='MyRadioButtonId2') (name='MyRadioButtonName')] found by: BY_LABEL coverage: 13 distance: 27",
            tmpFound.getEntriesSorted().get(0).toString());

    tmpFound = identify(tmpHtmlCode, "MyRadioButtonId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyRadioButtonId2", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlRadioButtonInput 'value1' (id='MyRadioButtonId1') (name='MyRadioButtonName')] found by: BY_LABEL_TEXT coverage: 12 distance: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
