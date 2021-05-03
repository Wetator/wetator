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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author rbri
 * @author frank.danek
 */
public class SearchPatternMinLengthTest {

  @Test
  public void matchAll() {
    SearchPattern tmpPattern = SearchPattern.compile(null);
    assertEquals(0, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("");
    assertEquals(0, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("*");
    assertEquals(0, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("***");
    assertEquals(0, tmpPattern.getMinLength());
  }

  @Test
  public void textOnly() {
    SearchPattern tmpPattern = SearchPattern.compile("a");
    assertEquals(1, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("abc");
    assertEquals(3, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlyWildcardsEscaped() {
    SearchPattern tmpPattern = SearchPattern.compile("\\*");
    assertEquals(1, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("\\?");
    assertEquals(1, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlyBackslash() {
    final SearchPattern tmpPattern = SearchPattern.compile("\\");
    assertEquals(1, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlySpecialCharactersBrackets() {
    final SearchPattern tmpPattern = SearchPattern.compile("t[e](s)t");
    assertEquals(8, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlySpecialCharactersRepetition() {
    SearchPattern tmpPattern = SearchPattern.compile("tes+t");
    assertEquals(5, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("tes{1}t");
    assertEquals(7, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("tes{0,4}t");
    assertEquals(9, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlySpecialCharactersAnchors() {
    SearchPattern tmpPattern = SearchPattern.compile("^test");
    assertEquals(5, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("test$");
    assertEquals(5, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("te$t");
    assertEquals(4, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlySpecialCharactersEscaped() {
    SearchPattern tmpPattern = SearchPattern.compile("t\\[e\\]t");
    assertEquals(7, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("t\\(e\\)t");
    assertEquals(7, tmpPattern.getMinLength());
  }

  @Test
  public void textOnlyWhiteSpaces() {
    final SearchPattern tmpPattern = SearchPattern.compile("\t\r\n");
    assertEquals(3, tmpPattern.getMinLength());
  }

  @Test
  public void regExpStar() {
    SearchPattern tmpPattern = SearchPattern.compile("*rest");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("***rest");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Test*");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Test***");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Te*st");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Te***st");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("T*e*s*t");
    assertEquals(4, tmpPattern.getMinLength());
  }

  @Test
  public void reqExpQuestionMark() {
    SearchPattern tmpPattern = SearchPattern.compile("?");
    assertEquals(1, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("???");
    assertEquals(3, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("?est");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("???t");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Tes?");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("T???");
    assertEquals(4, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Te?st");
    assertEquals(5, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("Te???st");
    assertEquals(7, tmpPattern.getMinLength());

    tmpPattern = SearchPattern.compile("T?e?s?t");
    assertEquals(7, tmpPattern.getMinLength());
  }
}
