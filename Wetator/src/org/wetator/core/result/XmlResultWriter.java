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


package org.wetator.core.result;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.Version;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.backend.control.Control;
import org.wetator.commandset.ICommandSet;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.Variable;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.AssertionFailedException;
import org.wetator.i18n.Messages;
import org.wetator.scripter.IScripter;
import org.wetator.util.Output;
import org.wetator.util.SecretString;
import org.wetator.util.StringUtil;
import org.wetator.util.XmlUtil;

/**
 * The class that generates the XML output.
 * 
 * @author rbri
 * @author frank.danek
 */
public class XmlResultWriter implements IProgressListener {
  private static final Log LOG = LogFactory.getLog(XmlResultWriter.class);

  private static final String TAG_WET = "wet";
  private static final String TAG_ABOUT = "about";
  private static final String TAG_PRODUCT = "product";
  private static final String TAG_VERSION = "version";
  private static final String TAG_BUILD = "build";
  private static final String TAG_START_TIME = "startTime";
  private static final String TAG_TEST_FILE = "testFile";
  private static final String TAG_EXECUTION_TIME = "executionTime";
  private static final String TAG_TESTCASE = "testcase";
  private static final String TAG_TESTRUN = "testrun";
  private static final String TAG_TESTFILE = "testfile";
  private static final String TAG_COMMAND = "command";
  private static final String TAG_FIRST_PARAM = "param0";
  private static final String TAG_SECOND_PARAM = "param1";
  private static final String TAG_RESPONSE = "response";
  private static final String TAG_LOG = "log";
  private static final String TAG_LEVEL = "level";
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_ERROR = "error";
  private static final String TAG_CONFIGURATION = "configuration";
  private static final String TAG_VARIABLES = "variables";
  private static final String TAG_VARIABLE = "variable";
  private static final String TAG_PROPERTY = "property";
  private static final String TAG_COMMAND_SET = "commandSet";
  private static final String TAG_CONTROL = "control";

  private Writer writer;
  private Output output;
  private XmlUtil xmlUtil;
  private File resultFile;
  private File outputDir;
  private List<String> xslTemplates;

  private long tagId;
  private long executionStartTime;
  private long commandExecutionStartTime;

