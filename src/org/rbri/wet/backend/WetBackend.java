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


package org.rbri.wet.backend;

import java.net.URL;

import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.SecretString;


/**
 * The interface for all browsers.
 * This is more or less a tagging interface.
 * Every command has to check for the right
 * implementation.
 *
 * @author rbri
 */
public interface WetBackend {

    public enum ContentType { HTML, TEXT, PDF, XLS, OTHER };

    public enum Browser { FIREFOX_2, FIREFOX_3, INTERNET_EXPLORER_6, INTERNET_EXPLORER_7, INTERNET_EXPLORER_8 };

    public ControlFinder getControlFinder() throws AssertionFailedException;

    public void openUrl(URL aUrl) throws WetException, AssertionFailedException;
    public String getCurrentTitle() throws AssertionFailedException;
    public String getCurrentContentAsString() throws AssertionFailedException;
    public void saveCurrentWindowToLog();
    public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException;
    public void closeWindow(SecretString aWindowName) throws AssertionFailedException;
    public void startNewSession();
    public void checkFailure() throws AssertionFailedException;

}
