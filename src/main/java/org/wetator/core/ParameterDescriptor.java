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

/**
 * Describes a single parameter of a command for tooling and discovery.
 *
 * @author jbri
 */
public final class ParameterDescriptor {

  /**
   * The type of a parameter.
   */
  public enum ParameterType {
    /** A general text or WPath value. */
    STRING,
    /** A numeric (long) value. */
    LONG,
    /** A comma-separated list of values. */
    MULTI
  }

  private final int position;
  private final String name;
  private final boolean required;
  private final ParameterType type;

  /**
   * The constructor.
   *
   * @param aPosition the 0-based position of this parameter (0, 1, or 2)
   * @param aName a human-readable name for this parameter (e.g. "url", "wpath", "timeout")
   * @param aRequired whether this parameter is required
   * @param aType the type of this parameter
   */
  public ParameterDescriptor(final int aPosition, final String aName, final boolean aRequired,
      final ParameterType aType) {
    position = aPosition;
    name = aName;
    required = aRequired;
    type = aType;
  }

  /**
   * @return the 0-based position of this parameter
   */
  public int getPosition() {
    return position;
  }

  /**
   * @return a human-readable name for this parameter
   */
  public String getName() {
    return name;
  }

  /**
   * @return whether this parameter is required
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * @return the type of this parameter
   */
  public ParameterType getType() {
    return type;
  }

  @Override
  public String toString() {
    return name + " (pos=" + position + ", " + (required ? "required" : "optional") + ", " + type + ")";
  }
}
