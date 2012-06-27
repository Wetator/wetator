/*
 * Copyright (c) 2008-2011 wetator.org
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
public class HtmlUnitButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitButtonIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("my*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_ID coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", 1, new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithAnotherText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 21",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Sarker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byText() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ButtonWithTe*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("uttonWithTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byText_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byText_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdNameText() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='MyName' type='button' name='MyName'>"
        + "<p>MyName</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyName");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Name() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_NameWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_NamePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yImageNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byInnerImage_Name_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Name_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Wrong text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byInnerImage_Alt() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_AltWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageA*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_AltPart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yImageAl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Alt_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Alt_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Wrong text", false));
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byInnerImage_Title() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_TitleWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTit*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_TitlePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yImageTitl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Title_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Title_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Wrong text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byInnerImage_FileName() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_FileNameWildcard() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.p*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_FileNamePart() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byInnerImage_FileNameWithPath() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: web/picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_FileName_TextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_FileName_WrongTextBefore() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Wrong text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
