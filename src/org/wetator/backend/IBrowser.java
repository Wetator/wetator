/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.backend;

import java.net.URL;
import java.util.List;

import org.wetator.backend.control.Control;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.SecretString;

/**
 * The interface for all browsers.
 * This is more or less a tagging interface.
 * Every command has to check for the right
 * implementation.
 * 
 * @author rbri
 */
public interface IBrowser {

  /**
   * Enum for the supported content type.
   */
  public enum ContentType {
    /** html. */
    HTML,
    /** css. */
    CSS,
    /** javascript. */
    JAVASCRIPT,
    /** plain text. */
    TEXT,
    /** xml. */
    XML,
    /** pdf. */
    PDF,
    /** excel. */
    XLS,
    /** rtf. */
    RTF,
    /** png. */
    PNG,
    /** gif. */
    GIF,
    /** bmp. */
    BMP,
    /** jpeg. */
    JPEG,
    /** the rest. */
    OTHER
  };

  /**
   * Enum for the supported browser types.
   */
  public enum BrowserType {
    /** firefox 3. */
    FIREFOX_3("Firefox3", "Firefox_3"),
    /** firefox 3.6. */
    FIREFOX_3_6("Firefox3.6", "Firefox_3_6"),
    /** the famous internet explorer 6. */
    INTERNET_EXPLORER_6("IE6", "IE_6"),
    /** internet explorer 7. */
    INTERNET_EXPLORER_7("IE7", "IE_7"),
    /** internet explorer 8. */
    INTERNET_EXPLORER_8("IE8", "IE_8");

    private String label;
    private String symbol;

    /**
     * The constructor.
     * 
     * @param aLabel the label of the browser type
     * @param aSymbol the symbol for the browser type (e.g. used by the {@link org.wetator.core.WetatorConfiguration})
     */
    BrowserType(final String aLabel, final String aSymbol) {
      label = aLabel;
      symbol = aSymbol;
    }

    /**
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
     * @param aSymbol the symbol to get the browser type for
     * @return the found browser type or null if found none
     */
    public static BrowserType getForSymbol(final String aSymbol) {
      if (null == aSymbol) {
        return null;
      }

      final String tmpSymbol = aSymbol.trim();
      BrowserType tmpFound = null;
      for (BrowserType tmpBrowserType : values()) {
        if (tmpBrowserType.getSymbol().equalsIgnoreCase(tmpSymbol)) {
          tmpFound = tmpBrowserType;
          break;
        }
      }
      return tmpFound;
    }
  };

  /**
   * Returns the {@link IControlFinder} for this backend.
   * Every supported backend has his own ControlFinder.
   * 
   * @return the ControlFinder for this backend
   * @throws AssertionFailedException in case of error
   */
  public IControlFinder getControlFinder() throws AssertionFailedException;

  /**
   * Opens the given URL in the current window.
   * 
   * @param aUrl the URL to open
   * @throws AssertionFailedException in case of error
   */
  public void openUrl(URL aUrl) throws AssertionFailedException;

  /**
   * Wait until the 'immediate' javascript jobs are finished.
   * 
   * @throws AssertionFailedException in case of error
   */
  public void waitForImmediateJobs() throws AssertionFailedException;

  /**
   * Checks, if the page title contains the given list of strings.<br>
   * If the text is not found, than this method waits at aTimeoutInSeconds
   * and checks the title again. If the text is still not found than an
   * AssertionFailedException is thrown.
   * 
   * @param aTitleToWaitFor the expected text (parts)
   * @param aTimeoutInSeconds the timeout in seconds, if less than 1s than 1s is used
   * @return true, if there was a page change during the wait
   * @throws AssertionFailedException if the content was not available
   */
  public boolean assertTitleInTimeFrame(List<SecretString> aTitleToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  /**
   * Checks, if the page content contains the given list of strings.<br>
   * If the content is not found, than this method waits at aTimeoutInSeconds
   * and checks the content again. If the content is still not found than an
   * AssertionFailedException is thrown.
   * 
   * @param aContentToWaitFor the expected content (parts)
   * @param aTimeoutInSeconds the timeout in seconds, if less than 1s than 1s is used
   * @return true, if there was a page change during the wait
   * @throws AssertionFailedException if the content was not available
   */
  public boolean assertContentInTimeFrame(List<SecretString> aContentToWaitFor, long aTimeoutInSeconds)
      throws AssertionFailedException;

  /**
   * Saves the content of the current window to the log.
   * 
   * @param aControls the controls to be highlighted
   */
  public void saveCurrentWindowToLog(Control... aControls);

  /**
   * Goes back (simulates the browser's back button) in the current window.
   * 
   * @param aSteps the number of steps to go back
   * @throws AssertionFailedException in case of error
   */
  public void goBackInCurrentWindow(int aSteps) throws AssertionFailedException;

  /**
   * Closes the window with the given name.
   * 
   * @param aWindowName the name
   * @throws AssertionFailedException in case of error
   */
  public void closeWindow(SecretString aWindowName) throws AssertionFailedException;

  /**
   * Starts a new browser session.<br/>
   * If there are any open sessions (and open windows) currently they are closed.
   * 
   * @param aBrowserType the browser type to start a session for
   */
  public void startNewSession(IBrowser.BrowserType aBrowserType);

  /**
   * Returns the url for the bookmark with the given name.
   * 
   * @param aBookmarkName the name of the bookmark
   * @return the url (including get parameters)
   */
  public URL getBookmark(String aBookmarkName);

  /**
   * Stores the given bookmark.
   * 
   * @param aBookmarkName the name of the bookmark
   * @param aBookmarkUrl the url (including get parameters)
   */
  public void saveBookmark(String aBookmarkName, URL aBookmarkUrl);

  /**
   * Stores the current page as a bookmark with the given name.
   * 
   * @param aBookmarkName the name of the bookmark
   * @throws AssertionFailedException in case of problems
   */
  public void bookmarkPage(String aBookmarkName) throws AssertionFailedException;

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
