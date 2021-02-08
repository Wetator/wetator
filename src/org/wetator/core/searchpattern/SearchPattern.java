/*
 * Copyright (c) 2008-2021 wetator.org
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
 * The central wildcard handling.<br>
 * This supports the DOS wildcards '*' and '?'.
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
  public static String getStatistics() {
    return searchPatternCache.getStatistics();
  }

  /**
   * Constructs a new {@link SearchPattern} from a list of {@link SecretString}s.
   *
   * @param aSearch the list of {@link SecretString}s
   * @param aNumberOfElements the number of elements of the list to be used (from the start of the list)
   * @return the {@link SearchPattern}
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

    return SearchPattern.compile(tmpPattern.toString());
  }

  /**
   * Constructs a new {@link SearchPattern} from a list of {@link SecretString}s.
   *
   * @param aSearch the list of {@link SecretString}s
   * @return the {@link SearchPattern}
   */
  public static SearchPattern createFromList(final List<SecretString> aSearch) {
    return createFromList(aSearch, aSearch.size());
  }

  /**
   * Constructs a new {@link SearchPattern} from a string.
   *
   * @param aDosStyleWildcardString the string to construct the {@link SearchPattern} from;
   *        It supports the wildcards '*' and '?'.
   * @return the {@link SearchPattern}
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
          tmpIsStarPattern = false;
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
   * @return the minimal length of a string that this {@link SearchPattern} can match
   */
  public abstract int getMinLength();

  /**
   * @param aString the string to match
   * @return <code>true</code> if this {@link SearchPattern} matches the whole given string
   */
  public abstract boolean matches(String aString);

  /**
   * @param aString the string to match
   * @return <code>true</code>, if this {@link SearchPattern} matches the given string's end
   */
  public abstract boolean matchesAtEnd(String aString);

  /**
   * Searches for the first occurrence of this {@link SearchPattern} inside the given string.
   *
   * @param aString the string to search inside
   * @return the {@link FindSpot} of the first occurrence
   */
  public abstract FindSpot firstOccurenceIn(String aString);

  /**
   * Searches for the first occurrence of this {@link SearchPattern} inside the given string starting at the given
   * position.
   *
   * @param aString the string to search inside
   * @param aStartPos the position to start
   * @return the {@link FindSpot} of the first occurrence
   */
  public abstract FindSpot firstOccurenceIn(String aString, int aStartPos);

  /**
   * Searches for the last occurrence of this {@link SearchPattern} inside the given string.
   *
   * @param aString the string to search inside
   * @return the {@link FindSpot} of the last occurrence
   */
  public abstract FindSpot lastOccurenceIn(String aString);

  /**
   * Calculates the number of chars before the last occurrence of this {@link SearchPattern} in the given string.<br>
   * If this {@link SearchPattern} is left truncated (star at start), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsBeforeLastOccurenceIn(String aString);

  /**
   * Calculates the number of chars before the last shortest occurrence of this {@link SearchPattern} in the given
   * string.<br>
   * If this {@link SearchPattern} is left truncated (star at start), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsBeforeLastShortestOccurenceIn(String aString);

  /**
   * Calculates the number of chars after the last occurrence of this {@link SearchPattern} in the given string.<br>
   * If this {@link SearchPattern} is right truncated (star at end), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsAfterLastOccurenceIn(String aString);

  /**
   * Calculates the number of chars after the last shortest occurrence of this {@link SearchPattern} in the given
   * string.<br>
   * If this {@link SearchPattern} is right truncated (star at end), then this returns zero.
   *
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is not found
   */
  public abstract int noOfCharsAfterLastShortestOccurenceIn(String aString);

  /**
   * Calculates the sum of the number of characters before and after the match of this {@link SearchPattern} in the
   * given string.
   *
   * @param aString the string to search inside
   * @return the number of characters or -1 if the pattern is not found
   */
  public abstract int noOfSurroundingCharsIn(String aString);

  /**
   * @return the string used to construct this {@link SearchPattern}
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
      cache = new HashMap<>(anInitialSize);

    }

    /**
     * @param aDosStyleWildcardString the wildcard string
     * @return the cached pattern or <code>null</code> if not found
     */
    public synchronized SearchPattern get(final String aDosStyleWildcardString) {
      final SearchPattern tmpPattern = cache.get(aDosStyleWildcardString);
      if (null != tmpPattern) {
        hitsCount++;
      }
      return tmpPattern;
    }

    /**
     * Adds another entry to the cache.
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
    public synchronized String getStatistics() {
      // @formatter:off
      final StringBuilder tmpResult = new StringBuilder(80) // NOPMD
        .append("SearchPatternCache statistics:\n")
        .append("      Entries: ").append(cache.size()).append('\n')
        .append("      Hits:    ").append(hitsCount);
      // @formatter:on

      return tmpResult.toString();
    }
  }
}
