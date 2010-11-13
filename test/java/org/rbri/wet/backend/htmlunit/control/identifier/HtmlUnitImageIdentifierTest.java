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
public class HtmlUnitImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitImageIdentifier();
  }

  @Test
  public void anchor_Image_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Alt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Title_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='web/picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'web/picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_FileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void anchor_Image_Name_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void button_Image_Name() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void button_Image_Alt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void button_Image_Title() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void button_Image_FileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void button_Image_FileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'web/picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
