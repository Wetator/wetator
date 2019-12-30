/*
 * Copyright (c) 2008-2018 wetator.org
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.CommandException;
import org.wetator.exception.InvalidInputException;
import org.wetator.i18n.Messages;
import org.wetator.util.SecretString;

/**
 * The context that holds all information about the current executed file and makes them available to the different
 * commands.
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class WetatorContext {

  private static final Logger LOG = LogManager.getLogger(WetatorContext.class);

  /** The name of the {@link Variable} containing the name of the current test case. */
  public static final String VARIABLE_TESTCASE = "wetator.testcase";
  /** The name of the {@link Variable} containing the label of the current browser. */
  public static final String VARIABLE_BROWSER = "wetator.browser";
  /** The name of the {@link Variable} containing the name of the current test file. */
  public static final String VARIABLE_TESTFILE = "wetator.testfile";
  /** The name of the {@link Variable} containing the configured base URL. */
  public static final String VARIABLE_BASEURL = "wetator.baseurl";

  private WetatorEngine engine;
  private String testCaseName;
  private File file;
  private BrowserType browserType;
  private List<Variable> variables; // store them in defined order

  private WetatorContext parentContext;

  private boolean errorOccurred;
  private boolean invalidInput;

  /**
   * Constructor for a root context.
   *
   * @param aWetatorEngine the engine that processes this file
   * @param aTestCaseName the name of the test case this context is for
   * @param aFile the file this context is for
   * @param aBrowserType the emulated browser type
   */
  public WetatorContext(final WetatorEngine aWetatorEngine, final String aTestCaseName, final File aFile,
      final BrowserType aBrowserType) {
    super();
    engine = aWetatorEngine;
    testCaseName = aTestCaseName;
    file = aFile;
    browserType = aBrowserType;
    variables = new LinkedList<>();

    // we add our implicit variables first so they always 'win' against variables with the same name defined
    // programmatically or by configuration
    addVariable(new Variable(VARIABLE_TESTCASE, new SecretString(aTestCaseName)));
    addVariable(new Variable(VARIABLE_BROWSER, new SecretString(aBrowserType.getLabel())));
    addVariable(new Variable(VARIABLE_TESTFILE, new SecretString(aFile.getName())));
    addVariable(new Variable(VARIABLE_BASEURL, new SecretString(getConfiguration().getBaseUrl())));
  }

  /**
   * Constructor for a sub context.
   *
   * @param aContext the parent context
   * @param aFile the file this context is for
   */
  protected WetatorContext(final WetatorContext aContext, final File aFile) {
    this(aContext.engine, aContext.testCaseName, aFile, aContext.browserType);

    parentContext = aContext;
    // do not use setErrorOccurred here as it would also reset the value in the parent context
    errorOccurred = aContext.errorOccurred;
  }

  /**
   * @return the browserType
   */
  public IBrowser.BrowserType getBrowserType() {
    return browserType;
  }

  /**
   * Use this method to create a sub context.
   *
   * @param aFile the file the sub context is for
   * @return the sub context
   */
  public WetatorContext createSubContext(final File aFile) {
    return new WetatorContext(this, aFile);
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
   * @return the configuration
   */
  public WetatorConfiguration getConfiguration() {
    return engine.getConfiguration();
  }

  /**
   * @param aVariable the {@link Variable} to add
   */
  public void addVariable(final Variable aVariable) {
    variables.add(aVariable);
  }

  /**
   * @return the list of known {@link Variable}s
   */
  public List<Variable> getVariables() {
    final List<Variable> tmpResult = new LinkedList<>();

    // we just add all variables to one combined list; as the replace algorithm always takes the first occurrence of a
    // variable we do not need to implement a shadowing or filter mechanism but 'just' ensure the correct order

    // first our own
    tmpResult.addAll(variables);

    // then the stuff from the parent or from the configuration in case of the root context
    if (null == parentContext) {
      tmpResult.addAll(getConfiguration().getVariables());
    } else {
      tmpResult.addAll(parentContext.getVariables());
    }

    return tmpResult;
  }

  /**
   * @param aStringWithPlaceholders the string containing the variables to replace
   * @return the {@link SecretString} (as the result of the replacement)
   */
  public SecretString replaceVariables(final String aStringWithPlaceholders) {
    return new SecretString(aStringWithPlaceholders).replaceVariables(getVariables());
  }

  /**
   * Processes the associated test file by reading all the commands from the file and executing every single command.
   *
   * @return false if execution failed due to invalid input
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   */
  public boolean execute() {
    final File tmpFile = getFile();

    engine.informListenersTestFileStart(tmpFile.getAbsolutePath());
    try {
      final List<Command> tmpCommands = engine.readCommandsFromFile(tmpFile);

      for (final Command tmpCommand : tmpCommands) {
        if (!executeCommand(tmpCommand)) {
          setInvalidInput(true);
        }
      }
    } catch (final InvalidInputException e) {
      engine.informListenersError(e);
      return false;
    } finally {
      engine.informListenersTestFileEnd();
    }
    return !invalidInput;
  }

  private boolean executeCommand(final Command aCommand) {
    engine.informListenersExecuteCommandStart(this, aCommand);
    try {
      if (aCommand.isComment()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Comment: '" + aCommand.toPrintableString(this) + "'");
        }
      } else {
        try {
          if (determineAndExecuteCommandImpl(aCommand)) {
            engine.informListenersExecuteCommandSuccess();
          } else {
            engine.informListenersExecuteCommandIgnored();
          }
        } catch (final AssertionException e) {
          engine.informListenersExecuteCommandFailure(e);
        } catch (final InvalidInputException e) {
          engine.informListenersExecuteCommandError(e);
          setErrorOccurred(true);
          return false;
        } catch (final Exception e) {
          engine.informListenersExecuteCommandError(e);
          setErrorOccurred(true);
        }
      }
    } finally {
      engine.informListenersExecuteCommandEnd();
    }
    return true;
  }

  /**
   * Determines the command implementation for the given {@link Command} and executes it.
   *
   * @param aCommand the command to be executed
   * @return true if the command was executed, false if the command was ignored
   * @throws CommandException in case of a problem executing the command
   * @throws InvalidInputException in case of invalid user input
   */
  public boolean determineAndExecuteCommandImpl(final Command aCommand) throws CommandException, InvalidInputException {
    final ICommandImplementation tmpCommandImplementation = engine.getCommandImplementationFor(aCommand.getName());
    if (null == tmpCommandImplementation) {
      throw new InvalidInputException(Messages.getMessage("unsupportedCommand", aCommand.getName(),
          getFile().getAbsolutePath(), Integer.toString(aCommand.getLineNo())));
    }

    // execute the command only if no error occurred so far or the command should be executed even if an error occurred
    if (!errorOccurred || tmpCommandImplementation.getClass().isAnnotationPresent(ForceExecution.class)) {
      final IBrowser tmpBrowser = getBrowser();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Executing '" + aCommand.toPrintableString(this) + "'");
      }
      try {
        tmpCommandImplementation.execute(this, aCommand);
      } catch (final ActionException e) {
        tmpBrowser.saveCurrentWindowToLog();
        tmpBrowser.checkAndResetFailures();
        throw e;
      } catch (final CommandException | InvalidInputException | RuntimeException | Error e) {
        tmpBrowser.checkAndResetFailures();
        throw e;
      }
      final AssertionException tmpFailure = tmpBrowser.checkAndResetFailures();
      if (null != tmpFailure) {
        throw tmpFailure;
      }
      return true;
    }
    return false;
  }

  /**
   * Informs all listeners about 'warn'.
   *
   * @param aMessageKey the message key of the warning
   * @param aParameters the message parameters
   */
  public void informListenersWarn(final String aMessageKey, final String... aParameters) {
    informListenersWarn(aMessageKey, aParameters, (String) null);
  }

  /**
   * Informs all listeners about 'warn'.
   *
   * @param aMessageKey the message key of the warning
   * @param aParameters the message parameters
   * @param aDetails optional details
   */
  public void informListenersWarn(final String aMessageKey, final String[] aParameters, final String aDetails) {
    engine.informListenersWarn(aMessageKey, aParameters, aDetails);
  }

  /**
   * Informs all listeners about 'warn'.
   *
   * @param aMessageKey the message key of the warning
   * @param aParameters the message parameters
   * @param aThrowable the optional reason (with stacktrace) of the warning
   */
  public void informListenersWarn(final String aMessageKey, final String[] aParameters, final Throwable aThrowable) {
    engine.informListenersWarn(aMessageKey, aParameters, aThrowable);
  }

  /**
   * Informs all listeners about 'info'.
   *
   * @param aMessageKey the message key of the information
   * @param aParameters the message parameters
   */
  public void informListenersInfo(final String aMessageKey, final String... aParameters) {
    engine.informListenersInfo(aMessageKey, (Object[]) aParameters);
  }

  /**
   * Informs all listeners about 'HtmlDescribe'.
   *
   * @param aHtmlDescription the html source
   */
  public void informListenersHtmlDocu(final String aHtmlDescription) {
    engine.informListenersHtmlDescribe(aHtmlDescription);
  }

  /**
   * Sets the errorOccurred to the given value. Additionally if a parent context is present it is set there, too.
   *
   * @param anErrorOccurred the errorOccurred to set
   */
  private void setErrorOccurred(final boolean anErrorOccurred) {
    errorOccurred = anErrorOccurred;
    if (parentContext != null) {
      parentContext.setErrorOccurred(anErrorOccurred);
    }
  }

  /**
   * Sets the invalidInput to the given value. Additionally if a parent context is present it is set there, too.
   *
   * @param anInvalidInput the invalidInput to set
   */
  public void setInvalidInput(final boolean anInvalidInput) {
    invalidInput = anInvalidInput;
    if (parentContext != null) {
      parentContext.setInvalidInput(anInvalidInput);
    }
  }
}
