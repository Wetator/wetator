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

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.util.SecretString;

/**
 * An object that stores a variable.
 * This supports a name, a value and a secret flag.
 * The value of variables marked as secret is never visible in the output reports.
 * 
 * @author rbri
 */
public final class Variable {
  private String name;
  private SecretString secretValue;

  /**
   * Constructor.
   * The value is not secret
   * 
   * @param aName the name of the variable (required)
   * @param aValue the value of the variable
   */
  public Variable(String aName, String aValue) {
    this(aName, aValue, false);
  }

  /**
   * Constructor.
   * 
   * @param aName the name of the variable (required)
   * @param aValue the value of the variable
   * @param anSecretFlag true if the value is a secret
   */
  public Variable(String aName, String aValue, boolean anSecretFlag) {
    this(aName, new SecretString(aValue, anSecretFlag));
  }

  /**
   * Constructor.
   * 
   * @param aName the name of the variable
   * @param aValue the value as SecretString
   */
  public Variable(String aName, SecretString aValue) {
    super();

    if (StringUtils.isEmpty(aName)) {
      throw new IllegalArgumentException("Parameter aName can't be null.");
    }

    name = aName;
    secretValue = aValue;
  }

  /**
   * Getter for the name
   * 
   * @return the name of this variable
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the secret string
   * 
   * @return the secret string
   */
  public SecretString getValue() {
    return secretValue;
  }
}
