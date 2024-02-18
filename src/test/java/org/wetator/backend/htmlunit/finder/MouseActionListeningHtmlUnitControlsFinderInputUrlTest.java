/*
 * Copyright (c) 2008-2024 wetator.org
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
import org.htmlunit.html.HtmlUrlInput;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlUrlInput}s.
 *
 * @author tobwoerk
 * @author rbri
 * @see MouseActionListeningHtmlUnitControlsFinderInputFileTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputUrlTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++
      // one url input

      // 0
      { CONTENT + inputUrl("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputUrl("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input"))
      },

      // 2
      { inputUrl("input") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++
      // label + url input

      // 3
      { label("input", CONTENT).noListen().inputUrl("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", CONTENT).inputUrl("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlUrlInput.class, "input"))
      },

      // 5
      { inputUrl("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //++++++++++++++++++++++
      // url input with value

      // 6
      { inputUrl("input").value(CONTENT),
        null
      },

      // 7
      { inputUrl("input").value(CONTENT).inputUrl("input-after"), // FIXME inputUrl.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input-after"))
      },

      //+++++++++++++++++++++++
      // subsequent url inputs

      // 8
      { CONTENT + inputUrl("input1") + CONTENT + inputUrl("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input1"),
            new ExpectedControl(HtmlUrlInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + inputUrl("input1") + inputUrl("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input1"),
            new ExpectedControl(HtmlUrlInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + inputUrl("input1") + "x" + inputUrl("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + "x" + inputUrl("input1") + inputUrl("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input1"),
            new ExpectedControl(HtmlUrlInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { CONTENT + "x" + inputUrl("input1") + CONTENT + inputUrl("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlUrlInput.class, "input2"),
            new ExpectedControl(HtmlUrlInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
