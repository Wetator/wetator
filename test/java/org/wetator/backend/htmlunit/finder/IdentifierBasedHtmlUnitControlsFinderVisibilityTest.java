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
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.wetator.backend.WPath;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitAnchorIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputCheckBoxIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputImageIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputPasswordIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputRadioButtonIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputResetIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputSubmitIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitInputTextIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitOptionIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitSelectIdentifier;
import org.wetator.backend.htmlunit.control.identifier.HtmlUnitTextAreaIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * Tests for {@link IdentifierBasedHtmlUnitControlsFinder#find(WPath)} respecting CSS styles defining element
 * visibility.
 *
 * @author tobwoerk
 */
@RunWith(Parameterized.class)
public class IdentifierBasedHtmlUnitControlsFinderVisibilityTest
    extends AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  private static final String DISPLAY_NONE = "display: none;";
  private static final String VISIBILITY_HIDDEN = "visibility: hidden;";

  @Parameter(0)
  public Object htmlCode;
  @Parameter(1)
  public SortedEntryExpectation expected;
  @Parameter(2)
  public Object identifiers;

  @Parameters(name = "{0}")
  public static Collection<Object[]> provideParameters() {
    final Object[][] tmpData = new Object[][] { //
    // @formatter:off
      { a("dn", CONTENT).style(DISPLAY_NONE).a("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitAnchorIdentifier.class
      },

      { button("dn", CONTENT).style(DISPLAY_NONE).button("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitButtonIdentifier.class
      },

      { CONTENT + checkbox("dn").style(DISPLAY_NONE).checkbox("vh").style(VISIBILITY_HIDDEN),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class)),
        HtmlUnitInputCheckBoxIdentifier.class
      },

      { div("dn", CONTENT).style(DISPLAY_NONE).div("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        null
      },

      { inputButton("dn", CONTENT).style(DISPLAY_NONE).inputButton("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputButtonIdentifier.class
      },

      { inputImage("dn", CONTENT).style(DISPLAY_NONE).inputImage("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputImageIdentifier.class
      },

      { inputPassword("dn", CONTENT).style(DISPLAY_NONE).inputPassword("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputPasswordIdentifier.class
      },

      { inputReset("dn", CONTENT).style(DISPLAY_NONE).inputReset("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputResetIdentifier.class
      },

      { inputSubmit("dn", CONTENT).style(DISPLAY_NONE).inputSubmit("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputSubmitIdentifier.class
      },

      { inputText("dn", CONTENT).style(DISPLAY_NONE).inputText("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitInputTextIdentifier.class
      },

      { image("dn", CONTENT).style(DISPLAY_NONE).image("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        null
      },

      { label("dn", CONTENT).style(DISPLAY_NONE).label("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        null
      },

      { CONTENT + radio("dn").style(DISPLAY_NONE).radio("vh").style(VISIBILITY_HIDDEN),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class)),
        HtmlUnitInputRadioButtonIdentifier.class
      },

      { select("dn", CONTENT).style(DISPLAY_NONE).option("option", CONTENT).build() + select("vh", CONTENT).style(DISPLAY_NONE).option("option", CONTENT),
        null,
        Arrays.asList(HtmlUnitSelectIdentifier.class, HtmlUnitOptionIdentifier.class)
      },

      { span("dn", CONTENT).style(DISPLAY_NONE).span("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        null
      },

      { CONTENT + table("dn").style(DISPLAY_NONE) + table("vh").style(VISIBILITY_HIDDEN),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlBody.class)),
        null
      },

      { textArea("dn", CONTENT).style(DISPLAY_NONE).textArea("vh", CONTENT).style(VISIBILITY_HIDDEN),
        null,
        HtmlUnitTextAreaIdentifier.class
      },

      //++++++++++++
      // label cases

      { label("dn", CONTENT).noListen().checkbox("dn").style(DISPLAY_NONE).label("vh", CONTENT).noListen().checkbox("vh").style(VISIBILITY_HIDDEN),
        new SortedEntryExpectation(
            new ExpectedControl(HtmlCheckBoxInput.class, "dn"),
            new ExpectedControl(HtmlCheckBoxInput.class, "vh"),
            new ExpectedControl(HtmlLabel.class, "lbl-dn"),
            new ExpectedControl(HtmlLabel.class, "lbl-vh")), // FIXME label finds invisible controls?
        HtmlUnitInputCheckBoxIdentifier.class
      },

      { label("dn", CONTENT).style(DISPLAY_NONE).checkbox("dn").label("vh", CONTENT).style(VISIBILITY_HIDDEN).checkbox("vh"),
        null,
        HtmlUnitInputCheckBoxIdentifier.class
      }
      // @formatter:on
    };

    return Arrays.asList(tmpData);
  }

  @Override
  protected IdentifierBasedHtmlUnitControlsFinder createFinder() {
    return new IdentifierBasedHtmlUnitControlsFinder(htmlPageIndex, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setup(final String anHtmlCode) throws IOException {
    super.setup(anHtmlCode);

    if (identifiers instanceof Class) {
      addIdentifiers((Class<? extends AbstractHtmlUnitControlIdentifier>) identifiers);
    } else if (identifiers != null) {
      addIdentifiers((List<Class<? extends AbstractHtmlUnitControlIdentifier>>) identifiers);
    }
  }

  @Test
  public void checkFoundElementsFocus() throws Exception {
    checkFoundElements(htmlCode, expected);
  }
}
