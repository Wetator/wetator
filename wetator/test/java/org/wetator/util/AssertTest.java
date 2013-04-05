/*
 * Copyright (c) 2008-2013 wetator.org
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
 */
public class AssertTest {

  @Test
  public void assertEqualsSecretStringBothNull() throws AssertionException {
    Assert.assertEquals((SecretString) null, null, "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringExpectedNull() {
    try {
      Assert.assertEquals((SecretString) null, "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString(null, false), "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullSecret() {
    try {
      Assert.assertEquals(new SecretString(null, true), "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def", false), null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullSecret() {
    try {
      Assert.assertEquals(new SecretString("def", true), null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringEqualsNotSecret() throws AssertionException {
    Assert.assertEquals(new SecretString("def", false), "def", "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringEqualsSecret() throws AssertionException {
    Assert.assertEquals(new SecretString("def", true), "def", "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringNotEqualsNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def", false), "abc", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <abc>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringNotEqualsSecret() {
    try {
      Assert.assertEquals(new SecretString("def", true), "abc", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void testAssertMatch() throws AssertionException {
    Assert.assertMatch(null, null, "wrongErrorMessage", null);
    Assert.assertMatch("", "", "wrongErrorMessage", null);
    Assert.assertMatch("abc", "abc", "wrongErrorMessage", null);
    Assert.assertMatch("a*c", "abc", "wrongErrorMessage", null);

    try {
      Assert.assertMatch(null, "", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <>", e.getMessage());
    }

    try {
      Assert.assertMatch("", null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <null>", e.getMessage());
    }

    try {
      Assert.assertMatch("abc", "dxy", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <abc> but was: <dxy>", e.getMessage());
    }

    try {
      Assert.assertMatch("a*c", "abx", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionException expected");
    } catch (AssertionException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <a*c> but was: <abx>", e.getMessage());
    }
  }
}
