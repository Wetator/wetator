/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.exception;

/**
 * This exception is thrown for problems during command execution.
 *
 * @author frank.danek
 */
public class CommandException extends Exception {

  private static final long serialVersionUID = 1008530384322847811L;

  /**
   * The constructor.
   *
   * @param aMessage the message text
   */
  public CommandException(final String aMessage) {
    super(aMessage);
  }

  /**
   * The constructor.
   *
   * @param aMessage the message text
   * @param aThrowable the reason of this exception
   */
  public CommandException(final String aMessage, final Throwable aThrowable) {
    super(aMessage, aThrowable);
  }
}
