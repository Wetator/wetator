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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Common ground for {@link IdentifierBasedHtmlUnitControlsFinder} tests.
 *
 * @author tobwoerk
 */
public abstract class AbstractIdentifierBasedHtmlUnitControlsFinderTest {

  private WetatorConfiguration config;
  protected HtmlUnitControlRepository repository;

  protected HtmlPageIndex htmlPageIndex;
  protected IdentifierBasedHtmlUnitControlsFinder finder;
  // FIXME [UNKNOWN] remove as soon as included in MouseActionListeningHtmlUnitControlsFinder
  protected UnknownHtmlUnitControlsFinder finderUnknown;

  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  @Before
  public void createControlRepository() {
    repository = new HtmlUnitControlRepository();
    repository.add(HtmlUnitAnchor.class);
    repository.add(HtmlUnitButton.class);
    repository.add(HtmlUnitImage.class);
    repository.add(HtmlUnitInputButton.class);
    repository.add(HtmlUnitInputCheckBox.class);
    repository.add(HtmlUnitInputFile.class);
    repository.add(HtmlUnitInputImage.class);
    repository.add(HtmlUnitInputPassword.class);
    repository.add(HtmlUnitInputRadioButton.class);
    repository.add(HtmlUnitInputReset.class);
    repository.add(HtmlUnitInputSubmit.class);
    repository.add(HtmlUnitInputText.class);
    repository.add(HtmlUnitOption.class);
    repository.add(HtmlUnitOptionGroup.class);
    repository.add(HtmlUnitSelect.class);
    repository.add(HtmlUnitTextArea.class);
  }

