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

/**
 * Describes a command's metadata for tooling and discovery.
 *
 * @author jbri
 */
public final class CommandDescriptor {

  private final String name;
  private final String commandSetClassName;
  private final List<ParameterDescriptor> parameters;
  private final boolean forceExecution;
  private final String description;

  /**
   * The constructor.
   *
   * @param aName the command name (e.g. "open-url")
   * @param aCommandSetClassName the fully-qualified class name of the owning command set
   * @param aParameters the parameter descriptors (may be empty)
   * @param aForceExecution whether this command is annotated with {@link ForceExecution}
   * @param aDescription an optional human-readable description (may be null)
   */
  public CommandDescriptor(final String aName, final String aCommandSetClassName,
      final List<ParameterDescriptor> aParameters, final boolean aForceExecution, final String aDescription) {
    name = aName;
    commandSetClassName = aCommandSetClassName;
    parameters = Collections.unmodifiableList(aParameters);
    forceExecution = aForceExecution;
    description = aDescription;
  }

  /**
   * @return the command name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the fully-qualified class name of the owning command set
   */
  public String getCommandSetClassName() {
    return commandSetClassName;
  }

  /**
   * @return an unmodifiable list of parameter descriptors
   */
  public List<ParameterDescriptor> getParameters() {
    return parameters;
  }

  /**
   * @return the number of parameters (0-3)
   */
  public int getParameterCount() {
    return parameters.size();
  }

  /**
   * @return whether this command is annotated with {@link ForceExecution}
   */
  public boolean isForceExecution() {
    return forceExecution;
  }

  /**
   * @return an optional human-readable description, or null
   */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return name + " [" + commandSetClassName + "] (" + parameters.size() + " params)";
  }
}
