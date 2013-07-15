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


package org.rbri.wet.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.WetBackend.Browser;
import org.rbri.wet.backend.htmlunit.HtmlUnitBrowser;
import org.rbri.wet.commandset.WetCommandImplementation;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.result.WetResultWriter;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scripter.WetScripter;

/**
 * The engine that makes the monster running.<br/>
 * Everything that is in common use for the whole test process is stored here.
 * 
 * @author rbri
 */
public final class WetEngine {
  private static final Log LOG = LogFactory.getLog(WetEngine.class);

  private static final String PROPERTY_TEST_CONFIG = "wetator.config";
  private static final String CONFIG_FILE_DEFAULT_NAME = "wetator.config";

  private String configFileName;
  private Map<String, String> externalProperties;
  private List<File> files;

  private WetConfiguration configuration;
  private WetBackend backend;
  private List<WetCommandSet> commandSets;
  private List<WetScripter> scripter;
  private List<WetEngineProgressListener> progressListener;

  public WetEngine() throws WetException {
    super();

    files = new LinkedList<File>();
    progressListener = new LinkedList<WetEngineProgressListener>();
  }

  public void init() throws WetException {
    readWetConfiguration();

    // setup the scripter
    scripter = getWetConfiguration().getScripters();

    // setup the command sets
    commandSets = getWetConfiguration().getCommandSets();

    // setup the browser
    HtmlUnitBrowser tmpBrowser = new HtmlUnitBrowser(this);
    setWetBackend(tmpBrowser);
  }

  /**
   * Adds a test file to be executed.
   * 
   * @param aFile the test file to be added.
   * @throws WetException if the test file does not exist.
   */
  public void addTestFile(File aFile) {
    if (!aFile.exists()) {
      throw new WetException("The test file '" + aFile.getAbsolutePath() + "' does not exist.");
    }
    files.add(aFile);
  }

  public void executeTests() {
    addProgressListener(new WetResultWriter());

    informListenersSetup();
    for (WetCommandSet tmpCommandSet : commandSets) {
      informListenersCommandSetSetup(tmpCommandSet);
    }

    try {
      informListenersTestStart();
      try {
        for (File tmpFile : files) {
          try {
            // TODO
            LOG.info("Executing tests from file '" + tmpFile.getAbsolutePath() + "'");

            for (Browser tmpBrowser : configuration.getBrowsers()) {
              // new session for every (root) file and browser
              getWetBackend().startNewSession(tmpBrowser);

              // setup the context
              WetContext tmpWetContext = new WetContext(this, tmpFile, tmpBrowser);
              tmpWetContext.execute();
            }
          } catch (Throwable e) {
            // informListenersWarn("testCaseError", new String[] {e.getMessage()});
            e.printStackTrace();
          }
        }
      } finally {
        informListenersTestEnd();
      }
    } finally {
      informListenersFinish();
    }
  }

  /**
   * Reads all commands of the given file and returns them in the same order they occur in the file.
   * 
   * @param aFile the file to read the commands from.
   * @return a list of {@link WetCommand}s.
   * @throws WetException if no {@link WetScripter} can be found for the given file.
   */
  protected List<WetCommand> readCommandsFromFile(File aFile) throws WetException {
    WetScripter tmpScripter;
    List<WetCommand> tmpResult;

    tmpScripter = createScripter(aFile);
    tmpResult = tmpScripter.getCommands();

    return tmpResult;
  }

  private void readWetConfiguration() throws WetException {
    File tmpConfigFile = getConfigFile();
    configuration = new WetConfiguration(tmpConfigFile, getExternalProperties());
  }

  private WetScripter createScripter(File aFile) {
    for (WetScripter tmpScripter : scripter) {
      if (tmpScripter.isSupported(aFile)) {
        tmpScripter.setFile(aFile);
        return tmpScripter;
      }
    }
    throw new WetException("No scripter found for file '" + aFile.getAbsolutePath() + "'.");
  }

  protected WetCommandImplementation getCommandImplementationFor(String aCommandName) throws WetException {
    for (WetCommandSet tmpCommandSet : commandSets) {
      WetCommandImplementation tmpCommandImplementation;
      tmpCommandImplementation = tmpCommandSet.getCommandImplementationFor(aCommandName);
      if (null != tmpCommandImplementation) {
        return tmpCommandImplementation;
      }
    }
    return null;
  }

