/*
 * Copyright (c) 2008-2021 wetator.org
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
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByAriaLabelAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByTextMatcher;
import org.wetator.backend.htmlunit.matcher.ByTitleAttributeMatcher;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * In addition to the {@link IdentifierBasedHtmlUnitControlsFinder} this finder has a support for elements not marked as
 * {@link IClickable clickable} but having an event listener for a {@link MouseAction}.
 *
 * @author frank.danek
 */
public class MouseActionListeningHtmlUnitControlsFinder extends IdentifierBasedHtmlUnitControlsFinder {

  private static final String PAGE_WPATH = "$PAGE";

  private MouseAction mouseAction;
  private boolean supportUnknownControlsWithoutListener = true;
  private HtmlUnitControlRepository controlRepository;

  /**
   * The constructor.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aThreadPool the thread pool to use for worker threads; may be <code>null</code>
   * @param aMouseAction the {@link MouseAction} to check for
   * @param aControlRepository the {@link HtmlUnitControlRepository} to use; may be <code>null</code>
   */
  public MouseActionListeningHtmlUnitControlsFinder(final HtmlPageIndex aHtmlPageIndex,
      final ThreadPoolExecutor aThreadPool, final MouseAction aMouseAction,
      final HtmlUnitControlRepository aControlRepository) {
    super(aHtmlPageIndex, aThreadPool);

    mouseAction = aMouseAction;
    controlRepository = aControlRepository;
  }

  @Override
  public WeightedControlList find(final WPath aWPath) {
    // check for the $PAGE pseudo wpath for finding the body element
    if (aWPath.getPathNodes().isEmpty() && aWPath.getTableCoordinates().isEmpty() && aWPath.getLastNode() != null
        && PAGE_WPATH.equals(aWPath.getLastNode().getValue())) {
      for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
        if (tmpHtmlElement instanceof HtmlBody) {
          final WeightedControlList tmpFoundControls = new WeightedControlList();
          tmpFoundControls.add(new HtmlUnitUnspecificControl<>((HtmlBody) tmpHtmlElement),
              WeightedControlList.FoundType.BY_ID, // by (pseudo) id
              0, // no deviation
              0, // no distance from page start
              htmlPageIndex.getPosition(tmpHtmlElement).getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
              htmlPageIndex.getIndex(tmpHtmlElement));
          return tmpFoundControls;
        }
      }
    }

    // do the normal stuff
    final WeightedControlList tmpFoundControls = super.find(aWPath);

