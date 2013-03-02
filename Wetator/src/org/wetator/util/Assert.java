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


package org.wetator.util;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.AssertionException;
import org.wetator.i18n.Messages;

/**
 * A small set of assert methods.
 * 
 * @author rbri
 */
public final class Assert {

  /** The marker for more content. */
  protected static final String MORE_MARKER = "...";

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
   * @param aParameterArray the parameters as array
   * @throws AssertionException always
   */
  public static void fail(final String aMessageKey, final Object[] aParameterArray) throws AssertionException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    throw new AssertionException(tmpMessage);
  }

  /**
   * Throws an AssertionException with the given
   * message if the object is null.
   * 
   * @param anObject an object to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException always
   */
  public static void assertNotNull(final Object anObject, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionException {
    if (null != anObject) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionException with the given
   * message if the value is null or empty.
   * 
   * @param aValue a string to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the value is null or empty
   */
  public static void assertNotEmptyOrNull(final String aValue, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionException {
    if (StringUtils.isNotEmpty(aValue)) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionException with the given
   * message if the condition is NOT true.
   * 
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the condition is NOT true
   */
  public static void assertTrue(final boolean aCondition, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionException {
    if (aCondition) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Throws an AssertionException with the given
   * message if the condition is NOT false.
   * 
   * @param aCondition a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the condition is NOT false
   */
  public static void assertFalse(final boolean aCondition, final String aMessageKey, final Object[] aParameterArray)
      throws AssertionException {
    if (!aCondition) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Asserts that two booleans are equal.
   * Otherwise throws an AssertionException.
   * 
   * @param anExpectedBoolean a boolean to check
   * @param aCurrentBoolean a boolean to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the to booleans are not the same
   */
  public static void assertEquals(final boolean anExpectedBoolean, final boolean aCurrentBoolean,
      final String aMessageKey, final Object[] aParameterArray) throws AssertionException {
    if (anExpectedBoolean == aCurrentBoolean) {
      return;
    }
    fail(aMessageKey, aParameterArray);
  }

  /**
   * Asserts that two Strings are equal.
   * Otherwise throws an AssertionException.
   * 
   * @param anExpectedString a String to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertEquals(final String anExpectedString, final String aCurrentString, final String aMessageKey,
      final Object[] aParameterArray) throws AssertionException {
    if (anExpectedString == null && aCurrentString == null) {
      return;
    }

    if (anExpectedString != null && anExpectedString.equals(aCurrentString)) {
      return;
    }

    final StringBuilder tmpMessage = new StringBuilder(Messages.getMessage(aMessageKey, aParameterArray));
    tmpMessage.append(' ');
    tmpMessage.append(constructComparisonMessage(anExpectedString, aCurrentString));
    throw new AssertionException(tmpMessage.toString());
  }

  /**
   * Asserts that a SecretString and a String are equal.
   * Otherwise throws an AssertionException.
   * 
   * @param anExpectedString a SecretString to check
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertEquals(final SecretString anExpectedString, final String aCurrentString,
      final String aMessageKey, final Object[] aParameterArray) throws AssertionException {
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

    throw new AssertionException(tmpMessage);
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
   * Asserts that two Strings are matching.
   * This supports dos style wildcards.
   * Otherwise throws an AssertionException.
   * 
   * @param anExpectedPattern a String to check including '*' as wildcard
   * @param aCurrentString a String to check
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws AssertionException if the two strings are not the same
   */
  public static void assertMatch(final String anExpectedPattern, final String aCurrentString, final String aMessageKey,
      final Object[] aParameterArray) throws AssertionException {
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
    throw new AssertionException(tmpMessage);
  }
}