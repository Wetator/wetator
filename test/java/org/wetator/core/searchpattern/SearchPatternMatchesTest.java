/*
 * Copyright (c) 2008-2021 wetator.org
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

/**
 * @author rbri
 */
public class SearchPatternMatchesTest {

  @Test
  public void nullPattern() {
    final String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
  }

  @Test
  public void empty() {
    final String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
  }

  @Test
  public void oneChar() {
    final String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    tmpPattern = SearchPattern.compile("?");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*?*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("X?");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?X");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?X?");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
  }

  @Test
  public void text() {
    final String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("tor");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("ato");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("*We*or*");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("e*r");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));

    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?etator");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("Wetato?");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?etato?");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("?e???o?");
    Assert.assertTrue(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("et?t");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    tmpPattern = SearchPattern.compile("e?r");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));

    tmpPattern = SearchPattern.compile("Wet\\*r");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Wet*r"));
    tmpPattern = SearchPattern.compile("Wet\\?tor");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Wet?tor"));
    tmpPattern = SearchPattern.compile("Wet\\ator");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Wet\\ator"));
    tmpPattern = SearchPattern.compile("\\Wetator");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("\\Wetator"));
    tmpPattern = SearchPattern.compile("Wetator\\");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Wetator\\"));
    tmpPattern = SearchPattern.compile("W+e(t)a[t]o{r}");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("W+e(t)a[t]o{r}"));
    tmpPattern = SearchPattern.compile("Weta\tor");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Weta\tor"));
    tmpPattern = SearchPattern.compile("Weta\nor");
    Assert.assertFalse(tmpPattern.matches(tmpMatcher));
    Assert.assertTrue(tmpPattern.matches("Weta\nor"));
  }
}
