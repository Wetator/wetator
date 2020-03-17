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

import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.CONTENT;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.pageEnd;
import static org.wetator.backend.htmlunit.finder.ClickableHtmlCodeCreator.pageStart;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.ExpectedControl;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Common ground for {@link ClickableHtmlUnitControlsFinder} tests.
 *
 * @author tobwoerk
 */
public abstract class AbstractClickableHtmlUnitControlsFinderTest {

  protected WetatorConfiguration config;
  protected IdentifierBasedHtmlUnitControlsFinder finder;

  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  public void checkFoundElements(final String anHtmlCode, final SortedEntryExpectation anExpected) throws Exception {
    setup(anHtmlCode);
    final WeightedControlList tmpFound = find();
    assertion(anExpected, tmpFound);
  }

  public void checkFoundElements(final String anHtmlCode, final ExpectedControl... anExpected) throws Exception {
    checkFoundElements(anHtmlCode, new SortedEntryExpectation(anExpected));
  }

  protected void setup(final String anHtmlCode) throws IOException {
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(pageStart() + anHtmlCode + pageEnd());
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    finder = new ClickableHtmlUnitControlsFinder(tmpHtmlPageIndex, null, null);
  }

  @SafeVarargs
  protected final void addIdentifiers(final Class<? extends AbstractMatcherBasedIdentifier>... anIdentifiers) {
    finder.addIdentifiers(Arrays.asList(anIdentifiers));
  }

  protected final void addIdentifiers(final List<Class<? extends AbstractMatcherBasedIdentifier>> anIdentifiers) {
    for (Class<? extends AbstractMatcherBasedIdentifier> tmpIdentifier : anIdentifiers) {
      finder.addIdentifier(tmpIdentifier);
    }
  }

  protected final WeightedControlList find() throws InvalidInputException {
    return finder.find(new WPath(new SecretString(getWPath()), config));
  }

  protected String getWPath() {
    return CONTENT;
  }

  protected final void assertion(final SortedEntryExpectation anExpected, final WeightedControlList anActual) {
    WeightedControlListEntryAssert.assertEntriesSorted(anExpected, anActual);
  }

  protected final void assertion(final WeightedControlList anActual, final ExpectedControl... anExpected) {
    assertion(new SortedEntryExpectation(anExpected), anActual);
  }
}
