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
public class HtmlUnitImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitImageIdentifier();
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("my*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ID coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 5 start: 14",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_NAME coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 5 start: 14",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byAltWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyA*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byAltPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yAl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byAlt_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byAlt_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'  alt='MyAlt'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' title='MyTitle'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTitleWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' title='MyTitle'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTit*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTitlePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yTitl", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 2 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTitle_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTitle_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'  title='MyTitle'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byFileNameWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.p*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byFileNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("icture.pn", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='myId' name='MyName' src='web/picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'web/picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byFileName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byFileName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<img id='myId' name='MyName' src='picture.png'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byIdNameAltTitleFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<img id='MyName' name='MyName' src='MyName' alt='MyName' title='MyName'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "MyName");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' alt='MyAlt'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byAlt_TextBefore() throws IOException {
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
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byTitle_TextBefore() throws IOException {
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
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='web/picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'web/picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byFileName_TextBefore() throws IOException {
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
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>" + "</a>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>" + "</a>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 5 start: 14",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byAlt() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' alt='MyImageAlt'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageAlt", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byTitle() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyImageTitle", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byFileName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>" + "</button>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byFileNameWithPath() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='web/picture.png' title='MyImageTitle'>" + "</button>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("picture.png", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'web/picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 4 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }
}
