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


package org.rbri.wet.backend.control;

import java.io.File;

import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.SecretString;

/**
 * @author frank.danek
 */
public interface Settable extends Control {

  /**
   * Sets the value of the control
   * 
   * @param aWetContext the wet context
   * @param aValue the new value of the control
   * @param aDirectory parameter only used for file upload controls; for this the aValue is the name of a file and
   *        aDirectory points to the dir for searching the file
   * @throws AssertionFailedException if the the control supports no value
   */
  public void setValue(WetContext aWetContext, SecretString aValue, File aDirectory) throws AssertionFailedException;

  /**
   * Returns the value of the control
   * 
   * @param aWetContext the wet context
   * @return the value as string
   * @throws AssertionFailedException if the the control supports no value
   */
  public String getValue(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Asserts that the value of the control is equal to the given (expected) value.
   * 
   * @param aWetContext the wet context
   * @param anExpectedValue the expected value of the control
   * @throws AssertionFailedException if the value of the control does not match the expected value
   */
  public void assertValue(WetContext aWetContext, SecretString anExpectedValue) throws AssertionFailedException;
}
