/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitOption;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public class HtmlUnitOptionInSelectIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isElementSupported(HtmlElement aHtmlElement) {
    return (aHtmlElement instanceof HtmlSelect) || (aHtmlElement instanceof HtmlLabel);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(java.util.List,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public void identify(List<SecretString> aSearch, HtmlElement aHtmlElement) {
    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();

    SearchPattern tmpSearchPatternSelect;
    SearchPattern tmpPathSearchPatternSelect;
    if (aSearch.size() <= 1) {
      tmpSearchPatternSelect = SearchPattern.compile("");
      tmpPathSearchPatternSelect = SearchPattern.compile("");
    } else {
      tmpSearchPatternSelect = aSearch.get(aSearch.size() - 2).getSearchPattern();
      tmpPathSearchPatternSelect = SearchPattern.createFromList(aSearch, aSearch.size() - 2);
    }
    FindSpot tmpPathSpotSelect = domNodeText.firstOccurence(tmpPathSearchPatternSelect);

    if (null == tmpPathSpotSelect) {
      return;
    }

    if (aHtmlElement instanceof HtmlSelect) {
      // has the node the text before
      FindSpot tmpNodeSpot = domNodeText.getPosition(aHtmlElement);
      if (tmpPathSpotSelect.endPos <= tmpNodeSpot.startPos) {

        // if the select follows text directly and text matches => choose it
        String tmpText = domNodeText.getLabelTextBefore(aHtmlElement, tmpPathSpotSelect.endPos);
        if (StringUtils.isNotEmpty(tmpText)) {
          int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpText);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(aHtmlElement);
            int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, foundElements);
          }
        }

        // name
        String tmpName = aHtmlElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(aHtmlElement);
            int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, foundElements);
          }
        }

        // id
        String tmpId = aHtmlElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(aHtmlElement);
            int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            getOption((HtmlSelect) aHtmlElement, tmpSearchPattern, tmpDistance, foundElements);
          }
        }
      }

    } else if (aHtmlElement instanceof HtmlLabel) {
      // has the node the text before
      FindSpot tmpNodeSpot = domNodeText.getPosition(aHtmlElement);
      HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      String tmpText = domNodeText.getAsText(tmpLabel);

      // select
      if (tmpPathSpotSelect.endPos <= tmpNodeSpot.startPos) {

        int tmpCoverage = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if (tmpCoverage > -1) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);
              if (tmpElementForLabel instanceof HtmlSelect) {
                if (tmpElementForLabel.isDisplayed()) {
                  String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);
                  int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                  getOption((HtmlSelect) tmpElementForLabel, tmpSearchPattern, tmpDistance, foundElements);
                }
              }
            } catch (ElementNotFoundException e) {
              // not found
            }
          }

          // Element must be a nested element of label
          Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpChilds) {
            if (tmpChildElement instanceof HtmlSelect) {
              if (tmpChildElement.isDisplayed()) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);
                int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                getOption((HtmlSelect) tmpChildElement, tmpSearchPattern, tmpDistance, foundElements);
              }
            }
          }
        }
      }
    }
  }

  /**
   * searches for nested option of a given select by label, value or text
   * 
   * @param aSelect HtmlSelect which should contain this option
   * @param aSearchPattern value or label of option
   * @param aDistance the distance of the control
   * @param aWeightedControlList the list to add the control to
   * @return found
   */
  protected boolean getOption(HtmlSelect aSelect, SearchPattern aSearchPattern, int aDistance,
      WeightedControlList aWeightedControlList) {
    boolean tmpFound = false;
    Iterable<HtmlOption> tmpOptions = aSelect.getOptions();
    for (HtmlOption tmpOption : tmpOptions) {
      String tmpText = domNodeText.getAsText(tmpOption);
      if (StringUtils.isNotEmpty(tmpText)) {
        int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance);
          tmpFound = true;
        }
      }

      tmpText = tmpOption.getLabelAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance);
          tmpFound = true;
        }
      }

      tmpText = tmpOption.getValueAttribute();
      if (StringUtils.isNotEmpty(tmpText)) {
        int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (tmpCoverage > -1) {
          aWeightedControlList.add(new HtmlUnitOption(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              aDistance);
          tmpFound = true;
        }
      }
    }
    return tmpFound;
  }
}
