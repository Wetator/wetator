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


package org.wetator.util;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.AssertionException;
import org.wetator.i18n.Messages;

/**
 * A small set of assert methods.
 *
 * @author rbri
 * @author frank.danek
 */
public final class Assert {

  /** The marker for more content. */
  private static final String MORE_MARKER = "...";

  /**
   * This class should not be instantiated.
   */
  private Assert() {
    // nothing
  }

  /**
   * Throws an AssertionException with the given
   * message.
   *
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException always
   */
  public static void fail(final String aMessageKey, final Object... aParameters) throws AssertionException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameters);
    throw new AssertionException(tmpMessage);
  }

  /**
   * Throws an AssertionException with the given
   * message if the object is null.
   *
   * @param anObject an object to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException always
   */
  public static void assertNotNull(final Object anObject, final String aMessageKey, final Object... aParameters)
      throws AssertionException {
    if (null != anObject) {
      return;
    }
    fail(aMessageKey, aParameters);
  }

  /**
   * Throws an AssertionException with the given
   * message if the value is null or empty.
   *
   * @param aValue a string to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the value is null or empty
   */
  public static void assertNotEmptyOrNull(final String aValue, final String aMessageKey, final Object... aParameters)
      throws AssertionException {
    if (StringUtils.isNotEmpty(aValue)) {
      return;
    }
    fail(aMessageKey, aParameters);
  }

  /**
   * Throws an AssertionException with the given
   * message if the condition is NOT true.
   *
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the condition is NOT true
   */
  public static void assertTrue(final boolean aCondition, final String aMessageKey, final Object... aParameters)
      throws AssertionException {
    if (aCondition) {
      return;
    }
    fail(aMessageKey, aParameters);
  }

  /**
   * Throws an AssertionException with the given
   * message if the condition is NOT false.
   *
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the condition is NOT false
   */
  public static void assertFalse(final boolean aCondition, final String aMessageKey, final Object... aParameters)
      throws AssertionException {
    if (!aCondition) {
      return;
    }
    fail(aMessageKey, aParameters);
  }

  /**
   * Asserts that two booleans are equal.
   * Otherwise throws an AssertionException.
   *
   * @param anExpectedBoolean a boolean to check
   * @param aCurrentBoolean a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the to booleans are not the same
   */
  public static void assertEquals(final boolean anExpectedBoolean, final boolean aCurrentBoolean,
      final String aMessageKey, final Object... aParameters) throws AssertionException {
    if (anExpectedBoolean == aCurrentBoolean) {
      return;
    }
    fail(aMessageKey, aParameters);
  }

  /**
   * Asserts that two Strings are equal.
   * Otherwise throws an AssertionException.
   *
   * @param anExpectedString a String to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertEquals(final String anExpectedString, final String aCurrentString, final String aMessageKey,
      final Object... aParameters) throws AssertionException {
    if (anExpectedString == null && aCurrentString == null) {
      return;
    }

    if (anExpectedString != null && anExpectedString.equals(aCurrentString)) {
      return;
    }

    // @formatter:off
    final StringBuilder tmpMessage = new StringBuilder(Messages.getMessage(aMessageKey, aParameters))
        .append(' ')
        .append(constructComparisonMessage(anExpectedString, aCurrentString));
    // @formatter:on
    throw new AssertionException(tmpMessage.toString());
  }

  /**
   * Asserts that a SecretString and a String are equal.
   * Otherwise throws an AssertionException.
   *
   * @param anExpectedString a SecretString to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertEquals(final SecretString anExpectedString, final String aCurrentString,
      final String aMessageKey, final Object... aParameters) throws AssertionException {
    if ((anExpectedString == null || anExpectedString.getValue() == null) && aCurrentString == null) {
      return;
    }

    if (anExpectedString != null && anExpectedString.getValue() != null
        && anExpectedString.getValue().equals(aCurrentString)) {
      return;
    }

    final StringBuilder tmpMessage = new StringBuilder(Messages.getMessage(aMessageKey, aParameters));
    final String tmpExpected;
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

    tmpMessage.append(' ').append(constructComparisonMessage(tmpExpected, tmpCurrent));
    throw new AssertionException(tmpMessage.toString());
  }

  /**
   * Returns "..." in place of common prefix and "..." in
   * place of common suffix between expected and actual.
   */
  private static String constructComparisonMessage(final String anExpectedString, final String aCurrentString) {
    if (anExpectedString == null || aCurrentString == null) {
      return Messages.getMessage("assertExpectedActual", anExpectedString, aCurrentString);
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

    // equal strings
    if (j < i && k < i) {
      return Messages.getMessage("assertExpectedActual", anExpectedString, aCurrentString);
    }

    final StringBuilder tmpExpected = new StringBuilder();
    final StringBuilder tmpCurrent = new StringBuilder();

    int tmpFrom = 0;
    if (i > 4) {
      tmpExpected.append(MORE_MARKER);
      tmpCurrent.append(MORE_MARKER);
      tmpFrom = i;
    }

    if (j + 5 < tmpEnd && k + 5 < tmpEnd) {
      tmpExpected.append(anExpectedString.substring(tmpFrom, j + 1));
      tmpCurrent.append(aCurrentString.substring(tmpFrom, k + 1));

      tmpExpected.append(MORE_MARKER);
      tmpCurrent.append(MORE_MARKER);
    } else {
      tmpExpected.append(anExpectedString.substring(tmpFrom));
      tmpCurrent.append(aCurrentString.substring(tmpFrom));
    }
    return Messages.getMessage("assertExpectedActual", tmpExpected.toString(), tmpCurrent.toString());
  }

  /**
   * Asserts that two Strings are matching.
   * This supports DOS style wildcards.
   * Otherwise throws an AssertionException.
   *
   * @param anExpectedPattern a String to check including '*' as wildcard
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameters the parameters
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertMatch(final String anExpectedPattern, final String aCurrentString, final String aMessageKey,
      final Object... aParameters) throws AssertionException {
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

    // @formatter:off
    final StringBuilder tmpMessage = new StringBuilder(Messages.getMessage(aMessageKey, aParameters))
        .append(' ')
        .append(constructComparisonMessage(anExpectedPattern, aCurrentString));
    // @formatter:on
    throw new AssertionException(tmpMessage.toString());
  }
}