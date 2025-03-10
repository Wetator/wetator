/*
 * Copyright (c) 2008-2025 wetator.org
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


package org.wetator.backend.htmlunit.control.identifier;

import java.util.List;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlImage;
import org.wetator.backend.WPath;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByAriaLabelAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByDataTestidMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByImageAltAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByImageSrcAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByLabelingTextBeforeAsTextMatcher;
import org.wetator.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByTitleAttributeMatcher;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

/**
 * The identifier for a {@link HtmlUnitImage}.<br>
 * It can be identified by:
 * <ul>
 * <li>the (labeling) text before incl. 'wildcard at end' wpaths</li>
 * <li>its alt attribute</li>
 * <li>its src attribute</li>
 * <li>its title attribute</li>
 * <li>its aria-label attribute</li>
 * <li>its name</li>
 * <li>its id</li>
 * <li>table coordinates</li>
 * </ul>
 *
 * @author frank.danek
 * @author rbri
 */
public class HtmlUnitImageIdentifier extends AbstractMatcherBasedIdentifier {

  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlImage;
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
      aMatchers.add(new ByLabelingTextBeforeAsTextMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
          tmpSearchPattern, aWPath));
      aMatchers.add(new ByImageAltAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByImageSrcAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers
          .add(new ByAriaLabelAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));

      // default
      aMatchers.add(new ByNameAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByDataTestidMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
    } else if (!aWPath.getTableCoordinates().isEmpty()) {
      // table matcher
      // we have to use the reversed table coordinates to work from the inner most (last) to the outer most (first)
      aMatchers.add(new ByTableCoordinatesMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
          aWPath.getTableCoordinatesReversed(), HtmlImage.class));
    }
  }

  @Override
  protected IControl createControl(final MatchResult aMatch) {
    return new HtmlUnitImage((HtmlImage) aMatch.getHtmlElement());
  }
}
