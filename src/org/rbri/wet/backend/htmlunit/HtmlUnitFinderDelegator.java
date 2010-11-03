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


package org.rbri.wet.backend.htmlunit;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.finder.AbstractHtmlUnitElementsFinder;
import org.rbri.wet.backend.htmlunit.finder.AllHtmlElementsForTextFinder;
import org.rbri.wet.backend.htmlunit.finder.IdentifierBasedElementsFinder;
import org.rbri.wet.backend.htmlunit.finder.SettableHtmlElementsFinder;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is the HtmlUnit specific implementation of a {@link ControlFinder}. All requests for controls are delegated to
 * the specific finder.
 * 
 * @author frank.danek
 */
public class HtmlUnitFinderDelegator implements ControlFinder {

  /**
   * the page to work on
   */
  protected HtmlPage htmlPage;
  /**
   * the DomNodeText index of the page
   */
  protected DomNodeText domNodeText;

  private static ThreadPoolExecutor threadPool;

  private IdentifierBasedElementsFinder settablesFinder;
  private IdentifierBasedElementsFinder clickablesFinder;
  private IdentifierBasedElementsFinder selectablesFinder;
  private IdentifierBasedElementsFinder deselectablesFinder;
  private IdentifierBasedElementsFinder othersFinder;
  private AbstractHtmlUnitElementsFinder forTextFinder;

  /**
   * The default constructor.
   * 
   * @param anHtmlPage the page to search in
   */
  public HtmlUnitFinderDelegator(HtmlPage anHtmlPage) {
    if (null == anHtmlPage) {
      throw new NullPointerException("HtmlPage can't be null");
    }
    htmlPage = anHtmlPage;
    domNodeText = new DomNodeText(htmlPage.getBody());

    if (threadPool == null) {
      threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      threadPool.prestartAllCoreThreads();
    }

    settablesFinder = new SettableHtmlElementsFinder(htmlPage, domNodeText, threadPool);
    clickablesFinder = new IdentifierBasedElementsFinder(htmlPage, domNodeText, threadPool);
    selectablesFinder = new IdentifierBasedElementsFinder(htmlPage, domNodeText, threadPool);
    deselectablesFinder = new IdentifierBasedElementsFinder(htmlPage, domNodeText, threadPool);
    othersFinder = new IdentifierBasedElementsFinder(htmlPage, domNodeText, threadPool);
    forTextFinder = new AllHtmlElementsForTextFinder(htmlPage, domNodeText, threadPool);
  }

  /**
   * The constructor.
   * 
   * @param anHtmlPage the page to search in
   * @param aControlRepository the repository of controls this delegator supports
   */
  public HtmlUnitFinderDelegator(HtmlPage anHtmlPage, HtmlUnitControlRepository aControlRepository) {
    this(anHtmlPage);

    if (aControlRepository != null) {
      settablesFinder.addIdentifiers(aControlRepository.getSettableIdentifiers());
      clickablesFinder.addIdentifiers(aControlRepository.getClickableIdentifiers());
      selectablesFinder.addIdentifiers(aControlRepository.getSelectableIdentifiers());
      deselectablesFinder.addIdentifiers(aControlRepository.getDeselectableIdentifiers());
      othersFinder.addIdentifiers(aControlRepository.getOtherIdentifiers());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllSettables(java.util.List)
   */
  @Override
  public WeightedControlList getAllSettables(List<SecretString> aSearch) {
    return settablesFinder.find(aSearch);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllClickables(java.util.List)
   */
  @Override
  public WeightedControlList getAllClickables(List<SecretString> aSearch) {
    return clickablesFinder.find(aSearch);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllSelectables(java.util.List)
   */
  @Override
  public WeightedControlList getAllSelectables(final List<SecretString> aSearch) {
    return selectablesFinder.find(aSearch);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllDeselectables(java.util.List)
   */
  @Override
  public WeightedControlList getAllDeselectables(final List<SecretString> aSearch) {
    return deselectablesFinder.find(aSearch);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllOtherControls(java.util.List)
   */
  @Override
  public WeightedControlList getAllOtherControls(final List<SecretString> aSearch) {
    return othersFinder.find(aSearch);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllElementsForText(java.util.List)
   */
  @Override
  public WeightedControlList getAllElementsForText(List<SecretString> aSearch) {
    return forTextFinder.find(aSearch);
  }

  /**
   * @param <E> a subclass of HtmlElement
   * @param anElementName the name of the element
   * @param anAttributeName the name of the attribute
   * @param anAttributeValue the value of the attribute
   * @return a list containing all found elements
   * @deprecated do not use anymore
   */
  @Deprecated
  public final <E extends HtmlElement> List<E> getElementsByAttribute(String anElementName, String anAttributeName,
      String anAttributeValue) {
    HtmlElement tmpBody = htmlPage.getBody();
    if (null == tmpBody) {
      return null;
    }
    return tmpBody.getElementsByAttribute(anElementName, anAttributeName, anAttributeValue);
  }
}