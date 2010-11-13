/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend;

import java.util.List;

import org.rbri.wet.util.SecretString;

/**
 * A wpath contains the nodes describing the path to a {@link org.rbri.wet.backend.control.Control}.<br/>
 * 
 * @author frank.danek
 */
public class WPath {

  private List<SecretString> pathNodes;

  /**
   * The constructor.
   * 
   * @param aPathNodes the nodes of the path
   */
  public WPath(List<SecretString> aPathNodes) {
    pathNodes = aPathNodes;
  }

  /**
   * @return the pathNodes
   */
  public List<SecretString> getPathNodes() {
    return pathNodes;
  }

  /**
   * @param anIndex the index of the path node to return
   * @return the path node
   */
  public SecretString getNode(int anIndex) {
    return pathNodes.get(anIndex);
  }

  /**
   * @return true if the path contains no nodes
   */
  public boolean isEmpty() {
    return pathNodes.isEmpty();
  }

  /**
   * @return the number of path nodes
   */
  public int size() {
    return pathNodes.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return SecretString.toString(pathNodes);
  }
}
