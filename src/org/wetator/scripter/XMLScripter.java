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


package org.wetator.scripter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.exception.WetatorException;
import org.wetator.scripter.xml.ModelBuilder;
import org.wetator.scripter.xml.SchemaFinder;
import org.wetator.scripter.xml.XMLSchema;
import org.wetator.scripter.xml.model.CommandType;
import org.wetator.scripter.xml.model.ParameterType;

/**
 * Scripter for XML files using the new test XSDs.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class XMLScripter implements IScripter {

  private static final String WET_FILE_EXTENSION = ".wet";
  private static final String XML_FILE_EXTENSION = ".xml";

  /**
   * The base schema file for the test case model.
   */
  public static final String BASE_SCHEMA = "http://www.wetator.org/xsd/test-case";
  private static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.0.0");

  private static final String E_TEST_CASE = "test-case";
  private static final String E_COMMAND = "command";
  private static final String E_COMMENT = "comment";
  private static final String A_VERSION = "version";
  private static final String A_DISABLED = "disabled";

  private static final Pattern VERSION_PATTERN = Pattern.compile(".*" + A_VERSION + "\\s*=\\s*[\"'](.*)[\"'].*");

  /**
   * The schema for the default command set.
   */
  public static final XMLSchema DEFAULT_COMMAND_SET_SCHEMA = new XMLSchema("d",
      "http://www.wetator.org/xsd/default-command-set", "default-command-set-1.0.0.xsd");

  private Map<String, XMLSchema> schemas;
  private ModelBuilder model;
  private List<Command> commands;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#isSupported(java.io.File)
   */
  @Override
  public boolean isSupported(final File aFile) {
    // first check the file extension
    final String tmpFileName = aFile.getName().toLowerCase();
    if (!tmpFileName.endsWith(WET_FILE_EXTENSION) && !tmpFileName.endsWith(XML_FILE_EXTENSION)) {
      return false;
    }

    // now check the content
    try {
      return isSupported(new FileReader(aFile));
    } catch (final IOException e) {
      throw new WetatorException("Could not read file '" + aFile.getAbsolutePath() + "'.", e);
    }
  }

  /**
   * This method is used by the WTE.
   * 
   * @param aContent the content to check
   * @return true if this scripter is able to handle this content otherwise false
   */
  public boolean isSupported(final String aContent) {
    // now check the content
    try {
      return isSupported(new StringReader(aContent));
    } catch (final IOException e) {
      throw new WetatorException("Could not read content.", e);
    }
  }

  private boolean isSupported(final Reader aContent) throws IOException {
    // now check root element, schema and version
    final BufferedReader tmpReader = new BufferedReader(aContent);
    try {
      String tmpLine;
      boolean tmpTestCase = false;
      boolean tmpBaseSchema = false;
      String tmpVersion = null;
      while ((tmpLine = tmpReader.readLine()) != null) {
        if (tmpLine.contains("<" + E_TEST_CASE)) {
          tmpTestCase = true;
        }
        if (tmpLine.contains("<" + E_COMMAND) || tmpLine.contains("<" + E_COMMENT)) {
          break;
        }
        if (tmpTestCase && tmpLine.contains(BASE_SCHEMA)) {
          tmpBaseSchema = true;
        }
        if (tmpTestCase && tmpLine.contains(A_VERSION)) {
          final Matcher tmpMatcher = VERSION_PATTERN.matcher(tmpLine);
          if (tmpMatcher.matches()) {
            tmpVersion = tmpMatcher.group(1);
          }
        }
        if (tmpBaseSchema && tmpVersion != null && SUPPORTED_VERSIONS.contains(tmpVersion)) {
          return true;
        }
      }
    } finally {
      try {
        tmpReader.close();
      } catch (final IOException e) {
        // bad luck
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#script(java.io.File)
   */
  @Override
  public void script(final File aFile) throws WetatorException {
    try {
      Reader tmpReader = new FileReader(aFile);
      List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
      try {
        tmpSchemas = new SchemaFinder(tmpReader).getSchemas();
      } finally {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // ignore;
        }
      }

      addDefaultSchemas(tmpSchemas);

      buildSchemaMap(tmpSchemas);
      model = new ModelBuilder(tmpSchemas, aFile.getParentFile());

      tmpReader = new FileReader(aFile);
      try {
        commands = parseScript(tmpReader);
      } finally {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // ignore;
        }
      }
    } catch (final Exception e) {
      throw new WetatorException("Could not read file '" + aFile.getAbsolutePath() + "'.", e);
    }
  }

  /**
   * Scripts the given content by reading all commands.<br/>
   * This method is used by the WTE.
   * 
   * @param aContent the content
   * @param aDirectory the directory to search for schema files; may be null
   * @throws WetatorException in case of error
   */
  public void script(final String aContent, final File aDirectory) throws WetatorException {
    try {
      Reader tmpReader = new StringReader(aContent);
      List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
      try {
        tmpSchemas = new SchemaFinder(tmpReader).getSchemas();
      } finally {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // ignore;
        }
      }

      addDefaultSchemas(tmpSchemas);

      buildSchemaMap(tmpSchemas);
      model = new ModelBuilder(tmpSchemas, aDirectory);

      tmpReader = new StringReader(aContent);
      try {
        commands = parseScript(tmpReader);
      } finally {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // ignore;
        }
      }
    } catch (final Exception e) {
      throw new WetatorException("Could not read content.", e);
    }
  }

  private void addDefaultSchemas(final List<XMLSchema> aSchemaList) {
    // add schema for default command set since it is always loaded
    aSchemaList.add(DEFAULT_COMMAND_SET_SCHEMA);
  }

  private void buildSchemaMap(final List<XMLSchema> aSchemaList) {
    final Map<String, XMLSchema> tmpSchemaMap = new LinkedHashMap<String, XMLSchema>();
    for (XMLSchema tmpXMLSchema : aSchemaList) {
      tmpSchemaMap.put(tmpXMLSchema.getNamespace(), tmpXMLSchema);
    }

    schemas = tmpSchemaMap;

    aSchemaList.clear();
    for (Entry<String, XMLSchema> tmpXMLSchema : schemas.entrySet()) {
      aSchemaList.add(tmpXMLSchema.getValue());
    }
  }

  private List<Command> parseScript(final Reader aContent) throws XMLStreamException, IOException {
    final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    final XMLStreamReader tmpReader = tmpFactory.createXMLStreamReader(aContent);

    final List<Command> tmpResult = new ArrayList<Command>();
    try {
      // move reader position to test-case...
      while (tmpReader.hasNext()) {
        if (tmpReader.next() == XMLStreamConstants.START_ELEMENT && BASE_SCHEMA.equals(tmpReader.getNamespaceURI())
            && E_TEST_CASE.equals(tmpReader.getLocalName())) {
          break;
        }
      }

      // ...and now parse the rest of the file
      boolean tmpInComment = false;
      boolean tmpInCommand = false;
      boolean tmpInCommandType = false;
      boolean tmpInParameter = false;
      CommandType tmpCurrentCommandType = null;
      int tmpCurrentParameter = -1;
      StringBuilder tmpCurrentText = new StringBuilder();
      boolean tmpDisabled = false;
      String[] tmpParameters = new String[0];

      Command tmpCommand = null;
      while (tmpReader.hasNext()) {
        final int tmpEvent = tmpReader.next();
        if (tmpEvent == XMLStreamConstants.START_ELEMENT) {
          if (E_COMMENT.equals(tmpReader.getLocalName())) {
            // comment found
            tmpInComment = true;
            // build command
            tmpCommand = new Command("", true);
            tmpCommand.setLineNo(tmpResult.size() + 1);

            tmpCurrentText = new StringBuilder();
          } else if (E_COMMAND.equals(tmpReader.getLocalName())) {
            // command
            tmpInCommand = true;
            // detect disabled
            final String tmpIsCommentAsString = tmpReader.getAttributeValue(null, A_DISABLED);
            if (StringUtils.isNotEmpty(tmpIsCommentAsString)) {
              tmpDisabled = Boolean.valueOf(tmpIsCommentAsString).booleanValue();
            }
          } else {
            CommandType tmpCommandType = null;
            if (!tmpInCommandType) {
              tmpCommandType = model.getCommandType(tmpReader.getLocalName());
            }
            if (tmpCommandType != null) {
              // known commandType found
              tmpInCommandType = true;
              tmpCurrentCommandType = tmpCommandType;
              // build command
              tmpCommand = new Command(tmpCurrentCommandType.getName(), tmpDisabled);
              tmpCommand.setLineNo(tmpResult.size() + 1);

              tmpParameters = new String[tmpCurrentCommandType.getParameterTypes().size()];
            } else if (tmpInCommandType) {
              // no commandType found -> check for parameter of current commandType
              int i = 0;
              for (ParameterType tmpParameterType : tmpCurrentCommandType.getParameterTypes()) {
                if (tmpParameterType.getName().equals(tmpReader.getLocalName())) {
                  // new parameter of current commandType found
                  tmpInParameter = true;
                  tmpCurrentParameter = i;
                  tmpCurrentText = new StringBuilder();
                  break;
                }
                i++;
              }
            }
          }
        } else if (tmpEvent == XMLStreamConstants.CHARACTERS) {
          if (tmpInComment || tmpInParameter) {
            // only comments and parameters contain text
            tmpCurrentText.append(tmpReader.getText());
          }
        } else if (tmpEvent == XMLStreamConstants.END_ELEMENT) {
          if (tmpInParameter) {
            tmpInParameter = false;

            tmpParameters[tmpCurrentParameter] = tmpCurrentText.toString();

            tmpCurrentParameter = -1;
            tmpCurrentText = null;
          } else if (tmpInCommandType) {
            tmpInCommandType = false;

            if (tmpParameters.length >= 1 && StringUtils.isNotEmpty(tmpParameters[0])) {
              tmpCommand.setFirstParameter(new Parameter(tmpParameters[0]));
            }
            if (tmpParameters.length >= 2 && StringUtils.isNotEmpty(tmpParameters[1])) {
              tmpCommand.setSecondParameter(new Parameter(tmpParameters[1]));
            }
            if (tmpParameters.length >= 3 && StringUtils.isNotEmpty(tmpParameters[2])) {
              tmpCommand.setThirdParameter(new Parameter(tmpParameters[2]));
            }
            tmpResult.add(tmpCommand);

            tmpCurrentCommandType = null;
          } else if (tmpInCommand) {
            tmpInCommand = false;
            tmpDisabled = false;
          } else if (tmpInComment) {
            tmpInComment = false;

            tmpCommand.setFirstParameter(new Parameter(tmpCurrentText.toString()));
            tmpResult.add(tmpCommand);

            tmpCurrentText = null;
          }
        }
      }
    } finally {
      tmpReader.close();
      aContent.close();
    }
    return tmpResult;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#getCommands()
   */
  @Override
  public List<Command> getCommands() {
    return commands;
  }

  /**
   * @return the model
   */
  public ModelBuilder getModel() {
    return model;
  }

  /**
   * @return the schemas
   */
  public Map<String, XMLSchema> getSchemas() {
    return schemas;
  }
}