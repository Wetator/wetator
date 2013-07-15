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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.backend.WetBackend.Browser;
import org.rbri.wet.commandset.WetCommandImplementation;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.VariableReplaceUtil;

/**
 * The context that holds all information about
 * the current executed file and make
 * them available to the different commands.
 * 
 * @author rbri
 */
public class WetContext {
  private static final Log LOG = LogFactory.getLog(WetContext.class);

  private WetEngine engine;
  private File file;
  private Browser browser;
  private List<Variable> variables; // store them in defined order

  private WetContext parentWetContext;

  /**
   * Constructor for a root context
   * 
   * @param aWetEngine the engine that processes this file
   * @param aFile the file this context is for
   * @param aBrowser the emulated browser
   */
  public WetContext(WetEngine aWetEngine, File aFile, Browser aBrowser) {
    super();
    engine = aWetEngine;
    file = aFile;
    browser = aBrowser;
    variables = new LinkedList<Variable>();
  }

  /**
   * Constructor for a sub context
   * 
   * @param aWetContext the parent context
   * @param aFile the file this context is for
   */
  protected WetContext(WetContext aWetContext, File aFile) {
    this(aWetContext.engine, aFile, aWetContext.browser);

    parentWetContext = aWetContext;
  }

  /**
   * Use this method to create a sub context.
   * 
   * @param aFile the file the sub context is for
   * @return the sub context
   */
  public WetContext createSubContext(File aFile) {
    return new WetContext(this, aFile);
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * @return the wetBackend
   */
  public WetBackend getWetBackend() {
    return engine.getWetBackend();
  }

  /**
   * @return the wetConfiguration
   */
  public WetConfiguration getWetConfiguration() {
    return engine.getWetConfiguration();
  }

  /**
   * @param aVariable the {@link Variable} to add
   */
  public void addVariable(Variable aVariable) {
    variables.add(aVariable);
  }

  /**
   * @return the list of known {@link Variable}s.
   */
  public List<Variable> getVariables() {
    List<Variable> tmpResult = new LinkedList<Variable>();

    // first our own
    tmpResult.addAll(variables);

    // then the stuff from parent or configuration
    if (null == parentWetContext) {
      tmpResult.addAll(getWetConfiguration().getVariables());
    } else {
      tmpResult.addAll(parentWetContext.getVariables());
    }

    return tmpResult;
  }

  /**
   * @param aStringWithPlaceholders the string containing the variables to replace
   * @return the {@link SecretString} (as the result of the replacement)
   */
  public SecretString replaceVariables(String aStringWithPlaceholders) {
    String tmpResultValue = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(), false);
    String tmpResultValueForPrint = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(), true);

    return new SecretString(tmpResultValue, tmpResultValueForPrint);
  }

  private void executeCommand(WetCommand aWetCommand) {
    engine.informListenersExecuteCommandStart(this, aWetCommand);
    try {
      if (aWetCommand.isComment()) {
        LOG.debug("Comment: '" + aWetCommand.toPrintableString(this) + "'");
      } else {
        try {
          determineAndExecuteCommandImpl(aWetCommand);
          engine.informListenersExecuteCommandSuccess();
        } catch (AssertionFailedException e) {
          engine.informListenersExecuteCommandFailure(e);
        } catch (WetException e) {
          engine.informListenersExecuteCommandError(e);
          throw e;
        }
      }
    } finally {
      engine.informListenersExecuteCommandEnd();
    }
  }

  public void determineAndExecuteCommandImpl(WetCommand aWetCommand) throws AssertionFailedException {
    WetCommandImplementation tmpImpl = engine.getCommandImplementationFor(aWetCommand.getName());
    if (null == tmpImpl) {
      // TODO better error description
      Assert.fail("unsupportedCommand", new String[] { aWetCommand.getName() });
    }

    WetBackend tmpBackend = getWetBackend();
    LOG.debug("Executing '" + aWetCommand.toPrintableString(this) + "'");
    try {
      tmpImpl.execute(this, aWetCommand);
    } catch (AssertionFailedException e) {
      tmpBackend.checkAndResetFailures();
      throw e;
    }
    AssertionFailedException tmpFailed = tmpBackend.checkAndResetFailures();
    if (null != tmpFailed) {
      throw tmpFailed;
    }
  }

  public void execute() {
    File tmpFile = getFile();

    engine.informListenersTestFileStart(tmpFile.getAbsolutePath());
    try {
      List<WetCommand> tmpCommands = engine.readCommandsFromFile(tmpFile);

      for (WetCommand tmpCommand : tmpCommands) {
        executeCommand(tmpCommand);
      }
    } finally {
      engine.informListenersTestFileEnd();
    }
  }

  /**
   * Informs all listeners about 'warn'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersWarn(String aMessageKey, String[] aParameterArray) {
    engine.informListenersWarn(aMessageKey, aParameterArray);
  }

  /**
   * Informs all listeners about 'info'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersInfo(String aMessageKey, String[] aParameterArray) {
    engine.informListenersInfo(aMessageKey, aParameterArray);
  }
}
