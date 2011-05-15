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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.commandset.ICommandImplementation;
import org.wetator.core.variable.Variable;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.WetException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;
import org.wetator.util.VariableReplaceUtil;

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
  private BrowserType browserType;
  private List<Variable> variables; // store them in defined order

  private WetContext parentWetContext;

  /**
   * Constructor for a root context.
   * 
   * @param aWetEngine the engine that processes this file
   * @param aFile the file this context is for
   * @param aBrowserType the emulated browser type
   */
  public WetContext(final WetEngine aWetEngine, final File aFile, final BrowserType aBrowserType) {
    super();
    engine = aWetEngine;
    file = aFile;
    browserType = aBrowserType;
    variables = new LinkedList<Variable>();
  }

  /**
   * Constructor for a sub context.
   * 
   * @param aWetContext the parent context
   * @param aFile the file this context is for
   */
  protected WetContext(final WetContext aWetContext, final File aFile) {
    this(aWetContext.engine, aFile, aWetContext.browserType);

    parentWetContext = aWetContext;
  }

  /**
   * Use this method to create a sub context.
   * 
   * @param aFile the file the sub context is for
   * @return the sub context
   */
  public WetContext createSubContext(final File aFile) {
    return new WetContext(this, aFile);
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * @return the {@link IBrowser}
   */
  public IBrowser getBrowser() {
    return engine.getBrowser();
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
  public void addVariable(final Variable aVariable) {
    variables.add(aVariable);
  }

  /**
   * @return the list of known {@link Variable}s.
   */
  public List<Variable> getVariables() {
    final List<Variable> tmpResult = new LinkedList<Variable>();

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
  public SecretString replaceVariables(final String aStringWithPlaceholders) {
    final String tmpResultValue = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(), false);
    final String tmpResultValueForPrint = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(),
        true);

    return new SecretString(tmpResultValue, tmpResultValueForPrint);
  }

  private void executeCommand(final WetCommand aWetCommand) {
    engine.informListenersExecuteCommandStart(this, aWetCommand);
    try {
      if (aWetCommand.isComment()) {
        LOG.debug("Comment: '" + aWetCommand.toPrintableString(this) + "'");
      } else {
        try {
          determineAndExecuteCommandImpl(aWetCommand);
          engine.informListenersExecuteCommandSuccess();
        } catch (final AssertionFailedException e) {
          engine.informListenersExecuteCommandFailure(e);
        } catch (final WetException e) {
          engine.informListenersExecuteCommandError(e);
          throw e;
        }
      }
    } finally {
      engine.informListenersExecuteCommandEnd();
    }
  }

  /**
   * Determines the command implementation for the given {@link WetCommand} and executes it.
   * 
   * @param aWetCommand the command to be executed
   * @throws AssertionFailedException if no command implementation was found or the execution fails
   */
  public void determineAndExecuteCommandImpl(final WetCommand aWetCommand) throws AssertionFailedException {
    final ICommandImplementation tmpCommandImplementation = engine.getCommandImplementationFor(aWetCommand.getName());
    if (null == tmpCommandImplementation) {
      Assert.fail("unsupportedCommand", new String[] { aWetCommand.getName(), getFile().getAbsolutePath(),
          "" + aWetCommand.getLineNo() });
    }

    final IBrowser tmpBrowser = getBrowser();
    LOG.debug("Executing '" + aWetCommand.toPrintableString(this) + "'");
    try {
      tmpCommandImplementation.execute(this, aWetCommand);
    } catch (final AssertionFailedException e) {
      tmpBrowser.checkAndResetFailures();
      throw e;
    }
    final AssertionFailedException tmpFailed = tmpBrowser.checkAndResetFailures();
    if (null != tmpFailed) {
      throw tmpFailed;
    }
  }

  /**
   * Processes the associated test file by<br>
   * reading all the command from the file and <br>
   * executing every single command.
   */
  public void execute() {
    final File tmpFile = getFile();

    engine.informListenersTestFileStart(tmpFile.getAbsolutePath());
    try {
      final List<WetCommand> tmpCommands = engine.readCommandsFromFile(tmpFile);

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
  public void informListenersWarn(final String aMessageKey, final String[] aParameterArray) {
    engine.informListenersWarn(aMessageKey, aParameterArray);
  }

  /**
   * Informs all listeners about 'info'.
   * 
   * @param aMessageKey the message key of the warning.
   * @param aParameterArray the message parameters.
   */
  public void informListenersInfo(final String aMessageKey, final String[] aParameterArray) {
    engine.informListenersInfo(aMessageKey, aParameterArray);
  }
}
