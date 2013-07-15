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
public final class MatchAllSearchPattern extends SearchPattern {

  /**
   * Constructor
   */
  protected MatchAllSearchPattern() {
    super("*");
  }

  @Override
  public FindSpot firstOccurenceIn(String aString) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public FindSpot firstOccurenceIn(String aString, int aStartPos) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public FindSpot lastOccurenceIn(String aString) {
    return FindSpot.NOT_FOUND;
  }

  @Override
  public int noOfCharsBeforeLastOccurenceIn(String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }
    return 0;
  }

  @Override
  public int noOfCharsAfterLastOccurenceIn(String aString) {
    if (null == aString) {
      return 0;
    }
    return aString.length();
  }

  @Override
  public boolean matches(String aString) {
    return true;
  }

  @Override
  public boolean matchesAtEnd(String aString) {
    return false;
  }

  @Override
  public int noOfSurroundingCharsIn(String aString) {
    return 0;
  }

  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [matchAll]";
  }
}
