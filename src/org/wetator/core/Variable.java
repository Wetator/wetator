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

import org.apache.commons.lang3.StringUtils;
import org.wetator.util.SecretString;

/**
 * An object that stores a variable consisting of a {@link #getName() name} and a {@link #getValue() value}.<br>
 * The value can be marked as secret masking it in the print out (see {@link SecretString} for details). Use
 * {@link #Variable(String, String, boolean)} with the secret flag set to <code>true</code> or
 * {@link #Variable(String, SecretString)} with a secret {@link SecretString}.
 *
 * @author rbri
 * @author frank.danek
 */
public final class Variable {

  private String name;
  private SecretString value;

  /**
   * Constructor.<br>
   * The value is not secret.
   *
   * @param aName the name of the variable (required)
   * @param aValue the value of the variable
   */
  public Variable(final String aName, final String aValue) {
    this(aName, aValue, false);
  }

  /**
   * Constructor.
   *
   * @param aName the name of the variable (required)
   * @param aValue the value of the variable
   * @param anSecretFlag <code>true</code> if the value should be secret
   */
  public Variable(final String aName, final String aValue, final boolean anSecretFlag) {
    if (StringUtils.isBlank(aName)) {
      throw new IllegalArgumentException("The variable's name is mandatory.");
    }

    name = aName;
    if (anSecretFlag) {
      value = new SecretString().appendSecret(aValue);
    } else {
      value = new SecretString(aValue);
    }
  }

  /**
   * Constructor.
   *
   * @param aName the name of the variable
   * @param aValue the value of the variable as {@link SecretString}
   */
  public Variable(final String aName, final SecretString aValue) {
    if (StringUtils.isBlank(aName)) {
      throw new IllegalArgumentException("The variable's name is mandatory.");
    }

    name = aName;
    value = aValue;
  }

  /**
   * @return the name of this variable
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value of this variable
   */
  public SecretString getValue() {
    return value;
  }
}
