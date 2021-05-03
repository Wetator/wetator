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

import com.gargoylesoftware.htmlunit.html.HtmlDivision;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlDivision}s and text content that differs from the WPath.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderContainerContentEqWPathTest check sister test class for
 *      structured suite that leads to these test cases
 */
public class MouseActionListeningHtmlUnitControlsFinderContainerContentDiffWPathTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      // 0
      { div("out", div("in", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 1
      { div("out", CONTENT + "x" + div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 2
      { div("out", CONTENT + div("in", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 3
      { div("out", CONTENT + "x" + div("in", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 4
      { div("out", div("in", CONTENT + "x") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 5
      { div("out", div("in", CONTENT) + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 6
      { div("out", CONTENT + "x" + div("in", CONTENT) + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 7
      { div("out", CONTENT + div("in", CONTENT + "x") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 8
      { div("out", CONTENT + div("in", CONTENT) + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 9
      { div("out", CONTENT + "x" + div("in", CONTENT + "x") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 10
      { div("out", CONTENT + "x" + div("in", CONTENT) + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 11
      { div("out", CONTENT + div("in", CONTENT + "x") + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 12
      { div("first", CONTENT + "x").div("second", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "first"))
      },

      // 13
      { div("first", CONTENT).div("second", CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"))
      },

      // 14
      { div("first", CONTENT + "x").div("second", CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"))
      },

      // 15
      { div("out", div("first", CONTENT + "x").div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 16
      { div("out", div("first").div("second", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 17
      { div("out", CONTENT + "x" + div("first", CONTENT) + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 18
      { div("out", CONTENT + div("first", CONTENT + "x") + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 19
      { div("out", CONTENT + "x" + div("first", CONTENT + "x") + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 20
      { div("out", CONTENT + "x" + div("first") + div("second", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 21
      { div("out", CONTENT + div("first") + div("second", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 22
      { div("out", CONTENT + "x" + div("first") + div("second", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 23
      { div("out", div("first", CONTENT + "x") + CONTENT + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 24
      { div("out", div("first", CONTENT) + CONTENT + "x" + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 25
      { div("out", div("first", CONTENT + "x") + CONTENT + "x" + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 26
      { div("out", div("first", CONTENT + "x").div("second", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 27
      { div("out", div("first", CONTENT).div("second", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 28
      { div("out", div("first", CONTENT + "x").div("second", CONTENT + "x")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 29
      { div("out", div("first", CONTENT + "x").div("second") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 30
      { div("out", div("first", CONTENT).div("second") + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 31
      { div("out", div("first", CONTENT + "x").div("second") + CONTENT + "x"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
