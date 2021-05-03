/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.backend.htmlunit.control.identifier;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.control.IControl;
import org.wetator.backend.htmlunit.control.HtmlUnitUnspecificControl;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByValueAttributeMatcher;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * @author rbri
 * @author frank.danek
 */
public class MatcherBasedIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new TestIdentifier();
  }

  @Test
  public void inTable() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><input id='myId_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > ClickMe", "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_3')] found by: BY_LABEL deviation: 0 distance: 62 start: 62 hierarchy: 0>1>3>5>22>36>44>45 index: 45",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTable_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'><input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'>Some text .... <input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > Some text > ClickMe", "myId_1_1_2",
        "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "Some text > [header_3; row_2] > ClickMe", "myId_1_1_2", "myId_1_1_3",
        "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void inTable_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'><input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'><input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > Some text > ClickMe", "myId_1_1_2",
        "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_2_3')] found by: BY_LABEL deviation: 0 distance: 68 start: 148 hierarchy: 0>1>3>53>70>84>92>93 index: 93",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "Some text > [header_3; row_2] > ClickMe", "myId_1_1_2", "myId_1_1_3",
        "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_2_3')] found by: BY_LABEL deviation: 0 distance: 68 start: 148 hierarchy: 0>1>3>53>70>84>92>93 index: 93",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTable_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><input id='myId_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > wrong text > ClickMe", "myId_1_2",
        "myId_1_3", "myId_2_2", "myId_2_3");

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > [header_3; row_2] > ClickMe", "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void inTable_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><input id='myId_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_3'><input id='myId_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input id='myId_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_3'><input id='myId_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[header_3; row_2] > wrong text > ClickMe", "myId_1_2",
        "myId_1_3", "myId_2_2", "myId_2_3");

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "wrong text > [header_3; row_2] > ClickMe", "myId_1_2", "myId_1_3",
        "myId_2_2", "myId_2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void inTable_multiple() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "          <td id='cell_o_1_3'>cell_o_1_3</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'><input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'><input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final List<Entry> tmpEntriesSorted = identify(tmpHtmlCode, "[cell_o_1_3; cell_o_2_1] > [header_3; row_2] > ClickMe",
        "myId_1_1_2", "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_2_3')] found by: BY_LABEL deviation: 0 distance: 177 start: 177 hierarchy: 0>1>3>5>7>21>74>76>93>107>115>116 index: 116",
        tmpEntriesSorted.get(0).toString());
  }

  @Test
  public void inTable_multiple_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "          <td id='cell_o_1_3'>cell_o_1_3</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'>Some text .... <input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'>Some text .... <input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "[cell_o_1_3; cell_o_2_1] > [header_3; row_2] > Some text > ClickMe", "myId_1_1_2", "myId_1_1_3", "myId_1_2_2",
        "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "Some text > [cell_o_1_3; cell_o_2_1] > [header_3; row_2] > ClickMe",
        "myId_1_1_2", "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void inTable_multiple_textBefore_innerTable() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "          <td id='cell_o_1_3'>cell_o_1_3</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>Some text .... "
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'><input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>Some text .... "
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'><input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "[cell_o_1_3; cell_o_2_1] > [header_3; row_2] > Some text > ClickMe", "myId_1_1_2", "myId_1_1_3", "myId_1_2_2",
        "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "Some text > [cell_o_1_3; cell_o_2_1] > [header_3; row_2] > ClickMe",
        "myId_1_1_2", "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(0, tmpEntriesSorted.size());
  }

  @Test
  public void inTable_multiple_textBefore_outerTable() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "          <td id='cell_o_1_3'>cell_o_1_3</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1_1'>header_1</th>"
        + "          <th id='header_1_2'>header_2</th>"
        + "          <th id='header_1_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1_1'>row_1</td>"
        + "          <td id='cell_1_1_2'><input id='myId_1_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_1_3'><input id='myId_1_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input id='myId_1_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_1_2_3'><input id='myId_1_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>"
        + "    <table border='0' cellspacing='20' cellpadding='30'>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_2_1'>header_1</th>"
        + "          <th id='header_2_2'>header_2</th>"
        + "          <th id='header_2_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_2_1_1'>row_1</td>"
        + "          <td id='cell_2_1_2'><input id='myId_2_1_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_1_3'><input id='myId_2_1_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input id='myId_2_2_2' type='button' value='ClickMe'></td>"
        + "          <td id='cell_2_2_3'><input id='myId_2_2_3' type='button' value='ClickMe'></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    List<Entry> tmpEntriesSorted = identify(tmpHtmlCode,
        "[cell_o_1_3; cell_o_2_1] > [header_3; row_2] > Some text > ClickMe", "myId_1_1_2", "myId_1_1_3", "myId_1_2_2",
        "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_2_3')] found by: BY_LABEL deviation: 0 distance: 183 start: 192 hierarchy: 0>1>3>8>10>24>77>79>96>110>118>119 index: 119",
        tmpEntriesSorted.get(0).toString());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpEntriesSorted = identify(tmpHtmlCode, "Some text > [cell_o_1_3; cell_o_2_1] > [header_3; row_2] > ClickMe",
        "myId_1_1_2", "myId_1_1_3", "myId_1_2_2", "myId_1_2_3", "myId_2_1_2", "myId_2_1_3", "myId_2_2_2", "myId_2_2_3");

    assertEquals(1, tmpEntriesSorted.size());
    assertEquals(
        "[Unknown HtmlElement 'class com.gargoylesoftware.htmlunit.html.HtmlButtonInput' (id='myId_2_2_3')] found by: BY_LABEL deviation: 0 distance: 183 start: 192 hierarchy: 0>1>3>8>10>24>77>79>96>110>118>119 index: 119",
        tmpEntriesSorted.get(0).toString());
  }

  private class TestIdentifier extends AbstractMatcherBasedIdentifier {

    @Override
    protected void addMatchers(final WPath aWPath, final HtmlElement aHtmlElement,
        final List<AbstractHtmlUnitElementMatcher> aMatchers) {
      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!aWPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
        tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      if (tmpPathSpot == FindSpot.NOT_FOUND) {
        return;
      }

      final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();

      aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
      aMatchers.add(new ByValueAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
    }

    @Override
    protected IControl createControl(final MatchResult aMatch) {
      return new HtmlUnitUnspecificControl<HtmlElement>(aMatch.getHtmlElement());
    }

    @Override
    public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
      return true;
    }

  }
}