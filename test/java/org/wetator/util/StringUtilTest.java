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


package org.wetator.util;

import java.util.List;

import org.junit.Test;

/**
 * @author rbri
 */
public class StringUtilTest {

  @Test
  public void testExtractStrings_Null() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings(null, ",", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());
  }

  @Test
  public void testExtractStrings1() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("", "", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());

    tmpResult = StringUtil.extractStrings("", "a", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());
  }

  @Test
  public void testExtractStrings10() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1xab2ab3ab4", "ab", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("1ab2", tmpResult.get(0));
    org.junit.Assert.assertEquals("3", tmpResult.get(1));
    org.junit.Assert.assertEquals("4", tmpResult.get(2));
  }

  @Test
  public void testExtractStrings11() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xab3ab4", "ab", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2ab3", tmpResult.get(1));
    org.junit.Assert.assertEquals("4", tmpResult.get(2));
  }

  @Test
  public void testExtractStrings12() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xab", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2ab", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings13() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xa", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2xa", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings14() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2x", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2x", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings15() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1xxab2", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1x", tmpResult.get(0));
    org.junit.Assert.assertEquals("2", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings16() {
    try {
      StringUtil.extractStrings("1aa2", "a", 'a');
      org.junit.Assert.assertTrue("IllegalArgumentException expected", false);
    } catch (IllegalArgumentException e) {
      org.junit.Assert.assertEquals("Delimiter must be different from escape char.", e.getMessage());
    }
  }

  @Test
  public void testExtractStrings17() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1\\n2", "\\n", '\\');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings2() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12a34", "a", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings3() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12a34a", "a", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
    org.junit.Assert.assertEquals("", tmpResult.get(2));
  }

  @Test
  public void testExtractStrings4() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("a", "a", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("", tmpResult.get(0));
    org.junit.Assert.assertEquals("", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings5() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("aa", "a", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("", tmpResult.get(0));
    org.junit.Assert.assertEquals("", tmpResult.get(1));
    org.junit.Assert.assertEquals("", tmpResult.get(2));
  }

  @Test
  public void testExtractStrings6() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings7() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12aa34a", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34a", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings8() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1a2aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1a2", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void testExtractStrings9() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("a12aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("a12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }
}
