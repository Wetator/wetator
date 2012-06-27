/*
 * Copyright (c) 2008-2012 wetator.org
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
 * An object that stores a variable.
 * This supports a name, a value and a secret flag.
 * The value of variables marked as secret is never visible in any kind of output.
 * 
 * @author rbri
 */
public final class Variable {
  private String name;
  private SecretString value;

  /**
   * Constructor.
   * The value is not secret
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
   * @param anSecretFlag true if the value is a secret
   */
  public Variable(final String aName, final String aValue, final boolean anSecretFlag) {
    this(aName, new SecretString(aValue, anSecretFlag));
  }

  /**
   * Constructor.
   * 
   * @param aName the name of the variable
   * @param aValue the value as SecretString
   */
  public Variable(final String aName, final SecretString aValue) {
    super();

    if (StringUtils.isEmpty(aName)) {
      throw new IllegalArgumentException("Parameter aName can't be null.");
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
   * @return the secret string
   */
  public SecretString getValue() {
    return value;
  }
}
