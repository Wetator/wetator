/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.primefaces.backend.htmlunit.control.identifier;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WPath.TableCoordinate;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.primefaces.backend.htmlunit.control.HtmlUnitPrimeFacesOption;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * The identifier for a {@link HtmlUnitOption} nested inside a select.<br />
 * It can be identified by:
 * <ul>
 * <li>it's text</li>
 * <li>it's label attribute</li>
 * <li>it's value attribute</li>
 * </ul>
 * The surrounding select can be identified by:
 * <ul>
 * <li>the label text before</li>
 * <li>it's name</li>
 * <li>it's id</li>
 * <li>a label</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitPrimeFacesOptionInSelectIdentifier extends AbstractHtmlUnitControlIdentifier {

    /** Found by label text match. */
    public static final FoundType BY_LABEL_PF = new FoundType("BY_LABEL_PF", FoundType.BY_LABEL, -1);

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    if (!(aHtmlElement instanceof HtmlSelect)) {
        return false;
    };
    final HtmlSelect tmpSelect = (HtmlSelect) aHtmlElement;

    HtmlElement tmpParent = (HtmlElement) tmpSelect.getParentNode();
    if (tmpParent == null) {
        return false;
    }

    tmpParent = (HtmlElement) tmpSelect.getParentNode();
    if (tmpParent == null) {
        return false;
    }

    String tmpClass = tmpParent.getAttribute("class");
    return tmpClass.equals("ui-selectonemenu")
                || tmpClass.startsWith("ui-selectonemenu ")
                || tmpClass.endsWith(" ui-selectonemenu")
                || tmpClass.contains(" ui-selectonemenu ");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(WPath,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public WeightedControlList identify(final WPath aWPath, final HtmlElement aHtmlElement) {
    if (aWPath.getLastNode() == null) {
      // TODO implement table coordinate support
      return new WeightedControlList();
    }

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

    SearchPattern tmpSearchPatternSelect;
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
      return new WeightedControlList();
    }

    final WeightedControlList tmpResult = new WeightedControlList();
    if (aHtmlElement instanceof HtmlSelect) {
      // has the node the text before
      final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
      if (tmpPathSpotSelect == null || tmpPathSpotSelect.getEndPos() <= tmpNodeSpot.getStartPos()) {

        // if the select follows text directly and text matches => choose it
        int tmpStartPos = 0;
        if (tmpPathSpotSelect != null) {
          tmpStartPos = tmpPathSpotSelect.getEndPos();
        }
        final String tmpText = htmlPageIndex.getLabelTextBefore(aHtmlElement, tmpStartPos);
        if (StringUtils.isNotEmpty(tmpText)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpText);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance;
            if (tmpPathSearchPatternSelect != null) {
              tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance, tmpResult);
          }
        }

        // name
        final String tmpName = aHtmlElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance;
            if (tmpPathSearchPatternSelect != null) {
              tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance, tmpResult);
          }
        }

        // id
        final String tmpId = aHtmlElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance;
            if (tmpPathSearchPatternSelect != null) {
              tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, aWPath.getTableCoordinates(), tmpDistance, tmpResult);
          }
        }
      }
    }
    return tmpResult;
  }

  /**
   * Searches for nested option of a given select by label, value or text.
   *
   * @param aSelect HtmlSelect which should contain this option
   * @param aSearchPattern value or label of option
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
      String tmpText = htmlPageIndex.getAsText(tmpOption);
      final int tmpStart = htmlPageIndex.getPosition(tmpOption).getStartPos();
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          final boolean isInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
              .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

          if (isInTable) {
              aWeightedControlList.add(new HtmlUnitPrimeFacesOption((HtmlDivision) aSelect.getParentNode().getParentNode(), tmpOption), BY_LABEL_PF, tmpCoverage,
                      aDistance, tmpStart, htmlPageIndex.getIndex(tmpOption));
            tmpFound = true;
          }
        }
      }

      tmpText = tmpOption.getLabelAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          final boolean isInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
              .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

          if (isInTable) {
              aWeightedControlList.add(new HtmlUnitPrimeFacesOption((HtmlDivision) aSelect.getParentNode().getParentNode(), tmpOption), BY_LABEL_PF, tmpCoverage,
                      aDistance, tmpStart, htmlPageIndex.getIndex(tmpOption));
            tmpFound = true;
          }
        }
      }

      tmpText = tmpOption.getValueAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          final boolean isInTable = aTableCoordinates.isEmpty() || ByTableCoordinatesMatcher
              .isHtmlElementInTableCoordinates(aSelect, aTableCoordinates, htmlPageIndex, null);

          if (isInTable) {
              aWeightedControlList.add(new HtmlUnitPrimeFacesOption((HtmlDivision) aSelect.getParentNode().getParentNode(), tmpOption), BY_LABEL_PF, tmpCoverage,
                      aDistance, tmpStart, htmlPageIndex.getIndex(tmpOption));
            tmpFound = true;
          }
        }
      }
    }
    return tmpFound;
  }
}
