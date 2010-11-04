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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitFinderDelegatorGetAllElementsForTextTest {

  private HtmlUnitControlRepository controlRepository;

  @Before
  public void setupControlFinder() {
    controlRepository = new HtmlUnitControlRepository();
  }

  @Test
  public void testGetAllControlsForText_NoHtml() throws IOException {
    String tmpHtmlCode = "";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllControlsForText_NoBody() throws IOException {
    String tmpHtmlCode = "<html>" + "<head><title>MyTitle</title></head>" + "</html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllControlsForText_Empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllControlsForText_TextNotFound() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("YourText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllControlsForText_TextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 0 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_TextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("My*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 4 distance: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_ParagraphTextNotFound() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("YourText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void testGetAllControlsForText_ParagraphTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_ParagraphTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("My*", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 4 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_ParagraphFormatedTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>My<b>T</b>ext</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_AnchorTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a>MyText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlAnchor 'MyText'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getEntriesSorted()
        .get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_AnchorAndParagraphTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a>MyText</a><p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(2, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlAnchor 'MyText'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getEntriesSorted()
        .get(0).toString());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 7", tmpFound
        .getEntriesSorted().get(1).toString());
  }

  @Test
  public void testGetAllControlsForText_AnchorFormatedTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a>My<b>T</b>ext</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlAnchor 'MyText'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound.getEntriesSorted()
        .get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_ManyParagraphs() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>My<b>T</b>ext</p>" + "<p>line2</p>" + "<p>line3</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));
    tmpSearch.add(new SecretString("ine3", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 8", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void testGetAllControlsForText_ManyParagraphs_MatchInside() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>line2</p>" + "<p>line3</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("line2 li", false));
    tmpSearch.add(new SecretString("ne3", false));

    HtmlUnitFinderDelegator tmpFinder = new HtmlUnitFinderDelegator(tmpHtmlPage, controlRepository);
    WeightedControlList tmpFound = tmpFinder.getAllControlsForText(tmpSearch);

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }
}
