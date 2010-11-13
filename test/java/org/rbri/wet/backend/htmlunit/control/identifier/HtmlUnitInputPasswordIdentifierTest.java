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
public class HtmlUnitInputPasswordIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputPasswordIdentifier();
  }

  @Test
  public void byLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='inputId'>Label</label>"
        + "<input id='inputId' name='PasswordInput' type='password'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='inputId') (name='PasswordInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='inputId' name='PasswordInput' type='password'>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "labelId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='inputId') (name='PasswordInput')] found by: BY_LABEL coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>CWID</td>" + "<td>"
        + "<input id='userForm:cwidTxt' name='userForm:cwidTxt' type='text' value=''/>" + "</td>" + "</tr>" + "<tr>"
        + "<td>Passwort</td>" + "<td>"
        + "<input type='password' id='userForm:passwordTxt' name='userForm:passwordTxt'/>" + "</td>" + "</tr>"
        + "</table>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Passwort", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "userForm:passwordTxt", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlPasswordInput (id='userForm:passwordTxt') (name='userForm:passwordTxt')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBeforeWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<table>" + "<tr>" + "<td>CWID</td>" + "<td>"
        + "<input id='userForm:cwidTxt' name='userForm:cwidTxt' type='text' value=''/>" + "</td>" + "</tr>" + "<tr>"
        + "<td>Passwort</td>" + "<td>"
        + "<input type='password' id='userForm:passwordTxt' name='userForm:passwordTxt'/>" + "</td>" + "</tr>"
        + "</table>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Pass*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "userForm:passwordTxt", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlPasswordInput (id='userForm:passwordTxt') (name='userForm:passwordTxt')] found by: BY_LABEL_TEXT coverage: 4 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
