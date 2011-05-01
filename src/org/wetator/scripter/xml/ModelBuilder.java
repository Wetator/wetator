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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
 */
public class ModelBuilder {

  /**
   * The base schema file for this model.
   */
  public static final String BASE_SCHEMA = "http://www.wetator.org/xsd/test-case";
  private static final String BASE_COMMAND_TYPE = "commandType";
  private static final String BASE_PARAMETER_TYPE = "parameterType";

  private Map<String, String> schemaLocations = new HashMap<String, String>();

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

    parseSchemas(aFile.getParentFile());
  }

  /**
   * @param aSchemaDirectory the directory to search for schemas with relative paths
   * @param aSchemaLocationMap the map containing the schemas to use (key = namespace URI, value = schema location)
   * @throws SAXException in case of problems reading the file
   * @throws IOException in case of problems reading the file
   */
  public ModelBuilder(final File aSchemaDirectory, final Map<String, String> aSchemaLocationMap) throws SAXException,
      IOException {
    schemaLocations = aSchemaLocationMap;

    parseSchemas(aSchemaDirectory);
  }

  /**
   * @param aNamespace the namespace URI of the command type
   * @param aName the local name of the command type
   * @return the command type for the given parameters or null if none found
   */
  public CommandType getCommandType(final String aNamespace, final String aName) {
    return commandTypes.get(aNamespace + ":" + aName);
  }

  /**
   * @return a list containing all known command types
   */
  public List<CommandType> getCommandTypes() {
    return new ArrayList<CommandType>(commandTypes.values());
  }

  /**
   * @param aNamespace the namespace URI of the command and parameter type
   * @param aCommandName the local name of the command type
   * @param aName the local name of the parameter type
   * @return the parameter type for the given parameters or null if none found
   */
  public ParameterType getParameterType(final String aNamespace, final String aCommandName, final String aName) {
    final CommandType tmpCommandType = commandTypes.get(aNamespace + ":" + aCommandName);
    if (tmpCommandType != null) {
      return tmpCommandType.getParameterTypes().get(aNamespace + ":" + aName);
    }
    return null;
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
          tmpSchemaLocation = tmpSchemaLocation.replace("  ", " ");
          final String[] tmpSchemaLocations = tmpSchemaLocation.split(" ");
          for (int i = 0; i < tmpSchemaLocations.length; i += 2) {
            if (!"".equals(tmpSchemaLocations[i].trim())) {
              schemaLocations.put(tmpSchemaLocations[i], tmpSchemaLocations[i + 1]);
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
    final XSOMParser tmpParser = new XSOMParser();
    tmpParser.setAnnotationParser(new DomAnnotationParserFactory());
    tmpParser.setEntityResolver(new LocalEntityResolver());

    // parse all schemas
    for (Entry<String, String> tmpSchemaLocation : schemaLocations.entrySet()) {
      // first try the local xsd resolver
      final InputSource tmpSource = new LocalEntityResolver().resolveEntity(tmpSchemaLocation.getKey(),
          tmpSchemaLocation.getValue());
      if (tmpSource != null) {
        tmpParser.parse(tmpSource);
      } else {
        // nothing found locally -> try external file
        File tmpSchemaFile = new File(tmpSchemaLocation.getValue());
        if (!tmpSchemaFile.isAbsolute() && aSchemaDirectory != null) {
          tmpSchemaFile = new File(aSchemaDirectory, tmpSchemaFile.getName());
        }
        if (tmpSchemaFile.exists()) {
          tmpParser.parse(tmpSchemaFile);
        } else {
          // nothing found locally or as external file -> try URL
          try {
            final URL tmpURL = new URL(tmpSchemaLocation.getValue());
            tmpParser.parse(tmpURL);
          } catch (final MalformedURLException e) {
            throw new WetException("Could not resolve schema file '" + tmpSchemaLocation.getValue() + "'.", e);
          } catch (final SAXException e) {
            throw new WetException("Could not resolve schema file '" + tmpSchemaLocation.getValue() + "'.",
                e.getException());
          }
        }
      }
    }

    final XSSchemaSet tmpSchemaSet = tmpParser.getResult();
    final XSSchema tmpBaseSchema = tmpSchemaSet.getSchema(BASE_SCHEMA);
    baseCommandType = tmpBaseSchema.getComplexType(BASE_COMMAND_TYPE);
    baseParameterType = tmpBaseSchema.getSimpleType(BASE_PARAMETER_TYPE);

    // find all command types and their parameter types
    for (final Iterator<XSElementDecl> tmpIterator = tmpSchemaSet.iterateElementDecls(); tmpIterator.hasNext();) {
      final XSElementDecl tmpElement = tmpIterator.next();
      if (tmpElement.getType().isDerivedFrom(baseCommandType) && !((XSComplexType) tmpElement.getType()).isAbstract()) {
        final XSComplexType tmpType = (XSComplexType) tmpElement.getType();

        // build command
        final CommandType tmpCommandType = new CommandType();
        tmpCommandType.setNamespace(tmpElement.getTargetNamespace());
        tmpCommandType.setName(tmpElement.getName());
        tmpCommandType.setDocumentation(getDocumentation(tmpElement));
        commandTypes.put(tmpElement.getTargetNamespace() + ":" + tmpElement.getName(), tmpCommandType);

        for (ParameterType tmpParameterType : getParameterTypes(tmpType)) {
          tmpCommandType.getParameterTypes().put(tmpParameterType.getNamespace() + ":" + tmpParameterType.getName(),
              tmpParameterType);
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
}
