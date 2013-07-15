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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.backend.ControlFinder;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.util.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlHtml;
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
    private static final Log LOG = LogFactory.getLog(HtmlUnitControlFinder.class);;

    protected HtmlPage htmlPage;
    protected DomNodeText domNodeText;

    /**
     * Constructor
     *
     * @param aHtmlPage the page to search in
     */
    public HtmlUnitControlFinder(HtmlPage aHtmlPage) {
        if (null == aHtmlPage) {
            throw new NullPointerException("HtmlPage can't be null");
        }
        htmlPage = aHtmlPage;
        domNodeText = new DomNodeText(htmlPage);
    }


    public WeightedControlList getAllSetables(List<SecretString> aSearch) {
        WeightedControlList tmpFoundElements = new WeightedControlList();
        Iterable<HtmlElement> tmpAllchildElements = htmlPage.getHtmlElementDescendants();

        // special case to support some search engines
        if (aSearch.isEmpty()) {
            for (HtmlElement tmpElement : tmpAllchildElements) {
                if (tmpElement.isDisplayed()) {
                    if ((tmpElement instanceof HtmlTextInput)
                            || (tmpElement instanceof HtmlPasswordInput)
                            || (tmpElement instanceof HtmlTextArea)
                            || (tmpElement instanceof HtmlFileInput)
                    ) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                0, // no coverage
                                domNodeText.getTextBefore(tmpElement).length()); // distance from page start
                        return tmpFoundElements;
                    }
                }
            }
        }

        // ok, it's time for the normal search
        SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
        SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);
        SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(aSearch);

        for (HtmlElement tmpElement : domNodeText.getAllVisibleHtmlElements()) {

            // real label
            if (tmpElement instanceof HtmlLabel) {
                HtmlLabel tmpLabel = (HtmlLabel) tmpElement;

                // found a label with this text
                String tmpText = domNodeText.getAsText(tmpLabel);
                String tmpTextBefore = domNodeText.getTextBefore(tmpLabel);

                int tmpCoverage = tmpSearchPattern.noOfCharsAfterLastOccurenceIn(tmpText);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                    String tmpForAttribute = tmpLabel.getForAttribute();
                    // label contains a for-attribute => find corresponding element
                    if (StringUtils.isNotEmpty(tmpForAttribute)) {
                        try {
                            HtmlElement tmpElementForLabel = htmlPage.getHtmlElementById(tmpForAttribute);
                            if ((tmpElementForLabel instanceof HtmlTextInput)
                                    || (tmpElementForLabel instanceof HtmlPasswordInput)
                                    || (tmpElementForLabel instanceof HtmlTextArea)
                                    || (tmpElementForLabel instanceof HtmlFileInput)
                                ) {
                                tmpFoundElements.add(
                                        new HtmlUnitControl(tmpElementForLabel),
                                        WeightedControlList.FoundType.BY_LABEL,
                                        tmpCoverage,
                                        tmpDistance);
                                continue;
                            }
                        } catch (ElementNotFoundException e) {
                            // not found
                        }
                    }

                    // Element must be a nested element of label
                    Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
                    for (HtmlElement tmpChildElement : tmpChilds) {
                        if ((tmpChildElement instanceof HtmlTextInput)
                                || (tmpChildElement instanceof HtmlPasswordInput)
                                || (tmpChildElement instanceof HtmlTextArea)
                                || (tmpChildElement instanceof HtmlFileInput)
                        ) {
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpChildElement),
                                    WeightedControlList.FoundType.BY_LABEL,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }
                    }
                }
            }

            if ((tmpElement instanceof HtmlTextInput)
                    || (tmpElement instanceof HtmlPasswordInput)
                    || (tmpElement instanceof HtmlTextArea)
                    || (tmpElement instanceof HtmlFileInput)
            ) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                // label text before
                // and rest of the path before the label
                String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement);
                // TODO the text before contains the label text

                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabelTextBefore);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // whole text before
                tmpCoverage = tmpWholePathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                if (tmpCoverage > -1) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // name
                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }

                // id
                String tmpId = tmpElement.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
                                tmpDistance);
                        continue;
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
     * @param aSearchPattern the filter
     * @return the list of matching clickables
     */
    public WeightedControlList getAllClickables(List<SecretString> aSearch) {
        WeightedControlList tmpFoundElements = new WeightedControlList();

        SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
        SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

        for (HtmlElement tmpElement : domNodeText.getAllVisibleHtmlElements()) {
            if ((tmpElement instanceof HtmlSubmitInput)
                    || (tmpElement instanceof HtmlResetInput)
                    || (tmpElement instanceof HtmlButtonInput)) {

                String tmpLabel = tmpElement.getAttribute("value");
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabel);
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            } else if (tmpElement instanceof HtmlImageInput) {
                HtmlImageInput tmpImage = (HtmlImageInput) tmpElement;
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                // does image alt-text match?
                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAltAttribute());
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_IMG_ALT_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // does image title-text match?
                tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_IMG_TITLE_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // does image filename match?
                String tmpSrc = tmpImage.getSrcAttribute();
                if ((tmpSearchPattern.matchesAtEnd(tmpSrc)) && (tmpDistance > -1)) {
                    tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_IMG_SRC_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            } else if (tmpElement instanceof HtmlButton) {
                String tmpLabel = domNodeText.getAsText(tmpElement);
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpLabel);
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }

                // now check for the including image
                Iterable<HtmlElement> tmpAllChilds = tmpElement.getHtmlElementDescendants();
                for (HtmlElement tmpInnerElement : tmpAllChilds) {
                    if (tmpInnerElement instanceof HtmlImage) {
                        HtmlImage tmpImage = (HtmlImage) tmpInnerElement;
                        // check for the image alt tag is not needed, alt text is part of the button text text

                        // does image title-text match?
                        tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
                        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpElement),
                                    WeightedControlList.FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }

                        // does image filename match?
                        String tmpSrc = tmpImage.getSrcAttribute();
                        if ((tmpSearchPattern.matchesAtEnd(tmpSrc)) && (tmpDistance > -1)) {
                            tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpElement),
                                    WeightedControlList.FoundType.BY_INNER_IMG_SRC_ATTRIBUTE,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }

                        tmpName = tmpImage.getAttribute("name");
                        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                            if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                                tmpFoundElements.add(
                                        new HtmlUnitControl(tmpElement),
                                        WeightedControlList.FoundType.BY_INNER_NAME,
                                        tmpCoverage,
                                        tmpDistance);
                                continue;
                            }
                        }
                    }
                }
            } else if (tmpElement instanceof HtmlAnchor) {
                HtmlAnchor tmpAnchor = (HtmlAnchor) tmpElement;
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                // text match?
                String tmpText = domNodeText.getAsText(tmpAnchor);
                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpText);
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpAnchor),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
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
                        if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpAnchor),
                                    WeightedControlList.FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }

                        // does image filename match?
                        String tmpSrc = tmpImage.getSrcAttribute();
                        if ((tmpSearchPattern.matchesAtEnd(tmpSrc)) && (tmpDistance > -1)) {
                            tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpAnchor),
                                    WeightedControlList.FoundType.BY_INNER_IMG_SRC_ATTRIBUTE,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }

                        String tmpName = tmpImage.getAttribute("name");
                        if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                            tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                            if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                                tmpFoundElements.add(
                                        new HtmlUnitControl(tmpAnchor),
                                        WeightedControlList.FoundType.BY_INNER_NAME,
                                        tmpCoverage,
                                        tmpDistance);
                                continue;
                            }
                        }
                    }
                }

                // name match?
                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpAnchor),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            } else if (tmpElement instanceof HtmlImage) {
                HtmlImage tmpImage = (HtmlImage) tmpElement;
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                // does image alt-text match?
                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAltAttribute());
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpImage),
                            WeightedControlList.FoundType.BY_IMG_ALT_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // does image title-text match?
                tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpImage.getAttribute("title"));
                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpImage),
                            WeightedControlList.FoundType.BY_IMG_TITLE_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // does image filename match?
                String tmpSrc = tmpImage.getSrcAttribute();
                if ((tmpSearchPattern.matchesAtEnd(tmpSrc)) && (tmpDistance > -1)) {
                    tmpCoverage = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpSrc);
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpImage),
                            WeightedControlList.FoundType.BY_IMG_SRC_ATTRIBUTE,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                String tmpName = tmpImage.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpImage),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            }

            // id match?
            if ((tmpElement instanceof HtmlSubmitInput)
                || (tmpElement instanceof HtmlResetInput)
                || (tmpElement instanceof HtmlButtonInput)
                || (tmpElement instanceof HtmlImageInput)
                || (tmpElement instanceof HtmlButton)
                || (tmpElement instanceof HtmlAnchor)
                || (tmpElement instanceof HtmlImage)
            ) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                String tmpId = tmpElement.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
                    int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            }
        }

        return tmpFoundElements;
    }



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
                                HtmlSelect tmpHtmlSelect = (HtmlSelect)tmpElementForLabel;
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
                            HtmlSelect tmpHtmlSelect = (HtmlSelect)tmpChildElement;
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

                            if (    (tmpElementForLabel instanceof HtmlCheckBoxInput)
                                    || (tmpElementForLabel instanceof HtmlRadioButtonInput) ) {
                                tmpFoundElements.add(
                                        new HtmlUnitControl(tmpElementForLabel),
                                        WeightedControlList.FoundType.BY_LABEL,
                                        tmpCoverage,
                                        tmpDistance);
                                continue;
                            }
                        } catch (ElementNotFoundException e) {
                            // not found
                        }
                    }

                    // Element must be a nested element of label
                    Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
                    for (HtmlElement tmpChildElement : tmpChilds) {
                        if (    (tmpChildElement instanceof HtmlCheckBoxInput)
                                || (tmpChildElement instanceof HtmlRadioButtonInput) ) {
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpChildElement),
                                    WeightedControlList.FoundType.BY_LABEL,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }
                    }
                }

            } else if (tmpElement instanceof HtmlSelect) {
                HtmlSelect tmpHtmlSelect = (HtmlSelect) tmpElement;
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                // if the select follows text directly and text matches => choose it
                String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement);

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
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpCheckBox),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // by name
                String tmpName = tmpCheckBox.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpCheckBox),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }

                // id
                String tmpId = tmpCheckBox.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpCheckBox),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
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
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // no search by name

                // id
                String tmpId = tmpElement.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
                    tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
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
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }
            }
        }

        return tmpFoundElements;
    }



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
                                tmpFoundElements.add(
                                        new HtmlUnitControl(tmpElementForLabel),
                                        WeightedControlList.FoundType.BY_LABEL,
                                        tmpCoverage,
                                        tmpDistance);
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
                            tmpFoundElements.add(
                                    new HtmlUnitControl(tmpChildElement),
                                    WeightedControlList.FoundType.BY_LABEL,
                                    tmpCoverage,
                                    tmpDistance);
                            continue;
                        }
                    }
                }
            }

            if (tmpElement instanceof HtmlSelect) {
                String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

                // if the select follows text directly and text matches => choose it
                String tmpLabelTextBefore = domNodeText.getLabelTextBefore(tmpElement);

                int tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpLabelTextBefore);
                int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

                if ((tmpDistance > -1) && (tmpCoverage > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // name
                String tmpName = tmpElement.getAttribute("name");
                if (StringUtils.isNotEmpty(tmpName) && tmpLabelSearchPattern.matches(tmpName)) {
                    tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpName);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_NAME,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }

                // id
                String tmpId = tmpElement.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpLabelSearchPattern.matches(tmpId)) {
                    tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
                                tmpDistance);
                        continue;
                    }
                }

                // whole text before
                tmpCoverage = tmpWholePathSearchPattern.noOfSurroundingCharsIn(tmpTextBefore);
                tmpDistance = tmpWholePathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                if (tmpDistance > -1) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_TEXT,
                            tmpCoverage,
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
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_LABEL_TEXT,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }

                // id
                String tmpId = tmpElement.getId();
                if (StringUtils.isNotEmpty(tmpId) && tmpLabelSearchPattern.matches(tmpId)) {
                    tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpId);
                    if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpElement),
                                WeightedControlList.FoundType.BY_ID,
                                tmpCoverage,
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
     * @param aSearchPattern the filter
     * @return the list of matching elements
     */
    public WeightedControlList getAllElementsForText(List<SecretString> aSearch) {
        WeightedControlList tmpFoundElements = new WeightedControlList();

        SearchPattern tmpLabelSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
        SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

        DomNode tmpBodyNode = getBody(htmlPage);
        if (null == tmpBodyNode) {
            return tmpFoundElements;
        }

        String tmpText = domNodeText.getAsText(tmpBodyNode);
        int tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpText);
        if (!(tmpCoverage > -1)) {
            return tmpFoundElements;
        }


        List<HtmlElement> tmpElementsToTest = new LinkedList<HtmlElement>();
        List<HtmlElement> tmpNextElementsToTest = new LinkedList<HtmlElement>();
        tmpNextElementsToTest.add((HtmlBody)tmpBodyNode);

        do {
            tmpElementsToTest = tmpNextElementsToTest;
            tmpNextElementsToTest = new LinkedList<HtmlElement>();

            // iterate over the current level
            for (HtmlElement tmpHtmlElement : tmpElementsToTest) {
                // check the child's of the current node
                List<HtmlElement> tmpChildElementsFound = new LinkedList<HtmlElement>();
                for (HtmlElement tmpChildElement : tmpHtmlElement.getChildElements()) {
                    String tmpChildElementText = domNodeText.getAsText(tmpChildElement);
                    if (tmpLabelSearchPattern.noOfMatchingCharsIn(tmpChildElementText) > 0) {
                        tmpChildElementsFound.add(tmpChildElement);
                    }
                }
                if (tmpChildElementsFound.isEmpty()) {
                    // if we found nothing
                    // then the current node is part of the results
                    String tmpTextBefore = domNodeText.getTextBefore(tmpHtmlElement);

                    // it is a bit more complicated to calculate the text
                    String tmpElementText = domNodeText.getAsText(tmpHtmlElement);
                    int tmpPos = tmpLabelSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpElementText);
                    if (tmpPos > 0) {
                        tmpTextBefore = tmpTextBefore + " " + tmpElementText.substring(0, tmpPos);
                    }

                    int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
                    tmpCoverage = tmpLabelSearchPattern.noOfSurroundingCharsIn(tmpElementText);
                    if (tmpDistance > -1) {
                        tmpFoundElements.add(
                                new HtmlUnitControl(tmpHtmlElement),
                                WeightedControlList.FoundType.BY_TEXT,
                                tmpCoverage,
                                tmpDistance);
                    }
                } else {
                    // we have to check the child's
                    // in the next round
                    tmpNextElementsToTest.addAll(tmpChildElementsFound);
                }
            }

            // nothing new found, so lets stop
            if (tmpNextElementsToTest.isEmpty()) {
                break;
            }

        } while (true);

        return tmpFoundElements;
    }

    /**
     * TODO Returns
     *
     * @param aSearchPattern the filter
     * @return the first matching control
     */
    public WeightedControlList getFirstClickableTextElement(List<SecretString> aSearch) {
        WeightedControlList tmpFoundElements = new WeightedControlList();

        SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
        SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);

        // search with id
        Iterable<HtmlElement> tmpElements = htmlPage.getHtmlElementDescendants();
        for (HtmlElement tmpElement : tmpElements) {
            String tmpTextBefore = domNodeText.getTextBefore(tmpElement);

            int tmpDistance = tmpPathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);

            String tmpId = tmpElement.getId();
            if (StringUtils.isNotEmpty(tmpId) && tmpSearchPattern.matches(tmpId)) {
                int tmpCoverage = tmpSearchPattern.noOfSurroundingCharsIn(tmpId);
                if ((tmpCoverage > -1) && (tmpDistance > -1)) {
                    tmpFoundElements.add(
                            new HtmlUnitControl(tmpElement),
                            WeightedControlList.FoundType.BY_ID,
                            tmpCoverage,
                            tmpDistance);
                    continue;
                }
            }
        }

        HtmlBody tmpBodyNode = getBody(htmlPage);
        if (null == tmpBodyNode) {
            return tmpFoundElements;
        }

        String tmpText = domNodeText.getAsText(tmpBodyNode);
        int tmpLengthBefore = tmpSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpText);
        if (tmpLengthBefore < 0) {
            return tmpFoundElements;
        }

        if (tmpPathSearchPattern.noOfMatchingCharsIn(tmpText.substring(0, tmpLengthBefore)) < 0) {
            return tmpFoundElements;
        }

        for (HtmlElement tmpHtmlElement : tmpBodyNode.getChildElements()) {
            HtmlElement tmpResult = getFirstClickableTextElement(tmpHtmlElement, tmpSearchPattern, tmpPathSearchPattern);
            if (null != tmpResult) {
                // TODO
                tmpFoundElements.add(
                        new HtmlUnitControl(tmpResult),
                        WeightedControlList.FoundType.BY_TEXT,
                        0,
                        0);
                return tmpFoundElements;
            }
        }
        // TODO
        tmpFoundElements.add(
                new HtmlUnitControl(tmpBodyNode),
                WeightedControlList.FoundType.BY_TEXT,
                0,
                0);
        return tmpFoundElements;
    }


    private HtmlElement getFirstClickableTextElement(HtmlElement aHtmlElement, SearchPattern aSearchPattern, SearchPattern aPathSearchPattern) {
        String tmpText = domNodeText.getAsText(aHtmlElement);
        if ((aSearchPattern.noOfMatchingCharsIn(tmpText) < 0)) {
            return null;
        }

        String tmpTextBefore = domNodeText.getTextBefore(aHtmlElement);

        // it is a bit more complicated to calculate the text
        int tmpPos = aSearchPattern.noOfCharsBeforeLastOccurenceIn(tmpText);
        if (tmpPos > 0) {
            tmpTextBefore = tmpTextBefore + " " + tmpText.substring(0, tmpPos);
        }

        if (aPathSearchPattern.noOfMatchingCharsIn(tmpTextBefore) < 0) {
            return null;
        }

        for (HtmlElement tmpHtmlElement : aHtmlElement.getChildElements()) {
            HtmlElement tmpResult = getFirstClickableTextElement(tmpHtmlElement, aSearchPattern, aPathSearchPattern);
            if (null != tmpResult) {
                return tmpResult;
            }
        }
        return aHtmlElement;
    }


    /**
     * searches for nested option of a given select by label, value or text
     * @param aSelect HtmlSelect which should contain this option
     * @param aName value or label of option
     * @return found
     */
    protected boolean getOption(HtmlSelect aSelect, SearchPattern aSearchPattern, int aDistance, WeightedControlList aWeightedControlList) {
        boolean tmpFound = false;
        Iterable<HtmlOption> tmpOptions = aSelect.getOptions();
        for (HtmlOption tmpOption : tmpOptions) {
            if (!tmpOption.isDisabled()) {
                String tmpText = domNodeText.getAsText(tmpOption);
                int tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
                if (tmpCoverage > -1) {
                    aWeightedControlList.add(
                            new HtmlUnitControl(tmpOption),
                            WeightedControlList.FoundType.BY_LABEL,
                            tmpCoverage,
                            aDistance);
                    tmpFound = true;
                }

                tmpText = tmpOption.getLabelAttribute();
                tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
                if (tmpCoverage > -1) {
                    aWeightedControlList.add(
                            new HtmlUnitControl(tmpOption),
                            WeightedControlList.FoundType.BY_LABEL,
                            tmpCoverage,
                            aDistance);
                    tmpFound = true;
                }

                tmpText = tmpOption.getValueAttribute();
                tmpCoverage = aSearchPattern.noOfSurroundingCharsIn(tmpText);
                if (tmpCoverage > -1) {
                    aWeightedControlList.add(
                            new HtmlUnitControl(tmpOption),
                            WeightedControlList.FoundType.BY_LABEL,
                            tmpCoverage,
                            aDistance);
                    tmpFound = true;
                }
            }
        }
        return tmpFound;
    }

    /**
     * returns the body node of the given page
     * @param aHtmlPage the page
     * @return the body node or null if not found
     */
    protected HtmlBody getBody(HtmlPage aHtmlPage) {
        DomNode tmpBodyNode = htmlPage.getFirstChild();
        if (null == tmpBodyNode) {
            LOG.warn("No elements found in html page." );
            return null;
        }

        // search for HTML
        while ((null != tmpBodyNode) && !(tmpBodyNode instanceof HtmlHtml)) {
            tmpBodyNode = tmpBodyNode.getNextSibling();
        }
        if (null == tmpBodyNode) {
            LOG.warn("No <html> element found in html page." );
            return null;
        }

        // search for BODY
        tmpBodyNode = tmpBodyNode.getFirstChild();
        while ((null != tmpBodyNode) && !(tmpBodyNode instanceof HtmlBody)) {
            tmpBodyNode = tmpBodyNode.getNextSibling();
        }
        if (null == tmpBodyNode) {
            LOG.warn("No <body> element found in html page." );
            return null;
        }
        return (HtmlBody)tmpBodyNode;
    }

}