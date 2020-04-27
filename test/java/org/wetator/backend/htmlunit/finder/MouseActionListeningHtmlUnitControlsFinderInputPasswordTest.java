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
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlPasswordInput}s.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderInputTextTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++++++
      // one password input

      // 0
      { CONTENT + inputPassword("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputPassword("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input"))
      },

      // 2
      { inputPassword("input") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++++++
      // label + password input

      // 3
      { label("input", CONTENT).noListen().inputPassword("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", CONTENT).inputPassword("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlPasswordInput.class, "input"))
      },

      // 5
      { inputPassword("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //++++++++++++++++++++++++++
      // password input with value
      // 6
      { inputPassword("input").value(CONTENT),
        null
      },

      // 7
      { inputPassword("input").value(CONTENT).inputPassword("input-after"), // FIXME inputPasword.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input-after"))
      },

      //+++++++++++++++++++++++++++
      // subsequent password inputs

      // 8
      { CONTENT + inputPassword("input1") + CONTENT + inputPassword("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input1"),
            new ExpectedControl(HtmlPasswordInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + inputPassword("input1") + inputPassword("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input1"),
            new ExpectedControl(HtmlPasswordInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + inputPassword("input1") + "x" + inputPassword("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + "x" + inputPassword("input1") + inputPassword("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input1"),
            new ExpectedControl(HtmlPasswordInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { CONTENT + "x" + inputPassword("input1") + CONTENT + inputPassword("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlPasswordInput.class, "input2"),
            new ExpectedControl(HtmlPasswordInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
