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

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * The identifier for a {@link HtmlUnitOption} nested inside a select.<br>
 * It can be identified by:
 * <ul>
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
public class HtmlUnitOptionInSelectIdentifier extends AbstractHtmlUnitControlIdentifier {

  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return aHtmlElement instanceof HtmlSelect || aHtmlElement instanceof HtmlLabel;
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

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

    final WeightedControlList tmpResult = new WeightedControlList();
    if (aHtmlElement instanceof HtmlSelect) {
      // has the node the text before
      final FindSpot tmpNodeSpotSelect = htmlPageIndex.getPosition(aHtmlElement);
      if (tmpPathSpotSelect == null || tmpPathSpotSelect.getEndPos() <= tmpNodeSpotSelect.getStartPos()) {

        // if the select follows text directly and text matches => choose it
        int tmpStartPos = 0;
        if (tmpPathSpotSelect != null) {
          tmpStartPos = tmpPathSpotSelect.getEndPos();
        }

        // labeling text before
        final String tmpLabelingTextBefore = htmlPageIndex.getLabelTextBefore(aHtmlElement, tmpStartPos);
        if (StringUtils.isNotEmpty(tmpLabelingTextBefore)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpLabelingTextBefore);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from select to page start
              final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
              tmpDistance = tmpTextBefore.length();
            } else {
              // select part -> distance from select to end of part
              tmpDistance = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpLabelingTextBefore);
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance,
                tmpResult);
          }
        }

        // name
        final String tmpName = aHtmlElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from select to page start
              final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
              tmpDistance = tmpTextBefore.length();
            } else {
              // id matched select directly -> distance from option to select -> distance 0
              tmpDistance = 0;
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance,
                tmpResult);
          }
        }

        // id
        final String tmpId = aHtmlElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          final int tmpDeviation = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if (tmpDeviation > -1) {
            final int tmpDistance;
            if (aWPath.getPathNodes().isEmpty()) {
              // no select part -> distance from select to page start
              final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
              tmpDistance = tmpTextBefore.length();
            } else {
              // id matched select directly -> distance from option to select -> distance 0
              tmpDistance = 0;
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance,
                tmpResult);
          }
        }
      }

    } else if (aHtmlElement instanceof HtmlLabel) {
      // has the node the text before
      final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
      final HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      final String tmpText = htmlPageIndex.getAsTextWithoutFormControls(tmpLabel);

      // select
      if (tmpPathSpotSelect == null || tmpPathSpotSelect.getEndPos() <= tmpNodeSpot.getStartPos()) {

        final int tmpDeviation = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if (tmpDeviation > -1) {
          final String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              final HtmlElement tmpElementForLabel = htmlPageIndex.getHtmlElementById(tmpForAttribute);
              if (tmpElementForLabel instanceof HtmlSelect && tmpElementForLabel.isDisplayed()) {
                final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpElementForLabel);
                final int tmpDistance;
                if (aWPath.getPathNodes().isEmpty()) {
                  // no select part -> distance from select to page start
                  tmpDistance = tmpTextBefore.length();
                } else {
                  // select part -> distance from select to end of part
                  tmpDistance = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                }
                getOption((HtmlSelect) tmpElementForLabel, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance,
                    tmpResult);
              }
            } catch (final ElementNotFoundException e) {
              // not found
            }
          }

          // element must be a nested element of label
          final Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (final HtmlElement tmpChildElement : tmpChilds) {
            if (tmpChildElement instanceof HtmlSelect && tmpChildElement.isDisplayed()) {
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpChildElement);
              final int tmpDistance;
              if (aWPath.getPathNodes().isEmpty()) {
                // no select part -> distance from select to page start
                tmpDistance = tmpTextBefore.length();
              } else {
                // select part -> distance from select to end of part
                tmpDistance = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              }
              getOption((HtmlSelect) tmpChildElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance,
                  tmpResult);
            }
          }
        }
      }
    }
    return tmpResult;
  }

  /**
   * Searches for nested option of a given select by label, value or text.
   *
   * @param aSelect {@link HtmlSelect} which should contain this option
   * @param aSearchPattern value or label of option
   * @param aTableCoordinates the table coordinates to check if our option is inside or an empty list
   * @param aDistance the distance of the control
   * @param aWeightedControlList the list to add the control to
   * @return found
   */
  protected boolean getOption(final HtmlSelect aSelect, final SearchPattern aSearchPattern,
      final List<TableCoordinate> aTableCoordinates, final int aDistance,
      final WeightedControlList aWeightedControlList) {
    boolean tmpFound = false;
    final Iterable<HtmlOption> tmpOptions = aSelect.getOptions();
    for (final HtmlOption tmpOption : tmpOptions) {
      final int tmpStart = htmlPageIndex.getPosition(tmpOption).getStartPos();

      // does the text match?
      final String tmpText = htmlPageIndex.getAsText(tmpOption);
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
            aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABELING_TEXT,
                tmpDeviation, aDistance, tmpStart, htmlPageIndex.getIndex(tmpOption));
            tmpFound = true;
          }
        }
      }

      // does the label attribute match?
      final String tmpLabel = tmpOption.getLabelAttribute();
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
            aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABELING_TEXT,
                tmpDeviation, aDistance, tmpStart, htmlPageIndex.getIndex(tmpOption));
            tmpFound = true;
          }
        }
      }
    }
    return tmpFound;
  }
}
