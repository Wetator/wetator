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

import org.wetator.backend.control.IClickable;

/**
 * Builder for HTML code of clickable elements.<br>
 * <br>
 * Adds <code>onclick</code>-event listeners for non-{@link IClickable}s
 * per default. Use {@link #noListen()} to avoid.
 *
 * @author tobwoerk
 */
public class MouseActionHtmlCodeBuilder {

  private ElementType elementType;
  private boolean listen = true;

  private String id;
  private String content;

  private enum ElementType {
    ANCHOR,
    BUTTON,
    CHECKBOX,
    DIV,
    IMAGE,
    INPUT_TEXT,
    LABEL,
    RADIO,
    SPAN
  }

  public static MouseActionHtmlCodeBuilder a(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return a(anId, aContent.build());
  }

  public static MouseActionHtmlCodeBuilder a(final String anId, final String aContent) {
    return a(anId).contain(aContent);
  }

  public static MouseActionHtmlCodeBuilder a(final String anId) {
    return init(ElementType.ANCHOR, anId);
  }

  public static MouseActionHtmlCodeBuilder button(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return button(anId, aContent.build());
  }

  public static MouseActionHtmlCodeBuilder button(final String anId, final String aContent) {
    return button(anId).contain(aContent);
  }

  public static MouseActionHtmlCodeBuilder button(final String anId) {
    return init(ElementType.BUTTON, anId);
  }

  public static MouseActionHtmlCodeBuilder checkbox(final String anId) {
    return init(ElementType.CHECKBOX, anId);
  }

  public static MouseActionHtmlCodeBuilder div(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return div(anId, aContent.build());
  }

  public static MouseActionHtmlCodeBuilder div(final String anId, final String aContent) {
    return div(anId).contain(aContent);
  }

  public static MouseActionHtmlCodeBuilder div(final String anId) {
    return init(ElementType.DIV, anId);
  }

  public static MouseActionHtmlCodeBuilder image(final String anId, final String anAltText) {
    return image(anId).contain(anAltText);
  }

  public static MouseActionHtmlCodeBuilder image(final String anId) {
    return init(ElementType.IMAGE, anId);
  }

  public static MouseActionHtmlCodeBuilder inputText(final String anId, final String aPlaceholder) {
    return inputText(anId).contain(aPlaceholder);
  }

  public static MouseActionHtmlCodeBuilder inputText(final String anId) {
    return init(ElementType.INPUT_TEXT, anId);
  }

  public static MouseActionHtmlCodeBuilder label(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return label(anId, aContent.build());
  }

  public static MouseActionHtmlCodeBuilder label(final String anId, final String aContent) {
    return label(anId).contain(aContent);
  }

  public static MouseActionHtmlCodeBuilder label(final String anId) {
    return init(ElementType.LABEL, anId);
  }

  public static MouseActionHtmlCodeBuilder radio(final String anId) {
    return init(ElementType.RADIO, anId);
  }

  public static MouseActionHtmlCodeBuilder span(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return span(anId, aContent.build());
  }

  public static MouseActionHtmlCodeBuilder span(final String anId, final String aContent) {
    return span(anId).contain(aContent);
  }

  public static MouseActionHtmlCodeBuilder span(final String anId) {
    return init(ElementType.SPAN, anId);
  }

  public MouseActionHtmlCodeBuilder noListen() {
    listen = false;
    return this;
  }

  private static MouseActionHtmlCodeBuilder init(final ElementType anElementType, final String anId) {
    final MouseActionHtmlCodeBuilder tmpBuilder = new MouseActionHtmlCodeBuilder();
    tmpBuilder.elementType = anElementType;
    tmpBuilder.id = anId;
    return tmpBuilder;
  }

  private MouseActionHtmlCodeBuilder contain(final String aContent) {
    content = aContent;
    return this;
  }

  public String build() {
    switch (elementType) {
      case ANCHOR:
        return MouseActionHtmlCodeCreator.a(id, content);
      case BUTTON:
        return MouseActionHtmlCodeCreator.button(id, content);
      case CHECKBOX:
        return MouseActionHtmlCodeCreator.checkbox(id, listen);
      case DIV:
        final StringBuilder tmpDivHtml = new StringBuilder();
        tmpDivHtml.append(MouseActionHtmlCodeCreator.divStart(id, listen));
        if (content != null) {
          tmpDivHtml.append(content);
        }
        tmpDivHtml.append(MouseActionHtmlCodeCreator.divEnd());
        return tmpDivHtml.toString();
      case IMAGE:
        return MouseActionHtmlCodeCreator.image(id, content, listen);
      case INPUT_TEXT:
        return MouseActionHtmlCodeCreator.inputText(id, content, listen);
      case LABEL:
        final StringBuilder tmpLabelHtml = new StringBuilder();
        tmpLabelHtml.append(MouseActionHtmlCodeCreator.labelStart(id, listen));
        if (content != null) {
          tmpLabelHtml.append(content);
        }
        tmpLabelHtml.append(MouseActionHtmlCodeCreator.labelEnd());
        return tmpLabelHtml.toString();
      case RADIO:
        return MouseActionHtmlCodeCreator.radio(id, listen);
      case SPAN:
        final StringBuilder tmpSpanHtml = new StringBuilder();
        tmpSpanHtml.append(MouseActionHtmlCodeCreator.spanStart(id, listen));
        if (content != null) {
          tmpSpanHtml.append(content);
        }
        tmpSpanHtml.append(MouseActionHtmlCodeCreator.spanEnd());
        return tmpSpanHtml.toString();
      default:
        throw new RuntimeException();
    }
  }

  @Override
  public String toString() {
    return build();
  }
}
