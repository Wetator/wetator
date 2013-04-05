/*
 * Copyright (c) 2008-2013 wetator.org
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


package org.wetator.backend.htmlunit.util;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
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
 * @author rbri
 */
public final class HtmlElementUtil {

  /**
   * Private constructor; this util has only static methods.
   */
  private HtmlElementUtil() {
    super();
  }

  /**
   * Generates a describing text for the {@link HtmlAnchor}.
   * 
   * @param anHtmlAnchor the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlAnchor(final HtmlAnchor anHtmlAnchor) {
    final StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlAnchor");

    // TODO this handles only the most common situations
    if (anHtmlAnchor.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" 'image: ");
      tmpResult.append(((HtmlImage) anHtmlAnchor.getFirstChild()).getSrcAttribute());
      tmpResult.append('\'');
    }

    final String tmpText = anHtmlAnchor.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '");
      tmpResult.append(tmpText);
      tmpResult.append('\'');
    }

    addId(tmpResult, anHtmlAnchor);
    addName(tmpResult, anHtmlAnchor);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlButton.
   * 
   * @param anHtmlButton the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlButton(final HtmlButton anHtmlButton) {
    final StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlButton");

    // TODO this handles only the most common situations
    if (anHtmlButton.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" 'image: ");
      tmpResult.append(((HtmlImage) anHtmlButton.getFirstChild()).getSrcAttribute());
      tmpResult.append('\'');
    }
    if (StringUtils.isNotEmpty(anHtmlButton.asText())) {
      tmpResult.append(" '");
      tmpResult.append(anHtmlButton.asText());
      tmpResult.append('\'');
    } else if (StringUtils.isNotEmpty(anHtmlButton.getValueAttribute())) {
      tmpResult.append(" '");
      tmpResult.append(anHtmlButton.getValueAttribute());
      tmpResult.append('\'');
    }

    addId(tmpResult, anHtmlButton);
    addName(tmpResult, anHtmlButton);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlButtonInput.
   * 
   * @param anHtmlButtonInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlButtonInput(final HtmlButtonInput anHtmlButtonInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlButtonInput '");
    tmpResult.append(anHtmlButtonInput.getValueAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlButtonInput);
    addName(tmpResult, anHtmlButtonInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlCheckBoxInput.
   * 
   * @param anHtmlCheckBoxInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlCheckBoxInput(final HtmlCheckBoxInput anHtmlCheckBoxInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlCheckBoxInput");

    addId(tmpResult, anHtmlCheckBoxInput);
    addName(tmpResult, anHtmlCheckBoxInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlFileInput.
   * 
   * @param anHtmlFileInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlFileInput(final HtmlFileInput anHtmlFileInput) {
    final StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlFileInput");

    addId(tmpResult, anHtmlFileInput);
    addName(tmpResult, anHtmlFileInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlHiddenInput.
   * 
   * @param anHtmlHiddenInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlHiddenInput(final HtmlHiddenInput anHtmlHiddenInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlHiddenInput");

    addId(tmpResult, anHtmlHiddenInput);
    addName(tmpResult, anHtmlHiddenInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlImage.
   * 
   * @param anHtmlImage the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlImage(final HtmlImage anHtmlImage) {
    final StringBuilder tmpResult = new StringBuilder();

    tmpResult.append("[HtmlImage '");
    tmpResult.append(anHtmlImage.getSrcAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlImage);
    addName(tmpResult, anHtmlImage);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlImageInput.
   * 
   * @param anHtmlImageInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlImageInput(final HtmlImageInput anHtmlImageInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlImageInput '");
    tmpResult.append(anHtmlImageInput.getValueAttribute());
    tmpResult.append("\' (src='");
    tmpResult.append(anHtmlImageInput.getSrcAttribute());
    tmpResult.append("')");

    addId(tmpResult, anHtmlImageInput);
    addName(tmpResult, anHtmlImageInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlParagraph.
   * 
   * @param anHtmlParagraph the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlParagraph(final HtmlParagraph anHtmlParagraph) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlParagraph");

    final String tmpText = anHtmlParagraph.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '");
      tmpResult.append(tmpText);
      tmpResult.append('\'');
    }

    addId(tmpResult, anHtmlParagraph);
    addName(tmpResult, anHtmlParagraph);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlPasswordInput.
   * 
   * @param anHtmlPasswordInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlPasswordInput(final HtmlPasswordInput anHtmlPasswordInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlPasswordInput");

    addId(tmpResult, anHtmlPasswordInput);
    addName(tmpResult, anHtmlPasswordInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlRadioButtonInput.
   * 
   * @param anHtmlRadioButtonInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlRadioButtonInput(final HtmlRadioButtonInput anHtmlRadioButtonInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlRadioButtonInput '");
    tmpResult.append(anHtmlRadioButtonInput.getValueAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlRadioButtonInput);
    addName(tmpResult, anHtmlRadioButtonInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlResetInput.
   * 
   * @param anHtmlResetInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlResetInput(final HtmlResetInput anHtmlResetInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlResetInput '");
    tmpResult.append(anHtmlResetInput.getValueAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlResetInput);
    addName(tmpResult, anHtmlResetInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlSelect.
   * 
   * @param anHtmlSelect the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSelect(final HtmlSelect anHtmlSelect) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlSelect");

    addId(tmpResult, anHtmlSelect);
    addName(tmpResult, anHtmlSelect);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlSpan.
   * 
   * @param anHtmlSpan the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSpan(final HtmlSpan anHtmlSpan) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlSpan '");
    tmpResult.append(anHtmlSpan.asText());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlSpan);
    addName(tmpResult, anHtmlSpan);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlSubmitInput.
   * 
   * @param anHtmlSubmitInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSubmitInput(final HtmlSubmitInput anHtmlSubmitInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlSubmitInput '");
    tmpResult.append(anHtmlSubmitInput.getValueAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlSubmitInput);
    addName(tmpResult, anHtmlSubmitInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlTextArea.
   * 
   * @param anHtmlTextArea the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlTextArea(final HtmlTextArea anHtmlTextArea) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlTextArea");

    addId(tmpResult, anHtmlTextArea);
    addName(tmpResult, anHtmlTextArea);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlTextInput.
   * 
   * @param anHtmlTextInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlTextInput(final HtmlTextInput anHtmlTextInput) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlTextInput");

    addId(tmpResult, anHtmlTextInput);
    addName(tmpResult, anHtmlTextInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlOption.
   * 
   * @param anHtmlOption the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlOption(final HtmlOption anHtmlOption) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlOption '");

    tmpResult.append(anHtmlOption.asText());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlOption);
    addName(tmpResult, anHtmlOption);

    final HtmlSelect tmpSelect = anHtmlOption.getEnclosingSelect();
    if (null != tmpSelect) {
      tmpResult.append(" part of ");
      tmpResult.append(getDescribingTextForHtmlSelect(tmpSelect));
    }

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlOptionGroup.
   * 
   * @param anHtmlOptionGroup the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlOptionGroup(final HtmlOptionGroup anHtmlOptionGroup) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlOptionGroup '");

    tmpResult.append(anHtmlOptionGroup.getLabelAttribute());
    tmpResult.append('\'');

    addId(tmpResult, anHtmlOptionGroup);
    addName(tmpResult, anHtmlOptionGroup);

    // HtmlSelect tmpSelect = anHtmlOptionGroup.getEnclosingSelect();
    final HtmlSelect tmpSelect = (HtmlSelect) anHtmlOptionGroup.getEnclosingElement("select");
    if (null != tmpSelect) {
      tmpResult.append(" part of ");
      tmpResult.append(getDescribingTextForHtmlSelect(tmpSelect));
    }

    tmpResult.append(']');
    return tmpResult.toString();
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpId = anHtmlElement.getAttribute("id");
    if (StringUtils.isNotEmpty(tmpId)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpName = anHtmlElement.getAttribute("name");
    if (StringUtils.isNotEmpty(tmpName)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
  }
}