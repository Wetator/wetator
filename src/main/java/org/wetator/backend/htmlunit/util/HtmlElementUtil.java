/*
 * Copyright (c) 2008-2025 wetator.org
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
import org.htmlunit.Page;
import org.htmlunit.css.ComputedCssStyleDeclaration;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlButtonInput;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlEmailInput;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlHiddenInput;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlImageInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlLink;
import org.htmlunit.html.HtmlNumberInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlOptionGroup;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlParagraph;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlResetInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSpan;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTelInput;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.htmlunit.html.HtmlUrlInput;

/**
 * Helper methods to work with the HtmlElements page.
 *
 * @author rbri
 * @author fdanek
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
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlAnchor);

    // TODO this handles only the most common situations
    if (anHtmlAnchor.getFirstChild() instanceof HtmlImage) {
      tmpBuilder.addPlain("'image: " + ((HtmlImage) anHtmlAnchor.getFirstChild()).getSrcAttribute() + "'");
    }

    final String tmpText = anHtmlAnchor.asNormalizedText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    return tmpBuilder.addId().addName().addDataTestid().build();
  }

  /**
   * Generates a describing text for the {@link HtmlBody}.
   *
   * @param anHtmlBody the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlBody(final HtmlBody anHtmlBody) {
    return DescribingTextBuilder.createDefault(anHtmlBody).build();
  }

  /**
   * Generates a describing text for the {@link HtmlButton}.
   *
   * @param anHtmlButton the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlButton(final HtmlButton anHtmlButton) {
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlButton);

    // TODO this handles only the most common situations
    if (anHtmlButton.getFirstChild() instanceof HtmlImage) {
      tmpBuilder.addPlain("'image: " + ((HtmlImage) anHtmlButton.getFirstChild()).getSrcAttribute() + "'");
    }

    final String tmpText = anHtmlButton.asNormalizedText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    } else if (StringUtils.isNotEmpty(anHtmlButton.getValueAttribute())) {
      tmpBuilder.addText(anHtmlButton.getValueAttribute());
    }

    return tmpBuilder.addId().addName().addDataTestid().build();
  }

  /**
   * Generates a describing text for the {@link HtmlButtonInput}.
   *
   * @param anHtmlButtonInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlButtonInput(final HtmlButtonInput anHtmlButtonInput) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlButtonInput)
        .addText(anHtmlButtonInput.getValue())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlCheckBoxInput}.
   *
   * @param anHtmlCheckBoxInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlCheckBoxInput(final HtmlCheckBoxInput anHtmlCheckBoxInput) {
    return DescribingTextBuilder.createDefault(anHtmlCheckBoxInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlFileInput}.
   *
   * @param anHtmlFileInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlFileInput(final HtmlFileInput anHtmlFileInput) {
    return DescribingTextBuilder.createDefault(anHtmlFileInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlHiddenInput}.
   *
   * @param anHtmlHiddenInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlHiddenInput(final HtmlHiddenInput anHtmlHiddenInput) {
    return DescribingTextBuilder.createDefault(anHtmlHiddenInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlImage}.
   *
   * @param anHtmlImage the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlImage(final HtmlImage anHtmlImage) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlImage)
        .addText(anHtmlImage.getSrcAttribute())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlImageInput}.
   *
   * @param anHtmlImageInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlImageInput(final HtmlImageInput anHtmlImageInput) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlImageInput)
        .addText(anHtmlImageInput.getValue())
        .addAttribute("src", anHtmlImageInput.getSrcAttribute())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlLabel}.
   *
   * @param anHtmlLabel the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlLabel(final HtmlLabel anHtmlLabel) {
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlLabel);

    final String tmpText = anHtmlLabel.asNormalizedText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    tmpBuilder.addId().addName().addDataTestid();

    final String tmpFor = anHtmlLabel.getForAttribute();
    if (StringUtils.isNotEmpty(tmpFor)) {
      tmpBuilder.addAttribute("for", tmpFor);
    }

    return tmpBuilder.build();
  }

  /**
   * Generates a describing text for the {@link HtmlParagraph}.
   *
   * @param anHtmlParagraph the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlParagraph(final HtmlParagraph anHtmlParagraph) {
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlParagraph);

    final String tmpText = anHtmlParagraph.asNormalizedText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    return tmpBuilder.addId().addName().addDataTestid().build();
  }

  /**
   * Generates a describing text for the {@link HtmlPasswordInput}.
   *
   * @param anHtmlPasswordInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlPasswordInput(final HtmlPasswordInput anHtmlPasswordInput) {
    return DescribingTextBuilder.createDefault(anHtmlPasswordInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlRadioButtonInput}.
   *
   * @param anHtmlRadioButtonInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlRadioButtonInput(final HtmlRadioButtonInput anHtmlRadioButtonInput) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlRadioButtonInput)
        .addText(anHtmlRadioButtonInput.getValue())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlResetInput}.
   *
   * @param anHtmlResetInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlResetInput(final HtmlResetInput anHtmlResetInput) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlResetInput)
        .addText(anHtmlResetInput.getValue())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlSelect}.
   *
   * @param anHtmlSelect the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSelect(final HtmlSelect anHtmlSelect) {
    return DescribingTextBuilder.createDefault(anHtmlSelect).build();
  }

  /**
   * Generates a describing text for the {@link HtmlSpan}.
   *
   * @param anHtmlSpan the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSpan(final HtmlSpan anHtmlSpan) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlSpan)
        .addText(anHtmlSpan.asNormalizedText())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlSubmitInput}.
   *
   * @param anHtmlSubmitInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlSubmitInput(final HtmlSubmitInput anHtmlSubmitInput) {
    // @formatter:off
    return DescribingTextBuilder.createCustom(anHtmlSubmitInput)
        .addText(anHtmlSubmitInput.getValue())
        .addId().addName().addDataTestid()
        .build();
    // @formatter:on
  }

  /**
   * Generates a describing text for the {@link HtmlTextArea}.
   *
   * @param anHtmlTextArea the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlTextArea(final HtmlTextArea anHtmlTextArea) {
    return DescribingTextBuilder.createDefault(anHtmlTextArea).build();
  }

  /**
   * Generates a describing text for the {@link HtmlEmailInput}.
   *
   * @param anHtmlEmailInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlEmailInput(final HtmlEmailInput anHtmlEmailInput) {
    return DescribingTextBuilder.createDefault(anHtmlEmailInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlNumberInput}.
   *
   * @param anHtmlNumberInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlNumberInput(final HtmlNumberInput anHtmlNumberInput) {
    return DescribingTextBuilder.createDefault(anHtmlNumberInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlTelInput}.
   *
   * @param anHtmlTelInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlTelInput(final HtmlTelInput anHtmlTelInput) {
    return DescribingTextBuilder.createDefault(anHtmlTelInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlUrlInput}.
   *
   * @param anHtmlUrlInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlUrlInput(final HtmlUrlInput anHtmlUrlInput) {
    return DescribingTextBuilder.createDefault(anHtmlUrlInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlTextInput}.
   *
   * @param anHtmlTextInput the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlTextInput(final HtmlTextInput anHtmlTextInput) {
    return DescribingTextBuilder.createDefault(anHtmlTextInput).build();
  }

  /**
   * Generates a describing text for the {@link HtmlOption}.
   *
   * @param anHtmlOption the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlOption(final HtmlOption anHtmlOption) {
    // @formatter:off
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlOption)
        .addText(anHtmlOption.asNormalizedText())
        .addId().addName().addDataTestid();
    // @formatter:on

    final HtmlSelect tmpSelect = anHtmlOption.getEnclosingSelect();
    if (null != tmpSelect) {
      tmpBuilder.addPlain("part of " + getDescribingTextForHtmlSelect(tmpSelect));
    }

    return tmpBuilder.build();
  }

  /**
   * Generates a describing text for the {@link HtmlOptionGroup}.
   *
   * @param anHtmlOptionGroup the control
   * @return the describing text
   */
  public static String getDescribingTextForHtmlOptionGroup(final HtmlOptionGroup anHtmlOptionGroup) {
    // @formatter:off
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlOptionGroup)
        .addText(anHtmlOptionGroup.getLabelAttribute())
        .addId().addName().addDataTestid();
    // @formatter:on

    final HtmlSelect tmpSelect = anHtmlOptionGroup.getEnclosingSelect();
    if (null != tmpSelect) {
      tmpBuilder.addPlain("part of " + getDescribingTextForHtmlSelect(tmpSelect));
    }

    return tmpBuilder.build();
  }

  /**
   * Returns <code>true</code>, if the provided {@link DomNode} has display block.
   * This respects the default for the various tags and additionally
   * checks for CSS definitions that might overrule this.
   *
   * @param aDomNode the node
   * @return <code>true</code> if the given {@link DomNode} has display block
   */
  public static boolean isBlock(final DomNode aDomNode) {
    final Page tmpPage = aDomNode.getPage();
    if (tmpPage instanceof HtmlPage && tmpPage.getEnclosingWindow().getWebClient().getOptions().isCssEnabled()) {
      if (aDomNode instanceof HtmlElement) {
        final HtmlElement tmpElement = (HtmlElement) aDomNode;

        // we like to write this in separate lines
        if (tmpElement instanceof HtmlLink) {
          return true;
        }

        // ie fix; ie marks option elements as inline
        // let's hope no browser will ever support inline rendering of options in a select
        if (tmpElement instanceof HtmlOption) {
          return true;
        }

        final ComputedCssStyleDeclaration tmpStyle = tmpPage.getEnclosingWindow().getComputedStyle(tmpElement, null);
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
      }
    }

    return false;
  }
}