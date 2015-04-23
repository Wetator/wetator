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


package org.wetator.jenkins.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to keep track of the current location in the XML tree.<br/>
 * The delimiter between two nodes is '/'.
 * 
 * @author frank.danek
 */
public class Path {

  private static final String DELIMITER = "/";

  private List<String> path = new ArrayList<String>();
  private StringBuilder pathAsString = new StringBuilder();

  /**
   * @param aNode the node to add to the end of the path
   */
  public void push(String aNode) {
    path.add(aNode);
    pathAsString.append(DELIMITER);
    pathAsString.append(aNode);
  }

  /**
   * Removes the last node from the end of the path.
   * 
   * @return the removed node
   */
  public String pop() {
    String tmpNode = path.remove(path.size() - 1);
    pathAsString.delete(pathAsString.length() - tmpNode.length() - DELIMITER.length(), pathAsString.length());
    return tmpNode;
  }

  /**
   * @return true if the path is empty, false otherwise
   */
  public boolean isEmpty() {
    return path.isEmpty();
  }

  /**
   * @param aPath the path to match
   * @return true if the current path matches the given path, false otherwise
   */
  public boolean matches(String aPath) {
    if (aPath == null) {
      return false;
    }
    return pathAsString.toString().equals(aPath);
  }

  /**
   * @param aPath the path to match
   * @return true if the current path starts with the given path, false otherwise
   */
  public boolean startsWith(String aPath) {
    if (aPath == null) {
      return false;
    }
    return pathAsString.toString().startsWith(aPath);
  }

  /**
   * @param aPath the path to match
   * @return true if the current path ends with the given path, false otherwise
   */
  public boolean endsWith(String aPath) {
    if (aPath == null) {
      return false;
    }
    return pathAsString.toString().endsWith(aPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return pathAsString.toString();
  }
}
