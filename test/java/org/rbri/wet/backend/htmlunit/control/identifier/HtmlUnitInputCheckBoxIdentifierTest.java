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
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputCheckBoxIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputCheckBoxIdentifier();
  }

  @Test
  public void byIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyCheckboxId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("CheckBox", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyCheckboxId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("e*Box", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyCheckboxId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyCheckboxId' name='MyCheckboxName' value='value1' type='checkbox'>CheckBox" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyCheckboxName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyCheckboxId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals(
        "[HtmlCheckBoxInput (id='MyCheckboxId') (name='MyCheckboxName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
