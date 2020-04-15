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


package org.wetator.backend.htmlunit.finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for HTML code of select elements.<br>
 * <br>
 * Adds listeners defined in {@link HtmlCodeCreator} for all select elements per default. Use {@link #noListen()} to
 * avoid.
 *
 * @author tobwoerk
 */
public final class HtmlCodeSelectBuilder {

  private String id;
  private String style;
  private String name;
  private List<SelectOption> options = new ArrayList<>();
  private SelectOption currentOption;

  private boolean listen = true;

  private class SelectOption {
    private String optionId;
    private String optionStyle;
    private String content;

    private boolean optionListen = true;

    SelectOption(final String anOptionId) {
      optionId = anOptionId;
    }

    SelectOption(final String anOptionId, final String aContent) {
      this(anOptionId);
      content = aContent;
    }
  }

  private HtmlCodeSelectBuilder(final String aTableId) {
    id = aTableId;
  }

  public static HtmlCodeSelectBuilder select(final String aSelectId) {
    return new HtmlCodeSelectBuilder(aSelectId);
  }

  public static HtmlCodeSelectBuilder select(final String aSelectId, final String aName) {
    final HtmlCodeSelectBuilder tmpBuilder = select(aSelectId);
    tmpBuilder.name = aName;
    return tmpBuilder;
  }

  public HtmlCodeSelectBuilder option(final String anOptionId) {
    currentOption = new SelectOption(anOptionId);
    options.add(currentOption);
    return this;
  }

  public HtmlCodeSelectBuilder option(final String anOptionId, final String aContent) {
    currentOption = new SelectOption(anOptionId, aContent);
    options.add(currentOption);
    return this;
  }

  public HtmlCodeSelectBuilder style(final String aStyle) {
    if (currentOption == null) {
      style = aStyle;
    } else {
      currentOption.optionStyle = aStyle;
    }
    return this;
  }

  public HtmlCodeSelectBuilder noListen() {
    if (currentOption == null) {
      listen = false;
    } else {
      currentOption.optionListen = false;
    }
    return this;
  }

  public String build() {
    final StringBuilder tmpSelectHtml = new StringBuilder(HtmlCodeCreator.selectStart(id, name, style, listen));
    for (SelectOption tmpOption : options) {
      tmpSelectHtml.append(HtmlCodeCreator.selectOption(id, tmpOption.optionId, tmpOption.content,
          tmpOption.optionStyle, tmpOption.optionListen));
    }
    tmpSelectHtml.append(HtmlCodeCreator.selectEnd());
    return tmpSelectHtml.toString();
  }

  @Override
  public String toString() {
    return build();
  }
}
