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

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTextInput}s.
 *
 * @author tobwoerk
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

      // 3
      { inputText("input").value(CONTENT),
        null
      },

      //+++++++++++++++++++
      // label + text input

      // 4
      { label("input", CONTENT).noListen().inputText("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 5
      { label("input", CONTENT).inputText("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlTextInput.class, "input"))
      },

      // 6
      { inputText("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //+++++++++++++++++++++++
      // subsequent text inputs

      // 7
      { CONTENT + inputText("input1") + CONTENT + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 8
      { CONTENT + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + inputText("input1") + "x" + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + "x" + inputText("input1") + inputText("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextInput.class, "input1"),
            new ExpectedControl(HtmlTextInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
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
