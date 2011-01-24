/*
 * Copyright (c) 2008-2011 www.wetator.org
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

/**
 * Stores Strings in a normalized way.<br>
 * All whitespace is reduced to one blank;
 * Blanks/whitespace at the start and the end are trimmed.<br>
 * Char(160) is also recognized as whitespace.
 * 
 * @author rbri
 */
public final class NormalizedString {
  private static final String BLANK = " ";

  private StringBuilder content;

  /**
   * Default constructor; creates a new empty NormalizedString
   */
  public NormalizedString() {
    content = new StringBuilder();
  }

  /**
   * Default constructor; creates a new NormalizedString with an initial value
   * 
   * @param aString the initial value
   */
  public NormalizedString(final String aString) {
    this();
    append(aString);
  }

  /**
   * Appends the specified String to this sequence.
   * 
   * @param aString the string to append.
   * @return a reference to this object.
   */
  public NormalizedString append(final String aString) {
    if (null == aString) {
      return this;
    }
    if (aString.length() < 1) {
      return this;
    }

    boolean tmpBlank = (content.length() == 0) || isWhitespace(content.charAt(content.length() - 1));

    for (int i = 0; i < aString.length(); i++) {
      final char tmpChar = aString.charAt(i);
      if (isWhitespace(tmpChar)) {
        if (!tmpBlank) {
          tmpBlank = true;
          // don't use tmpChar here,
          // we replace all whitespace with a blank
          content.append(BLANK);
        }
      } else {
        tmpBlank = false;
        content.append(tmpChar);
      }
    }
    return this;
  }

  /**
   * Returns a new <code>String</code> that contains a subsequence of
   * characters currently contained in this sequence. The
   * substring begins at the specified <code>start</code> and
   * extends to the character at index <code>end - 1</code>.
   * The String has no whitespace at the beginning or the end. So be careful, the length may differ from anEndPos -
   * anStartPos
   * 
   * @param aStartPos The beginning index, inclusive.
   * @param anEndPos The ending index, exclusive.
   * @return The new string.
   * @throws StringIndexOutOfBoundsException if <code>start</code> or <code>end</code> are negative or greater than
   *         <code>length()</code>, or <code>start</code> is
   *         greater than <code>end</code>.
   */
  public String substring(final int aStartPos, final int anEndPos) {
    final int tmpLength = length();

    if (anEndPos > tmpLength) {
      throw new StringIndexOutOfBoundsException("NormalizedString index out of range: " + anEndPos + " lenght: "
          + tmpLength + ".");
    }

    if (tmpLength == 0) {
      return "";
    }

    if (aStartPos == anEndPos) {
      return "";
    }

    int tmpEndPos = anEndPos;
    if (isWhitespace(content.charAt(tmpEndPos - 1))) {
      tmpEndPos--;
    }

    int tmpStartPos = aStartPos;
    if (isWhitespace(content.charAt(tmpStartPos))) {
      tmpStartPos++;
    }

    if (aStartPos == anEndPos) {
      return "";
    }

    return content.substring(tmpStartPos, tmpEndPos);
  }

  /**
   * Returns the length of the normalized String
   * 
   * @return the length
   */
  public int length() {
    int tmpLength = content.length();
    if (tmpLength == 0) {
      return tmpLength;
    }

    if (isWhitespace(content.charAt(content.length() - 1))) {
      tmpLength--;
    }
    return tmpLength;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return substring(0, length());
  }

  private boolean isWhitespace(final char aChar) {
    // char 160 not detected as whitespace by java
    return Character.isWhitespace(aChar) || (char) 160 == aChar;
  }
}
