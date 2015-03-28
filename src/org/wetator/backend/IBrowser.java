/*
 * Copyright (c) 2008-2015 wetator.org
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

import org.wetator.backend.control.IControl;
import org.wetator.core.searchpattern.ContentPattern;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.exception.BackendException;
import org.wetator.util.SecretString;

/**
 * The interface for all browsers.
 * 
 * @author rbri
 * @author frank.danek
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
    /** zip. */
    ZIP,
    /** the rest. */
    OTHER
  };

  /**
   * Enum for the supported browser types.
   */
  public enum BrowserType {
    /** firefox 24 ESR. */
    FIREFOX_24("Firefox24", "Firefox_24"),
    /** firefox 24 ESR. */
    FIREFOX_31("Firefox31", "Firefox_31"),
    /** internet explorer 8. */
    INTERNET_EXPLORER_8("IE8", "IE_8"),
    /** internet explorer 11. */
    INTERNET_EXPLORER_11("IE11", "IE_11");

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
   * Returns the {@link IControlFinder} for this browser.
   * Every supported browser has its own ControlFinder.
   * 
   * @return the ControlFinder for this browser
   * @throws BackendException if no ControlFinder could be found for the current page, e.g. because the page is not a
   *         HTML page
   */
  IControlFinder getControlFinder() throws BackendException;

  /**
   * @return the currently focused {@link IControl} or null if no focus is set
   * @throws BackendException if no control could be created for the currently focused element
   */
  IControl getFocusedControl() throws BackendException;

  /**
   * Opens the given URL in the current window.<br/>
   * Adds failures for JavaScript problems and failing HTTP status codes. All other problems result in exceptions.
   * 
   * @param aUrl the URL to open
   * @throws ActionException if opening the URL fails
   */
  void openUrl(URL aUrl) throws ActionException;

  /**
   * Wait until the 'immediate' JavaScript jobs are finished.
   * 
   * @return true, if still some javascript jobs pending
   * @throws BackendException in case of problems
   */
  boolean waitForImmediateJobs() throws BackendException;

  /**
   * Wait until the 'immediate' JavaScript jobs are finished.
   * 
   * @param aTimeoutInMillis the timeout
   * @return true, if still some javascript jobs pending
   * @throws BackendException in case of problems
   */
  boolean waitForImmediateJobs(long aTimeoutInMillis) throws BackendException;

  /**
   * Checks, if the page title matches the given pattern.<br>
   * If the pattern is not found, this method waits for <code>aTimeoutInSeconds</code> and checks the title again. If
   * the pattern is still not found an {@link AssertionException} is thrown.
   * 
   * @param aTitleToWaitFor the expected text
   * @param aTimeoutInSeconds the timeout in seconds, if less than 1s than 1s is used
   * @return true, if there was a page change during the wait
   * @throws AssertionException if the content was not available
   */
  boolean assertTitleInTimeFrame(ContentPattern aTitleToWaitFor, long aTimeoutInSeconds) throws AssertionException;

  /**
   * Checks, if the page content matches the given pattern.<br>
   * If the pattern is not found, this method waits for <code>aTimeoutInSeconds</code> and checks the content again. If
   * the pattern is still not found an {@link AssertionException} is thrown.
   * 
   * @param aContentToWaitFor the expected content (parts)
   * @param aTimeoutInSeconds the timeout in seconds, if less than 1s than 1s is used
   * @return true, if there was a page change during the wait
   * @throws AssertionException if the content was not available
   */
  boolean assertContentInTimeFrame(ContentPattern aContentToWaitFor, long aTimeoutInSeconds) throws AssertionException;

  /**
   * Saves the content of the current window to the log.
   * 
   * @param aControls the controls to be highlighted
   */
  void saveCurrentWindowToLog(IControl... aControls);

  /**
   * Creates a new log entry that points to the last save screenshot
   * including url parameters pointing to the controls to be highlighted.
   * 
   * @param aControls the controls to be highlighted
   */
  void markControls(final IControl... aControls);

  /**
   * Goes back (simulates the browser's back button) in the current window.
   * 
   * @param aSteps the number of steps to go back
   * @throws ActionException if going back fails
   */
  void goBackInCurrentWindow(int aSteps) throws ActionException;

  /**
   * Closes the window with the given name.
   * 
   * @param aWindowName the name
   * @throws ActionException if finding or closing the window fails
   */
  void closeWindow(SecretString aWindowName) throws ActionException;

  /**
   * Starts a new browser session.<br/>
   * If there are any open sessions (and open windows) currently they are closed.
   * 
   * @param aBrowserType the browser type to start a session for
   */
  void startNewSession(IBrowser.BrowserType aBrowserType);

  /**
   * Closes the current browser session and cleans up all associated resources.<br/>
   */
  void endSession();

  /**
   * Returns the url for the bookmark with the given name.
   * 
   * @param aBookmarkName the name of the bookmark
   * @return the url (including get parameters)
   */
  URL getBookmark(String aBookmarkName);

  /**
   * Stores the given bookmark.
   * 
   * @param aBookmarkName the name of the bookmark
   * @param aBookmarkUrl the url (including get parameters)
   */
  void saveBookmark(String aBookmarkName, URL aBookmarkUrl);

  /**
   * Stores the current page as a bookmark with the given name.
   * 
   * @param aBookmarkName the name of the bookmark
   * @throws ActionException in case of problems
   */
  void bookmarkPage(String aBookmarkName) throws ActionException;

  /**
   * The browser manages a list of failures detected during the execution
   * of an action. This failures are collected. Normally such a failure doesn't stop
   * the processing of the action.<br>
   * 
   * @param aFailure the original problem
   */
  void addFailure(AssertionException aFailure);

  /**
   * Helper to store a failure.
   * 
   * @see #addFailure(AssertionException)
   * @param aMessageKey the key for the message lookup
   * @param aParameterArray the parameters as array
   * @param aCause the original problem
   * @see #addFailure(AssertionException)
   */
  void addFailure(String aMessageKey, Object[] aParameterArray, Throwable aCause);

  /**
   * This logs all collected exceptions and resets the list.
   * 
   * @return the first {@link AssertionException} in the list or null if the list is empty
   */
  AssertionException checkAndResetFailures();
}
