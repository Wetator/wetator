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

import org.wetator.core.WetatorContext;

/**
 * This interface marks all focusable {@link IControl}s. These controls are returned by
 * {@link org.wetator.backend.IControlFinder#getAllFocusables(org.wetator.backend.WPath)}.
 *
 * @author frank.danek
 */
public interface IFocusable extends IControl {

  // FIXME move IControl#canReceiveFocus() here?
  // the default impl currently just returns false
  // all really focusable controls overwrite it
  // but: Clickables, Settables and Selectables then do not offer it by itself as
  // they do not extend Focusable but just Control
  // -> let Settable and Selectable extend Focusable? is it a good idea to bind these interfaces together? what about
  // e.g. embeds or objects?
  // -> Clickable cannot extend Focusable as not all clickables are focusable (e.g. images)

  /**
   * @param aContext the current {@link WetatorContext}
   * @return <code>true</code> if the control has the focus
   */
  boolean hasFocus(WetatorContext aContext);
}
