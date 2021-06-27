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


package org.wetator.backend.htmlunit.finder;

import static org.wetator.backend.htmlunit.finder.HtmlCodeCreator.CONTENT;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputFileIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputImageIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputPasswordIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputRadioButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputResetIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitSelectIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitTextAreaIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlResetInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Tests for element weighting during {@link IdentifierBasedHtmlUnitControlsFinder#find(WPath)} with the use
 * of control-specific identifiers.
 *
 * @author tobwoerk
 * @see IdentifierBasedHtmlUnitControlsFinderWithoutIdentifierTest
 */
@RunWith(Parameterized.class)
public class IdentifierBasedHtmlUnitControlsFinderWithIdentifierTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  @Parameter(0)
  public Object htmlCode;
  @Parameter(1)
  public SortedEntryExpectation expected;
  @Parameter(2)
  public Object identifiers;

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      { a("anchor-before").a("anchor", CONTENT).a("anchor-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlAnchor.class, "anchor")),
        HtmlUnitAnchorIdentifier.class
      },

      { button("button-value").value(CONTENT).button("button-before").button("button", CONTENT).button("button-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "button")),
        HtmlUnitButtonIdentifier.class
      },

      { checkbox("checkbox-value").value(CONTENT).checkbox("checkbox-before") + CONTENT + checkbox("checkbox-after").label("checkbox-label", CONTENT).noListen().checkbox("checkbox-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-label"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-before"),
            new ExpectedControl(HtmlCheckBoxInput.class, "checkbox-after")),
        HtmlUnitInputCheckBoxIdentifier.class
      },

      { inputButton("inputButton-value").value(CONTENT).inputButton("inputButton-before").inputButton("inputButton", CONTENT).inputButton("inputButton-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButtonInput.class, "inputButton-value"),
            new ExpectedControl(HtmlButtonInput.class, "inputButton")),
        HtmlUnitInputButtonIdentifier.class
      },

      { inputFile("inputFile-before").inputFile("inputFile", CONTENT).inputFile("inputFile-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "inputFile")),
        HtmlUnitInputFileIdentifier.class
      },

      { inputImage("inputImg-before").inputImage("inputImg", CONTENT).inputImage("inputImg-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImageInput.class, "inputImg")),
        HtmlUnitInputImageIdentifier.class
      },

      { inputPassword("inputPassword-before").inputPassword("inputPassword-value").value(CONTENT).inputPassword("inputPassword-between").inputPassword("inputPassword", CONTENT).inputPassword("inputPassword-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "inputPassword"),
            new ExpectedControl(HtmlPasswordInput.class, "inputPassword-between"), // FIXME inputPassword.value special desired?
            new ExpectedControl(HtmlPasswordInput.class, "inputPassword-after")),
        HtmlUnitInputPasswordIdentifier.class
      },

      { inputReset("inputReset-value").value(CONTENT).inputReset("inputReset-before").inputReset("inputReset", CONTENT).inputReset("inputReset-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlResetInput.class, "inputReset-value"),
            new ExpectedControl(HtmlResetInput.class, "inputReset")),
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("inputSubmit-value").value(CONTENT).inputSubmit("inputSubmit-before").inputSubmit("inputSubmit", CONTENT).inputSubmit("inputSubmit-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit-value"),
            new ExpectedControl(HtmlSubmitInput.class, "inputSubmit")),
        HtmlUnitInputSubmitIdentifier.class
      },

      { inputText("inputText-before").inputText("inputText-value").value(CONTENT).inputText("inputText-between").inputText("inputText", CONTENT).inputText("inputText-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "inputText"),
            new ExpectedControl(HtmlTextInput.class, "inputText-between"), // FIXME inputText.value special desired?
            new ExpectedControl(HtmlTextInput.class, "inputText-after")),
        HtmlUnitInputTextIdentifier.class
      },

      { radio("radio-value").value(CONTENT).radio("radio-before") + CONTENT + radio("radio-after").label("radio-label", CONTENT).noListen().radio("radio-label"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-label"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-before"),
            new ExpectedControl(HtmlRadioButtonInput.class, "radio-after")),
        HtmlUnitInputRadioButtonIdentifier.class
      },

      { select("select-before") + CONTENT + select("select").option("option-before").option("option", CONTENT).option("option-after") + select("select-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlSelect.class, "select-after")),
        Arrays.asList(HtmlUnitSelectIdentifier.class, HtmlUnitOptionIdentifier.class)
      },

      { textArea("textArea-before").textArea("textArea-value").value(CONTENT).textArea("textArea-between").textArea("textArea", CONTENT).textArea("textArea-after"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"),
            new ExpectedControl(HtmlTextArea.class, "textArea-between"), // FIXME textArea.value special desired?
            new ExpectedControl(HtmlTextArea.class, "textArea-after")),
        HtmlUnitTextAreaIdentifier.class
      },
      // @formatter:on
    };

    return Arrays.asList(tmpData);
  }

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    if (identifiers instanceof Class) {
      addIdentifiers((Class<? extends AbstractHtmlUnitControlIdentifier>) identifiers);
    } else if (identifiers != null) {
      addIdentifiers((List<Class<? extends AbstractHtmlUnitControlIdentifier>>) identifiers);
    }
  }

  @Test
  public void checkFoundElementsFocus() throws Exception {
    checkFoundElements(htmlCode, expected);
  }
}
