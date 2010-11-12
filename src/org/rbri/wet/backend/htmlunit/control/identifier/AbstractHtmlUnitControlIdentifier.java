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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.util.List;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * The base class for all identifiers.<br />
 * An identifier can be used to identify a {@link HtmlElement} as a {@link org.rbri.wet.backend.control.Control}
 * matching a specific search. To check, if a {@link HtmlElement} is supported by an identifier at all, use
 * {@link #isHtmlElementSupported(HtmlElement)}.
 * 
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifier implements Runnable {

  /**
   * The HtmlPageIndex index of the page.
   */
  protected HtmlPageIndex htmlPageIndex;
  /**
   * The list the found controls should be added to.
   */
  private WeightedControlList foundControls;

  // for asynchronous use
  private boolean initializedForAsynch;
  private List<SecretString> search;
  private HtmlElement htmlElement;

  /**
   * Initializes the identifier.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page
   */
  public void initialize(HtmlPageIndex aHtmlPageIndex) {
    htmlPageIndex = aHtmlPageIndex;
  }

  /**
   * Initializes the identifier to work asynchronously.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page
   * @param aHtmlElement the {@link HtmlElement} to be identified
   * @param aSearch the search used to identify the control
   * @param aFoundControls the list the found controls should be added to
   */
  public void initializeForAsynch(HtmlPageIndex aHtmlPageIndex, HtmlElement aHtmlElement, List<SecretString> aSearch,
      WeightedControlList aFoundControls) {
    initialize(aHtmlPageIndex);
    htmlElement = aHtmlElement;
    search = aSearch;
    foundControls = aFoundControls;
    initializedForAsynch = true;
  }

  /**
   * @param aHtmlElement the {@link HtmlElement} to check
   * @return true if the given {@link HtmlElement} is supported
   */
  public abstract boolean isHtmlElementSupported(HtmlElement aHtmlElement);

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    if (!initializedForAsynch) {
      throw new WetException(getClass().getName()
          + " is not initialized to work asynchronously. Use initializeForAsynch().");
    }
    WeightedControlList tmpResult = identify(search, htmlElement);
    if (tmpResult != null) {
      foundControls.addAll(tmpResult);
    }
  }

  /**
   * Tries to identify the given {@link HtmlElement} with the given search.
   * 
   * @param aSearch the search used to identify the control
   * @param aHtmlElement the {@link HtmlElement} to be identified
   * @return the list containing the identified controls
   */
  public abstract WeightedControlList identify(List<SecretString> aSearch, HtmlElement aHtmlElement);
}
