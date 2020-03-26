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

/**
 * Contains all supported actions.
 *
 * @author frank.danek
 */
public enum Action {

  /** Single left click on a control. */
  CLICK,
  /** Double left click on a control. */
  CLICK_DOUBLE,
  /** Single right click on a control. */
  CLICK_RIGHT,
  /** Move the mouse over a control. */
  MOUSE_OVER,
  /** Set the value of a control. */
  SET,
  /** Select a control. */
  SELECT,
  /** Deselect a control. */
  DESELECT,
  /** Disable a control. */
  DISABLE,
  /** Focus a control. */
  FOCUS,
}
