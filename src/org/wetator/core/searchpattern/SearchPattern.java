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


package org.wetator.core.searchpattern;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.util.SecretString;

/**
 * The central wildcard handling.
 * This supports the dos wildcards '*' and '?'.
 * 
 * @author rbri
 */
public abstract class SearchPattern {

  private static final String SPECIAL_CHARS = "(){}[]|&~+^-.#@\"<>";

  @SuppressWarnings("unchecked")
  private static Map<String, SearchPattern> searchPatternCache = Collections.synchronizedMap(new LRUMap(500));
  private String originalString;

  /**
   * Construct a new SearchPattern from a list of SecretString's.
   * 
   * @param aSearch the list of SecretString's
   * @param aNumberOfElements the number of elements of the list to be used (from the start of the list)
   * @return the SearchPattern
   */
  public static SearchPattern createFromList(final List<SecretString> aSearch, final int aNumberOfElements) {
    final StringBuilder tmpPattern = new StringBuilder();

    for (int i = 0; i < aNumberOfElements; i++) {
      final String tmpExpectedString = aSearch.get(i).getValue();

      tmpPattern.append("*");
      tmpPattern.append(tmpExpectedString);
    }
    tmpPattern.append("*");

    final SearchPattern tmpSearchPattern = SearchPattern.compile(tmpPattern.toString());
    return tmpSearchPattern;
  }

  /**
   * Construct a new SearchPattern from a list of SecretString's.
   * 
   * @param aSearch the list of SecretString's
   * @return the SearchPattern
   */
  public static SearchPattern createFromList(final List<SecretString> aSearch) {
    return createFromList(aSearch, aSearch.size());
  }

  /**
   * Construct a new SearchPattern from a string.
   * 
   * @param aDosStyleWildcardString the string to construct the SearchPattern for.
   *        This supports the wildcards '*' and '?'.
   * @return the SearchPattern
   */
  public static SearchPattern compile(final String aDosStyleWildcardString) {
    String tmpDosStyleWildcardString = "";
    if (null != aDosStyleWildcardString) {
      tmpDosStyleWildcardString = aDosStyleWildcardString;
    }

    SearchPattern tmpSearchPattern = searchPatternCache.get(tmpDosStyleWildcardString);
    if (tmpSearchPattern != null) {
      return tmpSearchPattern;
    }
    synchronized (searchPatternCache) {
      tmpSearchPattern = searchPatternCache.get(tmpDosStyleWildcardString);
      if (tmpSearchPattern != null) {
        return tmpSearchPattern;
      }

      final String tmpOriginalString = tmpDosStyleWildcardString;

      final StringBuilder tmpPattern = new StringBuilder();
      final StringBuilder tmpTextPattern = new StringBuilder();

      boolean tmpSlash = false;
      boolean tmpIsStarPattern = true;
      boolean tmpIsTextOnly = true;
      for (int i = 0; i < tmpDosStyleWildcardString.length(); i++) {
        final char tmpChar = tmpDosStyleWildcardString.charAt(i);

        if ('*' == tmpChar) {
          if (tmpSlash) {
            tmpPattern.append("\\*");
            tmpTextPattern.append("*");
            tmpSlash = false;
            continue;
          }
          tmpPattern.append(".*");
          tmpIsTextOnly = false;
          continue;
        } else if ('?' == tmpChar) {
          tmpIsStarPattern = false;
          if (tmpSlash) {
            tmpPattern.append("\\?");
            tmpTextPattern.append("?");
            tmpSlash = false;
            continue;
          }
          tmpPattern.append(".");
          tmpIsTextOnly = false;
          continue;
        } else if (SPECIAL_CHARS.indexOf(tmpChar) > -1) {
          tmpIsStarPattern = false;
          if (tmpSlash) {
            tmpPattern.append("\\\\\\");
            tmpPattern.append(tmpChar);
            tmpTextPattern.append('\\');
            tmpTextPattern.append(tmpChar);
            tmpSlash = false;
            continue;
          }
          tmpPattern.append("\\");
          tmpPattern.append(tmpChar);
          tmpTextPattern.append(tmpChar);
          continue;
        } else if ('\\' == tmpChar) {
          tmpSlash = true;
          continue;
        } else {
          tmpIsStarPattern = false;
          if (tmpSlash) {
            tmpPattern.append("\\\\");
            tmpTextPattern.append('\\');
            tmpSlash = false;
          }
          tmpPattern.append(tmpChar);
          tmpTextPattern.append(tmpChar);
          continue;
        }
      }
      if (tmpSlash) {
        tmpPattern.append("\\\\");
        tmpTextPattern.append('\\');
      }

      if (tmpIsStarPattern) {
        tmpSearchPattern = new MatchAllSearchPattern();
      } else if (tmpIsTextOnly) {
        tmpSearchPattern = new TextOnlySearchPattern(tmpOriginalString, tmpTextPattern.toString());
      } else {
        tmpSearchPattern = new RegExpSearchPattern(tmpOriginalString, tmpPattern.toString());
      }
      searchPatternCache.put(aDosStyleWildcardString, tmpSearchPattern);
    }
    return tmpSearchPattern;
  }

  /**
   * Constructor
   * 
   * @param anOriginalString the string used to construct the pattern
   */
  protected SearchPattern(final String anOriginalString) {
    super();
    originalString = anOriginalString;
  }

  public abstract FindSpot firstOccurenceIn(String aString);

  public abstract FindSpot firstOccurenceIn(String aString, int aStartPos);

  public abstract FindSpot lastOccurenceIn(String aString);

  /**
   * Calculates the number of chars before the
   * last occurrence of this search pattern in
   * the given string.<br>
   * If this search pattern is left truncated (star at
   * start), then this returns zero.
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public abstract int noOfCharsBeforeLastOccurenceIn(String aString);

  /**
   * Calculates the number of chars after the
   * last occurrence of this search pattern in
   * the given string.<br>
   * If this search pattern is right truncated (star at
   * end), then this returns zero.
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public abstract int noOfCharsAfterLastOccurenceIn(String aString);

  /**
   * Returns true if the patterns matches the whole string
   * 
   * @param aString the String to match with
   * @return true or false
   */
  public abstract boolean matches(String aString);

  /**
   * Returns true, if the Strings ends with this pattern.
   * 
   * @param aString the string to match
   * @return true or false
   */
  public abstract boolean matchesAtEnd(String aString);

  /**
   * Calculates the sum of the number of chars before
   * and after the match of this search pattern
   * in the given string.<br>
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public abstract int noOfSurroundingCharsIn(String aString);

  /**
   * Getter for attribute originalString
   * 
   * @return the value of attribute originalString
   */
  public String getOriginalString() {
    return originalString;
  }

  @Override
  public int hashCode() {
    final int tmpPrime = 31;
    int tmpResult = 1;
    tmpResult = tmpPrime * tmpResult;
    if (originalString != null) {
      tmpResult = tmpResult + originalString.hashCode();
    }
    return tmpResult;
  }

  @Override
  public boolean equals(final Object anObject) {
    if (this == anObject) {
      return true;
    }
    if (anObject == null) {
      return false;
    }

    if (anObject instanceof String) {
      return anObject.equals(originalString);
    }

    if (getClass() != anObject.getClass()) {
      return false;
    }

    final SearchPattern tmpOther = (SearchPattern) anObject;
    if (originalString == null) {
      if (tmpOther.originalString != null) {
        return false;
      }
    } else if (!originalString.equals(tmpOther.originalString)) {
      return false;
    }
    return true;
  }
}
