/*
 * Copyright (c) 2008-2011 wetator.org
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

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * The identifier for a {@link HtmlUnitOption} nested inside a select.<br />
 * It can be identified by:
 * <ul>
 * <li>it's text</li>
 * <li>it's label attribute</li>
 * <li>it's value attribute</li>
 * <li>table coordinates</li>
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
public class HtmlUnitOptionInSelectIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return (aHtmlElement instanceof HtmlSelect) || (aHtmlElement instanceof HtmlLabel);
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
      return new WeightedControlList();
    }

    final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

    SearchPattern tmpSearchPatternSelect;
    SearchPattern tmpPathSearchPatternSelect;
    if (aWPath.getPathNodes().isEmpty()) {
      tmpSearchPatternSelect = SearchPattern.compile("");
      tmpPathSearchPatternSelect = SearchPattern.compile("");
    } else {
      tmpSearchPatternSelect = aWPath.getPathNodes().get(aWPath.getPathNodes().size() - 1).getSearchPattern();
      tmpPathSearchPatternSelect = SearchPattern
          .createFromList(aWPath.getPathNodes(), aWPath.getPathNodes().size() - 1);
    }
    final FindSpot tmpPathSpotSelect = htmlPageIndex.firstOccurence(tmpPathSearchPatternSelect);

    if (null == tmpPathSpotSelect) {
      return new WeightedControlList();
    }

    final WeightedControlList tmpResult = new WeightedControlList();
    if (aHtmlElement instanceof HtmlSelect) {
      // has the node the text before
      final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
      if (tmpPathSpotSelect.endPos <= tmpNodeSpot.startPos) {

        // if the select follows text directly and text matches => choose it
        final String tmpText = htmlPageIndex.getLabelTextBefore(aHtmlElement, tmpPathSpotSelect.endPos);
        if (StringUtils.isNotEmpty(tmpText)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpText);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, tmpResult);
          }
        }

        // name
        final String tmpName = aHtmlElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, tmpResult);
          }
        }

        // id
        final String tmpId = aHtmlElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          final int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if (tmpCoverage > -1) {
            final String tmpTextBefore = htmlPageIndex.getTextBefore(aHtmlElement);
            final int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, tmpResult);
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
      if (tmpPathSpotSelect.endPos <= tmpNodeSpot.startPos) {

        final int tmpCoverage = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if (tmpCoverage > -1) {
          final String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              final HtmlElement tmpElementForLabel = htmlPageIndex.getHtmlElementById(tmpForAttribute);
              if (tmpElementForLabel instanceof HtmlSelect) {
                if (tmpElementForLabel.isDisplayed()) {
                  final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
                  final int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                  getOption((HtmlSelect) tmpElementForLabel, tmpSearchPattern, tmpDistance, tmpResult);
                }
              }
            } catch (final ElementNotFoundException e) {
              // not found
            }
          }

          // Element must be a nested element of label
          final Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpChilds) {
            if (tmpChildElement instanceof HtmlSelect) {
              if (tmpChildElement.isDisplayed()) {
                final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
                final int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                getOption((HtmlSelect) tmpChildElement, tmpSearchPattern, tmpDistance, tmpResult);
              }
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
   * @param aSelect HtmlSelect which should contain this option
   * @param aSearchPattern value or label of option
   * @param aDistance the distance of the control
   * @param aWeightedControlList the list to add the control to
   * @return found
   */
  protected boolean getOption(final HtmlSelect aSelect, final SearchPattern aSearchPattern, final int aDistance,
      final WeightedControlList aWeightedControlList) {
    boolean tmpFound = false;
    final Iterable<HtmlOption> tmpOptions = aSelect.getOptions();
    for (HtmlOption tmpOption : tmpOptions) {
      String tmpText = htmlPageIndex.getAsText(tmpOption);
      final int tmpStart = htmlPageIndex.getPosition(tmpOption).startPos;
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance, tmpStart);
          tmpFound = true;
        }
      }

      tmpText = tmpOption.getLabelAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance, tmpStart);
          tmpFound = true;
        }
      }

      tmpText = tmpOption.getValueAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        final int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance, tmpStart);
          tmpFound = true;
        }
      }
    }
    return tmpFound;
  }
}
