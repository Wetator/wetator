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
 * Builder for HTML code.<br>
 * <br>
 * Adds listeners defined in {@link HtmlCodeCreator} for all elements per default. Use {@link #noListen()} to
 * avoid.
 *
 * @author tobwoerk
 * @see HtmlCodeSelectBuilder
 * @see HtmlCodeTableBuilder
 */
public class HtmlCodeBuilder {

  private List<Element> elements = new ArrayList<>();
  private Element currentElement;

  private class Element {
    private ElementType elementType;

    private String id;
    private String alt;
    private String value;
    private String style;
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
    INPUT_BUTTON,
    INPUT_IMAGE,
    INPUT_PASSWORD,
    INPUT_RESET,
    INPUT_SUBMIT,
    INPUT_TEXT,
    LABEL,
    RADIO,
    SPAN
  }

  public HtmlCodeBuilder a(final String anId, final HtmlCodeBuilder aContent) {
    return a(anId, aContent.build());
  }

  public HtmlCodeBuilder a(final String anId, final String aContent) {
    return a(anId).contain(aContent);
  }

  public HtmlCodeBuilder a(final String anId) {
    return add(ElementType.ANCHOR, anId);
  }

  public HtmlCodeBuilder button(final String anId, final HtmlCodeBuilder aContent) {
    return button(anId, aContent.build());
  }

  public HtmlCodeBuilder button(final String anId, final String aContent) {
    return button(anId).contain(aContent);
  }

  public HtmlCodeBuilder button(final String anId) {
    return add(ElementType.BUTTON, anId);
  }

  public HtmlCodeBuilder checkbox(final String anId) {
    return add(ElementType.CHECKBOX, anId);
  }

  public HtmlCodeBuilder div(final String anId, final HtmlCodeBuilder aContent) {
    return div(anId, aContent.build());
  }

  public HtmlCodeBuilder div(final String anId, final String aContent) {
    return div(anId).contain(aContent);
  }

  public HtmlCodeBuilder div(final String anId) {
    return add(ElementType.DIV, anId);
  }

  public HtmlCodeBuilder image(final String anId, final String anAlt) {
    return image(anId).alt(anAlt);
  }

  public HtmlCodeBuilder image(final String anId) {
    return add(ElementType.IMAGE, anId);
  }

  public HtmlCodeBuilder inputButton(final String anId, final String aValue) {
    return inputButton(anId).value(aValue);
  }

  public HtmlCodeBuilder inputButton(final String anId) {
    return add(ElementType.INPUT_BUTTON, anId);
  }

  public HtmlCodeBuilder inputImage(final String anId, final String anAlt) {
    return add(ElementType.INPUT_IMAGE, anId).alt(anAlt);
  }

  public HtmlCodeBuilder inputImage(final String anId) {
    return add(ElementType.INPUT_IMAGE, anId);
  }

  public HtmlCodeBuilder inputPassword(final String anId, final String aPlaceholder) {
    return inputPassword(anId).contain(aPlaceholder);
  }

  public HtmlCodeBuilder inputPassword(final String anId) {
    return add(ElementType.INPUT_PASSWORD, anId);
  }

  public HtmlCodeBuilder inputReset(final String anId, final String aValue) {
    return inputReset(anId).value(aValue);
  }

  public HtmlCodeBuilder inputReset(final String anId) {
    return add(ElementType.INPUT_RESET, anId);
  }

  public HtmlCodeBuilder inputSubmit(final String anId, final String aValue) {
    return inputSubmit(anId).value(aValue);
  }

  public HtmlCodeBuilder inputSubmit(final String anId) {
    return add(ElementType.INPUT_SUBMIT, anId);
  }

  public HtmlCodeBuilder inputText(final String anId, final String aPlaceholder) {
    return inputText(anId).contain(aPlaceholder);
  }

  public HtmlCodeBuilder inputText(final String anId) {
    return add(ElementType.INPUT_TEXT, anId);
  }

  public HtmlCodeBuilder label(final String aForId, final HtmlCodeBuilder aContent) {
    return label(aForId, aContent.build());
  }

  public HtmlCodeBuilder label(final String aForId, final String aContent) {
    return label(aForId).contain(aContent);
  }

  public HtmlCodeBuilder label(final String aForId) {
    return add(ElementType.LABEL, aForId);
  }

  public HtmlCodeBuilder radio(final String anId) {
    return add(ElementType.RADIO, anId);
  }

  public HtmlCodeBuilder span(final String anId, final HtmlCodeBuilder aContent) {
    return span(anId, aContent.build());
  }

  public HtmlCodeBuilder span(final String anId, final String aContent) {
    return span(anId).contain(aContent);
  }

  public HtmlCodeBuilder span(final String anId) {
    return add(ElementType.SPAN, anId);
  }

  public HtmlCodeBuilder noListen() {
    currentElement.listen = false;
    return this;
  }

  public HtmlCodeBuilder style(final String aStyle) {
    currentElement.style = aStyle;
    return this;
  }

  private HtmlCodeBuilder add(final ElementType anElementType, final String anId) {
    currentElement = new Element(anElementType, anId);
    elements.add(currentElement);
    return this;
  }

  private HtmlCodeBuilder alt(final String anAlt) {
    currentElement.alt = anAlt;
    return this;
  }

  public HtmlCodeBuilder value(final String aValue) {
    currentElement.value = aValue;
    return this;
  }

  private HtmlCodeBuilder contain(final String aContent) {
    currentElement.content = aContent;
    return this;
  }

  public String build() {
    final StringBuilder tmpHtml = new StringBuilder();

    for (Element tmpElement : elements) {
      switch (tmpElement.elementType) {
        case ANCHOR:
          tmpHtml.append(HtmlCodeCreator.a(tmpElement.id, tmpElement.content, tmpElement.style, tmpElement.listen));
          break;
        case BUTTON:
          tmpHtml
              .append(HtmlCodeCreator.button(tmpElement.id, tmpElement.content, tmpElement.style, tmpElement.listen));
          break;
        case CHECKBOX:
          tmpHtml.append(HtmlCodeCreator.checkbox(tmpElement.id, tmpElement.style, tmpElement.listen));
          break;
        case DIV:
          tmpHtml.append(HtmlCodeCreator.divStart(tmpElement.id, tmpElement.style, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(HtmlCodeCreator.divEnd());
          break;
        case IMAGE:
          tmpHtml.append(HtmlCodeCreator.image(tmpElement.id, tmpElement.alt, tmpElement.style, tmpElement.listen));
          break;
        case INPUT_BUTTON:
          tmpHtml.append(
              HtmlCodeCreator.inputButton(tmpElement.id, tmpElement.value, tmpElement.style, tmpElement.listen));
          break;
        case INPUT_IMAGE:
          tmpHtml
              .append(HtmlCodeCreator.inputImage(tmpElement.id, tmpElement.alt, tmpElement.style, tmpElement.listen));
          break;
        case INPUT_PASSWORD:
          tmpHtml.append(HtmlCodeCreator.inputPassword(tmpElement.id, tmpElement.value, tmpElement.content,
              tmpElement.style, tmpElement.listen));
          break;
        case INPUT_RESET:
          tmpHtml
              .append(HtmlCodeCreator.inputReset(tmpElement.id, tmpElement.value, tmpElement.style, tmpElement.listen));
          break;
        case INPUT_SUBMIT:
          tmpHtml.append(
              HtmlCodeCreator.inputSubmit(tmpElement.id, tmpElement.value, tmpElement.style, tmpElement.listen));
          break;
        case INPUT_TEXT:
          tmpHtml.append(HtmlCodeCreator.inputText(tmpElement.id, tmpElement.value, tmpElement.content,
              tmpElement.style, tmpElement.listen));
          break;
        case LABEL:
          tmpHtml.append(HtmlCodeCreator.labelStart(tmpElement.id, tmpElement.style, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(HtmlCodeCreator.labelEnd());
          break;
        case RADIO:
          tmpHtml.append(HtmlCodeCreator.radio(tmpElement.id, tmpElement.style, tmpElement.listen));
          break;
        case SPAN:
          tmpHtml.append(HtmlCodeCreator.spanStart(tmpElement.id, tmpElement.style, tmpElement.listen));
          if (tmpElement.content != null) {
            tmpHtml.append(tmpElement.content);
          }
          tmpHtml.append(HtmlCodeCreator.spanEnd());
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
