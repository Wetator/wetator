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


package org.wetator.util;

import org.junit.Test;

/**
 * @author rbri
 */
public class NormalizedStringTest {

  @Test
  public void defaultConstructor() {
    final NormalizedString tmpResult = new NormalizedString();
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());
  }

  @Test
  public void constructor_Null() {
    final NormalizedString tmpResult = new NormalizedString(null);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());
  }

  @Test
  public void constructor_Empty() {
    final NormalizedString tmpResult = new NormalizedString("");
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());
  }

  @Test
  public void constructor() {
    final NormalizedString tmpResult = new NormalizedString("abcd");
    org.junit.Assert.assertEquals(4, tmpResult.length());
    org.junit.Assert.assertEquals("abcd", tmpResult.toString());
  }

  @Test
  public void constructor_Blanks() {
    final NormalizedString tmpResult = new NormalizedString(" ab   cd  ");
    org.junit.Assert.assertEquals(5, tmpResult.length());
    org.junit.Assert.assertEquals("ab cd", tmpResult.toString());
  }

  @Test
  public void constructor_LineBreak() {
    final NormalizedString tmpResult = new NormalizedString("ab\r\n\nc\n\nd\r\r");
    org.junit.Assert.assertEquals(6, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d", tmpResult.toString());
  }

  @Test
  public void constructor_SpecialWhitespace() {
    final NormalizedString tmpResult = new NormalizedString("ab" + (char) 160 + "cd");
    org.junit.Assert.assertEquals(5, tmpResult.length());
    org.junit.Assert.assertEquals("ab cd", tmpResult.toString());
  }

  @Test
  public void append() {
    final NormalizedString tmpResult = new NormalizedString();

    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    tmpResult.append("  ab");
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("ab", tmpResult.toString());

    tmpResult.append("  c ");
    org.junit.Assert.assertEquals(4, tmpResult.length());
    org.junit.Assert.assertEquals("ab c", tmpResult.toString());

    tmpResult.append("\n\n\n \r\rd ");
    org.junit.Assert.assertEquals(6, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d", tmpResult.toString());

    tmpResult.append("x");
    org.junit.Assert.assertEquals(8, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d x", tmpResult.toString());

    tmpResult.append("y");
    org.junit.Assert.assertEquals(9, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d xy", tmpResult.toString());

    tmpResult.append("\r\n");
    org.junit.Assert.assertEquals(9, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d xy", tmpResult.toString());

    tmpResult.append("z");
    org.junit.Assert.assertEquals(11, tmpResult.length());
    org.junit.Assert.assertEquals("ab c d xy z", tmpResult.toString());
  }

  @Test
  public void appendChars() {
    final NormalizedString tmpResult = new NormalizedString();

    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    tmpResult.append(new char[] { }, 0);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    tmpResult.append(new char[] { }, -1);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    tmpResult.append(new char[] { 'a', 'b' }, 2);
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("ab", tmpResult.toString());

    tmpResult.append(new char[] { 'x', 'y', 'z' }, 2);
    org.junit.Assert.assertEquals(4, tmpResult.length());
    org.junit.Assert.assertEquals("abxy", tmpResult.toString());

    tmpResult.append(new char[] { ' ' }, 1);
    org.junit.Assert.assertEquals(4, tmpResult.length());
    org.junit.Assert.assertEquals("abxy", tmpResult.toString());
  }

  @Test
  public void substring_Empty() {
    final NormalizedString tmpTestable = new NormalizedString();

    final String tmpResult = tmpTestable.substring(0, 0);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 1);
      org.junit.Assert.fail("StringIndexOutOfBoundsException expected");
    } catch (final StringIndexOutOfBoundsException e) {
      org.junit.Assert.assertEquals("NormalizedString index out of range: 1 lenght: 0.", e.getMessage());
    }

    try {
      tmpTestable.substring(0, 2);
      org.junit.Assert.fail("StringIndexOutOfBoundsException expected");
    } catch (final StringIndexOutOfBoundsException e) {
      org.junit.Assert.assertEquals("NormalizedString index out of range: 2 lenght: 0.", e.getMessage());
    }
  }

  @Test
  public void substring_BlankAtEnd() {
    final NormalizedString tmpTestable = new NormalizedString("abc ");

    String tmpResult = tmpTestable.substring(0, 2);
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("ab", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    org.junit.Assert.assertEquals(3, tmpResult.length());
    org.junit.Assert.assertEquals("abc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("bc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 4);
      org.junit.Assert.fail("StringIndexOutOfBoundsException expected");
    } catch (final StringIndexOutOfBoundsException e) {
      org.junit.Assert.assertEquals("NormalizedString index out of range: 4 lenght: 3.", e.getMessage());
    }
  }

  @Test
  public void substring_BlankAtStart() {
    final NormalizedString tmpTestable = new NormalizedString(" abc");

    String tmpResult = tmpTestable.substring(0, 2);
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("ab", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    org.junit.Assert.assertEquals(3, tmpResult.length());
    org.junit.Assert.assertEquals("abc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    org.junit.Assert.assertEquals(2, tmpResult.length());
    org.junit.Assert.assertEquals("bc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 4);
      org.junit.Assert.fail("StringIndexOutOfBoundsException expected");
    } catch (final StringIndexOutOfBoundsException e) {
      org.junit.Assert.assertEquals("NormalizedString index out of range: 4 lenght: 3.", e.getMessage());
    }
  }

  @Test
  public void substring_BlankInside() {
    final NormalizedString tmpTestable = new NormalizedString("a bc");

    String tmpResult = tmpTestable.substring(0, 2);
    org.junit.Assert.assertEquals(1, tmpResult.length());
    org.junit.Assert.assertEquals("a", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    org.junit.Assert.assertEquals(3, tmpResult.length());
    org.junit.Assert.assertEquals("a b", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    org.junit.Assert.assertEquals(1, tmpResult.length());
    org.junit.Assert.assertEquals("b", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    org.junit.Assert.assertEquals(0, tmpResult.length());
    org.junit.Assert.assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 5);
      org.junit.Assert.fail("StringIndexOutOfBoundsException expected");
    } catch (final StringIndexOutOfBoundsException e) {
      org.junit.Assert.assertEquals("NormalizedString index out of range: 5 lenght: 4.", e.getMessage());
    }
  }
}
