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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wetator.exception.WetException;
import org.wetator.scripter.XmlScripter;
import org.wetator.scripter.xml.model.CommandType;
import org.wetator.scripter.xml.model.ParameterType;
import org.wetator.util.NormalizedString;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

/**
 * This builder creates the model of command types and their parameter types out of an XML file by parsing the schemas.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class ModelBuilder {

  private static final String BASE_COMMAND_TYPE = "commandType";
  private static final String BASE_PARAMETER_TYPE = "parameterType";
  /**
   * The schema file for the default command set.
   */
  public static final String DEFAULT_COMMAND_SET_SCHEMA_URI = "http://www.wetator.org/xsd/default-command-set";
  private static final String DEFAULT_COMMAND_SET_XSD_LOCATION = "default-command-set-1.0.0.xsd";
  private static final String DEFAULT_COMMAND_SET_PREFIX = "d";

  private Map<String, XMLSchema> schemaLocations = new HashMap<String, XMLSchema>();

  private XSComplexType baseCommandType;
  private XSSimpleType baseParameterType;

  private Map<String, CommandType> commandTypes = new LinkedHashMap<String, CommandType>();

  /**
   * The constructor. Creates a new ModelBuilder by parsing the given file.
   * 
   * @param aFile the file to build the model from
   * @throws XMLStreamException in case of problems reading the file
   * @throws IOException in case of problems reading the file
   * @throws SAXException in case of problems reading the file
   */
  public ModelBuilder(final File aFile) throws XMLStreamException, IOException, SAXException {
    findSchemas(aFile);

    // add schema for default command set since it is always loaded
    schemaLocations.put(DEFAULT_COMMAND_SET_SCHEMA_URI, new XMLSchema(DEFAULT_COMMAND_SET_PREFIX,
        DEFAULT_COMMAND_SET_SCHEMA_URI, DEFAULT_COMMAND_SET_XSD_LOCATION));

    parseSchemas(aFile.getParentFile());
  }

  /**
   * @param aSchemaLocationMap the map containing the schemas to use (key = namespace URI, value = schema location)
   * @throws SAXException in case of problems reading the file
   * @throws IOException in case of problems reading the file
   */
  public ModelBuilder(final Map<String, XMLSchema> aSchemaLocationMap) throws SAXException, IOException {
    schemaLocations = aSchemaLocationMap;

    parseSchemas(null);
  }

  /**
   * @param aName the local name of the command type
   * @return the command type for the given parameters or null if none found
   */
  public CommandType getCommandType(final String aName) {
    return commandTypes.get(aName);
  }

  /**
   * @return a list containing all known command types
   */
  public List<CommandType> getCommandTypes() {
    final List<CommandType> tmpCommandTypes = new ArrayList<CommandType>(commandTypes.values());
    Collections.sort(tmpCommandTypes, new Comparator<CommandType>() {
      @Override
      public int compare(final CommandType aType1, final CommandType aType2) {
        return aType1.getName().compareToIgnoreCase(aType2.getName());
      }
    });
    return tmpCommandTypes;
  }

  private void findSchemas(final File aFile) throws XMLStreamException, IOException {
    final InputStream tmpInputStream = new FileInputStream(aFile);
    final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    final XMLStreamReader tmpReader = tmpFactory.createXMLStreamReader(tmpInputStream);

    try {
      while (tmpReader.hasNext()) {
        if (tmpReader.next() == XMLStreamConstants.START_ELEMENT) {
          String tmpSchemaLocation = tmpReader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
              "schemaLocation");

          final int tmpSchemaCount = tmpReader.getAttributeCount();
          final Map<String, String> tmpNamespacePrefixes = new HashMap<String, String>();
          for (int i = 0; i <= tmpSchemaCount; i++) {
            final String tmpPrefix = tmpReader.getNamespacePrefix(i);
            final String tmpNamespaceURI = tmpReader.getNamespaceURI(i);
            tmpNamespacePrefixes.put(tmpNamespaceURI, tmpPrefix);
          }

          tmpSchemaLocation = tmpSchemaLocation.replace("  ", " ");
          final String[] tmpSchemaLocations = tmpSchemaLocation.split(" ");
          for (int i = 0; i < tmpSchemaLocations.length; i += 2) {
            if (!"".equals(tmpSchemaLocations[i].trim())) {
              final String tmpNamespaceURI = tmpSchemaLocations[i];
              final String tmpPrefix = tmpNamespacePrefixes.get(tmpNamespaceURI);
              final XMLSchema tmpSchema = new XMLSchema(tmpPrefix, tmpNamespaceURI, tmpSchemaLocations[i + 1]);
              schemaLocations.put(tmpNamespaceURI, tmpSchema);
            }
          }
          break;
        }
      }
    } finally {
      tmpReader.close();
      tmpInputStream.close();
    }
  }

  private void parseSchemas(final File aSchemaDirectory) throws SAXException, IOException {
    if (schemaLocations == null || schemaLocations.isEmpty()) {
      throw new WetException("No schema to parse.");
    }

    final XSOMParser tmpParser = new XSOMParser();
    tmpParser.setAnnotationParser(new DomAnnotationParserFactory());
    tmpParser.setEntityResolver(new LocalEntityResolver(aSchemaDirectory));

    // parse all schemas
    for (Entry<String, XMLSchema> tmpSchemaLocation : schemaLocations.entrySet()) {
      final InputSource tmpSource = new LocalEntityResolver(aSchemaDirectory).resolveEntity(tmpSchemaLocation.getKey(),
          tmpSchemaLocation.getValue().getSchemaLocation());
      if (tmpSource != null) {
        try {
          tmpParser.parse(tmpSource);
        } catch (final SAXException e) {
          throw new WetException("Could not resolve schema file '" + tmpSchemaLocation.getValue().getUri() + "'.",
              e.getException());
        }
      } else {
        throw new WetException("Could not resolve schema file '" + tmpSchemaLocation.getValue().getUri() + "'.");
      }
    }

    final XSSchemaSet tmpSchemaSet = tmpParser.getResult();
    final XSSchema tmpBaseSchema = tmpSchemaSet.getSchema(XmlScripter.BASE_SCHEMA);
    if (tmpBaseSchema == null) {
      throw new WetException("No base schema '" + XmlScripter.BASE_SCHEMA + "' found.");
    }
    baseCommandType = tmpBaseSchema.getComplexType(BASE_COMMAND_TYPE);
    baseParameterType = tmpBaseSchema.getSimpleType(BASE_PARAMETER_TYPE);

    // find all command types and their parameter types
    for (final Iterator<XSElementDecl> tmpIterator = tmpSchemaSet.iterateElementDecls(); tmpIterator.hasNext();) {
      final XSElementDecl tmpElement = tmpIterator.next();
      if (tmpElement.getType().isDerivedFrom(baseCommandType) && !((XSComplexType) tmpElement.getType()).isAbstract()) {
        final XSComplexType tmpType = (XSComplexType) tmpElement.getType();

        final String tmpElementName = tmpElement.getName();
        final CommandType tmpExistingCommandType = commandTypes.get(tmpElementName);
        if (tmpExistingCommandType != null) {
          throw new RuntimeException("Duplicate command '" + tmpElementName + "' found ('"
              + tmpExistingCommandType.getNamespace() + "' and '" + tmpElement.getTargetNamespace() + "').");
        }

        // build command
        final CommandType tmpCommandType = new CommandType();
        tmpCommandType.setNamespace(tmpElement.getTargetNamespace());
        tmpCommandType.setName(tmpElementName);
        tmpCommandType.setDocumentation(getDocumentation(tmpElement));
        commandTypes.put(tmpElementName, tmpCommandType);

        for (ParameterType tmpParameterType : getParameterTypes(tmpType)) {
          final String tmpParameterName = tmpParameterType.getName();
          for (ParameterType tmpCommandParameterType : tmpCommandType.getParameterTypes()) {
            if (tmpCommandParameterType.getName().equals(tmpParameterName)) {
              throw new RuntimeException("Duplicate parameter '" + tmpElementName + "' found for command '"
                  + tmpCommandType.getNamespace() + ":" + tmpCommandType.getName() + "'.");
            }
          }
          tmpCommandType.getParameterTypes().add(tmpParameterType);
        }
      }
    }
  }

  private List<ParameterType> getParameterTypes(final XSComplexType aType) {
    final List<ParameterType> tmpParameterTypes = new ArrayList<ParameterType>();

    final XSContentType tmpXsContentType = aType.getContentType();
    final XSParticle tmpXsParticle = tmpXsContentType.asParticle();
    if (tmpXsParticle != null) {
      final XSTerm tmpXsTerm = tmpXsParticle.getTerm();
      if (tmpXsTerm.isModelGroup()) {
        final XSModelGroup tmpXsModelGroup = tmpXsTerm.asModelGroup();
        final XSParticle[] tmpXsParticles = tmpXsModelGroup.getChildren();
        for (XSParticle tmpChildParticle : tmpXsParticles) {
          final XSTerm tmpChildTerm = tmpChildParticle.getTerm();
          if (tmpChildTerm.isElementDecl()) {
            final XSElementDecl tmpChildElement = tmpChildTerm.asElementDecl();
            if (tmpChildElement.getType().isDerivedFrom(baseParameterType)) {
              // build parameter
              final ParameterType tmpParameter = new ParameterType();
              tmpParameter.setNamespace(tmpChildElement.getTargetNamespace());
              tmpParameter.setName(tmpChildElement.getName());
              tmpParameter.setDocumentation(getDocumentation(tmpChildElement));
              if (tmpChildParticle.getMinOccurs() == 0) {
                tmpParameter.setOptional(true);
              }
              tmpParameterTypes.add(tmpParameter);
            }
          }
        }
      }
    }

    return tmpParameterTypes;
  }

  private String getDocumentation(final XSElementDecl anElement) {
    final NormalizedString tmpDocumentation = new NormalizedString();
    if (anElement.getAnnotation() != null && anElement.getAnnotation().getAnnotation() != null) {
      final Element tmpAnnotation = (Element) anElement.getAnnotation().getAnnotation();
      final NodeList tmpDocumentationNodes = tmpAnnotation.getElementsByTagName("xs:documentation");
      for (int i = 0; i < tmpDocumentationNodes.getLength(); i++) {
        final Node tmpDocumentationNode = tmpDocumentationNodes.item(i);
        if (tmpDocumentationNode != null) {
          final String tmpText = tmpDocumentationNode.getTextContent();
          if (tmpText != null && !"".equals(tmpText.trim())) {
            if (tmpDocumentation.length() != 0) {
              tmpDocumentation.append("\n");
            }
            tmpDocumentation.append(tmpText);
          }
        }
      }
    }
    return tmpDocumentation.toString();
  }

  /**
   * @return the schemaLocations
   */
  public Map<String, XMLSchema> getSchemaLocations() {
    return schemaLocations;
  }
}
