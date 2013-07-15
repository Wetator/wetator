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


package org.rbri.wet.core.variable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.util.SecretString;

/**
 * @author rbri
 */
public final class VariableTest extends TestCase {

  public static void main(String[] anArgsArray) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(VariableTest.class);
  }

  public void testConstructor() {
    Variable tmpVariable = new Variable("TestName", "value");

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().toString());
  }

  public void testConstructor_WithoutName() {
    try {
      new Variable(null, "value");
    } catch (IllegalArgumentException e) {
      assertEquals("Parameter aName can't be null.", e.getMessage());
    }
  }

  public void testConstructor_SecretFlag() {
    Variable tmpVariable = new Variable("TestName", "value", true);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("****", tmpVariable.getValue().toString());
  }

  public void testConstructor_SecretString() {
    Variable tmpVariable = new Variable("TestName", new SecretString("value", true));

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("****", tmpVariable.getValue().toString());
  }
}
