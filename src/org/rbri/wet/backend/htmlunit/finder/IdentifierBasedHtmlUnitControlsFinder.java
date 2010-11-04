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


package org.rbri.wet.backend.htmlunit.finder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public class IdentifierBasedHtmlUnitControlsFinder extends AbstractHtmlUnitControlsFinder {

  /**
   * The supported identifiers.
   */
  protected List<Class<? extends AbstractHtmlUnitControlIdentifier>> identifiers = new ArrayList<Class<? extends AbstractHtmlUnitControlIdentifier>>();

  private List<Future<?>> futures = new LinkedList<Future<?>>();

  /**
   * The constructor.
   * 
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aThreadPool the thread pool to use for worker threads
   */
  public IdentifierBasedHtmlUnitControlsFinder(HtmlPage aHtmlPage, DomNodeText aDomNodeText,
      ThreadPoolExecutor aThreadPool) {
    super(aHtmlPage, aDomNodeText, aThreadPool);
  }

  /**
   * @param anIdentifier the identifier to add
   */
  public void addIdentifier(Class<? extends AbstractHtmlUnitControlIdentifier> anIdentifier) {
    identifiers.add(anIdentifier);
  }

  /**
   * @param anIdentifierList the list containing the identifiers to add
   */
  public void addIdentifiers(List<Class<? extends AbstractHtmlUnitControlIdentifier>> anIdentifierList) {
    identifiers.addAll(anIdentifierList);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder#find(java.util.List)
   */
  @Override
  public WeightedControlList find(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();
    for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
      for (Class<? extends AbstractHtmlUnitControlIdentifier> tmpIdentifierClass : identifiers) {
        try {
          AbstractHtmlUnitControlIdentifier tmpIdentifier = tmpIdentifierClass.newInstance();
          tmpIdentifier.initializeForAsynch(htmlPage, domNodeText, tmpHtmlElement, aSearch, tmpFoundElements);
          if (tmpIdentifier.isElementSupported(tmpHtmlElement)) {
            execute(tmpIdentifier);
          }
        } catch (IllegalAccessException e) {
          throw new WetException("Could not access identifier class '" + tmpIdentifierClass.getName() + "'.", e);
        } catch (InstantiationException e) {
          throw new WetException("Could not instantiate identifier for class '" + tmpIdentifierClass.getName() + "'.",
              e);
        }
      }
    }
    waitUntilExecuted();
    return tmpFoundElements;
  }

  /**
   * Executes the given identifier asynchronously in the thread pool.
   * 
   * @param anIdentifier the identifier
   */
  protected void execute(AbstractHtmlUnitControlIdentifier anIdentifier) {
    futures.add(threadPool.submit(anIdentifier));
  }

  /**
   * Waits until the execution of all identifiers in the thread pool finished.
   */
  protected void waitUntilExecuted() {
    for (Future<?> tmpFuture : futures) {
      try {
        tmpFuture.get();
      } catch (InterruptedException e) {
        throw new WetException("Exception waiting for executed threads.", e);
      } catch (ExecutionException e) {
        throw new WetException("Exception waiting for executed threads.", e);
      }
    }
  }
}