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


package org.wetator.core.searchpattern;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.Assert;
import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

/**
 * A content pattern contains the terms of match operations (AssertContent).
 *
 * @author rbri
 * @author frank.danek
 */
public class ContentPattern {

  /** The delimiter used for content parts. */
  public static final String DELIMITER = ",";
  private static final String NOT_OPERTOR = "~";
  private static final String ESCAPE_CHAR = "\\";

  private SecretString rawNode;
  private List<PatternNode> nodes;
  private List<List<PatternNode>> checks;

  /**
   * The constructor.
   *
   * @param anExpectedNodes the nodes expected from the text
   * @throws InvalidInputException if the provided pattern is empty or contains only negated nodes
   */
  public ContentPattern(final SecretString anExpectedNodes) throws InvalidInputException {
    rawNode = anExpectedNodes;

    // not empty
    if (anExpectedNodes == null || anExpectedNodes.isEmpty()) {
      final String tmpMessage = Messages.getMessage("invalidContentPattern", "",
          Messages.getMessage("emptyContentPattern"));
      throw new InvalidInputException(tmpMessage);
    }
    parseNodes();

    // create checks
    checks = new LinkedList<>();
    constructChecks(0, new LinkedList<PatternNode>());

    // validation
    // at least one positive node is required
    if (checks.get(0).isEmpty()) {
      final String tmpMessage = Messages.getMessage("invalidContentPattern", toString(),
          Messages.getMessage("onlyNegatedContentPattern", toString()));
      throw new InvalidInputException(tmpMessage);
    }
  }

  private void parseNodes() {
    final List<SecretString> tmpParts = rawNode.split(DELIMITER, '\\');
    nodes = new LinkedList<>();
    for (final SecretString tmpNode : tmpParts) {
      nodes.add(new PatternNode(tmpNode.trim()));
    }
  }

  private void constructChecks(final int aPos, final List<PatternNode> aNodes) {
    if (aPos >= nodes.size()) {
      checks.add(aNodes);
      return;
    }

    final PatternNode tmpNode = nodes.get(aPos);
    if (tmpNode.isNegated()) {
      final List<PatternNode> tmpNodes = new LinkedList<>();
      tmpNodes.addAll(aNodes);
      constructChecks(aPos + 1, tmpNodes);
    }

    aNodes.add(tmpNode);
    constructChecks(aPos + 1, aNodes);
  }

  /**
   * Asserts that the given content matches our pattern.
   * Otherwise throws an {@link AssertionException}.
   *
   * @param aContent a String to check
   * @param aMaxLength the maximum length of the content used for the
   *        exception text
   * @throws AssertionException if pattern does not match the given String
   */
  public void matches(final String aContent, final int aMaxLength) throws AssertionException {
    // first the positive only check
    // if this fails we have no need for check the negative ones also
    final List<PatternNode> tmpNodes = checks.get(0);
    privateMatches(tmpNodes, aContent, aMaxLength);

    // if we have negated parts, we have to check these also
    for (int i = checks.size() - 1; i > 0; i--) {
      privateMatchesNegated(checks.get(i), aContent, aMaxLength);
    }
  }

