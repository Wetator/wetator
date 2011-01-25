/*
 * Copyright (c) 2008-2011 wetator.org
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
   * @return the constucted string
   */
  public static String toString(final List<SecretString> aSecretStringList) {
    final StringBuilder tmpResult = new StringBuilder();

    boolean tmpIsNotFirst = false;
    for (SecretString tmpSecretString : aSecretStringList) {
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
   * Constructor.
   * 
   * @param aValue the value of the string
   * @param aSecretFlag true of the value is visible in any kind of output
   */
  public SecretString(final String aValue, final boolean aSecretFlag) {
    this(aValue, SECRET_PRINT);

    if (!aSecretFlag) {
      valueForPrint = aValue;
    }
  }

  /**
   * Constructor.
   * 
   * @param aValue the value of the string
   * @param aValueForPrint the string that is visible in any kind of output
   */
  public SecretString(final String aValue, final String aValueForPrint) {
    super();

    value = aValue;
    valueForPrint = aValueForPrint;
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
}
