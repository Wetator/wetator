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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author rbri
 * @author frank.danek
 */
public class SearchPatternTest {

  @Test
  public void compileNull() {
    final SearchPattern tmpPattern = SearchPattern.compile(null);

    assertEquals(MatchAllSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches(""));
    assertTrue(tmpPattern.matches("test"));
  }

  @Test
  public void empty() {
    final SearchPattern tmpPattern = SearchPattern.compile("");

    assertEquals(MatchAllSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches(""));
    assertTrue(tmpPattern.matches("test"));
  }

  @Test
  public void starOnly() {
    final SearchPattern tmpPattern = SearchPattern.compile("*");

    assertEquals(MatchAllSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches(""));
    assertTrue(tmpPattern.matches("test"));
  }

  @Test
  public void starOnlyMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("***");

    assertEquals(MatchAllSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches(""));
    assertTrue(tmpPattern.matches("test"));
  }

  @Test
  public void starAtStart() {
    final SearchPattern tmpPattern = SearchPattern.compile("*rest");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("the rest"));
    assertFalse(tmpPattern.matches("the rester"));
    assertFalse(tmpPattern.matches("the tester"));
  }

  @Test
  public void starAtStartMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("***rest");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("the rest"));
    assertFalse(tmpPattern.matches("the rester"));
    assertFalse(tmpPattern.matches("the tester"));
  }

  @Test
  public void starAtEnd() {
    final SearchPattern tmpPattern = SearchPattern.compile("Test*");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Tester"));
    assertFalse(tmpPattern.matches("tester"));
  }

  @Test
  public void starAtEndMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("Test***");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Tester"));
    assertFalse(tmpPattern.matches("tester"));
  }

  @Test
  public void starInMiddle() {
    final SearchPattern tmpPattern = SearchPattern.compile("Te*st");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Teeest"));
    assertFalse(tmpPattern.matches("tester"));
  }

  @Test
  public void starInMiddleMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("Te***st");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Teeest"));
    assertFalse(tmpPattern.matches("tester"));
  }

  @Test
  public void starMany() {
    final SearchPattern tmpPattern = SearchPattern.compile("T*e*s*t");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Teeest"));
    assertFalse(tmpPattern.matches("tester"));
  }

  @Test
  public void starOnlyEscaped() {
    final SearchPattern tmpPattern = SearchPattern.compile("\\*");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("*"));
    assertFalse(tmpPattern.matches("test"));
  }

  @Test
  public void starEscaped() {
    final SearchPattern tmpPattern = SearchPattern.compile("re\\*st");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("re*st"));
    assertFalse(tmpPattern.matches("rest"));
  }

  @Test
  public void questionMarkOnly() {
    final SearchPattern tmpPattern = SearchPattern.compile("?");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches(""));
    assertTrue(tmpPattern.matches("t"));
    assertFalse(tmpPattern.matches("test"));
  }

  @Test
  public void questionMarkOnlyMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("???");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches(""));
    assertFalse(tmpPattern.matches("t"));
    assertFalse(tmpPattern.matches("te"));
    assertTrue(tmpPattern.matches("tes"));
    assertFalse(tmpPattern.matches("test"));
    assertFalse(tmpPattern.matches(" test"));
    assertFalse(tmpPattern.matches("a test"));
  }

  @Test
  public void questionMarkAtStart() {
    final SearchPattern tmpPattern = SearchPattern.compile("?est");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("Test"));
    assertFalse(tmpPattern.matches(" Test"));
    assertFalse(tmpPattern.matches("Tester"));
    assertFalse(tmpPattern.matches("the tester"));
  }

  @Test
  public void questionMarkAtStartMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("???t");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("t"));
    assertFalse(tmpPattern.matches("st"));
    assertFalse(tmpPattern.matches("est"));
    assertTrue(tmpPattern.matches("Test"));
    assertFalse(tmpPattern.matches("aTest"));
    assertFalse(tmpPattern.matches("tesT"));
    assertFalse(tmpPattern.matches("Tester"));
  }

  @Test
  public void questionMarkAtEnd() {
    final SearchPattern tmpPattern = SearchPattern.compile("Tes?");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("Tes"));
    assertTrue(tmpPattern.matches("Test"));
    assertFalse(tmpPattern.matches("Teste"));
    assertFalse(tmpPattern.matches("test"));
  }

  @Test
  public void questionMarkAtEndMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("T???");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("T"));
    assertFalse(tmpPattern.matches("Te"));
    assertFalse(tmpPattern.matches("Tes"));
    assertTrue(tmpPattern.matches("Test"));
    assertFalse(tmpPattern.matches("Teste"));
    assertFalse(tmpPattern.matches("test"));
  }

  @Test
  public void questionMarkInMiddle() {
    final SearchPattern tmpPattern = SearchPattern.compile("Te?st");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("Teest"));
    assertFalse(tmpPattern.matches("Teeest"));
    assertFalse(tmpPattern.matches("teest"));
  }

  @Test
  public void questionMarkInMiddleMultiple() {
    final SearchPattern tmpPattern = SearchPattern.compile("Te???st");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("Test"));
    assertFalse(tmpPattern.matches("Teest"));
    assertFalse(tmpPattern.matches("Teeest"));
    assertTrue(tmpPattern.matches("Teeeest"));
    assertFalse(tmpPattern.matches("Teeeeest"));
    assertFalse(tmpPattern.matches("teeeest"));
  }

  @Test
  public void questionMarkMany() {
    final SearchPattern tmpPattern = SearchPattern.compile("T?e?s?t");

    assertEquals(RegExpSearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertFalse(tmpPattern.matches("Test"));
    assertTrue(tmpPattern.matches("TTeesxt"));
    assertFalse(tmpPattern.matches("Teeest"));
    assertFalse(tmpPattern.matches("tTeesxt"));
  }

  @Test
  public void questionMarkOnlyEscaped() {
    final SearchPattern tmpPattern = SearchPattern.compile("\\?");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("?"));
    assertFalse(tmpPattern.matches("t"));
  }

  @Test
  public void questionMarkEscaped() {
    final SearchPattern tmpPattern = SearchPattern.compile("te\\?st");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("te?st"));
    assertFalse(tmpPattern.matches("teest"));
  }

  @Test
  public void backslash() {
    final SearchPattern tmpPattern = SearchPattern.compile("ab\\cd");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("ab\\cd"));
  }

  @Test
  public void backslashAtStart() {
    final SearchPattern tmpPattern = SearchPattern.compile("\\ab");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("\\ab"));
  }

  @Test
  public void backslashAtEnd() {
    final SearchPattern tmpPattern = SearchPattern.compile("ab\\");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("ab\\"));
  }

  @Test
  public void specialCharactersBrackets() {
    final SearchPattern tmpPattern = SearchPattern.compile("t[e](s)t");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("t[e](s)t"));
  }

  @Test
  public void specialCharactersRepetition() {
    SearchPattern tmpPattern = SearchPattern.compile("tes+t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("tes+t"));
    assertFalse(tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("tes{1}t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("tes{1}t"));
    assertFalse(tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("tes{0,4}t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("tes{0,4}t"));
    assertFalse(tmpPattern.matches("test"));
  }

  @Test
  public void specialCharactersAnchors() {
    SearchPattern tmpPattern = SearchPattern.compile("^test");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("^test"));
    assertFalse(tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("test$");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("test$"));
    assertFalse(tmpPattern.matches("test"));

    tmpPattern = SearchPattern.compile("te$t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("te$t"));
    assertFalse(tmpPattern.matches("te\nt"));
  }

  @Test
  public void specialCharactersEscaped() {
    SearchPattern tmpPattern = SearchPattern.compile("t\\[e\\]t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("t\\[e\\]t"));

    tmpPattern = SearchPattern.compile("t\\(e\\)t");
    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("t\\(e\\)t"));
  }

  @Test
  public void whiteSpaces() {
    final SearchPattern tmpPattern = SearchPattern.compile("a\tb\rc\nd");

    assertEquals(TextOnlySearchPattern.class.getName(), tmpPattern.getClass().getName());
    assertTrue(tmpPattern.matches("a\tb\rc\nd"));
    assertFalse(tmpPattern.matches("a\\tb\\rc\\nd"));
  }

  @Test
  public void matchesAtEnd_null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertFalse(tmpPattern.matchesAtEnd(null));
  }

  @Test
  public void matchesAtEnd_empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertFalse(tmpPattern.matchesAtEnd(""));
  }

  @Test
  public void matchesAtEnd_same() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertTrue(tmpPattern.matchesAtEnd("abc"));
  }

  @Test
  public void matchesAtEnd_atStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertFalse(tmpPattern.matchesAtEnd("abcdef"));
  }

  @Test
  public void matchesAtEnd_inTheMiddle() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertFalse(tmpPattern.matchesAtEnd("xyzabcdef"));
  }

  @Test
  public void matchesAtEnd_atEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc");
    assertTrue(tmpPattern.matchesAtEnd("12abc"));
  }

  @Test
  public void matchesAtEnd_StartAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*abc");
    assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void matchesAtEnd_Star() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a*bc");
    assertTrue(tmpPattern.matchesAtEnd("xya__bc"));
  }

  @Test
  public void matchesAtEnd_StartAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc*");
    assertTrue(tmpPattern.matchesAtEnd("xyabccd"));
  }

  @Test
  public void matchesAtEnd_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?abc");
    assertTrue(tmpPattern.matchesAtEnd("xyabc"));
  }

  @Test
  public void matchesAtEnd_QuestionMark() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("a?bc");
    assertTrue(tmpPattern.matchesAtEnd("xya_bc"));
  }

  @Test
  public void matchesAtEnd_QuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("abc?");
    assertTrue(tmpPattern.matchesAtEnd("xyabcd"));
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn(null);

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("");

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("f");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("fa");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("af");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afe");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("ff");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afdfg");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abc f xyz f");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_RightTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_QuestionMarkAtEndRightTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f*?");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_QuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f?");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcfgh");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_ManyQuestionMarkAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f???");
    final int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("afbcfgh");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    assertEquals(2, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsAfterLastOccurenceIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsAfterLastOccurenceIn("abcdfabcdgh");
    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn(null);

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("");

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("f");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fa");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("af");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afe");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("ff");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("afdfg");

    assertEquals(3, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abc f xyz f");

    assertEquals(10, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_LeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_QuestionMarkAtStartLeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_ManyQuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    final int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcfgfh");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("123abcdfgh");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("11abcdfabcdgh");
    assertEquals(7, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfCharsBeforeLastOccurenceIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfCharsBeforeLastOccurenceIn("abcdfabcdgh");
    assertEquals(5, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Null() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn(null);

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Empty() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("");

    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_CompleteMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("f");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("fa");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("af");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_MatchInside() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("afe");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_CompleteDoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("ff");

    assertEquals(1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_DoubleMatch() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("afdfg");

    assertEquals(4, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_LastMatchAtEnd() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abc f xyz f");

    assertEquals(10, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_LeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_StartOnlyPattern() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("*");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(0, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_QuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(4, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_QuestionMarkAtStartLeftTruncated() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("?*f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgh");

    assertEquals(2, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_ManyQuestionMarkAtStart() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("???f");
    final int tmpResult = tmpPattern.noOfSurroundingCharsIn("abcfgfh");

    assertEquals(3, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Mixed() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab?*d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("123abcdfgh");
    assertEquals(6, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("11abcdfabxycdgh");
    assertEquals(4, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abdfabgh");
    assertEquals(-1, tmpResult);
  }

  @Test
  public void noOfSurroundingCharsIn_Mixed2() {
    final SearchPattern tmpPattern;

    tmpPattern = SearchPattern.compile("ab*?d");

    int tmpResult = tmpPattern.noOfSurroundingCharsIn("fghabcd");
    assertEquals(3, tmpResult);

    tmpResult = tmpPattern.noOfSurroundingCharsIn("abccdfabcdgh");
    assertEquals(2, tmpResult);
  }

  @Test
  @SuppressWarnings("unlikely-arg-type")
  public void equals() {
    final SearchPattern tmpPattern = SearchPattern.compile("te*");

    assertTrue(tmpPattern.matches("test"));
    assertFalse("test".equals(tmpPattern));
    assertFalse(tmpPattern.equals(SearchPattern.compile("test")));
    assertTrue(tmpPattern.equals(SearchPattern.compile("te*")));
    assertEquals("te*", tmpPattern.getOriginalString());
  }

  @Test
  public void testToString() {
    // match all
    SearchPattern tmpPattern = SearchPattern.compile(null);
    assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("");
    assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("*");
    assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("**");
    assertEquals("SearchPattern '*' [matchAll]", tmpPattern.toString());

    // text only
    tmpPattern = SearchPattern.compile("abcd");
    assertEquals("SearchPattern 'abcd' [text: 'abcd']", tmpPattern.toString());
    tmpPattern = SearchPattern.compile("a b\tcd");
    assertEquals("SearchPattern 'a b\tcd' [text: 'a b\tcd']", tmpPattern.toString());

    // wildcards
    tmpPattern = SearchPattern.compile("a*b?c d");
    assertEquals("SearchPattern 'a*b?c d' [regexp: 'a.*b.c d']", tmpPattern.toString());
  }
}
