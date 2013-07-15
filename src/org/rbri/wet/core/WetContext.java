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
    private List<Variable> variables; // store them in defined order

    private WetContext parentWetContext;


    /**
     * Constructor for a root context
     *
     * @param aWetEngine the engine that processes this file
     * @param aFile the file this context is for
     */
    public WetContext(WetEngine aWetEngine, File aFile) {
        super();
        engine = aWetEngine;
        file = aFile;
        variables = new LinkedList<Variable>();
    }

    /**
     * Constructor for a subcontext
     *
     * @param aWetContext the parent context
     * @param aFile the file this context is for
     */
    public WetContext(WetContext aWetContext, File aFile) {
        this(aWetContext.engine, aFile);

        parentWetContext = aWetContext;
    }


    public File getFile() {
        return file;
    }


    public WetBackend getWetBackend() {
        return engine.getWetBackend();
    }


    public WetConfiguration getWetConfiguration() {
        return engine.getWetConfiguration();
    }


    public void addVariable(Variable aVariable) {
        variables.add(aVariable);
    }


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


    public SecretString replaceVariables(String aStringWithPlaceholders) {
        String tmpResultValue = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(), false);
        String tmpResultValueForPrint = VariableReplaceUtil.replaceVariables(aStringWithPlaceholders, getVariables(), true);

        return new SecretString(tmpResultValue, tmpResultValueForPrint);
    }


    private void executeCommand(WetCommand aWetCommand) throws WetException {

        engine.informListenersContextExecuteCommandStart(this, aWetCommand);
        try {
            if (aWetCommand.isComment()) {
                LOG.debug("Comment: '" + aWetCommand.toPrintableString(this) + "'");
            } else {
                try {
                    determineAndExecuteCommandImpl(aWetCommand);
                    engine.informListenersContextExecuteCommandSuccess();
                } catch (AssertionFailedException e) {
                    engine.informListenersContextExecuteCommandFailure(e);
                } catch (WetException e) {
                    engine.informListenersContextExecuteCommandError(e);
                    throw e;
                }
            }
        } finally {
            engine.informListenersContextExecuteCommandEnd();
        }
    }


    public void determineAndExecuteCommandImpl(WetCommand aWetCommand) throws WetException, AssertionFailedException {
        WetCommandImplementation tmpImpl = engine.getCommandImplementationFor(aWetCommand.getName());
        if (null == tmpImpl) {
            // TODO better error description
            Assert.fail("unsupportedCommand", new String[] {aWetCommand.getName()});
        }

        LOG.debug("Executing '" + aWetCommand.toPrintableString(this) + "'");
        tmpImpl.execute(this, aWetCommand);
    }


    public void execute() throws WetException {
        File tmpFile = getFile();

        engine.informListenersContextTestStart(tmpFile.getAbsolutePath());
        try {
            List<WetCommand> tmpCommands = engine.readCommandsFromFile(tmpFile);

            for (WetCommand tmpCommand : tmpCommands) {
                executeCommand(tmpCommand);
            }
        } finally {
            engine.informListenersContextTestEnd();
        }
    }


    public void informListenersWarn(String aMessageKey, String[] aParameterArray) {
        engine.informListenersWarn(aMessageKey, aParameterArray);
    }


    public void informListenersInfo(String aMessageKey, String[] aParameterArray) {
        engine.informListenersInfo(aMessageKey, aParameterArray);
    }
}
