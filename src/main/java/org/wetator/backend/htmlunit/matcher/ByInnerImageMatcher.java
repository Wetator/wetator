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


package org.wetator.backend.htmlunit.matcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlImage;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

/**
 * This matcher checks if the given element contains an {@link HtmlImage} and this
 * image's attributes 'alt', 'title', 'src' or 'name' match the criteria.
 *
 * @author frank.danek
 */
public class ByInnerImageMatcher extends AbstractHtmlUnitElementMatcher {

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByInnerImageMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }

  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return Collections.emptyList();
    }

    // has the node the text before
    final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (pathSpot == null || pathSpot.getEndPos() <= tmpNodeSpot.getStartPos()) {
      final List<MatchResult> tmpMatches = new LinkedList<>();

      // now check for the inner image
      final Iterable<HtmlElement> tmpInnerElements = aHtmlElement.getHtmlElementDescendants();
      for (final HtmlElement tmpInnerElement : tmpInnerElements) {
        if (tmpInnerElement instanceof HtmlImage) {
          // does image alt-text match?
          tmpMatches.addAll(new ByInnerImageAltAttributeMatcher(htmlPageIndex, pathSearchPattern, pathSpot,
              searchPattern, (HtmlImage) tmpInnerElement).matches(aHtmlElement));

          // does image title-text match?
          tmpMatches.addAll(new ByInnerImageTitleAttributeMatcher(htmlPageIndex, pathSearchPattern, pathSpot,
              searchPattern, (HtmlImage) tmpInnerElement).matches(aHtmlElement));

          // does image filename match?
          tmpMatches.addAll(new ByInnerImageSrcAttributeMatcher(htmlPageIndex, pathSearchPattern, pathSpot,
              searchPattern, (HtmlImage) tmpInnerElement).matches(aHtmlElement));

          // does image name match?
          tmpMatches.addAll(new ByInnerImageNameMatcher(htmlPageIndex, pathSearchPattern, pathSpot, searchPattern,
              (HtmlImage) tmpInnerElement).matches(aHtmlElement));
        }
      }
      return tmpMatches;
    }

    return Collections.emptyList();
  }

  /**
   * This matcher checks if the attribute 'alt' of the given {@link HtmlImage} matches the criteria.
   *
   * @author frank.danek
   */
  protected static class ByInnerImageAltAttributeMatcher extends AbstractByAttributeMatcher {

    private HtmlImage innerHtmlImage;

    /**
     * The constructor.<br>
     * Creates a new matcher with the given criteria.
     *
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
     *        path given
     * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlImage the inner image element
     */
    public ByInnerImageAltAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlImage anInnerHtmlImage) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_ALT_ATTRIBUTE);
      innerHtmlImage = anInnerHtmlImage;
    }

    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlImage.getAltAttribute();
    }
  }

  /**
   * This matcher checks if the attribute 'src' of the given image {@link HtmlImage} matches the criteria.
   *
   * @author frank.danek
   */
  protected static class ByInnerImageSrcAttributeMatcher extends AbstractByAttributeMatcher {

    private HtmlImage innerHtmlImage;

    /**
     * The constructor.<br>
     * Creates a new matcher with the given criteria.
     *
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
     *        path given
     * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlImage the inner image element
     */
    public ByInnerImageSrcAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlImage anInnerHtmlImage) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_SRC_ATTRIBUTE);
      innerHtmlImage = anInnerHtmlImage;
      matchType = MatchType.ENDS_WITH;
    }

    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlImage.getSrcAttribute();
    }
  }

  /**
   * This matcher checks if the attribute 'title' of the given {@link HtmlImage} matches the criteria.
   *
   * @author frank.danek
   */
  protected static class ByInnerImageTitleAttributeMatcher extends AbstractByAttributeMatcher {

    private HtmlImage innerHtmlImage;

    /**
     * The constructor.<br>
     * Creates a new matcher with the given criteria.
     *
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
     *        path given
     * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlImage the inner image element
     */
    public ByInnerImageTitleAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlImage anInnerHtmlImage) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE);
      innerHtmlImage = anInnerHtmlImage;
    }

    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlImage.getAttribute("title");
    }
  }

  /**
   * This matcher checks if the attribute 'name' of the given {@link HtmlImage} matches the criteria.
   *
   * @author frank.danek
   */
  protected static class ByInnerImageNameMatcher extends AbstractByAttributeMatcher {

    private HtmlImage innerHtmlImage;

    /**
     * The constructor.<br>
     * Creates a new matcher with the given criteria.
     *
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
     *        path given
     * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlImage the inner image element
     */
    public ByInnerImageNameMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlImage anInnerHtmlImage) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_NAME);
      innerHtmlImage = anInnerHtmlImage;
      matchType = MatchType.EXACT;
    }

    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlImage.getAttribute("name");
    }
  }
}
