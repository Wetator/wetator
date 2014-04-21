/*
 * Copyright (c) 2008-2014 wetator.org
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

    // offset 0
    tmpMatcher = createMatcher(".", tmpText, 0);

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

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher(".*", tmpText, 0);

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

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStarException1() throws Exception {
    // this test corresponds to noOfCharsBeforeLastOccurenceIn

    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText);

    boolean tmpFound = tmpMatcher.find();
    // we found something
    while (tmpFound) {
      tmpMatcher.start();
      tmpFound = tmpMatcher.find();
    }

    // offset 0
    tmpMatcher = createMatcher(".*", tmpText, 0);

    tmpFound = tmpMatcher.find();
    // we found something
    while (tmpFound) {
      tmpMatcher.start();
      tmpFound = tmpMatcher.find();
    }
  }

  @Test
  public void dotStarException2() throws Exception {
    // this test corresponds to noOfSurroundingCharsIn

    String tmpText = "test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText);

    boolean tmpFound = tmpMatcher.find();
    // we found something
    int tmpResult = Integer.MAX_VALUE;
    // we found something
    while (tmpFound) {
      tmpResult = Math.min(tmpResult, tmpText.length() - tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }

    // offset 0
    tmpMatcher = createMatcher(".*", tmpText, 0);

    tmpFound = tmpMatcher.find();
    // we found something
    tmpResult = Integer.MAX_VALUE;
    // we found something
    while (tmpFound) {
      tmpResult = Math.min(tmpResult, tmpText.length() - tmpMatcher.group().length());
      tmpFound = tmpMatcher.find();
    }
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

    // offset 0
    tmpMatcher = createMatcher(".+", tmpText, 0);

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

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher(".?", tmpText, 0);

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

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

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

    // offset 0
    tmpMatcher = createMatcher("t.", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher("t.*", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher("t.+", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher("t.?", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher(".t", tmpText, 0);

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
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher(".*t", tmpText, 0);

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
  public void suffixDotPlus() throws Exception {
    String tmpText = "testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".+t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(2, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher(".+t", tmpText, 0);

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
    Assert.assertEquals(3, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher(".?t", tmpText, 0);

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
  public void prefixAndSuffixDot() throws Exception {
    String tmpText = "ttett";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.t", tmpText);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher("t.t", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher("t.*t", tmpText, 0);

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
    Assert.assertEquals(1, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());

    // offset 0
    tmpMatcher = createMatcher("t.+t", tmpText, 0);

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

    // offset 0
    tmpMatcher = createMatcher("t.?t", tmpText, 0);

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

  @Test
  public void dotOffset() throws Exception {
    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(7, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStarOffset() throws Exception {
    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(7, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotStarException1Offset() throws Exception {
    // this test corresponds to noOfCharsBeforeLastOccurenceIn

    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText, 4);

    boolean tmpFound = tmpMatcher.find();

    // we found something
    while (tmpFound) {
      tmpMatcher.start();
      tmpFound = tmpMatcher.find();
    }
  }

  @Test
  public void dotStarException2Offset() throws Exception {
    // this test corresponds to noOfSurroundingCharsIn

    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*", tmpText, 4);

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
  public void dotPlusOffset() throws Exception {
    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".+", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("e", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(7, tmpMatcher.end());
    Assert.assertEquals("s", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void dotQuestOffset() throws Exception {
    String tmpText = "abc_test";
    AutomatonShortMatcher tmpMatcher = createMatcher(".?", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(4, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(7, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotStarOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.*", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotPlusOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.+", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("te", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixDotQuestOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.?", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotStarOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".*t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotPlusOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".+t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(6, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("st", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void suffixDotQuestOffset() throws Exception {
    String tmpText = "abc_testt";
    AutomatonShortMatcher tmpMatcher = createMatcher(".?t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(5, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("t", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotOffset() throws Exception {
    String tmpText = "abc_ttett";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotStarOffset() throws Exception {
    String tmpText = "abc_ttestt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.*t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(8, tmpMatcher.start());
    Assert.assertEquals(10, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotPlusOffset() throws Exception {
    String tmpText = "abc_ttestt";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.+t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("test", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  @Test
  public void prefixAndSuffixDotQuestOffset() throws Exception {
    String tmpText = "abc_ttett";
    AutomatonShortMatcher tmpMatcher = createMatcher("t.?t", tmpText, 4);

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(4, tmpMatcher.start());
    Assert.assertEquals(6, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(5, tmpMatcher.start());
    Assert.assertEquals(8, tmpMatcher.end());
    Assert.assertEquals("tet", tmpMatcher.group());

    Assert.assertTrue(tmpMatcher.find());
    Assert.assertEquals(7, tmpMatcher.start());
    Assert.assertEquals(9, tmpMatcher.end());
    Assert.assertEquals("tt", tmpMatcher.group());

    Assert.assertFalse(tmpMatcher.find());
  }

  private AutomatonShortMatcher createMatcher(String aPattern, String aText) {
    Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonShortMatcher(aText, tmpRunAutomaton);
  }

  private AutomatonShortMatcher createMatcher(String aPattern, String aText, int aStartPos) {
    Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonShortMatcher(aText, aStartPos, tmpRunAutomaton);
  }
}
