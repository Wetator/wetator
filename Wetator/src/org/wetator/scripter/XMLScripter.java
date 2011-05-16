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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private File file;
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

    // now check root element, schema and version
    BufferedReader tmpReader = null;
    try {
      tmpReader = new BufferedReader(new FileReader(aFile));
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
    } catch (final IOException e) {
      throw new WetatorException("Could not read file '" + aFile.getAbsolutePath() + "'.", e);
    } finally {
      if (tmpReader != null) {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // bad luck
        }
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#setFile(java.io.File)
   */
  @Override
  public void setFile(final File aFile) throws WetatorException {
    file = aFile;

    try {
      model = new ModelBuilder(file);

      commands = parseScript(file);
    } catch (final Exception e) {
      throw new WetatorException("Could not read file '" + aFile.getAbsolutePath() + "'.", e);
    }
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

  private List<Command> parseScript(final File aFile) throws IOException, XMLStreamException {
    final InputStream tmpInputStream = new FileInputStream(aFile);
    final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    final XMLStreamReader tmpReader = tmpFactory.createXMLStreamReader(tmpInputStream);

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
      tmpInputStream.close();
    }
    return tmpResult;
  }
}