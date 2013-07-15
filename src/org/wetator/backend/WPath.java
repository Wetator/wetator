/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wetator.exception.WetatorException;
import org.wetator.util.SecretString;

/**
 * A wpath contains the nodes describing the path to a {@link org.wetator.backend.control.Control}.<br/>
 * 
 * @author frank.danek
 */
public class WPath {

  private List<SecretString> rawPath;
  private List<SecretString> pathNodes = new ArrayList<SecretString>();
  private List<TableCoordinate> tableCoordinates = new ArrayList<TableCoordinate>();
  private List<TableCoordinate> tableCoordinatesReversed;
  private SecretString lastNode;

  /**
   * The constructor.
   * 
   * @param aPathNodes the nodes of the path
   */
  public WPath(final List<SecretString> aPathNodes) {
    rawPath = aPathNodes;
    parseNodes();
  }

  /**
   * @return the rawPath
   */
  public List<SecretString> getRawPath() {
    return rawPath;
  }

  /**
   * @return the pathNodes
   */
  public List<SecretString> getPathNodes() {
    return pathNodes;
  }

  /**
   * @return the tableCoordinates
   */
  public List<TableCoordinate> getTableCoordinates() {
    return tableCoordinates;
  }

  /**
   * @return the tableCoordinatesReversed
   */
  public List<TableCoordinate> getTableCoordinatesReversed() {
    return tableCoordinatesReversed;
  }

  /**
   * @return the lastNode
   */
  public SecretString getLastNode() {
    return lastNode;
  }

  /**
   * @return true if the path contains no nodes
   */
  public boolean isEmpty() {
    return rawPath.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return SecretString.toString(rawPath);
  }

  private void parseNodes() {
    boolean tmpTableCoordinatesFinished = false;
    if (!rawPath.isEmpty()) {
      for (SecretString tmpNode : rawPath.subList(0, rawPath.size() - 1)) {
        if (tmpNode.startsWith("[") && tmpNode.endsWith("]") && !tmpNode.endsWith("\\]")) {
          if (tmpTableCoordinatesFinished) {
            throw new WetatorException("Invalid WPath. Only one group of table coordinates allowed.");
          }
          tableCoordinates.add(new TableCoordinate(tmpNode));
        } else {
          if (!tmpTableCoordinatesFinished && !tableCoordinates.isEmpty()) {
            tmpTableCoordinatesFinished = true;
          }
          pathNodes.add(tmpNode);
        }
      }
      lastNode = rawPath.get(rawPath.size() - 1);
      if (lastNode.startsWith("[") && lastNode.endsWith("]") && !lastNode.endsWith("\\]")) {
        tableCoordinates.add(new TableCoordinate(lastNode));
        lastNode = null;
      }
    }
    tableCoordinatesReversed = new ArrayList<TableCoordinate>(tableCoordinates);
    Collections.reverse(tableCoordinatesReversed);
  }

  /**
   * A parser and container for a table coordinate.<br/>
   * A valid table coordinate
   * <ul>
   * <li>starts with '['</li>
   * <li>may contain an x coordinate</li>
   * <li>may contain a y coordinate prefixed by ';'</li>
   * <li>contains at least one of x coordinate and y coordinate</li>
   * <li>ends with ']'</li>
   * </ul>
   * 
   * @author frank.danek
   */
  public static class TableCoordinate {

    private SecretString coordinateX;
    private SecretString coordinateY;

    /**
     * The constructor.
     * 
     * @param aTableCoordinates the {@link SecretString} containing the table coordinates
     */
    public TableCoordinate(final SecretString aTableCoordinates) {
      String tmpTableCoordinates = aTableCoordinates.getValue();
      String tmpTableCoordinatesString = aTableCoordinates.toString();
      if (!tmpTableCoordinates.startsWith("[") || !tmpTableCoordinates.endsWith("]")) {
        throw new WetatorException(aTableCoordinates.toString() + " is not a valid table coordinate.");
      }
      // cut away [ and ]
      tmpTableCoordinates = tmpTableCoordinates.substring(1, tmpTableCoordinates.length() - 1);
      tmpTableCoordinatesString = tmpTableCoordinatesString.substring(1, tmpTableCoordinatesString.length() - 1);
      if (tmpTableCoordinates.contains(";")) {
        final String[] tmpCoordinates = tmpTableCoordinates.split(";");
        final String[] tmpCoordinatesString = tmpTableCoordinatesString.split(";");
        if (tmpCoordinates.length > 2) {
          throw new WetatorException(aTableCoordinates.toString() + " is not a valid table coordinate.");
        }
        if (!"".equals(tmpCoordinates[0].trim())) {
          coordinateX = new SecretString(tmpCoordinates[0].trim(), tmpCoordinatesString[0].trim());
        }
        coordinateY = new SecretString(tmpCoordinates[1].trim(), tmpCoordinatesString[1].trim());
      } else {
        coordinateX = new SecretString(tmpTableCoordinates.trim(), tmpTableCoordinatesString.trim());
      }
    }

    /**
     * @return the coordinateX
     */
    public SecretString getCoordinateX() {
      return coordinateX;
    }

    /**
     * @return the coordinateY
     */
    public SecretString getCoordinateY() {
      return coordinateY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "[" + coordinateX + ";" + coordinateY + "]";
    }
  }
}
