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


package org.wetator.backend.htmlunit.matcher;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.FindSpot;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author frank.danek
 */
public class ByTableCoordinatesMatcherTest extends AbstractMatcherTest {

  @Test
  public void inTablePlain() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTablePlain_textBefore_insideCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'>Some text .... <input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > [header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTablePlain_textBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("Some text > [header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 44, 53, tmpMatches.get(0));
  }

  @Test
  public void inTablePlain_wrongTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <p>Some text .... </p>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > [header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTablePlain_noTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("wrong text > [header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTableNestedX() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'><table><tr><td id='header_i_3'>header_3</td></tr></table></th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedY() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'><table><tr><td id='cell_i_2_1'>row_2</td></tr></table></td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedCell() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3'><input type='text' id='InputText_2_3'/></td></tr></table></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedCellMultiple() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3_1'><input type='text' id='InputText_2_3_1'/></td><td id='cell_i_2_3_2'><input type='text' id='InputText_2_3_2'/></td></tr></table></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3_1", "InputText_2_3_2");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_3_1", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3_2", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inTableNestedTable() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableDifferentTableX() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[cell_o_1_2; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(1));
  }

  @Test
  public void inTableDifferentTableY() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>"
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>"
        + "          <td>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; cell_o_2_1]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 65, 65, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(1));
  }

  @Test
  public void inTableOnlyX() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 32, 32, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inTableOnlyPartlyX() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[ader_3]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTableOnlyXWithPathBehind() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("row_2 > [header_3]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTableOnlyY() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inTableOnlyPartlyY() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[; w_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTableOnlyYWithPath() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("header_3 > [; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3",
        "InputText_2_2", "InputText_2_3");

    assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 12, 38, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 12, 38, tmpMatches.get(1));
  }

  @Test
  public void inMultipleTable() throws IOException, InvalidInputException {
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
        + "    <table>"
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
        + "          <td id='cell_1_1_2'><input type='text' id='InputText_1_1_2'/></td>"
        + "          <td id='cell_1_1_3'><input type='text' id='InputText_1_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input type='text' id='InputText_1_2_2'/></td>"
        + "          <td id='cell_1_2_3'><input type='text' id='InputText_1_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>"
        + "    <table>"
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
        + "          <td id='cell_2_1_2'><input type='text' id='InputText_2_1_2'/></td>"
        + "          <td id='cell_2_1_3'><input type='text' id='InputText_2_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input type='text' id='InputText_2_2_2'/></td>"
        + "          <td id='cell_2_2_3'><input type='text' id='InputText_2_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[cell_o_1_2; cell_o_2_1] > [header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1_2", "InputText_1_1_3",
        "InputText_1_2_2", "InputText_1_2_3", "InputText_2_1_2", "InputText_2_1_3", "InputText_2_2_2",
        "InputText_2_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_1_2_3", FoundType.BY_TABLE_COORDINATE, 0, 82, 82, tmpMatches.get(0));
  }

  @Test
  public void inMultipleTable_textBeforeInnerTable() throws IOException, InvalidInputException {
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
        + "    <table>"
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
        + "          <td id='cell_1_1_2'><input type='text' id='InputText_1_1_2'/></td>"
        + "          <td id='cell_1_1_3'><input type='text' id='InputText_1_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input type='text' id='InputText_1_2_2'/></td>"
        + "          <td id='cell_1_2_3'><input type='text' id='InputText_1_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>Some text ...."
        + "    <table>"
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
        + "          <td id='cell_2_1_2'><input type='text' id='InputText_2_1_2'/></td>"
        + "          <td id='cell_2_1_3'><input type='text' id='InputText_2_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input type='text' id='InputText_2_2_2'/></td>"
        + "          <td id='cell_2_2_3'><input type='text' id='InputText_2_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[cell_o_1_2; cell_o_2_1] > Some text > [header_3; row_2]");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1_2", "InputText_1_1_3",
        "InputText_1_2_2", "InputText_1_2_3", "InputText_2_1_2", "InputText_2_1_3", "InputText_2_2_2",
        "InputText_2_2_3");

    assertEquals(0, tmpMatches.size());

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpSearch = new SecretString("Some text > [cell_o_1_2; cell_o_2_1] > [header_3; row_2]");

    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1_2", "InputText_1_1_3", "InputText_1_2_2",
        "InputText_1_2_3", "InputText_2_1_2", "InputText_2_1_3", "InputText_2_2_2", "InputText_2_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inMultipleTable_textBeforeOuterTable() throws IOException, InvalidInputException {
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
        + "    <table>"
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
        + "          <td id='cell_1_1_2'><input type='text' id='InputText_1_1_2'/></td>"
        + "          <td id='cell_1_1_3'><input type='text' id='InputText_1_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_1_2_1'>row_2</td>"
        + "          <td id='cell_1_2_2'><input type='text' id='InputText_1_2_2'/></td>"
        + "          <td id='cell_1_2_3'><input type='text' id='InputText_1_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "          <td>"
        + "    <table>"
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
        + "          <td id='cell_2_1_2'><input type='text' id='InputText_2_1_2'/></td>"
        + "          <td id='cell_2_1_3'><input type='text' id='InputText_2_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2_1'>row_2</td>"
        + "          <td id='cell_2_2_2'><input type='text' id='InputText_2_2_2'/></td>"
        + "          <td id='cell_2_2_3'><input type='text' id='InputText_2_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "          </td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("Some text > [cell_o_1_2; cell_o_2_1] > [header_3; row_2]");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1_2", "InputText_1_1_3",
        "InputText_1_2_2", "InputText_1_2_3", "InputText_2_1_2", "InputText_2_1_3", "InputText_2_2_2",
        "InputText_2_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_1_2_3", FoundType.BY_TABLE_COORDINATE, 0, 88, 97, tmpMatches.get(0));

    // as table cells are handled separately this wpath is equivalent to the wpath above
    tmpSearch = new SecretString("[cell_o_1_2; cell_o_2_1] > Some text > [header_3; row_2]");

    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1_2", "InputText_1_1_3", "InputText_1_2_2",
        "InputText_1_2_3", "InputText_2_1_2", "InputText_2_1_3", "InputText_2_2_2", "InputText_2_2_3");

    assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_1_2_3", FoundType.BY_TABLE_COORDINATE, 0, 88, 97, tmpMatches.get(0));
  }

  @Test
  public void rowSpan() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>row_1</td>"
        + "          <td id='cell_1_2' rowspan='2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[; row_1]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_2", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_3");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_3");
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void rowSpanHitBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1' rowspan='2'>row_1</td>"
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[; row_1]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_2", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_3");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_3");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));
  }

  @Test
  public void rowSpanHitAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><input type='text' id='InputText_1_1'/></td>"
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3' rowspan='2'>row_1</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'><input type='text' id='InputText_2_1'/></td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[; row_1]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_1", FoundType.BY_TABLE_COORDINATE, 0, 0, 0, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_2", FoundType.BY_TABLE_COORDINATE, 0, 0, 0, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_1");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_2_1", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));

    tmpSearch = new SecretString("[; row_1]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 5, 5, tmpMatches.get(0));
  }

  @Test
  public void brokenRowSpan() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1' rowspan='2'><input type='text' id='InputText_1_1'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[; row_somewhere]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");

    // no match but we like to be sure that there was no exception
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void colSpan() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_2</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1' colspan='2'><input type='text' id='InputText_1_1'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[header_1 ;]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_1", FoundType.BY_TABLE_COORDINATE, 0, 26, 26, tmpMatches.get(0));

    tmpSearch = new SecretString("[header_2 ;]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_1", FoundType.BY_TABLE_COORDINATE, 0, 26, 26, tmpMatches.get(0));
  }

  @Test
  public void colSpanHitBefore() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1' colspan='2'>header_1</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><input type='text' id='InputText_1_1'/></td>"
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[header_1 ;]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_1", FoundType.BY_TABLE_COORDINATE, 0, 17, 17, tmpMatches.get(0));

    tmpSearch = new SecretString("[header_1 ;]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_2", FoundType.BY_TABLE_COORDINATE, 0, 17, 17, tmpMatches.get(0));

    tmpSearch = new SecretString("[header_1 ;]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_3");
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void colSpanHitAfter() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <thead>"
        + "        <tr>"
        + "          <th id='header_1'>header_1</th>"
        + "          <th id='header_2'>header_1</th>"
        + "          <th id='header_3'>header_3</th>"
        + "        </tr>"
        + "      </thead>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'><input type='text' id='InputText_1_1'/></td>"
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>col_1</td>"
        + "          <td id='cell_2_2' colspan='2'>col_2</td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    SecretString tmpSearch = new SecretString("[col_2 ;]");
    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_2", FoundType.BY_TABLE_COORDINATE, 0, 26, 26, tmpMatches.get(0));

    tmpSearch = new SecretString("[col_2 ;]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_3");
    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 26, 26, tmpMatches.get(0));

    tmpSearch = new SecretString("[col_2 ;]");
    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void brokenColSpan() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1' colspan='2'><input type='text' id='InputText_1_1'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[col_somewhere ;]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_1");

    // no match but we like to be sure that there was no exception
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void brokenColSpan2() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>line one</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row 2</td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[line one;row 2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_2");

    // no match because 'line one' does not span to column 2
    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void brokenColSpan3() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
        + "      <tbody>"
        + "        <tr>"
        + "          <td id='cell_1_1'>line one</td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'><input type='text' id='InputText_2_1'/></td>"
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>"
        + "          <td id='cell_2_3'>row 2</td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[line one;row 2]");

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_1");

    assertEquals(1, tmpMatches.size());
    assertMatchEquals("InputText_2_1", FoundType.BY_TABLE_COORDINATE, 0, 8, 8, tmpMatches.get(0));

    tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_2_2");

    assertEquals(0, tmpMatches.size());
  }

  @Test
  public void otherControl() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "    <table>"
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
        + "          <td id='cell_1_2'><input type='file' id='InputFile_1_2'/></td>"
        + "          <td id='cell_1_3'><input type='file' id='InputFile_1_3'/></td>"
        + "        </tr>"
        + "        <tr>"
        + "          <td id='cell_2_1'>row_2</td>"
        + "          <td id='cell_2_2'><input type='file' id='InputFile_2_2'/></td>"
        + "          <td id='cell_2_3'><input type='file' id='InputFile_2_3'/></td>"
        + "        </tr>"
        + "      </tbody>"
        + "    </table>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("[header_3; row_2]");

    final List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputFile_1_2", "InputFile_1_3",
        "InputFile_2_2", "InputFile_2_3");

    assertEquals(0, tmpMatches.size());
  }

  @Override
  protected List<MatchResult> match(final String aHtmlCode, final SecretString aSearch,
      final String... anHtmlElementIds) throws IOException, InvalidInputException {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    final WetatorConfiguration tmpConfig = new WetatorConfiguration(new File("."), tmpProperties, null);

    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    final List<MatchResult> tmpMatches = new ArrayList<>();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      final HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      final WPath tmpPath = new WPath(aSearch, tmpConfig);

      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!tmpPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(tmpPath.getPathNodes());
        tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      tmpMatches.addAll(new ByTableCoordinatesMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
          tmpPath.getTableCoordinatesReversed(), HtmlTextInput.class).matches(tmpHtmlElement));
    }
    return tmpMatches;
  }

  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(final HtmlPageIndex aHtmlPageIndex,
      final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    return null;
  }
}
