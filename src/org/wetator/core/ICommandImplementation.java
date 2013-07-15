/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.core;

import org.wetator.exception.AssertionFailedException;

/**
 * The interface every implemented command fulfills.
 * 
 * @author rbri
 */
public interface ICommandImplementation {

  /**
   * This method executes the given {@link Command}. The implementation of this method must contain the logic for
   * executing the {@link Command}.
   * 
   * @param aContext The current {@link WetatorContext}.
   * @param aCommand The {@link Command} to execute.
   * @throws AssertionFailedException in case of a wrong assertion (if the command is an assert).
   */
  public void execute(WetatorContext aContext, Command aCommand) throws AssertionFailedException;

}
