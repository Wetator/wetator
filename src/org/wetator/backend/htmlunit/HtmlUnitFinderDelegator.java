/*
 * Copyright (c) 2008-2020 wetator.org
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wetator.backend.ControlFeature;
import org.wetator.backend.IControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.finder.IdentifierBasedHtmlUnitControlsFinder;
import org.wetator.backend.htmlunit.finder.MouseActionListeningHtmlUnitControlsFinder;
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

  private Map<ControlFeature, AbstractHtmlUnitControlsFinder> finders = new HashMap<>();

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
      tmpThread.setName("Wetator FinderThread " + id++);
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
      throw new IllegalArgumentException("HtmlPage can't be null");
    }
    htmlPageIndex = new HtmlPageIndex(anHtmlPage);

    final ThreadPoolExecutor tmpThreadPool = getThreadPool();

    finders.put(ControlFeature.CLICK, new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool,
        MouseAction.CLICK, aControlRepository));
    finders.put(ControlFeature.CLICK_DOUBLE, new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex,
        tmpThreadPool, MouseAction.CLICK_DOUBLE, aControlRepository));
    finders.put(ControlFeature.CLICK_RIGHT, new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool,
        MouseAction.CLICK_RIGHT, aControlRepository));
    finders.put(ControlFeature.MOUSE_OVER, new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool,
        MouseAction.MOUSE_OVER, aControlRepository));
    finders.put(ControlFeature.SET, new SettableHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool));
    finders.put(ControlFeature.SELECT, new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool));
    finders.put(ControlFeature.DESELECT, new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool));
    finders.put(ControlFeature.DISABLE, new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool));
    finders.put(ControlFeature.FOCUS, new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, tmpThreadPool));

    forTextFinder = new UnknownHtmlUnitControlsFinder(htmlPageIndex, aControlRepository);

    if (aControlRepository != null) {
      finders.entrySet().stream().filter(e -> e.getValue() instanceof IdentifierBasedHtmlUnitControlsFinder)
          .forEach(e -> ((IdentifierBasedHtmlUnitControlsFinder) e.getValue())
              .addIdentifiers(aControlRepository.getIdentifiers(e.getKey())));
    }
  }

  @Override
  public WeightedControlList findControls(final ControlFeature aFeature, final WPath aWPath) {
    return finders.get(aFeature).find(aWPath);
  }

  @Override
  public WeightedControlList getAllControlsForText(final WPath aWPath) {
    return forTextFinder.find(aWPath);
  }
}