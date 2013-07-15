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


package org.rbri.wet.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.i18n.Messages;

/**
 * A small set of assert methods.
 * 
 * @author rbri
 */
public final class Assert {

  /** the marker for more content */
  protected static final String MORE_MARKER = "...";
  /** the fixed text 'expected: ' */
  protected static final String TEXT_EXPECTED = "expected: "; // TODO i18n
  /** the start merker for values */
  protected static final String LEFT_VALUE_MARKER = "<";
  /** the start merker for values */
  protected static final String RIGHT_VALUE_MARKER = ">";
  /** the fixed text ' but was: ' */
  protected static final String TEXT_WAS = " but was: "; // TODO i18n

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
  public static void fail(String aMessageKey, Object[] aParameterArray) throws AssertionFailedException {
    String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
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
  public static void assertNotNull(Object anObject, String aMessageKey, Object[] aParameterArray)
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
  public static void assertNotEmptyOrNull(String aValue, String aMessageKey, Object[] aParameterArray)
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
  public static void assertTrue(boolean aCondition, String aMessageKey, Object[] aParameterArray)
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
  public static void assertFalse(boolean aCondition, String aMessageKey, Object[] aParameterArray)
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
  public static void assertEquals(boolean anExpectedBoolean, boolean aCurrentBoolean, String aMessageKey,
      Object[] aParameterArray) throws AssertionFailedException {
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
  public static void assertEquals(String anExpectedString, String aCurrentString, String aMessageKey,
      Object[] aParameterArray) throws AssertionFailedException {
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
   * Returns "..." in place of common prefix and "..." in
   * place of common suffix between expected and actual.
   */
  private static String constructComparisonMessage(String anExpectedString, String aCurrentString) {
    if (anExpectedString == null || aCurrentString == null) {
      return TEXT_EXPECTED + LEFT_VALUE_MARKER + anExpectedString + RIGHT_VALUE_MARKER + TEXT_WAS + LEFT_VALUE_MARKER
          + aCurrentString + RIGHT_VALUE_MARKER;
    }

    int tmpEnd = Math.min(anExpectedString.length(), aCurrentString.length());

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
    return TEXT_EXPECTED + LEFT_VALUE_MARKER + anExpectedString + RIGHT_VALUE_MARKER + TEXT_WAS + LEFT_VALUE_MARKER
        + aCurrentString + RIGHT_VALUE_MARKER;
  }

  /**
   * Asserts that a list of strings is part of the content in the given order.
   * Otherwise throws an AssertionFailedException.
   * 
   * @param anExpected the list of Strings to check
   * @param aContent a String to check
   * @throws AssertionFailedException if the two strings are not the same
   */
  public static void assertListMatch(List<SecretString> anExpected, String aContent) throws AssertionFailedException {
    // TODO i18n
    int tmpStartPos = 0;
    boolean tmpAssertFailed = false;
    StringBuilder tmpResultMessage = new StringBuilder();

    for (SecretString tmpExpceted : anExpected) {
      String tmpExpectedString = tmpExpceted.getValue();
      SearchPattern tmpPattern = new SearchPattern(tmpExpectedString);

      int tmpFoundPos = tmpPattern.noOfCharsBeforeFirstOccurenceInAfter(aContent, tmpStartPos);

      if (tmpResultMessage.length() > 0) {
        tmpResultMessage.append(", ");
      }

      if (tmpFoundPos < 0) {
        // pattern not found
        tmpAssertFailed = true;

        if (tmpPattern.noOfMatchingCharsIn(aContent) > -1) {
          // pattern is somewhere before one of the previous tokens =>
          // wrong order
          tmpResultMessage.append("[" + tmpExpceted.toString() + "]");
        } else {
          // pattern is not in whole content too
          tmpResultMessage.append("{" + tmpExpceted.toString() + "}");
        }
      } else {
        tmpResultMessage.append(tmpExpceted.toString());

        // continue search for other parts from here on
        tmpStartPos = tmpFoundPos;
      }
    }

    if (tmpAssertFailed) {
      // TODO maybe we have to limit the length of the content here
      Assert.fail("contentsFailed", new String[] { "{", "}", "[", "]", tmpResultMessage.toString(), aContent });
    }
  }

}