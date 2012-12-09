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

import org.junit.Assert;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * @author rbri
 */
public class AutomatonFromEndMatcherTest {

  @Test
  public void dot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".", "test");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".", "a");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void dotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".*", "test");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".*", "a");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".*", "");
    Assert.assertTrue(tmpMatcher.matches());
  }

  @Test
  public void dotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".+", "test");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".+", "a");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".+", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void dotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".?", "test");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".?", "a");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".?", "");
    Assert.assertTrue(tmpMatcher.matches());
  }

  @Test
  public void prefixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.", "te");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.", "tes");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.", "at");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.", "t");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.*", "te");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*", "tes");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*", "at");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*", "t");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixDotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.+", "te");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+", "tes");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+", "at");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+", "t");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixDotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.?", "te");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?", "tes");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?", "at");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?", "t");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void suffixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".t", "et");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".t", "set");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".t", "t");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher(".t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void suffixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".*t", "et");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".*t", "set");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".*t", "t");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".*t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void suffixDotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".+t", "et");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".+t", "set");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".+t", "t");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher(".+t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void suffixDotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".?t", "et");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".?t", "set");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".?t", "t");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher(".?t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixAndSuffixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.t", "tet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.t", "atet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.t", "set");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.t", "tt");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixAndSuffixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.*t", "tet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*t", "atet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*t", "set");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*t", "tt");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.*t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixAndSuffixDotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.+t", "tet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+t", "atet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+t", "set");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+t", "tt");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.+t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  @Test
  public void prefixAndSuffixDotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("t.?t", "tet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?t", "atet");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?t", "set");
    Assert.assertFalse(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?t", "tt");
    Assert.assertTrue(tmpMatcher.matches());

    tmpMatcher = createMatcher("t.?t", "");
    Assert.assertFalse(tmpMatcher.matches());
  }

  private AutomatonFromEndMatcher createMatcher(String aPattern, String aText) {
    Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonFromEndMatcher(aText, tmpRunAutomaton);
  }
}
