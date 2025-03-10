/*
 * Copyright (c) 2008-2025 wetator.org
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
public class AutomatonShortFromEndMatcherTest {

  @Test
  public void dot() throws Exception {
    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStar() throws Exception {
    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".*", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(0, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStarException1() throws Exception {
    // this test corresponds to noOfCharsBeforeLastOccurenceIn

    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".*", tmpText);

    boolean tmpFound = tmpMatcher.find();

    // we found something
    while (tmpFound) {
      tmpMatcher.start();
      tmpFound = tmpMatcher.find();
    }
  }

  @Test
  public void dotStarException2() throws Exception {
    // this test corresponds to noOfSurroundingCharsIn

    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".*", tmpText);

    boolean tmpFound = tmpMatcher.find();

    // we found something
    int tmpResult = Integer.MAX_VALUE;
    // we found something
    while (tmpFound) {
      tmpResult = Math.min(tmpResult, tmpText.length() - tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }
  }

  @Test
  public void dotPlus() throws Exception {
    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".+", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotQuest() throws Exception {
    final String tmpText = "test";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".?", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(0, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDot() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotStar() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.*", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotPlus() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.+", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotQuest() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.?", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDot() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotStar() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".*t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotPlus() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".+t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotQuest() throws Exception {
    final String tmpText = "testt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher(".?t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDot() throws Exception {
    final String tmpText = "ttett";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotStar() throws Exception {
    final String tmpText = "ttestt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.*t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotPlus() throws Exception {
    final String tmpText = "ttestt";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.+t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotQuest() throws Exception {
    final String tmpText = "ttett";
    final AutomatonShortFromEndMatcher tmpMatcher = createMatcher("t.?t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  private AutomatonShortFromEndMatcher createMatcher(final String aPattern, final String aText) {
    final Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    final RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonShortFromEndMatcher(aText, tmpRunAutomaton);
  }
}
