/*
 * Copyright (c) 2008-2011 wetator.org
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
  public void test_Null() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile(null);

    Assert.assertEquals(true, tmpPattern.matches(""));
    Assert.assertEquals(true, tmpPattern.matches("test"));
  }

  @Test
  public void test_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("");

    Assert.assertEquals(true, tmpPattern.matches(""));
    Assert.assertEquals(true, tmpPattern.matches("test"));
  }

  @Test
  public void test_StarAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Test*");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Tester"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void test_Star() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te*st");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void test_StarMany() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("T*e*s*t");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void test_StarDouble() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te****st");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tester"));
  }

  @Test
  public void test_StarAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*rest");

    Assert.assertEquals(true, tmpPattern.matches("ein rest"));
    Assert.assertEquals(false, tmpPattern.matches("ein rester"));
    Assert.assertEquals(false, tmpPattern.matches("ein tester"));
  }

  @Test
  public void test_StarEscaped() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("re\\*st");

    Assert.assertEquals(true, tmpPattern.matches("re*st"));
    Assert.assertEquals(false, tmpPattern.matches("rest"));
  }

  @Test
  public void test_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Test?");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teste"));
    Assert.assertEquals(false, tmpPattern.matches("teste"));
  }

  @Test
  public void test_QuestionMark() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te?st");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teest"));
    Assert.assertEquals(false, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("teest"));
  }

  @Test
  public void test_QuestionMarkMany() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("T?e?s?t");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("TTeesxt"));
    Assert.assertEquals(false, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("tTeesxt"));
  }

  @Test
  public void test_QuestionMarkDouble() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("Te??st");

    Assert.assertEquals(false, tmpPattern.matches("Test"));
    Assert.assertEquals(true, tmpPattern.matches("Teeest"));
    Assert.assertEquals(false, tmpPattern.matches("TeeesT"));
  }

  @Test
  public void test_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?est");

    Assert.assertEquals(true, tmpPattern.matches("Test"));
    Assert.assertEquals(false, tmpPattern.matches("Tester"));
    Assert.assertEquals(false, tmpPattern.matches("ein tester"));
  }

  @Test
  public void test_QuestionMarkEscaped() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("te\\?st");

    Assert.assertEquals(true, tmpPattern.matches("te?st"));
    Assert.assertEquals(false, tmpPattern.matches("teest"));
  }

  @Test
  public void test_Slash() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\\cd");

    Assert.assertEquals(true, tmpPattern.matches("ab\\cd"));
  }

  @Test
  public void test_SlashAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("\\ab");

    Assert.assertEquals(true, tmpPattern.matches("\\ab"));
  }

  @Test
  public void test_SlashAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\\");

    Assert.assertEquals(true, tmpPattern.matches("ab\\"));
  }

  @Test
  public void test_SpecialCharactersBrackets() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("t[e](s)t");

    Assert.assertEquals(true, tmpPattern.matches("t[e](s)t"));
  }

  @Test
  public void test_SpecialCharactersRepetition() {
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
  public void test_SpecialCharactersAnchors() {
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
  public void test_Tab() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab\tc");
    Assert.assertEquals(true, tmpPattern.matches("ab\tc"));
  }

  @Test
  public void test_matchesAtEnd_null() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd(null));
  }

  @Test
  public void test_matchesAtEnd_empty() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd(""));
  }

  @Test
  public void test_matchesAtEnd_same() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("abc"));
  }

  @Test
  public void test_matchesAtEnd_atStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd("abcdef"));
  }

  @Test
  public void test_matchesAtEnd_inTheMiddle() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertFalse(tmpPattern.matchesAtEnd("xyzabcdef"));
  }

  @Test
  public void test_matchesAtEnd_atEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("12abc"));
  }

  @Test
  public void test_matchesAtEnd_StartAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void test_matchesAtEnd_Star() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a*bc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xya__bc"));
  }

  @Test
  public void test_matchesAtEnd_StartAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc*");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabccd"));
  }

  @Test
  public void test_matchesAtEnd_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?abc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void test_matchesAtEnd_QuestionMark() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a?bc");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xya_bc"));
  }

  @Test
  public void test_matchesAtEnd_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc?");
    Assert.assertTrue(tmpPattern.matchesAtEnd("xyabcd"));
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("fa");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("af");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afe");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("ff");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afdfg");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abc f xyz f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_RightTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(6, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_QuestionMarkAtEndRightTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*?");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f?");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_ManyQuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f???");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afbcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(2, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsAfterLastOccurenceIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fa");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("af");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afe");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("ff");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afdfg");

    Assert.assertEquals(3, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abc f xyz f");

    Assert.assertEquals(10, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(3, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgfh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("123abcdfgh");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("11abcdfabcdgh");
    Assert.assertEquals(7, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfCharsBeforeLastOccurenceIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fghabcd");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcdfabcdgh");
    Assert.assertEquals(5, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn(null);

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("");

    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("f");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fa");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("af");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("afe");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("ff");

    Assert.assertEquals(1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("afdfg");

    Assert.assertEquals(4, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abc f xyz f");

    Assert.assertEquals(10, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(0, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(4, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    Assert.assertEquals(2, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgfh");

    Assert.assertEquals(3, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("123abcdfgh");
    Assert.assertEquals(6, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("11abcdfabxycdgh");
    Assert.assertEquals(9, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abdfabgh");
    Assert.assertEquals(-1, tmpResult);
  }

  @Test
  public void test_noOfSurroundingCharsIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fghabcd");
    Assert.assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abccdfabcdgh");
    Assert.assertEquals(7, tmpResult);
  }

  @Test
  public void test_Equals() {
    SearchPattern tmpPattern = SearchPattern.compile("te*");

    Assert.assertTrue(tmpPattern.matches(new String("test")));
    Assert.assertFalse("test".equals(tmpPattern));
    Assert.assertFalse(tmpPattern.equals(new String("test")));
    Assert.assertTrue(tmpPattern.equals(new String("te*")));
    Assert.assertEquals("te*", tmpPattern.getOriginalString());
  }
}
