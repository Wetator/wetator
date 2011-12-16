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


package org.wetator.commandset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.backend.IBrowser;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.ICommandSet;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionFailedException;
import org.wetator.exception.AssertionFailedException;
import org.wetator.exception.CommandExecutionException;
import org.wetator.exception.WrongCommandUsageException;
import org.wetator.i18n.Messages;

/**
 * A parent class for command sets.
 * 
 * @author rbri
 */
public abstract class AbstractCommandSet implements ICommandSet {

  private static final Log LOG = LogFactory.getLog(AbstractCommandSet.class);

  private List<String> initializationMessages;
  private Map<String, ICommandImplementation> commandImplementations;
  private int noOfCommands;

  /**
   * The constructor.
   */
  protected AbstractCommandSet() {
    super();
    initializationMessages = new LinkedList<String>();
    noOfCommands = 0;

    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registration started");
    commandImplementations = new HashMap<String, ICommandImplementation>();

    // initialize the list of supported commands
    registerCommands();

    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registered; " + noOfCommands + " commands added");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.ICommandSet#getCommandImplementationFor(java.lang.String)
   */
  @Override
  public final ICommandImplementation getCommandImplementationFor(final String aCommandName) {
    return commandImplementations.get(aCommandName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.ICommandSet#getInitializationMessages()
   */
  @Override
  public List<String> getInitializationMessages() {
    return initializationMessages;
  }

  /**
   * Adds an initialization message to this command set.
   * 
   * @param aMessage the message to be added
   */
  public void addInitializationMessage(final String aMessage) {
    initializationMessages.add(aMessage);
  }

  /**
   * Implement this method to do the whole setup of your set.
   */
  protected abstract void registerCommands();

  /**
   * Registers a command under the given name.
   * 
   * @param aCommandName the name of the command
   * @param aCommandImplementation the implementation (class) of the command
   */
  protected void registerCommand(final String aCommandName, final ICommandImplementation aCommandImplementation) {
    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " - register command : '" + aCommandName + "'");
    commandImplementations.put(aCommandName, aCommandImplementation);
    noOfCommands++;
  }

  /**
   * @param aContext the context
   * @return the {@link IBrowser}
   */
  protected IBrowser getBrowser(final WetatorContext aContext) {
    final IBrowser tmpBrowser = aContext.getBrowser();
    return tmpBrowser;
  }

  /**
   * Returns the first control from the WeightedControlList.<br>
   * If the list is empty an AssertionFailedException is thrown.<br>
   * If the list has elements for more than one control then some warnings are fired.
   * 
   * @param aContext the context
   * @param aWeightedControlList the WeightedControlList
   * @param aWPath the wpath (only needed for the warning message)
   * @param aNoElementFoundKey the key used to resolve the 'no element found' message.
   * @return the first control from the list
   * @throws CommandExecutionException if the list is empty
   */
  protected IControl getRequiredFirstHtmlElementFrom(final WetatorContext aContext,
      final WeightedControlList aWeightedControlList, final WPath aWPath, final String aNoElementFoundKey)
      throws CommandExecutionException {
    if (aWeightedControlList.isEmpty()) {
      throwCommandExecutionException(aNoElementFoundKey, new String[] { aWPath.toString() });
    }

    final List<WeightedControlList.Entry> tmpEntries = aWeightedControlList.getEntriesSorted();
    final WeightedControlList.Entry tmpEntry = tmpEntries.get(0);

    if (tmpEntries.size() > 1) {
      aContext.informListenersWarn("manyElementsFound", new String[] { aWPath.toString(),
          tmpEntry.getControl().getDescribingText() });
    }

    for (WeightedControlList.Entry tmpEachEntry : tmpEntries) {
      aContext.informListenersInfo("elementFound", new String[] { tmpEachEntry.toString() });
    }

    return tmpEntry.getControl();
  }

  /**
   * Throws a {@link CommandExecutionException} containing an {@link AssertionFailedException} with the given message.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws CommandExecutionException the created exception
   */
  protected void assertionFailed(final String aMessageKey, final Object[] aParameterArray)
      throws CommandExecutionException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    assertionFailed(new AssertionFailedException(tmpMessage));
  }

  /**
   * Throws a {@link CommandExecutionException} containing the given {@link AssertionFailedException}.
   * 
   * @param anException the AssertionFailedException
   * @throws CommandExecutionException the created exception
   */
  protected void assertionFailed(final AssertionFailedException anException) throws CommandExecutionException {
    throw new CommandExecutionException("Assertion failed.", anException);
  }

  /**
   * Throws a {@link CommandExecutionException} containing an {@link ActionFailedException} with the given message.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws CommandExecutionException the created exception
   */
  protected void actionFailed(final String aMessageKey, final Object[] aParameterArray)
      throws CommandExecutionException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    actionFailed(new ActionFailedException(tmpMessage));
  }

  /**
   * Throws a {@link CommandExecutionException} containing the given {@link ActionFailedException}.
   * 
   * @param anException the AssertionFailedException
   * @throws CommandExecutionException the created exception
   */
  protected void actionFailed(final ActionFailedException anException) throws CommandExecutionException {
    throw new CommandExecutionException("Action failed.", anException);
  }

  /**
   * Throws a WrongCommandUsageException with the given message.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws WrongCommandUsageException the created exception
   */
  protected void wrongCommandUsage(final String aMessageKey, final Object[] aParameterArray)
      throws WrongCommandUsageException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    throw new WrongCommandUsageException(tmpMessage);
  }

  /**
   * Throws a CommandExecutionException with the given message.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @throws CommandExecutionException the created exception
   */
  protected void throwCommandExecutionException(final String aMessageKey, final Object[] aParameterArray)
      throws CommandExecutionException {
    final String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
    throw new CommandExecutionException(tmpMessage);
  }
}
