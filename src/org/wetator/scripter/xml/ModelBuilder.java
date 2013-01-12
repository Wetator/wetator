/*
 * Copyright (c) 2008-2013 wetator.org
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wetator.exception.ImplementationException;
import org.wetator.scripter.ParseException;
import org.wetator.scripter.XMLScripter;
import org.wetator.scripter.xml.model.CommandType;
import org.wetator.scripter.xml.model.ParameterType;
import org.wetator.util.NormalizedString;
import org.xml.sax.EntityResolver;
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

  private XSComplexType baseCommandType;
  private XSSimpleType baseParameterType;

  private Map<String, CommandType> commandTypes = new LinkedHashMap<String, CommandType>();

  /**
   * @param aSchemas the list containing the schemas to use
   * @param aSchemaDirectory the directory to search for schema files; may be null
   * @throws IOException in case of problems reading the file
   * @throws SAXException in case of problems reading the file
   * @throws ParseException in case of problems parsing the file
   */
  public ModelBuilder(final List<XMLSchema> aSchemas, final File aSchemaDirectory) throws IOException, SAXException,
      ParseException {
    final XSSchemaSet tmpSchemaSet = parseSchemas(aSchemas, aSchemaDirectory);
    buildModel(tmpSchemaSet);
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

  private XSSchemaSet parseSchemas(final List<XMLSchema> aSchemaList, final File aSchemaDirectory) throws IOException,
      SAXException, ParseException {
    if (aSchemaList == null || aSchemaList.isEmpty()) {
      throw new ImplementationException("No schema to parse.");
    }

    final EntityResolver tmpEntityResolver = new LocalEntityResolver(aSchemaDirectory);
    final XSOMParser tmpParser = new XSOMParser();
    tmpParser.setAnnotationParser(new DomAnnotationParserFactory());
    tmpParser.setEntityResolver(tmpEntityResolver);

    // parse all schemas
    for (XMLSchema tmpSchema : aSchemaList) {
      final InputSource tmpSource = tmpEntityResolver.resolveEntity(tmpSchema.getNamespace(), tmpSchema.getLocation());
      if (tmpSource != null) {
        tmpParser.parse(tmpSource);
      } else {
        throw new ParseException("Could not resolve schema file '" + tmpSchema.getNamespace() + "'.");
      }
    }

    return tmpParser.getResult();
  }

  private void buildModel(final XSSchemaSet aSchemaSet) throws ParseException {
    final XSSchema tmpBaseSchema = aSchemaSet.getSchema(XMLScripter.BASE_SCHEMA.getNamespace());
    if (tmpBaseSchema == null) {
      throw new ParseException("No base schema '" + XMLScripter.BASE_SCHEMA.getNamespace() + "' found.");
    }
    baseCommandType = tmpBaseSchema.getComplexType(BASE_COMMAND_TYPE);
    baseParameterType = tmpBaseSchema.getSimpleType(BASE_PARAMETER_TYPE);

    // find all command types and their parameter types
    for (final Iterator<XSElementDecl> tmpIterator = aSchemaSet.iterateElementDecls(); tmpIterator.hasNext();) {
      final XSElementDecl tmpElement = tmpIterator.next();
      if (tmpElement.getType().isDerivedFrom(baseCommandType) && !((XSComplexType) tmpElement.getType()).isAbstract()) {
        final XSComplexType tmpType = (XSComplexType) tmpElement.getType();

        final String tmpElementName = tmpElement.getName();
        final CommandType tmpExistingCommandType = commandTypes.get(tmpElementName);
        if (tmpExistingCommandType != null) {
          throw new ParseException("Duplicate command '" + tmpElementName + "' found ('"
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
              throw new ParseException("Duplicate parameter '" + tmpElementName + "' found for command '"
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
}