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
public class HtmlUnitInputFileIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputFileIdentifier();
  }

  @Test
  public void testGetAllSetables_FileInputByLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "<input id='inputId' name='FileInput' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='inputId') (name='FileInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllSetables_FileInputByLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='inputId' name='FileInput' type='file'>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='inputId') (name='FileInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

}
