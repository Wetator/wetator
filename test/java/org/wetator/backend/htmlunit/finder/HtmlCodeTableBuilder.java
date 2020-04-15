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
 * Adds listeners defined in {@link HtmlCodeCreator} for all table elements per default. Use {@link #noListen()} to
 * avoid.
 *
 * @author tobwoerk
 */
public final class HtmlCodeTableBuilder {

  private String id;
  private List<TableRow> rows = new ArrayList<>();

  private String style;
  private boolean listen = true;

  private class TableRow {
    private String rowId;
    private int columnCount;

    private boolean rowListen = true;

    TableRow(final String aRowId, final int aColumnCount) {
      rowId = aRowId;
      columnCount = aColumnCount;
    }
  }

  private HtmlCodeTableBuilder(final String aTableId) {
    id = aTableId;
  }

  public static HtmlCodeTableBuilder table(final String aTableId) {
    return new HtmlCodeTableBuilder(aTableId);
  }

  public HtmlCodeTableBuilder tr(final String aRowId, final int aColumnCount) {
    rows.add(new TableRow(aRowId, aColumnCount));
    return this;
  }

  public HtmlCodeTableBuilder style(final String aStyle) {
    style = aStyle;
    return this;
  }

  public HtmlCodeTableBuilder noListen() {
    if (rows.isEmpty()) {
      listen = false;
    } else {
      rows.get(rows.size() - 1).rowListen = false;
    }
    return this;
  }

  public String build() {
    final StringBuilder tmpTableHtml = new StringBuilder(HtmlCodeCreator.tableStart(id, style, listen));
    for (TableRow tmpRow : rows) {
      tmpTableHtml.append(HtmlCodeCreator.tableRowWithCols(id, tmpRow.rowId, tmpRow.columnCount, tmpRow.rowListen));
    }
    tmpTableHtml.append(HtmlCodeCreator.tableEnd());
    return tmpTableHtml.toString();
  }

  @Override
  public String toString() {
    return build();
  }
}
