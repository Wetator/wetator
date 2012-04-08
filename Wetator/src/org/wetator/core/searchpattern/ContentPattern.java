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


package org.wetator.core.searchpattern;

import java.util.LinkedList;
import java.util.List;

import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * A content pattern contains the terms of match operations (AssertContent).<br/>
 * 
 * @author rbri
 */
public class ContentPattern {
  // private static final String NOT_OPERTOR = "~";

  private List<SecretString> rawNodes;
  private List<PatternNode> nodes;

  /**
   * The constructor.
   * 
   * @param anExpectedNodes the nodes expected from the text
   */
  public ContentPattern(final List<SecretString> anExpectedNodes) {
    rawNodes = anExpectedNodes;
    parseNodes();

    // TODO validation
    // not empty
    // at least one positive node is required
  }

  /**
   * Asserts that the given content matches our pattern.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param aContent a String to check
   * @throws AssertionFailedException if the two strings are not the same
   */
  public void matches(final String aContent) throws AssertionFailedException {
    // TODO check for at least one positive match
    privateMatches(aContent);
  }

  private void privateMatches(final String aContent) throws AssertionFailedException {
    int tmpStartPos = 0;
    boolean tmpAssertFailed = false;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (PatternNode tmpNode : nodes) {
      final String tmpExpectedValue = tmpNode.getValue();
      final String tmpExpectedString = tmpNode.toString();

      final SearchPattern tmpPattern = SearchPattern.compile(tmpExpectedValue);

      tmpContent = tmpContent.substring(tmpStartPos);
      final FindSpot tmpFoundSpot = tmpPattern.firstOccurenceIn(tmpContent);

      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(", ");
      }

      if (null == tmpFoundSpot || FindSpot.NOT_FOUND.equals(tmpFoundSpot)) {
        // pattern not found
        tmpAssertFailed = true;

        if (null == tmpPattern.firstOccurenceIn(aContent)) {
          // pattern is not in whole content too
          tmpResultMessage.append("{" + tmpExpectedString + "}");
        } else {
          // pattern is somewhere before one of the previous tokens =>
          // wrong order
          tmpResultMessage.append("[" + tmpExpectedString + "]");
        }
        tmpStartPos = 0;
      } else {
        // pattern found
        tmpResultMessage.append(tmpExpectedString);

        // continue search for other parts from here on
        tmpStartPos = tmpFoundSpot.endPos;
      }
    }

    if (tmpAssertFailed) {
      // TODO maybe we have to limit the length of the content here
      Assert.fail("contentsFailed", new String[] { "{", "}", "[", "]", tmpResultMessage.toString(), aContent });
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return SecretString.toString(rawNodes);
  }

  private void parseNodes() {
    nodes = new LinkedList<PatternNode>();
    for (SecretString tmpNode : rawNodes) {
      nodes.add(new PatternNode(tmpNode));
    }
  }

  /**
   * Internal helper class.<br/>
   */
  final class PatternNode {
    private SecretString value;
    private boolean isNegated;

    /**
     * Constructor.
     * 
     * @param aNode the SecretString this is based on
     */
    public PatternNode(final SecretString aNode) {
      value = aNode;
      // isNegated = aNode.startsWith(NOT_OPERTOR);
    }

    /**
     * @return the value
     */
    public String getValue() {
      return value.getValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return value.toString();
    }

    /**
     * @return the isNegated
     */
    public boolean isNegated() {
      return isNegated;
    }
  }
}
