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

import dk.brics.automaton.RunAutomaton;

/**
 * A tool that performs match operations on a given character sequence using a compiled automaton.
 * This tries to find the first longest match by starting at the end of the char sequence.
 *
 * @author rbri
 */
public final class AutomatonFromEndMatcher extends AbstractAutomatonMatcher {

  /**
   * Constructor.
   *
   * @param aCharSequence the string to search inside
   * @param anAutomaton the regex automaton
   */
  public AutomatonFromEndMatcher(final CharSequence aCharSequence, final RunAutomaton anAutomaton) {
    this(aCharSequence, 0, anAutomaton);
  }

  /**
   * Constructor.
   *
   * @param aCharSequence the string to search inside
   * @param anOffset the offset from end to start from
   * @param anAutomaton the regex automaton
   */
  public AutomatonFromEndMatcher(final CharSequence aCharSequence, final int anOffset, final RunAutomaton anAutomaton) {
    super(aCharSequence, anAutomaton);
    matchEnd = Math.max(chars.length(), chars.length() - anOffset);
  }

  /**
   * Find the next matching subsequence of the input. <br>
   * This also updates the values for the {@code start}, {@code end}, and {@code group} methods.
   *
   * @return {@code true} if there is a matching subsequence.
   */
  public boolean find() {
    if (matchStart == -2) {
      return false;
    }

    int tmpBegin;
    final int tmpLength = chars.length();
    if (matchStart == -1) {
      tmpBegin = tmpLength;
    } else {
      tmpBegin = matchStart - 1;
    }

    if (tmpBegin < 0) {
      return false;
    }

    boolean tmpFound = false;
    int tmpMatchEnd = -1;
    while (tmpBegin > -1) {
      tmpMatchEnd = findAt(tmpBegin);
      if (tmpMatchEnd == -1) {
        if (tmpFound) {
          return true;
        }
      } else {
        if (tmpFound && tmpMatchEnd != matchEnd) {
          return true;
        }
        setMatch(tmpBegin, tmpMatchEnd);
        tmpFound = true;
      }
      tmpBegin -= 1;
    }

    if (tmpMatchEnd != -1) {
      setMatch(tmpBegin + 1, tmpMatchEnd);
      return true;
    }

    setMatch(-2, -2);
    return false;
  }

  private int findAt(final int aPos) {
    int tmpMatchEnd = -1;

    if (automaton.isAccept(automaton.getInitialState())) {
      tmpMatchEnd = aPos;
    }

    final int tmpLength = chars.length();
    int tmpState = automaton.getInitialState();
    for (int i = aPos; i < tmpLength; i++) {
      final int tmpNewState = automaton.step(tmpState, chars.charAt(i));
      if (tmpNewState == -1) {
        break;
      }
      if (automaton.isAccept(tmpNewState)) {
        // found a match from begin to (i+1)
        tmpMatchEnd = i + 1;
      }
      tmpState = tmpNewState;
    }
    return tmpMatchEnd;
  }
}
