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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.wetator.backend.WeightedControlList;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Assertion helper for testing with {@link org.wetator.backend.WeightedControlList.Entry}s.
 *
 * @author tobwoerk
 */
public abstract class WeightedControlListEntryAssert {

  private static final Pattern ID_PATTERN = Pattern.compile("\\(id=.*\\)");

  public static void assertEntriesSorted(final SortedEntryExpectation anExpected, final WeightedControlList anActual) {
    Assert.assertEquals(anExpected == null ? "" : anExpected.toString(),
        toSortedEntriesExpectationString(anActual.getEntriesSorted()));
  }

  private static String toSortedEntriesExpectationString(final List<WeightedControlList.Entry> anEntryList) {
    final StringBuilder tmpEntryList = new StringBuilder();
    for (WeightedControlList.Entry tmpEntry : anEntryList) {
      String tmpDescribingText = tmpEntry.getControl().getDescribingText();

      if (tmpDescribingText.startsWith("[Unknown HtmlElement")) {
        tmpDescribingText = tmpDescribingText
            .substring(tmpDescribingText.lastIndexOf('.') + 1, tmpDescribingText.length() - 1).replaceFirst("\'", " ");
      } else {
        tmpDescribingText = tmpDescribingText.substring(1, tmpDescribingText.length() - 1);
      }

      String tmpEntryExpectation = tmpDescribingText.substring(0, tmpDescribingText.indexOf(' '));

      final Matcher tmpIDMatcher = ID_PATTERN.matcher(tmpDescribingText);
      if (tmpIDMatcher.find()) {
        tmpEntryExpectation += tmpDescribingText.substring(tmpIDMatcher.start() - 1,
            tmpDescribingText.indexOf(')') + 1);
      }
      tmpEntryList.append(tmpEntryExpectation.trim());

      if (anEntryList.indexOf(tmpEntry) != anEntryList.size() - 1) {
        tmpEntryList.append('\n');
      }
    }
    return tmpEntryList.toString();
  }

  public static class SortedEntryExpectation {
    final List<ExpectedControl> entries = new LinkedList<>();

    SortedEntryExpectation(final ExpectedControl... aControls) {
      for (ExpectedControl tmpControl : aControls) {
        entries.add(tmpControl);
      }
    }

    @Override
    public String toString() {
      final StringBuilder tmpEntryList = new StringBuilder();
      for (ExpectedControl tmpEntry : entries) {
        tmpEntryList
            .append(tmpEntry.element.getSimpleName() + (tmpEntry.id != null ? " (id='" + tmpEntry.id + "')" : ""));

        if (entries.indexOf(tmpEntry) != entries.size() - 1) {
          tmpEntryList.append('\n');
        }
      }
      return tmpEntryList.toString();
    }
  }

  public static class ExpectedControl {
    final Class<? extends HtmlElement> element;
    final String id;

    ExpectedControl(final Class<? extends HtmlElement> anExpectedHtmlElement, final String anExpectedID) {
      element = anExpectedHtmlElement;
      id = anExpectedID;
    }

    ExpectedControl(final Class<? extends HtmlElement> anExpectedHtmlElement) {
      this(anExpectedHtmlElement, null);
    }
  }
}
