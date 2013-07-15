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

import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.exception.AssertionFailedException;



/**
 * The interface for listeners of the engine
 * progress. Register a listener to be informed.
 *  
 * @author rbri
 */
public interface WetEngineProgressListener {
    public void engineSetup(WetEngine aWetEngine);
    public void engineTestStart();
    public void engineResponseStored(String aResponseFileName);
    public void engineTestEnd();
    public void engineFinish();

    public void commandSetSetup(WetCommandSet aWetCommandSet);

    public void contextTestStart(String aFileName);
    public void contextExecuteCommandStart(WetContext aWetContext, WetCommand aCommand);
    public void contextExecuteCommandSuccess();
    public void contextExecuteCommandFailure(AssertionFailedException anAssertionFailedException);
    public void contextExecuteCommandError(Throwable aThrowable);
    public void contextExecuteCommandEnd();
    public void contextTestEnd();

    public void warn(String aMessageKey, String[] aParameterArray);
    public void info(String aMessageKey, String[] aParameterArray);
}