  private void privateMatches(final List<PatternNode> aNodes, final String aContent, final int aMaxLength)
      throws AssertionException {
    int tmpStartPos = 0;
    boolean tmpFailed = false;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (final PatternNode tmpNode : nodes) {
      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(DELIMITER).append(' ');
      }

      final String tmpExpectedString = buildExpectedStringOutput(tmpNode.toString());
      if (!aNodes.contains(tmpNode)) {
        tmpResultMessage.append(tmpExpectedString);
        continue;
      }

      final String tmpExpectedValue = tmpNode.getValue();
      final SearchPattern tmpPattern = SearchPattern.compile(tmpExpectedValue);

      tmpContent = tmpContent.substring(tmpStartPos);
      final FindSpot tmpFoundSpot = tmpPattern.firstOccurenceIn(tmpContent);

      if (null == tmpFoundSpot || FindSpot.NOT_FOUND == tmpFoundSpot) {
        // pattern not found
        tmpFailed = true;

        final FindSpot tmpWholeContentFoundSpot = tmpPattern.firstOccurenceIn(aContent);
        if (null == tmpWholeContentFoundSpot || FindSpot.NOT_FOUND == tmpWholeContentFoundSpot) {
          // pattern is not in whole content too
          tmpResultMessage.append('{');
          tmpResultMessage.append(tmpExpectedString);
          tmpResultMessage.append('}');
        } else {
          // pattern is somewhere before one of the previous tokens =>
          // wrong order
          tmpResultMessage.append('[');
          tmpResultMessage.append(tmpExpectedString);
          tmpResultMessage.append(']');
        }
        tmpStartPos = 0;
      } else {
        // pattern found
        tmpResultMessage.append(tmpExpectedString);

        // continue search for other parts from here on
        tmpStartPos = tmpFoundSpot.getEndPos();
      }
    }

    if (tmpFailed) {
      // limit the length of the content for the error message
      tmpContent = StringUtils.abbreviate(aContent, aMaxLength);
      Assert.fail("contentsFailed", "{", "}", "[", "]", tmpResultMessage.toString(), tmpContent);
    }
  }

  private String buildExpectedStringOutput(final String anExpectedString) {
    String tmpResult = StringUtils.replace(anExpectedString, " ", "\u2423");
    tmpResult = StringUtils.replace(tmpResult, "\t", "\u2423");
    return tmpResult;
  }

  private void privateMatchesNegated(final List<PatternNode> aNodes, final String aContent, final int aMaxLength)
      throws AssertionException {
    int tmpStartPos = 0;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (final PatternNode tmpNode : nodes) {
      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(DELIMITER).append(' ');
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

      if (null == tmpFoundSpot || FindSpot.NOT_FOUND == tmpFoundSpot) {
        return;
      }

      // pattern found
      if (tmpNode.isNegated()) {
        tmpResultMessage.append('{');
        tmpResultMessage.append(tmpExpectedString);
        tmpResultMessage.append('}');
      } else {
        tmpResultMessage.append(tmpExpectedString);
      }

      // continue search for other parts from here on
      tmpStartPos = tmpFoundSpot.getEndPos();
    }

    // limit the length of the content for the error message
    tmpContent = StringUtils.abbreviate(aContent, aMaxLength);
    Assert.fail("contentsFoundButNegated", "{", "}", tmpResultMessage.toString(), tmpContent);
  }

  @Override
  public String toString() {
    return rawNode.toString();
  }

  /**
   * Internal helper class representing one node of a {@link ContentPattern}.
   */
  static final class PatternNode implements Cloneable {

    private SecretString value;
    private boolean negated;
    private boolean negatedEscaped;

    /**
     * Constructor.
     *
     * @param aNode the {@link SecretString} this node is based on
     */
    PatternNode(final SecretString aNode) {
      final String tmpValue = aNode.getValue();
      if (tmpValue.startsWith(NOT_OPERTOR)) {
        negated = true;
        value = aNode.substring(1);
      } else if (tmpValue.startsWith(ESCAPE_CHAR + NOT_OPERTOR)) {
        negatedEscaped = true;
        value = aNode.substring(1);
      } else {
        value = aNode;
      }
    }

    /**
     * @return the value
     */
    public String getValue() {
      return value.getValue();
    }

    /**
     * @return <code>true</code> if this node is negated
     */
    public boolean isNegated() {
      return negated;
    }

    @Override
    public String toString() {
      if (negated) {
        return NOT_OPERTOR + value.toString();
      }
      if (negatedEscaped) {
        return ESCAPE_CHAR + value.toString();
      }
      return value.toString();
    }

    @Override
    public PatternNode clone() {
      try {
        return (PatternNode) super.clone();
      } catch (final CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
