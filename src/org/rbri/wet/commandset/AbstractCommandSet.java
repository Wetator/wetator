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
import org.rbri.wet.exception.WetException;
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
    public Map<String, WetCommandImplementation> commandImplementations;
    protected int noOfCommands;

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


    public final WetCommandImplementation getCommandImplementationFor(String aCommandName) {
        return commandImplementations.get(aCommandName);
    }


    public List<String> getInitializationMessages() {
        return initializationMessages;
    }


    public void addInitializationMessage(String aMessage) {
        initializationMessages.add(aMessage);
    }


    /**
     * Implement this method to do the whole setup of your set.
     */
    protected abstract void registerCommands();


    protected void registerCommand(String aCommandName, WetCommandImplementation aWetCommandImplementation) {
        LOG.debug(ClassUtils.getShortClassName(this.getClass()) + " - register command : '" + aCommandName + "'");
        commandImplementations.put(aCommandName, aWetCommandImplementation);
        noOfCommands++;
    }


    protected WetBackend getWetBackend(WetContext aWetContext) throws AssertionFailedException {
        WetBackend tmpWetBackend = aWetContext.getWetBackend();
        return tmpWetBackend;
    }


    protected Control getRequiredFirstHtmlElementFrom(WetContext aWetContext, WeightedControlList aWeightedControlList, List<SecretString> aSearchParam) throws AssertionFailedException, WetException {
        if (aWeightedControlList.isEmpty()) {
            Assert.fail("noHtmlElementFound", new String[] {SecretString.toString(aSearchParam)});
        }

        WeightedControlList.Entry tmpEntry = aWeightedControlList.getElementsSorted().get(0);
        if (aWeightedControlList.hasManyEntires()) {
            aWetContext.informListenersWarn("manyElementsFound", new String[] {SecretString.toString(aSearchParam), tmpEntry.getControl().getDescribingText()});

            List<WeightedControlList.Entry> tmpEntries = aWeightedControlList.getElementsSorted();
            for (WeightedControlList.Entry tmpEachEntry : tmpEntries) {
                aWetContext.informListenersInfo("elementFound", new String[] {tmpEachEntry.toString()});
            }
        }
        return tmpEntry.getControl();
    }
}
