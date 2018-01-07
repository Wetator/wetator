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
import org.wetator.exception.AssertionException;

/**
 * @author rbri
 * @author frank.danek
 */
public class AssertTest {

  @Test
  public void assertEqualsSecretStringBothNull() throws AssertionException {
    Assert.assertEquals((SecretString) null, null, "wrongErrorMessage");
  }

  @Test
  public void assertEqualsSecretStringExpectedNull() {
    try {
      Assert.assertEquals((SecretString) null, "def", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString(null), "def", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullSecret() {
    try {
      Assert.assertEquals(new SecretString().appendSecret(null), "def", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueEmptyNotSecret() {
    try {
      Assert.assertEquals(new SecretString(""), "def", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueEmptySecret() {
    try {
      Assert.assertEquals(new SecretString().appendSecret(""), "def", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def"), null, "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullSecret() {
    try {
      Assert.assertEquals(new SecretString().appendSecret("def"), null, "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringEqualsNotSecret() throws AssertionException {
    Assert.assertEquals(new SecretString("def"), "def", "wrongErrorMessage");
  }

  @Test
  public void assertEqualsSecretStringEqualsSecret() throws AssertionException {
    Assert.assertEquals(new SecretString().appendSecret("def"), "def", "wrongErrorMessage");
  }

  @Test
  public void assertEqualsSecretStringNotEqualsNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def"), "abc", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <abc>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringNotEqualsSecret() {
    try {
      Assert.assertEquals(new SecretString().appendSecret("def"), "abc", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void testAssertMatch() throws AssertionException {
    Assert.assertMatch(null, null, "wrongErrorMessage");
    Assert.assertMatch("", "", "wrongErrorMessage");
    Assert.assertMatch("abc", "abc", "wrongErrorMessage");
    Assert.assertMatch("a*c", "abc", "wrongErrorMessage");

    try {
      Assert.assertMatch(null, "", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <>", e.getMessage());
    }

    try {
      Assert.assertMatch("", null, "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <null>", e.getMessage());
    }

    try {
      Assert.assertMatch("abc", "dxy", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <abc> but was: <dxy>", e.getMessage());
    }

    try {
      Assert.assertMatch("a*c", "abx", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <a*c> but was: <abx>", e.getMessage());
    }

    try {
      Assert.assertMatch("012345*c", "012345bx", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <...*c> but was: <...bx>", e.getMessage());
    }

    try {
      Assert.assertMatch("ab012345", "ac012345", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <ab...> but was: <ac...>", e.getMessage());
    }

    try {
      Assert.assertMatch("012345ab012345", "012345ca012345", "wrongErrorMessage");
      org.junit.Assert.fail("AssertionException expected");
    } catch (final AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <...ab...> but was: <...ca...>", e.getMessage());
    }
  }
}
