/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.scripter.xml.model;

/**
 * Meta-model representation of a parameter.
 *
 * @author frank.danek
 */
public class ParameterType {

  private String namespace;
  private String name;
  private String documentation;
  private boolean optional;

  /**
   * @return the namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * @param aNamespace the namespace to set
   */
  public void setNamespace(final String aNamespace) {
    namespace = aNamespace;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param aName the name to set
   */
  public void setName(final String aName) {
    name = aName;
  }

  /**
   * @return the documentation
   */
  public String getDocumentation() {
    return documentation;
  }

  /**
   * @param aDocumentation the documentation to set
   */
  public void setDocumentation(final String aDocumentation) {
    documentation = aDocumentation;
  }

  /**
   * @return the optional
   */
  public boolean isOptional() {
    return optional;
  }

  /**
   * @param anOptional the optional to set
   */
  public void setOptional(final boolean anOptional) {
    optional = anOptional;
  }

  @Override
  public String toString() {
    return namespace + ":" + name;
  }
}
