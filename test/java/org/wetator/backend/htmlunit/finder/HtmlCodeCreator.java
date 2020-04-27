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

import org.apache.commons.lang3.StringUtils;

/**
 * Creator for HTML code of clickable elements.
 *
 * @author tobwoerk
 */
public abstract class HtmlCodeCreator {

  public static final String CONTENT = "test";

  private static final String ON_CLICK = "onclick=''";
  private static final String ON_CLICK_DOUBLE = "ondblclick=''";
  private static final String ON_CLICK_RIGHT = "oncontextmenu=''";
  private static final String ON_MOUSE_OVER = "onmouseover=''";
  private static final String ON_ANY_MOUSE_ACTION = "onclick='' ondblclick='' oncontextmenu='' onmouseover=''";

  private static final String TAB_INDEX = "tabindex='123'";

  private static String listeners = "";

  public static String pageStart() {
    return "<html><body>";
  }

  public static String pageEnd() {
    return "</body></html>";
  }

  static String a(final String anAnchorId, final String aContent, final String aStyle, final boolean anIsListening) {
    return "<a id='" + anAnchorId + "' href='#'" + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + '>' + (aContent != null ? aContent : "") + "</a>";
  }

  static String button(final String aButtonId, final String aContent, final String aStyle,
      final boolean anIsListening) {
    return "<button type='button' id='" + aButtonId + '\'' + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + '>' + (aContent != null ? aContent : "") + "</button>";
  }

  static String checkbox(final String aCheckboxId, final String aStyle, final boolean anIsListening) {
    return input("checkbox", aCheckboxId, aStyle, anIsListening);
  }

  static String divStart(final String aDivId, final String aStyle, final boolean anIsListening) {
    return "<div id='" + aDivId + '\'' + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + '>';
  }

  static String divEnd() {
    return "</div>";
  }

  static String inputButton(final String anInputID, final String aValue, final String aStyle,
      final boolean anIsListening) {
    return input("button", anInputID, anIsListening, aStyle, aValue != null ? "value='" + aValue + '\'' : null);
  }

  static String inputImage(final String anInputID, final String anAlt, final String aStyle,
      final boolean anIsListening) {
    return input("image", anInputID, anIsListening, aStyle,
        (anAlt != null ? "alt='" + anAlt + "' " : "") + "src='pathtoimg'");
  }

  static String inputPassword(final String anInputID, final String aValue, final String aPlaceholder,
      final String aStyle, final boolean anIsListening) {
    return input("password", anInputID, anIsListening, aStyle,
        (aPlaceholder != null ? " placeholder='" + aPlaceholder + '\'' : "")
            + (aValue != null ? (aPlaceholder != null ? ' ' : "") + "value='" + aValue + '\'' : ""));
  }

  static String inputReset(final String anInputID, final String aValue, final String aStyle,
      final boolean anIsListening) {
    return input("reset", anInputID, anIsListening, aStyle, aValue != null ? "value='" + aValue + '\'' : null);
  }

  static String inputSubmit(final String anInputID, final String aValue, final String aStyle,
      final boolean anIsListening) {
    return input("submit", anInputID, anIsListening, aStyle, aValue != null ? "value='" + aValue + '\'' : null);
  }

  static String inputText(final String anInputID, final String aValue, final String aPlaceholder, final String aStyle,
      final boolean anIsListening) {
    return input("text", anInputID, anIsListening, aStyle,
        (aPlaceholder != null ? " placeholder='" + aPlaceholder + '\'' : "")
            + (aValue != null ? (aPlaceholder != null ? ' ' : "") + "value='" + aValue + '\'' : ""));
  }

  static String image(final String anImageId, final String anAlt, final String aStyle, final boolean anIsListening) {
    return "<image id='" + anImageId + '\'' + (anAlt != null ? " alt='" + anAlt + '\'' : "")
        + (anIsListening ? ' ' + listeners : "") + (aStyle != null ? " style='" + aStyle + "'" : "")
        + " src='pathtoimg' />";
  }

  static String labelStart(final String aFor, final String aStyle, final boolean anIsListening) {
    return "<label id='lbl-" + aFor + "' for='" + aFor + '\'' + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + '>';
  }

