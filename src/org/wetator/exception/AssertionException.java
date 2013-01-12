/*
 * Copyright (c) 2008-2013 wetator.org
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
 * This exception is thrown if an assertion fails.
 * 
 * @author rbri
 * @author frank.danek
 */
public class AssertionException extends CommandException {

  private static final long serialVersionUID = -1587032805061848761L;

  /**
   * The constructor.
   * 
   * @param aMessage the message text
   */
  public AssertionException(final String aMessage) {
    super(aMessage);
  }

  /**
   * The constructor.
   * 
   * @param aMessage the message text
   * @param aCause the cause
   */
  public AssertionException(final String aMessage, final Throwable aCause) {
    super(aMessage, aCause);
  }
}
