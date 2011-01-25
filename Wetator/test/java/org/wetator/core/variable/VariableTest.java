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


package org.wetator.core.variable;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.util.SecretString;

/**
 * @author rbri
 */
public class VariableTest {

  @Test
  public void testConstructor() {
    Variable tmpVariable = new Variable("TestName", "value");

    Assert.assertEquals("TestName", tmpVariable.getName());
    Assert.assertEquals("value", tmpVariable.getValue().toString());
  }

  @Test
  public void testConstructor_WithoutName() {
    try {
      new Variable(null, "value");
    } catch (IllegalArgumentException e) {
      Assert.assertEquals("Parameter aName can't be null.", e.getMessage());
    }
  }

  @Test
  public void testConstructor_SecretFlag() {
    Variable tmpVariable = new Variable("TestName", "value", true);

    Assert.assertEquals("TestName", tmpVariable.getName());
    Assert.assertEquals("****", tmpVariable.getValue().toString());
  }

  @Test
  public void testConstructor_SecretString() {
    Variable tmpVariable = new Variable("TestName", new SecretString("value", true));

    Assert.assertEquals("TestName", tmpVariable.getName());
    Assert.assertEquals("****", tmpVariable.getValue().toString());
  }
}
