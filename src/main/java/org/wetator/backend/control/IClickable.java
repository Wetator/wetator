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

/**
 * This interface marks all clickable {@link IControl}s.<br>
 * As all controls implement {@link #click(org.wetator.core.WetatorContext)} no additional action needs to be
 * implemented.
 *
 * @see ControlFeature#CLICK
 * @see ControlFeature#CLICK_DOUBLE
 * @see ControlFeature#CLICK_RIGHT
 * @author frank.danek
 */
public interface IClickable extends IControl {
  // nothing so far
}
