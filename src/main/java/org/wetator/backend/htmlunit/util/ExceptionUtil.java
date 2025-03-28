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


package org.wetator.backend.htmlunit.util;

import org.htmlunit.ScriptException;
import org.htmlunit.corejs.javascript.WrappedException;

/**
 * Utility class for exception handling.
 *
 * @author rbri
 */
public final class ExceptionUtil {

  /**
   * This class should not be instantiated.
   */
  private ExceptionUtil() {
    // nothing
  }

  /**
   * Try to get the included script exception.
   *
   * @param aWrappedException the exception to analyze
   * @return the ScriptException
   */
  public static Exception getScriptExceptionCauseIfPossible(final WrappedException aWrappedException) {
    Throwable tmpThrowable = aWrappedException.getCause();

    while (null != tmpThrowable) {
      if (tmpThrowable instanceof ScriptException) {
        return (Exception) tmpThrowable;
      }
      tmpThrowable = tmpThrowable.getCause();
    }
    return aWrappedException;
  }

}
