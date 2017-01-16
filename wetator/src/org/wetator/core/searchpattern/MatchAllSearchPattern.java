/*
 * Copyright (c) 2008-2017 wetator.org
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

import org.wetator.util.FindSpot;

/**
 * This is a special implementation of a {@link SearchPattern} that matches everything.
 *
 * @author rbri
 * @author frank.danek
 */
public final class MatchAllSearchPattern extends SearchPattern {

  /**
   * The constructor.
   */
  protected MatchAllSearchPattern() {
    super("*");
  }

  @Override
  public int getMinLength() {
    return 0;
  }

  @Override
  public boolean matches(final String aString) {
    return true;
  }

  @Override
  public boolean matchesAtEnd(final String aString) {
    return false;
  }

  @Override
  public FindSpot firstOccurenceIn(final String aString) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public FindSpot firstOccurenceIn(final String aString, final int aStartPos) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public FindSpot lastOccurenceIn(final String aString) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public int noOfCharsBeforeLastOccurenceIn(final String aString) {
    if (aString == null) {
      return -1;
    }
    return 0;
  }

  @Override
  public int noOfCharsBeforeLastShortestOccurenceIn(final String aString) {
    if (aString == null) {
      return -1;
    }
    return aString.length();
  }

  @Override
  public int noOfCharsAfterLastOccurenceIn(final String aString) {
    if (aString == null) {
      return -1;
    }
    return 0;
  }

  @Override
  public int noOfCharsAfterLastShortestOccurenceIn(final String aString) {
    return noOfCharsAfterLastOccurenceIn(aString);
  }

  @Override
  public int noOfSurroundingCharsIn(final String aString) {
    if (aString == null) {
      return -1;
    }
    return 0;
  }

  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [matchAll]";
  }
}
