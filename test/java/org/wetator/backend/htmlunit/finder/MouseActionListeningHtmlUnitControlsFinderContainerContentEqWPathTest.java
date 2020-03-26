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

import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeBuilder.div;

import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.CONTENT;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlDivision}s and text content exactly matching the WPath.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderContainerContentDiffWPathTest tests from here with multiple
 *      element hits lead to
 *      test cases in check sister test class
 */
public class MouseActionListeningHtmlUnitControlsFinderContainerContentEqWPathTest
    extends AbstractMouseClickListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //++++++++
      // one div

      // 0
      { CONTENT + div("div"),
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 1
      { div("div", CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "div"))
      },

      // 2
      { div("div") + CONTENT,
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      //++++++++++++++++++++++++++++++
      // two nested divs, content once

      // 3
      { CONTENT + div("out", div("in")),
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 4
      { div("out", CONTENT + div("in")),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 5
      { div("out", div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 6
      { div("out", div("in") + CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 7
      { div("out", div("in")) + CONTENT,
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++++++++++++++
      // two nested divs, content twice

      // 8
      { CONTENT + div("out", CONTENT + div("in")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + div("out", div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + div("out", div("in") + CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + div("out", div("in")) + CONTENT,
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 12
      { div("out", CONTENT + div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 13
      { div("out", CONTENT + div("in") + CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 14
      { div("out", CONTENT + div("in")) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 15
      { div("out", div("in", CONTENT) + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 16
      { div("out", div("in", CONTENT)) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 17
      { div("out", div("in") + CONTENT) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      //++++++++++++++++++++++++++++++++
      // two nested divs, content thrice

      // 18
      { CONTENT + div("out", CONTENT + div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 19
      { CONTENT + div("out", CONTENT + div("in") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 20
      { CONTENT + div("out", CONTENT + div("in")) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 21
      { CONTENT + div("out", div("in", CONTENT) + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 22
      { CONTENT + div("out", div("in", CONTENT)) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 23
      { CONTENT + div("out", div("in") + CONTENT) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 24
      { div("out", CONTENT + div("in", CONTENT) + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 25
      { div("out", CONTENT + div("in", CONTENT)) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 26
      { div("out", CONTENT + div("in") + CONTENT) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      // 27
      { div("out", div("in", CONTENT) + CONTENT) + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlDivision.class, "out"),
            new ExpectedControl(HtmlBody.class))
      },

      //++++++++++++++++++++++++++++++++++
      // two subsequent divs, content once

      // 28
      { CONTENT + div("first") + div("second"),
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 29
      { div("first", CONTENT).build() + div("second"),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "first"))
      },

      // 30
      { div("first") + CONTENT + div("second"),
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 31
      { div("first").build() + div("second", CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "second"))
      },

      // 32
      { div("first").build() + div("second") + CONTENT,
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      //+++++++++++++++++++++++++++++++++++
      // two subsequent divs, content twice

      // 33
      { CONTENT + div("first", CONTENT) + div("second"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlBody.class))
      },

      // 34
      { CONTENT + div("first") + CONTENT + div("second"),
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 35
      { CONTENT + div("first") + div("second", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlBody.class))
      },

      // 36
      { CONTENT + div("first") + div("second") + CONTENT,
        new SortedEntryExpectation(new ExpectedControl(HtmlBody.class))
      },

      // 37
      { div("first", CONTENT) + CONTENT + div("second"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlBody.class))
      },

      // 38
      { div("first", CONTENT).build() + div("second", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"))
      },

      // 39
      { div("first", CONTENT).build() + div("second") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlBody.class))
      },

      //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      // two subsequent divs nested in a third div, content once

      // 40
      { div("out", CONTENT + div("first") + div("second")),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 41
      { div("out", div("first", CONTENT).build() + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 42
      { div("out", div("first") + CONTENT + div("second")),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 43
      { div("out", div("first").build() + div("second", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 44
      { div("out", div("first").build() + div("second") + CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      // two subsequent divs nested in a third div, content twice

      // 45
      { div("out", CONTENT + div("first", CONTENT) + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 46
      { div("out", CONTENT + div("first") + CONTENT + div("second")),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 47
      { div("out", CONTENT + div("first") + div("second", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 48
      { div("out", CONTENT + div("first") + div("second") + CONTENT),
        new SortedEntryExpectation(new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 49
      { div("out", div("first", CONTENT) + CONTENT + div("second")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 50
      { div("out", div("first", CONTENT).build() + div("second", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "second"),
            new ExpectedControl(HtmlDivision.class, "out"))
      },

      // 51
      { div("out", div("first", CONTENT).build() + div("second") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "first"),
            new ExpectedControl(HtmlDivision.class, "out"))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
