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
public class HtmlUnitInputImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputImageIdentifier();
  }

  @Test
  public void imageInput_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Alt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Alt_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_AltPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yAl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 2 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png' title='MyTitle'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_Title_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_TitlePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yTitl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='web/picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='web/picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImageInput '' (src='picture.png') (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void imageInput_FileName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_FileNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='image' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("icture.pn", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void imageInput_IdNameAltTitleFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='MyName' type='image' name='MyName' src='MyName' alt='MyName' title='MyName'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "MyName", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImageInput '' (src='MyName') (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

}
