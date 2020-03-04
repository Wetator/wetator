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

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * A builder for describing texts of {@link HtmlElement}s.
 *
 * @author frank.danek
 */
public final class DescribingTextBuilder {

  private HtmlElement htmlElement;
  private StringBuilder describingText = new StringBuilder();

  /**
   * Creates a {@link DescribingTextBuilder} for the given {@link HtmlElement} adding it's id and name.
   *
   * @param anHtmlElement the {@link HtmlElement} to create a describing text for
   * @return a {@link DescribingTextBuilder}
   */
  public static DescribingTextBuilder createDefault(final HtmlElement anHtmlElement) {
    return new DescribingTextBuilder(anHtmlElement).addId().addName();
  }

  /**
   * Creates a {@link DescribingTextBuilder} for the given {@link HtmlElement}.
   *
   * @param anHtmlElement the {@link HtmlElement} to create a describing text for
   * @return a {@link DescribingTextBuilder}
   */
  public static DescribingTextBuilder createCustom(final HtmlElement anHtmlElement) {
    return new DescribingTextBuilder(anHtmlElement);
  }

  private DescribingTextBuilder(final HtmlElement anHtmlElement) {
    htmlElement = anHtmlElement;

    describingText.append('[').append(htmlElement.getClass().getSimpleName());
  }

  /**
   * Adds the {@link HtmlElement}'s id to the describing text.
   *
   * @return this builder
   */
  public DescribingTextBuilder addId() {
    return addAttribute("id", htmlElement.getAttribute("id"));
  }

  /**
   * Adds the {@link HtmlElement}'s name to the describing text.
   *
   * @return this builder
   */
  public DescribingTextBuilder addName() {
    return addAttribute("name", htmlElement.getAttribute("name"));
  }

  /**
   * Adds the given attribute to the describing text.
   *
   * @param aName the attribute name
   * @param aValue the attribute value
   * @return this builder
   */
  public DescribingTextBuilder addAttribute(final String aName, final String aValue) {
    if (StringUtils.isNotBlank(aName) && StringUtils.isNotEmpty(aValue)) {
      describingText.append(" (").append(aName).append("='").append(aValue).append("')");
    }
    return this;
  }

  /**
   * Adds the given text to the describing text.
   *
   * @param aText the text
   * @return this builder
   */
  public DescribingTextBuilder addText(final String aText) {
    describingText.append(" '").append(aText).append('\'');
    return this;
  }

  /**
   * Adds the given text plainly to the describing text.
   *
   * @param aText the text
   * @return this builder
   */
  public DescribingTextBuilder addPlain(final String aText) {
    if (StringUtils.isNotBlank(aText)) {
      describingText.append(' ').append(aText);
    }
    return this;
  }

  /**
   * @return the describing text
   */
  public String build() {
    return describingText.append(']').toString();
  }
}
