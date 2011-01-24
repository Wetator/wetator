/*
 * Copyright (c) 2008-2011 www.wetator.org
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
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitOptionGroupIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionGroupIdentifier();
  }

  @Test
  public void byLabelText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("colors", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "optgroup_colors", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='MyFirstSelectId' size='2'>"
        + "<optgroup label='colors' id='optgroup_colors'>" + "<option value='o_red'>red</option>"
        + "<option value='o_green'>green</option>" + "<option value='o_blue'>blue</option>" + "</select>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("optgroup_colors", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "optgroup_colors", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOptionGroup 'colors' (id='optgroup_colors') part of [HtmlSelect (id='MyFirstSelectId')]] found by: BY_ID coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
