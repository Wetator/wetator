/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.wetator.exception.CommandException;
import org.wetator.exception.InvalidInputException;

/**
 * The interface every implemented command fulfills.
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public interface ICommandImplementation {

  /**
   * This method executes the given {@link Command}. The implementation of this method must contain the logic for
   * executing the {@link Command}.
   *
   * @param aContext the current {@link WetatorContext}
   * @param aCommand the {@link Command} to execute
   * @throws CommandException in case of a problem executing the command
   * @throws InvalidInputException in case of invalid user input
   */
  void execute(WetatorContext aContext, Command aCommand) throws CommandException, InvalidInputException;

}
