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


package org.rbri.wet.backend;

import java.io.File;

import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.SecretString;

/**
 * The common interface for the Control.
 * 
 * @author rbri
 */
public interface Control {

  /**
   * Returns a description of the control.
   * 
   * @return a description string
   */
  public String getDescribingText();

  /**
   * Returns true, if the control is selected
   * 
   * @param aWetContext the wet context
   * @return true or false
   * @throws AssertionFailedException if the check is not supported for the control
   */
  public boolean isSelected(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Returns true, if the control is disabled
   * 
   * @param aWetContext the wet context
   * @return true or false
   * @throws AssertionFailedException if the check is not supported for the control
   */
  public boolean isDisabled(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Returns the value of the control
   * 
   * @param aWetContext the wet context
   * @return the value as string
   * @throws AssertionFailedException if the the control supports no value
   */
  public String getValue(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Selects the control
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the control is not supported
   */
  public void select(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Deselects the control
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the control is not supported
   */
  public void deselect(WetContext aWetContext) throws AssertionFailedException;

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
   * Simulates a (mouse) click on the control
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for clicks
   */
  public void click(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Simulates moving the (mouse) over the control
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for mouse events
   */
  public void mouseOver(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Checks, if the provided Control has the same backend control
   * 
   * @param aControl the control to compare with
   * @return true or false
   */
  public boolean hasSameBackendControl(Control aControl);
}