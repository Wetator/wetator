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
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitOptionIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitOptionIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='myId' name='myName' size='2'>"
        + "<option id='myOptionId1' value='o_value1'>option1</option>"
        + "<option id='myOptionId2' value='o_value2'>option2</option>"
        + "<option id='myOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myOptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myOptionId1", "myOptionId2",
        "myOptionId3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='myOptionId1') part of [HtmlSelect (id='myId') (name='myName')]] found by: BY_ID coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='myId' name='myName' size='2'>"
        + "<option id='myOptionId1' value='o_value1'>option1</option>"
        + "<option id='myOptionId2' value='o_value2'>option2</option>"
        + "<option id='myOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("*OptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myOptionId1", "myOptionId2",
        "myOptionId3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='myOptionId1') part of [HtmlSelect (id='myId') (name='myName')]] found by: BY_ID coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<select id='myId' name='myName' size='2'>"
        + "<option id='myOptionId1' value='o_value1'>option1</option>"
        + "<option id='myOptionId2' value='o_value2'>option2</option>"
        + "<option id='myOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("OptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myOptionId1", "myOptionId2",
        "myOptionId3");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<select id='myId' name='myName' size='2'>" + "<option id='myOptionId1' value='o_value1'>option1</option>"
        + "<option id='myOptionId2' value='o_value2'>option2</option>"
        + "<option id='myOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myOptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myOptionId1", "myOptionId2",
        "myOptionId3");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[HtmlOption 'option1' (id='myOptionId1') part of [HtmlSelect (id='myId') (name='myName')]] found by: BY_ID coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<select id='myId' name='myName' size='2'>" + "<option id='myOptionId1' value='o_value1'>option1</option>"
        + "<option id='myOptionId2' value='o_value2'>option2</option>"
        + "<option id='myOptionId3' value='o_value3'>option3</option>" + "</select>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myOptionId1", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myOptionId1", "myOptionId2",
        "myOptionId3");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
