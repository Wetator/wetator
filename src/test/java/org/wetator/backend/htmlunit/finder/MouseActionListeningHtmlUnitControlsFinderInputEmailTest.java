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

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import org.htmlunit.html.HtmlBody;
import org.htmlunit.html.HtmlEmailInput;
import org.htmlunit.html.HtmlLabel;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlEmailInput}s.
 *
 * @author tobwoerk
 * @author rbri
 * @see MouseActionListeningHtmlUnitControlsFinderInputFileTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputEmailTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++
      // one email input

      // 0
      { CONTENT + inputEmail("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputEmail("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input"))
      },

      // 2
      { inputEmail("input") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++
      // label + email input

      // 3
      { label("input", CONTENT).noListen().inputEmail("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", CONTENT).inputEmail("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlEmailInput.class, "input"))
      },

      // 5
      { inputEmail("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //++++++++++++++++++++++
      // email input with value

      // 6
      { inputEmail("input").value(CONTENT),
        null
      },

      // 7
      { inputEmail("input").value(CONTENT).inputEmail("input-after"), // FIXME inputEmail.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input-after"))
      },

      //+++++++++++++++++++++++
      // subsequent email inputs

      // 8
      { CONTENT + inputEmail("input1") + CONTENT + inputEmail("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input1"),
            new ExpectedControl(HtmlEmailInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + inputEmail("input1") + inputEmail("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input1"),
            new ExpectedControl(HtmlEmailInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + inputEmail("input1") + "x" + inputEmail("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + "x" + inputEmail("input1") + inputEmail("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input1"),
            new ExpectedControl(HtmlEmailInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { CONTENT + "x" + inputEmail("input1") + CONTENT + inputEmail("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlEmailInput.class, "input2"),
            new ExpectedControl(HtmlEmailInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
