/*
 * Copyright (c) 2008-2025 wetator.org
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

import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableDataCell;
import org.htmlunit.html.HtmlTableRow;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTable}s and their children.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderTableTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, null, MouseAction.CLICK, repository);
  }

  @Test
  public void tableBig() throws Exception {
    HtmlCodeCreator.listenToClick();

    // @formatter:off
    final String tmpHtmlCode = HtmlCodeCreator.pageStart()

    + table("table")
        .tr("tr1", 2)
        .tr("tr2", 3)
        .tr("tr3", 2)
        .tr("tr4", 1)

    + HtmlCodeCreator.pageEnd();
    // @formatter:on

    // @formatter:off
    final SortedEntryExpectation tmpSortedEntryExpectation = new SortedEntryExpectation(
        new ExpectedControl(HtmlTableDataCell.class, "table-tr1-td1"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr1-td2"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr2-td1"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr2-td2"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr2-td3"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr3-td1"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr3-td2"),
        new ExpectedControl(HtmlTableDataCell.class, "table-tr4-td"),
        new ExpectedControl(HtmlTableRow.class, "table-tr4"),
        new ExpectedControl(HtmlTableRow.class, "table-tr1"),
        new ExpectedControl(HtmlTableRow.class, "table-tr3"),
        new ExpectedControl(HtmlTableRow.class, "table-tr2"),
        new ExpectedControl(HtmlTableBody.class, "table-body"),
        new ExpectedControl(HtmlTable.class, "table")
    );
    // @formatter:on

    checkFoundElements(tmpHtmlCode, tmpSortedEntryExpectation);
  }
}