  static String labelEnd() {
    return "</label>";
  }

  static String radio(final String aRadioId, final String aStyle, final boolean anIsListening) {
    return input("radio", aRadioId, aStyle, anIsListening);
  }

  static String spanStart(final String aSpanId, final String aStyle, final boolean anIsListening) {
    return "<span id='" + aSpanId + '\'' + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + '>';
  }

  static String spanEnd() {
    return "</span>";
  }

  static String selectStart(final String aSelectId, final String aName, final String aStyle,
      final boolean anIsListening) {
    return "<select id='" + aSelectId + '\'' + (aName != null ? " name='" + aName + '\'' : "")
        + (anIsListening ? ' ' + listeners : "") + (aStyle != null ? " style='" + aStyle + "'" : "") + ">";
  }

  static String selectEnd() {
    return "</select>";
  }

  static String selectOption(final String aSelectId, final String anOptionId, final String aContent,
      final String aStyle, final boolean anIsListening) {
    final String tmpOptionId = aSelectId + '-' + anOptionId;
    return "<option id='" + tmpOptionId + '\'' + " value='" + tmpOptionId + "-val' "
        + (anIsListening ? ' ' + listeners : "") + (aStyle != null ? " style='" + aStyle + "'" : "") + "'>"
        + (aContent != null ? aContent : "") + "</option>";
  }

  static String tableStart(final String aTableId, final String aStyle, final boolean anIsListening) {
    return "<table id='" + aTableId + '\'' + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + "><tbody id='" + aTableId + "-body'"
        + (anIsListening ? ' ' + listeners : "") + '>';
  }

  static String tableEnd() {
    return "</tbody></table>";
  }

  static String tableRowWithCols(final String aTableId, final String aRowId, final int aColumnCount,
      final boolean anIsListening) {
    String tmpRow = tableRowStart(aTableId, aRowId, anIsListening);
    for (int i = 1; i <= aColumnCount; i++) {
      tmpRow += "<td id='" + aTableId + '-' + aRowId + "-td" + (aColumnCount > 1 ? i : "") + '\''
          + (anIsListening ? ' ' + listeners : "") + '>' + CONTENT + "</td>";
    }
    tmpRow += tableRowEnd();

    return tmpRow;
  }

  private static String tableRowStart(final String aTableId, final String aRowId, final boolean anIsListening) {
    return "<tr id='" + aTableId + '-' + aRowId + '\'' + (anIsListening ? ' ' + listeners : "") + '>';
  }

  private static String tableRowEnd() {
    return "</tr>";
  }

  static String textArea(final String aTextAreaID, final String aText, final String aPlaceholder, final String aStyle,
      final boolean anIsListening) {
    return "<textarea id='" + aTextAreaID + '\'' + (aPlaceholder != null ? " placeholder='" + aPlaceholder + '\'' : "")
        + (anIsListening ? ' ' + listeners : "") + (aStyle != null ? " style='" + aStyle + "'" : "") + '>'
        + (aText != null ? aText : "") + "</textarea>";
  }

  private static String input(final String aType, final String anInputID, final String aStyle,
      final boolean anIsListening) {
    return input(aType, anInputID, anIsListening, aStyle, null);
  }

  private static String input(final String aType, final String anInputID, final boolean anIsListening,
      final String aStyle, final String anOptional) {
    return "<input id='" + anInputID + "' type='" + aType + '\''
        + (!StringUtils.isEmpty(anOptional) ? ' ' + anOptional : "") + (anIsListening ? ' ' + listeners : "")
        + (aStyle != null ? " style='" + aStyle + "'" : "") + " />";
  }

  public static void resetListeners() {
    listeners = "";
  }

  public static void listenToClick() {
    listeners = ON_CLICK;
  }

  public static void listenToClickDouble() {
    listeners = ON_CLICK_DOUBLE;
  }

  public static void listenToClickRight() {
    listeners = ON_CLICK_RIGHT;
  }

  public static void listenToMouseOver() {
    listeners = ON_MOUSE_OVER;
  }

  public static void listenToAnyMouseAction() {
    listeners = ON_ANY_MOUSE_ACTION;
  }

  public static void listenToFocus() {
    listeners = TAB_INDEX;
  }
}
