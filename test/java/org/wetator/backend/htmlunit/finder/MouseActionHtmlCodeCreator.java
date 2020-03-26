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

/**
 * Creator for HTML code of clickable elements.
 *
 * @author tobwoerk
 */
public abstract class MouseActionHtmlCodeCreator {

  public static final String CONTENT = "test";

  private static final String ONCLICK = "onclick=''";
  public static String onMouseAction = ONCLICK;

  public static String pageStart() {
    return "<html><body>";
  }

  public static String pageEnd() {
    return "</body></html>";
  }

  public static String a(final String anAnchorId, final String aContent) {
    return "<a id='" + anAnchorId + "' href='#'>" + (aContent != null ? aContent : "") + "</a>";
  }

  public static String button(final String aButtonId, final String aContent) {
    return "<button id='" + aButtonId + "' type='button'>" + (aContent != null ? aContent : "") + "</button>";
  }

  public static String checkbox(final String aCheckboxId, final boolean anIsListening) {
    return input("checkbox", aCheckboxId, anIsListening);
  }

  public static String divStart(final String aDivId, final boolean anIsListening) {
    return "<div id='" + aDivId + "'" + (anIsListening ? ' ' + onMouseAction : "") + '>';
  }

  public static String divEnd() {
    return "</div>";
  }

  public static String inputText(final String anInputID, final String aPlaceholder, final boolean anIsListening) {
    if (aPlaceholder != null) {
      return input("text", anInputID, anIsListening, "placeholder='" + aPlaceholder + "'");
    }
    return input("text", anInputID, anIsListening);
  }

  public static String image(final String anImageId, final String anAltText, final boolean anIsListening) {
    return "<image id='" + anImageId + "'" + (anAltText != null ? " alt='" + anAltText + "'" : "")
        + (anIsListening ? ' ' + onMouseAction : "") + " src='pathtoimg' />";
  }

  public static String labelStart(final String aFor, final boolean anIsListening) {
    return "<label id='lbl-" + aFor + "' for='" + aFor + '\'' + (anIsListening ? ' ' + onMouseAction : "") + '>';
  }

  public static String labelEnd() {
    return "</label>";
  }

  public static String radio(final String aRadioId, final boolean anIsListening) {
    return input("radio", aRadioId, anIsListening);
  }

  public static String spanStart(final String aSpanId, final boolean anIsListening) {
    return "<span id='" + aSpanId + "'" + (anIsListening ? ' ' + onMouseAction : "") + '>';
  }

  public static String spanEnd() {
    return "</span>";
  }

  // FIXME tables -> builder

  public static String tableStart(final String aTableId, final boolean anIsListening) {
    return "<table id='" + aTableId + "'" + (anIsListening ? ' ' + onMouseAction : "") + "><tbody id='" + aTableId
        + "-body'" + (anIsListening ? ' ' + onMouseAction : "") + '>';
  }

  public static String tableEnd() {
    return "</tbody></table>";
  }

  public static String tableRowStart(final String aTableId, final String aRowId, final boolean anIsListening) {
    return "<tr id='" + aTableId + '-' + aRowId + "'" + (anIsListening ? ' ' + onMouseAction : "") + '>';
  }

  public static String tableRowEnd() {
    return "</tr>";
  }

  public static String tableRowWithCols(final String aTableId, final String aRowId, final int aColumnCount,
      final boolean anIsListening) {
    String tmpRow = tableRowStart(aTableId, aRowId, anIsListening);
    for (int i = 1; i <= aColumnCount; i++) {
      tmpRow += "<td id='" + aTableId + '-' + aRowId + "-td" + (aColumnCount > 1 ? i : "") + "'"
          + (anIsListening ? ' ' + onMouseAction : "") + '>' + CONTENT + "</td>";
    }
    tmpRow += tableRowEnd();

    return tmpRow;
  }

  private static String input(final String aType, final String anInputID, final boolean anIsListening) {
    return input(aType, anInputID, anIsListening, null);
  }

  private static String input(final String aType, final String anInputID, final boolean anIsListening,
      final String anOptional) {
    return "<input type = '" + aType + "' id='" + anInputID + "'" + (anIsListening ? ' ' + onMouseAction : "")
        + (anOptional != null ? ' ' + anOptional : "") + " />";
  }

  public static void resetOnMouseAction() {
    onMouseAction = ONCLICK;
  }
}
