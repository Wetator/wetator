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

/**
 * This interface marks all disableable (and enableable) {@link IControl}s.
 *
 * @see ControlFeature#DISABLE
 * @author frank.danek
 */
public interface IDisableable extends IControl {

  /**
   * @param aContext the current {@link WetatorContext}
   * @return <code>true</code> if the control is disabled
   * @throws org.wetator.exception.UnsupportedOperationException if the check is not supported by the control
   */
  boolean isDisabled(WetatorContext aContext);
}
