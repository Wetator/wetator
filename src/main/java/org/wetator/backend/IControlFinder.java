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


package org.wetator.backend;

import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.control.IDisableable;
import org.wetator.backend.control.IFocusable;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.control.ISettable;

/**
 * A control finder is responsible for finding the correct controls in a page to execute an action or assertion on them.
 *
 * @author rbri
 * @author frank.danek
 */
public interface IControlFinder {

  /**
   * Returns a list of all controls for the given {@link WPath} supporting the given {@link ControlFeature}.<br>
   * For {@link ControlFeature#SET} all returned controls implement {@link ISettable}.<br>
   * For {@link ControlFeature#SELECT} all returned controls implement {@link ISelectable}.<br>
   * For {@link ControlFeature#DESELECT} all returned controls implement {@link IDeselectable}.<br>
   * For {@link ControlFeature#DISABLE} all returned controls implement {@link IDisableable}.<br>
   * For {@link ControlFeature#FOCUS} all returned controls implement {@link IFocusable}.<br>
   * For all other features all returned controls implement {@link IControl}.<br>
   *
   * @param aFeature the {@link ControlFeature} that needs to be supported
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList findControls(ControlFeature aFeature, WPath aWPath);
}