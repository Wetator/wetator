/*
 * Copyright (c) 2008-2015 wetator.org
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

import dk.brics.automaton.RunAutomaton;

/**
 * @author rbri
 */
public final class AutomatonShortMatcher extends AbstractAutomatonMatcher {

  /**
   * Constructor.
   * 
   * @param aCharSequence the chars to set
   * @param anAutomaton the automaton to set
   */
  public AutomatonShortMatcher(final CharSequence aCharSequence, final RunAutomaton anAutomaton) {
    this(aCharSequence, 0, anAutomaton);
  }

  /**
   * Constructor.
   * 
   * @param aCharSequence the chars to set
   * @param aStartPos the start pos for the search in aCharSequence
   * @param anAutomaton the automaton to set
   */
  public AutomatonShortMatcher(final CharSequence aCharSequence, final int aStartPos, final RunAutomaton anAutomaton) {
    super(aCharSequence, anAutomaton);
    matchStart = Math.max(0, aStartPos) - 1;
  }

  /**
   * Find the next matching subsequence of the input. <br />
   * This also updates the values for the {@code start}, {@code end}, and {@code group} methods.
   * 
   * @return {@code true} if there is a matching subsequence.
   */
  public boolean find() {
    if (matchStart == -2) {
      return false;
    }

    int tmpBegin;
    if (matchStart == -1) {
      tmpBegin = 0;
    } else {
      tmpBegin = matchStart + 1;
    }

    final int tmpLength = chars.length();
    if (tmpBegin > tmpLength) {
      return false;
    }

    if (automaton.isAccept(automaton.getInitialState())) {
      setMatch(tmpBegin, tmpBegin);
      return true;
    }
    int tmpMatchStart = -1;

    while (tmpBegin < tmpLength) {
      int tmpState = automaton.getInitialState();
      for (int i = tmpBegin; i < tmpLength; i += 1) {
        final int tmpNewState = automaton.step(tmpState, chars.charAt(i));
        if (tmpNewState == -1) {
          break;
        }
        if (automaton.isAccept(tmpNewState)) {
          if (tmpMatchStart == -1) {
            tmpMatchStart = tmpBegin;
          }

          setMatch(tmpMatchStart, i + 1);

          // match found, but is this the shortest one?
          if (matchEnd - matchStart > 1) {
            reduceIfPossible();
          }

          return true;
        }
        tmpState = tmpNewState;
      }
      tmpBegin += 1;
    }

    setMatch(-2, -2);
    return false;
  }

  /**
   * Internal helper, that check, if a shorter match
   * of the already found substring is also possible.
   * If yes, this updates the match position.
   */
  private void reduceIfPossible() {
    int tmpBegin = matchStart + 1;

    while (tmpBegin < matchEnd) {
      int tmpState = automaton.getInitialState();
      for (int i = tmpBegin; i < matchEnd; i += 1) {
        final int tmpNewState = automaton.step(tmpState, chars.charAt(i));
        if (tmpNewState == -1) {
          return;
        } else if (automaton.isAccept(tmpNewState)) {
          setMatch(tmpBegin, matchEnd);
          break;
        }
        tmpState = tmpNewState;
      }
      tmpBegin += 1;
    }
  }
}
