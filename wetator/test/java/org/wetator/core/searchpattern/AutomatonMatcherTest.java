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

import org.junit.Assert;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * @author frank.danek
 */
public class AutomatonMatcherTest {

  @Test
  public void dot() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".", "ab");
    assertGroup(tmpMatcher, 0, 1, "a");
  }

  @Test
  public void dotStar() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".*", "");
    assertGroup(tmpMatcher, 0, 0, "");

    tmpMatcher = createMatcher(".*", "ab");
    assertGroup(tmpMatcher, 0, 2, "ab");
  }

  @Test
  public void dotPlus() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".+", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+", "ab");
    assertGroup(tmpMatcher, 0, 2, "ab");
  }

  @Test
  public void dotQuest() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".?", "");
    assertGroup(tmpMatcher, 0, 0, "");

    tmpMatcher = createMatcher(".?", "ab");
    assertGroup(tmpMatcher, 0, 1, "a");
  }

  @Test
  public void prefixDot() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.", "axb");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.", "xbb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.", "aaxbb");
    assertGroup(tmpMatcher, 2, 4, "xb");

    tmpMatcher = createMatcher("x.", "axbx");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.", "xbxc");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.", "axbxc");
    assertGroup(tmpMatcher, 1, 3, "xb");
  }

  @Test
  public void prefixDotStar() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.*", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher("x.*", "ax");
    assertGroup(tmpMatcher, 1, 2, "x");

    tmpMatcher = createMatcher("x.*", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.*", "axb");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.*", "aax");
    assertGroup(tmpMatcher, 2, 3, "x");

    tmpMatcher = createMatcher("x.*", "xbb");
    assertGroup(tmpMatcher, 0, 3, "xbb");

    tmpMatcher = createMatcher("x.*", "aaxbb");
    assertGroup(tmpMatcher, 2, 5, "xbb");

    tmpMatcher = createMatcher("x.*", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.*", "xbxc");
    assertGroup(tmpMatcher, 0, 4, "xbxc");

    tmpMatcher = createMatcher("x.*", "axbxc");
    assertGroup(tmpMatcher, 1, 5, "xbxc");
  }

  @Test
  public void prefixDotPlus() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.+", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.+", "axb");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.+", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+", "xbb");
    assertGroup(tmpMatcher, 0, 3, "xbb");

    tmpMatcher = createMatcher("x.+", "aaxbb");
    assertGroup(tmpMatcher, 2, 5, "xbb");

    tmpMatcher = createMatcher("x.+", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.+", "xbxc");
    assertGroup(tmpMatcher, 0, 4, "xbxc");

    tmpMatcher = createMatcher("x.+", "axbxc");
    assertGroup(tmpMatcher, 1, 5, "xbxc");
  }

  @Test
  public void prefixDotQuest() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.?", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher("x.?", "ax");
    assertGroup(tmpMatcher, 1, 2, "x");

    tmpMatcher = createMatcher("x.?", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.?", "axb");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.?", "aax");
    assertGroup(tmpMatcher, 2, 3, "x");

    tmpMatcher = createMatcher("x.?", "xbb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.?", "aaxbb");
    assertGroup(tmpMatcher, 2, 4, "xb");

    tmpMatcher = createMatcher("x.?", "axbx");
    assertGroup(tmpMatcher, 1, 3, "xb");

    tmpMatcher = createMatcher("x.?", "xbxc");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher("x.?", "axbxc");
    assertGroup(tmpMatcher, 1, 3, "xb");
  }

  @Test
  public void suffixDot() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x", "axb");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".x", "aax");
    assertGroup(tmpMatcher, 1, 3, "ax");

    tmpMatcher = createMatcher(".x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x", "aaxbb");
    assertGroup(tmpMatcher, 1, 3, "ax");

    tmpMatcher = createMatcher(".x", "xbx");
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".x", "axbx");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".x", "xbxc");
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".x", "axbxc");
    assertGroup(tmpMatcher, 0, 2, "ax");
  }

  @Test
  public void suffixDotStar() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".*x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".*x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".*x", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".*x", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".*x", "xb");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".*x", "axb");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".*x", "aax");
    assertGroup(tmpMatcher, 0, 3, "aax");

    tmpMatcher = createMatcher(".*x", "xbb");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".*x", "aaxbb");
    assertGroup(tmpMatcher, 0, 3, "aax");

    tmpMatcher = createMatcher(".*x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher(".*x", "axbx");
    assertGroup(tmpMatcher, 0, 4, "axbx");

    tmpMatcher = createMatcher(".*x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher(".*x", "axbxc");
    assertGroup(tmpMatcher, 0, 4, "axbx");
  }

  @Test
  public void suffixDotPlus() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".+x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".+x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x", "axb");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".+x", "aax");
    assertGroup(tmpMatcher, 0, 3, "aax");

    tmpMatcher = createMatcher(".+x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x", "aaxbb");
    assertGroup(tmpMatcher, 0, 3, "aax");

    tmpMatcher = createMatcher(".+x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher(".+x", "axbx");
    assertGroup(tmpMatcher, 0, 4, "axbx");

    tmpMatcher = createMatcher(".+x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher(".+x", "axbxc");
    assertGroup(tmpMatcher, 0, 4, "axbx");
  }

  @Test
  public void suffixDotQuest() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".?x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".?x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".?x", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".?x", "xb");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x", "axb");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".?x", "aax");
    assertGroup(tmpMatcher, 1, 3, "ax");

    tmpMatcher = createMatcher(".?x", "xbb");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x", "aaxbb");
    assertGroup(tmpMatcher, 1, 3, "ax");

    tmpMatcher = createMatcher(".?x", "xbx");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x", "axbx");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".?x", "xbxc");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x", "axbxc");
    assertGroup(tmpMatcher, 0, 2, "ax");
  }

  @Test
  public void prefixAndSuffixDot() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "axb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "aaxbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "xx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "axx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "xxc");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "axxc");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.x", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.x", "axbxc");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.x", "xbxcx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.x", "axbxcx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.x", "xbxcxd");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.x", "axbxcxd");
    assertGroup(tmpMatcher, 1, 4, "xbx");
  }

  @Test
  public void prefixAndSuffixDotStar() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.*x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "axb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "aaxbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.*x", "xx");
    assertGroup(tmpMatcher, 0, 2, "xx");

    tmpMatcher = createMatcher("x.*x", "axx");
    assertGroup(tmpMatcher, 1, 3, "xx");

    tmpMatcher = createMatcher("x.*x", "xxc");
    assertGroup(tmpMatcher, 0, 2, "xx");

    tmpMatcher = createMatcher("x.*x", "axxc");
    assertGroup(tmpMatcher, 1, 3, "xx");

    tmpMatcher = createMatcher("x.*x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.*x", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.*x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.*x", "axbxc");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.*x", "xbxcx");
    assertGroup(tmpMatcher, 0, 5, "xbxcx");

    tmpMatcher = createMatcher("x.*x", "axbxcx");
    assertGroup(tmpMatcher, 1, 6, "xbxcx");

    tmpMatcher = createMatcher("x.*x", "xbxcxd");
    assertGroup(tmpMatcher, 0, 5, "xbxcx");

    tmpMatcher = createMatcher("x.*x", "axbxcxd");
    assertGroup(tmpMatcher, 1, 6, "xbxcx");
  }

  @Test
  public void prefixAndSuffixDotPlus() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.+x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "axb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "aaxbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "xx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "axx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "xxc");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "axxc");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.+x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.+x", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.+x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.+x", "axbxc");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.+x", "xbxcx");
    assertGroup(tmpMatcher, 0, 5, "xbxcx");

    tmpMatcher = createMatcher("x.+x", "axbxcx");
    assertGroup(tmpMatcher, 1, 6, "xbxcx");

    tmpMatcher = createMatcher("x.+x", "xbxcxd");
    assertGroup(tmpMatcher, 0, 5, "xbxcx");

    tmpMatcher = createMatcher("x.+x", "axbxcxd");
    assertGroup(tmpMatcher, 1, 6, "xbxcx");
  }

  @Test
  public void prefixAndSuffixDotQuest() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher("x.?x", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "axb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "aaxbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher("x.?x", "xx");
    assertGroup(tmpMatcher, 0, 2, "xx");

    tmpMatcher = createMatcher("x.?x", "axx");
    assertGroup(tmpMatcher, 1, 3, "xx");

    tmpMatcher = createMatcher("x.?x", "xxc");
    assertGroup(tmpMatcher, 0, 2, "xx");

    tmpMatcher = createMatcher("x.?x", "axxc");
    assertGroup(tmpMatcher, 1, 3, "xx");

    tmpMatcher = createMatcher("x.?x", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.?x", "axbx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.?x", "xbxc");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.?x", "axbxc");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.?x", "xbxcx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.?x", "axbxcx");
    assertGroup(tmpMatcher, 1, 4, "xbx");

    tmpMatcher = createMatcher("x.?x", "xbxcxd");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher("x.?x", "axbxcxd");
    assertGroup(tmpMatcher, 1, 4, "xbx");
  }

  @Test
  public void dotMiddleDot() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".x.", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "axb");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".x.", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "aaxbb");
    assertGroup(tmpMatcher, 1, 4, "axb");

    tmpMatcher = createMatcher(".x.", "xbx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".x.", "axbx");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".x.", "xbxc");
    assertGroup(tmpMatcher, 1, 4, "bxc");

    tmpMatcher = createMatcher(".x.", "axbxc");
    assertGroup(tmpMatcher, 0, 3, "axb");
  }

  @Test
  public void dotStarMiddleDotStar() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".*x.*", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".*x.*", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".*x.*", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".*x.*", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".*x.*", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher(".*x.*", "axb");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".*x.*", "aax");
    assertGroup(tmpMatcher, 0, 3, "aax");

    tmpMatcher = createMatcher(".*x.*", "xbb");
    assertGroup(tmpMatcher, 0, 3, "xbb");

    tmpMatcher = createMatcher(".*x.*", "aaxbb");
    assertGroup(tmpMatcher, 0, 5, "aaxbb");

    tmpMatcher = createMatcher(".*x.*", "xbx");
    assertGroup(tmpMatcher, 0, 3, "xbx");

    tmpMatcher = createMatcher(".*x.*", "axbx");
    assertGroup(tmpMatcher, 0, 4, "axbx");

    tmpMatcher = createMatcher(".*x.*", "xbxc");
    assertGroup(tmpMatcher, 0, 4, "xbxc");

    tmpMatcher = createMatcher(".*x.*", "axbxc");
    assertGroup(tmpMatcher, 0, 5, "axbxc");
  }

  @Test
  public void dotPlusMiddleDotPlus() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".+x.+", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "x");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "ax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "xb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "axb");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".+x.+", "aax");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "xbb");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "aaxbb");
    assertGroup(tmpMatcher, 0, 5, "aaxbb");

    tmpMatcher = createMatcher(".+x.+", "xbx");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+x.+", "axbx");
    assertGroup(tmpMatcher, 0, 4, "axbx");

    tmpMatcher = createMatcher(".+x.+", "xbxc");
    assertGroup(tmpMatcher, 0, 4, "xbxc");

    tmpMatcher = createMatcher(".+x.+", "axbxc");
    assertGroup(tmpMatcher, 0, 5, "axbxc");
  }

  @Test
  public void dotQuestMiddleDotQuest() throws Exception {
    AutomatonMatcher tmpMatcher = createMatcher(".?x.?", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".?x.?", "ab");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".?x.?", "x");
    assertGroup(tmpMatcher, 0, 1, "x");

    tmpMatcher = createMatcher(".?x.?", "ax");
    assertGroup(tmpMatcher, 0, 2, "ax");

    tmpMatcher = createMatcher(".?x.?", "xb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher(".?x.?", "axb");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".?x.?", "aax");
    assertGroup(tmpMatcher, 1, 3, "ax");

    tmpMatcher = createMatcher(".?x.?", "xbb");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher(".?x.?", "aaxbb");
    assertGroup(tmpMatcher, 1, 4, "axb");

    tmpMatcher = createMatcher(".?x.?", "xbx");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher(".?x.?", "axbx");
    assertGroup(tmpMatcher, 0, 3, "axb");

    tmpMatcher = createMatcher(".?x.?", "xbxc");
    assertGroup(tmpMatcher, 0, 2, "xb");

    tmpMatcher = createMatcher(".?x.?", "axbxc");
    assertGroup(tmpMatcher, 0, 3, "axb");
  }

  private AutomatonMatcher createMatcher(final String aPattern, final String aText) {
    final Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    final RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return tmpRunAutomaton.newMatcher(aText);
  }

  private void assertGroup(final AutomatonMatcher aMatcher, final int aStart, final int anEnd, final String aGroup) {
    Assert.assertTrue(aMatcher.find());
    Assert.assertEquals(aStart, aMatcher.start());
    Assert.assertEquals(anEnd, aMatcher.end());
    Assert.assertEquals(aGroup, aMatcher.group());
  }
}
