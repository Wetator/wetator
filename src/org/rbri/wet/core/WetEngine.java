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
import org.rbri.wet.backend.htmlunit.HtmlUnitBrowser;
import org.rbri.wet.commandset.WetCommandImplementation;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.result.WetResultWriter;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scripter.WetScripter;


/**
 * The engine that makes the monster running
 * Everything that is in common use for the
 * whole test process is stored there.
 *
 * @author rbri
 */
public final class WetEngine {
    private static final Log LOG = LogFactory.getLog(WetEngine.class);

    private static final String PROPERTY_TEST_CONFIG = "wetator.config";
    private static final String CONFIG_FILE_DEFAULT_NAME = "wetator.config";

    private String configFileName;
    private Map<String, String> externalProperties;
    private LinkedList<File> files;

    private WetConfiguration configuration;
    private WetBackend backend;
    private List<WetCommandSet> commandSets;
    private List<WetScripter> scripter;
    private List<WetEngineProgressListener> progressListener;


    public WetBackend getWetBackend() {
        return backend;
    }


    public void setWetBackend(WetBackend aWetBackend) {
        backend = aWetBackend;
    }


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


    public void addTestFile(File aFile) throws WetException {
        if (!aFile.exists()) {
            throw new WetException("The test file '" + aFile.getAbsolutePath() + "' does not exist.");
        }
        files.add(aFile);
    }


    public void executeTests() throws WetException {
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

	                    // new session for every (root) file
	                    getWetBackend().startNewSession();

	                    // setup the context
	                    WetContext tmpWetContext = new WetContext(this, tmpFile);
	                    tmpWetContext.execute();
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


    protected List<WetCommand> readCommandsFromFile(File aFile) throws WetException {
        WetScripter tmpScripter;
        List<WetCommand> tmpResult;

        tmpScripter = createScripter(aFile);
        tmpResult = tmpScripter.getCommands();

        return tmpResult;
    }



    public WetConfiguration getWetConfiguration() {
        return configuration;
    }


    private void readWetConfiguration() throws WetException {
        File tmpConfigFile = getConfigFile();
        configuration = new WetConfiguration(tmpConfigFile, getExternalProperties());
    }


    private WetScripter createScripter(File aFile) throws WetException {
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


    public String getConfigFileName() {
        return configFileName;
    }


    public void setConfigFileName(String aConfigFileName) {
        configFileName = aConfigFileName;
    }


    protected Map<String, String> getExternalProperties() {
        return externalProperties;
    }


    public void setExternalProperties(Map<String, String> anExternalProperties) {
        externalProperties = anExternalProperties;
    }


    public void addProgressListener(WetEngineProgressListener aProgressListener) {
        if (progressListener.contains(aProgressListener)) {
            return;
        }
        progressListener.add(aProgressListener);
    }


    protected void informListenersSetup() throws WetException {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.engineSetup(this);
        }
    }

    protected void informListenersCommandSetSetup(WetCommandSet aWetCommandSet) throws WetException {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.commandSetSetup(aWetCommandSet);
        }
    }

    protected void informListenersTestStart() throws WetException {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.engineTestStart();
        }
    }

    protected void informListenersTestEnd() {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.engineTestEnd();
        }
    }

    protected void informListenersFinish() {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.engineFinish();
        }
    }


    protected void informListenersContextTestStart(String aFileName) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextTestStart(aFileName);
        }
    }


    protected void informListenersContextTestEnd() {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextTestEnd();
        }
    }


    protected void informListenersContextExecuteCommandStart(WetContext aWetContext, WetCommand aCommand) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextExecuteCommandStart(aWetContext, aCommand);
        }
    }


    protected void informListenersContextExecuteCommandEnd() {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextExecuteCommandEnd();
        }
    }


    protected void informListenersContextExecuteCommandSuccess() {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextExecuteCommandSuccess();
        }
    }


    protected void informListenersContextExecuteCommandFailure(AssertionFailedException anAssertionFailedException) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextExecuteCommandFailure(anAssertionFailedException);
        }
    }


    protected void informListenersContextExecuteCommandError(Throwable aThrowable) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.contextExecuteCommandError(aThrowable);
        }
    }

    protected void informListenersWarn(String aMessageKey, String[] aParameterArray) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.warn(aMessageKey, aParameterArray);
        }
    }


    public void informListenersInfo(String aMessageKey, String[] aParameterArray) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.info(aMessageKey, aParameterArray);
        }
    }


    public void informListenersResponseStored(String aResponseFileName) {
        for (WetEngineProgressListener tmpListener : progressListener) {
            tmpListener.engineResponseStored(aResponseFileName);
        }
    }
}
