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
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} on pages with
 * {@link HtmlTextArea}s.
 *
 * @author tobwoerk
 * @see MouseActionListeningHtmlUnitControlsFinderInputTextTest
 * @see MouseActionListeningHtmlUnitControlsFinderInputPasswordTest
 */
public class MouseActionListeningHtmlUnitControlsFinderTextAreaTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderParameterizedTest {

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> provideParameters() {
    listenToClick();

    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      //++++++++++++++
      // one text area

      // 0
      { CONTENT + textArea("textArea"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"),
            new ExpectedControl(HtmlBody.class))
      },

      // 1
      { textArea("textArea", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"))
      },

      // 2
      { textArea("textArea") + CONTENT,
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class))
      },

      //++++++++++++++++++
      // label + text area

      // 3
      { label("textArea", CONTENT).noListen().textArea("textArea"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"),
            new ExpectedControl(HtmlLabel.class, "lbl-textArea"))
      },

      // 4
      { label("textArea", CONTENT).textArea("textArea"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlLabel.class, "lbl-textArea"),
            new ExpectedControl(HtmlTextArea.class, "textArea"))
      },

      // 5
      { textArea("textArea").label("textArea", CONTENT),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea"),
            new ExpectedControl(HtmlLabel.class, "lbl-textArea"))
      },

      //+++++++++++++++++++++
      // text area with value

      // 6
      { textArea("textArea").value(CONTENT),
        null
      },

      // 7
      { textArea("textArea").value(CONTENT).textArea("textArea-after"), // FIXME textArea.value special desired?
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea-after"))
      },

      //++++++++++++++++++++++
      // subsequent text areas

      // 8
      { CONTENT + textArea("textArea1") + CONTENT + textArea("textArea2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea1"),
            new ExpectedControl(HtmlTextArea.class, "textArea2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 9
      { CONTENT + textArea("textArea1") + textArea("textArea2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea1"),
            new ExpectedControl(HtmlTextArea.class, "textArea2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 10
      { CONTENT + textArea("textArea1") + "x" + textArea("textArea2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea1"),
            new ExpectedControl(HtmlBody.class))
      },

      // 11
      { CONTENT + "x" + textArea("textArea1") + textArea("textArea2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea1"),
            new ExpectedControl(HtmlTextArea.class, "textArea2"),
            new ExpectedControl(HtmlBody.class))
      },

      // 12
      { CONTENT + "x" + textArea("textArea1") + CONTENT + textArea("textArea2"),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlTextArea.class, "textArea2"),
            new ExpectedControl(HtmlTextArea.class, "textArea1"),
            new ExpectedControl(HtmlBody.class))
      }
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }
}
