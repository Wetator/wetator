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


package org.rbri.wet.backend.htmlunit;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.util.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Util class that contains all methods
 * used to find html elements on a page.
 * 
 * @author rbri
 */
public class HtmlUnitControlFinder implements ControlFinder {
  // private static final Log LOG = LogFactory.getLog(HtmlUnitControlFinder.class);;

  /**
   * the page to work on
   */
  protected HtmlPage htmlPage;
  /**
   * the DomNodeText index of the page
   */
  protected DomNodeText domNodeText;

  /**
   * Constructor
   * 
   * @param anHtmlPage the page to search in
   */
  public HtmlUnitControlFinder(HtmlPage anHtmlPage) {
    if (null == anHtmlPage) {
      throw new NullPointerException("HtmlPage can't be null");
    }
    htmlPage = anHtmlPage;
    domNodeText = new DomNodeText(htmlPage.getBody());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllSetables(java.util.List)
   */
  public WeightedControlList getAllSetables(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    // special case to support some search engines
    if (aSearch.isEmpty()) {
      for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
        if (tmpHtmlElement.isDisplayed()) {
          if ((tmpHtmlElement instanceof HtmlTextInput) || (tmpHtmlElement instanceof HtmlPasswordInput)
              || (tmpHtmlElement instanceof HtmlTextArea) || (tmpHtmlElement instanceof HtmlFileInput)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_ID, 0, // no
                // coverage
                domNodeText.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundElements;
          }
        }
      }
    }

    // ok, it's time for the normal search
    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);
    SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(aSearch);

    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);
    if (null == tmpPathSpot) {
      return tmpFoundElements;
    }

    for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {

      // real label
      if (tmpHtmlElement instanceof HtmlLabel) {
        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          HtmlLabel tmpLabel = (HtmlLabel) tmpHtmlElement;

          // found a label with this text
          String tmpText = domNodeText.getAsText(tmpLabel);
          int tmpCoverage = tmpSearchPattern.noOfCharsAfterLastOccurenceIn(tmpText);
          if (tmpCoverage > -1) {
            String tmpForAttribute = tmpLabel.getForAttribute();
            // label contains a for-attribute => find corresponding element
            if (StringUtils.isNotEmpty(tmpForAttribute)) {
              try {
                HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);
                if ((tmpElementForLabel instanceof HtmlTextInput) || (tmpElementForLabel instanceof HtmlPasswordInput)
                    || (tmpElementForLabel instanceof HtmlTextArea) || (tmpElementForLabel instanceof HtmlFileInput)) {
                  if (tmpElementForLabel.isDisplayed()) {
                    String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);
                    int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                    tmpFoundElements.add(new HtmlUnitControl(tmpElementForLabel),
                        WeightedControlList.FoundType.BY_LABEL, tmpCoverage, tmpDistance);
                    continue;
                  }
                }
              } catch (ElementNotFoundException e) {
                // not found
              }
            }

            // Element must be a nested element of label
            Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
            for (HtmlElement tmpChildElement : tmpChilds) {
              if ((tmpChildElement instanceof HtmlTextInput) || (tmpChildElement instanceof HtmlPasswordInput)
                  || (tmpChildElement instanceof HtmlTextArea) || (tmpChildElement instanceof HtmlFileInput)) {
                if (tmpChildElement.isDisplayed()) {
                  String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);
                  int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                  tmpFoundElements.add(new HtmlUnitControl(tmpChildElement), WeightedControlList.FoundType.BY_LABEL,
                      tmpCoverage, tmpDistance);
                  continue;
                }
              }
            }
          }
        }
      }

      if ((tmpHtmlElement instanceof HtmlTextInput) || (tmpHtmlElement instanceof HtmlPasswordInput)
          || (tmpHtmlElement instanceof HtmlTextArea) || (tmpHtmlElement instanceof HtmlFileInput)) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          // label text before
          // and rest of the path before the label
          String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpHtmlElement, tmpPathSpot.endPos);
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabelTextBefore);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            // in this case the label is part of the text before
            // lets try to remove that
            tmpTextBefore = tmpTextBefore.substring(0, tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpTextBefore));
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_LABEL_TEXT,
                tmpCoverage, tmpDistance);
            continue;
          }

          // whole text before
          String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
          tmpCoverage = tmpWholePathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
          if (tmpCoverage > -1) {
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_TEXT,
                tmpCoverage, tmpDistance);
            continue;
          }

          // name
          String tmpName = tmpHtmlElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_NAME,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }

          // id
          String tmpId = tmpHtmlElement.getId();
          if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
            if (tmpCoverage > -1) {
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_ID,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }
      }
    }

    return tmpFoundElements;
  }

  /**
   * Returns all Buttons (HtmlSubmitInput, HtmlResetInput,
   * HtmlButtonInput, HtmlButton) on the page with
   * the given name (value, name).
   * 
   * @param aSearch the filter
   * @return the list of matching clickables
   */
  public WeightedControlList getAllClickables(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);
    if (null == tmpPathSpot) {
      return tmpFoundElements;
    }

    for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
      if ((tmpHtmlElement instanceof HtmlSubmitInput) || (tmpHtmlElement instanceof HtmlResetInput)
          || (tmpHtmlElement instanceof HtmlButtonInput)) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          String tmpLabel = tmpHtmlElement.getAttribute("value");

          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabel);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_LABEL_TEXT,
                tmpCoverage, tmpDistance);
            continue;
          }

          String tmpName = tmpHtmlElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_NAME,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }
      } else if (tmpHtmlElement instanceof HtmlImageInput) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          HtmlImageInput tmpImage = (HtmlImageInput) tmpHtmlElement;

          // does image alt-text match?
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAltAttribute());
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                WeightedControlList.FoundType.BY_IMG_ALT_ATTRIBUTE, tmpCoverage, tmpDistance);
            continue;
          }

          // does image title-text match?
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                WeightedControlList.FoundType.BY_IMG_TITLE_ATTRIBUTE, tmpCoverage, tmpDistance);
            continue;
          }

          // does image filename match?
          String tmpSrc = tmpImage.getSrcAttribute();
          if (tmpSearchPattern.matchesAtEnd(tmpSrc)) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                WeightedControlList.FoundType.BY_IMG_SRC_ATTRIBUTE, tmpCoverage, tmpDistance);
            continue;
          }

          String tmpName = tmpHtmlElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_NAME,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }
      } else if (tmpHtmlElement instanceof HtmlButton) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          String tmpLabel = domNodeText.getAsText(tmpHtmlElement);

          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabel);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_LABEL_TEXT,
                tmpCoverage, tmpDistance);
            continue;
          }

          String tmpName = tmpHtmlElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_NAME,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }

          // now check for the including image
          Iterable<HtmlElement> tmpAllChilds = tmpHtmlElement.getHtmlElementDescendants();
          for (HtmlElement tmpInnerElement : tmpAllChilds) {
            if (tmpInnerElement instanceof HtmlImage) {
              HtmlImage tmpImage = (HtmlImage) tmpInnerElement;
              // check for the image alt tag is not needed, alt text is part of the button text text

              // does image title-text match?
              tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
              if (tmpCoverage > -1) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                    WeightedControlList.FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, tmpCoverage, tmpDistance);
                continue;
              }

              // does image filename match?
              String tmpSrc = tmpImage.getSrcAttribute();
              if (tmpSearchPattern.matchesAtEnd(tmpSrc)) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                    WeightedControlList.FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, tmpCoverage, tmpDistance);
                continue;
              }

              tmpName = tmpImage.getAttribute("name");
              if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                if (tmpCoverage > -1) {
                  String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                  int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                  tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement),
                      WeightedControlList.FoundType.BY_INNER_NAME, tmpCoverage, tmpDistance);
                  continue;
                }
              }
            }
          }
        }
      } else if (tmpHtmlElement instanceof HtmlAnchor) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElement;

          // text match?
          String tmpText = domNodeText.getAsText(tmpAnchor);
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpText);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpAnchor), WeightedControlList.FoundType.BY_LABEL_TEXT,
                tmpCoverage, tmpDistance);
            continue;
          }

          // now check for the including image
          Iterable<HtmlElement> tmpAllchildElements = tmpAnchor.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpAllchildElements) {
            if (tmpChildElement instanceof HtmlImage) {
              HtmlImage tmpImage = (HtmlImage) tmpChildElement;
              // check for the image alt tag is not needed, alt text is part of the anchor text

              // does image title-text match?
              tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
              if (tmpCoverage > -1) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                tmpFoundElements.add(new HtmlUnitControl(tmpAnchor),
                    WeightedControlList.FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE, tmpCoverage, tmpDistance);
                continue;
              }

              // does image filename match?
              String tmpSrc = tmpImage.getSrcAttribute();
              if (tmpSearchPattern.matchesAtEnd(tmpSrc)) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                tmpFoundElements.add(new HtmlUnitControl(tmpAnchor),
                    WeightedControlList.FoundType.BY_INNER_IMG_SRC_ATTRIBUTE, tmpCoverage, tmpDistance);
                continue;
              }

              String tmpName = tmpImage.getAttribute("name");
              if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                if (tmpCoverage > -1) {
                  String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
                  int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                  tmpFoundElements.add(new HtmlUnitControl(tmpAnchor), WeightedControlList.FoundType.BY_INNER_NAME,
                      tmpCoverage, tmpDistance);
                  continue;
                }
              }
            }
          }

          // name match?
          String tmpName = tmpHtmlElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpAnchor), WeightedControlList.FoundType.BY_NAME, tmpCoverage,
                  tmpDistance);
              continue;
            }
          }
        }
      } else if (tmpHtmlElement instanceof HtmlImage) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          HtmlImage tmpImage = (HtmlImage) tmpHtmlElement;

          // does image alt-text match?
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAltAttribute());
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpImage), WeightedControlList.FoundType.BY_IMG_ALT_ATTRIBUTE,
                tmpCoverage, tmpDistance);
            continue;
          }

          // does image title-text match?
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpImage), WeightedControlList.FoundType.BY_IMG_TITLE_ATTRIBUTE,
                tmpCoverage, tmpDistance);
            continue;
          }

          // does image filename match?
          String tmpSrc = tmpImage.getSrcAttribute();
          if (tmpSearchPattern.matchesAtEnd(tmpSrc)) {
            tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpImage), WeightedControlList.FoundType.BY_IMG_SRC_ATTRIBUTE,
                tmpCoverage, tmpDistance);
            continue;
          }

          String tmpName = tmpImage.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpImage), WeightedControlList.FoundType.BY_NAME, tmpCoverage,
                  tmpDistance);
              continue;
            }
          }
        }
      }

      // id match?
      if ((tmpHtmlElement instanceof HtmlSubmitInput) || (tmpHtmlElement instanceof HtmlResetInput)
          || (tmpHtmlElement instanceof HtmlButtonInput) || (tmpHtmlElement instanceof HtmlImageInput)
          || (tmpHtmlElement instanceof HtmlButton) || (tmpHtmlElement instanceof HtmlAnchor)
          || (tmpHtmlElement instanceof HtmlImage)) {

        // has the node the text before
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

          String tmpId = tmpHtmlElement.getId();
          if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
            int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
            if (tmpCoverage > -1) {
              String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
              int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
              tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_ID,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }
      }
    }

    return tmpFoundElements;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllSelectables(java.util.List)
   */
  public WeightedControlList getAllSelectables(final List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

    SearchPattern tmpSearchPatternSelect = new SearchPattern("");
    if (aSearch.size() > 1) {
      tmpSearchPatternSelect = aSearch.get(aSearch.size() - 2).getSearchPattern();
    }
    SearchPattern tmpPathSearchPatternSelect = SearchPattern.createFromList(aSearch, aSearch.size() - 2);

    for (HtmlElement tmpElement : domNodeText.getAllVisibleHtmlElements()) {
      if (tmpElement instanceof HtmlLabel) {
        HtmlLabel tmpLabel = (HtmlLabel) tmpElement;

        // found a label with this text
        String tmpText = domNodeText.getAsText(tmpLabel);
        String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);

        int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        // label for select
        int tmpCoverage = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);

              if (tmpElementForLabel instanceof HtmlSelect) {
                HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpElementForLabel;
                boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
                if (tmpFound) {
                  continue;
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
              HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpChildElement;
              boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
              if (tmpFound) {
                continue;
              }
            }
          }
        }

        // label for Checkbox/RadioButton
        tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpText);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);

              if ((tmpElementForLabel instanceof HtmlCheckBoxInput)
                  || (tmpElementForLabel instanceof HtmlRadioButtonInput)) {
                tmpFoundElements.add(new HtmlUnitControl(tmpElementForLabel), WeightedControlList.FoundType.BY_LABEL,
                    tmpCoverage, tmpDistance);
                continue;
              }
            } catch (ElementNotFoundException e) {
              // not found
            }
          }

          // Element must be a nested element of label
          Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpChilds) {
            if ((tmpChildElement instanceof HtmlCheckBoxInput) || (tmpChildElement instanceof HtmlRadioButtonInput)) {
              tmpFoundElements.add(new HtmlUnitControl(tmpChildElement), WeightedControlList.FoundType.BY_LABEL,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }

      } else if (tmpElement instanceof HtmlSelect) {
        HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpElement;
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        // if the select follows text directly and text matches => choose it
        // TODO
        String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement, -1);

        int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        int tmpLabelDistance = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpLabelTextBefore);
        if ((tmpDistance > -1) && (tmpLabelDistance > -1)) {
          boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpLabelDistance, tmpFoundElements);
          if (tmpFound) {
            continue;
          }
        }

        // name
        String tmpName = tmpElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
          int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
            if (tmpFound) {
              continue;
            }
          }
        }

        // id
        String tmpId = tmpElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
          int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
            if (tmpFound) {
              continue;
            }
          }
        }
      } else if (tmpElement instanceof HtmlCheckBoxInput) {
        HtmlCheckBoxInput tmpCheckBox = (HtmlCheckBoxInput) tmpElement;
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        // if the select follows text directly and text matches => choose it
        String tmpLabelTextAfter = domNodeText.getLabelTextAfter(tmpElement);

        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabelTextAfter);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_LABEL_TEXT,
              tmpCoverage, tmpDistance);
          continue;
        }

        // by name
        String tmpName = tmpCheckBox.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_NAME, tmpCoverage,
                tmpDistance);
            continue;
          }
        }

        // id
        String tmpId = tmpCheckBox.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      } else if (tmpElement instanceof HtmlRadioButtonInput) {
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        // if the select follows text directly and text matches => choose it
        String tmpLabelTextAfter = domNodeText.getLabelTextAfter(tmpElement);

        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabelTextAfter);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_LABEL_TEXT,
              tmpCoverage, tmpDistance);
          continue;
        }

        // no search by name

        // id
        String tmpId = tmpElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      }

      // by id
      if (tmpElement instanceof HtmlOption) {
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        String tmpId = tmpElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      }
    }

    return tmpFoundElements;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllDeselectables(java.util.List)
   */
  public WeightedControlList getAllDeselectables(final List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

    SearchPattern tmpSearchPatternSelect = new SearchPattern("");
    if (aSearch.size() > 1) {
      tmpSearchPatternSelect = aSearch.get(aSearch.size() - 2).getSearchPattern();
    }
    SearchPattern tmpPathSearchPatternSelect = SearchPattern.createFromList(aSearch, aSearch.size() - 2);

    for (HtmlElement tmpElement : domNodeText.getAllVisibleHtmlElements()) {
      if (tmpElement instanceof HtmlLabel) {
        HtmlLabel tmpLabel = (HtmlLabel) tmpElement;

        // found a label with this text
        String tmpText = domNodeText.getAsText(tmpLabel);
        String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);

        int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        // label for select
        int tmpCoverage = tmpSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpText);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);

              if (tmpElementForLabel instanceof HtmlSelect) {
                HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpElementForLabel;
                if (tmpHtmlSelect.isMultipleSelectEnabled()) {
                  boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
                  if (tmpFound) {
                    continue;
                  }
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
              HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpChildElement;
              if (tmpHtmlSelect.isMultipleSelectEnabled()) {
                boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
                if (tmpFound) {
                  continue;
                }
              }
            }
          }
        }

        // label for Checkbox
        tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpText);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);

              if (tmpElementForLabel instanceof HtmlCheckBoxInput) {
                tmpFoundElements.add(new HtmlUnitControl(tmpElementForLabel), WeightedControlList.FoundType.BY_LABEL,
                    tmpCoverage, tmpDistance);
                continue;
              }
            } catch (ElementNotFoundException e) {
              // not found
            }
          }

          // Element must be a nested element of label
          Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpChilds) {
            if (tmpChildElement instanceof HtmlCheckBoxInput) {
              tmpFoundElements.add(new HtmlUnitControl(tmpChildElement), WeightedControlList.FoundType.BY_LABEL,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }

      } else if (tmpElement instanceof HtmlSelect) {
        HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpElement;
        if (tmpHtmlSelect.isMultipleSelectEnabled()) {
          String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

          // if the select follows text directly and text matches => choose it
          // TODO
          String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement, -1);

          int tmpDistance = tmpPathSearchPatternSelect.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
          int tmpLabelDistance = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpLabelTextBefore);
          if ((tmpDistance > -1) && (tmpLabelDistance > -1)) {
            boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpLabelDistance, tmpFoundElements);
            if (tmpFound) {
              continue;
            }
          }

          // name
          String tmpName = tmpElement.getAttribute("name");
          if (StringUtils.isNotEmpty(tmpName) && tmpSearchPatternSelect.matches(tmpName)) {
            int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpName);
            if ((tmpCoverage > -1) && (tmpDistance > -1)) {
              boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
              if (tmpFound) {
                continue;
              }
            }
          }

          // id
          String tmpId = tmpElement.getId();
          if (StringUtils.isNotEmpty(tmpId) && tmpSearchPatternSelect.matches(tmpId)) {
            int tmpCoverage = tmpSearchPatternSelect.noOfSurroundingCharsIn(tmpId);
            if ((tmpCoverage > -1) && (tmpDistance > -1)) {
              boolean tmpFound = getOption(tmpHtmlSelect, tmpSearchPattern, tmpDistance, tmpFoundElements);
              if (tmpFound) {
                continue;
              }
            }
          }
        }
      } else if (tmpElement instanceof HtmlCheckBoxInput) {
        HtmlCheckBoxInput tmpCheckBox = (HtmlCheckBoxInput) tmpElement;
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        // if the select follows text directly and text matches => choose it
        String tmpLabelTextAfter = domNodeText.getLabelTextAfter(tmpElement);

        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabelTextAfter);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_LABEL_TEXT,
              tmpCoverage, tmpDistance);
          continue;
        }

        // by name
        String tmpName = tmpCheckBox.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_NAME, tmpCoverage,
                tmpDistance);
            continue;
          }
        }

        // id
        String tmpId = tmpCheckBox.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
          tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpCheckBox), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      }
    }

    return tmpFoundElements;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.ControlFinder#getAllOtherControls(java.util.List)
   */
  public WeightedControlList getAllOtherControls(final List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpLabelSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);
    SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(aSearch);

    for (HtmlElement tmpElement : domNodeText.getAllVisibleHtmlElements()) {

      // real label
      if (tmpElement instanceof HtmlLabel) {
        HtmlLabel tmpLabel = (HtmlLabel) tmpElement;

        // found a label with this text
        String tmpText = domNodeText.getAsText(tmpLabel);
        String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);

        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        int tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpText);
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          String tmpForAttribute = tmpLabel.getForAttribute();
          // label contains a for-attribute => find corresponding element
          if (StringUtils.isNotEmpty(tmpForAttribute)) {
            try {
              HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);
              if (tmpElementForLabel instanceof HtmlSelect) {
                tmpFoundElements.add(new HtmlUnitControl(tmpElementForLabel), WeightedControlList.FoundType.BY_LABEL,
                    tmpCoverage, tmpDistance);
                continue;
              }
            } catch (ElementNotFoundException e) {
              // not found
            }
          }

          // Element must be a nested element of label
          Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
          for (HtmlElement tmpChildElement : tmpChilds) {
            if (tmpChildElement instanceof HtmlSelect) {
              tmpFoundElements.add(new HtmlUnitControl(tmpChildElement), WeightedControlList.FoundType.BY_LABEL,
                  tmpCoverage, tmpDistance);
              continue;
            }
          }
        }
      }

      if (tmpElement instanceof HtmlSelect) {
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

        // if the select follows text directly and text matches => choose it
        // TODO
        String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement, -1);

        int tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpLabelTextBefore);
        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
              tmpDistance);
          continue;
        }

        // name
        String tmpName = tmpElement.getAttribute("name");
        if (StringUtils.isNotEmpty(tmpName) && tmpLabelSearchPattern.matches(tmpName)) {
          tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpName);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_NAME, tmpCoverage,
                tmpDistance);
            continue;
          }
        }

        // id
        String tmpId = tmpElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpLabelSearchPattern.matches(tmpId)) {
          tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }

        // whole text before
        tmpCoverage = tmpWholePathSearchPattern.noOfSurroundingCharsIn(tmpTextBefore);
        tmpDistance = tmpWholePathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
        if (tmpDistance > -1) {
          tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_TEXT, tmpCoverage,
              tmpDistance);
          continue;
        }
      }
      if (tmpElement instanceof HtmlOptionGroup) {
        String tmpTextBefore = domNodeText.getTextBefore(tmpElement);
        int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

        // label
        int tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpElement.getAttribute("label"));
        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
          tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_LABEL_TEXT,
              tmpCoverage, tmpDistance);
          continue;
        }

        // id
        String tmpId = tmpElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpLabelSearchPattern.matches(tmpId)) {
          tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpId);
          if ((tmpCoverage > -1) && (tmpDistance > -1)) {
            tmpFoundElements.add(new HtmlUnitControl(tmpElement), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      }
    }

    return tmpFoundElements;
  }

  /**
   * Returns all elements for the text (path).
   * 
   * @param aSearch the filter
   * @return the list of matching elements
   */
  public WeightedControlList getAllElementsForText(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);
    if (null == tmpPathSpot) {
      return tmpFoundElements;
    }

    // search with id
    for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
      FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);

      // has the node the text before
      if (tmpPathSpot.endPos <= tmpNodeSpot.startPos) {

        String tmpId = tmpHtmlElement.getId();
        if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
          int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);
            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_ID, tmpCoverage,
                tmpDistance);
            continue;
          }
        }
      }
    }

    FindSpot tmpHitSpot = domNodeText.firstOccurence(tmpSearchPattern, Math.max(0, tmpPathSpot.endPos));
    while ((null != tmpHitSpot) && (tmpHitSpot.endPos > -1)) {
      // found a hit

      // find the first element that surrounds this
      for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElementsBottomUpBottomUp()) {
        FindSpot tmpNodeSpot = domNodeText.getPosition(tmpHtmlElement);
        if ((tmpNodeSpot.startPos <= tmpHitSpot.startPos) && (tmpHitSpot.endPos <= tmpNodeSpot.endPos)) {
          // found one
          String tmpTextBefore = domNodeText.getTextBeforeIncludingMyself(tmpHtmlElement);
          FindSpot tmpLastOccurence = tmpSearchPattern.lastOccurenceIn(tmpTextBefore);
          int tmpCoverage = tmpTextBefore.length() - tmpLastOccurence.endPos;

          tmpTextBefore = tmpTextBefore.substring(0, tmpLastOccurence.startPos);
          int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

          tmpFoundElements.add(new HtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_TEXT, tmpCoverage,
              tmpDistance);
          break;
        }
      }

      tmpHitSpot = domNodeText.firstOccurence(tmpSearchPattern, tmpHitSpot.startPos + 1);
    }
    return tmpFoundElements;
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
      int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        aWeightedControlList.add(new HtmlUnitControl(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
            aDistance);
        tmpFound = true;
      }

      tmpText = tmpOption.getLabelAttribute();
      tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        aWeightedControlList.add(new HtmlUnitControl(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
            aDistance);
        tmpFound = true;
      }

      tmpText = tmpOption.getValueAttribute();
      tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        aWeightedControlList.add(new HtmlUnitControl(tmpOption), WeightedControlList.FoundType.BY_LABEL, tmpCoverage,
            aDistance);
        tmpFound = true;
      }
    }
    return tmpFound;
  }

  /**
   * TODO helper; has to be removed if writing your own finder is possible
   */
  public final <E extends HtmlElement> List<E> getElementsByAttribute(String anElementName, String anAttributeName,
      String anAttributeValue) {
    HtmlElement tmpBody = htmlPage.getBody();
    if (null == tmpBody) {
      return null;
    }
    return tmpBody.getElementsByAttribute(anElementName, anAttributeName, anAttributeValue);
  }
}