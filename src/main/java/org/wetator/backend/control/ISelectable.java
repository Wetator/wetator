/*
 * Copyright (c) 2008-2025 wetator.org
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

import org.wetator.backend.ControlFeature;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;

/**
 * This interface marks all selectable {@link IControl}s.
 *
 * @see ControlFeature#SELECT
 * @author frank.danek
 */
public interface ISelectable extends IControl {

  /**
   * Selects the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the select
   */
  void select(WetatorContext aContext) throws ActionException;

  /**
   * @param aContext the current {@link WetatorContext}
   * @return <code>true</code> if the control is selected
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported for the control
   */
  boolean isSelected(WetatorContext aContext);
}
