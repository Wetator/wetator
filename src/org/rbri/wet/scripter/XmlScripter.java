/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.scripter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;

/**
 * Scripter for XML files
 * 
 * @author tobwoerk
 */
public final class XmlScripter implements WetScripter {

  private static final String XML_FILE_EXTENSION = ".xml";

  /**
   * The element name for test step
   */
  public static final String E_STEP = "step";
  /**
   * The element name for optional parameter
   */
  public static final String E_OPTIONAL_PARAMETER = "optionalParameter";
  /**
   * The attribute name for command
   */
  public static final String A_COMMAND = "command";
  /**
   * The attribute name for comment
   */
  public static final String A_COMMENT = "comment";

  private File file;
  private InputStream inputStream;
  private XMLStreamReader reader;

  private List<WetCommand> commands;

  /**
   * Standard constructor.
   */
  public XmlScripter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#setFile(java.io.File)
   */
  public void setFile(File aFile) throws WetException {
    file = aFile;

    commands = readCommands();
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#isSupported(java.io.File)
   */
  public boolean isSupported(File aFile) {
    String tmpFileName;
    boolean tmpResult;

    tmpFileName = aFile.getName().toLowerCase();
    tmpResult = tmpFileName.endsWith(XML_FILE_EXTENSION);

    return tmpResult;
  }

  private List<WetCommand> readCommands() throws WetException {
    List<WetCommand> tmpResult = new LinkedList<WetCommand>();

    try {
      inputStream = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new WetException("File '" + getFile().getAbsolutePath() + "' not available.", e);
    }
    XMLInputFactory tmpFactory = XMLInputFactory.newInstance();
    try {
      reader = tmpFactory.createXMLStreamReader(inputStream);
    } catch (XMLStreamException e) {
      throw new WetException("Error creating reader for file '" + getFile().getAbsolutePath() + "'.", e);
    }

    try {
      WetCommand tmpWetCommand = null;
      while (reader.hasNext()) {
        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
          if (E_STEP.equals(reader.getLocalName())) {
            String tmpCommandName = reader.getAttributeValue(null, A_COMMAND).replace('_', ' ');

            // comment handling
            boolean tmpIsComment = A_COMMENT.equals(tmpCommandName.toLowerCase());
            if (!tmpIsComment) {
              String tmpIsCommentAsString = reader.getAttributeValue(null, A_COMMENT);
              if (StringUtils.isNotEmpty(tmpIsCommentAsString)) {
                tmpIsComment = Boolean.valueOf(tmpIsCommentAsString).booleanValue();
              }
            }

            // build WetCommand
            tmpWetCommand = new WetCommand(tmpCommandName, tmpIsComment);
            tmpWetCommand.setLineNo(tmpResult.size() + 1);

            // go to CHARACTER event (parameter) if there
            if (reader.next() == XMLStreamConstants.CHARACTERS) {
              String tmpParameters = reader.getText();
              tmpWetCommand.setFirstParameter(new Parameter(tmpParameters));
            }
          } else if (E_OPTIONAL_PARAMETER.equals(reader.getLocalName())) {
            String tmpOptionalParameter = reader.getElementText();
            if (null == tmpWetCommand) {
              throw new WetException("Error parsing file '" + getFile().getAbsolutePath()
                  + "'. Unexpected optional parameter '" + tmpOptionalParameter + "'.");
            }

            tmpWetCommand.setSecondParameter(new Parameter(tmpOptionalParameter));
          }
        }
        if (reader.getEventType() == XMLStreamConstants.END_ELEMENT && E_STEP.equals(reader.getLocalName())) {
          tmpResult.add(tmpWetCommand);
        }
      }

      return tmpResult;
    } catch (XMLStreamException e) {
      throw new WetException("Error parsing file '" + getFile().getAbsolutePath() + "' (" + e.getMessage() + ").", e);
    } finally {
      try {
        reader.close();
        inputStream.close();
      } catch (Exception e) {
        throw new WetException("Problem closing resources for file '" + getFile().getAbsolutePath() + "'.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#getCommands()
   */
  public List<WetCommand> getCommands() {
    return commands;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#initialize(java.util.Properties)
   */
  public void initialize(Properties aConfiguration) {
    // nothing to do
  }
}
