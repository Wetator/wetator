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

  private String value;
  private String valueForPrint;

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
   * @param aStringWithPlaceholders the string containing the variables to replace
   * @param aVariables a list of variables
   * @return the {@link SecretString} (as the result of the replacement)
   */
  public static SecretString replaceVariables(final String aStringWithPlaceholders, final List<Variable> aVariables) {
    final String tmpResultValue = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, aVariables, false);
    final String tmpResultValueForPrint = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, aVariables,
        true);

    final SecretString tmpResult = new SecretString(tmpResultValue);
    tmpResult.valueForPrint = tmpResultValueForPrint;
    return tmpResult;
  }

  /**
   * Constructor.
   */
  public SecretString() {
    super();
    value = "";
    valueForPrint = "";
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
    value += aPublicText;
    valueForPrint += aPublicText;
    return this;
  }

  /**
   * Appends the given secret text.
   * 
   * @param aSecretText the secret text to append
   * @return the receiver
   */
  public SecretString appendSecret(final String aSecretText) {
    value += aSecretText;
    valueForPrint += SECRET_PRINT;
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
    prefixWith(aValuePrefix, aValuePrefix);
  }

  /**
   * Prefixes the value and the printout with the given string.
   * 
   * @param aValuePrefix the prefix for the value
   * @param aValueForPrintPrefix the prefix for the printout
   */
  public void prefixWith(final String aValuePrefix, final String aValueForPrintPrefix) {
    value = aValuePrefix + value;
    valueForPrint = aValueForPrintPrefix + valueForPrint;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return valueForPrint;
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
    value = value.trim();
    valueForPrint = valueForPrint.trim();

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
    final SecretString tmpResult = new SecretString(value.substring(aBeginIndex));
    tmpResult.valueForPrint = valueForPrint.substring(aBeginIndex);

    return tmpResult;
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
    tmpResult.valueForPrint = valueForPrint.substring(aBeginIndex, anEndIndex);

    return tmpResult;
  }

  /**
   * Splits this string around matches of the given regular expression.
   * 
   * @param aRegex the delimiting regular expression
   * @return the array of strings computed by splitting this string
   *         around matches of the given regular expression
   */
  public SecretString[] split(final String aRegex) {
    final String[] tmpValues = value.split(";");
    final String[] tmpValuesForPrint = valueForPrint.split(";");
    final SecretString[] tmpResult = new SecretString[tmpValues.length];
    for (int i = 0; i < tmpValues.length; i++) {
      final SecretString tmpSecret = new SecretString(tmpValues[i].trim());
      tmpSecret.valueForPrint = tmpValuesForPrint[i].trim();
      tmpResult[i] = tmpSecret;
    }
    return tmpResult;
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

}
