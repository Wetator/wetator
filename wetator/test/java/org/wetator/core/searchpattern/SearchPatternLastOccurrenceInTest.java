/*
 * Copyright (c) 2008-2016 wetator.org
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
public class SearchPatternLastOccurrenceInTest {

  @Test
  public void nullPattern() {
    final String tmpMatcher = null;

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void empty() {
    final String tmpMatcher = "";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void oneChar() {
    final String tmpMatcher = "X";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("f");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*X*");
    Assert.assertEquals(new FindSpot(0, 1), tmpPattern.lastOccurenceIn(tmpMatcher));
  }

  @Test
  public void text() {
    final String tmpMatcher = "Wetator";

    // match all
    SearchPattern tmpPattern = SearchPattern.compile((String) null);
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("");
    Assert.assertEquals(FindSpot.NOT_FOUND, tmpPattern.lastOccurenceIn(tmpMatcher));

    // static string
    tmpPattern = SearchPattern.compile("Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("X");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("wetator");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("tor");
    Assert.assertEquals(new FindSpot(4, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("ato");
    Assert.assertEquals(new FindSpot(3, 6), tmpPattern.lastOccurenceIn(tmpMatcher));

    // regexp
    tmpPattern = SearchPattern.compile("f*x");
    Assert.assertEquals(null, tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*Wetator*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("*We*or*");
    Assert.assertEquals(new FindSpot(0, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("et*o");
    Assert.assertEquals(new FindSpot(1, 6), tmpPattern.lastOccurenceIn(tmpMatcher));
    tmpPattern = SearchPattern.compile("e*r");
    Assert.assertEquals(new FindSpot(1, 7), tmpPattern.lastOccurenceIn(tmpMatcher));
  }
}
