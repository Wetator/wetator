/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;

/**
 * The common interface for a control.<br>
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
  String getDescribingText();

  /**
   * @param aContext the current {@link WetatorContext}
   * @return <code>true</code> if the control is disabled
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported by the control
   */
  boolean isDisabled(WetatorContext aContext);

  /**
   * @param aContext the current {@link WetatorContext}
   * @return <code>true</code> if the control has the focus
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported by the control
   */
  boolean hasFocus(final WetatorContext aContext);

  /**
   * Simulates moving the mouse over the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the mouse over
   */
  void mouseOver(WetatorContext aContext) throws ActionException;

  /**
   * Simulates a mouse click on the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the click
   */
  void click(WetatorContext aContext) throws ActionException;

  /**
   * Simulates a mouse double click on the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the double click
   */
  void clickDouble(WetatorContext aContext) throws ActionException;

  /**
   * Simulates a mouse right click on the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the right click
   */
  void clickRight(WetatorContext aContext) throws ActionException;

  /**
   * Simulates pressing of keys.
   *
   * @param aContext the current {@link WetatorContext}
   * @param aKeySequence the sequence of keys to simulate
   * @throws ActionException if an error occurred during the typing
   */
  void type(WetatorContext aContext, KeySequence aKeySequence) throws ActionException;

  /**
   * @param aControl the control to compare with
   * @return <code>true</code> if the given control has the same backend control
   */
  boolean hasSameBackendControl(IControl aControl);

  /**
   * @return the CSS selector of the control
   */
  String getUniqueSelector();
}