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

import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;

/**
 * This interface marks all deselectable {@link Control}s. These controls are returned by
 * {@link org.wetator.backend.ControlFinder#getAllDeselectables(org.wetator.backend.WPath)}. All deselectable control
 * are also
 * selectable.
 * 
 * @author frank.danek
 */
public interface Deselectable extends Selectable {

  /**
   * Deselects the control.
   * 
   * @param aWetContext the wet context
   * @throws AssertionFailedException if the control supports no deselect
   */
  public void deselect(WetContext aWetContext) throws AssertionFailedException;
}
