/*
 * Copyright (c) 2008-2011 wetator.org
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
import org.wetator.backend.htmlunit.util.FindSpot;

/**
 * This is a special implementation of a {@link SearchPattern} that matches everything.
 * 
 * @author rbri
 */
public final class MatchAllSearchPattern extends SearchPattern {

  /**
   * The constructor.
   */
  protected MatchAllSearchPattern() {
    super("*");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#firstOccurenceIn(java.lang.String)
   */
  @Override
  public FindSpot firstOccurenceIn(final String aString) {
    return FindSpot.NOT_FOUND;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#firstOccurenceIn(java.lang.String, int)
   */
  @Override
  public FindSpot firstOccurenceIn(final String aString, final int aStartPos) {
    return FindSpot.NOT_FOUND;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#lastOccurenceIn(java.lang.String)
   */
  @Override
  public FindSpot lastOccurenceIn(final String aString) {
    return FindSpot.NOT_FOUND;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsBeforeLastOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsBeforeLastOccurenceIn(final String aString) {
    if (StringUtils.isEmpty(aString)) {
      return -1;
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfCharsAfterLastOccurenceIn(java.lang.String)
   */
  @Override
  public int noOfCharsAfterLastOccurenceIn(final String aString) {
    if (null == aString) {
      return 0;
    }
    return aString.length();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#matches(java.lang.String)
   */
  @Override
  public boolean matches(final String aString) {
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#matchesAtEnd(java.lang.String)
   */
  @Override
  public boolean matchesAtEnd(final String aString) {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.searchpattern.SearchPattern#noOfSurroundingCharsIn(java.lang.String)
   */
  @Override
  public int noOfSurroundingCharsIn(final String aString) {
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchPattern '" + getOriginalString() + "' [matchAll]";
  }
}
