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

import org.wetator.backend.control.IClickable;

/**
 * Builder for HTML code of clickable elements.<br>
 * <br>
 * Adds <code>onclick</code>-event listeners for non-{@link IClickable}s per default. Use {@link #noListen()} to avoid.
 *
 * @author tobwoerk
 */
public class MouseActionHtmlCodeBuilder {

  private List<Element> elements = new ArrayList<>();

  private class Element {
    private ElementType elementType;

    private String id;
    private String content;

    private boolean listen = true;

    Element(final ElementType anElementType, final String anId) {
      elementType = anElementType;
      id = anId;
    }
  }

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

  public MouseActionHtmlCodeBuilder a(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return a(anId, aContent.build());
  }

  public MouseActionHtmlCodeBuilder a(final String anId, final String aContent) {
    return a(anId).contain(aContent);
  }

  public MouseActionHtmlCodeBuilder a(final String anId) {
    return add(ElementType.ANCHOR, anId);
  }

  public MouseActionHtmlCodeBuilder button(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return button(anId, aContent.build());
  }

  public MouseActionHtmlCodeBuilder button(final String anId, final String aContent) {
    return button(anId).contain(aContent);
  }

  public MouseActionHtmlCodeBuilder button(final String anId) {
    return add(ElementType.BUTTON, anId);
  }

  public MouseActionHtmlCodeBuilder checkbox(final String anId) {
    return add(ElementType.CHECKBOX, anId);
  }

  public MouseActionHtmlCodeBuilder div(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return div(anId, aContent.build());
  }

  public MouseActionHtmlCodeBuilder div(final String anId, final String aContent) {
    return div(anId).contain(aContent);
  }

  public MouseActionHtmlCodeBuilder div(final String anId) {
    return add(ElementType.DIV, anId);
  }

  public MouseActionHtmlCodeBuilder image(final String anId, final String anAltText) {
    return image(anId).contain(anAltText);
  }

  public MouseActionHtmlCodeBuilder image(final String anId) {
    return add(ElementType.IMAGE, anId);
  }

  public MouseActionHtmlCodeBuilder inputText(final String anId, final String aPlaceholder) {
    return inputText(anId).contain(aPlaceholder);
  }

  public MouseActionHtmlCodeBuilder inputText(final String anId) {
    return add(ElementType.INPUT_TEXT, anId);
  }

  public MouseActionHtmlCodeBuilder label(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return label(anId, aContent.build());
  }

  public MouseActionHtmlCodeBuilder label(final String anId, final String aContent) {
    return label(anId).contain(aContent);
  }

  public MouseActionHtmlCodeBuilder label(final String anId) {
    return add(ElementType.LABEL, anId);
  }

  public MouseActionHtmlCodeBuilder radio(final String anId) {
    return add(ElementType.RADIO, anId);
  }

  public MouseActionHtmlCodeBuilder span(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return span(anId, aContent.build());
  }

  public MouseActionHtmlCodeBuilder span(final String anId, final String aContent) {
    return span(anId).contain(aContent);
  }

  public MouseActionHtmlCodeBuilder span(final String anId) {
    return add(ElementType.SPAN, anId);
  }

  public MouseActionHtmlCodeBuilder noListen() {
    elements.get(elements.size() - 1).listen = false;
    return this;
  }

  private MouseActionHtmlCodeBuilder add(final ElementType anElementType, final String anId) {
    elements.add(new Element(anElementType, anId));
    return this;
  }

  private MouseActionHtmlCodeBuilder contain(final String aContent) {
    elements.get(elements.size() - 1).content = aContent;
    return this;
  }

  public String build() {
    final StringBuilder tmpHtml = new StringBuilder();

    for (Element tmpElement : elements) {
      switch (tmpElement.elementType) {
        case ANCHOR:
          tmpHtml.append(MouseActionHtmlCodeCreator.a(tmpElement.id, tmpElement.content));
          break;
        case BUTTON:
          tmpHtml.append(MouseActionHtmlCodeCreator.button(tmpElement.id, tmpElement.content));
          break;
        case CHECKBOX:
          tmpHtml.append(MouseActionHtmlCodeCreator.checkbox(tmpElement.id, tmpElement.listen));
          break;
        case DIV:
          tmpHtml.append(MouseActionHtmlCodeCreator.divStart(tmpElement.id, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(MouseActionHtmlCodeCreator.divEnd());
          break;
        case IMAGE:
          tmpHtml.append(MouseActionHtmlCodeCreator.image(tmpElement.id, tmpElement.content, tmpElement.listen));
          break;
        case INPUT_TEXT:
          tmpHtml.append(MouseActionHtmlCodeCreator.inputText(tmpElement.id, tmpElement.content, tmpElement.listen));
          break;
        case LABEL:
          tmpHtml.append(MouseActionHtmlCodeCreator.labelStart(tmpElement.id, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(MouseActionHtmlCodeCreator.labelEnd());
          break;
        case RADIO:
          tmpHtml.append(MouseActionHtmlCodeCreator.radio(tmpElement.id, tmpElement.listen));
          break;
        case SPAN:
          tmpHtml.append(MouseActionHtmlCodeCreator.spanStart(tmpElement.id, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(MouseActionHtmlCodeCreator.spanEnd());
          break;
        default:
          throw new RuntimeException();
      }
    }

    return tmpHtml.toString();
  }

  @Override
  public String toString() {
    return build();
  }
}
