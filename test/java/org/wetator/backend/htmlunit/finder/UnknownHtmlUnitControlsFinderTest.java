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


package org.wetator.backend.htmlunit.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class UnknownHtmlUnitControlsFinderTest {

  @Test
  public void noHtml() throws IOException {
    String tmpHtmlCode = "";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void noBody() throws IOException {
    String tmpHtmlCode = "<html>" + "<head><title>MyTitle</title></head>" + "</html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void empty() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Name", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void textNotFound() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("YourText", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void textExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 0 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void textWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "MyText" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("My*", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert
        .assertEquals(
            "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlBody'] found by: BY_TEXT coverage: 4 distance: 0 start: 0",
            tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void paragraphTextNotFound() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("YourText", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void paragraphTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0 start: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void paragraphTextWildcard() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>MyText</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("My*", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 4 distance: 0 start: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void paragraphFormatedTextExact() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>My<b>T</b>ext</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'MyText'] found by: BY_TEXT coverage: 0 distance: 0 start: 0", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void manyParagraphs() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>My<b>T</b>ext</p>" + "<p>line2</p>" + "<p>line3</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));
    tmpSearch.add(new SecretString("ine3", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 8 start: 12", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void manyParagraphs_MatchInside() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<p>line2</p>" + "<p>line3</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("line2 li", false));
    tmpSearch.add(new SecretString("ne3", false));

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
    Assert.assertEquals("[HtmlParagraph 'line3'] found by: BY_TEXT coverage: 0 distance: 0 start: 5", tmpFound
        .getEntriesSorted().get(0).toString());
  }

  @Test
  public void ignoringElementForControl() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a>MyText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("MyText", false));

    HtmlUnitControlRepository tmpRepository = new HtmlUnitControlRepository();
    tmpRepository.add(HtmlUnitAnchor.class);

    UnknownHtmlUnitControlsFinder tmpFinder = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, tmpRepository);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }
}
