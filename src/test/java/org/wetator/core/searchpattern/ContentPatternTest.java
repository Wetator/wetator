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


package org.wetator.core.searchpattern;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.exception.AssertionException;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author tobwoerk
 * @author frank.danek
 */
public class ContentPatternTest {

  @Test
  public void constructorNull() {
    try {
      new ContentPattern(null);
      Assert.fail("InvalidInputException expected");
    } catch (final InvalidInputException e) {
      Assert.assertEquals("Pattern '' is invalid (empty patterns not supported).", e.getMessage());
    }
  }

  @Test
  public void constructorEmpty() {
    try {
      new ContentPattern(new SecretString());
      Assert.fail("InvalidInputException expected");
    } catch (final InvalidInputException e) {
      Assert.assertEquals("Pattern '' is invalid (empty patterns not supported).", e.getMessage());
    }
  }

  @Test
  public void constructorOnlyNegated() {
    try {
      new ContentPattern(new SecretString("~a"));
      Assert.fail("AssertionException expected");
    } catch (final InvalidInputException e) {
      Assert.assertEquals("Pattern '~a' is invalid (pattern must contain one not negated term at least).",
          e.getMessage());
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
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: 'a, b, [c], d' (content: 'a c b d').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNotFound() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: 'a, b, {c}' (content: 'a b').",
          e.getMessage());
    }
  }

  @Test
  public void matchesWrongOrderNotFound() throws InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, b, c, d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("c a d", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: 'a, {b}, [c], d' (content: 'c a d').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c d", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: 'a, {b}, c, d' (content: 'a c d').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNotSoNiceErrorMessage() throws InvalidInputException {
    // in this case our error message has to be improved

    final SecretString tmpExpected = new SecretString("A, Patents, Search, Patents, R");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("A Patent Search Patents R", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals(
          "Expected content(s) {not found} or [in wrong order]: 'A, Patents, [Search], [Patents], R' (content: 'A Patent Search Patents R').",
          e.getMessage());
    }
  }

  @Test
  public void matchesEscapedNegated() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("\\~a");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("a", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{\\~a}' (content: 'a').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{\\~a}' (content: 'b').",
          e.getMessage());
    }

    tmpPattern.matches("~a", 100);
  }

  @Test
  public void matchesNegatedAtStart() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("~a, b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("b", 100);
    try {
      tmpPattern.matches("a", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '~a, {b}' (content: 'a').",
          e.getMessage());
    }
    try {
      tmpPattern.matches("c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '~a, {b}' (content: 'c').",
          e.getMessage());
    }

    tmpPattern.matches("c b", 100);
    tmpPattern.matches("b a", 100);
    try {
      tmpPattern.matches("a b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: '{~a}, b' (content: 'a b').", e.getMessage());
    }

    try {
      tmpPattern.matches("a c b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: '{~a}, b' (content: 'a c b').", e.getMessage());
    }
  }

  @Test
  public void matchesNegatedAtEnd() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a", 100);
    try {
      tmpPattern.matches("b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b' (content: 'b').",
          e.getMessage());
    }
    try {
      tmpPattern.matches("c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b' (content: 'c').",
          e.getMessage());
    }

    tmpPattern.matches("a c", 100);
    tmpPattern.matches("b a", 100);
    try {
      tmpPattern.matches("a b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}' (content: 'a b').", e.getMessage());
    }

    try {
      tmpPattern.matches("a c b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}' (content: 'a c b').", e.getMessage());
    }
  }

  @Test
  public void matchesNegatedInMiddle() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    try {
      tmpPattern.matches("c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {not found} or [in wrong order]: '{a}, ~b, c' (content: 'c').",
          e.getMessage());
    }

    tmpPattern.matches("a c", 100);

    tmpPattern.matches("a c b", 100);
    tmpPattern.matches("b a c", 100);
    try {
      tmpPattern.matches("a b c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c' (content: 'a c b c').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedAtEndMultiple() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, ~c");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a", 100);
    tmpPattern.matches("a d", 100);
    tmpPattern.matches("b a", 100);
    tmpPattern.matches("c a", 100);
    tmpPattern.matches("b c a", 100);
    tmpPattern.matches("c b a", 100);

    try {
      tmpPattern.matches("a b c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, {~c}' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, ~c' (content: 'a b').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, ~b, {~c}' (content: 'a c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c b", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, ~c' (content: 'a c b').",
          e.getMessage());
    }
  }

  @Test
  public void matchesNegatedMultiple() throws AssertionException, InvalidInputException {
    final SecretString tmpExpected = new SecretString("a, ~b, c, ~d");
    final ContentPattern tmpPattern = new ContentPattern(tmpExpected);

    tmpPattern.matches("a c", 100);
    tmpPattern.matches("a c b", 100);
    tmpPattern.matches("a d c", 100);
    tmpPattern.matches("a d c b", 100);

    try {
      tmpPattern.matches("a b c", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c, ~d' (content: 'a b c').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a c d", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, ~b, c, {~d}' (content: 'a c d').",
          e.getMessage());
    }

    try {
      tmpPattern.matches("a b c d", 100);
      Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      Assert.assertEquals("Expected content(s) {found but should not}: 'a, {~b}, c, {~d}' (content: 'a b c d').",
          e.getMessage());
    }
  }
}
