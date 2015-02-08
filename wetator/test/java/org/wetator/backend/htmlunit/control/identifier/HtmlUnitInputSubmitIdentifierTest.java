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
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputSubmitIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputSubmitIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("myId");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='submit' name='MyName' value='ClickMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlSubmitInput 'ClickMe' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0 index: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byValue() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("ClickMe");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlSubmitInput 'ClickMe' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdNameValue() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='MyName' type='submit' name='MyName' value='MyName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyName");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlSubmitInput 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
