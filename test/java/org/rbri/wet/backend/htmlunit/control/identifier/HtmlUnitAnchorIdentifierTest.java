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
public class HtmlUnitAnchorIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitAnchorIdentifier();
  }

  @Test
  public void anchor_Id() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlAnchor 'TestAnchor' (id='myId')] found by: BY_ID coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Id_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Id_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void anchor_IdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void anchor_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Name_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void anchor_NamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void anchor_Text() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("TestAnchor", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Text_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("TestAnchor", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Text_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("TestAnchor", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void anchor_FormatedText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a id='myId'>My<b>T</b>ext</a>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlAnchor 'MyText' (id='myId')] found by: BY_LABEL_TEXT coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_TextPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("estAncho", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_IdNameText() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<a id='myAnchor' name='myAnchor' href='snoopy.php'>myAnchor</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myAnchor", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myAnchor", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'myAnchor' (id='myAnchor') (name='myAnchor')] found by: BY_ID coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Alt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>" + "<img src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Title_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='web/picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: web/picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myId", tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

}
