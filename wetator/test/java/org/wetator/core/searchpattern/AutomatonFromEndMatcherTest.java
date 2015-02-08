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

import org.junit.Assert;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * @author frank.danek
 */
public class AutomatonFromEndMatcherTest {

  @Test
  public void dot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".", "ab");
    assertGroup(tmpMatcher, 1, 2, "b");
  }

  @Test
  public void dotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".*", "");
    assertGroup(tmpMatcher, 0, 0, "");

    tmpMatcher = createMatcher(".*", "ab");
    assertGroup(tmpMatcher, 0, 2, "ab");
  }

  @Test
  public void dotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".+", "");
    Assert.assertFalse(tmpMatcher.find());

    tmpMatcher = createMatcher(".+", "ab");
    assertGroup(tmpMatcher, 0, 2, "ab");
  }

  @Test
  public void dotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".?", "");
    assertGroup(tmpMatcher, 0, 0, "");

    tmpMatcher = createMatcher(".?", "ab");
    assertGroup(tmpMatcher, 1, 2, "b");
  }

  @Test
  public void prefixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.", "");
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
    assertGroup(tmpMatcher, 2, 4, "xc");

    tmpMatcher = createMatcher("x.", "axbxc");
    assertGroup(tmpMatcher, 3, 5, "xc");
  }

  @Test
  public void prefixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.*", "");
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
    assertGroup(tmpMatcher, 3, 4, "x");

    tmpMatcher = createMatcher("x.*", "xbxc");
    assertGroup(tmpMatcher, 2, 4, "xc");

    tmpMatcher = createMatcher("x.*", "axbxc");
  }

  @Test
  public void prefixDotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.+", "");
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
    assertGroup(tmpMatcher, 2, 4, "xc");

    tmpMatcher = createMatcher("x.+", "axbxc");
    assertGroup(tmpMatcher, 3, 5, "xc");
  }

  @Test
  public void prefixDotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.?", "");
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
    assertGroup(tmpMatcher, 3, 4, "x");

    tmpMatcher = createMatcher("x.?", "xbxc");
    assertGroup(tmpMatcher, 2, 4, "xc");

    tmpMatcher = createMatcher("x.?", "axbxc");
    assertGroup(tmpMatcher, 3, 5, "xc");
  }

  @Test
  public void suffixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".x", "");
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
    assertGroup(tmpMatcher, 2, 4, "bx");

    tmpMatcher = createMatcher(".x", "xbxc");
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".x", "axbxc");
    assertGroup(tmpMatcher, 2, 4, "bx");
  }

  @Test
  public void suffixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".*x", "");
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
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".+x", "");
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
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".?x", "");
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
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".?x", "axbx");
    assertGroup(tmpMatcher, 2, 4, "bx");

    tmpMatcher = createMatcher(".?x", "xbxc");
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".?x", "axbxc");
    assertGroup(tmpMatcher, 2, 4, "bx");
  }

  @Test
  public void prefixAndSuffixDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.x", "");
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
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.x", "axbxcx");
    assertGroup(tmpMatcher, 3, 6, "xcx");

    tmpMatcher = createMatcher("x.x", "xbxcxd");
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.x", "axbxcxd");
    assertGroup(tmpMatcher, 3, 6, "xcx");
  }

  @Test
  public void prefixAndSuffixDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.*x", "");
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
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.*x", "axbxcx");
    assertGroup(tmpMatcher, 3, 6, "xcx");

    tmpMatcher = createMatcher("x.*x", "xbxcxd");
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.*x", "axbxcxd");
    assertGroup(tmpMatcher, 3, 6, "xcx");
  }

  @Test
  public void prefixAndSuffixDotPlus() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.+x", "");
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
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.+x", "axbxcx");
    assertGroup(tmpMatcher, 3, 6, "xcx");

    tmpMatcher = createMatcher("x.+x", "xbxcxd");
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.+x", "axbxcxd");
    assertGroup(tmpMatcher, 3, 6, "xcx");
  }

  @Test
  public void prefixAndSuffixDotQuest() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher("x.?x", "");
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
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.?x", "axbxcx");
    assertGroup(tmpMatcher, 3, 6, "xcx");

    tmpMatcher = createMatcher("x.?x", "xbxcxd");
    assertGroup(tmpMatcher, 2, 5, "xcx");

    tmpMatcher = createMatcher("x.?x", "axbxcxd");
    assertGroup(tmpMatcher, 3, 6, "xcx");
  }

  @Test
  public void dotMiddleDot() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".x.", "");
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
    assertGroup(tmpMatcher, 2, 5, "bxc");
  }

  @Test
  public void dotStarMiddleDotStar() throws Exception {
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".*x.*", "");
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
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".+x.+", "");
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
    AutomatonFromEndMatcher tmpMatcher = createMatcher(".?x.?", "");
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
    assertGroup(tmpMatcher, 1, 3, "bx");

    tmpMatcher = createMatcher(".?x.?", "axbx");
    assertGroup(tmpMatcher, 2, 4, "bx");

    tmpMatcher = createMatcher(".?x.?", "xbxc");
    assertGroup(tmpMatcher, 1, 4, "bxc");

    tmpMatcher = createMatcher(".?x.?", "axbxc");
    assertGroup(tmpMatcher, 2, 5, "bxc");
  }

  private AutomatonFromEndMatcher createMatcher(String aPattern, String aText) {
    Automaton tmpAutomaton = new RegExp(aPattern).toAutomaton();
    RunAutomaton tmpRunAutomaton = new RunAutomaton(tmpAutomaton);
    return new AutomatonFromEndMatcher(aText, tmpRunAutomaton);
  }

  private void assertGroup(AutomatonFromEndMatcher aMatcher, int aStart, int anEnd, String aGroup) {
    Assert.assertTrue(aMatcher.find());
    Assert.assertEquals(aStart, aMatcher.start());
    Assert.assertEquals(anEnd, aMatcher.end());
    Assert.assertEquals(aGroup, aMatcher.group());
  }
}
