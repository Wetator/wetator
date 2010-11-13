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
public class HtmlUnitButtonIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitButtonIdentifier();
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", 1, new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithAnotherText' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "<p>Marker</p>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithAnotherText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Sarker", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("ButtonWithText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("uttonWithTex", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlButton 'ButtonWithText' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<button id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdNameLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='MyName' type='button' name='MyName'>"
        + "<p>MyName</p>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyName", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlButton 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImage_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImage_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImage_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImage_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImage_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlButton 'image: web/picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

}
