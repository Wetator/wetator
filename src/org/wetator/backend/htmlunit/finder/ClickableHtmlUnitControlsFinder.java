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


package org.wetator.backend.htmlunit.finder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.ByAriaLabelAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByTextMatcher;
import org.wetator.backend.htmlunit.matcher.ByTitleAttributeMatcher;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * In addition to the {@link IdentifierBasedHtmlUnitControlsFinder} this finder has a support for elements not marked as
 * {@link IClickable clickable} but having a click event listener.
 *
 * @author frank.danek
 */
public class ClickableHtmlUnitControlsFinder extends IdentifierBasedHtmlUnitControlsFinder {

  /**
   * The constructor.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aThreadPool the thread pool to use for worker threads; may be null
   */
  public ClickableHtmlUnitControlsFinder(final HtmlPageIndex aHtmlPageIndex, final ThreadPoolExecutor aThreadPool) {
    super(aHtmlPageIndex, aThreadPool);
  }

  @Override
  protected boolean identify(final HtmlElement aHtmlElement, final WPath aWPath,
      final WeightedControlList aFoundControls) {
    final boolean tmpSupported = super.identify(aHtmlElement, aWPath, aFoundControls);

    if (!tmpSupported) {
      final AbstractHtmlUnitControlIdentifier tmpIdentifier = new HtmlUnitClickListeningControlIdentifier();
      tmpIdentifier.initializeForAsynch(htmlPageIndex, aHtmlElement, aWPath, aFoundControls);
      if (tmpIdentifier.isHtmlElementSupported(aHtmlElement)) {
        execute(tmpIdentifier);
        return true;
      }
    }
    return tmpSupported;
  }

  /**
   * Special identifier supporting every HTML element having a click event listener attached.<br>
   * Handles every HTML element as {@link HtmlUnitUnspecificControl}s.<br>
   * The HTML element can be identified by:
   * <ul>
   * <li>its text</li>
   * <li>its title attribute</li>
   * <li>its aria-label attribute</li>
   * <li>its id</li>
   * <li>table coordinates</li>
   * </ul>
   *
   * @author frank.danek
   */
  static class HtmlUnitClickListeningControlIdentifier extends AbstractMatcherBasedIdentifier {

    @Override
    public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
      return htmlPageIndex.hasClickListener(aHtmlElement);
    }

    @Override
    protected void addMatchers(final WPath aWPath, final HtmlElement aHtmlElement,
        final List<AbstractHtmlUnitElementMatcher> aMatchers) {
      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!aWPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
        tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      if (tmpPathSpot == FindSpot.NOT_FOUND) {
        return;
      }

      if (aWPath.getLastNode() != null) {
        // normal matchers
        final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

        // element specific
        aMatchers.add(new ByTextMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers
            .add(new ByAriaLabelAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));

        // default
        aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      } else if (!aWPath.getTableCoordinates().isEmpty()) {
        // table matcher
        // we have to use the reversed table coordinates to work from the inner most (last) to the outer most (first)
        aMatchers.add(new ByTableCoordinatesMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            aWPath.getTableCoordinatesReversed(), HtmlElement.class));
      }
    }

    @Override
    protected IControl createControl(final HtmlElement aHtmlElement) {
      return new HtmlUnitUnspecificControl<HtmlElement>(aHtmlElement);
    }
  }
}
