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
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitElementIdentifier implements Runnable {

  protected HtmlPage htmlPage;
  protected DomNodeText domNodeText;
  protected WeightedControlList foundElements;
  private List<SecretString> search;
  private HtmlElement htmlElement;

  public void initialize(HtmlPage aHtmlPage, DomNodeText aDomNodeText, WeightedControlList aFoundElements) {
    htmlPage = aHtmlPage;
    domNodeText = aDomNodeText;
    foundElements = aFoundElements;
  }

  public void initializeForAsynch(HtmlPage aHtmlPage, DomNodeText aDomNodeText, HtmlElement aHtmlElement,
      List<SecretString> aSearch, WeightedControlList aFoundElements) {
    initialize(aHtmlPage, aDomNodeText, aFoundElements);
    htmlElement = aHtmlElement;
    search = aSearch;
  }

  /**
   * @param aHtmlElement the {@link HtmlElement} to check
   * @return true if the given element is supported
   */
  public abstract boolean isElementSupported(HtmlElement aHtmlElement);

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    identify(search, htmlElement);
  }

  public abstract void identify(List<SecretString> aSearch, HtmlElement aHtmlElement);
}
