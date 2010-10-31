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
    /** firefox 3 */
    FIREFOX_3("Firefox3", "Firefox_3"),
    /** firefox 3.6 */
    FIREFOX_3_6("Firefox3.6", "Firefox_3_6"),
    /** the famous internet explorer 6 */
    INTERNET_EXPLORER_6("IE6", "IE_6"),
    /** internet explorer 7 */
    INTERNET_EXPLORER_7("IE7", "IE_7"),
    /** internet explorer 8 */
    INTERNET_EXPLORER_8("IE8", "IE_8");

    private String label;
    private String symbol;

    /**
     * Constructor
     * 
     * @param aLabel the label of the browser
     * @param aSymbol the symbol for the browser (e.g. used by the {@link org.rbri.wet.core.WetConfiguration})
     */
    Browser(String aLabel, String aSymbol) {
      label = aLabel;
      symbol = aSymbol;
    }

    /**
     * Getter for the label
     * 
     * @return the label
     */
    public String getLabel() {
      return label;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * @param aSymbol the symbol to get the browser for
     * @return the found browser or null if found none
     */
    public static Browser getForSymbol(String aSymbol) {
      if (null == aSymbol) {
        return null;
      }

      String tmpSymbol = aSymbol.trim();
      Browser tmpFound = null;
      for (Browser tmpBrowser : values()) {
        if (tmpBrowser.getSymbol().equalsIgnoreCase(tmpSymbol)) {
          tmpFound = tmpBrowser;
          break;
        }
      }
      return tmpFound;
    }
  };

  public ControlFinder getControlFinder() throws AssertionFailedException;

  public void openUrl(URL aUrl) throws AssertionFailedException;

  public void waitForImmediateJobs() throws AssertionFailedException;

  public String waitForTitle(List<SecretString> aTitleToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  public String waitForContent(List<SecretString> aContentToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  public void saveCurrentWindowToLog();

  public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException;

  public void closeWindow(SecretString aWindowName) throws AssertionFailedException;

  public void startNewSession(WetBackend.Browser aBrowser);

  /**
   * The backend manages a list of exceptions detected during the execution
   * of an action. This exceptions are collected. Normally such an exception doesn't stop
   * the processing of the action.<br>
   * 
   * @param aFailure the original problem
   */
  public void addFailure(AssertionFailedException aFailure);

  /**
   * Helper.
   * 
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @param aCause the original problem
   */
  public void addFailure(String aMessageKey, Object[] aParameterArray, Throwable aCause);

  /**
   * This logs all collected exceptions and resets the list.
   * 
   * @return the first {@link AssertionFailedException} in the list or null if the list is empty
   */
  public AssertionFailedException checkAndResetFailures();
}
