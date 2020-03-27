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

/**
 * Builder for HTML code of table elements.<br>
 * <br>
 * Adds <code>onclick</code>-event listeners for all table elements
 * per default. Use {@link #noListen()} to avoid.
 *
 * @author tobwoerk
 */
public class MouseActionHtmlCodeTableBuilder {

  private TableElementType elementType;
  private boolean listen = true;

  private String tableId;
  private String rowId;
  private int columnCount;

  private String content;

  private enum TableElementType {
    TABLE,
    TR
  }

  public static MouseActionHtmlCodeTableBuilder table(final String aTableId,
      final MouseActionHtmlCodeTableBuilder aContent) {
    return table(aTableId, aContent.build());
  }

  public static MouseActionHtmlCodeTableBuilder table(final String aTableId, final String aContent) {
    return table(aTableId).contain(aContent);
  }

  public static MouseActionHtmlCodeTableBuilder table(final String aTableId) {
    return init(TableElementType.TABLE, aTableId);
  }

  public static MouseActionHtmlCodeTableBuilder tr(final String aTableId, final String aRowId, final int aColumnCount) {
    final MouseActionHtmlCodeTableBuilder tmpBuilder = init(TableElementType.TR, aTableId);
    tmpBuilder.rowId = aRowId;
    tmpBuilder.columnCount = aColumnCount;
    return tmpBuilder;
  }

  public MouseActionHtmlCodeTableBuilder noListen() {
    listen = false;
    return this;
  }

  private static MouseActionHtmlCodeTableBuilder init(final TableElementType anElementType, final String anId) {
    final MouseActionHtmlCodeTableBuilder tmpBuilder = new MouseActionHtmlCodeTableBuilder();
    tmpBuilder.elementType = anElementType;
    tmpBuilder.tableId = anId;
    return tmpBuilder;
  }

  private MouseActionHtmlCodeTableBuilder contain(final String aContent) {
    content = aContent;
    return this;
  }

  public String build() {
    switch (elementType) {
      case TABLE:
        final StringBuilder tmpTableHtml = new StringBuilder(MouseActionHtmlCodeCreator.tableStart(tableId, listen));
        if (content != null) {
          tmpTableHtml.append(content);
        }
        tmpTableHtml.append(MouseActionHtmlCodeCreator.tableEnd());
        return tmpTableHtml.toString();
      case TR:
        return MouseActionHtmlCodeCreator.tableRowWithCols(tableId, rowId, columnCount, listen);
      default:
        throw new RuntimeException();
    }
  }

  @Override
  public String toString() {
    return build();
  }
}
