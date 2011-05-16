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


package org.wetator.backend.htmlunit;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.finder.IdentifierBasedHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.finder.SettableHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.finder.UnknownHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is the HtmlUnit specific implementation of a {@link IControlFinder}. All requests for
 * {@link org.wetator.backend.control.Control}s are delegated to the specific finder.
 * 
 * @author frank.danek
 */
public class HtmlUnitFinderDelegator implements IControlFinder {

  /**
   * The index of the page.
   */
  protected HtmlPageIndex htmlPageIndex;

  private static ThreadPoolExecutor threadPool;

  private IdentifierBasedHtmlUnitControlsFinder settablesFinder;
  private IdentifierBasedHtmlUnitControlsFinder clickablesFinder;
  private IdentifierBasedHtmlUnitControlsFinder selectablesFinder;
  private IdentifierBasedHtmlUnitControlsFinder deselectablesFinder;
  private IdentifierBasedHtmlUnitControlsFinder othersFinder;
  private AbstractHtmlUnitControlsFinder forTextFinder;

  /**
   * The default constructor.
   * 
   * @param anHtmlPage the page to search in
   */
  public HtmlUnitFinderDelegator(final HtmlPage anHtmlPage) {
    this(anHtmlPage, null);
  }

  /**
   * The constructor.
   * 
   * @param anHtmlPage the page to search in
   * @param aControlRepository the repository of controls this delegator supports
   */
  public HtmlUnitFinderDelegator(final HtmlPage anHtmlPage, final HtmlUnitControlRepository aControlRepository) {
    if (null == anHtmlPage) {
      throw new NullPointerException("HtmlPage can't be null");
    }
    htmlPageIndex = new HtmlPageIndex(anHtmlPage);

    if (threadPool == null) {
      threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      threadPool.prestartAllCoreThreads();
    }

    settablesFinder = new SettableHtmlUnitControlsFinder(htmlPageIndex, threadPool);
    clickablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, threadPool);
    selectablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, threadPool);
    deselectablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, threadPool);
    othersFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, threadPool);
    forTextFinder = new UnknownHtmlUnitControlsFinder(htmlPageIndex, aControlRepository);

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
   * @see org.wetator.backend.IControlFinder#getAllSettables(WPath)
   */
  @Override
  public WeightedControlList getAllSettables(final WPath aWPath) {
    return settablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IControlFinder#getAllClickables(WPath)
   */
  @Override
  public WeightedControlList getAllClickables(final WPath aWPath) {
    return clickablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IControlFinder#getAllSelectables(WPath)
   */
  @Override
  public WeightedControlList getAllSelectables(final WPath aWPath) {
    return selectablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IControlFinder#getAllDeselectables(WPath)
   */
  @Override
  public WeightedControlList getAllDeselectables(final WPath aWPath) {
    return deselectablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IControlFinder#getAllOtherControls(WPath)
   */
  @Override
  public WeightedControlList getAllOtherControls(final WPath aWPath) {
    return othersFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.IControlFinder#getAllControlsForText(WPath)
   */
  @Override
  public WeightedControlList getAllControlsForText(final WPath aWPath) {
    return forTextFinder.find(aWPath);
  }
}