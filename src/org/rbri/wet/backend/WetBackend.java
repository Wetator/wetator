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
import java.util.List;

import org.rbri.wet.exception.AssertionFailedException;
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

  /**
   * Enum for the supported content type.
   */
  public enum ContentType {
    /** html */
    HTML,
    /** plain text */
    TEXT,
    /** pdf */
    PDF,
    /** excel */
    XLS,
    /** the rest */
    OTHER
  };

  /**
   * Enum for the supported browsers.
   */
  public enum Browser {
    /** firefox 2 */
    FIREFOX_2("Firefox2"),
    /** firefox 3 */
    FIREFOX_3("Firefox3"),
    /** firefox 3.6 */
    FIREFOX_3_6("Firefox3.6"),
    /** the famous internet explorer 6 */
    INTERNET_EXPLORER_6("IE6"),
    /** internet explorer 7 */
    INTERNET_EXPLORER_7("IE7"),
    /** internet explorer 8 */
    INTERNET_EXPLORER_8("IE8");

    private String label;

    /**
     * Constructor
     * 
     * @param aLabel the label of the browser
     */
    Browser(String aLabel) {
      label = aLabel;
    }

    /**
     * Getter for the label
     * 
     * @return the label
     */
    public String getLabel() {
      return label;
    }
  };

  public ControlFinder getControlFinder() throws AssertionFailedException;

  public void openUrl(URL aUrl) throws AssertionFailedException;

  public String waitForTitle(List<SecretString> aTitleToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  public String waitForContent(List<SecretString> aContentToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  public void saveCurrentWindowToLog();

  public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException;

  public void closeWindow(SecretString aWindowName) throws AssertionFailedException;

  public void startNewSession(WetBackend.Browser aBrowser);

  public void checkFailure() throws AssertionFailedException;

}
