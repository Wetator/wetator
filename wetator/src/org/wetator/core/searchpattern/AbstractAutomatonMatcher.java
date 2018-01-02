/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.util.regex.MatchResult;

import dk.brics.automaton.RunAutomaton;

/**
 * Abstract class for the common code of all automaton
 * based matchers.
 *
 * @author rbri
 */
public abstract class AbstractAutomatonMatcher implements MatchResult {
  /** The the string to search inside. **/
  protected final CharSequence chars;
  /** The regex automaton. **/
  protected final RunAutomaton automaton;

  /** The start position of the current match. **/
  protected int matchStart = -1;
  /** The end position of the current match. **/
  protected int matchEnd = -1;

  /**
   * Constructor.
   *
   * @param aCharSequence the string to search inside
   * @param anAutomaton the regex automaton
   */
  public AbstractAutomatonMatcher(final CharSequence aCharSequence, final RunAutomaton anAutomaton) {
    chars = aCharSequence;
    automaton = anAutomaton;
  }

  /**
   * Stores a match.
   *
   * @param aMatchStart the start of the match to store
   * @param aMatchEnd the end of the match to store
   * @throws IllegalArgumentException if aMatchStart is greater than aMatchEnd.
   */
  protected void setMatch(final int aMatchStart, final int aMatchEnd) throws IllegalArgumentException {
    if (aMatchStart > aMatchEnd) {
      throw new IllegalArgumentException("Start must be less than or equal to end: " + aMatchStart + ", " + aMatchEnd);
    }
    matchStart = aMatchStart;
    matchEnd = aMatchEnd;
  }

  /**
   * Returns the offset after the last character matched.
   *
   * @return The offset after the last character matched.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   */
  @Override
  public int end() throws IllegalStateException {
    matchGood();
    return matchEnd;
  }

  /**
   * Returns the offset after the last character matched of the specified
   * capturing group. <br>
   * Note that because the automaton does not support capturing groups the
   * only valid group is 0 (the entire match).
   *
   * @param aGroup the desired capturing group.
   * @return The offset after the last character matched of the specified
   *         capturing group.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   * @throws IndexOutOfBoundsException if the specified capturing group does
   *         not exist in the underlying automaton.
   */
  @Override
  public int end(final int aGroup) throws IndexOutOfBoundsException, IllegalStateException {
    onlyZero(aGroup);
    return end();
  }

  /**
   * Returns the subsequence of the input found by the previous match.
   *
   * @return The subsequence of the input found by the previous match.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   */
  @Override
  public String group() throws IllegalStateException {
    matchGood();
    return chars.subSequence(matchStart, matchEnd).toString();
  }

  /**
   * Returns the subsequence of the input found by the specified capturing
   * group during the previous match operation. <br>
   * Note that because the automaton does not support capturing groups the
   * only valid group is 0 (the entire match).
   *
   * @param aGroup the desired capturing group.
   * @return The subsequence of the input found by the specified capturing
   *         group during the previous match operation the previous match. Or {@code null} if the given group did match.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   * @throws IndexOutOfBoundsException if the specified capturing group does
   *         not exist in the underlying automaton.
   */
  @Override
  public String group(final int aGroup) throws IndexOutOfBoundsException, IllegalStateException {
    onlyZero(aGroup);
    return group();
  }

  /**
   * Returns the number of capturing groups in the underlying automaton. <br>
   * Note that because the automaton does not support capturing groups this
   * method will always return 0.
   *
   * @return The number of capturing groups in the underlying automaton.
   */
  @Override
  public int groupCount() {
    return 0;
  }

  /**
   * Returns the offset of the first character matched.
   *
   * @return The offset of the first character matched.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   */
  @Override
  public int start() throws IllegalStateException {
    matchGood();
    return matchStart;
  }

  /**
   * Returns the offset of the first character matched of the specified
   * capturing group. <br>
   * Note that because the automaton does not support capturing groups the
   * only valid group is 0 (the entire match).
   *
   * @param aGroup the desired capturing group.
   * @return The offset of the first character matched of the specified
   *         capturing group.
   * @throws IllegalStateException if there has not been a match attempt or
   *         if the last attempt yielded no results.
   * @throws IndexOutOfBoundsException if the specified capturing group does
   *         not exist in the underlying automaton.
   */
  @Override
  public int start(final int aGroup) throws IndexOutOfBoundsException, IllegalStateException {
    onlyZero(aGroup);
    return start();
  }

  /** Helper method that requires the group argument to be 0. */
  private static void onlyZero(final int aGroup) throws IndexOutOfBoundsException {
    if (aGroup != 0) {
      throw new IndexOutOfBoundsException("The only group supported is 0.");
    }
  }

  /** Helper method to check that the last match attempt was valid. */
  private void matchGood() throws IllegalStateException {
    if (matchStart < 0 || matchEnd < 0) {
      throw new IllegalStateException("There was no available match.");
    }
  }
}
