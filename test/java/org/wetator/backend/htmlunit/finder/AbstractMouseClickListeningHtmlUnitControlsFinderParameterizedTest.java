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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.wetator.backend.MouseAction;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;

/**
 * Common ground for parameterized {@link MouseActionListeningHtmlUnitControlsFinder} tests listening to
 * {@link MouseAction#CLICK}s.
 *
 * @author tobwoerk
 */
@RunWith(Parameterized.class)
public abstract class AbstractMouseClickListeningHtmlUnitControlsFinderParameterizedTest
    extends AbstractMouseActionListeningHtmlUnitControlsFinderTest {

  @Parameter(0)
  public String htmlCode;
  @Parameter(1)
  public SortedEntryExpectation expected;

  @Test
  public void checkFoundElementsClick() throws Exception {
    setMouseAction(MouseAction.CLICK);
    super.checkFoundElements(htmlCode, expected);
  }
}
