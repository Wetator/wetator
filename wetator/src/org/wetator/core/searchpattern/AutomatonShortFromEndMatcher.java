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
 * This tries to find the first shortest match by starting at the end of the char sequence.
 *
 * @author rbri
 */
public final class AutomatonShortFromEndMatcher extends AbstractAutomatonMatcher {

  /**
   * Constructor.
   *
   * @param aCharSequence the chars to set
   * @param anAutomaton the automaton to set
   */
  public AutomatonShortFromEndMatcher(final CharSequence aCharSequence, final RunAutomaton anAutomaton) {
    super(aCharSequence, anAutomaton);
  }

  /**
   * Find the next matching subsequence of the input. <br>
   * This also updates the values for the {@code start}, {@code end}, and {@code group} methods.
   *
   * @return {@code true} if there is a matching subsequence.
   */
  public boolean find() {
    if (matchEnd == -2) {
      return false;
    }

    int tmpLength = chars.length();
    int tmpBegin;
    if (matchEnd == -1) {
      tmpBegin = chars.length();
    } else {
      tmpBegin = matchStart - 1;
      tmpLength = matchEnd - 1;
    }

    if (tmpBegin < 0) {
      return false;
    }

    if (automaton.isAccept(automaton.getInitialState())) {
      setMatch(tmpBegin, tmpBegin);
      return true;
    }

    int tmpMatchStart = -1;
    while (tmpBegin > -1) {
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
          return true;
        }
        tmpState = tmpNewState;
      }
      tmpBegin -= 1;
    }

    setMatch(-2, -2);
    return false;
  }
}
