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
 * Adds <code>onclick</code>-event listeners for all select elements per default. Use {@link #noListen()} to avoid.
 *
 * @author tobwoerk
 */
public final class MouseActionHtmlCodeSelectBuilder {

  private String selectId;
  private List<SelectOption> options = new ArrayList<>();

  private boolean listenSelect = true;

  private class SelectOption {
    private String optionId;
    private String content;

    private boolean listenOption = true;

    SelectOption(final String anOptionId) {
      optionId = anOptionId;
    }

    SelectOption(final String anOptionId, final String aContent) {
      this(anOptionId);
      content = aContent;
    }
  }

  private MouseActionHtmlCodeSelectBuilder(final String aTableId) {
    selectId = aTableId;
  }

  public static MouseActionHtmlCodeSelectBuilder select(final String aSelectId) {
    return new MouseActionHtmlCodeSelectBuilder(aSelectId);
  }

  public MouseActionHtmlCodeSelectBuilder option(final String anOptionId) {
    options.add(new SelectOption(anOptionId));
    return this;
  }

  public MouseActionHtmlCodeSelectBuilder option(final String anOptionId, final String aContent) {
    options.add(new SelectOption(anOptionId, aContent));
    return this;
  }

  public MouseActionHtmlCodeSelectBuilder noListen() {
    if (options.isEmpty()) {
      listenSelect = false;
    } else {
      options.get(options.size() - 1).listenOption = false;
    }
    return this;
  }

  public String build() {
    final StringBuilder tmpSelectHtml = new StringBuilder(
        MouseActionHtmlCodeCreator.selectStart(selectId, listenSelect));
    for (SelectOption tmpOption : options) {
      tmpSelectHtml.append(MouseActionHtmlCodeCreator.selectOption(selectId, tmpOption.optionId, tmpOption.content,
          tmpOption.listenOption));
    }
    tmpSelectHtml.append(MouseActionHtmlCodeCreator.selectEnd());
    return tmpSelectHtml.toString();
  }

  @Override
  public String toString() {
    return build();
  }
}
