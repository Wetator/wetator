/*
 * Copyright (c) 2008-2015 wetator.org
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

import org.junit.Test;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author tobwoerk
 */
public class ContentPatternTest {

  @Test
  public void constructorNull() {
    try {
      new ContentPattern(null);
      org.junit.Assert.fail("InvalidInputException expected");
    } catch (final InvalidInputException e) {
      org.junit.Assert.assertEquals("Pattern '' is invalid (empty patterns not supported).", e.getMessage());
    }
  }

  @Test
  public void constructorEmpty() {
    try {
      new ContentPattern(new SecretString());
      org.junit.Assert.fail("InvalidInputException expected");
    } catch (final InvalidInputException e) {
      org.junit.Assert.assertEquals("Pattern '' is invalid (empty patterns not supported).", e.getMessage());
    }
  }

  @Test
  public void matches() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a b c", 100);
    tmpPattern.matches("x a b c y", 100);

    tmpPattern.matches("a bb c", 100);

    tmpPattern.matches("a c b c", 100);
    tmpPattern.matches("b c a b c", 100);
  }

  @Test
  public void matchesDots() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("def, ...");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches(" abc def ghi ... xyz", 100);
  }

  @Test
  public void matchesWrongOrder() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c, d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b d", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, [c], d' (content: 'a c b d').", e.getMessage());
    }
  }

  @Test
  public void matchesNotFound() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, b, {c}' (content: 'a b').", e.getMessage());
    }
  }

  @Test
  public void matchesWrongOrderNotFound() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c, d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("c a d", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, [c], d' (content: 'c a d').", e.getMessage());
    }

    try {
      tmpPattern.matches("a c d", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'a, {b}, c, d' (content: 'a c d').", e.getMessage());
    }
  }

  @Test
  public void matchesNotSoNiceErrorMessage() throws InvalidInputException {
    // in this case our error message has to be improved

    final SecretString tmpExpected = new SecretString("A, Patents, Search, Patents, R");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("A Patent Search Patents R", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'A, Patents, [Search], [Patents], R' (content: 'A Patent Search Patents R').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegated() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a", 100);
    tmpPattern.matches("a c", 100);
  }

  @Test
  public void matchesNegated2() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("~a, b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("b", 100);
    tmpPattern.matches("c b", 100);
  }

  @Test
  public void matchesNegated3() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a c", 100);
    tmpPattern.matches("a c b", 100);
    tmpPattern.matches("b a c", 100);
  }

  @Test
  public void matchesNegated4() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, ~c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a", 100);
    tmpPattern.matches("a d", 100);
    tmpPattern.matches("b a", 100);
    tmpPattern.matches("c a", 100);
    tmpPattern.matches("b c a", 100);
    tmpPattern.matches("c b a", 100);
  }

  @Test
  public void matchesNegated5() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c, ~d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a c", 100);
    tmpPattern.matches("a c b", 100);
    tmpPattern.matches("a d c", 100);
    tmpPattern.matches("a d c b", 100);
  }

  @Test
  public void matchesNegatedFailes() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}' (content: 'a c b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedFailes2() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("~a, b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a c b", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: '{~a}, b' (content: 'a c b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '~a, {b}' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedFailes3() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c' (content: 'a c b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b, c' (content: 'c').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedFails4() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, ~c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, {~c}' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, ~c' (content: 'a b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, ~b, {~c}' (content: 'a c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, ~c' (content: 'a c b').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedFails6() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c, ~d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b c", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c, ~d' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c d", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Expected content(s) {found but should not}: 'a, ~b, c, {~d}' (content: 'a c d').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b c d", 100);
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals(
          "Expected content(s) {found but should not}: 'a, {~b}, c, {~d}' (content: 'a b c d').", e.getMessage());
    }
  }

  @Test
  public void patternNegatedInvalid() {
    try {
      new ContentPattern(new SecretString("~a"));
      org.junit.Assert.fail("AssertionException expected");
    } catch (final InvalidInputException e) {
      org.junit.Assert.assertEquals("Pattern '~a' is invalid (pattern must contain one not negated term at least).",
          e.getMessage());
    }
  }
}
