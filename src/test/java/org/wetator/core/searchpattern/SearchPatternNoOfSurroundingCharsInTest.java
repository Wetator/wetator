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

/**
 * @author rbri
 * @author frank.danek
 */
public class SearchPatternNoOfSurroundingCharsInTest {

  @Test
  public void nullText() {
    final String tmpText = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // regexp
    tmpPattern = SearchPattern.compile("?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?i?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("f*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*i*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
  }

  @Test
  public void emptyText() {
    final String tmpText = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // regexp
    tmpPattern = SearchPattern.compile("?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?i?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("f*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*i*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
  }

  @Test
  public void oneCharText() {
    final String tmpText = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // regexp
    tmpPattern = SearchPattern.compile("?");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?i?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("X?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?X");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?X?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("f*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*i*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
  }

  @Test
  public void multipleCharText() {
    final String tmpText = "Wetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // text only
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("t");
    Assert.assertEquals(6, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("We");
    Assert.assertEquals(5, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("tor");
    Assert.assertEquals(4, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("tato");
    Assert.assertEquals(3, tmpPattern.noOfSurroundingCharsIn(tmpText));

    // regexp
    tmpPattern = SearchPattern.compile("?");
    Assert.assertEquals(tmpText.length() - 1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f?x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?i?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("f*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*i*");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("Wetator?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?Wetator");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?Wetator?");
    Assert.assertEquals(-1, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("Wetato?");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?etator");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?etato?");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("Wet?tor");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("Wetat?");
    Assert.assertEquals(1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?tator");
    Assert.assertEquals(1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?tat?");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("tato?");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?etat");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("t?t");
    Assert.assertEquals(4, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("t?");
    Assert.assertEquals(5, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("?t");
    Assert.assertEquals(5, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("Wetat*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*tator");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*tat*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("We*or");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("tat*");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*tat");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("t*t");
    Assert.assertEquals(4, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("t*");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("*t");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));

    tmpPattern = SearchPattern.compile("*We*or*");
    Assert.assertEquals(0, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertEquals(2, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("e*r");
    Assert.assertEquals(1, tmpPattern.noOfSurroundingCharsIn(tmpText));
    tmpPattern = SearchPattern.compile("W*o");
    Assert.assertEquals(1, tmpPattern.noOfSurroundingCharsIn(tmpText));
  }
}
