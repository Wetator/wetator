/*
 * Copyright (c) 2008-2013 wetator.org
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
public class HtmlUnitImageIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitImageIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("myId");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' alt='MyAlt'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyAlt");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png' title='MyTitle'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyTitle");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='myId' name='MyName' src='picture.png'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("picture.png");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myId') (name='MyName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdNameAltTitleSrc() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<img id='MyName' name='MyName' src='MyName' alt='MyName' title='MyName'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "MyName");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'MyName' (id='MyName') (name='MyName')] found by: BY_ID coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyImageName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyAlt");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyImageTitle");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inAnchor_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName' title='MyTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("picture.png");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' name='MyImageName'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyImageName");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert
        .assertEquals(
            "[HtmlImage 'picture.png' (id='myInnerId') (name='MyImageName')] found by: BY_NAME coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageAlt() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' alt='MyImageAlt'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyImageAlt");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_ALT_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageTitle() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("MyImageTitle");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void inButton_byImageSrc() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<img id='myInnerId' src='picture.png' title='MyImageTitle'>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("picture.png");

    WeightedControlList tmpFound = identify(tmpHtmlCode, "myInnerId", new WPath(tmpSearch, config));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlImage 'picture.png' (id='myInnerId')] found by: BY_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
