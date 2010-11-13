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
public class HtmlUnitOptionIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionIdentifier();
  }

  @Test
  public void byIdExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyId' name='MySelectName' size='2'>"
        + "<option id='MyOptionId1' value='o_value1'>option1</option>"
        + "<option id='MyOptionId2' value='o_value2'>option2</option>"
        + "<option id='MyOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyOptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyOptionId1", new WPath(tmpSearch));
    tmpFound.addAll(identify(tmpHtmlCode, "MyOptionId2", new WPath(tmpSearch)));
    tmpFound.addAll(identify(tmpHtmlCode, "MyOptionId3", new WPath(tmpSearch)));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='MyOptionId1') part of [HtmlSelect (id='MyId') (name='MySelectName')]] found by: BY_ID coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
