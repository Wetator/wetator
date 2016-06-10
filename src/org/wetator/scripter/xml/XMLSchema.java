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


package org.wetator.scripter.xml;

/**
 * An XML Schema meta representation bean.
 *
 * @author tobwoerk
 * @author frank.danek
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

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int tmpPrime = 31;
    int tmpResult = 1;
    tmpResult = tmpPrime * tmpResult;
    if (namespace != null) {
      tmpResult += namespace.hashCode();
    }
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object anObj) {
    if (this == anObj) {
      return true;
    }
    if (anObj == null) {
      return false;
    }
    if (getClass() != anObj.getClass()) {
      return false;
    }
    final XMLSchema tmpOther = (XMLSchema) anObj;
    if (namespace == null) {
      if (tmpOther.namespace != null) {
        return false;
      }
    } else if (!namespace.equals(tmpOther.namespace)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "XMLSchema [namespace=" + namespace + "]";
  }
}
