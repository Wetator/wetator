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
import org.htmlunit.html.HtmlDivision;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;
import org.wetator.core.WetatorConfiguration;

/**
 * Tests for element weighting during {@link MouseActionListeningHtmlUnitControlsFinder#find(WPath)} with a static set
 * of
 * {@link HtmlDivision}s and varying WPath to aim specific controls.
 *
 * @author tobwoerk
 */
@RunWith(Parameterized.class)
public class MouseActionListeningHtmlUnitControlsFinderContainerSeparatorWPathTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  private static final String SEP = WetatorConfiguration.DEFAULT_WPATH_SEPARATOR;

  @Parameter(0)
  public SortedEntryExpectation expected;
  @Parameter(1)
  public String wPath;

  @Before
  public void setup() throws Exception {
    if (finder == null) {
      HtmlCodeCreator.listenToClick();
      super.setup(CONTENT + "x" + div("out", "x" + CONTENT + "x" + div("in", "x" + CONTENT)));
    }
  }

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new MouseActionListeningHtmlUnitControlsFinder(htmlPageIndex, null, MouseAction.CLICK, repository);
  }

  @Parameters(name = "{index}: {1}")
  public static Collection<Object[]> provideParameters() {
    HtmlCodeCreator.listenToClick();

    final Object[][] tmpData = new Object[][] { //
      // @formatter:off
      { new SortedEntryExpectation(
          new ExpectedControl(HtmlDivision.class, "in"),
          new ExpectedControl(HtmlDivision.class, "out"),
          new ExpectedControl(HtmlBody.class)),
        CONTENT
      },

      { new SortedEntryExpectation(
          new ExpectedControl(HtmlDivision.class, "in"),
          new ExpectedControl(HtmlDivision.class, "out")),
        CONTENT + SEP + CONTENT
      },

      { new SortedEntryExpectation(
          new ExpectedControl(HtmlDivision.class, "in")),
        CONTENT + SEP + CONTENT + SEP + CONTENT
      },

      { new SortedEntryExpectation(
          new ExpectedControl(HtmlDivision.class, "out"),
          new ExpectedControl(HtmlBody.class)),
        CONTENT + "x x" + CONTENT
      },

      { new SortedEntryExpectation(
          new ExpectedControl(HtmlDivision.class, "out")),
        CONTENT + "x " + SEP + " x" + CONTENT + "x x" + CONTENT
      },
      // @formatter:on
    };
    return Arrays.asList(tmpData);
  }

  @Override
  protected String getWPath() {
    return wPath;
  }

  @Test
  public void checkFoundElements() throws Exception {
    final WeightedControlList tmpFound = find();
    assertion(expected, tmpFound);
  }
}
