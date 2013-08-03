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


package org.wetator.backend.htmlunit.finder;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class IdentifierBasedHtmlUnitControlsFinderTest {

  @Test
  public void empty() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    SecretString tmpSearch = new SecretString("Name");

    IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void hidden() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input id='myId' type='submit' value='ClickMe' style='visibility: hidden;'>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    SecretString tmpSearch = new SecretString("ClickMe");

    IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    tmpFinder.addIdentifier(HtmlUnitInputSubmitIdentifier.class);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(0, tmpFound.getEntriesSorted().size());
  }

  @Test
  public void visible() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<input id='myId' type='submit' value='ClickMe'>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    SecretString tmpSearch = new SecretString("ClickMe");

    IdentifierBasedHtmlUnitControlsFinder tmpFinder = new IdentifierBasedHtmlUnitControlsFinder(tmpHtmlPageIndex, null);
    tmpFinder.addIdentifier(HtmlUnitInputSubmitIdentifier.class);
    WeightedControlList tmpFound = tmpFinder.find(new WPath(tmpSearch));

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());
  }
}
