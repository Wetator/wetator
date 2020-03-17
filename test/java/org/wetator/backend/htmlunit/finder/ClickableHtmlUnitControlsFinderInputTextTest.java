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
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.inputText;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.label;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.labelClickable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Tests for element weighting during {@link ClickableHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTextInput}s.
 *
 * @author tobwoerk
 */
public class ClickableHtmlUnitControlsFinderInputTextTest
    extends AbstractClickableHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++
      // one text input

      // 0
      { CONTENT + inputText("input"),
        new SortedEntryExpectation(new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 1
      { inputText("input") + CONTENT,
        null
      },

      //+++++++++++++++++++
      // label + text input

      // 2
      { label("input", CONTENT) + inputText("input"),
        new SortedEntryExpectation(new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 3
      { labelClickable("input", CONTENT) + inputText("input"),
        new SortedEntryExpectation(
            // FIXME new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 4
      { inputText("input") + labelClickable("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"))
           // FIXME new ExpectedControl(HtmlLabel.class, "lbl-input")
      },

      //+++++++++++++++++++++++
      // subsequent text inputs
      // 5
      { CONTENT + inputText("input1") + CONTENT + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"))
      },

      // 6
      { CONTENT + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"))
      },

      // 7
      { CONTENT + inputText("input1") + "x" + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"))
      },

      // 8
      { CONTENT + "x" + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"))
      },

      // 9
      { CONTENT + "x" + inputText("input1") + CONTENT + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlTextInput.class, "input1"))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);
    addIdentifiers(HtmlUnitInputTextIdentifier.class);
  }
}
