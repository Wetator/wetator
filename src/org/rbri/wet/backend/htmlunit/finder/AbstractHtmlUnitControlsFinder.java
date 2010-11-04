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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlsFinder {

  /**
   * The page to work on.
   */
  protected HtmlPage htmlPage;
  /**
   * The DomNodeText index of the page.
   */
  protected DomNodeText domNodeText;
  /**
   * The thread pool to use for worker threads.
   */
  protected ThreadPoolExecutor threadPool;

  /**
   * The constructor.
   * 
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aThreadPool the thread pool to use for worker threads
   */
  public AbstractHtmlUnitControlsFinder(HtmlPage aHtmlPage, DomNodeText aDomNodeText, ThreadPoolExecutor aThreadPool) {
    htmlPage = aHtmlPage;
    domNodeText = aDomNodeText;

    threadPool = aThreadPool;
    if (null == threadPool) {
      // no executor was given, this mainly happens when called from unit tests
      threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      threadPool.prestartAllCoreThreads();
    }
  }

  /**
   * Returns all controls on the page matching the given search.
   * 
   * @param aSearch the search
   * @return the list of matching controls
   */
  public abstract WeightedControlList find(List<SecretString> aSearch);
}