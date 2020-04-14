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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlButton}s and according specialties.
 *
 * @author tobwoerk
 */
public class MouseActionListeningHtmlUnitControlsFinderButtonTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //++++++++++++++++++++++++++++++++++++++++++
      // one button, with nested clickable content

      // 0
      { button("out", CONTENT + div("in")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 1
      { button("out", CONTENT + image("in")),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"),
            new ExpectedControl(HtmlImage.class, "in"))
      },

      // 2
      { button("out", div("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 3
      { button("out", image("in", CONTENT)),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImage.class, "in"),
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 4
      { button("out", div("in") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 5
      { button("out", image("in") + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      },

      //++++++++++++++++++++++++++++++++++++++++++++++
      // one button, with nested non-clickable content

      // 6
      { button("out", CONTENT + div("in").noListen()),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 7
      { button("out", CONTENT + image("in").noListen()),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"),
            new ExpectedControl(HtmlImage.class, "in"))
      },

      // 8
      { button("out", div("in", CONTENT).noListen()),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlDivision.class, "in"),
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 9
      { button("out", image("in", CONTENT).noListen()),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlImage.class, "in"),
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 10
      { button("out", div("in").noListen() + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      },

      // 11
      { button("out", image("in").noListen() + CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlButton.class, "out"))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    addIdentifiers(HtmlUnitButtonIdentifier.class);
  }

}
