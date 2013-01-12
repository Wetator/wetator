/*
 * Copyright (c) 2008-2013 wetator.org
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

import org.wetator.exception.ImplementationException;

/**
 * An object that stores the start and end position of a match.<br/>
 * Like in {@link String#substring(int, int)} the start position is inclusive but the end position is exclusive. So the
 * last character of the match is <code>endPos - 1</code>.
 * 
 * @author rbri
 * @author frank.danek
 */
public class FindSpot {

  /**
   * Static object to reduce the number of objects in use.
   */
  public static final FindSpot NOT_FOUND = new FixedFindSpot(-1, -1);

  /**
   * The start position of a match, inclusive.
   */
  private int startPos;
  /**
   * The end position of a match, exclusive.
   */
  private int endPos;

  /**
   * The constructor.
   */
  public FindSpot() {
    this(-1, -1);
  }

  /**
   * The constructor.
   * 
   * @param aStartPos the startPos
   * @param anEndPos the endPos
   */
  public FindSpot(final int aStartPos, final int anEndPos) {
    startPos = aStartPos;
    endPos = anEndPos;
  }

  /**
   * @return the startPos
   */
  public int getStartPos() {
    return startPos;
  }

  /**
   * @param aStartPos the startPos to set
   */
  public void setStartPos(final int aStartPos) {
    startPos = aStartPos;
  }

  /**
   * @return the endPos
   */
  public int getEndPos() {
    return endPos;
  }

  /**
   * @param aEndPos the endPos to set
   */
  public void setEndPos(final int aEndPos) {
    endPos = aEndPos;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int tmpPrime = 31;
    int tmpHash = 1;
    tmpHash = tmpPrime * tmpHash + endPos;
    tmpHash = tmpPrime * tmpHash + startPos;
    return tmpHash;
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
    final FindSpot tmpOtherFindSpot = (FindSpot) anObject;
    if (endPos != tmpOtherFindSpot.endPos) {
      return false;
    }
    if (startPos != tmpOtherFindSpot.startPos) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FindSpot(" + startPos + ", " + endPos + ")";
  }

  /**
   * A find spot with fixed start and end position.
   * 
   * @author frank.danek
   */
  public static final class FixedFindSpot extends FindSpot {

    /**
     * The constructor.
     * 
     * @param aStartPos the startPos
     * @param anEndPos the endPos
     */
    public FixedFindSpot(final int aStartPos, final int anEndPos) {
      super(aStartPos, anEndPos);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.searchpattern.FindSpot#setStartPos(int)
     */
    @Override
    public void setStartPos(final int aStartPos) {
      throw new ImplementationException("FixedFindSpot does not support changing its values.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.searchpattern.FindSpot#setEndPos(int)
     */
    @Override
    public void setEndPos(final int aEndPos) {
      throw new ImplementationException("FixedFindSpot does not support changing its values.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.core.searchpattern.FindSpot#toString()
     */
    @Override
    public String toString() {
      if (this == FindSpot.NOT_FOUND) {
        return "FixedFindSpot(NOT_FOUND)";
      }
      return "FixedFindSpot(" + getStartPos() + ", " + getEndPos() + ")";
    }
  }
}
