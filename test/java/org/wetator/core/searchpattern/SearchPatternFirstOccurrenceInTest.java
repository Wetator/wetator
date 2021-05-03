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
import org.wetator.util.FindSpot;

/**
 * @author rbri
 */
public class SearchPatternFirstOccurrenceInTest {

  @Test
  public void nullString() {
    final String tmpString = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
  }

  @Test
  public void empty() {
    final String tmpString = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
  }

  @Test
  public void oneChar() {
    final String tmpString = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("find");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));

    tmpPattern = SearchPattern.compile("?");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*?*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("X?");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?X");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?X?");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
  }

  @Test
  public void text() {
    final String tmpString = "Wetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    // text only
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("Wet");
    Assert.assertEquals(new FindSpot(0, 3), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("eta");
    Assert.assertEquals(new FindSpot(1, 4), tmpPattern.firstOccurenceIn(tmpString));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("*et*or*");
    Assert.assertEquals(new FindSpot(1, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertEquals(new FindSpot(1, 6), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("W*t");
    Assert.assertEquals(new FindSpot(0, 3), tmpPattern.firstOccurenceIn(tmpString));

    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?etator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("Wetato?");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?etato?");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("?e???o?");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("et?t");
    Assert.assertEquals(new FindSpot(1, 5), tmpPattern.firstOccurenceIn(tmpString));
    tmpPattern = SearchPattern.compile("e?r");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));

    tmpPattern = SearchPattern.compile("Wet\\*r");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 5), tmpPattern.firstOccurenceIn("Wet*r"));
    tmpPattern = SearchPattern.compile("Wet\\?tor");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn("Wet?tor"));
    tmpPattern = SearchPattern.compile("Wet\\ator");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 8), tmpPattern.firstOccurenceIn("Wet\\ator"));
    tmpPattern = SearchPattern.compile("\\Wetator");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 8), tmpPattern.firstOccurenceIn("\\Wetator"));
    tmpPattern = SearchPattern.compile("Wetator\\");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 8), tmpPattern.firstOccurenceIn("Wetator\\"));
    tmpPattern = SearchPattern.compile("W+e(t)a[t]o{r}");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 14), tmpPattern.firstOccurenceIn("W+e(t)a[t]o{r}"));
    tmpPattern = SearchPattern.compile("Weta\tor");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn("Weta\tor"));
    tmpPattern = SearchPattern.compile("Weta\nor");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString));
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.firstOccurenceIn("Weta\nor"));
  }

  // with start psos

  @Test
  public void startPosNull() {
    final String tmpString = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));
  }

  @Test
  public void startPosEmpty() {
    final String tmpString = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 17));
  }

  @Test
  public void startPosOneChar() {
    final String tmpString = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 0));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 0));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 0));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString, 0));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 0));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString, 0));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString, 0));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.firstOccurenceIn(tmpString, 0));
  }

  @Test
  public void startPosText() {
    final String tmpString = "myWetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 2));

    // text only
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertEquals(new FindSpot(2, 9), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("Wet");
    Assert.assertEquals(new FindSpot(2, 5), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("eta");
    Assert.assertEquals(new FindSpot(3, 6), tmpPattern.firstOccurenceIn(tmpString, 2));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertEquals(new FindSpot(2, 9), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertEquals(new FindSpot(2, 9), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertEquals(new FindSpot(2, 9), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("*et*or*");
    Assert.assertEquals(new FindSpot(3, 9), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertEquals(new FindSpot(3, 8), tmpPattern.firstOccurenceIn(tmpString, 2));
    tmpPattern = SearchPattern.compile("W*t");
    Assert.assertEquals(new FindSpot(2, 5), tmpPattern.firstOccurenceIn(tmpString, 2));
  }
}
