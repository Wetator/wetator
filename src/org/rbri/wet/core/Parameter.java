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


package org.rbri.wet.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;

/**
 * An object that stores a list of parameters.
 * 
 * @author rbri
 */
public final class Parameter {
  public static final String PARAMETER_DELIMITER = ",";
  public static final char PARAMETER_ESCAPE_CHAR = '\\';

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
    public Part(String aValue) {
      super();
      // TODO null check
      value = aValue;
    }

    /**
     * Constructs a secret string from the value
     * 
     * @param aWetContext the wet context needed to resolve the value (variables)
     * @return the constructed secret string
     */
    public SecretString getValue(WetContext aWetContext) {
      // double dispatch to resolve the variables
      SecretString tmpResult = aWetContext.replaceVariables(value);
      return tmpResult;
    }
  }

  private String value;
  private List<Part> parameters;

  /**
   * Constructor.
   * 
   * @param aValue the value of this parameter
   */
  public Parameter(String aValue) {
    super();

    value = aValue;
  }

  public SecretString getValue(WetContext aWetContext) {
    return aWetContext.replaceVariables(value);
  }

  public Part getFirstPart() {
    parseIfNeeded();
    return parameters.get(0);
  }

  public int getNumberOfParts() {
    parseIfNeeded();
    return parameters.size();
  }

  public List<Part> getParts() {
    parseIfNeeded();
    return Collections.unmodifiableList(parameters);
  }

  private void parseIfNeeded() {
    if (null != parameters) {
      return;
    }

    parameters = new LinkedList<Part>();
    if (StringUtils.isEmpty(value)) {
      return;
    }

    List<String> tmpParts = StringUtil.extractStrings(value, PARAMETER_DELIMITER, PARAMETER_ESCAPE_CHAR);
    for (String tmpString : tmpParts) {
      Part tmpPart = new Part(tmpString.trim());
      parameters.add(tmpPart);
    }
  }

  // TODO security
  public String getValue() {
    return value;
  }
}