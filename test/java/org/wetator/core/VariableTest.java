/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class VariableTest {

  @Test
  public void stringOnlyNameNull() {
    try {
      new Variable(null, "value");
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringOnlyNameEmpty() {
    try {
      new Variable("", "value");
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringOnlyNameBlank() {
    try {
      new Variable(" ", "value");
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringOnlyValueNull() {
    final Variable tmpVariable = new Variable("TestName", (String) null);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("", tmpVariable.getValue().toString());
  }

  @Test
  public void stringOnlyValueEmpty() {
    final Variable tmpVariable = new Variable("TestName", "");

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("", tmpVariable.getValue().toString());
  }

  @Test
  public void stringOnlyValueBlank() {
    final Variable tmpVariable = new Variable("TestName", " ");

    assertEquals("TestName", tmpVariable.getName());
    assertEquals(" ", tmpVariable.getValue().getValue());
    assertEquals(" ", tmpVariable.getValue().toString());
  }

  @Test
  public void stringOnly() {
    final Variable tmpVariable = new Variable("TestName", "value");

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().getValue());
    assertEquals("value", tmpVariable.getValue().toString());
  }

  @Test
  public void stringPublicNameNull() {
    try {
      new Variable(null, "value", false);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringPublicNameEmpty() {
    try {
      new Variable("", "value", false);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringPublicNameBlank() {
    try {
      new Variable(" ", "value", false);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringPublicValueNull() {
    final Variable tmpVariable = new Variable("TestName", null, false);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("", tmpVariable.getValue().toString());
  }

  @Test
  public void stringPublicValueEmpty() {
    final Variable tmpVariable = new Variable("TestName", "", false);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("", tmpVariable.getValue().toString());
  }

  @Test
  public void stringPublicValueBlank() {
    final Variable tmpVariable = new Variable("TestName", " ", false);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals(" ", tmpVariable.getValue().getValue());
    assertEquals(" ", tmpVariable.getValue().toString());
  }

  @Test
  public void stringPublic() {
    final Variable tmpVariable = new Variable("TestName", "value", false);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().getValue());
    assertEquals("value", tmpVariable.getValue().toString());
  }

  @Test
  public void stringSecretNameNull() {
    try {
      new Variable(null, "value", true);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringSecretNameEmpty() {
    try {
      new Variable("", "value", true);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringSecretNameBlank() {
    try {
      new Variable(" ", "value", true);
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void stringSecretValueNull() {
    final Variable tmpVariable = new Variable("TestName", null, true);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("", tmpVariable.getValue().toString());
  }

  @Test
  public void stringSecretValueEmpty() {
    final Variable tmpVariable = new Variable("TestName", "", true);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("", tmpVariable.getValue().getValue());
    assertEquals("****", tmpVariable.getValue().toString());
  }

  @Test
  public void stringSecretValueBlank() {
    final Variable tmpVariable = new Variable("TestName", " ", true);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals(" ", tmpVariable.getValue().getValue());
    assertEquals("****", tmpVariable.getValue().toString());
  }

  @Test
  public void stringSecret() {
    final Variable tmpVariable = new Variable("TestName", "value", true);

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().getValue());
    assertEquals("****", tmpVariable.getValue().toString());
  }

  @Test
  public void secretStringNameNull() {
    try {
      new Variable(null, new SecretString().appendSecret("value"));
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void secreStringtNameEmpty() {
    try {
      new Variable("", new SecretString().appendSecret("value"));
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void secretStringNameBlank() {
    try {
      new Variable(" ", new SecretString().appendSecret("value"));
    } catch (final IllegalArgumentException e) {
      assertEquals("The variable's name is mandatory.", e.getMessage());
    }
  }

  @Test
  public void secretString() {
    final Variable tmpVariable = new Variable("TestName", new SecretString().appendSecret("value"));

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().getValue());
    assertEquals("****", tmpVariable.getValue().toString());
  }
}
