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


package org.rbri.wet.util;

import java.io.File;

import org.rbri.wet.Version;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetEngineProgressListener;
import org.rbri.wet.exception.AssertionFailedException;


/**
 * Simple progress listener that writes to stdout.
 *
 * @author rbri
 */
public class StdOutProgressListener implements WetEngineProgressListener {

    private static final int dotsPerLine = 100;

    private long stepsCount;
    private long errorCount;
    private long failureCount;
    private int dotCount;
    private int contextDeep;


    public void engineSetup(WetEngine aWetEngine) {
        println(Version.getProductName() + " " + Version.getVersion());
        println("  using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version " + com.gargoylesoftware.htmlunit.Version.getProductVersion());

        stepsCount = 0;
        errorCount = 0;
        failureCount = 0;
        contextDeep = 0;

        File tmpConfigFile = aWetEngine.getConfigFile();
        if (null != tmpConfigFile) {
            println("  Config: '" + tmpConfigFile.getAbsolutePath() + "'");
        }
    }


    public void contextExecuteCommandStart(WetContext aWetContext, WetCommand aWommand) {
        stepsCount++;
    }


    public void contextExecuteCommandEnd() {
    }


    public void contextExecuteCommandError(Throwable aThrowable) {
        errorCount++;

        if (dotCount == dotsPerLine) {
            println("E");
            dotCount = 1;
            return;
        }
        print("E");
        dotCount++;
    }


    public void contextExecuteCommandFailure(AssertionFailedException anAssertionFailedException) {
        failureCount++;

        if (dotCount == dotsPerLine) {
            println("F");
            dotCount = 1;
            return;
        }
        print("F");
        dotCount++;
    }


    public void contextExecuteCommandSuccess() {
        if (dotCount == dotsPerLine) {
            println(".");
            dotCount = 1;
            return;
        }
        print(".");
        dotCount++;
    }


    public void contextTestEnd() {
        contextDeep--;
        if (contextDeep > 0) {
            // subcontext
            print(">");
        } else {
            println("");
        }
    }


    public void contextTestStart(String aFileName) {
        if (contextDeep > 0) {
            // subcontext
            print("<");
        } else {
            println("Test: " + aFileName);
            dotCount = 1;
        }
        contextDeep++;
    }


    public void engineTestStart() {
    }


    public void engineResponseStored(String aResponseFileName) {
    }


    public void engineTestEnd() {
    }


    public void engineFinish() {
        // print summary
        println("");
        println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
    }


    public void commandSetSetup(WetCommandSet wetCommandSet) {
    }

    public void warn(String aMessageKey, String[] aParameterArray) {
    }


    public void info(String aMessageKey, String[] aParameterArray) {
    }


    protected void println(String aString) {
        System.out.println(aString);
    }

    protected void print(String aString) {
        System.out.print(aString);
    }
}
