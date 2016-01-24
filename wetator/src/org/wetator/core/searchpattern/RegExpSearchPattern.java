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

import org.apache.commons.lang3.StringUtils;
import org.wetator.util.FindSpot;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * This is the standard implementation of a {@link SearchPattern} based on automaton RegExp.<br/>
 * <b>Notice:</b> the currently used RegExp implementation dk.briks.automaton does not support all functionality of
 * RegExp. But it is fast. ;)
 * 
 * @author rbri
 * @author frank.danek
 */
final class RegExpSearchPattern extends SearchPattern {

  private static long constructor;
  private static long noOfCharsBeforeLastOccurenceIn;
  private static long noOfCharsBeforeLastShortestOccurenceIn;
  private static long noOfCharsAfterLastOccurenceIn;
  private static long noOfCharsAfterLastShortestOccurenceIn;
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
    System.out.println("matches: " + matches);
    System.out.println("matchesAtEnd: " + matchesAtEnd);
    System.out.println();
    System.out.println("firstOccurenceIn: " + firstOccurenceIn);
    System.out.println("lastOccurenceIn: " + lastOccurenceIn);
    System.out.println();
    System.out.println("noOfCharsBeforeLastOccurenceIn: " + noOfCharsBeforeLastOccurenceIn);
    System.out.println("noOfCharsBeforeLastShortestOccurenceIn: " + noOfCharsBeforeLastShortestOccurenceIn);
    System.out.println("noOfCharsAfterLastOccurenceIn: " + noOfCharsAfterLastOccurenceIn);
    System.out.println("noOfCharsAfterLastShortestOccurenceIn: " + noOfCharsAfterLastShortestOccurenceIn);
    System.out.println("noOfSurroundingCharsIn: " + noOfSurroundingCharsIn);
  }

  private String patternString;
  private RunAutomaton runAutomaton;
  private int minLength;

  /**
   * The constructor.
   * 
   * @param anOriginalString the string used to construct the pattern
   * @param aPatternString the compiled pattern used by the automaton
   */
  protected RegExpSearchPattern(final String anOriginalString, final String aPatternString) {
    super(anOriginalString);

    patternString = aPatternString;

    final Automaton tmpAutomaton = new RegExp(patternString).toAutomaton();
    minLength = tmpAutomaton.getShortestExample(true).length();
    runAutomaton = new RunAutomaton(tmpAutomaton);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#matches(java.lang.String)
   */
  @Override
  public boolean matches(final String aString) {
    matches++;
    if (null == aString) {
      return false;
    }

    if (aString.length() < minLength) {
      return false;
    }

    return runAutomaton.run(aString);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#matchesAtEnd(java.lang.String)
   */
  @Override
  public boolean matchesAtEnd(final String aString) {
    matchesAtEnd++;
    if (StringUtils.isEmpty(aString)) {
      return false;
    }

    if (aString.length() < minLength) {
      return false;
    }

    final AutomatonFromEndMatcher tmpMatcher = new AutomatonFromEndMatcher(aString, minLength, runAutomaton);
    if (!tmpMatcher.find()) {
      return false;
    }

    return tmpMatcher.end() == aString.length();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#firstOccurenceIn(java.lang.String)
   */
  @Override
  public FindSpot firstOccurenceIn(final String aString) {
    return firstOccurenceIn(aString, 0);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#firstOccurenceIn(java.lang.String, int)
   */
  @Override
  public FindSpot firstOccurenceIn(final String aString, final int aStartPos) {
    firstOccurenceIn++;

    if (StringUtils.isEmpty(aString)) {
      return FindSpot.NOT_FOUND;
    }

    if (aString.length() < minLength) {
      return FindSpot.NOT_FOUND;
    }

    final AutomatonShortMatcher tmpMatcher = new AutomatonShortMatcher(aString, aStartPos, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return FindSpot.NOT_FOUND;
    }

    return new FindSpot(tmpMatcher.start(), tmpMatcher.end());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#lastOccurenceIn(java.lang.String)
   */
  @Override
  public FindSpot lastOccurenceIn(final String aString) {
    lastOccurenceIn++;

    if (StringUtils.isEmpty(aString)) {
      return FindSpot.NOT_FOUND;
    }

    if (aString.length() < minLength) {
      return null;
    }

    final AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return null;
    }

    return new FindSpot(tmpMatcher.start(), tmpMatcher.end());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsBeforeLastOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsBeforeLastOccurenceIn(final String aString) {
    noOfCharsBeforeLastOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    final AutomatonFromEndMatcher tmpMatcher = new AutomatonFromEndMatcher(aString, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = tmpMatcher.start();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsBeforeLastShortestOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsBeforeLastShortestOccurenceIn(final String aString) {
    noOfCharsBeforeLastShortestOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    final AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = tmpMatcher.start();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsAfterLastOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsAfterLastOccurenceIn(final String aString) {
    noOfCharsAfterLastOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    final AutomatonFromEndMatcher tmpMatcher = new AutomatonFromEndMatcher(aString, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = aString.length() - tmpMatcher.end();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsAfterLastShortestOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsAfterLastShortestOccurenceIn(final String aString) {
    noOfCharsAfterLastShortestOccurenceIn++;
    int tmpResult = -1;

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    if (aString.length() < minLength) {
      return -1;
    }

    final AutomatonShortFromEndMatcher tmpMatcher = new AutomatonShortFromEndMatcher(aString, runAutomaton);

    final boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    // we found something
    tmpResult = aString.length() - tmpMatcher.end();
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfSurroundingCharsIn(java.lang.String)
   */
  @Override
  public int noOfSurroundingCharsIn(final String aString) {
    noOfSurroundingCharsIn++;
    if (null == aString) {
      return -1;
    }

    final AutomatonMatcher tmpMatcher = runAutomaton.newMatcher(aString);

    boolean tmpFound = tmpMatcher.find();
    if (!tmpFound) {
      return -1;
    }

    int tmpResult = Integer.MAX_VALUE;
    // we found something
    while (tmpFound) {
      tmpResult = Math.min(tmpResult, aString.length() - tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }

    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [regexp: '" + patternString + "']";
  }
}
