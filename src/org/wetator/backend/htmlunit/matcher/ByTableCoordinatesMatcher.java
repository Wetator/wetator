/*
 * Copyright (c) 2008-2015 wetator.org
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

import java.util.LinkedList;
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
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or null if no path given
   * @param aPathSpot the {@link FindSpot} the path was found first or null if no path given
   * @param aTableCoordinates the {@link TableCoordinate}s to match
   * @param aClass the class of the element to find
   */
  public ByTableCoordinatesMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final List<TableCoordinate> aTableCoordinates, final Class<? extends HtmlElement> aClass) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, null);

    tableCoordinates = aTableCoordinates;
    clazz = aClass;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    final List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    if (!clazz.isAssignableFrom(aHtmlElement.getClass())) {
      return tmpMatches;
    }

    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return tmpMatches;
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
      tmpMatches.add(new MatchResult(aHtmlElement, FoundType.BY_TABLE_COORDINATE, 0, tmpDistance, tmpNodeSpot
          .getStartPos()));
    }
    return tmpMatches;
  }

  /**
   * Checks if the given {@link HtmlElement} is found within the given {@link TableCoordinate}s.
   * 
   * @param aHtmlElement the {@link HtmlElement} to check
   * @param aTableCoordinates the {@link TableCoordinate}s to search for
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the check is based on
   * @param aPathSpot the {@link FindSpot} the path was found first or null if no path given
   * @return true if the given element is found
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
        final HtmlTableRow tmpRow = tmpCell.getEnclosingRow();
        final HtmlTable tmpTable = tmpRow.getEnclosingTable();

        // check the x coordinate in the row
        if (!tmpFoundX && tmpSearchPatternCoordX != null) {
          final int tmpXStart = findCellInRow(tmpRow, tmpCell);
          final int tmpXEnd = tmpXStart + tmpCell.getColumnSpan();
          for (int i = tmpXStart; i < tmpXEnd; i++) {
            for (int j = 0; j < tmpTable.getRowCount(); j++) {
              final HtmlTableCell tmpOuterCellX = tmpTable.getCellAt(j, i);
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
          final int tmpYStart = findRowInTable(tmpTable, tmpRow);
          int tmpYEnd = tmpYStart + tmpCell.getRowSpan();
          tmpYEnd = Math.min(tmpYEnd, tmpTable.getRowCount());
          for (int i = tmpYStart; i < tmpYEnd; i++) {
            for (int j = 0; j < tmpTable.getRow(i).getCells().size(); j++) {
              final HtmlTableCell tmpOuterCellY = tmpTable.getCellAt(i, j);
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

  private static int findCellInRow(final HtmlTableRow aRow, final HtmlTableCell aCell) {
    int i = 0;
    for (final HtmlTableCell tmpCell : aRow.getCells()) {
      if (tmpCell == aCell) {
        return i;
      }
      i++;
    }
    return -1;
  }

  private static int findRowInTable(final HtmlTable aTable, final HtmlTableRow aRow) {
    int i = 0;
    for (final HtmlTableRow tmpRow : aTable.getRows()) {
      if (tmpRow == aRow) {
        return i;
      }
      i++;
    }
    return -1;
  }
}
