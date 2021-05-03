/*
 * Copyright (c) 2008-2021 wetator.org
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

import org.junit.AfterClass;
import org.junit.Test;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;

/**
 * Tests for the '$PAGE' pseudo WPath.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderPagePseudoWPathTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  private static final String PAGE_WPATH = "$PAGE";
  private static final HtmlCodeBuilder HTML_PAGE = div(PAGE_WPATH, PAGE_WPATH);
  private static final SortedEntryExpectation EXPECTED = new SortedEntryExpectation(
      new ExpectedControl(HtmlDivision.class, "$PAGE"),
      // FIXME [UNKNOWN] remove div from expectation when unknown controls are supported by
      // MouseActionListeningHtmlUnitControlsFinder
      new ExpectedControl(HtmlBody.class));

  private MouseAction mouseAction;

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, null, mouseAction, repository);
  }

  @Test
  public void clickPage() throws Exception {
    mouseAction = MouseAction.CLICK;
    HtmlCodeCreator.listenToClick();
    checkFoundElements(HTML_PAGE, EXPECTED);
  }

  @Test
  public void clickDoublePage() throws Exception {
    mouseAction = MouseAction.CLICK_DOUBLE;
    HtmlCodeCreator.listenToClickDouble();
    checkFoundElements(HTML_PAGE, EXPECTED);
  }

  @Test
  public void clickRightPage() throws Exception {
    mouseAction = MouseAction.CLICK_RIGHT;
    HtmlCodeCreator.listenToClickRight();
    checkFoundElements(HTML_PAGE, EXPECTED);
  }

  @Test
  public void mouseOverPage() throws Exception {
    mouseAction = MouseAction.MOUSE_OVER;
    HtmlCodeCreator.listenToMouseOver();
    checkFoundElements(HTML_PAGE, EXPECTED);
  }

  @Override
  protected String getWPath() {
    return PAGE_WPATH;
  }

  @AfterClass
  public static void resetListenersInCreator() {
    HtmlCodeCreator.resetListeners();
  }
}
