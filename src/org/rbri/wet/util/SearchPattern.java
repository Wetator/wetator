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
import org.rbri.wet.backend.htmlunit.util.FindSpot;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * The central wildcard handling
 * 
 * @author rbri
 */
public final class SearchPattern {
  private static long constructor;
  private static long noOfCharsBeforeFirstOccurenceIn;
  private static long noOfCharsBeforeLastOccurenceIn;
  private static long noOfCharsAfterLastOccurenceIn;
  private static long matches;
  private static long noOfSurroundingCharsIn;
  private static long noOfMatchingCharsIn;
  private static long matchesAtEnd;
  private static long noOfCharsBeforeFirstOccurenceInAfter;

  private static long firstOccurenceIn;
  private static long lastOccurenceIn;

  public static void dumpStatistics() {
    System.out.println("constructor: " + constructor);
    System.out.println("noOfCharsBeforeFirstOccurenceIn: " + noOfCharsBeforeFirstOccurenceIn);
    System.out.println("noOfCharsBeforeLastOccurenceIn: " + noOfCharsBeforeLastOccurenceIn);
    System.out.println("noOfCharsAfterLastOccurenceIn: " + noOfCharsAfterLastOccurenceIn);
    System.out.println("matches: " + matches);
    System.out.println("noOfSurroundingCharsIn: " + noOfSurroundingCharsIn);
    System.out.println("noOfMatchingCharsIn: " + noOfMatchingCharsIn);
    System.out.println("matchesAtEnd: " + matchesAtEnd);
    System.out.println("noOfCharsBeforeFirstOccurenceInAfter: " + noOfCharsBeforeFirstOccurenceInAfter);
    System.out.println();
    System.out.println("firstOccurenceIn: " + firstOccurenceIn);
    System.out.println("lastOccurenceIn: " + lastOccurenceIn);
  }

  private static final String SPECIAL_CHARS = "(){}[]|&~+^-.#@\"<>";

  private String originalString;
  private String patternString;
  private boolean isStarPattern;
  private RunAutomaton automaton;

  @Override
  public String toString() {
    return "SearchPattern '" + originalString + "' [" + patternString + "]";
  }

  public static SearchPattern createFromList(List<SecretString> aSearch, int aNumberOfElements) {
    StringBuilder tmpPattern = new StringBuilder();

    for (int i = 0; i < aNumberOfElements; i++) {
      String tmpExpectedString = aSearch.get(i).getValue();

      tmpPattern.append("*");
      tmpPattern.append(tmpExpectedString);
    }
    tmpPattern.append("*");

    SearchPattern tmpSearchPattern = new SearchPattern(tmpPattern.toString());
    return tmpSearchPattern;
  }

  public static SearchPattern createFromList(List<SecretString> aSearch) {
    return createFromList(aSearch, aSearch.size());
  }

  public SearchPattern(String aDosStyleWildcardString) {
    super();
    constructor++;

    String tmpDosStyleWildcardString = "";
    if (null != aDosStyleWildcardString) {
      tmpDosStyleWildcardString = aDosStyleWildcardString;
    }

    originalString = tmpDosStyleWildcardString;

    StringBuilder tmpPattern = new StringBuilder();

    boolean tmpSlash = false;
    isStarPattern = true;
    for (int i = 0; i < tmpDosStyleWildcardString.length(); i++) {
      char tmpChar = tmpDosStyleWildcardString.charAt(i);

      if ('*' == tmpChar) {
        if (tmpSlash) {
          tmpPattern.append("\\*");
          tmpSlash = false;
          continue;
        }
        tmpPattern.append(".*");
        continue;
      } else if ('?' == tmpChar) {
        isStarPattern = false;
        if (tmpSlash) {
          tmpPattern.append("\\?");
          tmpSlash = false;
          continue;
        }
        tmpPattern.append(".");
        continue;
      } else if (SPECIAL_CHARS.indexOf(tmpChar) > -1) {
        isStarPattern = false;
        if (tmpSlash) {
          tmpPattern.append("\\\\\\");
          tmpPattern.append(tmpChar);
          tmpSlash = false;
          continue;
        }
        tmpPattern.append("\\");
        tmpPattern.append(tmpChar);
        continue;
      } else if ('\\' == tmpChar) {
        tmpSlash = true;
        continue;
      } else {
        isStarPattern = false;
        if (tmpSlash) {
          tmpPattern.append("\\\\");
          tmpSlash = false;
        }
        tmpPattern.append(tmpChar);
        continue;
      }
    }
    if (tmpSlash) {
      tmpPattern.append("\\\\");
    }

    patternString = tmpPattern.toString();
    automaton = new RunAutomaton(new RegExp(patternString).toAutomaton());
  }

