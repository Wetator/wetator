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

/**
 * @author rbri
 */
public class SearchPatternTest {

  @Test
  public void compileNull() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile(null);

    Assert.assertEquals(true, tmpPattern.matches(""));
    Assert.assertEquals(true, tmpPattern.matches("test"));
  }

  @Test
  public void empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("");

    Assert.assertEquals(true, tmpPattern.matches(""));
    Assert.assertEquals(true, tmpPattern.matches("test"));
  }

  @Test
  public void starAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Test*");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Tester"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void star() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te*st");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void starMany() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("T*e*s*t");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void starDouble() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te****st");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void starAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*rest");

    Assert.assertEquals(true, tmpPattern.matches("ein rest"));
    Assert.assertEquals(false, tmpPattern.matches("ein rester"));
    Assert.assertEquals(false, tmpPattern.matches("ein tester"));
  }

  @Test
  public void starEscaped() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("re\\*st");

    Assert.assertEquals(true, tmpPattern.matches("re*st"));
    Assert.assertEquals(false, tmpPattern.matches("rest"));
  }

  @Test
  public void questionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Test?");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teste"));
    Assert.assertEquals(false, tmpPattern.matches("teste"));
  }

  @Test
  public void questionMark() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te?st");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teest"));
    Assert.assertEquals(false, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("teest"));
  }

  @Test
  public void questionMarkMany() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("T?e?s?t");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("TTeesxt"));
    Assert.assertEquals(false, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tTeesxt"));
  }

  @Test
  public void questionMarkDouble() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te??st");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("TeeesT"));
  }

  @Test
  public void questionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?est");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(false, tmpPattern.matches("Tester"));
    Assert.assertEquals(false, tmpPattern.matches("ein tester"));
  }

  @Test
  public void questionMarkEscaped() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("te\\?st");

    Assert.assertEquals(true, tmpPattern.matches("te?st"));
    Assert.assertEquals(false, tmpPattern.matches("teest"));
  }

  @Test
  public void slash() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\\cd");

    Assert.assertEquals(true, tmpPattern.matches("ab\\cd"));
  }

  @Test
  public void slashAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("\\ab");

    Assert.assertEquals(true, tmpPattern.matches("\\ab"));
  }

  @Test
  public void slashAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\\");

    Assert.assertEquals(true, tmpPattern.matches("ab\\"));
  }

  @Test
  public void specialCharactersBrackets() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("t[e](s)t");

    Assert.assertEquals(true, tmpPattern.matches("t[e](s)t"));
  }

  @Test
  public void specialCharactersRepetition() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("tes+t");
    Assert.assertEquals(true, tmpPattern.matches("tes+t"));
    Assert.assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("tes{1}t");
    Assert.assertEquals(true, tmpPattern.matches("tes{1}t"));
    Assert.assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("tes{0,4}t");
    Assert.assertEquals(true, tmpPattern.matches("tes{0,4}t"));
    Assert.assertEquals(false, tmpPattern.matches("test"));
  }

  @Test
  public void specialCharactersAnchors() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("^test");
    Assert.assertEquals(true, tmpPattern.matches("^test"));
    Assert.assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("test$");
    Assert.assertEquals(true, tmpPattern.matches("test$"));
    Assert.assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("te$t");
    Assert.assertEquals(true, tmpPattern.matches("te$t"));
    Assert.assertEquals(false, tmpPattern.matches("te\nt"));
  }

  @Test
  public void specialCharactersEscaped() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("t\\[e\\]t");
    Assert.assertEquals(true, tmpPattern.matches("t\\[e\\]t"));

    tmpPattern = SearchPattern.compile("t\\(e\\)t");
    Assert.assertEquals(true, tmpPattern.matches("t\\(e\\)t"));
  }

  @Test
  public void test_Tab() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\tc");
    Assert.assertEquals(true, tmpPattern.matches("ab\tc"));
  }

  @Test
  public void matchesAtEnd_null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd(null));
  }

  @Test
  public void matchesAtEnd_empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd(""));
  }

  @Test
  public void matchesAtEnd_same() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("abc"));
  }

  @Test
  public void matchesAtEnd_atStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd("abcdef"));
  }

  @Test
  public void matchesAtEnd_inTheMiddle() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd("xyzabcdef"));
  }

  @Test
  public void matchesAtEnd_atEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("12abc"));
  }

  @Test
  public void matchesAtEnd_StartAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void matchesAtEnd_Star() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a*bc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xya__bc"));
  }

  @Test
  public void matchesAtEnd_StartAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc*");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabccd"));
  }

  @Test
  public void matchesAtEnd_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void matchesAtEnd_QuestionMark() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a?bc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xya_bc"));
  }

  @Test
  public void matchesAtEnd_QuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc?");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabcd"));
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("fa");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("af");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afe");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("ff");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afdfg");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abc f xyz f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_RightTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_QuestionMarkAtEndRightTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*?");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_QuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f?");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_ManyQuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f???");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afbcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(2, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fa");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("af");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afe");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("ff");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afdfg");

    Assert.assertEquals(3, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abc f xyz f");

    Assert.assertEquals(10, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_LeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStartLeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_ManyQuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgfh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("123abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("11abcdfabcdgh");
    Assert.assertEquals(7, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fghabcd");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(5, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("fa");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("af");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("afe");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("ff");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("afdfg");

    Assert.assertEquals(4, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abc f xyz f");

    Assert.assertEquals(10, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_LeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(4, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_QuestionMarkAtStartLeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_ManyQuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgfh");

    Assert.assertEquals(3, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("123abcdfgh");
    Assert.assertEquals(6, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("11abcdfabxycdgh");
    Assert.assertEquals(4, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fghabcd");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abccdfabcdgh");
    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void equals() {
    final SearchPattern tmpPattern = SearchPattern.compile("te*");

    Assert.assertTrue(tmpPattern.matches("test"));
    Assert.assertFalse("test".equals(tmpPattern));
    Assert.assertFalse(tmpPattern.equals(SearchPattern.compile("test")));
    Assert.assertTrue(tmpPattern.equals(SearchPattern.compile("te*")));
    Assert.assertEquals("te*", tmpPattern.getOriginalString());
  }

  @Test
  public void testToString() {
    // match all
    SearchPattern tmpPattern = SearchPattern.compile("*");
    Assert.assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("**");
    Assert.assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());

    // text only
    tmpPattern = SearchPattern.compile("abcd");
    Assert.assertEquals("SearchPattern 'abcd' [text: 'abcd']", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("a b\tcd");
    Assert.assertEquals("SearchPattern 'a b\tcd' [text: 'a b\tcd']", tmpPattern.toString());

    // wildcrads
    tmpPattern = SearchPattern.compile("a*b?c d");
    Assert.assertEquals("SearchPattern 'a*b?c d' [regexp: 'a.*b.c d']", tmpPattern.toString());
  }
}
