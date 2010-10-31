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


package org.rbri.wet.core.searchpattern;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.htmlunit.util.FindSpot;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * The wildcard handling based on automaton regexp
 * 
 * @author rbri
 */
final class RegExpSearchPattern extends SearchPattern {
  private static long constructor;
  private static long noOfCharsBeforeLastOccurenceIn;
  private static long noOfCharsAfterLastOccurenceIn;
  private static long matches;
  private static long noOfSurroundingCharsIn;
  private static long matchesAtEnd;

  private static long firstOccurenceIn;
  private static long lastOccurenceIn;

  /**
   * Helper to print the number of calls for the methods to stdout.
   */
  public static void dumpStatistics() {
    System.out.println("constructor: " + constructor);
    System.out.println("noOfCharsBeforeLastOccurenceIn: " + noOfCharsBeforeLastOccurenceIn);
    System.out.println("noOfCharsAfterLastOccurenceIn: " + noOfCharsAfterLastOccurenceIn);
    System.out.println("matches: " + matches);
    System.out.println("matchesAtEnd: " + matchesAtEnd);
    System.out.println("noOfSurroundingCharsIn: " + noOfSurroundingCharsIn);
    System.out.println();
    System.out.println("firstOccurenceIn: " + firstOccurenceIn);
    System.out.println("lastOccurenceIn: " + lastOccurenceIn);
  }

  private String patternString;
  private RunAutomaton runAutomaton;
  private int minLength;

  /**
   * Constructor
   * 
   * @param anOriginalString the string used to construct the pattern
   * @param aPatternString the compiled patter used by the automaton
   */
  protected RegExpSearchPattern(String anOriginalString, String aPatternString) {
    super(anOriginalString);

    patternString = aPatternString;

    Automaton tmpAutomaton = new RegExp(patternString).toAutomaton();
    minLength = tmpAutomaton.getShortestExample(true).length();
    runAutomaton = new RunAutomaton(tmpAutomaton);
  }

  @Override
  public FindSpot firstOccurenceIn(String aString) {
    return firstOccurenceIn(aString, 0);
  }

  @Override
  public FindSpot firstOccurenceIn(String aString, int aStartPos) {
    firstOccurenceIn++;
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return null;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, aStartPos, runAutomaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return null;
    }

    tmpResult.startPos = tmpMatcher.start();
    tmpResult.endPos = tmpMatcher.end();

    return tmpResult;
  }

  @Override
  public FindSpot lastOccurenceIn(String aString) {
    lastOccurenceIn++;
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return null;
    }

    AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, runAutomaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return null;
    }

    tmpResult.startPos = tmpMatcher.start();
    tmpResult.endPos = tmpMatcher.end();

    return tmpResult;
  }

  @Override
  public int noOfCharsBeforeLastOccurenceIn(String aString) {
    noOfCharsBeforeLastOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, runAutomaton);

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

  @Override
  public int noOfCharsAfterLastOccurenceIn(String aString) {
    noOfCharsAfterLastOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, runAutomaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = aString.length() - tmpMatcher.end();
    return tmpResult;
  }

  @Override
  public boolean matches(String aString) {
    matches++;
    if (null == aString) {
      return false;
    }

    if (aString.length() < minLength) {
      return false;
    }

    return runAutomaton.run(aString);
  }

  @Override
  public boolean matchesAtEnd(String aString) {
    matchesAtEnd++;
    if (StringUtils.isEmpty(aString)) {
      return false;
    }

    if (aString.length() < minLength) {
      return false;
    }

    AutomatonFromEndMatcher tmpMatcher = new AutomatonFromEndMatcher(aString, minLength, runAutomaton);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return false;
    }
    return aString.length() == tmpMatcher.end();
  }

  @Override
  public int noOfSurroundingCharsIn(String aString) {
    noOfSurroundingCharsIn++;
    if (null == aString) {
      return -1;
    }

    AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, runAutomaton);

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

  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [regexp: '" + patternString + "']";
  }
}
