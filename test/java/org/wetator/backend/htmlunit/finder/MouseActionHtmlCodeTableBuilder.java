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

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for HTML code of table elements.<br>
 * <br>
 * Adds <code>onclick</code>-event listeners for all table elements per default. Use {@link #noListen()} to avoid.
 *
 * @author tobwoerk
 */
public final class MouseActionHtmlCodeTableBuilder {

  private String tableId;
  private List<TableRow> rows = new ArrayList<>();

  private boolean listenTable = true;

  private class TableRow {
    private String rowId;
    private int columnCount;

    private boolean listenRow = true;

    TableRow(final String aRowId, final int aColumnCount) {
      rowId = aRowId;
      columnCount = aColumnCount;
    }
  }

  private MouseActionHtmlCodeTableBuilder(final String aTableId) {
    tableId = aTableId;
  }

  public static MouseActionHtmlCodeTableBuilder table(final String aTableId) {
    return new MouseActionHtmlCodeTableBuilder(aTableId);
  }

  public MouseActionHtmlCodeTableBuilder tr(final String aRowId, final int aColumnCount) {
    rows.add(new TableRow(aRowId, aColumnCount));
    return this;
  }

  public MouseActionHtmlCodeTableBuilder noListen() {
    if (rows.isEmpty()) {
      listenTable = false;
    } else {
      rows.get(rows.size() - 1).listenRow = false;
    }
    return this;
  }

  public String build() {
    final StringBuilder tmpTableHtml = new StringBuilder(MouseActionHtmlCodeCreator.tableStart(tableId, listenTable));
    for (TableRow tmpRow : rows) {
      tmpTableHtml.append(
          MouseActionHtmlCodeCreator.tableRowWithCols(tableId, tmpRow.rowId, tmpRow.columnCount, tmpRow.listenRow));
    }
    tmpTableHtml.append(MouseActionHtmlCodeCreator.tableEnd());
    return tmpTableHtml.toString();
  }

  @Override
  public String toString() {
    return build();
  }
}
