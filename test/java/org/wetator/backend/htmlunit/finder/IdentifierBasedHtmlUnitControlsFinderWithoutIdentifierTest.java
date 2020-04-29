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

import static org.wetator.backend.htmlunit.finder.HtmlCodeCreator.CONTENT;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

/**
 * Tests for element weighting during {@link IdentifierBasedHtmlUnitControlsFinder#find(WPath)} without control-specific
 * identifiers.
 *
 * @author tobwoerk
 * @see IdentifierBasedHtmlUnitControlsFinderWithIdentifierTest
 */
@RunWith(Parameterized.class)
public class IdentifierBasedHtmlUnitControlsFinderWithoutIdentifierTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  @Parameter(0)
  public Object htmlCode;
  @Parameter(1)
  public SortedEntryExpectation expected;

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      { "",
        null
      },

      { a("anchor-before").a("anchor", CONTENT).a("anchor-after"),
        null
      },

      { button("button-value").value(CONTENT).button("button-before").button("button", CONTENT).button("button-after"),
        null
      },

      { checkbox("checkbox-value").value(CONTENT).checkbox("checkbox-before") + CONTENT + checkbox("checkbox-after").label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-checkbox-label"))
      },

      { div("div-before").div("div", CONTENT).div("div-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "div"))
      },

      { inputButton("inputButton-value").value(CONTENT).inputButton("inputButton-before").inputButton("inputButton", CONTENT).inputButton("inputButton-after"),
        null
      },

      { inputFile("inputFile-before").inputFile("inputFile", CONTENT).inputFile("inputFile-after"),
        null
      },

      { inputImage("inputImg-before").inputImage("inputImg", CONTENT).inputImage("inputImg-after"),
        null
      },

      { inputPassword("inputPassword-before").inputPassword("inputPassword-value").value(CONTENT).inputPassword("inputPassword-between").inputPassword("inputPassword", CONTENT).inputPassword("inputPassword-after"),
        null
      },

      { inputReset("inputReset-value").value(CONTENT).inputReset("inputReset-before").inputReset("inputReset", CONTENT).inputReset("inputReset-after"),
        null
      },

      { inputSubmit("inputSubmit-value").value(CONTENT).inputSubmit("inputSubmit-before").inputSubmit("inputSubmit", CONTENT).inputSubmit("inputSubmit-after"),
        null
      },

      { inputText("inputText-before").inputText("inputText-value").value(CONTENT).inputText("inputText-between").inputText("inputText", CONTENT).inputText("inputText-after"),
        null
      },

      { image("img-before").image("img", CONTENT).image("img-after"),
        null
      },

      { label("before").label("main", CONTENT).label("after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-main"))
      },

      { radio("radio-value").value(CONTENT).radio("radio-before") + CONTENT + radio("radio-after").label("radio-label", CONTENT).noListen().radio("radio-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-radio-label"))
      },

      { select("select-before") + CONTENT + select("select").option("option-before").option("option", CONTENT).option("option-after") + select("select-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      { span("span-before").span("span", CONTENT).span("span-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSpan.class, "span"))
      },

      { table("table-before") + CONTENT + table("table").tr("tr", 1) + table("table-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td"))
      },

      { textArea("textArea-before").textArea("textArea-value").value(CONTENT).textArea("textArea-between").textArea("textArea", CONTENT).textArea("textArea-after"),
        null
      },
      // @formatter:on
    };

    return Arrays.asList(tmpData);
  }

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, null);
  }

  @Test
  public void checkFoundElementsFocus() throws Exception {
    checkFoundElements(htmlCode, expected);
  }
}
