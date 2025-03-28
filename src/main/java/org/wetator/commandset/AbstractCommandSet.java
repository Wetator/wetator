/*
 * Copyright (c) 2008-2025 wetator.org
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.core.ICommandImplementation;
import org.wetator.core.ICommandSet;
import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.BackendException;
import org.wetator.i18n.Messages;

/**
 * A parent class for command sets.
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public abstract class AbstractCommandSet implements ICommandSet {

  private static final Logger LOG = LogManager.getLogger(AbstractCommandSet.class);

  private List<String> initializationMessages;
  private Map<String, ICommandImplementation> commandImplementations;
  private int noOfCommands;

  /**
   * The constructor.
   */
  protected AbstractCommandSet() {
    super();
    initializationMessages = new LinkedList<>();
    noOfCommands = 0;

    if (LOG.isDebugEnabled()) {
      LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registration started");
    }
    commandImplementations = new HashMap<>();

    // initialize the list of supported commands
    registerCommands();

    if (LOG.isDebugEnabled()) {
      LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registered; " + noOfCommands + " commands added");
    }
  }

  @Override
  public final ICommandImplementation getCommandImplementationFor(final String aCommandName) {
    return commandImplementations.get(aCommandName);
  }

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
    if (LOG.isDebugEnabled()) {
      LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " - register command : '" + aCommandName + "'");
    }
    commandImplementations.put(aCommandName, aCommandImplementation);
    noOfCommands++;
  }

  /**
   * @param aContext the context
   * @return the {@link IBrowser}
   */
  protected IBrowser getBrowser(final WetatorContext aContext) {
    return aContext.getBrowser();
  }

  /**
   * @param aBrowser the browser
   * @return the {@link IControlFinder}
   * @throws ActionException in case of problems
   */
  protected IControlFinder getControlFinder(final IBrowser aBrowser) throws ActionException {
    final IControlFinder tmpControlFinder;
    try {
      tmpControlFinder = aBrowser.getControlFinder();
    } catch (final BackendException e) {
      final String tmpMessage = Messages.getMessage("commandBackendError", e.getMessage());
      throw new ActionException(tmpMessage, e);
    }
    return tmpControlFinder;
  }

  /**
   * Returns the first control from the WeightedControlList or null if no controls found.<br>
   * If the list has elements for more than one control then some warnings are fired.
   *
   * @param aContext the context
   * @param aWeightedControlList the WeightedControlList
   * @param aWPath the wpath (only needed for the warning message)
   * @param aNothingFoundMsgKey the key for the exception message used when nothing found
   * @return the first control from the list
   * @throws ActionException if nothing found
   */
  protected IControl getFirstRequiredHtmlElementFrom(final WetatorContext aContext,
      final WeightedControlList aWeightedControlList, final WPath aWPath, final String aNothingFoundMsgKey)
      throws ActionException {
    final IControl tmpControl = getFirstHtmlElementFrom(aContext, aWeightedControlList, aWPath);
    if (null == tmpControl) {
      final String tmpMessage = Messages.getMessage(aNothingFoundMsgKey, aWPath.toString());
      throw new ActionException(tmpMessage);
    }
    return tmpControl;
  }

  /**
   * Returns the first control from the WeightedControlList or null if no controls found.<br>
   * If the list has elements for more than one control then some warnings are fired.
   *
   * @param aContext the context
   * @param aWeightedControlList the WeightedControlList
   * @param aWPath the wpath (only needed for the warning message)
   * @return the first control from the list or null
   */
  protected IControl getFirstHtmlElementFrom(final WetatorContext aContext,
      final WeightedControlList aWeightedControlList, final WPath aWPath) {
    if (aWeightedControlList.isEmpty()) {
      return null;
    }

    final List<WeightedControlList.Entry> tmpEntries = aWeightedControlList.getEntriesSorted();
    final WeightedControlList.Entry tmpEntry = tmpEntries.get(0);

    if (tmpEntries.size() > 1) {
      aContext.informListenersInfo("manyElementsFound", aWPath.toString(), tmpEntry.getControl().getDescribingText());
    }

    for (final WeightedControlList.Entry tmpEachEntry : tmpEntries) {
      aContext.informListenersInfo("elementFound", tmpEachEntry.toString());
    }

    return tmpEntry.getControl();
  }
}
