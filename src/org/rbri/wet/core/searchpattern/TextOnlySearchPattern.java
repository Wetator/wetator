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

/**
 * The special pattern that matches everything
 * 
 * @author rbri
 */
public final class TextOnlySearchPattern extends SearchPattern {

  private String patternString;
  private int patternLength;

  /**
   * Constructor
   * 
   * @param anOriginalString the string used to construct the pattern
   * @param aPatternString the string to compare with
   */
  protected TextOnlySearchPattern(String anOriginalString, String aPatternString) {
    super(anOriginalString);
    patternString = aPatternString;
    patternLength = aPatternString.length();
  }

  @Override
  public FindSpot firstOccurenceIn(String aString) {
    return firstOccurenceIn(aString, 0);
  }

  @Override
  public FindSpot firstOccurenceIn(String aString, int aStartPos) {
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    int tmpPos = aString.indexOf(patternString, aStartPos);
    if (0 > tmpPos) {
      return null;
    }

    return new FindSpot(tmpPos, tmpPos + patternLength);
  }

  @Override
  public FindSpot lastOccurenceIn(String aString) {
    FindSpot tmpResult = new FindSpot();

    if (StringUtils.isEmpty(aString)) {
      return tmpResult;
    }

    int tmpPos = aString.lastIndexOf(patternString);
    if (0 > tmpPos) {
      return null;
    }

    return new FindSpot(tmpPos, tmpPos + patternLength);
  }

  @Override
  public int noOfCharsBeforeLastOccurenceIn(String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }
    int tmpPos = aString.lastIndexOf(patternString);
    return tmpPos;
  }

  @Override
  public int noOfCharsAfterLastOccurenceIn(String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }
    int tmpPos = aString.lastIndexOf(patternString);
    if (0 > tmpPos) {
      return -1;
    }

    tmpPos = tmpPos + patternLength;
    return aString.length() - tmpPos;
  }

  @Override
  public boolean matches(String aString) {
    return patternString.equals(aString);
  }

  @Override
  public boolean matchesAtEnd(String aString) {
    if (StringUtils.isEmpty(aString)) {
      return false;
    }

    return aString.endsWith(patternString);
  }

  @Override
  public int noOfSurroundingCharsIn(String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }

    int tmpPos = aString.indexOf(patternString);
    if (0 > tmpPos) {
      return -1;
    }

    return aString.length() - patternLength;
  }

  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [text: '" + patternString + "']";
  }
}
