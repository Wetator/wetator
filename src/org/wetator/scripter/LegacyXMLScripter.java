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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.exception.ResourceException;
import org.wetator.exception.WetatorException;

/**
 * Scripter for XML files.
 * 
 * @author tobwoerk
 */
public final class LegacyXMLScripter implements IScripter {

  private static final String WET_FILE_EXTENSION = ".wet";
  private static final String XML_FILE_EXTENSION = ".xml";

  private static final String BASE_SCHEMA = "http://www.wetator.org/xsd/defaultCommandSet";

  private static final String E_TESTCASE = "testcase";
  /**
   * The element name for test step.
   */
  public static final String E_STEP = "step";
  /**
   * The element name for optional parameter.
   */
  public static final String E_OPTIONAL_PARAMETER = "optionalParameter";
  /**
   * The element name for second optional parameter.
   */
  public static final String E_OPTIONAL_PARAMETER2 = "optionalParameter2";
  /**
   * The attribute name for command.
   */
  public static final String A_COMMAND = "command";
  /**
   * The attribute name for comment.
   */
  public static final String A_COMMENT = "comment";

  private File file;

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
  public IScripter.IsSupportedResult isSupported(final File aFile) {
    // first check the file extension
    final String tmpFileName = aFile.getName().toLowerCase();
    final boolean tmpResult = tmpFileName.endsWith(WET_FILE_EXTENSION) || tmpFileName.endsWith(XML_FILE_EXTENSION);
    if (!tmpResult) {
      return new IScripter.IsSupportedResult("File '" + aFile.getName()
          + "' not supported by LegacyXMLScripter. Extension is not '" + WET_FILE_EXTENSION + "' or '"
          + XML_FILE_EXTENSION + "'.");
    }

    // now check root element and schema
    BufferedReader tmpReader = null;
    try {
      tmpReader = new BufferedReader(new FileReader(aFile));
      String tmpLine;
      boolean tmpTestCase = false;
      while ((tmpLine = tmpReader.readLine()) != null) {
        if (tmpLine.contains("<" + E_TESTCASE)) {
          tmpTestCase = true;
        }
        if (tmpLine.contains(E_STEP + " ")) {
          break;
        }
        if (tmpTestCase && tmpLine.contains(BASE_SCHEMA)) {
          return IScripter.IS_SUPPORTED;
        }
      }
    } catch (final IOException e) {
      throw new ResourceException("Could not read file '" + aFile.getAbsolutePath() + "'.", e);
    } finally {
      if (tmpReader != null) {
        try {
          tmpReader.close();
        } catch (final IOException e) {
          // bad luck
        }
      }
    }

    return new IScripter.IsSupportedResult("File '" + aFile.getName()
        + "' not supported by LegacyXMLScripter. Parsing the file failed.");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#script(java.io.File)
   */
  @Override
  public void script(final File aFile) {
    file = aFile;

    commands = readCommands();
  }

  private List<Command> readCommands() {
    final List<Command> tmpResult = new ArrayList<Command>();

    InputStream tmpInputStream = null;
    try {
      tmpInputStream = new FileInputStream(file);
    } catch (final FileNotFoundException e) {
      throw new ResourceException("Could not read file '" + file.getAbsolutePath() + "'.", e);
    }

    XMLStreamReader tmpReader = null;
    try {
      final XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
      try {
        tmpReader = tmpFactory.createXMLStreamReader(tmpInputStream);
      } catch (final XMLStreamException e) {
        // TODO which exception? mostly this is an invalid input! -> checked?
        throw new WetatorException("Error creating reader for file '" + file.getAbsolutePath() + "'.", e);
      }

      try {
        Command tmpCommand = null;
        while (tmpReader.hasNext()) {
          if (tmpReader.next() == XMLStreamConstants.START_ELEMENT) {
            if (E_STEP.equals(tmpReader.getLocalName())) {
              String tmpCommandName = tmpReader.getAttributeValue(null, A_COMMAND);
              // normalize command name
              tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase();

              // comment handling
              boolean tmpIsComment = A_COMMENT.equals(tmpCommandName.toLowerCase());
              if (!tmpIsComment) {
                final String tmpIsCommentAsString = tmpReader.getAttributeValue(null, A_COMMENT);
                if (StringUtils.isNotEmpty(tmpIsCommentAsString)) {
                  tmpIsComment = Boolean.valueOf(tmpIsCommentAsString).booleanValue();
                }
              }

              // build command
              tmpCommand = new Command(tmpCommandName, tmpIsComment);
              tmpCommand.setLineNo(tmpResult.size() + 1);

              // go to CHARACTER event (parameter) if there
              final StringBuilder tmpParameters = new StringBuilder("");
              while (tmpReader.next() == XMLStreamConstants.CHARACTERS) {
                tmpParameters.append(tmpReader.getText());
              }

              final String tmpParameterValue = tmpParameters.toString();
              if (StringUtils.isNotEmpty(tmpParameterValue)) {
                tmpCommand.setFirstParameter(new Parameter(tmpParameterValue));
              }
            }
            if (E_OPTIONAL_PARAMETER.equals(tmpReader.getLocalName())) {
              final String tmpOptionalParameter = tmpReader.getElementText();
              if (null == tmpCommand) {
                // TODO which exception? this is an invalid input! -> checked
                throw new WetatorException("Error parsing file '" + file.getAbsolutePath()
                    + "'. Unexpected optional parameter '" + tmpOptionalParameter + "'.");
              }

              if (StringUtils.isNotEmpty(tmpOptionalParameter)) {
                tmpCommand.setSecondParameter(new Parameter(tmpOptionalParameter));
              }
              tmpReader.next();
            }
            if (E_OPTIONAL_PARAMETER2.equals(tmpReader.getLocalName())) {
              final String tmpOptionalParameter = tmpReader.getElementText();
              if (null == tmpCommand) {
                // TODO which exception? this is an invalid input! -> checked
                throw new WetatorException("Error parsing file '" + file.getAbsolutePath()
                    + "'. Unexpected optional parameter 2 '" + tmpOptionalParameter + "'.");
              }

              if (StringUtils.isNotEmpty(tmpOptionalParameter)) {
                tmpCommand.setThirdParameter(new Parameter(tmpOptionalParameter));
              }
              tmpReader.next();
            }
          }
          if (tmpReader.getEventType() == XMLStreamConstants.END_ELEMENT && E_STEP.equals(tmpReader.getLocalName())) {
            tmpResult.add(tmpCommand);
          }
        }

        return tmpResult;
      } catch (final XMLStreamException e) {
        // TODO which exception? mostly this is an invalid input! -> checked?
        throw new WetatorException("Error parsing file '" + file.getAbsolutePath() + "' (" + e.getMessage() + ").", e);
      }
    } finally {
      if (tmpReader != null) {
        try {
          tmpReader.close();
        } catch (final Exception e) {
          // bad luck
        }
      }
      if (tmpInputStream != null) {
        try {
          tmpInputStream.close();
        } catch (final Exception e) {
          // bad luck
        }
      }
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
}
