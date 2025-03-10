/*
 * Copyright (c) 2008-2025 wetator.org
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlButtonInput;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlImageInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlResetInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSpan;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableDataCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputResetIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Basic tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)}.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderBasicTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameter(2)
  public Class<? extends AbstractMatcherBasedIdentifier> identifier;

  @BeforeClass
  public static void listenToAnyMouseAction() {
    HtmlCodeCreator.listenToAnyMouseAction();
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    listenToAnyMouseAction();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //++++++++++
      // clickable

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
        HtmlUnitInputButtonIdentifier.class
      },

      { inputFile("inputFile-before").inputFile("inputFile", CONTENT).inputFile("inputFile-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "inputFile")),
        null
      },

      { inputImage("inputImg-before").inputImage("inputImg", CONTENT).inputImage("inputImg-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImageInput.class, "inputImg")),
        null
      },

      { inputPassword("inputPassword-before").inputPassword("inputPassword", CONTENT).inputPassword("inputPassword-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "inputPassword"),
            new ExpectedControl(HtmlPasswordInput.class, "inputPassword-after")),
        null
      },

      { inputReset("inputReset-before").inputReset("inputReset", CONTENT).inputReset("inputReset-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("inputSubmit-before").inputSubmit("inputSubmit", CONTENT).inputSubmit("inputSubmit-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        HtmlUnitInputSubmitIdentifier.class
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
      },

      { textArea("textArea-before").textArea("textArea", CONTENT).textArea("textArea-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"),
            new ExpectedControl(HtmlTextArea.class, "textArea-after")),
        null
      },

      //++++++++++++++
      // non-clickable

      { a("anchor-before").noListen().a("anchor", CONTENT).noListen().a("anchor-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlAnchor.class, "anchor")),
        HtmlUnitAnchorIdentifier.class
      },

      { button("button-before").noListen().button("button", CONTENT).noListen().button("button-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "button")),
        HtmlUnitButtonIdentifier.class
      },

      { checkbox("checkbox-before").noListen() + CONTENT + checkbox("checkbox-after").noListen().label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-checkbox-label")),
        null
      },

      { div("div-before").noListen().div("div", CONTENT).noListen().div("div-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "div")),
        null
      },

      { inputButton("inputButton-before").noListen().inputButton("inputButton", CONTENT).noListen().inputButton("inputButton-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButtonInput.class, "inputButton")),
        HtmlUnitInputButtonIdentifier.class
      },

      { inputFile("inputFile-before").noListen().inputFile("inputFile", CONTENT).noListen().inputFile("inputFile-after").noListen(),
        null,
        null
      },

      { inputImage("inputImg-before").noListen().inputImage("inputImg", CONTENT).noListen().inputImage("inputImg-after").noListen(),
        null,
        null
      },

      { inputPassword("inputPassword-before").noListen().inputPassword("inputPassword", CONTENT).noListen().inputPassword("inputPassword-after").noListen(),
        null,
        null
      },

      { inputReset("inputReset-before").noListen().inputReset("inputReset", CONTENT).noListen().inputReset("inputReset-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("inputSubmit-before").noListen().inputSubmit("inputSubmit", CONTENT).noListen().inputSubmit("inputSubmit-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        HtmlUnitInputSubmitIdentifier.class
      },

      { inputText("inputText-before").noListen().inputText("inputText", CONTENT).noListen().inputText("inputText-after").noListen(),
        null,
        null
      },

      { image("img-before").noListen().image("img", CONTENT).noListen().image("img-after").noListen(),
        null,
        null
      },

      { label("before").noListen().label("main", CONTENT).noListen().label("after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-main")),
        null
      },

      { radio("radio-before").noListen() + CONTENT + radio("radio-after").noListen().label("radio-label", CONTENT).noListen().radio("radio-label").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlLabel.class, "lbl-radio-label")),
        null
      },

      { select("select-before").noListen() + CONTENT + select("select").noListen().option("option-before").noListen().option("option", CONTENT).noListen().option("option-after").noListen() + select("select-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class)),
        null
      },

      { span("span-before").noListen().span("span", CONTENT).noListen().span("span-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSpan.class, "span")),
        null
      },

      { table("table-before").noListen() + CONTENT + table("table").noListen().tr("tr", 1).noListen() + table("table-after").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class),
            new ExpectedControl(HtmlTableDataCell.class, "table-tr-td")),
        null
      },

      { textArea("textArea-before").noListen().textArea("textArea", CONTENT).noListen().textArea("textArea-after").noListen(),
        null,
        null
      },
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
