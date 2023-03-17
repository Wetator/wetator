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

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlNumberInput;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlNumberInput}s.
 *
 * @author tobwoerk
 * @author rbri
 * @see MouseActionListeningHtmlUnitControlsFinderInputFileTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputNumberTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputNumberTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  private static final String NUMBER_CONTENT = "42";

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++++++
      // one number input

      // 0
      { NUMBER_CONTENT + inputNumber("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputNumber("input", NUMBER_CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input"))
      },

      // 2
      { inputNumber("input") + NUMBER_CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++++++
      // label + number input

      // 3
      { label("input", NUMBER_CONTENT).noListen().inputNumber("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", NUMBER_CONTENT).inputNumber("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlNumberInput.class, "input"))
      },

      // 5
      { inputNumber("input").label("input", NUMBER_CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //++++++++++++++++++++++++++
      // number input with value

      // 6
      { inputNumber("input").value(NUMBER_CONTENT),
        null
      },

      // 7
      { inputNumber("input").value(NUMBER_CONTENT).inputNumber("input-after"), // FIXME inputNumber.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input-after"))
      },

      //+++++++++++++++++++++++++++
      // subsequent number inputs

      // 8
      { NUMBER_CONTENT + inputNumber("input1") + NUMBER_CONTENT + inputNumber("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input1"),
            new ExpectedControl(HtmlNumberInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { NUMBER_CONTENT + inputNumber("input1") + inputNumber("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input1"),
            new ExpectedControl(HtmlNumberInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { NUMBER_CONTENT + inputNumber("input1") + "x" + inputNumber("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { NUMBER_CONTENT + "x" + inputNumber("input1") + inputNumber("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input1"),
            new ExpectedControl(HtmlNumberInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { NUMBER_CONTENT + "x" + inputNumber("input1") + NUMBER_CONTENT + inputNumber("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlNumberInput.class, "input2"),
            new ExpectedControl(HtmlNumberInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected String getWPath() {
    return NUMBER_CONTENT;
  }
}