  public FindSpot firstOccurenceIn(String aString) {
    return firstOccurenceIn(aString, 0);

  }

  public FindSpot firstOccurenceIn(String aString, int aStartPos) {
    firstOccurenceIn++;
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (isStarPattern) {
      return tmpResult;
    }

    String tmpString = aString;
    if (aStartPos > 0) {
      tmpString = tmpString.substring(aStartPos);
    }
    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(tmpString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return null;
    }

    tmpResult.startPos = tmpMatcher.start() + aStartPos;
    tmpResult.endPos = tmpMatcher.end() + aStartPos;

    return tmpResult;
  }

  public FindSpot lastOccurenceIn(String aString) {
    lastOccurenceIn++;
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (isStarPattern) {
      return tmpResult;
    }

    AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return null;
    }

    tmpResult.startPos = tmpMatcher.start();
    tmpResult.endPos = tmpMatcher.end();

    return tmpResult;
  }

  /**
   * Calculates the number of chars before the
   * first occurrence of this search pattern in
   * the given string.<br>
   * If this search pattern is left truncated (star at
   * start), then this returns zero.
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public int noOfCharsBeforeFirstOccurenceIn(String aString) {
    noOfCharsBeforeFirstOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (isStarPattern) {
      return 0;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    tmpResult = tmpMatcher.start();
    return tmpResult;
  }

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
  public int noOfCharsBeforeLastOccurenceIn(String aString) {
    noOfCharsBeforeLastOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (isStarPattern) {
      return 0;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    while (tmpFound) {
      tmpResult = tmpMatcher.start();
      tmpFound = tmpMatcher.find();
    }

    return tmpResult;
  }

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
  public int noOfCharsAfterLastOccurenceIn(String aString) {
    noOfCharsAfterLastOccurenceIn++;
    int tmpResult = -1;

    if (isStarPattern) {
      if (StringUtils.isEmpty(aString)) {
        return 0;
      }
      return aString.length();
    }

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = aString.length() - tmpMatcher.end();
    return tmpResult;
  }

  /**
   * Returns true if the patterns matches the whole string
   * 
   * @param aString the String to match with
   * @return true or false
   */
  public boolean matches(String aString) {
    matches++;
    if (isStarPattern) {
      return true;
    }

    if (null == aString) {
      return false;
    }

    return automaton.run(aString);

  }

  /**
   * Calculates the sum of the number of chars before
   * and after the match of this search pattern
   * in the given string.<br>
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public int noOfSurroundingCharsIn(String aString) {
    noOfSurroundingCharsIn++;
    if (isStarPattern) {
      return 0;
    }

    if (null == aString) {
      return -1;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    int tmpResult = Integer.MAX_VALUE;
    // we found something
    while (tmpFound) {
      tmpResult = Math.min(tmpResult, aString.length() - tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }

    return tmpResult;
  }

  /**
   * Calculates the number of chars matching
   * the given string.<br>
   * 
   * @param aString the string to search inside
   * @return the number of chars or -1 if the pattern is
   *         not found
   */
  public int noOfMatchingCharsIn(String aString) {
    noOfMatchingCharsIn++;
    if (isStarPattern) {
      return aString.length();
    }

    if (null == aString) {
      return -1;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    int tmpResult = -1;
    // we found something
    while (tmpFound) {
      tmpResult = Math.max(tmpResult, tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }

    return tmpResult;
  }

  public boolean matchesAtEnd(String aString) {
    matchesAtEnd++;
    if (StringUtils.isEmpty(aString)) {
      return false;
    }

    if (isStarPattern) {
      return false;
    }

    AutomatonFromEndMatcher tmpMatcher = new AutomatonFromEndMatcher(aString, automaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return false;
    }
    return aString.length() == tmpMatcher.end();
  }

  public int noOfCharsBeforeFirstOccurenceInAfter(String aString, int aStartPos) {
    noOfCharsBeforeFirstOccurenceInAfter++;
    if (null == aString) {
      return -1;
    }

    if (aStartPos >= aString.length()) {
      return -1;
    }

    int tmpResult = noOfCharsBeforeFirstOccurenceIn(aString.substring(aStartPos));
    if (tmpResult > -1) {
      return tmpResult + aStartPos;
    }
    return tmpResult;
  }

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
  public boolean equals(Object anObject) {
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

    SearchPattern tmpOther = (SearchPattern) anObject;
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
