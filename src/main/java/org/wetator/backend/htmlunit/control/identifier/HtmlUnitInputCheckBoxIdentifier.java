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

import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlLabel;
import org.wetator.backend.WPath;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByDataTestidMatcher;
import org.wetator.backend.htmlunit.matcher.ByHtmlLabelMatcher;
import org.wetator.backend.htmlunit.matcher.ByHtmlLabelMatcher.ByHtmlLabelMatchResult;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByLabelingTextAfterMatcher;
import org.wetator.backend.htmlunit.matcher.ByLabelingTextBeforeAsTextMatcher;
import org.wetator.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByTitleAttributeMatcher;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

/**
 * The identifier for a {@link HtmlUnitInputCheckBox}.<br>
 * It can be identified by:
 * <ul>
 * <li>the (labeling) text before incl. 'wildcard at end' wpaths</li>
 * <li>the labeling text after</li>
 * <li>its title attribute</li>
 * <li>its name</li>
 * <li>its id</li>
 * <li>a label</li>
 * <li>table coordinates</li>
 * </ul>
 *
 * @author frank.danek
 * @author rbri
 */
public class HtmlUnitInputCheckBoxIdentifier extends AbstractMatcherBasedIdentifier {

  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlCheckBoxInput || aHtmlElement instanceof HtmlLabel
        && ((HtmlLabel) aHtmlElement).getLabeledElement() instanceof HtmlCheckBoxInput;
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
      if (aHtmlElement instanceof HtmlCheckBoxInput) {
        // element specific
        aMatchers.add(new ByLabelingTextBeforeAsTextMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            tmpSearchPattern, aWPath));
        aMatchers
            .add(new ByLabelingTextAfterMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));

        // default
        aMatchers.add(new ByNameAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByDataTestidMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      } else if (aHtmlElement instanceof HtmlLabel) {
        // label
        aMatchers.add(new ByHtmlLabelMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
            HtmlCheckBoxInput.class, true));
      }
    } else if (!aWPath.getTableCoordinates().isEmpty()) {
      // table matcher
      // we have to use the reversed table coordinates to work from the inner most (last) to the outer most (first)
      aMatchers.add(new ByTableCoordinatesMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
          aWPath.getTableCoordinatesReversed(), HtmlCheckBoxInput.class));
    }
  }

  @Override
  protected IControl createControl(final MatchResult aMatch) {
    final HtmlUnitInputCheckBox tmpCheckBox = new HtmlUnitInputCheckBox((HtmlCheckBoxInput) aMatch.getHtmlElement());
    if (aMatch instanceof ByHtmlLabelMatchResult && !htmlPageIndex.isVisible(aMatch.getHtmlElement())) {
      // we support finding an invisible control by label
      // in that case we pass this label to the control so we might use it later
      tmpCheckBox.setHtmlLabel(((ByHtmlLabelMatchResult) aMatch).getLabel());
    }
    return tmpCheckBox;
  }
}
