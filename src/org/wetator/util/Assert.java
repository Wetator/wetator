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


package org.wetator.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.AssertionFailedException;
import org.wetator.i18n.Messages;

/**
 * A small set of assert methods.
 * 
 * @author rbri
 */
public final class Assert {

  /** the marker for more content */
  protected static final String MORE_MARKER = "...";

  /**
   * This class should not be instantiated.
   */
  private Assert() {
    // nothing
  }

  /**
   * Throws an AssertionFailedException with the given
   * message.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException always
   */
  public static void fail(final String aMessageKey, final Object[] aParameterArray) throws AssertionFailedException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    throw new AssertionFailedException(tmpMessage);
  }

  /**
   * Throws an AssertionFailedException with the given
   * message if the object is null.
   * 
   * @param anObject an object to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException always
   */
  public static void assertNotNull(final Object anObject, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionFailedException {
    if (null != anObject) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionFailedException with the given
   * message if the value is null or empty.
   * 
   * @param aValue a string to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the value is null or empty
   */
  public static void assertNotEmptyOrNull(final String aValue, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionFailedException {
    if (StringUtils.isNotEmpty(aValue)) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionFailedException with the given
   * message if the condition is NOT true.
   * 
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the condition is NOT true
   */
  public static void assertTrue(final boolean aCondition, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionFailedException {
    if (aCondition) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionFailedException with the given
   * message if the condition is NOT false.
   * 
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the condition is NOT false
   */
  public static void assertFalse(final boolean aCondition, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionFailedException {
    if (!aCondition) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Asserts that two booleans are equal.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpectedBoolean a boolean to check
   * @param aCurrentBoolean a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the to booleans are not the same
   */
  public static void assertEquals(final boolean anExpectedBoolean, final boolean aCurrentBoolean,
      final String aMessageKey, final Object[] aParameterArray) throws AssertionFailedException {
    if (anExpectedBoolean == aCurrentBoolean) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Asserts that two Strings are equal.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpectedString a String to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the two strings are not the same
   */
  public static void assertEquals(final String anExpectedString, final String aCurrentString, final String aMessageKey,
      final Object[] aParameterArray) throws AssertionFailedException {
    if (anExpectedString == null && aCurrentString == null) {
      return;
    }

    if (anExpectedString != null && anExpectedString.equals(aCurrentString)) {
      return;
    }

    String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    tmpMessage = tmpMessage + " " + constructComparisonMessage(anExpectedString, aCurrentString);
    throw new AssertionFailedException(tmpMessage);
  }

  /**
   * Asserts that a SecretString and a String are equal.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpectedString a SecretString to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the two strings are not the same
   */
  public static void assertEquals(final SecretString anExpectedString, final String aCurrentString,
      final String aMessageKey, final Object[] aParameterArray) throws AssertionFailedException {
    if ((anExpectedString == null || anExpectedString.getValue() == null) && aCurrentString == null) {
      return;
    }

    if (anExpectedString != null && anExpectedString.getValue() != null
        && anExpectedString.getValue().equals(aCurrentString)) {
      return;
    }

    String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    String tmpExpected;
    String tmpCurrent = aCurrentString;
    if (anExpectedString == null) {
      tmpExpected = "null";
    } else {
      if (anExpectedString.getValue() == null) {
        tmpExpected = "null";
        if (tmpCurrent != null && anExpectedString.toString() != null) {
          // secret
          tmpCurrent = SecretString.SECRET_PRINT;
        }
      } else {
        tmpExpected = anExpectedString.toString();
        if (tmpCurrent != null && !anExpectedString.toString().equals(anExpectedString.getValue())) {
          // secret
          tmpCurrent = SecretString.SECRET_PRINT;
        }
      }
    }
    tmpMessage = tmpMessage + " " + constructComparisonMessage(tmpExpected, tmpCurrent);

    throw new AssertionFailedException(tmpMessage);
  }

  /**
   * Returns "..." in place of common prefix and "..." in
   * place of common suffix between expected and actual.
   */
  private static String constructComparisonMessage(final String anExpectedString, final String aCurrentString) {
    if (anExpectedString == null || aCurrentString == null) {
      return Messages.getMessage("assertExpectedActual", new String[] { anExpectedString, aCurrentString });
    }

    final int tmpEnd = Math.min(anExpectedString.length(), aCurrentString.length());

    int i = 0;
    for (; i < tmpEnd; i++) {
      if (anExpectedString.charAt(i) != aCurrentString.charAt(i)) {
        break;
      }
    }
    int j = anExpectedString.length() - 1;
    int k = aCurrentString.length() - 1;

    for (; k >= i && j >= i; k--, j--) {
      if (anExpectedString.charAt(j) != aCurrentString.charAt(k)) {
        break;
      }
    }

    String tmpCurrent;
    String tmpExpected;

    // equal strings
    if (j < i && k < i) {
      tmpExpected = anExpectedString;
      tmpCurrent = aCurrentString;
    } else {
      tmpExpected = anExpectedString.substring(i, j + 1);
      tmpCurrent = aCurrentString.substring(i, k + 1);
      if (i <= tmpEnd && i > 0) {
        tmpExpected = MORE_MARKER + tmpExpected;
        tmpCurrent = MORE_MARKER + tmpCurrent;
      }

      if (j < anExpectedString.length() - 1) {
        tmpExpected = tmpExpected + MORE_MARKER;
      }
      if (k < aCurrentString.length() - 1) {
        tmpCurrent = tmpCurrent + MORE_MARKER;
      }
    }
    // TODO really use anExpectedString and aCurrentString instead of tmpExpected and tmpCurrent
    return Messages.getMessage("assertExpectedActual", new String[] { anExpectedString, aCurrentString });
  }

  /**
   * Asserts that a list of strings is part of the content in the given order.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpected the list of Strings to check
   * @param aContent a String to check
   * @throws AssertionFailedException if the two strings are not the same
   */
  public static void assertListMatch(final List<SecretString> anExpected, final String aContent)
      throws AssertionFailedException {
    // TODO i18n
    int tmpStartPos = 0;
    boolean tmpAssertFailed = false;
    final StringBuilder tmpResultMessage = new StringBuilder();
    String tmpContent = aContent;

    for (SecretString tmpExpceted : anExpected) {
      final String tmpExpectedString = tmpExpceted.getValue();
      final SearchPattern tmpPattern = SearchPattern.compile(tmpExpectedString);

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
          tmpResultMessage.append("{" + tmpExpceted.toString() + "}");
        } else {
          // pattern is somewhere before one of the previous tokens =>
          // wrong order
          tmpResultMessage.append("[" + tmpExpceted.toString() + "]");
        }
        tmpStartPos = 0;
      } else {
        tmpResultMessage.append(tmpExpceted.toString());

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
   * Asserts that two Strings are matching.
   * This supports dos style wildcards.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpectedPattern a String to check including '*' as wildcard
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionFailedException if the two strings are not the same
   */
  public static void assertMatch(final String anExpectedPattern, final String aCurrentString, final String aMessageKey,
      final Object[] aParameterArray) throws AssertionFailedException {
    if (anExpectedPattern == null && aCurrentString == null) {
      return;
    }

    if (anExpectedPattern != null) {
      if (anExpectedPattern.isEmpty()) {
        if (null != aCurrentString && aCurrentString.isEmpty()) {
          return;
        }
      } else {
        final SearchPattern tmpSearchPattern = SearchPattern.compile(anExpectedPattern);
        if (tmpSearchPattern.matches(aCurrentString)) {
          return;
        }
      }
    }

    String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    tmpMessage = tmpMessage + " " + constructComparisonMessage(anExpectedPattern, aCurrentString);
    throw new AssertionFailedException(tmpMessage);
  }
}