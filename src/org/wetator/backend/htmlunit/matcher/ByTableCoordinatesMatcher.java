/*
 * Copyright (c) 2008-2018 wetator.org
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


package org.wetator.backend.htmlunit.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.wetator.backend.WPath.TableCoordinate;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * This matcher checks if the given element matches the given table coordinates.
 *
 * @author frank.danek
 */
public class ByTableCoordinatesMatcher extends AbstractHtmlUnitElementMatcher {

  private List<TableCoordinate> tableCoordinates;
  private Class<? extends HtmlElement> clazz;

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aTableCoordinates the {@link TableCoordinate}s to match
   * @param aClass the class of the element to find
   */
  public ByTableCoordinatesMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final List<TableCoordinate> aTableCoordinates,
      final Class<? extends HtmlElement> aClass) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, null);

    tableCoordinates = aTableCoordinates;
    clazz = aClass;
  }

  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    if (!clazz.isAssignableFrom(aHtmlElement.getClass())) {
      return Collections.emptyList();
    }

    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return Collections.emptyList();
    }

    // has the node the text before
    final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if ((pathSpot == null || pathSpot.getEndPos() <= tmpNodeSpot.getStartPos())
        && isHtmlElementInTableCoordinates(aHtmlElement, tableCoordinates, htmlPageIndex, pathSpot)) {
      final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
      final int tmpDistance;
      if (pathSearchPattern != null) {
        tmpDistance = pathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
      } else {
        tmpDistance = tmpTextBefore.length();
      }
      return Arrays.asList(
          new MatchResult(aHtmlElement, FoundType.BY_TABLE_COORDINATE, 0, tmpDistance, tmpNodeSpot.getStartPos()));
    }

    return Collections.emptyList();
  }

  /**
   * Checks if the given {@link HtmlElement} is found within the given {@link TableCoordinate}s.
   *
   * @param aHtmlElement the {@link HtmlElement} to check
   * @param aTableCoordinates the {@link TableCoordinate}s to search for
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the check is based on
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @return <code>true</code> if the given element is found
   */
  public static boolean isHtmlElementInTableCoordinates(final HtmlElement aHtmlElement,
      final List<TableCoordinate> aTableCoordinates, final HtmlPageIndex aHtmlPageIndex, final FindSpot aPathSpot) {
    boolean tmpFound = true;
    HtmlElement tmpHtmlElement = aHtmlElement;

    for (final TableCoordinate tmpTableCoordinate : aTableCoordinates) {
      if (!tmpFound) {
        // the last table coordinate was not found and no outer table is left
        break;
      }
      tmpFound = false;

      HtmlTableCell tmpCell = findEnclosingCell(tmpHtmlElement);
      if (tmpCell == null) {
        break;
      }

      SearchPattern tmpSearchPatternCoordX = null;
      SearchPattern tmpSearchPatternCoordY = null;
      if (tmpTableCoordinate.getCoordinateX() != null) {
        tmpSearchPatternCoordX = tmpTableCoordinate.getCoordinateX().getSearchPattern();
      }
      if (tmpTableCoordinate.getCoordinateY() != null) {
        tmpSearchPatternCoordY = tmpTableCoordinate.getCoordinateY().getSearchPattern();
      }

      boolean tmpFoundX = false;
      boolean tmpFoundY = false;
      while (tmpCell != null) {
        HtmlTableRow tmpRow = tmpCell.getEnclosingRow();
        final TableMatrix tmpTableMatrix = new TableMatrix(tmpRow.getEnclosingTable(), tmpCell);

        // check the x coordinate in the row
        if (!tmpFoundX && tmpSearchPatternCoordX != null) {
          final int tmpXStart = tmpTableMatrix.getAnchorColumn();
          final int tmpXEnd = tmpXStart + tmpCell.getColumnSpan();
          for (int col = tmpXStart; col < tmpXEnd; col++) {
            for (int row = 0; row < tmpTableMatrix.getRowCount(); row++) {
              final HtmlTableCell tmpOuterCellX = tmpTableMatrix.getCellAt(col, row);
              if (null != tmpOuterCellX) {
                final FindSpot tmpOuterCellXSpot = aHtmlPageIndex.getPosition(tmpOuterCellX);
                if ((aPathSpot == null || aPathSpot.getEndPos() < tmpOuterCellXSpot.getStartPos())
                    && tmpSearchPatternCoordX.matches(aHtmlPageIndex.getAsText(tmpOuterCellX))) {
                  tmpFoundX = true;
                  break;
                }
              }
            }
            if (tmpFoundX) {
              break;
            }
          }
        }

        // check the y coordinate in the column
        if (!tmpFoundY && tmpSearchPatternCoordY != null) {
          final int tmpYStart = tmpTableMatrix.getAnchorRow();
          int tmpYEnd = tmpYStart + tmpCell.getRowSpan();
          tmpYEnd = Math.min(tmpYEnd, tmpTableMatrix.getRowCount());
          for (int row = tmpYStart; row < tmpYEnd; row++) {
            for (int col = 0; col < tmpTableMatrix.getColCount(row); col++) {
              final HtmlTableCell tmpOuterCellY = tmpTableMatrix.getCellAt(col, row);
              final FindSpot tmpOuterCellYSpot = aHtmlPageIndex.getPosition(tmpOuterCellY);
              if ((aPathSpot == null || aPathSpot.getEndPos() < tmpOuterCellYSpot.getStartPos())
                  && tmpSearchPatternCoordY.matches(aHtmlPageIndex.getAsText(tmpOuterCellY))) {
                tmpFoundY = true;
                break;
              }
            }
            if (tmpFoundY) {
              break;
            }
          }
        }

        if ((tmpFoundX || tmpSearchPatternCoordX == null) && (tmpFoundY || tmpSearchPatternCoordY == null)) {
          // we have found the current table coordinates so continue with the next ones
          tmpFound = true;
          tmpHtmlElement = tmpCell.getEnclosingRow();
          break;
        }

        tmpCell = findEnclosingCell(tmpRow);
      }
    }

    return tmpFound;
  }

  private static HtmlTableCell findEnclosingCell(final HtmlElement aHtmlElement) {
    DomNode tmpParent = aHtmlElement;
    while (tmpParent != null && !(tmpParent instanceof HtmlTableCell)) {
      tmpParent = tmpParent.getParentNode();
    }
    if (tmpParent == null) {
      return null;
    }
    return (HtmlTableCell) tmpParent;
  }

  /**
   * Helper class that stores the table more like a matrix to pre process
   * all the nasty span stuff.
   */
  private static final class TableMatrix {

    private List<List<HtmlTableCell>> rows = new ArrayList<>();
    private int anchorColumn;
    private int anchorRow;

    private TableMatrix(final HtmlTable aTable, final HtmlTableCell anchorCell) {
      final HashMap<Long, HtmlTableCell> occupied = new HashMap<>();
      int row = 0;
      int maxRow = 0;
      int maxCol = 0;

      for (final HtmlTableRow htmlTableRow : aTable.getRows()) {
        List<HtmlTableCell> tmpRowCells = new ArrayList<>();
        rows.add(tmpRowCells);

        final HtmlTableRow.CellIterator tmpCellIterator = htmlTableRow.getCellIterator();
        int col = 0;
        for (final HtmlTableCell cell : tmpCellIterator) {
          HtmlTableCell tmpOccupyingCell = occupied.get(calculateMatrixPos(col, row));
          while (tmpOccupyingCell != null) {
            tmpRowCells.add(tmpOccupyingCell);
            col++;
            tmpOccupyingCell = occupied.get(calculateMatrixPos(col, row));
          }

          tmpRowCells.add(cell);
          if (cell == anchorCell) {
            setAnchorColumn(col);
            setAnchorRow(row);
          }

          if (cell.getRowSpan() > 1 || cell.getColumnSpan() > 1) {
            maxRow = Math.max(maxRow, row + cell.getRowSpan());
            maxCol = Math.max(maxCol, col + cell.getColumnSpan());
            for (int i = 0; i < cell.getRowSpan(); i++) {
              for (int j = 0; j < cell.getColumnSpan(); j++) {
                occupied.put(calculateMatrixPos(col + j, row + i), cell);
              }
            }
          }
          col++;
          maxCol = Math.max(maxCol, col);
        }

        for (; col < maxCol; col++) {
          tmpRowCells.add(occupied.get(calculateMatrixPos(col, row)));
        }

        row++;
        maxRow = Math.max(maxRow, row);
      }

      // maybe we have some overlap
      for (int i = 0; i < maxRow; i++) {
        List<HtmlTableCell> tmpRowCells = new ArrayList<>();
        rows.add(tmpRowCells);
        for (int j = 0; j < maxCol; j++) {
          tmpRowCells.add(occupied.get(calculateMatrixPos(j, i)));
        }
      }
    }

    private long calculateMatrixPos(final int aColumn, final int aRow) {
      return (long) aColumn << 32 | aRow & 0xFFFFFFFFL;
    }

    private HtmlTableCell getCellAt(final int col, final int row) {
      if (row >= rows.size()) {
        return null;
      }

      List<HtmlTableCell> tmpRow = rows.get(row);
      if (col >= tmpRow.size()) {
        return null;
      }

      return tmpRow.get(col);
    }

    private int getRowCount() {
      return rows.size();
    }

    private int getColCount(final int row) {
      if (row >= rows.size()) {
        return 0;
      }

      return rows.get(row).size();
    }

    private int getAnchorColumn() {
      return anchorColumn;
    }

    private void setAnchorColumn(final int col) {
      anchorColumn = col;
    }

    private int getAnchorRow() {
      return anchorRow;
    }

    private void setAnchorRow(final int row) {
      anchorRow = row;
    }
  }
}
