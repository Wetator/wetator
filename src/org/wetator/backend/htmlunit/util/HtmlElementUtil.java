/*
 * Copyright (c) 2008-2020 wetator.org
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
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlOptionGroup;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
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
    final DescribingTextBuilder tmpBuilder = DescribingTextBuilder.createCustom(anHtmlAnchor);

    // TODO this handles only the most common situations
    if (anHtmlAnchor.getFirstChild() instanceof HtmlImage) {
      tmpBuilder.addPlain("'image: " + ((HtmlImage) anHtmlAnchor.getFirstChild()).getSrcAttribute() + "'");
    }

    final String tmpText = anHtmlAnchor.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    return tmpBuilder.addId().addName().build();
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

    final String tmpText = anHtmlButton.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    } else if (StringUtils.isNotEmpty(anHtmlButton.getValueAttribute())) {
      tmpBuilder.addText(anHtmlButton.getValueAttribute());
    }

    return tmpBuilder.addId().addName().build();
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
        .addText(anHtmlButtonInput.getValueAttribute())
        .addId().addName()
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
        .addId().addName()
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
        .addText(anHtmlImageInput.getValueAttribute())
        .addAttribute("src", anHtmlImageInput.getSrcAttribute())
        .addId().addName()
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

    final String tmpText = anHtmlLabel.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    tmpBuilder.addId().addName();

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

    final String tmpText = anHtmlParagraph.asText();
    if (StringUtils.isNotEmpty(tmpText)) {
      tmpBuilder.addText(tmpText);
    }

    return tmpBuilder.addId().addName().build();
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
        .addText(anHtmlRadioButtonInput.getValueAttribute())
        .addId().addName()
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
        .addText(anHtmlResetInput.getValueAttribute())
        .addId().addName()
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
        .addText(anHtmlSpan.asText())
        .addId().addName()
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
        .addText(anHtmlSubmitInput.getValueAttribute())
        .addId().addName()
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
        .addText(anHtmlOption.asText())
        .addId().addName();
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
        .addId().addName();
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
}