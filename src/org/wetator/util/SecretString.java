/*
 * Copyright (c) 2008-2018 wetator.org
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Variable;
import org.wetator.core.searchpattern.SearchPattern;

/**
 * An object for storing strings that may contains secret parts.<br>
 * These secret parts are readable in the {@link #getValue() value} but are {@link #SECRET_PRINT masked} in the
 * {@link #toString() print-out}.
 *
 * @author rbri
 * @author frank.danek
 */
public final class SecretString {

  /** The replacement for printing the secret parts. */
  public static final String SECRET_PRINT = "****";

  private static final String VAR_START_SEQ = "${";
  private static final String VAR_END_SEQ = "}";

  private String value;
  private List<FindSpot> secrets;

  /**
   * Constructor.
   */
  public SecretString() {
    super();
    value = "";
    secrets = new LinkedList<>();
  }

  /**
   * Constructor.
   *
   * @param aPublicText the text of this {@link SecretString}
   */
  public SecretString(final String aPublicText) {
    this();

    this.append(aPublicText);
  }

  /**
   * Appends the given text.
   *
   * @param aPublicText the text to append
   * @return a reference to this {@link SecretString}
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
   * @return a reference to this {@link SecretString}
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
   * Prefixes the value and the print-out with the given text.
   *
   * @param aPublicText the text to use as prefix
   * @return a reference to this {@link SecretString}
   */
  public SecretString prefixWith(final String aPublicText) {
    if (StringUtils.isNotEmpty(aPublicText)) {
      value = aPublicText + value;
      moveSecrets(aPublicText.length(), 0);
    }
    return this;
  }

  /**
   * Removes any leading and trailing whitespace from the value and the print-out of this {@link SecretString}.<br>
   * The {@link SecretString} returned is NOT a new {@link SecretString} but THIS {@link SecretString}.
   *
   * @return a reference to this {@link SecretString}
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
      secrets = new LinkedList<>();
      return this;
    }

    value = value.substring(tmpStart, tmpEnd + 1);
    moveSecrets(tmpStart * -1, 0);

    return this;
  }

  /**
   * Returns a new {@link SecretString} that is a substring of this {@link SecretString}. The substring begins with the
   * character at the specified <code>beginIndex</code> and extends to the end of this {@link SecretString}.
   *
   * @param aBeginIndex the beginning index, inclusive
   * @return the specified substring
   * @exception IndexOutOfBoundsException if the <code>beginIndex</code> is negative or larger than the length of this
   *            {@link SecretString}
   */
  public SecretString substring(final int aBeginIndex) {
    return substring(aBeginIndex, length());
  }

  /**
   * Returns a new {@link SecretString} that is a substring of this {@link SecretString}. The substring begins with the
   * character at the specified <code>beginIndex</code> and extends to the character at index <code>endIndex - 1</code>.
   * Thus the length of the substring is <code>endIndex-beginIndex</code>.
   *
   * @param aBeginIndex the beginning index, inclusive
   * @param anEndIndex the ending index, exclusive
   * @return the specified substring
   * @exception IndexOutOfBoundsException if the <code>beginIndex</code> is negative, or <code>endIndex</code> is larger
   *            than the length of this {@link SecretString}, or <code>beginIndex</code> is larger than
   *            <code>endIndex</code>.
   */
  public SecretString substring(final int aBeginIndex, final int anEndIndex) {
    final SecretString tmpResult = new SecretString(value.substring(aBeginIndex, anEndIndex));
    secrets.forEach(f -> tmpResult.secrets.add(new FindSpot(f)));
    tmpResult.moveSecrets(aBeginIndex * -1, 0);

    return tmpResult;
  }

  /**
   * Returns <code>true</code> if the value starts with the given prefix or the given prefix is an empty string.
   *
   * @param aPrefix the prefix
   * @return <code>true</code> if the value starts with the given prefix; <code>false</code> otherwise
   */
  public boolean startsWith(final String aPrefix) {
    return value.startsWith(aPrefix);
  }

  /**
   * Returns <code>true</code> if the value starts with the given prefix at the given offset or the given prefix is an
   * empty string.
   *
   * @param aPrefix the prefix
   * @param anOffset the offset to start checking
   * @return <code>true</code> if the value starts with the given prefix at the given offset; <code>false</code>
   *         otherwise
   */
  public boolean startsWith(final String aPrefix, final int anOffset) {
    return value.startsWith(aPrefix, anOffset);
  }

  /**
   * Returns <code>true</code> if the value ends with the given suffix or the given prefix is an empty string.
   *
   * @param aSuffix the suffix
   * @return <code>true</code> if the value ends with the given suffix; <code>false</code> otherwise
   */
  public boolean endsWith(final String aSuffix) {
    return value.endsWith(aSuffix);
  }

