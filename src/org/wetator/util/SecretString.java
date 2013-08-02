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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Variable;
import org.wetator.core.searchpattern.SearchPattern;

/**
 * An object that stores a variable.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class SecretString {
  /**
   * The replacement for printing secret strings.
   */
  public static final String SECRET_PRINT = "****";
  private static final String VAR_START_SEQ = "${";
  private static final String VAR_END_SEQ = "}";

  private String value;
  private List<FindSpot> secrets;

  /**
   * Constructs a comma separated string from the given list of secret strings.
   * 
   * @param aSecretStringList the input
   * @return the constructed string
   */
  public static String toString(final List<SecretString> aSecretStringList) {
    final StringBuilder tmpResult = new StringBuilder();

    boolean tmpIsNotFirst = false;
    for (final SecretString tmpSecretString : aSecretStringList) {
      if (tmpIsNotFirst) {
        tmpResult.append(", ");
      } else {
        tmpIsNotFirst = true;
      }
      tmpResult.append(tmpSecretString.toString());
    }

    return tmpResult.toString();
  }

  /**
   * @param aVariables a list of variables
   * @return the {@link SecretString} (as the result of the replacement)
   */
  public SecretString replaceVariables(final List<Variable> aVariables) {
    if (null == aVariables || aVariables.isEmpty()) {
      return this;
    }

    int tmpReplace = replace(aVariables, value.length());
    while (tmpReplace > -1) {
      tmpReplace = replace(aVariables, tmpReplace);
    }
    Collections.sort(secrets, new Comparator<FindSpot>() {
      @Override
      public int compare(final FindSpot aO1, final FindSpot aO2) {
        return aO1.getStartPos() - aO2.getStartPos();
      }
    });

    return this;
  }

  private int replace(final List<Variable> aVariables, final int aFrom) {
    final int tmpVarStartPos = value.lastIndexOf(VAR_START_SEQ, aFrom);
    if (tmpVarStartPos < 0) {
      return -1;
    }

    int tmpVarEndPos = value.indexOf(VAR_END_SEQ, tmpVarStartPos);
    if (tmpVarEndPos < 0) {
      return tmpVarStartPos - 1;
    }
    final String tmpVarName = value.substring(tmpVarStartPos + VAR_START_SEQ.length(), tmpVarEndPos);

    tmpVarEndPos++;
    for (final Variable tmpVariable : aVariables) {
      if (tmpVarName.equals(tmpVariable.getName())) {
        final SecretString tmpVarSecret = tmpVariable.getValue();
        final String tmpVarSecretValue = tmpVarSecret.getValue();

        // replace
        value = value.substring(0, tmpVarStartPos) + tmpVarSecretValue + value.substring(tmpVarEndPos);

        final int tmpOffset = tmpVarSecretValue.length() - (tmpVarEndPos - tmpVarStartPos);
        // merge in the secrets
        boolean tmpEnclosed = false;
        for (FindSpot tmpSpot : secrets) {
          if (tmpSpot.getStartPos() <= tmpVarStartPos && tmpVarEndPos <= tmpSpot.getEndPos()) {
            // whole replace area was a secret
            tmpSpot.setEndPos(tmpSpot.getEndPos() + tmpOffset);
            tmpEnclosed = true;
          } else {
            if (tmpVarStartPos < tmpSpot.getStartPos()) {
              tmpSpot.setStartPos(tmpSpot.getStartPos() + tmpOffset);
            }
            if (tmpVarEndPos < tmpSpot.getEndPos()) {
              tmpSpot.setEndPos(tmpSpot.getEndPos() + tmpOffset);
            }
          }
        }
        if (!tmpEnclosed) {
          for (FindSpot tmpSpot : tmpVarSecret.secrets) {
            final FindSpot tmpNewSpot = new FindSpot();
            tmpNewSpot.setStartPos(tmpSpot.getStartPos() + tmpVarStartPos);
            tmpNewSpot.setEndPos(tmpSpot.getEndPos() + tmpVarStartPos);

            secrets.add(tmpNewSpot);
          }
        }

        // done
        return aFrom;
      }
    }
    return tmpVarStartPos - 1;
  }

  /**
   * Constructor.
   */
  public SecretString() {
    super();
    value = "";
    secrets = new LinkedList<FindSpot>();
  }

  /**
   * Constructor.
   * 
   * @param aValue the value of the string
   */
  public SecretString(final String aValue) {
    this();

    this.append(aValue);
  }

  /**
   * Appends the given text.
   * 
   * @param aPublicText the text to append
   * @return the receiver
   */
  public SecretString append(final String aPublicText) {
    if (StringUtils.isNotEmpty(aPublicText)) {
      value += aPublicText;
    }
    return this;
  }

  /**
   * Appends the given secret text.
   * 
   * @param aSecretText the secret text to append
   * @return the receiver
   */
  public SecretString appendSecret(final String aSecretText) {
    if (null != aSecretText) {
      final FindSpot tmpSecretSpot = new FindSpot(value.length(), -1);
      value += aSecretText;
      tmpSecretSpot.setEndPos(value.length());
      secrets.add(tmpSecretSpot);
    }
    return this;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Prefixes the value and the printout with the given string.
   * 
   * @param aValuePrefix the prefix
   */
  public void prefixWith(final String aValuePrefix) {
    if (StringUtils.isNotEmpty(aValuePrefix)) {
      value = aValuePrefix + value;
      moveSecrets(aValuePrefix.length(), 0);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder tmpResult = new StringBuilder();

    int tmpStart = 0;
    for (FindSpot tmpSpot : secrets) {
      tmpResult.append(value.substring(tmpStart, tmpSpot.getStartPos()));
      tmpResult.append(SECRET_PRINT);
      tmpStart = tmpSpot.getEndPos();
    }

    if (tmpStart == 0) {
      tmpResult.append(value);
    } else {
      tmpResult.append(value.substring(tmpStart, value.length()));
    }
    return tmpResult.toString();
  }

  /**
   * Constructs and returns a new search pattern from the value.
   * 
   * @return the search pattern
   */
  public SearchPattern getSearchPattern() {
    return SearchPattern.compile(getValue());
  }

  /**
   * Returns true if a value starts with the given prefix.
   * 
   * @param aPrefix the prefix
   * @return true or false
   */
  public boolean startsWith(final String aPrefix) {
    return value.startsWith(aPrefix);
  }

  /**
   * Returns true if a value starts at offset with the given prefix.
   * 
   * @param aPrefix the prefix
   * @param anOffset the start position
   * @return true or false
   */
  public boolean startsWith(final String aPrefix, final int anOffset) {
    return value.startsWith(aPrefix, anOffset);
  }

  /**
   * Returns true if a value ends with the given suffix.
   * 
   * @param aSuffix the suffix
   * @return true or false
   */
  public boolean endsWith(final String aSuffix) {
    return value.endsWith(aSuffix);
  }

  /**
   * Returns the lower case form of the string.
   * 
   * @param aLocale the locale for the conversion
   * @return the lower case form of the string
   */
  public String toLowerCase(final Locale aLocale) {
    return value.toLowerCase(aLocale);
  }

  /**
   * Trims the value and the print value of this object.<br>
   * this returns NOT a new object
   * 
   * @return this
   */
  public SecretString trim() {
    final int tmpLength = value.length();
    int tmpStart = 0;
    while (tmpStart < tmpLength && Character.isWhitespace(value.charAt(tmpStart))) {
      tmpStart++;
    }
    int tmpEnd = tmpLength - 1;
    while (tmpEnd > tmpStart && Character.isWhitespace(value.charAt(tmpEnd))) {
      tmpEnd--;
    }

    if (tmpEnd < tmpStart) {
      value = "";
      secrets = new LinkedList<FindSpot>();
      return this;
    }

    value = value.substring(tmpStart, tmpEnd + 1);
    moveSecrets(tmpStart * -1, 0);

    return this;
  }

  /**
   * Returns a new string that is a substring of this string. The
   * substring begins with the character at the specified index and
   * extends to the end of this string.
   * 
   * @param aBeginIndex the beginning index, inclusive.
   * @return the specified substring.
   * @exception IndexOutOfBoundsException if <code>beginIndex</code> is negative or larger than the
   *            length of this <code>String</code> object.
   */
  public SecretString substring(final int aBeginIndex) {
    return substring(aBeginIndex, length());
  }

  /**
   * Returns a new string that is a substring of this string. The
   * substring begins at the specified <code>beginIndex</code> and
   * extends to the character at index <code>endIndex - 1</code>.
   * Thus the length of the substring is <code>endIndex-beginIndex</code>.
   * 
   * @param aBeginIndex the beginning index, inclusive.
   * @param anEndIndex the ending index, exclusive.
   * @return the specified substring.
   * @exception IndexOutOfBoundsException if the <code>beginIndex</code> is negative, or <code>endIndex</code> is larger
   *            than the length of
   *            this <code>String</code> object, or <code>beginIndex</code> is larger than <code>endIndex</code>.
   */
  public SecretString substring(final int aBeginIndex, final int anEndIndex) {
    final SecretString tmpResult = new SecretString(value.substring(aBeginIndex, anEndIndex));
    tmpResult.secrets.addAll(secrets);
    tmpResult.moveSecrets(aBeginIndex * -1, 0);

    return tmpResult;
  }

  /**
   * Splits this string around matches of the given delimiter.
   * 
   * @param aDelimiter the delimiting string
   * @param anEscapeChar an optional character, that can be used as an escape
   *        character inside receiver to make delimiter characters ignored.
   *        Specify -1 here to not use escape characters.
   * @return the list of strings computed by splitting this string
   *         around matches of the given delimiter
   */
  public List<SecretString> split(final String aDelimiter, final int anEscapeChar) {
    final int tmpDelimiterSize = aDelimiter.length();
    if (tmpDelimiterSize == 1 && aDelimiter.charAt(0) == anEscapeChar) {
      throw new IllegalArgumentException("Delimiter must be different from escape char.");
    }

    final List<SecretString> tmpResult = new LinkedList<SecretString>();

    final int tmpSize = length();
    if (tmpSize < tmpDelimiterSize) {
      return tmpResult;
    }

    int tmpStartPos = 0;
    int tmpSplitPos = value.indexOf(aDelimiter);
    final List<Integer> tmpEscPos = new LinkedList<Integer>();
    while (tmpSplitPos > -1) {
      if (tmpSplitPos > 0) {
        final char tmpEscape = value.charAt(tmpSplitPos - 1);
        if (tmpEscape == anEscapeChar) {
          tmpEscPos.add(0, tmpSplitPos - tmpStartPos - 1);
          tmpSplitPos = value.indexOf(aDelimiter, tmpSplitPos + tmpDelimiterSize);
          continue;
        }
      }
      final SecretString tmpPart = substring(tmpStartPos, tmpSplitPos);
      tmpPart.removeEsc(tmpEscPos);
      tmpEscPos.clear();
      tmpResult.add(tmpPart);
      tmpStartPos = tmpSplitPos + tmpDelimiterSize;

      tmpSplitPos = value.indexOf(aDelimiter, tmpSplitPos + tmpDelimiterSize);
    }

    final SecretString tmpPart = substring(tmpStartPos, tmpSize);
    tmpPart.removeEsc(tmpEscPos);
    tmpResult.add(tmpPart);
    return tmpResult;
  }

  private void removeEsc(final List<Integer> anEscPosList) {
    final StringBuilder tmpBuilder = new StringBuilder(value);
    for (Integer tmpEscPos : anEscPosList) {
      final int tmpPos = tmpEscPos.intValue();
      tmpBuilder.replace(tmpPos, tmpPos + 1, "");
      moveSecrets(-1, tmpPos);
    }
    value = tmpBuilder.toString();
  }

  /**
   * Returns the length of this string.
   * 
   * @return the length
   */
  public int length() {
    return value.length();
  }

  /**
   * Returns true if and only if this string contains the specified
   * sequence of char values.
   * 
   * @param aPart the sequence to search for
   * @return true if this string contains <code>s</code>, false otherwise
   */
  public boolean contains(final CharSequence aPart) {
    return value.contains(aPart);
  }

  /**
   * @return {@code true} if this is empty or null
   */
  public boolean isEmpty() {
    return StringUtils.isEmpty(value);
  }

  private void moveSecrets(final int aDistance, final int aStartPos) {
    final int tmpLength = value.length();

    final Iterator<FindSpot> tmpSecrets = secrets.iterator();
    while (tmpSecrets.hasNext()) {
      final FindSpot tmpSpot = tmpSecrets.next();

      final int tmpStart = tmpSpot.getStartPos();
      if (aStartPos <= tmpStart) {
        tmpSpot.setStartPos(Math.max(0, tmpStart + aDistance));
      }

      final int tmpEnd = tmpSpot.getEndPos();
      if (aStartPos <= tmpEnd) {
        tmpSpot.setEndPos(Math.min(tmpLength, tmpEnd + aDistance));
      }

      if ((0 > tmpSpot.getEndPos()) || (tmpSpot.getEndPos() <= tmpSpot.getStartPos())) {
        tmpSecrets.remove();
      }
    }
  }
}
