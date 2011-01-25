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


package org.wetator.commandset;

import org.wetator.core.WetCommand;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;

/**
 * The interface every implemented command
 * fulfills.
 * 
 * @author rbri
 */
public interface WetCommandImplementation {

  /**
   * This method executes the given WetCommand. The implementation of this method must contain the logic for executing
   * the WetCommand.
   * 
   * @param aWetContext The current WetContext.
   * @param aWetCommand The WetCommand to execute.
   * @throws AssertionFailedException in case of a wrong assertion (if the command is an assert).
   */
  public void execute(WetContext aWetContext, WetCommand aWetCommand) throws AssertionFailedException;

}
