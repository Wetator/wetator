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


package org.rbri.wet.commandset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.Control;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WetBackend;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;
import org.rbri.wet.util.SecretString;

/**
 * A parent class for command sets.
 * 
 * @author rbri
 */
public abstract class AbstractCommandSet implements WetCommandSet {
  private static final Log LOG = LogFactory.getLog(AbstractCommandSet.class);

  private List<String> initializationMessages;
  private Map<String, WetCommandImplementation> commandImplementations;
  private int noOfCommands;

  /**
   * Constructor
   */
  protected AbstractCommandSet() {
    super();
    initializationMessages = new LinkedList<String>();
    noOfCommands = 0;

    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registration started");
    commandImplementations = new HashMap<String, WetCommandImplementation>();

    // initialize the list of supported commands
    registerCommands();

    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " registered; " + noOfCommands + " commands added");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#getCommandImplementationFor(java.lang.String)
   */
  @Override
  public final WetCommandImplementation getCommandImplementationFor(String aCommandName) {
    return commandImplementations.get(aCommandName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.commandset.WetCommandSet#getInitializationMessages()
   */
  @Override
  public List<String> getInitializationMessages() {
    return initializationMessages;
  }

  /**
   * Adds an initialization message to this command set
   * 
   * @param aMessage the message to be added
   */
  public void addInitializationMessage(String aMessage) {
    initializationMessages.add(aMessage);
  }

  /**
   * Implement this method to do the whole setup of your set.
   */
  protected abstract void registerCommands();

  /**
   * Registers a command under the given name
   * 
   * @param aCommandName the name of the command
   * @param aWetCommandImplementation the implementation (class) of the command
   */
  protected void registerCommand(String aCommandName, WetCommandImplementation aWetCommandImplementation) {
    LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " - register command : '" + aCommandName + "'");
    commandImplementations.put(aCommandName, aWetCommandImplementation);
    noOfCommands++;
  }

  /**
   * Getter for the backend
   * 
   * @param aWetContext the wet context
   * @return the WetBackend
   */
  protected WetBackend getWetBackend(WetContext aWetContext) {
    WetBackend tmpWetBackend = aWetContext.getWetBackend();
    return tmpWetBackend;
  }

  /**
   * Returns the first control from the WeightedControlList<br>
   * if the list is empty a AssertionFailedException is thrown<br>
   * if the list has elements for more than one control then some warnings are fired
   * 
   * @param aWetContext the wet context
   * @param aWeightedControlList the WeightedControlList
   * @param aSearchParam the search parameter (only needed for the warning message)
   * @return the first control from the list
   * @throws AssertionFailedException if the list is empty
   */
  protected Control getRequiredFirstHtmlElementFrom(WetContext aWetContext, WeightedControlList aWeightedControlList,
      List<SecretString> aSearchParam) throws AssertionFailedException {
    if (aWeightedControlList.isEmpty()) {
      Assert.fail("noHtmlElementFound", new String[] { SecretString.toString(aSearchParam) });
    }

    List<WeightedControlList.Entry> tmpEntries = aWeightedControlList.getElementsSorted();
    WeightedControlList.Entry tmpEntry = tmpEntries.get(0);

    if (tmpEntries.size() > 1) {
      aWetContext.informListenersWarn("manyElementsFound", new String[] { SecretString.toString(aSearchParam),
          tmpEntry.getControl().getDescribingText() });
    }

    for (WeightedControlList.Entry tmpEachEntry : tmpEntries) {
      aWetContext.informListenersInfo("elementFound", new String[] { tmpEachEntry.toString() });
    }

    return tmpEntry.getControl();
  }
}
