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


package org.wetator.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.SecretString;

/**
 * A WPath contains the nodes describing the path to a {@link org.wetator.backend.control.IControl}.<br/>
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
   * @throws InvalidInputException in case of an invalid {@link WPath}
   */
  public WPath(final List<SecretString> aPathNodes) throws InvalidInputException {
    if (aPathNodes == null) {
      // TODO i18n
      final String tmpMessage = Messages.getMessage("invalidWPath", new String[] { "null",
          "Invalid WPath. Must not be null." });
      throw new InvalidInputException(tmpMessage);
    }
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

  private void parseNodes() throws InvalidInputException {
    boolean tmpTableCoordinatesFinished = false;
    if (!rawPath.isEmpty()) {
      for (final SecretString tmpNode : rawPath.subList(0, rawPath.size() - 1)) {
        if (tmpNode.startsWith("[") && tmpNode.endsWith("]") && !tmpNode.endsWith("\\]")) {
          if (tmpTableCoordinatesFinished) {
            // TODO i18n
            final String tmpMessage = Messages.getMessage("invalidWPath", new String[] {
                SecretString.toString(rawPath), "Invalid WPath. Only one group of table coordinates allowed." });
            throw new InvalidInputException(tmpMessage);
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
     * @throws InvalidInputException in case of invalid table coordinates
     */
    public TableCoordinate(final SecretString aTableCoordinates) throws InvalidInputException {
      if (!aTableCoordinates.startsWith("[") || !aTableCoordinates.endsWith("]")) {
        throw new InvalidInputException(aTableCoordinates.toString() + " is not a valid table coordinate.");
      }
      // cut away [ and ]
      final SecretString tmpTableCoordinates = aTableCoordinates.substring(1, aTableCoordinates.length() - 1);
      if (tmpTableCoordinates.contains(";")) {
        final List<SecretString> tmpCoordinates = tmpTableCoordinates.split(";");
        if (tmpCoordinates.size() > 2) {
          throw new InvalidInputException(aTableCoordinates.toString() + " is not a valid table coordinate.");
        }
        if (tmpCoordinates.get(0).trim().length() > 0) {
          coordinateX = tmpCoordinates.get(0).trim();
        }
        coordinateY = tmpCoordinates.get(1).trim();
      } else {
        coordinateX = tmpTableCoordinates.trim();
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
