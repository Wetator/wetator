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
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlSelect;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlSelect}s and their children.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderSelectSeparatorTest
 */
public class MouseActionListeningHtmlUnitControlsFinderSelectTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //+++++++++++
      // one select

      // 0
      { CONTENT + select("select").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { select("select", CONTENT).option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"))
      },

      // 2
      { select("select").option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      // 3
      { select("select").option("option") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 4
      { CONTENT + select("select").option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlBody.class))
      },

      // 5
      { select("select", CONTENT).option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      // 6
      { label("select", CONTENT).noListen().build() + select("select").option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlOption.class, "select-option"),
            new ExpectedControl(HtmlLabel.class, "lbl-select"))
      },

      //+++++++++++++++++++++++++++++++++
      // one select, select not listening

      // 7
      { CONTENT + select("select").noListen().option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 8
      { select("select", CONTENT).noListen().option("option"),
        null
      },

      // 9
      { select("select").noListen().option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      // 10
      { select("select").noListen().option("option") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { select("select", CONTENT).noListen().option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      //+++++++++++++++++++++++++++++++++
      // one select, option not listening

      // 12
      { CONTENT + select("select").option("option").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlBody.class))
      },

      // 13
      { select("select", CONTENT).option("option").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"))
      },

      // 14
      { select("select").option("option", CONTENT).noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      // 15
      { select("select").option("option").noListen() + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 16
      { select("select", CONTENT).option("option", CONTENT).noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select"),
            new ExpectedControl(HtmlOption.class, "select-option"))
      },

      //++++++++++++++++++++++++++++++
      // one select, nothing listening

      // 17
      { CONTENT + select("select").noListen().option("option").noListen(),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 18
      { select("select", CONTENT).noListen().option("option").noListen(),
        null
      },

      // 19
      { select("select").noListen().option("option", CONTENT).noListen(),
        null
      },

      // 20
      { select("select").noListen().option("option").noListen() + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      // 21
      { select("select", CONTENT).noListen().option("option", CONTENT).noListen(),
        null
      },

      //+++++++++++++++++++
      // subsequent selects

      // 22
      { CONTENT + select("select1").option("option") + CONTENT + select("select2").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 23
      { CONTENT + select("select1").option("option") + select("select2").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 24
      { CONTENT + select("select1").option("option") + "x" + select("select2").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 25
      { CONTENT + "x" + select("select1").option("option") + select("select2").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 26
      { CONTENT + "x" + select("select1").option("option") + CONTENT + select("select2").option("option"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 27
      { CONTENT + select("select1").option("option", CONTENT) + CONTENT + select("select2").option("option", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select1-option"),
            new ExpectedControl(HtmlOption.class, "select2-option"),
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 28
      { CONTENT + select("select1").option("option1").option("option2", CONTENT) + CONTENT + select("select2").option("option1", CONTENT).option("option2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlOption.class, "select1-option2"),
            new ExpectedControl(HtmlOption.class, "select2-option1"),
            new ExpectedControl(HtmlSelect.class, "select1"),
            new ExpectedControl(HtmlSelect.class, "select2"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
