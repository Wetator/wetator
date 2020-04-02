/*
 * Copyright (c) 2008-2020 wetator.org
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
 * This interface marks all deselectable {@link IControl}s. All deselectable control
 * are also selectable.
 *
 * @see ControlFeature#DESELECT
 * @author frank.danek
 */
public interface IDeselectable extends ISelectable {

  /**
   * Deselects the control.
   *
   * @param aContext the current {@link WetatorContext}
   * @throws ActionException if an error occurred during the deselect
   */
  void deselect(WetatorContext aContext) throws ActionException;
}
