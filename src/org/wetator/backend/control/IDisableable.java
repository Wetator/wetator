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

/**
 * This interface marks all disableable (and enableable) {@link IControl}s. These controls are returned by
 * {@link org.wetator.backend.IControlFinder#getAllDisableables(org.wetator.backend.WPath)}.
 *
 * @author frank.danek
 */
public interface IDisableable extends IControl {
  // nothing so far

  // FIXME move IControl#isDisabled() here?
  // the default impl currently just throws an exception -> should never be called
  // all really disableable controls overwrite it
  // but: Clickables, Settables and Selectables then do not offer it by itself as
  // they do not extend Disableable but just Control
  // -> let Settable and Selectable extend Disableable? is it a good idea to bind these interfaces together? what about
  // e.g. embeds or objects?
  // -> Clickable cannot extend Disableable as not all clickables are disableable (e.g. anchors or images)
}
