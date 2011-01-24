/*
 * Copyright (c) 2008-2011 www.wetator.org
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

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author rbri
 */
public class SearchPatternNoOfCharsBeforeLastOccurenceInTest {

  @Test
  public void nullPattern() {
    String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
  }

  @Test
  public void empty() {
    String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
  }

  @Test
  public void oneChar() {
    String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
  }

  @Test
  public void text() {
    String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(7, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(7, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("Wet");
    Assert.assertEquals(4, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("ato");
    Assert.assertEquals(1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(-1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*We*or*");
    Assert.assertEquals(0, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertEquals(1, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("We*t");
    Assert.assertEquals(4, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("W*at");
    Assert.assertEquals(2, tmpPattern.noOfCharsAfterLastOccurenceIn(tmpMatcher));
  }
}
