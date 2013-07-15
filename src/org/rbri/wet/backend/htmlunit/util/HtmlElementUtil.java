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


package org.rbri.wet.backend.htmlunit.util;

import org.apache.commons.lang.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


/**
 * Helper methods to work with the HtmlElements page.
 *
 * @author exxws
 */
public class HtmlElementUtil {
    
    /**
     * Private constructor; this util has only static methods
     */
    private HtmlElementUtil() {
        super();
    }

    public static String getDescribingTextForHtmlAnchor(HtmlAnchor aHtmlAnchor) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlAnchor");

        // TODO this handles only the most common situations
        if (aHtmlAnchor.getFirstChild() instanceof HtmlImage) {
            tmpResult.append(" '");
            tmpResult.append("image: ");
            tmpResult.append(((HtmlImage)aHtmlAnchor.getFirstChild()).getSrcAttribute());
            tmpResult.append("'");
        }

        String tmpText = aHtmlAnchor.asText();
        if (StringUtils.isNotEmpty(tmpText)) {
            tmpResult.append(" '");
            tmpResult.append(tmpText);
            tmpResult.append("'");
        }

        addId(tmpResult, aHtmlAnchor);
        addName(tmpResult, aHtmlAnchor);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlButton(HtmlButton aHtmlButton) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlButton");

        // TODO this handles only the most common situations
        if (aHtmlButton.getFirstChild() instanceof HtmlImage) {
            tmpResult.append(" '");
            tmpResult.append("image: ");
            tmpResult.append(((HtmlImage)aHtmlButton.getFirstChild()).getSrcAttribute());
            tmpResult.append("'");
        }
        if (StringUtils.isNotEmpty(aHtmlButton.asText())) {
            tmpResult.append(" '");
            tmpResult.append(aHtmlButton.asText());
            tmpResult.append("'");
        } else if (StringUtils.isNotEmpty(aHtmlButton.getValueAttribute())) {
            tmpResult.append(" '");
            tmpResult.append(aHtmlButton.getValueAttribute());
            tmpResult.append("'");
        }

        addId(tmpResult, aHtmlButton);
        addName(tmpResult, aHtmlButton);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlButtonInput(HtmlButtonInput aHtmlButtonInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlButtonInput '");
        tmpResult.append(aHtmlButtonInput.getValueAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlButtonInput);
        addName(tmpResult, aHtmlButtonInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlCheckBoxInput(HtmlCheckBoxInput aHtmlCheckBoxInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlCheckBoxInput");

        addId(tmpResult, aHtmlCheckBoxInput);
        addName(tmpResult, aHtmlCheckBoxInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlFileInput(HtmlFileInput aHtmlFileInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlFileInput");

        addId(tmpResult, aHtmlFileInput);
        addName(tmpResult, aHtmlFileInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlImage(HtmlImage aHtmlImage) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlImage '");
        tmpResult.append(aHtmlImage.getSrcAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlImage);
        addName(tmpResult, aHtmlImage);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlImageInput(HtmlImageInput aHtmlImageInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlImageInput '");
        tmpResult.append(aHtmlImageInput.getValueAttribute());
        tmpResult.append("'");

        tmpResult.append(" (src='");
        tmpResult.append(aHtmlImageInput.getSrcAttribute());
        tmpResult.append("')");

        addId(tmpResult, aHtmlImageInput);
        addName(tmpResult, aHtmlImageInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlParagraph(HtmlParagraph aHtmlParagraph) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlParagraph");

        String tmpText = aHtmlParagraph.asText();
        if (StringUtils.isNotEmpty(tmpText)) {
            tmpResult.append(" '");
            tmpResult.append(tmpText);
            tmpResult.append("'");
        }

        addId(tmpResult, aHtmlParagraph);
        addName(tmpResult, aHtmlParagraph);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlPasswordInput(HtmlPasswordInput aHtmlPasswordInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlPasswordInput");

        addId(tmpResult, aHtmlPasswordInput);
        addName(tmpResult, aHtmlPasswordInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlRadioButtonInput(HtmlRadioButtonInput aHtmlRadioButtonInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlRadioButtonInput '");
        tmpResult.append(aHtmlRadioButtonInput.getValueAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlRadioButtonInput);
        addName(tmpResult, aHtmlRadioButtonInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlResetInput(HtmlResetInput aHtmlResetInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlResetInput '");
        tmpResult.append(aHtmlResetInput.getValueAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlResetInput);
        addName(tmpResult, aHtmlResetInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlSelect(HtmlSelect aHtmlSelect) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlSelect");

        addId(tmpResult, aHtmlSelect);
        addName(tmpResult, aHtmlSelect);

        tmpResult.append("]");
        return tmpResult.toString();
    }


    public static String getDescribingTextForHtmlSpan(HtmlSpan aHtmlSpan) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlSpan '");
        tmpResult.append(aHtmlSpan.asText());
        tmpResult.append("'");

        addId(tmpResult, aHtmlSpan);
        addName(tmpResult, aHtmlSpan);

        tmpResult.append("]");
        return tmpResult.toString();
    }

    
    public static String getDescribingTextForHtmlSubmitInput(HtmlSubmitInput aHtmlSubmitInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlSubmitInput '");
        tmpResult.append(aHtmlSubmitInput.getValueAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlSubmitInput);
        addName(tmpResult, aHtmlSubmitInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlTextArea(HtmlTextArea aHtmlTextArea) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlTextArea");

        addId(tmpResult, aHtmlTextArea);
        addName(tmpResult, aHtmlTextArea);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlTextInput(HtmlTextInput aHtmlTextInput) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlTextInput");

        addId(tmpResult, aHtmlTextInput);
        addName(tmpResult, aHtmlTextInput);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlOption(HtmlOption aHtmlOption) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlOption '");

        tmpResult.append(aHtmlOption.asText());
        tmpResult.append("'");

        addId(tmpResult, aHtmlOption);
        addName(tmpResult, aHtmlOption);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    public static String getDescribingTextForHtmlOptionGroup(HtmlOptionGroup aHtmlOptionGroup) {
        StringBuilder tmpResult = new StringBuilder();

        tmpResult.append("[HtmlOptionGroup '");

        tmpResult.append(aHtmlOptionGroup.getLabelAttribute());
        tmpResult.append("'");

        addId(tmpResult, aHtmlOptionGroup);
        addName(tmpResult, aHtmlOptionGroup);

        tmpResult.append("]");
        return tmpResult.toString();
    }



    private static void addId(StringBuilder aStringBuilder, HtmlElement aHtmlElement) {
        String tmpId = aHtmlElement.getAttribute("id");
        if ((null != tmpId) && (tmpId.length() > 0)) {
            aStringBuilder.append(" (id='");
            aStringBuilder.append(tmpId);
            aStringBuilder.append("')");
        }
    }


    private static void addName(StringBuilder aStringBuilder, HtmlElement aHtmlElement) {
        String tmpName = aHtmlElement.getAttribute("name");
        if ((null != tmpName) && (tmpName.length() > 0)) {
            aStringBuilder.append(" (name='");
            aStringBuilder.append(tmpName);
            aStringBuilder.append("')");
        }
    }
}