  /**
   * Returns <code>true</code> if and only if this {@link SecretString} contains the given {@link CharSequence}.
   *
   * @param aPart the {@link CharSequence} to search for
   * @return <code>true</code> if the given {@link CharSequence} is contained; <code>false</code> otherwise
   */
  public boolean contains(final CharSequence aPart) {
    return value.contains(aPart);
  }

  /**
   * Splits this {@link SecretString} around the matches of the given delimiter.
   *
   * @param aDelimiter the delimiting string
   * @param anEscapeChar an optional character that can be used as an escape character inside receiver to make delimiter
   *        characters ignored. Specify -1 here to not use escape characters.
   * @return the list of {@link SecretString}s computed by splitting this {@link SecretString} around matches of the
   *         given delimiter
   */
  public List<SecretString> split(final String aDelimiter, final int anEscapeChar) {
    final int tmpDelimiterSize = aDelimiter.length();
    if (tmpDelimiterSize == 0) {
      throw new IllegalArgumentException("Delimiter must not be an empty string.");
    }
    if (tmpDelimiterSize == 1 && aDelimiter.charAt(0) == anEscapeChar) {
      throw new IllegalArgumentException("Delimiter must be different from escape char.");
    }

    final List<SecretString> tmpResult = new LinkedList<>();

    final int tmpSize = length();
    if (tmpSize < tmpDelimiterSize) {
      return tmpResult;
    }

    int tmpStartPos = 0;
    int tmpSplitPos = value.indexOf(aDelimiter);
    final List<Integer> tmpEscPos = new LinkedList<>();
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
    for (final Integer tmpEscPos : anEscPosList) {
      final int tmpPos = tmpEscPos.intValue();
      tmpBuilder.replace(tmpPos, tmpPos + 1, "");
      moveSecrets(-1, tmpPos);
    }
    value = tmpBuilder.toString();
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

      if (0 > tmpSpot.getEndPos() || tmpSpot.getEndPos() <= tmpSpot.getStartPos()) {
        tmpSecrets.remove();
      }
    }
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
   * @return {@code true} if this is empty or null
   */
  public boolean isEmpty() {
    return StringUtils.isEmpty(value);
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
   * @param aVariables a list of variables
   * @return the {@link SecretString} (as the result of the replacement)
   */
  public SecretString replaceVariables(final List<Variable> aVariables) {
    if (null == aVariables || aVariables.isEmpty()) {
      return this;
    }

    int tmpLoops = 0;
    int tmpReplace = replace(aVariables, value.length());
    while (tmpReplace > -1 && tmpLoops < 4444) {
      tmpLoops++;
      tmpReplace = replace(aVariables, tmpReplace);
    }
    if (tmpLoops >= 4444) {
      throw new IllegalArgumentException("Recursion during variable replacement (" + toString() + ").");
    }
    Collections.sort(secrets, (aFindSpot1, aFindSpot2) -> aFindSpot1.getStartPos() - aFindSpot2.getStartPos());

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
        for (final Iterator<FindSpot> tmpSecretsIterator = secrets.iterator(); tmpSecretsIterator.hasNext();) {
          final FindSpot tmpSpot = tmpSecretsIterator.next();
          if (tmpSpot.getStartPos() <= tmpVarStartPos && tmpVarEndPos <= tmpSpot.getEndPos()) {
            // whole replace area was a secret
            tmpSpot.setEndPos(tmpSpot.getEndPos() + tmpOffset);
            tmpEnclosed = true;
          } else if (tmpSpot.getStartPos() > tmpVarStartPos && tmpVarEndPos > tmpSpot.getEndPos()) {
            // whole secret was inside replace area
            tmpSecretsIterator.remove();
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
          for (final FindSpot tmpSpot : tmpVarSecret.secrets) {
            final FindSpot tmpNewSpot = new FindSpot();
            tmpNewSpot.setStartPos(tmpSpot.getStartPos() + tmpVarStartPos);
            tmpNewSpot.setEndPos(tmpSpot.getEndPos() + tmpVarStartPos);

            secrets.add(tmpNewSpot);
          }
        }

        // avoid recursion
        if (!(VAR_START_SEQ + tmpVarName + VAR_END_SEQ).equals(tmpVarSecretValue)) {
          return aFrom;
        }
      }
    }
    return tmpVarStartPos - 1;
  }

  /**
   * Constructs and returns a new {@link SearchPattern} from the value.
   *
   * @return the {@link SearchPattern}
   */
  public SearchPattern getSearchPattern() {
    return SearchPattern.compile(getValue());
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    final StringBuilder tmpResult = new StringBuilder();

    int tmpStart = 0;
    for (final FindSpot tmpSpot : secrets) {
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
}
