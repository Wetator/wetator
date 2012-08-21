/*
 * Copyright (c) 2008-2012 wetator.org
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

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * A content pattern contains the terms of match operations (AssertContent).<br/>
 * 
 * @author rbri
 */
public class ContentPattern {
  private static final String NOT_OPERTOR = "~";

  private List<SecretString> rawNodes;
  private List<PatternNode> nodes;
  private List<List<PatternNode>> checks;

  /**
   * The constructor.
   * 
   * @param anExpectedNodes the nodes expected from the text
   * @throws InvalidInputException if provided pattern is empty or contains only negated nodes
   */
  public ContentPattern(final List<SecretString> anExpectedNodes) throws InvalidInputException {
    rawNodes = anExpectedNodes;

    // not empty
    if (anExpectedNodes == null || anExpectedNodes.isEmpty()) {
      final String tmpMessage = Messages.getMessage("invalidContentPattern",
          new String[] { SecretString.toString(anExpectedNodes), Messages.getMessage("emptyContentPattern", null) });
      throw new InvalidInputException(tmpMessage);
    }
    parseNodes();

    // create checks
    checks = new LinkedList<List<PatternNode>>();
    constructChecks(0, new LinkedList<PatternNode>());

    // validation
    // at least one positive node is required
    if (checks.get(0).isEmpty()) {
      final String tmpMessage = Messages.getMessage(
          "invalidContentPattern",
          new String[] { SecretString.toString(anExpectedNodes),
              Messages.getMessage("onlyNegatedContentPattern", new String[] { toString() }) });
      throw new InvalidInputException(tmpMessage);
    }
  }

  private void parseNodes() {
    nodes = new LinkedList<PatternNode>();
    for (SecretString tmpNode : rawNodes) {
      nodes.add(new PatternNode(tmpNode));
    }
  }

  private void constructChecks(final int aPos, final List<PatternNode> aNodes) {
    if (aPos >= nodes.size()) {
      checks.add(aNodes);
      return;
    }

    final PatternNode tmpNode = nodes.get(aPos);
    if (tmpNode.isNegated()) {
      final List<PatternNode> tmpNodes = new LinkedList<PatternNode>();
      tmpNodes.addAll(aNodes);
      constructChecks(aPos + 1, tmpNodes);
    }

    aNodes.add(tmpNode);
    constructChecks(aPos + 1, aNodes);
  }

  /**
   * Asserts that the given content matches our pattern.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param aContent a String to check
   * @throws AssertionException if the two strings are not the same
   */
  public void matches(final String aContent) throws AssertionException {
    // first the positive only check
    // if this fails we have no need for check the negative ones also
    final List<PatternNode> tmpNodes = checks.get(0);
    privateMatches(tmpNodes, aContent);

    // if we have negated parts, we have to check these also
    for (int i = checks.size() - 1; i > 0; i--) {
      privateMatchesNegated(checks.get(i), aContent);
    }
  }

  private void privateMatches(final List<PatternNode> aNodes, final String aContent) throws AssertionException {
    int tmpStartPos = 0;
    boolean tmpFailed = false;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (PatternNode tmpNode : nodes) {
      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(", ");
      }

      final String tmpExpectedString = contructExpectedStringOutput(tmpNode.toString());
      if (!aNodes.contains(tmpNode)) {
        tmpResultMessage.append(tmpExpectedString);
        continue;
      }

      final String tmpExpectedValue = tmpNode.getValue();
      final SearchPattern tmpPattern = SearchPattern.compile(tmpExpectedValue);

      tmpContent = tmpContent.substring(tmpStartPos);
      final FindSpot tmpFoundSpot = tmpPattern.firstOccurenceIn(tmpContent);

      if (null == tmpFoundSpot || FindSpot.NOT_FOUND.equals(tmpFoundSpot)) {
        // pattern not found
        tmpFailed = true;

        if (null == tmpPattern.firstOccurenceIn(aContent)) {
          // pattern is not in whole content too
          tmpResultMessage.append("{");
          tmpResultMessage.append(tmpExpectedString);
          tmpResultMessage.append("}");
        } else {
          // pattern is somewhere before one of the previous tokens =>
          // wrong order
          tmpResultMessage.append("[");
          tmpResultMessage.append(tmpExpectedString);
          tmpResultMessage.append("]");
        }
        tmpStartPos = 0;
      } else {
        // pattern found
        tmpResultMessage.append(tmpExpectedString);

        // continue search for other parts from here on
        tmpStartPos = tmpFoundSpot.endPos;
      }
    }

