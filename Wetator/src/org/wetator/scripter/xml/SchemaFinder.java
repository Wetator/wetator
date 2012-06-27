/*
 * Copyright (c) 2008-2012 wetator.org
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

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class finds the schema declarations in XML data.
 * 
 * @author frank.danek
 */
public class SchemaFinder {

  private List<XMLSchema> schemas;

  /**
   * The constructor.
   * 
   * @param anXMLReader a reader reading the XML data
   * @throws XMLStreamException in case of problems
   */
  public SchemaFinder(final Reader anXMLReader) throws XMLStreamException {
    findSchemas(anXMLReader);
  }

  private void findSchemas(final Reader anXMLReader) throws XMLStreamException {
    final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    final XMLStreamReader tmpReader = tmpFactory.createXMLStreamReader(anXMLReader);

    schemas = new ArrayList<XMLSchema>();
    try {
      while (tmpReader.hasNext()) {
        if (tmpReader.next() == XMLStreamConstants.START_ELEMENT) {
          final int tmpSchemaCount = tmpReader.getNamespaceCount();

          if (tmpSchemaCount > 0) {
            final Map<String, String> tmpNamespacePrefixes = new HashMap<String, String>();
            for (int i = 0; i < tmpSchemaCount; i++) {
              final String tmpPrefix = tmpReader.getNamespacePrefix(i);
              final String tmpNamespaceURI = tmpReader.getNamespaceURI(i);
              tmpNamespacePrefixes.put(tmpNamespaceURI, tmpPrefix);
            }

            String tmpSchemaLocation = tmpReader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                "schemaLocation");
            if (tmpSchemaLocation != null) {
              tmpSchemaLocation = tmpSchemaLocation.replace("  ", " ");
              final String[] tmpSchemaLocations = tmpSchemaLocation.split(" ");
              for (int i = 0; i < tmpSchemaLocations.length; i += 2) {
                if (!"".equals(tmpSchemaLocations[i].trim())) {
                  final String tmpNamespaceURI = tmpSchemaLocations[i];
                  final String tmpPrefix = tmpNamespacePrefixes.get(tmpNamespaceURI);
                  final XMLSchema tmpSchema = new XMLSchema(tmpPrefix, tmpNamespaceURI, tmpSchemaLocations[i + 1]);
                  schemas.add(tmpSchema);
                }
              }
            }
          }
          break;
        }
      }
    } finally {
      try {
        tmpReader.close();
      } catch (final XMLStreamException e) {
        // ignore
      }
    }
  }

  /**
   * @return the schemas
   */
  public List<XMLSchema> getSchemas() {
    return schemas;
  }
}