/*
 * Copyright (c) 2008-2012 wetator.org
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
 * This verifies, if the given CharSequence ends with the pattern.
 * 
 * @author rbri
 */
public final class AutomatonFromEndMatcher {

  private final RunAutomaton automaton;
  private final CharSequence chars;

  private int matchStart;

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
    chars = aCharSequence;
    automaton = anAutomaton;
    matchStart = Math.min(chars.length(), chars.length() - anOffset);
  }

  /**
   * Find the next matching subsequence of the input. <br />
   * This also updates the values for the {@code start}, {@code end}, and {@code group} methods.
   * 
   * @return {@code true} if there is a matching subsequence.
   */
  public boolean matches() {
    int tmpBegin = matchStart;

    if (tmpBegin < 0) {
      return false;
    }

    if (automaton.isAccept(automaton.getInitialState())) {
      return true;
    }

    final int tmpLength = chars.length();
    while (tmpBegin > -1) {
      int tmpState = automaton.getInitialState();
      for (int i = tmpBegin; i < tmpLength; i += 1) {
        final int tmpNewState = automaton.step(tmpState, chars.charAt(i));
        if (tmpNewState == -1) {
          break;
        } else if (automaton.isAccept(tmpNewState)) {
          if (i + 1 == tmpLength) {
            return true;
          }
        }
        tmpState = tmpNewState;
      }
      tmpBegin -= 1;
    }
    return false;
  }
}
