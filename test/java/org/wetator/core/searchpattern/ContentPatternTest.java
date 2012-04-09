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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author tobwoerk
 */
public class ContentPatternTest {

  @Test
  public void matches() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a b c");
    tmpPattern.matches("x a b c y");

    tmpPattern.matches("a bb c");

    tmpPattern.matches("a c b c");
    tmpPattern.matches("b c a b c");
  }

  @Test
  public void matches_Dots() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("def", "def"));
    tmpExpected.add(new SecretString("...", "..."));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches(" abc def ghi ... xyz");
  }

  @Test
  public void matches_WrongOrder() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("d", "d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, [c], d' (content: 'a c b d').", e.getMessage());
    }
  }

  @Test
  public void match_NotFound() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, {c}' (content: 'a b').", e.getMessage());
    }
  }

  @Test
  public void match_WrongOrderNotFound() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("b", "b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("d", "d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("c a d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, [c], d' (content: 'c a d').", e.getMessage());
    }

    try {
      tmpPattern.matches("a c d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, c, d' (content: 'a c d').", e.getMessage());
    }
  }

  @Test
  public void pattern_negatedInvalid() {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("~a", "~a"));
    try {
      new ContentPattern(tmpExpected);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Pattern must contain one not negated term at least.", e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_Negated() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a");
    tmpPattern.matches("a c");
  }

  @Test
  public void testAssertListMatch_Negated2() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("~a", "~a"));
    tmpExpected.add(new SecretString("b", "b"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("b");
    tmpPattern.matches("c b");
  }

  @Test
  public void testAssertListMatch_Negated3() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a c");
    tmpPattern.matches("a c b");
    tmpPattern.matches("b a c");
  }

  @Test
  public void testAssertListMatch_Negated4() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("~c", "~c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a");
    tmpPattern.matches("a d");
    tmpPattern.matches("b a");
    tmpPattern.matches("c a");
    tmpPattern.matches("b c a");
    tmpPattern.matches("c b a");
  }

  @Test
  public void testAssertListMatch_Negated5() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("~d", "~d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a c");
    tmpPattern.matches("a c b");
    tmpPattern.matches("a d c");
    tmpPattern.matches("a d c b");
  }

  @Test
  public void testAssertListMatch_NegatedFailes() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b' (content: 'a c b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_NegatedFailes2() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("~a", "~a"));
    tmpExpected.add(new SecretString("b", "b"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: '~a, b' (content: 'a c b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{b}' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_NegatedFailes3() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("c", "c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b, c' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b, c' (content: 'a c b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, c' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_NegatedFails4() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("~c", "~c"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b, ~c' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b' (content: 'a b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~c' (content: 'a c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b' (content: 'a c b').",
          e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_NegatedFails6() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("a", "a"));
    tmpExpected.add(new SecretString("~b", "~b"));
    tmpExpected.add(new SecretString("c", "c"));
    tmpExpected.add(new SecretString("~d", "~d"));
    ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b, c' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, c, ~d' (content: 'a c d').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b c d");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Expected content(s) found but should not: 'a, ~b, c, ~d' (content: 'a b c d').",
          e.getMessage());
    }
  }
}
