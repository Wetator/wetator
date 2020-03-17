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

import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.CONTENT;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.anchor;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.button;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.checkbox;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.div;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.image;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.inputText;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.label;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.labelClickable;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.radio;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.span;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.tableEnd;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.tableRowWithCols;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.tableStart;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitImageIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputRadioButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Basic tests for element weighting during {@link ClickableHtmlUnitControlsFinder#find(WPath)}.
 *
 * @author tobwoerk
 */
public class ClickableHtmlUnitControlsFinderBasicTest extends AbstractClickableHtmlUnitControlsFinderParameterizedTest {

  @Parameter(2)
  public List<Class<? extends AbstractMatcherBasedIdentifier>> identifiers;

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      { anchor("anchor-before", null) + anchor("anchor", CONTENT) + anchor("anchor-after", null),
        new SortedEntryExpectation(new ExpectedControl(HtmlAnchor.class, "anchor")),
        Arrays.asList(HtmlUnitAnchorIdentifier.class)
      },

      { button("button-before", null) + button("button", CONTENT) + button("button-after", null),
        new SortedEntryExpectation(new ExpectedControl(HtmlButton.class, "button")),
        Arrays.asList(HtmlUnitButtonIdentifier.class)
      },

      { checkbox("checkbox-before") + CONTENT + checkbox("checkbox-after") + label("checkbox-label", CONTENT) + checkbox("checkbox-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-label"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-before"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-after")),
        Arrays.asList(HtmlUnitInputCheckBoxIdentifier.class)
      },

      { div("div-before", null) + div("div", CONTENT) + div("div-after", null),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "div")),
        null
      },

      { inputText("inputText-before") + CONTENT + inputText("inputText-after"),
        new SortedEntryExpectation(new ExpectedControl(HtmlTextInput.class, "inputText-after")),
        Arrays.asList(HtmlUnitInputTextIdentifier.class)
      },

      { image("img-before", "") + image("img", CONTENT) + image("img-after", ""),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImage.class, "img"),
            new ExpectedControl(HtmlImage.class, "img-after")),
        Arrays.asList(HtmlUnitImageIdentifier.class)
      },

      { labelClickable("before", "") + labelClickable("main", CONTENT) + labelClickable("after", ""),
        new SortedEntryExpectation(new ExpectedControl(HtmlLabel.class, "lbl-main")),
        null
      },

      { radio("radio-before") + CONTENT + radio("radio-after") + label("radio-label", CONTENT) + radio("radio-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-label"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-before"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-after")),
        Arrays.asList(HtmlUnitInputRadioButtonIdentifier.class)
      },

      { span("span-before", null) + span("span", CONTENT) + span("span-after", null),
        new SortedEntryExpectation(new ExpectedControl(HtmlSpan.class, "span")),
        null
      },

      { tableStart("table") + tableRowWithCols("table", "tr", 1) + tableEnd(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td"),
            new ExpectedControl(HtmlTableRow.class, "table-tr"),
            new ExpectedControl(HtmlTableBody.class, "table-body"),
            new ExpectedControl(HtmlTable.class, "table")),
        null
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    if (identifiers != null) {
      addIdentifiers(identifiers);
    }
  }
}
