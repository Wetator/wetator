/*
 * Copyright (c) 2008-2011 www.wetator.org
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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.wetator.exception.AssertionFailedException;

/**
 * @author rbri
 */
public class AssertTest {

  @Test
  public void assertEqualsSecretStringBothNull() throws AssertionFailedException {
    Assert.assertEquals((SecretString) null, null, "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringExpectedNull() {
    try {
      Assert.assertEquals((SecretString) null, "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString(null, false), "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <def>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringExpectedValueNullSecret() {
    try {
      Assert.assertEquals(new SecretString(null, true), "def", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def", false), null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringActualNullSecret() {
    try {
      Assert.assertEquals(new SecretString("def", true), null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <null>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringEqualsNotSecret() throws AssertionFailedException {
    Assert.assertEquals(new SecretString("def", false), "def", "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringEqualsSecret() throws AssertionFailedException {
    Assert.assertEquals(new SecretString("def", true), "def", "wrongErrorMessage", null);
  }

  @Test
  public void assertEqualsSecretStringNotEqualsNotSecret() {
    try {
      Assert.assertEquals(new SecretString("def", false), "abc", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <def> but was: <abc>", e.getMessage());
    }
  }

  @Test
  public void assertEqualsSecretStringNotEqualsSecret() {
    try {
      Assert.assertEquals(new SecretString("def", true), "abc", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <****> but was: <****>", e.getMessage());
    }
  }

  @Test
  public void testAssertListMatch_Dots() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("def", "def"));
    tmpExpected.add(new SecretString("...", "..."));

    Assert.assertListMatch(tmpExpected, " abc def ghi ... xyz");
  }

  @Test
  public void testAssertListMatch_1() throws AssertionFailedException {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("GET Parameters", "GET Parameters"));
    tmpExpected.add(new SecretString("Key", "Key"));
    tmpExpected.add(new SecretString("Value", "Value"));
    tmpExpected.add(new SecretString("inputText_Name_Value InputTextNameValueTest",
        "inputText_Name_Value InputTextNameValueTest"));
    tmpExpected.add(new SecretString("OK", "OK"));

    Assert
        .assertListMatch(
            tmpExpected,
            "Request Snoopy @ rbri.de / rbri.org Home Projects NewView ProjectX jRipper WeT Links Imprint GET Parameters Key Value inputText_Name_Value InputTextNameValueTest OK POST Parameters Key Value Headers Key Value Host www.rbri.org User-Agent Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1 Accept-Language en-us,en;q=0.8,de-de;q=0.5,de;q=0.3 Referer http://wet.rbri.org/testcases/set.html Accept */* © rbri 2007, 2008");
  }

  @Test
  public void testAssertListMatch_WrongOrder() {
    List<SecretString> tmpExpected = new LinkedList<SecretString>();
    tmpExpected.add(new SecretString("Pferde", "Pferde"));
    tmpExpected.add(new SecretString("keinen", "keinen"));
    tmpExpected.add(new SecretString("fressen", "fressen"));
    tmpExpected.add(new SecretString("Gurkensalat", "Gurkensalat"));

    try {
      Assert.assertListMatch(tmpExpected, "Pferde fressen keinen Gurkensalat");
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert
          .assertEquals(
              "Expected content(s) {not found} or [in wrong order]: 'Pferde, keinen, [fressen], Gurkensalat' (content: 'Pferde fressen keinen Gurkensalat').",
              e.getMessage());
    }
  }

  @Test
  public void testAssertMatch() throws AssertionFailedException {
    Assert.assertMatch(null, null, "wrongErrorMessage", null);
    Assert.assertMatch("", "", "wrongErrorMessage", null);
    Assert.assertMatch("abc", "abc", "wrongErrorMessage", null);
    Assert.assertMatch("a*c", "abc", "wrongErrorMessage", null);

    try {
      Assert.assertMatch(null, "", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <null> but was: <>", e.getMessage());
    }

    try {
      Assert.assertMatch("", null, "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <> but was: <null>", e.getMessage());
    }

    try {
      Assert.assertMatch("abc", "dxy", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <abc> but was: <dxy>", e.getMessage());
    }

    try {
      Assert.assertMatch("a*c", "abx", "wrongErrorMessage", null);
      org.junit.Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      org.junit.Assert.assertEquals("Wrong error message: expected: <a*c> but was: <abx>", e.getMessage());
    }
  }
}
