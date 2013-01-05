/*
 * Copyright (c) 2008-2012 wetator.org
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.searchpattern.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.exception.InvalidInputException;
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
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'><table><tr><td id='header_i_3'>header_3</td></tr></table></th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'><table><tr><td id='cell_i_2_1'>row_2</td></tr></table></td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedCell() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3'><input type='text' id='InputText_2_3'/></td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableNestedCellMultiple() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><table><tr><td id='cell_i_2_3_1'><input type='text' id='InputText_2_3_1'/></td><td id='cell_i_2_3_2'><input type='text' id='InputText_2_3_2'/></td></tr></table></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3_1", "InputText_2_3_2");

    Assert.assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_3_1", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3_2", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inTableNestedTable() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
  }

  @Test
  public void inTableDifferentTableX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[cell_o_1_2; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(1));
  }

  @Test
  public void inTableDifferentTableY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3; cell_o_2_1]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 65, 65, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(1));
  }

  @Test
  public void inTableOnlyX() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[header_3]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_1_3", FoundType.BY_TABLE_COORDINATE, 0, 32, 32, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inTableOnlyXWithPathBehind() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("row_2", false));
    tmpSearch.add(new SecretString("[header_3]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(0, tmpMatches.size());
  }

  @Test
  public void inTableOnlyY() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(2, tmpMatches.size());

    assertMatchEquals("InputText_2_2", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(0));
    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 38, 38, tmpMatches.get(1));
  }

  @Test
  public void inMultipleTable() throws IOException, InvalidInputException {
    String tmpHtmlCode = "<html><body>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_o_1_1'>cell_o_1_1</td>" //
        + "          <td id='cell_o_1_2'>cell_o_1_2</td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_o_2_1'>cell_o_2_1</td>" //
        + "          <td>" //
        + "    <table border='0' cellspacing='20' cellpadding='30'>" //
        + "      <thead>" //
        + "        <tr>" //
        + "          <th id='header_1'>header_1</th>" //
        + "          <th id='header_2'>header_2</th>" //
        + "          <th id='header_3'>header_3</th>" //
        + "        </tr>" //
        + "      </thead>" //
        + "      <tbody>" //
        + "        <tr>" //
        + "          <td id='cell_1_1'>row_1</td>" //
        + "          <td id='cell_1_2'><input type='text' id='InputText_1_2'/></td>" //
        + "          <td id='cell_1_3'><input type='text' id='InputText_1_3'/></td>" //
        + "        </tr>" //
        + "        <tr>" //
        + "          <td id='cell_2_1'>row_2</td>" //
        + "          <td id='cell_2_2'><input type='text' id='InputText_2_2'/></td>" //
        + "          <td id='cell_2_3'><input type='text' id='InputText_2_3'/></td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "          </td>" //
        + "        </tr>" //
        + "      </tbody>" //
        + "    </table>" //
        + "</body></html>";

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("[cell_o_1_2; cell_o_2_1]", false));
    tmpSearch.add(new SecretString("[header_3; row_2]", false));

    List<MatchResult> tmpMatches = match(tmpHtmlCode, tmpSearch, "InputText_1_2", "InputText_1_3", "InputText_2_2",
        "InputText_2_3");

    Assert.assertEquals(1, tmpMatches.size());

    assertMatchEquals("InputText_2_3", FoundType.BY_TABLE_COORDINATE, 0, 71, 71, tmpMatches.get(0));
  }

  @Override
  protected List<MatchResult> match(String aHtmlCode, List<SecretString> aSearch, String... anHtmlElementIds)
      throws IOException, InvalidInputException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<MatchResult> tmpMatches = new ArrayList<MatchResult>();
    for (String tmpHtmlElementId : anHtmlElementIds) {
      HtmlElement tmpHtmlElement = tmpHtmlPage.getHtmlElementById(tmpHtmlElementId);

      WPath tmpPath = new WPath(aSearch);

      SearchPattern tmpPathSearchPattern = null;
      FindSpot tmpPathSpot = null;
      if (!tmpPath.getPathNodes().isEmpty()) {
        tmpPathSearchPattern = SearchPattern.createFromList(tmpPath.getPathNodes());
        tmpPathSpot = tmpHtmlPageIndex.firstOccurence(tmpPathSearchPattern);
      }

      tmpMatches.addAll(new ByTableCoordinatesMatcher(tmpHtmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpPath
          .getTableCoordinatesReversed(), HtmlTextInput.class).matches(tmpHtmlElement));
    }
    return tmpMatches;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractMatcherTest#createMatcher(org.wetator.backend.htmlunit.util.HtmlPageIndex,
   *      org.wetator.core.searchpattern.SearchPattern, org.wetator.core.searchpattern.FindSpot,
   *      org.wetator.core.searchpattern.SearchPattern)
   */
  @Override
  protected AbstractHtmlUnitElementMatcher createMatcher(HtmlPageIndex aHtmlPageIndex,
      SearchPattern aPathSearchPattern, FindSpot aPathSpot, SearchPattern aSearchPattern) {
    return null;
  }
}
