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


package org.wetator.backend.htmlunit;

/**
 * Contains all supported mouse actions.
 *
 * @author frank.danek
 */
public enum MouseAction {

  /** A single left click. */
  CLICK,
  /** A double left click. */
  CLICK_DOUBLE,
  /** A single right click. */
  CLICK_RIGHT,
  /** A movement of the mouse over a control. */
  MOUSE_OVER
}
