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
  private String uri;
  private String schemaLocation;

  /**
   * Constructor.
   * 
   * @param aUri the uri to set
   * @param aSchemaLocation the schemaLocation to set
   */
  public XMLSchema(final String aUri, final String aSchemaLocation) {
    super();
    uri = aUri;
    schemaLocation = aSchemaLocation;
  }

  /**
   * Constructor.
   * 
   * @param aNamespace the namespace to set
   * @param aUri the uri to set
   * @param aSchemaLocation the schemaLocation to set
   */
  public XMLSchema(final String aNamespace, final String aUri, final String aSchemaLocation) {
    super();
    prefix = aNamespace;
    uri = aUri;
    schemaLocation = aSchemaLocation;
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
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * @param aUri the uri to set
   */
  public void setUri(final String aUri) {
    uri = aUri;
  }

  /**
   * @return the schemaLocation
   */
  public String getSchemaLocation() {
    return schemaLocation;
  }

  /**
   * @param aSchemaLocation the schemaLocation to set
   */
  public void setSchemaLocation(final String aSchemaLocation) {
    schemaLocation = aSchemaLocation;
  }
}
