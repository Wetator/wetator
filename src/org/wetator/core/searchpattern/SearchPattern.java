/*
 * Copyright (c) 2008-2016 wetator.org
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

/**
 * The central wildcard handling.<br/>
 * This supports the dos wildcards '*' and '?'.
 *
 * @author rbri
 * @author frank.danek
 */
public abstract class SearchPattern {

  private static final String SPECIAL_CHARS = "(){}[]|&~+^-.#@\"<>";

  private static SearchPatternCache searchPatternCache = new SearchPatternCache(500);
  private String originalString;

  /**
   * @return a string with some statistic info
   */
  public static String getStatics() {
    return searchPatternCache.getStatics();
  }

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

      if (i > 0) {
        tmpPattern.append('*');
      }
      tmpPattern.append(tmpExpectedString);
    }

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
            tmpTextPattern.append('*');
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
            tmpTextPattern.append('?');
            tmpSlash = false;
            continue;
          }
          tmpPattern.append('.');
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
          tmpPattern.append('\\');
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
   * The constructor.
   *
   * @param anOriginalString the string used to construct the pattern
   */
  protected SearchPattern(final String anOriginalString) {
    super();
    originalString = anOriginalString;
  }

  /**
   * Returns true if the patterns matches the whole string.
   *
   * @param aString the String to match with
   * @return true or false
   */
  public abstract boolean matches(String aString);

  /**
   * Returns true, if the string ends with this pattern.
   *
   * @param aString the string to match
   * @return true or false
   */
  public abstract boolean matchesAtEnd(String aString);

  /**
   * Searches for the first occurrence of this search pattern inside the given string.
   *
   * @param aString the string to search inside
   * @return the {@link FindSpot} of the first occurrence
   */
  public abstract FindSpot firstOccurenceIn(String aString);

  /**
   * Searches for the first occurrence of this search pattern inside the given string starting at the given position.
   *
   * @param aString the string to search inside
   * @param aStartPos the position to start
   * @return the {@link FindSpot} of the first occurrence
   */
  public abstract FindSpot firstOccurenceIn(String aString, int aStartPos);

  /**
   * Searches for the last occurrence of this search pattern inside the given string.
   *
   * @param aString the string to search inside
   * @return the {@link FindSpot} of the last occurrence
   */
  public abstract FindSpot lastOccurenceIn(String aString);

  /**
   * Calculates the number of chars before the last occurrence of this search pattern in the given string.<br>
   * If this search pattern is left truncated (star at start), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsBeforeLastOccurenceIn(String aString);

  /**
   * Calculates the number of chars before the last shortest occurrence of this search pattern in the given string.<br>
   * If this search pattern is left truncated (star at start), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsBeforeLastShortestOccurenceIn(String aString);

  /**
   * Calculates the number of chars after the last occurrence of this search pattern in the given string.<br>
   * If this search pattern is right truncated (star at end), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsAfterLastOccurenceIn(String aString);

  /**
   * Calculates the number of chars after the last shortest occurrence of this search pattern in the given string.<br>
   * If this search pattern is right truncated (star at end), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsAfterLastShortestOccurenceIn(String aString);

  /**
   * Calculates the sum of the number of characters before and after the match of this search pattern in the given
   * string.
   *
   * @param aString the string to search inside
   * @return the number of characters or -1 if the pattern is not found
   */
  public abstract int noOfSurroundingCharsIn(String aString);

  /**
   * @return the originalString
   */
  public String getOriginalString() {
    return originalString;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
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

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object anObject) {
    if (this == anObject) {
      return true;
    }
    if (anObject == null) {
      return false;
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

  /**
   * Cache.
   */
  private static class SearchPatternCache {
    private Map<String, SearchPattern> cache;
    private int hitsCount;

    SearchPatternCache(final int anInitialSize) {
      cache = new HashMap<String, SearchPattern>(anInitialSize);

    }

    /**
     * @param aDosStyleWildcardString the wildcard string
     * @return the cached pattern or null if not found
     */
    public synchronized SearchPattern get(final String aDosStyleWildcardString) {
      final SearchPattern tmpPattern = cache.get(aDosStyleWildcardString);
      if (null != tmpPattern) {
        hitsCount++;
      }
      return tmpPattern;
    }

    /**
     * Add another entry to the cache.
     *
     * @param aDosStyleWildcardString the wildcard string
     * @param aSearchPattern the compiled {@link SearchPattern}
     */
    public synchronized void put(final String aDosStyleWildcardString, final SearchPattern aSearchPattern) {
      cache.put(aDosStyleWildcardString, aSearchPattern);
    }

    /**
     * @return a string with some statistic info
     */
    public synchronized String getStatics() {
      final StringBuilder tmpResult = new StringBuilder(80);
      tmpResult.append("SearchPatternCache statistics:\n");

      tmpResult.append("      Entries: ");
      tmpResult.append(cache.size());
      tmpResult.append('\n');

      tmpResult.append("      Hits:    ");
      tmpResult.append(hitsCount);

      return tmpResult.toString();
    }
  }
}
