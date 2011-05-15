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


package org.wetator.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.backend.htmlunit.HtmlUnitBrowser;
import org.wetator.commandset.WetCommandImplementation;
import org.wetator.commandset.ICommandSet;
import org.wetator.core.result.WetResultWriter;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.WetException;
import org.wetator.scripter.IScripter;

/**
 * The engine that makes the monster running.<br/>
 * Everything that is in common use for the whole test process is stored here.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class WetEngine {
  private static final Log LOG = LogFactory.getLog(WetEngine.class);

  private static final String PROPERTY_TEST_CONFIG = "wetator.config";
  private static final String CONFIG_FILE_DEFAULT_NAME = "wetator.config";

  private String configFileName;
  private Map<String, String> externalProperties;
  private List<File> files;

  private WetConfiguration configuration;
  private IBrowser browser;
  private List<ICommandSet> commandSets;
  private List<IScripter> scripter;
  private List<WetProgressListener> progressListener;

  /**
   * The constructor.
   * 
   * @throws WetException in case of problems
   */
  public WetEngine() throws WetException {
    super();

    files = new LinkedList<File>();
    progressListener = new LinkedList<WetProgressListener>();
  }

  /**
   * Initializes the wetator engine. The configuration is read from the configuration file got by
   * {@link #getConfigFile()}.
   * 
   * @throws WetException in case of problems
   */
  public void init() throws WetException {
    informListenersInit();
    init(readWetConfiguration());
  }

  /**
   * Initializes the wetator engine using the given configuration.
   * 
   * @param aWetConfiguration the configuration to use
   * @throws WetException in case of problems
   */
  public void init(final WetConfiguration aWetConfiguration) throws WetException {
    configuration = aWetConfiguration;
    if (configFileName == null) {
      configFileName = "";
    }

    // setup the scripter
    scripter = getWetConfiguration().getScripters();

    // setup the command sets
    commandSets = getWetConfiguration().getCommandSets();

    // setup the browser
    final HtmlUnitBrowser tmpBrowser = new HtmlUnitBrowser(this);
    setBrowser(tmpBrowser);
  }

  /**
   * Returns the list of all test files.
   * 
   * @return the list of all test files
   */
  public List<File> getTestFiles() {
    return files;
  }

  /**
   * Adds a test file to be executed.
   * 
   * @param aFile the test file to be added.
   * @throws WetException if the test file does not exist.
   */
  public void addTestFile(final File aFile) {
    if (!aFile.exists()) {
      throw new WetException("The test file '" + aFile.getAbsolutePath() + "' does not exist.");
    }
    files.add(aFile);
  }

  /**
   * Executes the tests.
   */
  public void executeTests() {
    // create new result writer and call the init() method.
    final WetResultWriter tmpResultWriter = new WetResultWriter();
    tmpResultWriter.init(this);
    addProgressListener(tmpResultWriter);

    informListenersStart();
    try {
      for (File tmpFile : files) {
        LOG.info("Executing tests from file '" + tmpFile.getAbsolutePath() + "'");
        informListenersTestCaseStart(tmpFile.getName());
        try {
          for (BrowserType tmpBrowserType : configuration.getBrowserTypes()) {
            informListenersTestRunStart(tmpBrowserType.getLabel());
            try {
              // new session for every (root) file and browser
              getBrowser().startNewSession(tmpBrowserType);

              // setup the context
              final WetContext tmpWetContext = new WetContext(this, tmpFile, tmpBrowserType);
              tmpWetContext.execute();
            } finally {
              informListenersTestRunEnd();
            }
          }
        } catch (final Throwable e) {
          // TODO what to do with exceptions?
          // informListenersWarn("testCaseError", new String[] {e.getMessage()});
          e.printStackTrace();
        } finally {
          informListenersTestCaseEnd();
        }

      }
    } finally {
      informListenersEnd();
    }
  }

  /**
   * Reads all commands of the given file and returns them in the same order they occur in the file.
   * 
   * @param aFile the file to read the commands from.
   * @return a list of {@link WetCommand}s.
   * @throws WetException if no {@link IScripter} can be found for the given file.
   */
  protected List<WetCommand> readCommandsFromFile(final File aFile) throws WetException {
    final IScripter tmpScripter = createScripter(aFile);
    final List<WetCommand> tmpResult = tmpScripter.getCommands();

    return tmpResult;
  }

  private WetConfiguration readWetConfiguration() throws WetException {
    final File tmpConfigFile = getConfigFile();
    return new WetConfiguration(tmpConfigFile, getExternalProperties());
  }

  private IScripter createScripter(final File aFile) {
    for (IScripter tmpScripter : scripter) {
      if (tmpScripter.isSupported(aFile)) {
        tmpScripter.setFile(aFile);
        return tmpScripter;
      }
    }
    throw new WetException("No scripter found for file '" + aFile.getAbsolutePath() + "'.");
  }

  /**
   * @param aCommandName the name of the {@link WetCommandImplementation}
   * @return the {@link WetCommandImplementation} for the given name or null if none was found
   */
  protected WetCommandImplementation getCommandImplementationFor(final String aCommandName) {
    for (ICommandSet tmpCommandSet : commandSets) {
      WetCommandImplementation tmpCommandImplementation;
      tmpCommandImplementation = tmpCommandSet.getCommandImplementationFor(aCommandName);
      if (null != tmpCommandImplementation) {
        return tmpCommandImplementation;
      }
    }
    return null;
  }

  /**
   * Returns the configuration file. The configuration file name is searched in:
   * <ol>
   * <li>{@link #getConfigFileName()}</li>
   * <li>the system property <code>wetator.config</code></li>
   * <li>the default configuration file name 'wetator.config'</li>
   * </ol>
   * 
   * @return the configuration file
   */
  public File getConfigFile() {
    String tmpConfigName = getConfigFileName();

    // config was initialized directly
    if ("".equals(tmpConfigName)) {
      return null;
    }

    // ok try harder
    if (null == tmpConfigName) {
      tmpConfigName = System.getProperty(PROPERTY_TEST_CONFIG, CONFIG_FILE_DEFAULT_NAME);
    }

    File tmpConfigFile;
    tmpConfigFile = new File(tmpConfigName);
    return tmpConfigFile;
  }

  /**
   * @return the {@link IBrowser}
   */
  public IBrowser getBrowser() {
    return browser;
  }

  /**
   * @param aBrowser the browser to set
   */
  public void setBrowser(final IBrowser aBrowser) {
    browser = aBrowser;
  }

  /**
   * @return the configuration
   */
  public WetConfiguration getWetConfiguration() {
    return configuration;
  }

  /**
   * @return the configFileName
   */
  public String getConfigFileName() {
    return configFileName;
  }

  /**
   * @param aConfigFileName the configFileName to set
   */
  public void setConfigFileName(final String aConfigFileName) {
    configFileName = aConfigFileName;
  }

  /**
   * @return the externalProperties
   */
  public Map<String, String> getExternalProperties() {
    return externalProperties;
  }

  /**
   * @param aExternalProperties the externalProperties to set
   */
  public void setExternalProperties(final Map<String, String> aExternalProperties) {
    externalProperties = aExternalProperties;
  }

  /**
   * Adds the given {@link WetProgressListener} as listener. If this listener is already added it will not be
   * added again but the listener added first will be taken.
   * 
   * @param aProgressListener the listener to add
   */
  public void addProgressListener(final WetProgressListener aProgressListener) {
    if (progressListener.contains(aProgressListener)) {
      return;
    }
    progressListener.add(aProgressListener);
  }

  /**
   * Informs all listeners about 'init'.
   */
  protected void informListenersInit() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.init(this);
    }
  }

  /**
   * Informs all listeners about 'start'.
   */
  protected void informListenersStart() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.start(this);
    }
  }

  /**
   * Informs all listeners about 'testStart'.
   * 
   * @param aTestName the file name of the test started.
   */
  protected void informListenersTestCaseStart(final String aTestName) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testCaseStart(aTestName);
    }
  }

  /**
   * Informs all listeners about 'testRunStart'.
   * 
   * @param aBrowserName the browser name of the test started.
   */
  protected void informListenersTestRunStart(final String aBrowserName) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testRunStart(aBrowserName);
    }
  }

  /**
   * Informs all listeners about 'testFileStart'.
   * 
   * @param aFileName the file name of the test started.
   */
  protected void informListenersTestFileStart(final String aFileName) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testFileStart(aFileName);
    }
  }

  /**
   * Informs all listeners about 'executeCommandStart'.
   * 
   * @param aWetContext the {@link WetContext} used to execute the command.
   * @param aCommand the {@link WetCommand} to be executed.
   */
  protected void informListenersExecuteCommandStart(final WetContext aWetContext, final WetCommand aCommand) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandStart(aWetContext, aCommand);
    }
  }

  /**
   * Informs all listeners about 'executeCommandEnd'.
   */
  protected void informListenersExecuteCommandEnd() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandEnd();
    }
  }

  /**
   * Informs all listeners about 'executeCommandSuccess'.
   */
  protected void informListenersExecuteCommandSuccess() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandSuccess();
    }
  }

  /**
   * Informs all listeners about 'executeCommandFailure'.
   * 
   * @param anAssertionFailedException The exception thrown by the failed command.
   */
  protected void informListenersExecuteCommandFailure(final AssertionFailedException anAssertionFailedException) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandFailure(anAssertionFailedException);
    }
  }

  /**
   * Informs all listeners about 'executeCommandError'.
   * 
   * @param aThrowable The exception thrown by the command.
   */
  protected void informListenersExecuteCommandError(final Throwable aThrowable) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.executeCommandError(aThrowable);
    }
  }

  /**
   * Informs all listeners about 'testFileEnd'.
   */
  protected void informListenersTestFileEnd() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testFileEnd();
    }
  }

  /**
   * Informs all listeners about 'testRunEnd'.
   */
  protected void informListenersTestRunEnd() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testRunEnd();
    }
  }

  /**
   * Informs all listeners about 'testEnd'.
   */
  protected void informListenersTestCaseEnd() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.testCaseEnd();
    }
  }

  /**
   * Informs all listeners about 'end'.
   */
  protected void informListenersEnd() {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.end(this);
    }
  }

  /**
   * Informs all listeners about 'warn'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersWarn(final String aMessageKey, final String[] aParameterArray) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.warn(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'info'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersInfo(final String aMessageKey, final String[] aParameterArray) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.info(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'engineResponseStored'.
   * 
   * @param aResponseFileName the file name of the stored response.
   */
  public void informListenersResponseStored(final String aResponseFileName) {
    for (WetProgressListener tmpListener : progressListener) {
      tmpListener.responseStored(aResponseFileName);
    }
  }
}