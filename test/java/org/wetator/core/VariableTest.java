/*
 * Copyright (c) 2008-2020 wetator.org
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

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class VariableTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void stringOnlyNameNull() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(null, "value");
  }

  @Test
  public void stringOnlyNameEmpty() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable("", "value");
  }

  @Test
  public void stringOnlyNameBlank() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(" ", "value");
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
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(null, "value", false);
  }

  @Test
  public void stringPublicNameEmpty() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable("", "value", false);
  }

  @Test
  public void stringPublicNameBlank() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(" ", "value", false);
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
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(null, "value", true);
  }

  @Test
  public void stringSecretNameEmpty() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable("", "value", true);
  }

  @Test
  public void stringSecretNameBlank() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(" ", "value", true);
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
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(null, new SecretString().appendSecret("value"));
  }

  @Test
  public void secreStringtNameEmpty() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable("", new SecretString().appendSecret("value"));
  }

  @Test
  public void secretStringNameBlank() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The variable's name is mandatory.");

    new Variable(" ", new SecretString().appendSecret("value"));
  }

  @Test
  public void secretString() {
    final Variable tmpVariable = new Variable("TestName", new SecretString().appendSecret("value"));

    assertEquals("TestName", tmpVariable.getName());
    assertEquals("value", tmpVariable.getValue().getValue());
    assertEquals("****", tmpVariable.getValue().toString());
  }
}
