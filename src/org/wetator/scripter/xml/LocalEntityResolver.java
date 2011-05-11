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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wetator.scripter.XmlScripter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This implementation of the interface {@link EntityResolver} tries to resolve the entity
 * <ul>
 * <li>in a local list of known schema files. The local schema files must be places in the sub-package 'xsd'.</li>
 * <li>as a file in the given schema directory.</li>
 * <li>as a URL.</li>
 * </ul>
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class LocalEntityResolver implements EntityResolver {

  private static final Map<String, List<String>> KNOWN_SCHEMAS = getKnownSchemas();
  private static final String XSD_DIRECTORY = "xsd/";

  private File schemaDirectory;

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
   * The default constructor.<br/>
   * Sets the schema directory to null.
   */
  public LocalEntityResolver() {
    // nothing
  }

  /**
   * The constructor.
   * 
   * @param aSchemaDirectory the directory to look in for schema files
   */
  public LocalEntityResolver(final File aSchemaDirectory) {
    schemaDirectory = aSchemaDirectory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
   */
  @Override
  public InputSource resolveEntity(final String aPublicId, final String aSystemId) throws SAXException, IOException {
    // first try the known schemas
    final List<String> tmpKnownSchemaFiles = KNOWN_SCHEMAS.get(aPublicId);
    if (tmpKnownSchemaFiles != null) {
      for (String tmpKnownSchemaFile : tmpKnownSchemaFiles) {
        if (aSystemId.equals(tmpKnownSchemaFile) || aSystemId.endsWith("/" + tmpKnownSchemaFile)
            || aSystemId.endsWith("\\" + tmpKnownSchemaFile)) {
          final InputSource tmpInputSource = new InputSource(getClass().getResourceAsStream(
              XSD_DIRECTORY + tmpKnownSchemaFile));
          tmpInputSource.setPublicId(aPublicId);
          tmpInputSource.setSystemId(getClass().getResource(XSD_DIRECTORY + tmpKnownSchemaFile).toExternalForm());
          return tmpInputSource;
        }
      }
    }

    // nothing found so far -> try external file
    File tmpSchemaFile = new File(aSystemId);
    if (!tmpSchemaFile.isAbsolute() && schemaDirectory != null) {
      tmpSchemaFile = new File(schemaDirectory, tmpSchemaFile.getName());
    }
    if (tmpSchemaFile.exists()) {
      final InputSource tmpInputSource = new InputSource(tmpSchemaFile.toURI().toURL().toExternalForm());
      tmpInputSource.setPublicId(aPublicId);
      return tmpInputSource;
    }

    // nothing found so far -> try URL
    try {
      final InputSource tmpInputSource = new InputSource(new URL(aSystemId).toExternalForm());
      tmpInputSource.setPublicId(aPublicId);
      return tmpInputSource;
    } catch (final MalformedURLException e) {
      // nothing found anywhere
      return null;
    }
  }
}
