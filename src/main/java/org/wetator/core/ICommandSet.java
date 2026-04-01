/*
 * Copyright (c) 2008-2026 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A collection of available commands.
 * The implementation of this interface also holds the implementation of the commands.
 *
 * @author rbri
 * @author frank.danek
 */
public interface ICommandSet {

  /**
   * Initialize everything the command set needs here. To leave messages for the result presentation use
   * {@link #getInitializationMessages()}.
   *
   * @param aConfiguration The configuration properties.
   * @throws org.wetator.exception.ConfigurationException in case of problems during initialization
   */
  void initialize(Properties aConfiguration);

  /**
   * @return The messages (e.g. info, warnings, errors) stored during initialization of the command set.
   */
  List<String> getInitializationMessages();

  /**
   * Close everything the command set needed and which has to be closed (e.g. database connections).
   */
  void cleanup();

  /**
   * Returns the {@link ICommandImplementation} for the given command name or null, if no {@link ICommandImplementation}
   * was found.
   *
   * @param aCommandName The name of the {@link ICommandImplementation}.
   * @return The found {@link ICommandImplementation}.
   */
  ICommandImplementation getCommandImplementationFor(String aCommandName);

  /**
   * Returns the names of all commands provided by this command set.
   *
   * @return an unmodifiable set of command names, never null
   */
  default Set<String> getCommandNames() {
    return Collections.emptySet();
  }

  /**
   * Returns the {@link CommandDescriptor} for the given command name, or null if not available.
   *
   * @param aCommandName the name of the command
   * @return the descriptor or null
   */
  default CommandDescriptor getCommandDescriptor(final String aCommandName) {
    return null;
  }

  /**
   * Returns all {@link CommandDescriptor}s provided by this command set.
   *
   * @return an unmodifiable map of command name to descriptor, never null
   */
  default Map<String, CommandDescriptor> getCommandDescriptors() {
    return Collections.emptyMap();
  }
}
