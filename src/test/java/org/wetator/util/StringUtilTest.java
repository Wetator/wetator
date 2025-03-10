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


package org.wetator.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

/**
 * @author rbri
 */
public class StringUtilTest {

  @Test
  public void extractStrings_Null() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings(null, ",", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());
  }

  @Test
  public void extractStrings1() {
    List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("", "", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());

    tmpResult = StringUtil.extractStrings("", "a", 'x');
    org.junit.Assert.assertEquals(0, tmpResult.size());
  }

  @Test
  public void extractStrings10() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1xab2ab3ab4", "ab", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("1ab2", tmpResult.get(0));
    org.junit.Assert.assertEquals("3", tmpResult.get(1));
    org.junit.Assert.assertEquals("4", tmpResult.get(2));
  }

  @Test
  public void extractStrings11() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xab3ab4", "ab", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2ab3", tmpResult.get(1));
    org.junit.Assert.assertEquals("4", tmpResult.get(2));
  }

  @Test
  public void extractStrings12() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xab", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2ab", tmpResult.get(1));
  }

  @Test
  public void extractStrings13() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2xa", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2xa", tmpResult.get(1));
  }

  @Test
  public void extractStrings14() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1ab2x", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2x", tmpResult.get(1));
  }

  @Test
  public void extractStrings15() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1xxab2", "ab", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1x", tmpResult.get(0));
    org.junit.Assert.assertEquals("2", tmpResult.get(1));
  }

  @Test
  public void extractStrings16() {
    try {
      StringUtil.extractStrings("1aa2", "a", 'a');
      org.junit.Assert.assertTrue("IllegalArgumentException expected", false);
    } catch (final IllegalArgumentException e) {
      org.junit.Assert.assertEquals("Delimiter must be different from escape char.", e.getMessage());
    }
  }

  @Test
  public void extractStrings17() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1\\n2", "\\n", '\\');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1", tmpResult.get(0));
    org.junit.Assert.assertEquals("2", tmpResult.get(1));
  }

  @Test
  public void extractStrings2() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12a34", "a", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void extractStrings3() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12a34a", "a", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
    org.junit.Assert.assertEquals("", tmpResult.get(2));
  }

  @Test
  public void extractStrings4() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("a", "a", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("", tmpResult.get(0));
    org.junit.Assert.assertEquals("", tmpResult.get(1));
  }

  @Test
  public void extractStrings5() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("aa", "a", 'x');
    org.junit.Assert.assertEquals(3, tmpResult.size());
    org.junit.Assert.assertEquals("", tmpResult.get(0));
    org.junit.Assert.assertEquals("", tmpResult.get(1));
    org.junit.Assert.assertEquals("", tmpResult.get(2));
  }

  @Test
  public void extractStrings6() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void extractStrings7() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("12aa34a", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34a", tmpResult.get(1));
  }

  @Test
  public void extractStrings8() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("1a2aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("1a2", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void extractStrings9() {
    final List<String> tmpResult;

    tmpResult = StringUtil.extractStrings("a12aa34", "aa", 'x');
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("a12", tmpResult.get(0));
    org.junit.Assert.assertEquals("34", tmpResult.get(1));
  }

  @Test
  public void formatDate() {
    Date tmpDate = new GregorianCalendar(2010, 3, 1, 8, 30, 0).getTime();
    String tmpResult = StringUtil.formatDate(tmpDate);
    org.junit.Assert.assertEquals("01.04.2010 08:30:00", tmpResult);

    tmpDate = new GregorianCalendar(2010, 3, 1, 16, 30, 11).getTime();
    tmpResult = StringUtil.formatDate(tmpDate);
    org.junit.Assert.assertEquals("01.04.2010 16:30:11", tmpResult);
  }

  @Test
  public void split() {
    org.junit.Assert.assertEquals(0, StringUtil.split(null, 10).size());
    org.junit.Assert.assertEquals(0, StringUtil.split("", 10).size());

    List<String> tmpResult = StringUtil.split("abcd", 1);
    org.junit.Assert.assertEquals(4, tmpResult.size());
    org.junit.Assert.assertEquals("d", tmpResult.get(3));

    tmpResult = StringUtil.split("abcd", 2);
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("cd", tmpResult.get(1));

    tmpResult = StringUtil.split("abcd", 3);
    org.junit.Assert.assertEquals(2, tmpResult.size());
    org.junit.Assert.assertEquals("abc", tmpResult.get(0));
    org.junit.Assert.assertEquals("d", tmpResult.get(1));

    tmpResult = StringUtil.split("abcd", 4);
    org.junit.Assert.assertEquals(1, tmpResult.size());
    org.junit.Assert.assertEquals("abcd", tmpResult.get(0));

    tmpResult = StringUtil.split("abcd", 5);
    org.junit.Assert.assertEquals(1, tmpResult.size());
    org.junit.Assert.assertEquals("abcd", tmpResult.get(0));
  }

}
