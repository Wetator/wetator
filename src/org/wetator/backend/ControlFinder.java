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


package org.wetator.backend;

/**
 * The common interface for the backend.
 * 
 * @author rbri
 * @author frank.danek
 */
public interface ControlFinder {

  /**
   * Return a list of all clickable controls for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllClickables(WPath aWPath);

  /**
   * Return a list of all selectable controls for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllDeselectables(WPath aWPath);

  /**
   * Return a list of all deselectable controls for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllSelectables(WPath aWPath);

  /**
   * Return a list of all settable controls for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllSettables(WPath aWPath);

  /**
   * Return a list of all other controls (not clickable, deselectable, selectable or settable) for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllOtherControls(WPath aWPath);

  /**
   * Return a list of all controls for the given wpath.
   * 
   * @param aWPath the wpath describing the controls
   * @return a WeightedControlList
   */
  public WeightedControlList getAllControlsForText(WPath aWPath);
}