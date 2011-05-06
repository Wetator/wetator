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


package org.wetator.scripter.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wetator.scripter.XmlScripter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This implementation of the interface {@link EntityResolver} uses a local list of known schema files to resolve the
 * entity. The local schema files must be places in the sub-package 'xsd'.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class LocalEntityResolver implements EntityResolver {

  private static final Map<String, List<String>> KNOWN_SCHEMAS = getKnownSchemas();

  private static final String XSD_DIRECTORY = "xsd/";

  private static Map<String, List<String>> getKnownSchemas() {
    final Map<String, List<String>> tmpKnownSchemas = new HashMap<String, List<String>>();
    tmpKnownSchemas.put(XmlScripter.BASE_SCHEMA, Arrays.asList("test-case-1.0.0.xsd"));
    tmpKnownSchemas.put(ModelBuilder.DEFAULT_COMMAND_SET_SCHEMA, Arrays.asList("default-command-set-1.0.0.xsd"));
    tmpKnownSchemas.put("http://www.wetator.org/xsd/sql-command-set", Arrays.asList("sql-command-set-1.0.0.xsd"));
    tmpKnownSchemas.put("http://www.wetator.org/xsd/test-command-set", Arrays.asList("test-command-set-1.0.0.xsd"));
    tmpKnownSchemas.put("http://www.wetator.org/xsd/incubator-command-set",
        Arrays.asList("incubator-command-set-1.0.0.xsd"));
    return tmpKnownSchemas;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
   */
  @Override
  public InputSource resolveEntity(final String aPublicId, final String aSystemId) throws SAXException, IOException {
    final List<String> tmpKnownSchemaFiles = KNOWN_SCHEMAS.get(aPublicId);
    if (tmpKnownSchemaFiles != null) {
      for (String tmpKnownSchemaFile : tmpKnownSchemaFiles) {
        if (aSystemId.equals(tmpKnownSchemaFile) || aSystemId.endsWith("/" + tmpKnownSchemaFile)
            || aSystemId.endsWith("\\" + tmpKnownSchemaFile)) {
          final InputSource tmpSource = new InputSource(getClass().getResourceAsStream(
              XSD_DIRECTORY + tmpKnownSchemaFile));
          tmpSource.setPublicId(aPublicId);
          tmpSource.setSystemId(getClass().getResource(XSD_DIRECTORY + tmpKnownSchemaFile).toExternalForm());
          return tmpSource;
        }
      }
    }
    return null;
  }
}
