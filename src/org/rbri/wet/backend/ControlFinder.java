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

import java.util.List;

import org.rbri.wet.util.SecretString;

/**
 * The common interface for the
 * backend.
 * 
 * @author rbri
 */
public interface ControlFinder {

  /**
   * Return a list of all clickables for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllClickables(List<SecretString> aSearch);

  /**
   * Return a list of all selectables for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllDeselectables(final List<SecretString> aSearch);

  /**
   * Return a list of all deselectables for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllSelectables(final List<SecretString> aSearch);

  /**
   * Return a list of all settables for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllSettables(List<SecretString> aSearch);

  /**
   * Return a list of all other controls for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllOtherControls(List<SecretString> aSearch);

  /**
   * Return a list of all elements for the given search
   * 
   * @param aSearch the list of secret strings describing the search
   * @return a WeightedControlList
   */
  public WeightedControlList getAllElementsForText(List<SecretString> aSearch);
}