  public void checkFoundElements(final Object anHtmlCode, final SortedEntryExpectation anExpected) throws Exception {
    if (anHtmlCode instanceof HtmlCodeBuilder) {
      checkFoundElements(((HtmlCodeBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof HtmlCodeSelectBuilder) {
      checkFoundElements(((HtmlCodeSelectBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof HtmlCodeTableBuilder) {
      checkFoundElements(((HtmlCodeTableBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof String) {
      checkFoundElements((String) anHtmlCode, anExpected);
    } else {
      throw new RuntimeException("'" + anHtmlCode + "' of wrong type (" + anHtmlCode.getClass().getSimpleName() + ").");
    }
  }

  private void checkFoundElements(final String anHtmlCode, final SortedEntryExpectation anExpected) throws Exception {
    setup(anHtmlCode);
    final WeightedControlList tmpFound = find();
    assertion(anExpected, tmpFound);
  }

  protected void setup(final String anHtmlCode) throws IOException {
    final HtmlPage tmpHtmlPage = PageUtil
        .constructHtmlPage(HtmlCodeCreator.pageStart() + anHtmlCode + HtmlCodeCreator.pageEnd());
    htmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    finder = createFinder();
    finderUnknown = new UnknownHtmlUnitControlsFinder(htmlPageIndex, repository);
  }

  protected abstract IdentifierBasedHtmlUnitControlsFinder createFinder();

  protected final void addIdentifiers(final List<Class<? extends AbstractHtmlUnitControlIdentifier>> anIdentifiers) {
    for (Class<? extends AbstractHtmlUnitControlIdentifier> tmpIdentifier : anIdentifiers) {
      finder.addIdentifier(tmpIdentifier);
    }
  }

  @SafeVarargs
  protected final void addIdentifiers(final Class<? extends AbstractHtmlUnitControlIdentifier>... anIdentifiers) {
    addIdentifiers(Arrays.asList(anIdentifiers));
  }

  protected final WeightedControlList find() throws InvalidInputException {
    final WPath tmpWPath = new WPath(new SecretString(getWPath()), config);
    final WeightedControlList tmpList = finder.find(tmpWPath);
    tmpList.addAll(finderUnknown.find(tmpWPath));
    return tmpList;
  }

  protected String getWPath() {
    return CONTENT;
  }

  protected final void assertion(final SortedEntryExpectation anExpected, final WeightedControlList anActual) {
    WeightedControlListEntryAssert.assertEntriesSorted(anExpected, anActual);
  }

  @AfterClass
  public static void resetListenersInCreator() {
    HtmlCodeCreator.resetListeners();
  }

  protected static HtmlCodeBuilder a(final String anId, final HtmlCodeBuilder aContent) {
    return a(anId, aContent.build());
  }

  protected static HtmlCodeBuilder a(final String anId, final String aContent) {
    return new HtmlCodeBuilder().a(anId, aContent);
  }

  protected static HtmlCodeBuilder a(final String anId) {
    return new HtmlCodeBuilder().a(anId);
  }

  protected static HtmlCodeBuilder button(final String anId, final HtmlCodeBuilder aContent) {
    return button(anId, aContent.build());
  }

  protected static HtmlCodeBuilder button(final String anId, final String aContent) {
    return new HtmlCodeBuilder().button(anId, aContent);
  }

  protected static HtmlCodeBuilder button(final String anId) {
    return new HtmlCodeBuilder().button(anId);
  }

  protected static HtmlCodeBuilder checkbox(final String anId) {
    return new HtmlCodeBuilder().checkbox(anId);
  }

  protected static HtmlCodeBuilder div(final String anId, final HtmlCodeBuilder aContent) {
    return div(anId, aContent.build());
  }

  protected static HtmlCodeBuilder div(final String anId, final String aContent) {
    return new HtmlCodeBuilder().div(anId, aContent);
  }

  protected static HtmlCodeBuilder div(final String anId) {
    return new HtmlCodeBuilder().div(anId);
  }

  protected static HtmlCodeBuilder image(final String anId, final String anAltText) {
    return new HtmlCodeBuilder().image(anId, anAltText);
  }

  protected static HtmlCodeBuilder image(final String anId) {
    return new HtmlCodeBuilder().image(anId);
  }

  protected static HtmlCodeBuilder inputButton(final String anId, final String aValue) {
    return new HtmlCodeBuilder().inputButton(anId, aValue);
  }

  protected static HtmlCodeBuilder inputButton(final String anId) {
    return new HtmlCodeBuilder().inputButton(anId);
  }

  protected static HtmlCodeBuilder inputImage(final String anId, final String anAltTetxValue) {
    return new HtmlCodeBuilder().inputImage(anId, anAltTetxValue);
  }

  protected static HtmlCodeBuilder inputImage(final String anId) {
    return new HtmlCodeBuilder().inputImage(anId);
  }

  protected static HtmlCodeBuilder inputReset(final String anId, final String aValue) {
    return new HtmlCodeBuilder().inputReset(anId, aValue);
  }

  protected static HtmlCodeBuilder inputReset(final String anId) {
    return new HtmlCodeBuilder().inputReset(anId);
  }

  protected static HtmlCodeBuilder inputSubmit(final String anId) {
    return new HtmlCodeBuilder().inputSubmit(anId);
  }

  protected static HtmlCodeBuilder inputText(final String anId, final String aPlaceholder) {
    return new HtmlCodeBuilder().inputText(anId, aPlaceholder);
  }

  protected static HtmlCodeBuilder inputText(final String anId) {
    return new HtmlCodeBuilder().inputText(anId);
  }

  protected static HtmlCodeBuilder label(final String anId, final HtmlCodeBuilder aContent) {
    return label(anId, aContent.build());
  }

  protected static HtmlCodeBuilder label(final String aForId, final String aContent) {
    return new HtmlCodeBuilder().label(aForId, aContent);
  }

  protected static HtmlCodeBuilder label(final String aForId) {
    return new HtmlCodeBuilder().label(aForId);
  }

  protected static HtmlCodeBuilder radio(final String anId) {
    return new HtmlCodeBuilder().radio(anId);
  }

  protected static HtmlCodeSelectBuilder select(final String anId, final String aName) {
    return HtmlCodeSelectBuilder.select(anId, aName);
  }

  protected static HtmlCodeSelectBuilder select(final String anId) {
    return HtmlCodeSelectBuilder.select(anId);
  }

  protected static HtmlCodeBuilder span(final String anId, final HtmlCodeBuilder aContent) {
    return span(anId, aContent.build());
  }

  protected static HtmlCodeBuilder span(final String anId, final String aContent) {
    return new HtmlCodeBuilder().span(anId, aContent);
  }

  protected static HtmlCodeBuilder span(final String anId) {
    return new HtmlCodeBuilder().span(anId);
  }

  protected static HtmlCodeTableBuilder table(final String anId) {
    return HtmlCodeTableBuilder.table(anId);
  }
}
