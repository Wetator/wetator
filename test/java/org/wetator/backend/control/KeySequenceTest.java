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


package org.wetator.backend.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.wetator.backend.control.KeySequence.Key;

/**
 * @author frank.danek
 */
public class KeySequenceTest {

  @Test
  public void type() throws Exception {
    final KeySequence tmpKeySequence = new KeySequence();

    tmpKeySequence.type(' ');
    assertEquals(" ", tmpKeySequence.toString());
    assertEquals(1, tmpKeySequence.getKeys().size());
    assertEquals(' ', tmpKeySequence.getKeys().get(0).getChar());

    tmpKeySequence.type('a');
    assertEquals(" a", tmpKeySequence.toString());
    assertEquals(2, tmpKeySequence.getKeys().size());
    assertEquals('a', tmpKeySequence.getKeys().get(1).getChar());

    tmpKeySequence.type('B');
    assertEquals(" aB", tmpKeySequence.toString());
    assertEquals(3, tmpKeySequence.getKeys().size());
    assertEquals('B', tmpKeySequence.getKeys().get(2).getChar());

    tmpKeySequence.type('1');
    assertEquals(" aB1", tmpKeySequence.toString());
    assertEquals(4, tmpKeySequence.getKeys().size());
    assertEquals('1', tmpKeySequence.getKeys().get(3).getChar());

    tmpKeySequence.type('$');
    assertEquals(" aB1$", tmpKeySequence.toString());
    assertEquals(5, tmpKeySequence.getKeys().size());
    assertEquals('$', tmpKeySequence.getKeys().get(4).getChar());

    tmpKeySequence.type('\t');
    assertEquals(" aB1$\t", tmpKeySequence.toString());
    assertEquals(6, tmpKeySequence.getKeys().size());
    assertEquals('\t', tmpKeySequence.getKeys().get(5).getChar());

    tmpKeySequence.type('\n');
    assertEquals(" aB1$\t\n", tmpKeySequence.toString());
    assertEquals(7, tmpKeySequence.getKeys().size());
    assertEquals('\n', tmpKeySequence.getKeys().get(6).getChar());
  }

  @Test
  public void pressKey() throws Exception {
    final KeySequence tmpKeySequence = new KeySequence();

    tmpKeySequence.pressKey(Key.KEY_RETURN);
    assertEquals("\0", tmpKeySequence.toString());
    assertEquals(1, tmpKeySequence.getKeys().size());
    assertEquals(Key.KEY_RETURN, tmpKeySequence.getKeys().get(0));
  }

  @Test
  public void parse_null() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse(null);
    assertEquals("", tmpKeySequence.toString());
  }

  @Test
  public void parse_empty() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse("");
    assertEquals("", tmpKeySequence.toString());
  }

  @Test
  public void parse_blank() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse(" ");
    assertEquals(" ", tmpKeySequence.toString());
  }

  @Test
  public void parse() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse(" aB1$\t\n");
    assertEquals(" aB1$\t\n", tmpKeySequence.toString());
    assertEquals(7, tmpKeySequence.getKeys().size());
  }

  @Test
  public void parse_specialKey_enter() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse("[ENTER]");
    assertEquals("\0", tmpKeySequence.toString());
    assertEquals(1, tmpKeySequence.getKeys().size());
    assertEquals(Key.KEY_RETURN, tmpKeySequence.getKeys().get(0));
  }

  @Test
  public void parse_specialKey_unknown() throws Exception {
    final Exception e = assertThrows(IllegalArgumentException.class, () -> KeySequence.parse("[UNK]"));
    assertEquals("Unsupported key 'UNK'.", e.getMessage());
  }

  @Test
  public void parse_specialKey_error() throws Exception {
    final Exception e = assertThrows(IllegalArgumentException.class, () -> KeySequence.parse("[ENTER"));
    assertEquals("Invalid special key definition; closing ']' missing.", e.getMessage());
  }

  @Test
  public void parse_specialKey_escaping() throws Exception {
    final KeySequence tmpKeySequence = KeySequence.parse("\\[ENTER]");
    assertEquals("[ENTER]", tmpKeySequence.toString());
    assertEquals(7, tmpKeySequence.getKeys().size());
  }

  @Test
  public void parse_specialKey_escaping_error() throws Exception {
    final Exception e = assertThrows(IllegalArgumentException.class, () -> KeySequence.parse("\\"));
    assertEquals("Invalid escape at pos 0.", e.getMessage());
  }
}