  /**
   * The constructor.
   */
  public XmlResultWriter() {
    tagId = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#init(WetatorEngine)
   */
  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    try {
      final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();

      outputDir = tmpConfiguration.getOutputDir();
      xslTemplates = tmpConfiguration.getXslTemplates();
      resultFile = new File(outputDir, "wetresult.xml");

      writer = new FileWriterWithEncoding(resultFile, "UTF-8");
      output = new Output(writer, "  ");
      xmlUtil = new XmlUtil("UTF-8");

      // start writing
      output.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      output.println();

      printlnStartTag(TAG_WET);

      printlnStartTag(TAG_ABOUT);

      printlnNode(TAG_PRODUCT, Version.getProductName());
      printlnNode(TAG_VERSION, Version.getVersion());
      printlnNode(TAG_BUILD, Version.getBuild());

      printlnEndTag(TAG_ABOUT);

    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#start(WetatorEngine)
   */
  @Override
  public void start(final WetatorEngine aWetatorEngine) {
    try {
      final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();

      // print the configuration
      printlnStartTag(TAG_CONFIGURATION);

      printConfigurationProperty(WetatorConfiguration.PROPERTY_BASE_URL, tmpConfiguration.getBaseUrl());
      for (BrowserType tmpBrowserType : tmpConfiguration.getBrowserTypes()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_BROWSER_TYPE, tmpBrowserType.getLabel());
      }
      printConfigurationProperty(WetatorConfiguration.PROPERTY_ACCEPT_LANGUAGE, tmpConfiguration.getAcceptLanaguage());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_OUTPUT_DIR, tmpConfiguration.getOutputDir()
          .getAbsolutePath());
      for (String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_XSL_TEMPLATES, tmpTemplate);
      }
      for (ICommandSet tmpCommandSet : tmpConfiguration.getCommandSets()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_COMMAND_SETS, tmpCommandSet.getClass().getName());
      }
      for (Class<? extends Control> tmpControl : tmpConfiguration.getControls()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_CONTROLS, tmpControl.getName());
      }
      for (IScripter tmpScripter : tmpConfiguration.getScripters()) {
        printConfigurationProperty(WetatorConfiguration.PROPERTY_SCRIPTERS, tmpScripter.getClass().getName());
      }

      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_HOST, tmpConfiguration.getProxyHost());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_PORT,
          Integer.toString(tmpConfiguration.getProxyPort()));
      // writeConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_HOSTS_TO_BYPASS,
      // tmpConfiguration.getProxyHostsToBypass());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_PROXY_USER, tmpConfiguration.getProxyUser());
      printConfigurationProperty(WetatorConfiguration.PROPERTY_BASIC_AUTH_USER, tmpConfiguration.getBasicAuthUser());

      printlnStartTag(TAG_VARIABLES);

      final List<Variable> tmpVariables = tmpConfiguration.getVariables();
      for (Variable tmpVariable : tmpVariables) {
        printStartTagOpener(TAG_VARIABLE);
        output.print("name=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpVariable.getName()));
        output.print("\" value=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpVariable.getValue().toString()));
        output.println("\" />");
      }

      printlnEndTag(TAG_VARIABLES);

      final List<ICommandSet> tmpCommandSets = tmpConfiguration.getCommandSets();
      for (ICommandSet tmpCommandSet : tmpCommandSets) {
        printStartTagOpener(TAG_COMMAND_SET);
        output.print("class=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpCommandSet.getClass().toString()));
        output.println("\" >");

        output.indent();
        for (String tmpMessage : tmpCommandSet.getInitializationMessages()) {
          printLogMessage("INFO", tmpMessage);
        }
        output.unindent();

        printEndTag(TAG_COMMAND_SET);
      }

      final List<Class<? extends Control>> tmpControls = tmpConfiguration.getControls();
      for (Class<? extends Control> tmpControl : tmpControls) {
        printStartTagOpener(TAG_CONTROL);
        output.print("class=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpControl.getClass().toString()));
        output.println("\" />");
      }

      printlnEndTag(TAG_CONFIGURATION);

      printlnNode(TAG_START_TIME, StringUtil.formatDate(new Date()));
      for (File tmpFile : aWetatorEngine.getTestFiles()) {
        printlnNode(TAG_TEST_FILE, tmpFile.getAbsolutePath());
      }

      executionStartTime = System.currentTimeMillis();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseStart(String)
   */
  @Override
  public void testCaseStart(final String aTestName) {
    try {
      printStartTagOpener(TAG_TESTCASE);
      output.print("name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aTestName));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(final String aBrowserName) {
    try {
      printStartTagOpener(TAG_TESTRUN);
      output.print("browser=\"");
      output.print(xmlUtil.normalizeAttributeValue(aBrowserName));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(final String aFileName) {
    try {
      printStartTagOpener(TAG_TESTFILE);
      output.print("file=\"");
      output.print(xmlUtil.normalizeAttributeValue(aFileName));
      output.println("\">");
      output.indent();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandStart(org.wetator.core.WetatorContext,
   *      org.wetator.core.Command)
   */
  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aCommand) {
    try {
      printStartTagOpener(TAG_COMMAND);
      output.print("name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aCommand.getName()));
      output.print("\" line=\"" + aCommand.getLineNo());
      if (aCommand.isComment()) {
        output.print("\" isComment=\"true");
      }
      output.println("\" >");
      output.indent();

      Parameter tmpParameter = aCommand.getFirstParameter();
      printStartTag(TAG_FIRST_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aContext).toString()));
      }
      printEndTag(TAG_FIRST_PARAM);
      output.println();

      tmpParameter = aCommand.getSecondParameter();
      printStartTag(TAG_SECOND_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aContext).toString()));
      }
      printEndTag(TAG_SECOND_PARAM);
      output.println();

      commandExecutionStartTime = System.currentTimeMillis();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
    try {
      printlnNode(TAG_EXECUTION_TIME, "" + (System.currentTimeMillis() - commandExecutionStartTime));

      printlnEndTag(TAG_COMMAND);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandFailure(org.wetator.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(final AssertionFailedException anAssertionFailedException) {
    try {
      printErrorStart(anAssertionFailedException);

      final Throwable tmpThrowable = anAssertionFailedException.getCause();
      if (null != tmpThrowable) {
        executeCommandError(tmpThrowable);
      }
      printErrorEnd();
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(final Throwable aThrowable) {
    try {
      printErrorStart(aThrowable);

      final Throwable tmpThrowable = aThrowable.getCause();
      if (null != tmpThrowable) {
        executeCommandError(tmpThrowable);
      }
      printErrorEnd();
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    try {
      printlnEndTag(TAG_TESTFILE);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    try {
      printlnEndTag(TAG_TESTRUN);
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
    try {
      printlnEndTag(TAG_TESTCASE);
      flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(final String aResponseFileName) {
    try {
      printlnNode(TAG_RESPONSE, aResponseFileName);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#end(WetatorEngine)
   */
  @Override
  public void end(final WetatorEngine aWetatorEngine) {
    try {
      printlnNode(TAG_EXECUTION_TIME, "" + (System.currentTimeMillis() - executionStartTime));

      printlnEndTag(TAG_WET);
      output.flush();
      writer.close();

      if (!xslTemplates.isEmpty()) {
        final XslTransformer tmpXslTransformer = new XslTransformer(resultFile);
        tmpXslTransformer.transform(xslTemplates, outputDir);
      }
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#warn(java.lang.String, java.lang.String[])
   */
  @Override
  public void warn(final String aMessageKey, final String[] aParameterArray) {
    try {
      final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
      if (LOG.isWarnEnabled()) {
        LOG.warn(tmpMessage);
      }
      printLogMessage("WARN", tmpMessage);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(final String aMessageKey, final String[] aParameterArray) {
    try {
      final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
      if (LOG.isInfoEnabled()) {
        LOG.info(tmpMessage);
      }
      printLogMessage("INFO", tmpMessage);
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void printLogMessage(final String aLevel, final String aMessage) throws IOException {
    printlnStartTag(TAG_LOG);

    printlnNode(TAG_LEVEL, aLevel);
    printlnNode(TAG_MESSAGE, aMessage);

    printlnEndTag(TAG_LOG);
  }

  private void printErrorStart(final Throwable aThrowable) throws IOException {
    printlnStartTag(TAG_ERROR);
    printlnNode(TAG_MESSAGE, aThrowable.getMessage());

    // TODO trace
  }

  private void printErrorEnd() throws IOException {
    printlnEndTag(TAG_ERROR);
  }

  private void printConfigurationProperty(final String aKey, final String aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print("key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue));
    }
    output.println("\" />");
  }

  private void printConfigurationProperty(final String aKey, final SecretString aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print("key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue.toString()));
    }
    output.println("\" />");
  }

  private void printlnNode(final String aNodeName, final String aNodeValue) throws IOException {
    printStartTag(aNodeName);
    output.print(xmlUtil.normalizeBodyValue(aNodeValue));
    printEndTag(aNodeName);
    output.println();
  }

  private void printlnStartTag(final String aName) throws IOException {
    printStartTag(aName);
    output.println();
    output.indent();
  }

  private void printStartTag(final String aName) throws IOException {
    printStartTagOpener(aName);
    output.print(">");
  }

  private void printlnEndTag(final String aName) throws IOException {
    output.unindent();
    printEndTag(aName);
    output.println();
  }

  private void printEndTag(final String aName) throws IOException {
    output.print("</").print(aName).print(">");
  }

  private void printStartTagOpener(final String aName) throws IOException {
    output.print("<").print(aName).print(" id=\"" + tagId++).print("\" ");
  }

  private void flush() throws IOException {
    output.flush();
  }
}