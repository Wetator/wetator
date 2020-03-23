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


package org.wetator.backend;

import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.control.IDisableable;
import org.wetator.backend.control.IFocusable;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.control.ISettable;

/**
 * The common interface for the backend.
 *
 * @author rbri
 * @author frank.danek
 */
public interface IControlFinder {

  /**
   * Return a list of all clickable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link IControl}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllClickables(WPath aWPath);

  /**
   * Return a list of all settable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link ISettable}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllSettables(WPath aWPath);

  /**
   * Return a list of all selectable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link ISelectable}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllSelectables(WPath aWPath);

  /**
   * Return a list of all deselectable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link IDeselectable}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllDeselectables(WPath aWPath);

  /**
   * Return a list of all disableable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link IDisableable}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllDisableables(WPath aWPath);

  /**
   * Return a list of all focusable controls for the given {@link WPath}.<br>
   * All returned controls implement {@link IFocusable}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllFocusables(WPath aWPath);

  /**
   * Return a list of all other controls (not clickable, deselectable, selectable or settable) for the given
   * {@link WPath}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllOtherControls(WPath aWPath);

  /**
   * Return a list of all controls for the given {@link WPath}.
   *
   * @param aWPath the {@link WPath} describing the controls
   * @return a {@link WeightedControlList}
   */
  WeightedControlList getAllControlsForText(WPath aWPath);
}