  public File getConfigFile() {
    String tmpConfigName = getConfigFileName();

    // ok try harder
    if (null == tmpConfigName) {
      tmpConfigName = System.getProperty(PROPERTY_TEST_CONFIG, CONFIG_FILE_DEFAULT_NAME);
    }

    File tmpConfigFile;
    tmpConfigFile = new File(tmpConfigName);
    return tmpConfigFile;
  }

  /**
   * @return the backend
   */
  public WetBackend getWetBackend() {
    return backend;
  }

  /**
   * @param aWetBackend the backend to set
   */
  public void setWetBackend(WetBackend aWetBackend) {
    backend = aWetBackend;
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
  public void setConfigFileName(String aConfigFileName) {
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
  public void setExternalProperties(Map<String, String> aExternalProperties) {
    externalProperties = aExternalProperties;
  }

  /**
   * Adds the given {@link WetEngineProgressListener} as listener. If this listener is already added it will not be
   * added again but the listener added first will be taken.
   * 
   * @param aProgressListener the listener to add
   */
  public void addProgressListener(WetEngineProgressListener aProgressListener) {
    if (progressListener.contains(aProgressListener)) {
      return;
    }
    progressListener.add(aProgressListener);
  }

  /**
   * Informs all listeners about 'engineSetup'.
   */
  protected void informListenersSetup() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.engineSetup(this);
    }
  }

  /**
   * Informs all listeners about 'commandSetSetup'.
   * 
   * @param aWetCommandSet the {@link WetCommandSet} that was set up
   */
  protected void informListenersCommandSetSetup(WetCommandSet aWetCommandSet) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.commandSetSetup(aWetCommandSet);
    }
  }

  /**
   * Informs all listeners about 'engineTestStart'.
   */
  protected void informListenersTestStart() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.engineTestStart();
    }
  }

  /**
   * Informs all listeners about 'engineTestEnd'.
   */
  protected void informListenersTestEnd() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.engineTestEnd();
    }
  }

  /**
   * Informs all listeners about 'engineFinish'.
   */
  protected void informListenersFinish() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.engineFinish();
    }
  }

  /**
   * Informs all listeners about 'contextTestStart'.
   * 
   * @param aFileName the file name of the test started.
   * @param aBrowserName the browser name of the test started.
   */
  protected void informListenersContextTestStart(String aFileName, String aBrowserName) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextTestStart(aFileName, aBrowserName);
    }
  }

  /**
   * Informs all listeners about 'contextTestEnd'.
   */
  protected void informListenersContextTestEnd() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextTestEnd();
    }
  }

  /**
   * Informs all listeners about 'contextExecuteCommandStart'.
   * 
   * @param aWetContext the {@link WetContext} used to execute the command.
   * @param aCommand the {@link WetCommand} to be executed.
   */
  protected void informListenersContextExecuteCommandStart(WetContext aWetContext, WetCommand aCommand) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextExecuteCommandStart(aWetContext, aCommand);
    }
  }

  /**
   * Informs all listeners about 'contextExecuteCommandEnd'.
   */
  protected void informListenersContextExecuteCommandEnd() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextExecuteCommandEnd();
    }
  }

  /**
   * Informs all listeners about 'contextExecuteCommandSuccess'.
   */
  protected void informListenersContextExecuteCommandSuccess() {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextExecuteCommandSuccess();
    }
  }

  /**
   * Informs all listeners about 'contextExecuteCommandFailure'.
   * 
   * @param anAssertionFailedException The exception thrown by the failed command.
   */
  protected void informListenersContextExecuteCommandFailure(AssertionFailedException anAssertionFailedException) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextExecuteCommandFailure(anAssertionFailedException);
    }
  }

  /**
   * Informs all listeners about 'contextExecuteCommandError'.
   * 
   * @param aThrowable The exception thrown by the command.
   */
  protected void informListenersContextExecuteCommandError(Throwable aThrowable) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.contextExecuteCommandError(aThrowable);
    }
  }

  /**
   * Informs all listeners about 'warn'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  protected void informListenersWarn(String aMessageKey, String[] aParameterArray) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.warn(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'info'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersInfo(String aMessageKey, String[] aParameterArray) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.info(aMessageKey, aParameterArray);
    }
  }

  /**
   * Informs all listeners about 'engineResponseStored'.
   * 
   * @param aResponseFileName the file name of the stored response.
   */
  public void informListenersResponseStored(String aResponseFileName) {
    for (WetEngineProgressListener tmpListener : progressListener) {
      tmpListener.engineResponseStored(aResponseFileName);
    }
  }
}
