/*
 * Copyright (c) 2008-2012 wetator.org
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * The base class for all identifiers using {@link AbstractHtmlUnitElementMatcher} to identify a {@link IControl}.<br />
 * Implement {@link #addMatchers(WPath, HtmlElement, List)} to add the matcher to use and
 * {@link #createControl(HtmlElement)} to create a {@link IControl} for an {@link HtmlElement}.
 * 
 * @author frank.danek
 */
public abstract class AbstractMatcherBasedIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(WPath, HtmlElement)
   */
  @Override
  public WeightedControlList identify(final WPath aWPath, final HtmlElement aHtmlElement) {
    final List<AbstractHtmlUnitElementMatcher> tmpMatchers = new ArrayList<AbstractHtmlUnitElementMatcher>();
    addMatchers(aWPath, aHtmlElement, tmpMatchers);
    if (tmpMatchers.isEmpty()) {
      return new WeightedControlList();
    }

    final List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    for (AbstractHtmlUnitElementMatcher tmpMatcher : tmpMatchers) {
      tmpMatches.addAll(tmpMatcher.matches(aHtmlElement));
    }

    final List<MatchResult> tmpProcessedMatches = new ArrayList<MatchResult>();
    if (aWPath.getTableCoordinates().isEmpty() || aWPath.getLastNode() == null) {
      // we do not have any table coordinates for post processing or they were used
      // by the matcher so we can just return the result
      tmpProcessedMatches.addAll(tmpMatches);
    } else {
      // we have some table coordinates and they were not used by the matcher so we have to
      // check if our matches are inside the table coordinates
      final SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
      final FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);

      for (MatchResult tmpMatchResult : tmpMatches) {
        final HtmlElement tmpFoundHtmlElement = tmpMatchResult.getHtmlElement();

        if (ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(tmpFoundHtmlElement,
            aWPath.getTableCoordinatesReversed(), htmlPageIndex, tmpPathSpot)) {
          tmpProcessedMatches.add(tmpMatchResult);
        }
      }
    }

    final WeightedControlList tmpResult = new WeightedControlList();
    for (MatchResult tmpMatch : tmpProcessedMatches) {
      tmpResult.add(createControl(tmpMatch.getHtmlElement()), tmpMatch.getFoundType(), tmpMatch.getCoverage(),
          tmpMatch.getDistance(), htmlPageIndex.getPosition(tmpMatch.getHtmlElement()).startPos);
    }
    return tmpResult;
  }

  /**
   * @param aWPath the wpath used to identify the controls
   * @param aHtmlElement the {@link HtmlElement} to be identified
   * @param aMatchers the list the matcher should be added to
   */
  protected abstract void addMatchers(WPath aWPath, HtmlElement aHtmlElement,
      List<AbstractHtmlUnitElementMatcher> aMatchers);

  /**
   * @param aHtmlElement the {@link HtmlElement} to create the control for
   * @return the created control
   */
  protected abstract IControl createControl(HtmlElement aHtmlElement);
}
