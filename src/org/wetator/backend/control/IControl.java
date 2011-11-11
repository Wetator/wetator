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

import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionFailedException;

/**
 * The common interface for a control.<br/>
 * It includes the actions and checks valid for all controls. If a control does not support an action or a check, an
 * {@link org.wetator.exception.UnsupportedOperationException} is thrown when calling this action or check.
 * 
 * @author rbri
 * @author frank.danek
 */
public interface IControl {

  /**
   * @return the description of the control
   */
  public String getDescribingText();

  /**
   * @param aContext the context
   * @return true, if the control is disabled
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported by the control
   */
  public boolean isDisabled(WetatorContext aContext);

  /**
   * @param aContext the context
   * @return true, if the control has the focus
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported by the control
   */
  public boolean hasFocus(final WetatorContext aContext);

  /**
   * Simulates moving the mouse over the control.
   * 
   * @param aContext the context
   * @throws ActionFailedException if an error occurred during the mouse over
   */
  public void mouseOver(WetatorContext aContext) throws ActionFailedException;

  /**
   * Simulates a mouse click on the control.
   * 
   * @param aContext the context
   * @throws ActionFailedException if an error occurred during the click
   */
  public void click(WetatorContext aContext) throws ActionFailedException;

  /**
   * Simulates a mouse double click on the control.
   * 
   * @param aContext the context
   * @throws ActionFailedException if an error occurred during the double click
   */
  public void clickDouble(WetatorContext aContext) throws ActionFailedException;

  /**
   * Simulates a mouse right click on the control.
   * 
   * @param aContext the context
   * @throws ActionFailedException if an error occurred during the right click
   */
  public void clickRight(WetatorContext aContext) throws ActionFailedException;

  /**
   * @param aControl the control to compare with
   * @return true, if the given control has the same backend control
   */
  public boolean hasSameBackendControl(IControl aControl);

  /**
   * Retrieves the style to be used for highlight this control from the configuration
   * and adds the style to the control.
   * 
   * @param aConfiguration the configuration
   */
  public void addHighlightStyle(WetatorConfiguration aConfiguration);
}