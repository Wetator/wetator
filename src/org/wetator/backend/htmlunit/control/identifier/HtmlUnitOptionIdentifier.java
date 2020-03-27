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


package org.wetator.backend.htmlunit.control.identifier;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.WPath;
import org.wetator.backend.WPath.TableCoordinate;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * The identifier for a {@link HtmlUnitOption} nested inside a select.<br>
 * It can be identified by:
 * <ul>
 * <li>its id</li>
 * <li>its text</li>
 * <li>its label attribute</li>
 * </ul>
 * The surrounding select can be identified by:
 * <ul>
 * <li>the labeling text before</li>
 * <li>its name</li>
 * <li>its id</li>
 * <li>a label</li>
 * </ul>
 *
 * @author frank.danek
 */
public class HtmlUnitOptionIdentifier extends AbstractHtmlUnitControlIdentifier {

  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlOption
        || aHtmlElement instanceof HtmlLabel && ((HtmlLabel) aHtmlElement).getReferencedElement() instanceof HtmlSelect;
  }

  @Override
  public WeightedControlList identify(final WPath aWPath, final HtmlElement aHtmlElement) {
    if (aWPath.getLastNode() == null) {
      // this identifier requires at least one node (to identify the option to select)
      // if not available, we can't do anything
      return WeightedControlList.EMPTY_LIST;
    }

    final SearchPattern tmpSearchPatternSelect;
    SearchPattern tmpPathSearchPatternSelect = null;
    FindSpot tmpPathSpotSelect = null;
    if (aWPath.getPathNodes().isEmpty()) {
      tmpSearchPatternSelect = SearchPattern.compile("");
    } else {
      tmpSearchPatternSelect = aWPath.getPathNodes().get(aWPath.getPathNodes().size() - 1).getSearchPattern();
      if (aWPath.getPathNodes().size() > 1) {
        tmpPathSearchPatternSelect = SearchPattern.createFromList(aWPath.getPathNodes(),
            aWPath.getPathNodes().size() - 1);
        tmpPathSpotSelect = htmlPageIndex.firstOccurence(tmpPathSearchPatternSelect);
      }
    }

    // was the path found at all
    if (tmpPathSpotSelect == FindSpot.NOT_FOUND) {
      return WeightedControlList.EMPTY_LIST;
    }

    final WeightedControlList tmpResult = new WeightedControlList();

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();
    if (aHtmlElement instanceof HtmlOption) {
      final HtmlSelect tmpEnclosingSelect = ((HtmlOption) aHtmlElement).getEnclosingSelect();

      // has the node the text before
      final FindSpot tmpNodeSpotSelect = htmlPageIndex.getPosition(tmpEnclosingSelect);
      if (tmpPathSpotSelect == null || tmpPathSpotSelect.getEndPos() <= tmpNodeSpotSelect.getStartPos()) {

        // labeling text before
        int tmpStartPos = 0;
        if (tmpPathSpotSelect != null) {
          tmpStartPos = tmpPathSpotSelect.getEndPos();
        }
        final String tmpLabelingTextBefore = htmlPageIndex.getLabelingTextBefore(tmpEnclosingSelect, tmpStartPos);
        if (StringUtils.isNotEmpty(tmpLabelingTextBefore)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpLabelingTextBefore);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from page start to select
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpEnclosingSelect);
              tmpDistance = tmpTextBefore.length();
            } else {
              // select part -> distance from end of part to select
              tmpDistance = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpLabelingTextBefore);
            }
            // we have to use the reversed table coordinates to work from the inner most (last) to the outer most
            // (first)
            identifyOption(tmpEnclosingSelect, (HtmlOption) aHtmlElement, tmpSearchPattern,
                aWPath.getTableCoordinatesReversed(), tmpDistance, tmpResult);
          }
        }

        // name
        final String tmpName = tmpEnclosingSelect.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from page start to select
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpEnclosingSelect);
              tmpDistance = tmpTextBefore.length();
            } else {
              // name matched select directly -> distance from select to option -> distance 0
              tmpDistance = 0;
            }
            // we have to use the reversed table coordinates to work from the inner most (last) to the outer most
            // (first)
            identifyOption(tmpEnclosingSelect, (HtmlOption) aHtmlElement, tmpSearchPattern,
                aWPath.getTableCoordinatesReversed(), tmpDistance, tmpResult);
          }
        }

        // id
        final String tmpId = tmpEnclosingSelect.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from page start to select
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpEnclosingSelect);
              tmpDistance = tmpTextBefore.length();
            } else {
              // id matched select directly -> distance from select to option -> distance 0
              tmpDistance = 0;
            }
            // we have to use the reversed table coordinates to work from the inner most (last) to the outer most
            // (first)
            identifyOption(tmpEnclosingSelect, (HtmlOption) aHtmlElement, tmpSearchPattern,
                aWPath.getTableCoordinatesReversed(), tmpDistance, tmpResult);
          }
        }
      }

    } else if (aHtmlElement instanceof HtmlLabel) {
      // has the node the text before
      final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
      if (tmpPathSpotSelect == null || tmpPathSpotSelect.getEndPos() <= tmpNodeSpot.getStartPos()) {
        final HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

        // found a label with this text
        final String tmpText = htmlPageIndex.getAsTextWithoutFormControls(tmpLabel);
        final int tmpDeviation = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if (tmpDeviation > -1) {
          final HtmlElement tmpLabeledElement = tmpLabel.getReferencedElement();
          if (tmpLabeledElement != null && tmpLabeledElement instanceof HtmlSelect
              && htmlPageIndex.isVisible(tmpLabeledElement)) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabeledElement);
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from page start to select
              tmpDistance = tmpTextBefore.length();
            } else {
              // select part -> distance from end of part to select
              tmpDistance = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            }
            final HtmlSelect tmpSelect = (HtmlSelect) tmpLabeledElement;
            for (final HtmlOption tmpOption : tmpSelect.getOptions()) {
              // we have to use the reversed table coordinates to work from the inner most (last) to the outer most
              // (first)
              identifyOption(tmpSelect, tmpOption, tmpSearchPattern, aWPath.getTableCoordinatesReversed(), tmpDistance,
                  tmpResult);
            }
          }
        }
      }
    }
    return tmpResult;
  }

  private void identifyOption(final HtmlSelect aSelect, final HtmlOption anOption, final SearchPattern aSearchPattern,
      final List<TableCoordinate> aTableCoordinates, final int aDistance,
      final WeightedControlList aWeightedControlList) {
    final int tmpStart = htmlPageIndex.getPosition(anOption).getStartPos();

    // does the id match?
    final String tmpId = anOption.getId();
    if (StringUtils.isNotEmpty(tmpId) && aSearchPattern.getMinLength() > 0 && aSearchPattern.matches(tmpId)) {
      final int tmpDeviation = aSearchPattern.noOfSurroundingCharsIn(tmpId);
      if (tmpDeviation > -1) {
        final boolean tmpIsInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
            .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

        if (tmpIsInTable) {
          aWeightedControlList.add(new HtmlUnitOption(anOption), WeightedControlList.FoundType.BY_ID, tmpDeviation,
              aDistance, tmpStart, htmlPageIndex.getHierarchy(anOption), htmlPageIndex.getIndex(anOption));
        }
      }
    }

    // does the text match?
    final String tmpText = htmlPageIndex.getAsText(anOption);
    if (tmpText != null) {
      int tmpDeviation = aSearchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpDeviation > -1) {
        if (aSearchPattern.getMinLength() == 0) {
          // as options with an empty text are valid we redefine the deviation here
          tmpDeviation = tmpText.length();
        }

        final boolean tmpIsInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
            .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

        if (tmpIsInTable) {
          aWeightedControlList.add(new HtmlUnitOption(anOption), WeightedControlList.FoundType.BY_LABEL, tmpDeviation,
              aDistance, tmpStart, htmlPageIndex.getHierarchy(anOption), htmlPageIndex.getIndex(anOption));
        }
      }
    }

    // does the label attribute match?
    final String tmpLabel = anOption.getLabelAttribute();
    if (DomElement.ATTRIBUTE_NOT_DEFINED != tmpLabel) {
      int tmpDeviation = aSearchPattern.noOfSurroundingCharsIn(tmpLabel);
      if (tmpDeviation > -1) {
        if (aSearchPattern.getMinLength() == 0) {
          // as options with an empty label attribute are valid we redefine the deviation here
          tmpDeviation = tmpLabel.length();
        }

        final boolean tmpIsInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
            .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

        if (tmpIsInTable) {
          aWeightedControlList.add(new HtmlUnitOption(anOption), WeightedControlList.FoundType.BY_LABEL, tmpDeviation,
              aDistance, tmpStart, htmlPageIndex.getHierarchy(anOption), htmlPageIndex.getIndex(anOption));
        }
      }
    }
  }
}
