/*
 * Copyright (c) 2008-2018 wetator.org
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
 * This exception is thrown if an operation is executed that is not supported, e.g. to check a control whether it is
 * disabled that does not support being disabled.
 *
 * @author frank.danek
 */
public class UnsupportedOperationException extends RuntimeException {

  private static final long serialVersionUID = -7074087174322447010L;

  /**
   * The constructor.
   *
   * @param aMessage the message text
   */
  public UnsupportedOperationException(final String aMessage) {
    super(aMessage);
  }

  /**
   * The constructor.
   *
   * @param aMessage the message text
   * @param aThrowable the reason of this exception
   */
  public UnsupportedOperationException(final String aMessage, final Throwable aThrowable) {
    super(aMessage, aThrowable);
  }
}