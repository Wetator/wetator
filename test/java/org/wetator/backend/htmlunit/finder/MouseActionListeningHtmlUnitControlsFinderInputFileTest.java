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
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlFileInput}s.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputTextTest
 * @see MouseActionListeningHtmlUnitControlsFinderTextAreaTest
 */
public class MouseActionListeningHtmlUnitControlsFinderInputFileTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++++++
      // one file input

      // 0
      { CONTENT + inputFile("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { inputFile("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input"))
      },

      // 2
      { inputFile("input") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++
      // label + file input

      // 3
      { label("input", CONTENT).noListen().inputFile("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      // 4
      { label("input", CONTENT).inputFile("input"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-input"),
            new ExpectedControl(HtmlFileInput.class, "input"))
      },

      // 5
      { inputFile("input").label("input", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input"),
            new ExpectedControl(HtmlLabel.class, "lbl-input"))
      },

      //+++++++++++++++++++++++
      // subsequent file inputs

      // 6
      { CONTENT + inputFile("input1") + CONTENT + inputFile("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input1"),
            new ExpectedControl(HtmlFileInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 7
      { CONTENT + inputFile("input1") + inputFile("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input1"),
            new ExpectedControl(HtmlFileInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 8
      { CONTENT + inputFile("input1") + "x" + inputFile("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + "x" + inputFile("input1") + inputFile("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input1"),
            new ExpectedControl(HtmlFileInput.class, "input2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + "x" + inputFile("input1") + CONTENT + inputFile("input2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlFileInput.class, "input2"),
            new ExpectedControl(HtmlFileInput.class, "input1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
