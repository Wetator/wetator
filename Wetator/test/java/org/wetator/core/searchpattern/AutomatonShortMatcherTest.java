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
 * @author frank.danek
 */
public class AutomatonShortMatcherTest {

  @Test
  public void dot() throws Exception {
    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStar() throws Exception {
    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(0, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    // TODO should there be one hit after the string?

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotPlus() throws Exception {
    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".+", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotQuest() throws Exception {
    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".?", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(0, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(3, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    // TODO should there be one hit after the string?

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDot() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotStar() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.*", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotPlus() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.+", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotQuest() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.?", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDot() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotStar() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("est", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    // TODO is it correct to find 'est' and 'st' if we are looking for the short match?

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotPlus() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".+t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("est", tmpMatcher.group());

    // TODO is it correct to find 'test' and 'est' if we are looking for the short match?

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotQuest() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".?t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(1, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    // TODO is it correct to find 'st' if we are looking for the short match?

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDot() throws Exception {
    String tmpText = "ttett";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotStar() throws Exception {
    String tmpText = "ttestt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.*t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotPlus() throws Exception {
    String tmpText = "ttestt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.+t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("ttest", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotQuest() throws Exception {
    String tmpText = "ttett";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.?t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(0, tmpMatcher.start());
    Assert.assertEquals(2, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  private AutomatonShortMatcher createMatcher(String aPattern, String aText) {
    Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonShortMatcher(aText, tmpRunAutomaton);
  }
}
