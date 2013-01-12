/*
 * Copyright (c) 2008-2013 wetator.org
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

import org.wetator.backend.WPath;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByLabelAttributeMatcher;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;

/**
 * The identifier for a {@link HtmlUnitOptionGroup}.<br />
 * It can be identified by:
 * <ul>
 * <li>it's label attribute</li>
 * <li>it's id</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitOptionGroupIdentifier extends AbstractMatcherBasedIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlOptionGroup;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier#addMatchers(org.wetator.backend.WPath,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement, java.util.List)
   */
  @Override
  protected void addMatchers(final WPath aWPath, final HtmlElement aHtmlElement,
      final List<AbstractHtmlUnitElementMatcher> aMatchers) {
    SearchPattern tmpPathSearchPattern = null;
    FindSpot tmpPathSpotSelect = null;
    if (!aWPath.getPathNodes().isEmpty()) {
      tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());

      if (!aWPath.getPathNodes().isEmpty()) {
        final SearchPattern tmpPathSearchPatternSelect = SearchPattern.createFromList(aWPath.getPathNodes(), aWPath
            .getPathNodes().size() - 1);
        tmpPathSpotSelect = htmlPageIndex.firstOccurence(tmpPathSearchPatternSelect);
      }
    }

    if (tmpPathSpotSelect == FindSpot.NOT_FOUND) {
      return;
    }

    if (aWPath.getLastNode() != null) {
      // normal matchers
      final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();
      aMatchers.add(new ByLabelAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpotSelect,
          tmpSearchPattern));
      aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpotSelect, tmpSearchPattern));
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier#createControl(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected IControl createControl(final HtmlElement aHtmlElement) {
    return new HtmlUnitOptionGroup((HtmlOptionGroup) aHtmlElement);
  }
}