    if (tmpFailed) {
      // TODO maybe we have to limit the length of the content here
      Assert.fail("contentsFailed", new String[] { "{", "}", "[", "]", tmpResultMessage.toString(), aContent });
    }
  }

  private String contructExpectedStringOutput(final String anExpectedString) {
    String tmpResult = StringUtils.replace(anExpectedString, " ", "\u2423 ");
    tmpResult = StringUtils.replace(tmpResult, "\t", "\u2423 ");
    return tmpResult;
  }

  private void privateMatchesNegated(final List<PatternNode> aNodes, final String aContent) throws AssertionException {
    int tmpStartPos = 0;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (PatternNode tmpNode : nodes) {
      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(", ");
      }

      final String tmpExpectedString = tmpNode.toString();
      if (!aNodes.contains(tmpNode)) {
        tmpResultMessage.append(tmpExpectedString);
        continue;
      }

      final String tmpExpectedValue = tmpNode.getValue();
      final SearchPattern tmpPattern = SearchPattern.compile(tmpExpectedValue);

      tmpContent = tmpContent.substring(tmpStartPos);
      final FindSpot tmpFoundSpot = tmpPattern.firstOccurenceIn(tmpContent);

      if (null == tmpFoundSpot || FindSpot.NOT_FOUND.equals(tmpFoundSpot)) {
        return;
      }

      // pattern found
      if (tmpNode.isNegated()) {
        tmpResultMessage.append("{" + tmpExpectedString + "}");
      } else {
        tmpResultMessage.append(tmpExpectedString);
      }

      // continue search for other parts from here on
      tmpStartPos = tmpFoundSpot.endPos;
    }

    // TODO maybe we have to limit the length of the content here
    Assert.fail("contentsFoundButNegated", new String[] { "{", "}", tmpResultMessage.toString(), aContent });
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

  /**
   * Internal helper class.<br/>
   */
  static final class PatternNode implements Cloneable {
    private SecretString value;
    private boolean isNegated;
    private boolean isIgnored;

    /**
     * Constructor.
     * 
     * @param aNode the SecretString this is based on
     */
    public PatternNode(final SecretString aNode) {
      String tmpValue = aNode.getValue();
      if (tmpValue.startsWith(NOT_OPERTOR)) {
        // TODO escaping?
        isNegated = true;
        tmpValue = tmpValue.substring(1);
        String tmpPrintValue = aNode.toString();
        if (tmpPrintValue.startsWith(NOT_OPERTOR)) {
          tmpPrintValue = tmpPrintValue.substring(1);
        }
        value = new SecretString(tmpValue, tmpPrintValue);
        return;
      }

      value = aNode;
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
      if (isNegated) {
        return NOT_OPERTOR + value.toString();
      }
      return value.toString();
    }

    /**
     * @return the isNegated
     */
    public boolean isNegated() {
      return isNegated;
    }

    /**
     * @return the isIgnored
     */
    public boolean isIgnored() {
      return isIgnored;
    }

    /**
     * Set the isIgnored property to true.
     */
    public void setIgnored() {
      isIgnored = true;
    }

    @Override
    public PatternNode clone() throws CloneNotSupportedException {
      return (PatternNode) super.clone();
    }
  }
}
