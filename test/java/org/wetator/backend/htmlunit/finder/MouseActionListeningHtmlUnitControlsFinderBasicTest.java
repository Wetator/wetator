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

import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.CONTENT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Basic tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)}.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderBasicTest
    extends AbstractMouseClickListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameter(2)
  public Class<? extends AbstractMatcherBasedIdentifier> identifier;

  @BeforeClass
  public static void listenToAnyMouseAction() {
    MouseActionHtmlCodeCreator.listenToAnyMouseAction();
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    listenToAnyMouseAction();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      { a("anchor-before").a("anchor", CONTENT).a("anchor-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlAnchor.class, "anchor")),
        HtmlUnitAnchorIdentifier.class
      },

      { button("button-before").button("button", CONTENT).button("button-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "button")),
        HtmlUnitButtonIdentifier.class
      },

      { checkbox("checkbox-before") + CONTENT + checkbox("checkbox-after").label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-label"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-before"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-checkbox-label")),
        null
      },

      { div("div-before").div("div", CONTENT).div("div-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "div")),
        null
      },

      { inputButton("inputButton-before").inputButton("inputButton", CONTENT).inputButton("inputButton-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButtonInput.class, "inputButton")),
        null
      },

      { inputImage("inputImg-before").inputImage("inputImg", CONTENT).inputImage("inputImg-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImageInput.class, "inputImg")),
        null
      },

      { inputReset("inputReset-before").inputReset("inputReset", CONTENT).inputReset("inputReset-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        null
      },

      { inputSubmit("inputSubmit-before").inputSubmit("inputSubmit", CONTENT).inputSubmit("inputSubmit-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        null
      },

      { inputText("inputText-before").inputText("inputText", CONTENT).inputText("inputText-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "inputText"),
            new ExpectedControl(HtmlTextInput.class, "inputText-after")),
        null
      },

      { image("img-before").image("img", CONTENT).image("img-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImage.class, "img"),
            new ExpectedControl(HtmlImage.class, "img-after")),
        null
      },

      { label("before").label("main", CONTENT).label("after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-main")),
        null
      },

      { radio("radio-before") + CONTENT + radio("radio-after").label("radio-label", CONTENT).noListen().radio("radio-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-label"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-before"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-after"),
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-radio-label")),
        null
      },

      { select("select-before") + CONTENT + select("select").option("option-before").option("option", CONTENT).option("option-after") + select("select-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlSelect.class, "select-after"),
            new ExpectedControl(HtmlBody.class)),
        null
      },

      { span("span-before").span("span", CONTENT).span("span-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSpan.class, "span")),
        null
      },

      { table("table-before") + CONTENT + table("table").tr("tr", 1) + table("table-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td"),
            new ExpectedControl(HtmlTableRow.class, "table-tr"),
            new ExpectedControl(HtmlTableBody.class, "table-body"),
            new ExpectedControl(HtmlTable.class, "table"),
            new ExpectedControl(HtmlBody.class)),
        null
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    if (identifier != null) {
      addIdentifiers(identifier);
    }
  }

  @Test
  public void checkFoundElementsClickDouble() throws Exception {
    setMouseAction(MouseAction.CLICK_DOUBLE);
    checkFoundElements(htmlCode, expected);
  }

  @Test
  public void checkFoundElementsClickRight() throws Exception {
    setMouseAction(MouseAction.CLICK_RIGHT);
    checkFoundElements(htmlCode, expected);
  }

  @Test
  public void checkFoundElementsMouseOver() throws Exception {
    setMouseAction(MouseAction.MOUSE_OVER);
    checkFoundElements(htmlCode, expected);
  }
}
