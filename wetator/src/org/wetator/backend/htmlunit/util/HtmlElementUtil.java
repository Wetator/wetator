/*
 * Copyright (c) 2008-2018 wetator.org
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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAbbreviated;
import com.gargoylesoftware.htmlunit.html.HtmlAcronym;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBig;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlCitation;
import com.gargoylesoftware.htmlunit.html.HtmlCode;
import com.gargoylesoftware.htmlunit.html.HtmlDefinition;
import com.gargoylesoftware.htmlunit.html.HtmlDeletedText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlEmphasis;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInlineQuotation;
import com.gargoylesoftware.htmlunit.html.HtmlInsertedText;
import com.gargoylesoftware.htmlunit.html.HtmlItalic;
import com.gargoylesoftware.htmlunit.html.HtmlKeyboard;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlPreformattedText;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSample;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSmall;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlStrong;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubscript;
import com.gargoylesoftware.htmlunit.html.HtmlSuperscript;
import com.gargoylesoftware.htmlunit.html.HtmlTeletype;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlVariable;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLLinkElement;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLOptionElement;

import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;

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
    final StringBuilder tmpResult = new StringBuilder("[HtmlAnchor");

    // TODO this handles only the most common situations
    if (anHtmlAnchor.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" 'image: ").append(((HtmlImage) anHtmlAnchor.getFirstChild()).getSrcAttribute()).append('\'');
    }

    final String tmpText = anHtmlAnchor.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '").append(tmpText).append('\'');
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
    final StringBuilder tmpResult = new StringBuilder("[HtmlButton");

    // TODO this handles only the most common situations
    if (anHtmlButton.getFirstChild() instanceof HtmlImage) {
      tmpResult.append(" 'image: ").append(((HtmlImage) anHtmlButton.getFirstChild()).getSrcAttribute()).append('\'');
    }
    if (StringUtils.isNotEmpty(anHtmlButton.asText())) {
      tmpResult.append(" '").append(anHtmlButton.asText()).append('\'');
    } else if (StringUtils.isNotEmpty(anHtmlButton.getValueAttribute())) {
      tmpResult.append(" '").append(anHtmlButton.getValueAttribute()).append('\'');
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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlButtonInput '")
        .append(anHtmlButtonInput.getValueAttribute())
        .append('\'');
    // @formatter:on

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
    final StringBuilder tmpResult = new StringBuilder("[HtmlFileInput");

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlImage '")
        .append(anHtmlImage.getSrcAttribute())
        .append('\'');
    // @formatter:on

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlImageInput '")
        .append(anHtmlImageInput.getValueAttribute())
        .append("\' (src='")
        .append(anHtmlImageInput.getSrcAttribute())
        .append("')");
    // @formatter:on

    addId(tmpResult, anHtmlImageInput);
    addName(tmpResult, anHtmlImageInput);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  /**
   * Generates a describing text for the HtmlLabel.
   *
   * @param anHtmlLabel the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlLabel(final HtmlLabel anHtmlLabel) {
    final StringBuilder tmpResult = new StringBuilder("[HtmlLabel");

    final String tmpText = anHtmlLabel.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpResult.append(" '").append(tmpText).append('\'');
    }

    addId(tmpResult, anHtmlLabel);
    addName(tmpResult, anHtmlLabel);

    final String tmpFor = anHtmlLabel.getForAttribute();
    if (StringUtils.isNotEmpty(tmpFor)) {
      tmpResult.append(" for='");
      tmpResult.append(tmpFor);
      tmpResult.append('\'');
    }

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
      tmpResult.append(" '").append(tmpText).append('\'');
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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlRadioButtonInput '")
        .append(anHtmlRadioButtonInput.getValueAttribute())
        .append('\'');
    // @formatter:on

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlResetInput '")
        .append(anHtmlResetInput.getValueAttribute())
        .append('\'');
    // @formatter:on

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlSpan '")
        .append(anHtmlSpan.asText())
        .append('\'');
    // @formatter:on

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlSubmitInput '")
        .append(anHtmlSubmitInput.getValueAttribute())
        .append('\'');
    // @formatter:on

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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlOption '")
        .append(anHtmlOption.asText())
        .append('\'');
    // @formatter:on

    addId(tmpResult, anHtmlOption);
    addName(tmpResult, anHtmlOption);

    final HtmlSelect tmpSelect = anHtmlOption.getEnclosingSelect();
    if (null != tmpSelect) {
      tmpResult.append(" part of ").append(getDescribingTextForHtmlSelect(tmpSelect));
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
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[HtmlOptionGroup '")
        .append(anHtmlOptionGroup.getLabelAttribute())
        .append('\'');
    // @formatter:on

    addId(tmpResult, anHtmlOptionGroup);
    addName(tmpResult, anHtmlOptionGroup);

    // HtmlSelect tmpSelect = anHtmlOptionGroup.getEnclosingSelect();
    final HtmlSelect tmpSelect = (HtmlSelect) anHtmlOptionGroup.getEnclosingElement("select");
    if (null != tmpSelect) {
      tmpResult.append(" part of ").append(getDescribingTextForHtmlSelect(tmpSelect));
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

  /**
   * Returns true, if the provided dom node has display block.
   * This respects the default for the various tags and additionally
   * checks for css definitions that might overrule this.
   *
   * @param aDomNode the node
   * @return true or false
   */
  public static boolean isBlock(final DomNode aDomNode) {
    final Page tmpPage = aDomNode.getPage();
    if (tmpPage instanceof HtmlPage && tmpPage.getEnclosingWindow().getWebClient().getOptions().isCssEnabled()) {
      ScriptableObject tmpScriptableObject = null;

      int i = 0;
      while (tmpScriptableObject == null) {
        try {
          tmpScriptableObject = aDomNode.getScriptableObject();
        } catch (final IllegalStateException e) {
          // it is possible, that we address some object under construction
          // in this case this might happen, so we will do a second attempt
          if (i > 1) {
            throw e;
          }

          try {
            Thread.sleep(42);
          } catch (final InterruptedException eX) {
            // ignore
          }
        }

        i++;
      }

      if (tmpScriptableObject instanceof HTMLElement) {
        final HTMLElement tmpElement = (HTMLElement) tmpScriptableObject;
        final CSSStyleDeclaration tmpStyle = tmpElement.getCurrentStyle();
        if (tmpStyle != null) {
          final String tmpDisplay = tmpStyle.getDisplay();
          if ("block".equals(tmpDisplay) || "inline-block".equals(tmpDisplay) || "list-item".equals(tmpDisplay)
              || "flex".equals(tmpDisplay)) {
            return true;
          }
          if (tmpDisplay != null && (tmpDisplay.startsWith("table") || "inline-table".equals(tmpDisplay))) {
            return true;
          }
        }

        // we like to write this in separate lines
        if (tmpElement instanceof HTMLLinkElement) {
          return true;
        }

        // ie fix; ie marks option elements as inline
        // let's hope no browser will ever support inline rendering of options in a select
        if (tmpElement instanceof HTMLOptionElement) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Returns true, if the provided dom node is a format Element.
   *
   * @param aDomNode the node
   * @return true or false
   */
  public static boolean isFormatElement(final DomNode aDomNode) {
    // @formatter:off
    return aDomNode instanceof HtmlItalic
        || aDomNode instanceof HtmlBold
        || aDomNode instanceof HtmlBig
        || aDomNode instanceof HtmlEmphasis
        || aDomNode instanceof HtmlSmall
        || aDomNode instanceof HtmlStrong
        || aDomNode instanceof HtmlSubscript
        || aDomNode instanceof HtmlSuperscript
        || aDomNode instanceof HtmlInsertedText
        || aDomNode instanceof HtmlDeletedText
        || aDomNode instanceof HtmlCode
        || aDomNode instanceof HtmlKeyboard
        || aDomNode instanceof HtmlSample
        || aDomNode instanceof HtmlPreformattedText
        || aDomNode instanceof HtmlTeletype
        || aDomNode instanceof HtmlVariable
        || aDomNode instanceof HtmlAbbreviated
        || aDomNode instanceof HtmlInlineQuotation
        || aDomNode instanceof HtmlCitation
        || aDomNode instanceof HtmlAcronym
        || aDomNode instanceof HtmlDefinition;
    // @formatter:on
  }
}