/*
 * Copyright (c) 2008-2020 wetator.org
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

/**
 * This is a special implementation of a {@link SearchPattern} that contains only plain text and no wildcards.<br>
 * The execution is faster compared to {@link RegExpSearchPattern} (containing no wildcards).
 *
 * @author rbri
 * @author frank.danek
 */
public final class TextOnlySearchPattern extends SearchPattern {

  private String patternString;
  private int patternLength;

  /**
   * The constructor.
   *
   * @param anOriginalString the string used to construct the pattern
   * @param aPatternString the pattern to compare with
   */
  protected TextOnlySearchPattern(final String anOriginalString, final String aPatternString) {
    super(anOriginalString);
    patternString = aPatternString;
    patternLength = aPatternString.length();
  }

  @Override
  public int getMinLength() {
    return patternLength;
  }

  @Override
  public boolean matches(final String aString) {
    return patternString.equals(aString);
  }

  @Override
  public boolean matchesAtEnd(final String aString) {
    if (StringUtils.isEmpty(aString)) {
      return false;
    }

    return aString.endsWith(patternString);
  }

  @Override
  public FindSpot firstOccurenceIn(final String aString) {
    return firstOccurenceIn(aString, 0);
  }

  @Override
  public FindSpot firstOccurenceIn(final String aString, final int aStartPos) {
    if (StringUtils.isEmpty(aString)) {
      return FindSpot.NOT_FOUND;
    }

    final int tmpPos = aString.indexOf(patternString, aStartPos);
    if (0 > tmpPos) {
      return FindSpot.NOT_FOUND;
    }

    return new FindSpot(tmpPos, tmpPos + patternLength);
  }

  @Override
  public FindSpot lastOccurenceIn(final String aString) {
    if (StringUtils.isEmpty(aString)) {
      return FindSpot.NOT_FOUND;
    }

    final int tmpPos = aString.lastIndexOf(patternString);
    if (0 > tmpPos) {
      return null;
    }

    return new FindSpot(tmpPos, tmpPos + patternLength);
  }

  @Override
  public int noOfCharsBeforeLastOccurenceIn(final String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }
    return aString.lastIndexOf(patternString);
  }

  @Override
  public int noOfCharsBeforeLastShortestOccurenceIn(final String aString) {
    return noOfCharsBeforeLastOccurenceIn(aString);
  }

  @Override
  public int noOfCharsAfterLastOccurenceIn(final String aString) {
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
  public int noOfCharsAfterLastShortestOccurenceIn(final String aString) {
    return noOfCharsAfterLastOccurenceIn(aString);
  }

  @Override
  public int noOfSurroundingCharsIn(final String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }

    final int tmpPos = aString.indexOf(patternString);
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
