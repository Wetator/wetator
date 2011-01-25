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


package org.wetator.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;

/**
 * An object that breaks a value into a list of parts and stores them.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class Parameter {
  /**
   * The delimiter between two parts of a parameter.
   */
  public static final String PARAMETER_DELIMITER = ",";
  /**
   * The character to escape the {@link #PARAMETER_DELIMITER}.
   */
  public static final char PARAMETER_ESCAPE_CHAR = '\\';

  private String value;
  private List<Part> parts;

  /**
   * Constructor.
   * 
   * @param aValue the value of this parameter
   */
  public Parameter(final String aValue) {
    value = aValue;
  }

  /**
   * Constructs a secret string from the value.
   * 
   * @param aWetContext the wet context needed to resolve the value (variables)
   * @return the constructed secret string
   */
  public SecretString getValue(final WetContext aWetContext) {
    return aWetContext.replaceVariables(value);
  }

  /**
   * @return the first part
   */
  public Part getFirstPart() {
    parseIfNeeded();
    return parts.get(0);
  }

  /**
   * @return the number of parts
   */
  public int getNumberOfParts() {
    parseIfNeeded();
    return parts.size();
  }

  /**
   * @return a list containing all parts. This is not the original list stored in this object.
   */
  public List<Part> getParts() {
    parseIfNeeded();
    return Collections.unmodifiableList(parts);
  }

  private void parseIfNeeded() {
    if (null != parts) {
      return;
    }

    parts = new LinkedList<Part>();
    if (StringUtils.isEmpty(value)) {
      return;
    }

    final List<String> tmpParts = StringUtil.extractStrings(value, PARAMETER_DELIMITER, PARAMETER_ESCAPE_CHAR);
    for (String tmpString : tmpParts) {
      final Part tmpPart = new Part(tmpString.trim());
      parts.add(tmpPart);
    }
  }

  /**
   * Returns the <b>RAW</b> value. Secrets are not replaced and so are readable. Use this method with greatest
   * care according security.
   * 
   * @return the raw value
   */
  // TODO how can we assure security here?
  public String getValue() {
    return value;
  }

  /**
   * An object that stores a flat string parameter.
   */
  public static final class Part {

    private String value;

    /**
     * Constructor.
     * 
     * @param aValue the value of this part
     */
    protected Part(final String aValue) {
      value = aValue;
    }

    /**
     * Constructs a secret string from the value.
     * 
     * @param aWetContext the wet context needed to resolve the value (variables)
     * @return the constructed secret string
     */
    public SecretString getValue(final WetContext aWetContext) {
      return aWetContext.replaceVariables(value);
    }
  }
}