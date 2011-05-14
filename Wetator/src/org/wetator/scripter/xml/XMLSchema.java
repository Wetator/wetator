/*
 * Copyright (c) 2010-2011 wetator.org
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


package org.wetator.scripter.xml;

/**
 * An XML Schema meta representation bean.
 * 
 * @author tobwoerk
 */
public class XMLSchema {

  private String prefix;
  private String namespace;
  private String location;

  /**
   * Constructor.
   * 
   * @param aNamespace the namespace to set
   * @param aLocation the location to set
   */
  public XMLSchema(final String aNamespace, final String aLocation) {
    super();
    namespace = aNamespace;
    location = aLocation;
  }

  /**
   * Constructor.
   * 
   * @param aPrefix the prefix to set
   * @param aNamespace the namespace to set
   * @param aLocation the location to set
   */
  public XMLSchema(final String aPrefix, final String aNamespace, final String aLocation) {
    super();
    prefix = aPrefix;
    namespace = aNamespace;
    location = aLocation;
  }

  /**
   * @return the prefix
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * @param aPrefix the prefix to set
   */
  public void setPrefix(final String aPrefix) {
    prefix = aPrefix;
  }

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
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param aLocation the location to set
   */
  public void setLocation(final String aLocation) {
    location = aLocation;
  }
}
