/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.backend.control;

import org.wetator.core.WetConfiguration;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;

/**
 * The common interface for a control.<br/>
 * It includes the actions and checks valid for all controls. If a control does not support an action or a check, an
 * {@link AssertionFailedException} is thrown when calling this action or check.
 * 
 * @author rbri
 * @author frank.danek
 */
public interface Control {

  /**
   * @return the description of the control
   */
  public String getDescribingText();

  /**
   * @param aWetContext the wet context
   * @return true, if the control is disabled
   * @throws AssertionFailedException if the check is not supported by the control
   */
  public boolean isDisabled(WetContext aWetContext) throws AssertionFailedException;

  /**
   * @param aWetContext the wet context
   * @return true, if the control has the focus
   * @throws AssertionFailedException if the check is not supported by the control
   */
  public boolean hasFocus(final WetContext aWetContext) throws AssertionFailedException;

  /**
   * Simulates moving the mouse over the control.
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for mouse events
   */
  public void mouseOver(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Simulates a mouse click on the control.
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for clicks
   */
  public void click(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Simulates a mouse double click on the control.
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for clicks
   */
  public void clickDouble(WetContext aWetContext) throws AssertionFailedException;

  /**
   * Simulates a mouse right click on the control.
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the the control has no support for clicks
   */
  public void clickRight(WetContext aWetContext) throws AssertionFailedException;

  /**
   * @param aControl the control to compare with
   * @return true, if the given control has the same backend control
   */
  public boolean hasSameBackendControl(Control aControl);

  /**
   * Retrieves the style to be used for highlight this control from the configuration
   * and adds the style to the control.
   * 
   * @param aWetConfiguration the wet configuration
   */
  public void addHighlightStyle(WetConfiguration aWetConfiguration);
}