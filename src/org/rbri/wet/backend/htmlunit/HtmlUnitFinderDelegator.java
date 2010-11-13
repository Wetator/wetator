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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WPath;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder;
import org.rbri.wet.backend.htmlunit.finder.AllHtmlUnitControlsForTextFinder;
import org.rbri.wet.backend.htmlunit.finder.IdentifierBasedHtmlUnitControlsFinder;
import org.rbri.wet.backend.htmlunit.finder.SettableHtmlUnitControlsFinder;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is the HtmlUnit specific implementation of a {@link ControlFinder}. All requests for
 * {@link org.rbri.wet.backend.control.Control}s are delegated to the specific finder.
 * 
 * @author frank.danek
 */
public class HtmlUnitFinderDelegator implements ControlFinder {

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
  public HtmlUnitFinderDelegator(HtmlPage anHtmlPage) {
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
    forTextFinder = new AllHtmlUnitControlsForTextFinder(htmlPageIndex);
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
   * @see org.rbri.wet.backend.ControlFinder#getAllSettables(WPath)
   */
  @Override
  public WeightedControlList getAllSettables(WPath aWPath) {
    return settablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllClickables(WPath)
   */
  @Override
  public WeightedControlList getAllClickables(WPath aWPath) {
    return clickablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllSelectables(WPath)
   */
  @Override
  public WeightedControlList getAllSelectables(final WPath aWPath) {
    return selectablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllDeselectables(WPath)
   */
  @Override
  public WeightedControlList getAllDeselectables(final WPath aWPath) {
    return deselectablesFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllOtherControls(WPath)
   */
  @Override
  public WeightedControlList getAllOtherControls(final WPath aWPath) {
    return othersFinder.find(aWPath);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllControlsForText(WPath)
   */
  @Override
  public WeightedControlList getAllControlsForText(WPath aWPath) {
    return forTextFinder.find(aWPath);
  }
}