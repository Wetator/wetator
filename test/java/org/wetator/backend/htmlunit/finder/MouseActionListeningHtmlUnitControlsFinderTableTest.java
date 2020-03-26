/*
 * Copyright (c) 2008-2020 wetator.org
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

import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.pageEnd;
import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.pageStart;
import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.tableEnd;
import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.tableRowWithCols;
import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.tableStart;

import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTable}s and their children.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderTableTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderTest {

  @Test
  public void tableBig() throws Exception {
    // @formatter:off
    final String tmpHtmlCode = pageStart()

    + tableStart("table")
    + tableRowWithCols("table", "tr1", 2)
    + tableRowWithCols("table", "tr2", 3)
    + tableRowWithCols("table", "tr3", 2)
    + tableRowWithCols("table", "tr4", 1)
    + tableEnd()

    + pageEnd();
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

    setMouseAction(MouseAction.CLICK);
    setup(tmpHtmlCode);
    final WeightedControlList tmpFound = find();
    assertion(tmpSortedEntryExpectation, tmpFound);
  }
}
