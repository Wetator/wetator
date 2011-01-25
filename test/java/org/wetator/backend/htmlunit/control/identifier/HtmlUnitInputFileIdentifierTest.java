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
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitInputFileIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputFileIdentifier();
  }

  @Test
  public void byId() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='myId') (name='myName')] found by: BY_ID coverage: 0 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("my*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals("[HtmlFileInput (id='myId') (name='myName')] found by: BY_ID coverage: 2 distance: 0 start: 0",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yI", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byId_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_ID coverage: 0 distance: 5 start: 14", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byId_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myId", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_NAME coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNameWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myNa*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_NAME coverage: 2 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byNamePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' name='myName' type='file'>"
        + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("yNam", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byName_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("myName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_NAME coverage: 0 distance: 5 start: 14", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("myName", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='otherId' name='otherName' type='file'>"
        + "<p>Marker</p>" + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBeforeWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='otherId' name='otherName' type='file'>"
        + "<p>Marker</p>" + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Mark*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL_TEXT coverage: 2 distance: 0 start: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBeforePart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='otherId' name='otherName' type='file'>"
        + "<p>Marker</p>" + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("arke", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL_TEXT coverage: 2 distance: 1 start: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBefore_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='file'>" + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Marker", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL_TEXT coverage: 0 distance: 6 start: 21",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTextBefore_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<input id='otherId' name='otherName' type='file'>" + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Marker", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabel() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Lab*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 2 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='file'>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 2 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>" + "<input id='myId' name='myName' type='file'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 5 start: 20", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabel_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='labelId' for='myId'>Label</label>" + "<input id='myId' name='myName' type='file'>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void byLabelChild() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='file'>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChildWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='file'>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Lab*", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 2 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChildPart() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='file'>" + "</label>" + "</form>" + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("abe", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 2 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_TextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='labelId'>Label" + "<input id='myId' name='myName' type='file'>" + "</label>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Some text", false));
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlFileInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 5 start: 20", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelChild_WrongTextBefore() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<p>Some text .... </p>"
        + "<label id='labelId'>Label" + "<input id='myId' name='myName' type='file'>" + "</label>" + "</form>"
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("wrong text", false));
    tmpSearch.add(new SecretString("Label", false));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
