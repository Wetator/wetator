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


package org.wetator.scriptcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;
import org.wetator.scripter.xml.ModelBuilder;
import org.wetator.scripter.xml.model.CommandType;
import org.wetator.scripter.xml.model.ParameterType;

/**
 * Creates a Wetator test script in XML format from the given commands with the given file name and XSD in the given
 * output directory.
 * 
 * @author frank.danek
 */
public class XmlScriptCreator implements IScriptCreator {

  private List<WetCommand> commands;
  private String fileName;
  private File outputDir;

  private static final String XML_ENCODING = "UTF-8";
  private static final String XML_VERSION = "1.0";

  private static final String E_TEST_CASE = "test-case";
  private static final String E_COMMAND = "command";
  private static final String E_COMMENT = "comment";
  private static final String A_VERSION = "version";
  private static final String A_DISABLED = "disabled";

  private static final String XSD_VERSION = "1.0.0";

  private static final Pattern CHARACTER_DATA_PATTERN = Pattern.compile(".*[<>&]");

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scriptcreator.IScriptCreator#createScript()
   */
  @Override
  public void createScript() throws WetException {
    final XMLOutputFactory tmpFactory = XMLOutputFactory.newInstance();
    try {
      final Map<String, String> tmpKnownSchemas = new HashMap<String, String>();
      tmpKnownSchemas.put("http://www.wetator.org/xsd/test-case", "test-case-1.0.0.xsd");
      tmpKnownSchemas.put("http://www.wetator.org/xsd/default-command-set", "default-command-set-1.0.0.xsd");
      tmpKnownSchemas.put("http://www.wetator.org/xsd/sql-command-set", "sql-command-set-1.0.0.xsd");
      tmpKnownSchemas.put("http://www.wetator.org/xsd/test-command-set", "test-command-set-1.0.0.xsd");
      tmpKnownSchemas.put("http://www.wetator.org/xsd/incubator-command-set", "incubator-command-set-1.0.0.xsd");
      final ModelBuilder tmpModel = new ModelBuilder(tmpKnownSchemas);

      final File tmpFile = new File(outputDir, fileName + ".wet");
      final XMLStreamWriter tmpWriter = tmpFactory.createXMLStreamWriter(new FileOutputStream(tmpFile), XML_ENCODING);

      tmpWriter.writeStartDocument(XML_ENCODING, XML_VERSION);
      tmpWriter.writeCharacters("\n");

      tmpWriter.writeStartElement(E_TEST_CASE);
      tmpWriter.writeDefaultNamespace("http://www.wetator.org/xsd/test-case");
      tmpWriter.writeNamespace("d", "http://www.wetator.org/xsd/default-command-set");
      tmpWriter.writeNamespace("s", "http://www.wetator.org/xsd/sql-command-set");
      tmpWriter.writeNamespace("i", "http://www.wetator.org/xsd/incubator-command-set");
      tmpWriter.writeNamespace("t", "http://www.wetator.org/xsd/test-command-set");
      tmpWriter.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      tmpWriter.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation",
          "http://www.wetator.org/xsd/test-case test-case-" + XSD_VERSION + ".xsd\n"
              + "http://www.wetator.org/xsd/default-command-set default-command-set-" + XSD_VERSION + ".xsd\n"
              + "http://www.wetator.org/xsd/sql-command-set sql-command-set-" + XSD_VERSION + ".xsd\n"
              + "http://www.wetator.org/xsd/incubator-command-set incubator-command-set-" + XSD_VERSION + ".xsd\n"
              + "http://www.wetator.org/xsd/test-command-set test-command-set-" + XSD_VERSION + ".xsd");
      tmpWriter.writeAttribute(A_VERSION, XSD_VERSION);
      tmpWriter.writeCharacters("\n");
      for (WetCommand tmpCommand : commands) {
        tmpWriter.writeCharacters("    ");
        if (tmpCommand.isComment() && StringUtils.isEmpty(tmpCommand.getName())) {
          tmpWriter.writeStartElement(E_COMMENT);
          writeContent(tmpWriter, tmpCommand.getFirstParameter().getValue());
          tmpWriter.writeEndElement();
          tmpWriter.writeCharacters("\n");
        } else {
          tmpWriter.writeStartElement(E_COMMAND);
          if (tmpCommand.isComment()) {
            tmpWriter.writeAttribute(A_DISABLED, "true");
          }

          final CommandType tmpCommandType = tmpModel.getCommandType(tmpCommand.getName());
          if (tmpCommandType == null) {
            throw new RuntimeException("Unknown command '" + tmpCommand.getName() + "'.");
          }
          tmpWriter.writeStartElement(tmpCommandType.getNamespace(), tmpCommandType.getName());

          final Collection<ParameterType> tmpParameterTypes = tmpCommandType.getParameterTypes();
          final String[] tmpParameterValues = new String[tmpParameterTypes.size()];
          if (tmpParameterValues.length >= 1 && tmpCommand.getFirstParameter() != null) {
            tmpParameterValues[0] = tmpCommand.getFirstParameter().getValue();
          }
          if (tmpParameterValues.length >= 2 && tmpCommand.getSecondParameter() != null) {
            tmpParameterValues[1] = tmpCommand.getSecondParameter().getValue();
          }
          if (tmpParameterValues.length >= 3 && tmpCommand.getThirdParameter() != null) {
            tmpParameterValues[2] = tmpCommand.getThirdParameter().getValue();
          }
          int i = 0;
          for (ParameterType tmpParameterType : tmpParameterTypes) {
            if (StringUtils.isNotEmpty(tmpParameterValues[i])) {
              tmpWriter.writeStartElement(tmpParameterType.getNamespace(), tmpParameterType.getName());
              writeContent(tmpWriter, tmpParameterValues[i]);
              tmpWriter.writeEndElement();
            }
            i++;
          }

          tmpWriter.writeEndElement();
          tmpWriter.writeEndElement();
          tmpWriter.writeCharacters("\n");
        }
      }
      tmpWriter.writeEndElement();
      tmpWriter.writeEndDocument();
      tmpWriter.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private void writeContent(final XMLStreamWriter aWriter, final String aContent) throws XMLStreamException {
    if (CHARACTER_DATA_PATTERN.matcher(aContent).matches()) {
      aWriter.writeCData(aContent);
    } else {
      aWriter.writeCharacters(aContent);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scriptcreator.IScriptCreator#setCommands(java.util.List)
   */
  @Override
  public void setCommands(final List<WetCommand> aCommandList) throws WetException {
    commands = aCommandList;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scriptcreator.IScriptCreator#setFileName(java.lang.String)
   */
  @Override
  public void setFileName(final String aFileName) {
    fileName = aFileName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.scriptcreator.IScriptCreator#setOutputDir(java.lang.String)
   */
  @Override
  public void setOutputDir(final String anOutputDir) {
    outputDir = new File(anOutputDir);
  }
}