    // FIXME [UNKNOWN] search for controls with matching text for click and mouse over
    // see pretty complex UnknownHtmlUnitControlsFinder
    if (supportUnknownControlsWithoutListener) {
      // as a last attempt we try to find the controls just by the text to support the 'you can click on anything' idea
      // note: another part of this approach is placed at the end of identify()
      if (aWPath.getLastNode() == null && aWPath.getTableCoordinates().isEmpty()) {
        // we do not support this unspecific paths
        return tmpFoundControls;
      }

      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!aWPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
        tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      if (tmpPathSpot == FindSpot.NOT_FOUND) {
        return tmpFoundControls;
      }

      if (aWPath.getLastNode() == null || aWPath.getLastNode().isEmpty()) {
        // wpath ends with empty node or contains only table coordinates
        // => empty last node: we search for the first element after the given path
        // => only table coordinates: we search for the first element inside the given coordinates
        int tmpStartPos = 0;
        if (tmpPathSpot != null) {
          tmpStartPos = Math.max(0, tmpPathSpot.getEndPos());
        }

        for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
          final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
          if (tmpStartPos <= tmpNodeSpot.getStartPos()
              && (controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null)
              && (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
                  tmpHtmlElement, aWPath.getTableCoordinatesReversed(), htmlPageIndex, null))) {

            final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpHtmlElement);
            final int tmpDeviation = htmlPageIndex.getAsText(tmpHtmlElement).length();

            final int tmpDistance;
            if (tmpPathSearchPattern != null) {
              tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }

            tmpFoundControls.add(new HtmlUnitUnspecificControl<>(tmpHtmlElement), FoundType.BY_TEXT, tmpDeviation,
                tmpDistance, tmpNodeSpot.getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
                htmlPageIndex.getIndex(tmpHtmlElement));

            break;
          }
        }
        return tmpFoundControls;
      }

      final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

      // search by text
      int tmpStartPos = 0;
      if (tmpPathSpot != null) {
        tmpStartPos = Math.max(0, tmpPathSpot.getEndPos());
      }
      FindSpot tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpStartPos);
      while (tmpHitSpot != FindSpot.NOT_FOUND && tmpHitSpot.getEndPos() > -1) {
        // found a hit

        // find the first element that surrounds this
        for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElementsBottomUp()) {
          final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(tmpHtmlElement);
          if (tmpNodeSpot.getStartPos() <= tmpHitSpot.getStartPos()
              && tmpHitSpot.getEndPos() <= tmpNodeSpot.getEndPos()) {
            // found one
            if ((controlRepository == null || controlRepository.getForHtmlElement(tmpHtmlElement) == null)
                && (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
                    tmpHtmlElement, aWPath.getTableCoordinatesReversed(), htmlPageIndex, null))) {

              String tmpTextBefore = htmlPageIndex.getTextBeforeIncludingMyself(tmpHtmlElement);
              final FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
              final int tmpDeviation = tmpTextBefore.length() - tmpLastOccurence.getEndPos();

              tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.getStartPos());
              final int tmpDistance;
              if (tmpPathSearchPattern != null) {
                tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
              } else {
                tmpDistance = tmpTextBefore.length();
              }

              tmpFoundControls.add(new HtmlUnitUnspecificControl<>(tmpHtmlElement), FoundType.BY_TEXT, tmpDeviation,
                  tmpDistance, tmpNodeSpot.getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
                  htmlPageIndex.getIndex(tmpHtmlElement));
            }
            break;
          }
        }

        tmpHitSpot = htmlPageIndex.firstOccurence(tmpSearchPattern, tmpHitSpot.getStartPos() + 1);
      }
    }
    return tmpFoundControls;
  }

  @Override
  protected boolean identify(final HtmlElement aHtmlElement, final WPath aWPath,
      final WeightedControlList aFoundControls) {
    boolean tmpSupported = super.identify(aHtmlElement, aWPath, aFoundControls);

    if (!tmpSupported) {
      // ok, not identified the 'normal' way -> let's look for controls having a matching action listener
      if (htmlPageIndex.hasMouseActionListener(mouseAction, aHtmlElement)) {
        // first try the known controls
        if (controlRepository != null) {
          final Class<? extends HtmlUnitBaseControl<?>> tmpControlClass = controlRepository
              .getForHtmlElement(aHtmlElement);
          if (tmpControlClass != null) {
            final IdentifiedBy tmpIdentifiedBy = tmpControlClass.getAnnotation(IdentifiedBy.class);

            for (final Class<? extends AbstractHtmlUnitControlIdentifier> tmpIdentifierClass : tmpIdentifiedBy
                .value()) {
              tmpSupported |= identify(tmpIdentifierClass, aHtmlElement, aWPath, aFoundControls);
            }
          }
        }

        // then try the unknown controls
        if (!tmpSupported) {
          tmpSupported |= identify(HtmlUnitUnspecificControlIdentifier.class, aHtmlElement, aWPath, aFoundControls);
        }
      }

      // ... and for a label we also have to check it's labeled element for action listeners
      if (aHtmlElement instanceof HtmlLabel && controlRepository != null) {
        final HtmlLabel tmpHtmlLabel = (HtmlLabel) aHtmlElement;
        final HtmlElement tmpLabeledElement = tmpHtmlLabel.getLabeledElement();

        if (htmlPageIndex.hasMouseActionListener(mouseAction, tmpLabeledElement)) {
          final Class<? extends HtmlUnitBaseControl<?>> tmpControlClass = controlRepository
              .getForHtmlElement(tmpLabeledElement);
          if (tmpControlClass != null) {
            final IdentifiedBy tmpIdentifiedBy = tmpControlClass.getAnnotation(IdentifiedBy.class);

            for (final Class<? extends AbstractHtmlUnitControlIdentifier> tmpIdentifierClass : tmpIdentifiedBy
                .value()) {
              tmpSupported |= identify(tmpIdentifierClass, aHtmlElement, aWPath, aFoundControls);
            }
          }
        }
      }
    }

    // FIXME [UNKNOWN] add unknown controls without action listener for click and mouse over
    // see pretty complex UnknownHtmlUnitControlsFinder
    if (!tmpSupported && supportUnknownControlsWithoutListener && aWPath.getLastNode() != null
        && !aWPath.getLastNode().isEmpty()
        && (controlRepository == null || controlRepository.getForHtmlElement(aHtmlElement) == null)) {
      // ok, still not identified (neither the normal way nor via action listener) -> give it a last try just by id or
      // title (as some kind of visible text) to support the 'you can click on anything' idea
      // note: the 'by text' part is covered within find()!
      // we only support specific paths in this case
      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!aWPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
        tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      if (tmpPathSpot != FindSpot.NOT_FOUND) {
        final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

        // search by id / title
        final ByIdMatcher tmpIdMatcher = new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            tmpSearchPattern);
        final ByTitleAttributeMatcher tmpTitleMatcher = new ByTitleAttributeMatcher(htmlPageIndex, tmpPathSearchPattern,
            tmpPathSpot, tmpSearchPattern);
        // id
        List<MatchResult> tmpMatches = tmpIdMatcher.matches(aHtmlElement);
        for (final MatchResult tmpMatch : tmpMatches) {
          if (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
              tmpMatch.getHtmlElement(), aWPath.getTableCoordinatesReversed(), htmlPageIndex, tmpPathSpot)) {
            aFoundControls.add(new HtmlUnitUnspecificControl<>(tmpMatch.getHtmlElement()), tmpMatch.getFoundType(),
                tmpMatch.getDeviation(), tmpMatch.getDistance(), tmpMatch.getStart(),
                htmlPageIndex.getHierarchy(tmpMatch.getHtmlElement()),
                htmlPageIndex.getIndex(tmpMatch.getHtmlElement()));
          }
        }

        // title
        tmpMatches = tmpTitleMatcher.matches(aHtmlElement);
        for (final MatchResult tmpMatch : tmpMatches) {
          if (aWPath.getTableCoordinates().isEmpty() || ByTableCoordinatesMatcher.isHtmlElementInTableCoordinates(
              tmpMatch.getHtmlElement(), aWPath.getTableCoordinatesReversed(), htmlPageIndex, null)) {
            aFoundControls.add(new HtmlUnitUnspecificControl<>(tmpMatch.getHtmlElement()), FoundType.BY_TITLE_TEXT,
                tmpMatch.getDeviation(), tmpMatch.getDistance(), tmpMatch.getStart(),
                htmlPageIndex.getHierarchy(tmpMatch.getHtmlElement()),
                htmlPageIndex.getIndex(tmpMatch.getHtmlElement()));
          }
        }
      }
    }

    return tmpSupported;
  }

  /**
   * @param aSupportUnknownControlsWithoutListener <code>true</code> if unknown controls without action listeners
   *        should be supported as well
   */
  public void setSupportUnknownControlsWithoutListener(final boolean aSupportUnknownControlsWithoutListener) {
    supportUnknownControlsWithoutListener = aSupportUnknownControlsWithoutListener;
  }

  /**
   * Special identifier supporting every HTML element.<br>
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
  static class HtmlUnitUnspecificControlIdentifier extends AbstractMatcherBasedIdentifier {

    @Override
    public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
      // the ControlsFinder already checked that the element has the right action listener
      return true;
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
    protected IControl createControl(final MatchResult aMatch) {
      return new HtmlUnitUnspecificControl<>(aMatch.getHtmlElement());
    }
  }
}
