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

import java.util.Arrays;
import java.util.Collection;

import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlTextInput;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTextInput}s.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderInputFileTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputTextTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++
      // one text input

      // 0
      { CONTENT + inputText("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputText("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 2
      { inputText("input") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++
      // label + text input

      // 3
      { label("input", CONTENT).noListen().inputText("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", CONTENT).inputText("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 5
      { inputText("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //++++++++++++++++++++++
      // text input with value

      // 6
      { inputText("input").value(CONTENT),
        null
      },

      // 7
      { inputText("input").value(CONTENT).inputText("input-after"), // FIXME inputText.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input-after"))
      },

      //+++++++++++++++++++++++
      // subsequent text inputs

      // 8
      { CONTENT + inputText("input1") + CONTENT + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + inputText("input1") + "x" + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + "x" + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { CONTENT + "x" + inputText("input1") + CONTENT + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
