/*
 * Copyright (c) 2008-2011 www.wetator.org
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
import org.wetator.backend.control.Control;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByImageAltAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByImageSrcAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByImageTitleAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;

/**
 * The identifier for a {@link HtmlUnitInputImage}.<br />
 * It can be identified by:
 * <ul>
 * <li>it's alt attribute</li>
 * <li>it's title attribute</li>
 * <li>it's src attribute</li>
 * <li>it's name</li>
 * <li>it's id</li>
 * <li>table coordinates</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitInputImageIdentifier extends AbstractMatcherBasedIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlImageInput;
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
    final SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
    final FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);

    if (null == tmpPathSpot) {
      return;
    }

    if (aWPath.getLastNode() != null) {
      // normal matchers
      final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();
      // does image alt-text match?
      aMatchers.add(new ByImageAltAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      // does image filename match?
      aMatchers.add(new ByImageSrcAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      // does image title-text match?
      aMatchers
          .add(new ByImageTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));

      aMatchers.add(new ByNameAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
    } else if (!aWPath.getTableCoordinates().isEmpty()) {
      // table matcher
      // we have to use the reversed table coordinates to work from the inner most (last) to the outer most (first)
      aMatchers.add(new ByTableCoordinatesMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, aWPath
          .getTableCoordinatesReversed(), HtmlImageInput.class));
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier#createControl(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected Control createControl(final HtmlElement aHtmlElement) {
    return new HtmlUnitInputImage((HtmlImageInput) aHtmlElement);
  }
}
