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

import java.util.List;
import java.util.Properties;

/**
 * A collection of available commands.
 * The implementation of this interface also holds the implementation of the commands.
 * 
 * @author rbri
 * @author frank.danek
 */
public interface WetCommandSet {

  /**
   * Initialize everything the command set needs here. To leave messages for the result presentation use
   * {@link #getInitializationMessages()}.
   * 
   * @param aConfiguration The configuration properties.
   */
  public void initialize(Properties aConfiguration);

  /**
   * @return The messages (e.g. info, warnings, errors) stored during initialization of the command set.
   */
  public List<String> getInitializationMessages();

  /**
   * Close everything the command set needed and which has to be closed (e.g. database connections).
   */
  public void cleanup();

  /**
   * Returns the {@link WetCommandImplementation} for the given command name or null, if no
   * {@link WetCommandImplementation} was found.
   * 
   * @param aCommandName The name of the {@link WetCommandImplementation}.
   * @return The found {@link WetCommandImplementation}.
   */
  public WetCommandImplementation getCommandImplementationFor(String aCommandName);
}
