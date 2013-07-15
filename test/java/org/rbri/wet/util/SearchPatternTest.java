/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class SearchPatternTest extends TestCase {

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(SearchPatternTest.class);
  }

  public void test_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern(null);

    assertEquals(true, tmpPattern.matches(""));
    assertEquals(true, tmpPattern.matches("test"));
  }

  public void test_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("");

    assertEquals(true, tmpPattern.matches(""));
    assertEquals(true, tmpPattern.matches("test"));
  }

  public void test_StarAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Test*");

    assertEquals(true, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Tester"));
    assertEquals(false, tmpPattern.matches("tester"));
  }

  public void test_Star() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Te*st");

    assertEquals(true, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("tester"));
  }

  public void test_StarMany() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("T*e*s*t");

    assertEquals(true, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("tester"));
  }

  public void test_StarDouble() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Te****st");

    assertEquals(true, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("tester"));
  }

  public void test_StarAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*rest");

    assertEquals(true, tmpPattern.matches("ein rest"));
    assertEquals(false, tmpPattern.matches("ein rester"));
    assertEquals(false, tmpPattern.matches("ein tester"));
  }

  public void test_StarEscaped() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("re\\*st");

    assertEquals(true, tmpPattern.matches("re*st"));
    assertEquals(false, tmpPattern.matches("rest"));
  }

  public void test_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Test?");

    assertEquals(false, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teste"));
    assertEquals(false, tmpPattern.matches("teste"));
  }

  public void test_QuestionMark() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Te?st");

    assertEquals(false, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teest"));
    assertEquals(false, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("teest"));
  }

  public void test_QuestionMarkMany() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("T?e?s?t");

    assertEquals(false, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("TTeesxt"));
    assertEquals(false, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("tTeesxt"));
  }

  public void test_QuestionMarkDouble() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("Te??st");

    assertEquals(false, tmpPattern.matches("Test"));
    assertEquals(true, tmpPattern.matches("Teeest"));
    assertEquals(false, tmpPattern.matches("TeeesT"));
  }

  public void test_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?est");

    assertEquals(true, tmpPattern.matches("Test"));
    assertEquals(false, tmpPattern.matches("Tester"));
    assertEquals(false, tmpPattern.matches("ein tester"));
  }

  public void test_QuestionMarkEscaped() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("te\\?st");

    assertEquals(true, tmpPattern.matches("te?st"));
    assertEquals(false, tmpPattern.matches("teest"));
  }

  public void test_Slash() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab\\cd");

    assertEquals(true, tmpPattern.matches("ab\\cd"));
  }

  public void test_SlashAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("\\ab");

    assertEquals(true, tmpPattern.matches("\\ab"));
  }

  public void test_SlashAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab\\");

    assertEquals(true, tmpPattern.matches("ab\\"));
  }

  public void test_SpecialCharactersBrackets() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("t[e](s)t");

    assertEquals(true, tmpPattern.matches("t[e](s)t"));
  }

  public void test_SpecialCharactersRepetition() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("tes+t");
    assertEquals(true, tmpPattern.matches("tes+t"));
    assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = new SearchPattern("tes{1}t");
    assertEquals(true, tmpPattern.matches("tes{1}t"));
    assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = new SearchPattern("tes{0,4}t");
    assertEquals(true, tmpPattern.matches("tes{0,4}t"));
    assertEquals(false, tmpPattern.matches("test"));
  }

  public void test_SpecialCharactersAnchors() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("^test");
    assertEquals(true, tmpPattern.matches("^test"));
    assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = new SearchPattern("test$");
    assertEquals(true, tmpPattern.matches("test$"));
    assertEquals(false, tmpPattern.matches("test"));

    tmpPattern = new SearchPattern("te$t");
    assertEquals(true, tmpPattern.matches("te$t"));
    assertEquals(false, tmpPattern.matches("te\nt"));
  }

  public void test_Tab() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab\tc");
    assertEquals(true, tmpPattern.matches("ab\tc"));
  }

  public void test_matchesAtEnd_null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertFalse(tmpPattern.matchesAtEnd(null));
  }

  public void test_matchesAtEnd_empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertFalse(tmpPattern.matchesAtEnd(""));
  }

  public void test_matchesAtEnd_same() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertTrue(tmpPattern.matchesAtEnd("abc"));
  }

  public void test_matchesAtEnd_atStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertFalse(tmpPattern.matchesAtEnd("abcdef"));
  }

  public void test_matchesAtEnd_inTheMiddle() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertFalse(tmpPattern.matchesAtEnd("xyzabcdef"));
  }

  public void test_matchesAtEnd_atEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc");
    assertTrue(tmpPattern.matchesAtEnd("12abc"));
  }

  public void test_matchesAtEnd_StartAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*abc");
    assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  public void test_matchesAtEnd_Star() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("a*bc");
    assertTrue(tmpPattern.matchesAtEnd("xya__bc"));
  }

  public void test_matchesAtEnd_StartAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc*");
    assertTrue(tmpPattern.matchesAtEnd("xyabccd"));
  }

  public void test_matchesAtEnd_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?abc");
    assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  public void test_matchesAtEnd_QuestionMark() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("a?bc");
    assertTrue(tmpPattern.matchesAtEnd("xya_bc"));
  }

  public void test_matchesAtEnd_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("abc?");
    assertTrue(tmpPattern.matchesAtEnd("xyabcd"));
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn(null);

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("");

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("af");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("fa");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("afe");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("ff");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("afdfg");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_FirstMatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("fabc f xyz f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("???f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abfcfgh");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?*f");
    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab?*d");

    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("ghabcdfabcd");
    assertEquals(2, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeFirstOccurenceIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab*?d");

    int tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeFirstOccurenceIn("ghabcdfabcd");
    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn(null);

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("");

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("fa");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("af");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afe");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("ff");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afdfg");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abc f xyz f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_RightTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f*");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(6, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_QuestionMarkAtEndRightTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f*?");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_QuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f?");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_ManyQuestionMarkAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f???");
    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afbcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab?*d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    assertEquals(2, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsAfterLastOccurenceIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab*?d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn(null);

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("");

    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fa");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("af");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afe");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("ff");

    assertEquals(1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afdfg");

    assertEquals(3, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abc f xyz f");

    assertEquals(10, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(3, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?*f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("???f");
    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgfh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab?*d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("123abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("11abcdfabcdgh");
    assertEquals(7, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  public void test_noOfCharsBeforeLastOccurenceIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab*?d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcdfabcdgh");
    assertEquals(5, tmpResult);
  }

  public void test_noOfMatchingCharsIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn(null);

    assertEquals(-1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("");

    assertEquals(-1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("f");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("fa");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("af");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("afe");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("ff");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("afdfg");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abc f xyz f");

    assertEquals(1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abcfgh");

    assertEquals(4, tmpResult);
  }

  public void test_noOfMatchingCharsIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abcfgh");

    assertEquals(6, tmpResult);
  }

  public void test_noOfMatchingCharsIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfMatchingCharsIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?*f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abcfgh");

    assertEquals(4, tmpResult);
  }

  public void test_noOfMatchingCharsIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("???f");
    int tmpResult = tmpPattern.noOfMatchingCharsIn("abcfgfh");

    assertEquals(4, tmpResult);
  }

  public void test_noOfMatchingCharsIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab?*d");

    int tmpResult = tmpPattern.noOfMatchingCharsIn("123abcdfgh");
    assertEquals(4, tmpResult);

    tmpResult = tmpPattern.noOfMatchingCharsIn("11abcdfabccdgh");
    assertEquals(5, tmpResult);

    tmpResult = tmpPattern.noOfMatchingCharsIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  public void test_noOfMatchingCharsIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab*?d");

    int tmpResult = tmpPattern.noOfMatchingCharsIn("fghabcd");
    assertEquals(4, tmpResult);

    tmpResult = tmpPattern.noOfMatchingCharsIn("abbbx4cdfabcdgh");
    assertEquals(8, tmpResult);

    tmpResult = tmpPattern.noOfMatchingCharsIn("abcdfabbbx4cdgh");
    assertEquals(8, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_Null() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn(null);

    assertEquals(-1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_Empty() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("");

    assertEquals(-1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_CompleteMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("f");

    assertEquals(0, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_MatchAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fa");

    assertEquals(1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_MatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("af");

    assertEquals(1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_MatchInside() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("afe");

    assertEquals(2, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_CompleteDoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("ff");

    assertEquals(1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_DoubleMatch() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("afdfg");

    assertEquals(4, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_LastMatchAtEnd() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abc f xyz f");

    assertEquals(10, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_LeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_StartOnlyPattern() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("*");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_QuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(4, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_QuestionMarkAtStartLeftTruncated() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("?*f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_ManyQuestionMarkAtStart() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("???f");
    int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgfh");

    assertEquals(3, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_Mixed() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab?*d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("123abcdfgh");
    assertEquals(6, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("11abcdfabxycdgh");
    assertEquals(9, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  public void test_noOfSurroundingCharsIn_Mixed2() {
    SearchPattern tmpPattern;

    tmpPattern = new SearchPattern("ab*?d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abccdfabcdgh");
    assertEquals(7, tmpResult);
  }

  public void test_Equals() {
    SearchPattern tmpPattern = new SearchPattern("te*");

    assertTrue(tmpPattern.matches("test"));
    assertFalse("test".equals(tmpPattern));
    assertFalse(tmpPattern.equals("test"));
    assertTrue(tmpPattern.equals("te*"));
    assertEquals("te*", tmpPattern.getOriginalString());
  }
}
