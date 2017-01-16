/*
 * Copyright (c) 2008-2017 wetator.org
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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 * {@link org.wetator.backend.control.IControl}s are delegated to the specific finder.
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
   * Our simple impl of a ThreadFactory (decorator) to be able to name
   * our threads.
   */
  private static final class ThreadNamingFactory implements ThreadFactory {
    private static int id = 1;
    private ThreadFactory baseFactory;

    private ThreadNamingFactory(final ThreadFactory aBaseFactory) {
      baseFactory = aBaseFactory;
    }

    @Override
    public Thread newThread(final Runnable aRunnable) {
      final Thread tmpThread = baseFactory.newThread(aRunnable);
      tmpThread.setName("WETATOR FinderThread " + id++);
      return tmpThread;
    }
  }

  private static synchronized ThreadPoolExecutor getThreadPool() {
    if (threadPool == null) {
      final ThreadPoolExecutor tmpThreadPool = (ThreadPoolExecutor) Executors
          .newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      tmpThreadPool.setThreadFactory(new ThreadNamingFactory(tmpThreadPool.getThreadFactory()));
      tmpThreadPool.prestartAllCoreThreads();
      threadPool = tmpThreadPool;
    }
    return threadPool;
  }

  /**
   * Shutdown the Thread pool.
   *
   * @throws InterruptedException in case of error
   */
  public static synchronized void shutdownThreadPool() throws InterruptedException {
    if (threadPool != null) {
      threadPool.shutdown();
      threadPool.awaitTermination(1, TimeUnit.SECONDS);
      threadPool = null;
    }
  }

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

    final ThreadPoolExecutor tmpThreadPool = getThreadPool();

    settablesFinder = new SettableHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool);
    clickablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool);
    selectablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool);
    deselectablesFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool);
    othersFinder = new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool);
    forTextFinder = new UnknownHtmlUnitControlsFinder(htmlPageIndex, aControlRepository);

    if (aControlRepository != null) {
      settablesFinder.addIdentifiers(aControlRepository.getSettableIdentifiers());
      clickablesFinder.addIdentifiers(aControlRepository.getClickableIdentifiers());
      selectablesFinder.addIdentifiers(aControlRepository.getSelectableIdentifiers());
      deselectablesFinder.addIdentifiers(aControlRepository.getDeselectableIdentifiers());
      othersFinder.addIdentifiers(aControlRepository.getOtherIdentifiers());
    }
  }

  @Override
  public WeightedControlList getAllSettables(final WPath aWPath) {
    return settablesFinder.find(aWPath);
  }

  @Override
  public WeightedControlList getAllClickables(final WPath aWPath) {
    return clickablesFinder.find(aWPath);
  }

  @Override
  public WeightedControlList getAllSelectables(final WPath aWPath) {
    return selectablesFinder.find(aWPath);
  }

  @Override
  public WeightedControlList getAllDeselectables(final WPath aWPath) {
    return deselectablesFinder.find(aWPath);
  }

  @Override
  public WeightedControlList getAllOtherControls(final WPath aWPath) {
    return othersFinder.find(aWPath);
  }

  @Override
  public WeightedControlList getAllControlsForText(final WPath aWPath) {
    return forTextFinder.find(aWPath);
  }
}