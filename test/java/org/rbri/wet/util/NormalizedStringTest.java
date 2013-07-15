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
public class NormalizedStringTest extends TestCase {
  public NormalizedStringTest(String aName) {
    super(aName);
  }

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(NormalizedStringTest.class);
  }

  public void testDefaultConstructor() {
    NormalizedString tmpResult = new NormalizedString();
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());
  }

  public void testConstructor_Null() {
    NormalizedString tmpResult = new NormalizedString(null);
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());
  }

  public void testConstructor_Empty() {
    NormalizedString tmpResult = new NormalizedString("");
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());
  }

  public void testConstructor() {
    NormalizedString tmpResult = new NormalizedString("abcd");
    assertEquals(4, tmpResult.length());
    assertEquals("abcd", tmpResult.toString());
  }

  public void testConstructor_Blanks() {
    NormalizedString tmpResult = new NormalizedString(" ab   cd  ");
    assertEquals(5, tmpResult.length());
    assertEquals("ab cd", tmpResult.toString());
  }

  public void testConstructor_LineBreak() {
    NormalizedString tmpResult = new NormalizedString("ab\r\n\nc\n\nd\r\r");
    assertEquals(6, tmpResult.length());
    assertEquals("ab c d", tmpResult.toString());
  }

  public void testConstructor_SpecialWhitespace() {
    NormalizedString tmpResult = new NormalizedString("ab" + (char) 160 + "cd");
    assertEquals(5, tmpResult.length());
    assertEquals("ab cd", tmpResult.toString());
  }

  public void testAppend() {
    NormalizedString tmpResult = new NormalizedString();

    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());

    tmpResult.append("  ab");
    assertEquals(2, tmpResult.length());
    assertEquals("ab", tmpResult.toString());

    tmpResult.append("  c ");
    assertEquals(4, tmpResult.length());
    assertEquals("ab c", tmpResult.toString());

    tmpResult.append("\n\n\n \r\rd ");
    assertEquals(6, tmpResult.length());
    assertEquals("ab c d", tmpResult.toString());

    tmpResult.append("x");
    assertEquals(8, tmpResult.length());
    assertEquals("ab c d x", tmpResult.toString());

    tmpResult.append("y");
    assertEquals(9, tmpResult.length());
    assertEquals("ab c d xy", tmpResult.toString());

    tmpResult.append("\r\n");
    assertEquals(9, tmpResult.length());
    assertEquals("ab c d xy", tmpResult.toString());

    tmpResult.append("z");
    assertEquals(11, tmpResult.length());
    assertEquals("ab c d xy z", tmpResult.toString());
  }

  public void testSubstring_Empty() {
    NormalizedString tmpTestable = new NormalizedString();

    String tmpResult = tmpTestable.substring(0, 0);
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());

    try {
      tmpResult = tmpTestable.substring(0, 1);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      assertEquals("NormalizedString index out of range: 1 lenght: 0.", e.getMessage());
    }

    try {
      tmpTestable.substring(0, 2);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      assertEquals("NormalizedString index out of range: 2 lenght: 0.", e.getMessage());
    }
  }

  public void testSubstring_BlankAtEnd() {
    NormalizedString tmpTestable = new NormalizedString("abc ");

    String tmpResult = tmpTestable.substring(0, 2);
    assertEquals(2, tmpResult.length());
    assertEquals("ab", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    assertEquals(3, tmpResult.length());
    assertEquals("abc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    assertEquals(2, tmpResult.length());
    assertEquals("bc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 4);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      assertEquals("NormalizedString index out of range: 4 lenght: 3.", e.getMessage());
    }
  }

  public void testSubstring_BlankAtStart() {
    NormalizedString tmpTestable = new NormalizedString(" abc");

    String tmpResult = tmpTestable.substring(0, 2);
    assertEquals(2, tmpResult.length());
    assertEquals("ab", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    assertEquals(3, tmpResult.length());
    assertEquals("abc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    assertEquals(2, tmpResult.length());
    assertEquals("bc", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 4);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      assertEquals("NormalizedString index out of range: 4 lenght: 3.", e.getMessage());
    }
  }

  public void testSubstring_BlankInside() {
    NormalizedString tmpTestable = new NormalizedString("a bc");

    String tmpResult = tmpTestable.substring(0, 2);
    assertEquals(1, tmpResult.length());
    assertEquals("a", tmpResult.toString());

    tmpResult = tmpTestable.substring(0, 3);
    assertEquals(3, tmpResult.length());
    assertEquals("a b", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 3);
    assertEquals(1, tmpResult.length());
    assertEquals("b", tmpResult.toString());

    tmpResult = tmpTestable.substring(1, 1);
    assertEquals(0, tmpResult.length());
    assertEquals("", tmpResult.toString());

    try {
      tmpTestable.substring(0, 5);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      assertEquals("NormalizedString index out of range: 5 lenght: 4.", e.getMessage());
    }
  }
}
