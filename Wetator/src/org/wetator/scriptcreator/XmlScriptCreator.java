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
import java.util.ArrayList;
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

  private static final String TEST_CASE_XSD_VERSION = "1.0.0";

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

      // get used namespaces
      final List<NamespaceBean> tmpNamespaces = new ArrayList<NamespaceBean>();
      for (WetCommand tmpCommand : commands) {
        if (!tmpCommand.isComment() && StringUtils.isNotEmpty(tmpCommand.getName())) {
          final CommandType tmpCommandType = tmpModel.getCommandType(tmpCommand.getName());
          if (tmpCommandType == null) {
            throw new RuntimeException("Unknown command '" + tmpCommand.getName() + "'.");
          }
          final NamespaceBean tmpBean = NamespaceBean.create(tmpCommandType.getNamespace());
          if (!tmpNamespaces.contains(tmpBean)) {
            tmpNamespaces.add(tmpBean);
          }
        }
      }

      final File tmpFile = new File(outputDir, fileName + ".wet");
      final XMLStreamWriter tmpWriter = tmpFactory.createXMLStreamWriter(new FileOutputStream(tmpFile), XML_ENCODING);

      tmpWriter.writeStartDocument(XML_ENCODING, XML_VERSION);
      tmpWriter.writeCharacters("\n");

      tmpWriter.writeStartElement(E_TEST_CASE);
      tmpWriter.writeDefaultNamespace("http://www.wetator.org/xsd/test-case");
      for (NamespaceBean tmpNamespaceBean : tmpNamespaces) {
        tmpWriter.writeNamespace(tmpNamespaceBean.getSymbol(), tmpNamespaceBean.getNamespace());
      }
      // tmpWriter.writeNamespace("d", "http://www.wetator.org/xsd/default-command-set");
      // tmpWriter.writeNamespace("s", "http://www.wetator.org/xsd/sql-command-set");
      // tmpWriter.writeNamespace("i", "http://www.wetator.org/xsd/incubator-command-set");
      // tmpWriter.writeNamespace("t", "http://www.wetator.org/xsd/test-command-set");
      tmpWriter.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      final StringBuilder tmpLocations = new StringBuilder("http://www.wetator.org/xsd/test-case test-case-"
          + TEST_CASE_XSD_VERSION + ".xsd\n");
      for (NamespaceBean tmpNamespaceBean : tmpNamespaces) {
        tmpLocations.append(tmpNamespaceBean.getNamespace());
        tmpLocations.append(" ");
        tmpLocations.append(tmpNamespaceBean.getLocation());
        tmpLocations.append("\n");
      }
      tmpWriter.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation",
          tmpLocations.substring(0, tmpLocations.length() - 1));
      // tmpWriter.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation",
      // "http://www.wetator.org/xsd/test-case test-case-" + XSD_VERSION + ".xsd\n"
      // + "http://www.wetator.org/xsd/default-command-set default-command-set-" + XSD_VERSION + ".xsd\n"
      // + "http://www.wetator.org/xsd/sql-command-set sql-command-set-" + XSD_VERSION + ".xsd\n"
      // + "http://www.wetator.org/xsd/incubator-command-set incubator-command-set-" + XSD_VERSION + ".xsd\n"
      // + "http://www.wetator.org/xsd/test-command-set test-command-set-" + XSD_VERSION + ".xsd");
      tmpWriter.writeAttribute(A_VERSION, TEST_CASE_XSD_VERSION);
      tmpWriter.writeCharacters("\n");
      for (WetCommand tmpCommand : commands) {
        tmpWriter.writeCharacters("    ");
        if (tmpCommand.isComment() && StringUtils.isEmpty(tmpCommand.getName())) {
          tmpWriter.writeStartElement(E_COMMENT);
          if (tmpCommand.getFirstParameter() != null) {
            writeContent(tmpWriter, tmpCommand.getFirstParameter().getValue());
          }
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

  /**
   * Little helper to store some namespace information.
   * 
   * @author frank.danek
   */
  private static class NamespaceBean {

    private String symbol;
    private String namespace;
    private String location;

    public static NamespaceBean create(final String aNamespace) {
      if ("http://www.wetator.org/xsd/default-command-set".equals(aNamespace)) {
        return new NamespaceBean("d", aNamespace, "default-command-set-1.0.0.xsd");
      }
      if ("http://www.wetator.org/xsd/sql-command-set".equals(aNamespace)) {
        return new NamespaceBean("s", aNamespace, "sql-command-set-1.0.0.xsd");
      }
      if ("http://www.wetator.org/xsd/incubator-command-set".equals(aNamespace)) {
        return new NamespaceBean("i", aNamespace, "incubator-command-set-1.0.0.xsd");
      }
      if ("http://www.wetator.org/xsd/test-command-set".equals(aNamespace)) {
        return new NamespaceBean("t", aNamespace, "test-command-set-1.0.0.xsd");
      }
      throw new RuntimeException("Unknown command set '" + aNamespace + "'.");
    }

    /**
     * @param aSymbol the symbol to set
     * @param aNamespace the namespace to set
     * @param aLocation the location to set
     */
    public NamespaceBean(final String aSymbol, final String aNamespace, final String aLocation) {
      symbol = aSymbol;
      namespace = aNamespace;
      location = aLocation;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
      return namespace;
    }

    /**
     * @return the location
     */
    public String getLocation() {
      return location;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int tmpPrime = 31;
      int tmpResult = 1;
      tmpResult = tmpPrime * tmpResult;
      if (location == null) {
        tmpResult += location.hashCode();
      }
      tmpResult = tmpPrime * tmpResult;
      if (namespace == null) {
        tmpResult += namespace.hashCode();
      }
      tmpResult = tmpPrime * tmpResult;
      if (symbol == null) {
        tmpResult += symbol.hashCode();
      }
      return tmpResult;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object anObj) {
      if (this == anObj) {
        return true;
      }
      if (anObj == null) {
        return false;
      }
      if (getClass() != anObj.getClass()) {
        return false;
      }
      final NamespaceBean tmpOther = (NamespaceBean) anObj;
      if (location == null) {
        if (tmpOther.location != null) {
          return false;
        }
      } else if (!location.equals(tmpOther.location)) {
        return false;
      }
      if (namespace == null) {
        if (tmpOther.namespace != null) {
          return false;
        }
      } else if (!namespace.equals(tmpOther.namespace)) {
        return false;
      }
      if (symbol == null) {
        if (tmpOther.symbol != null) {
          return false;
        }
      } else if (!symbol.equals(tmpOther.symbol)) {
        return false;
      }
      return true;
    }
  }
}
