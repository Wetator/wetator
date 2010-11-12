/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.backend.htmlunit.finder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputFile;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputPassword;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputText;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitTextArea;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * In addition to the {@link IdentifierBasedHtmlUnitControlsFinder} this finder has a special support for empty
 * searches. This is needed for some search engines which have no label in front of their input field. Currently the
 * following settable {@link org.rbri.wet.backend.control.Control}s are supported:
 * <ul>
 * <li>{@link HtmlUnitInputFile}</li>
 * <li>{@link HtmlUnitInputPassword}</li>
 * <li>{@link HtmlUnitInputText}</li>
 * <li>{@link HtmlUnitTextArea}</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class SettableHtmlUnitControlsFinder extends IdentifierBasedHtmlUnitControlsFinder {

  /**
   * The constructor.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aThreadPool the thread pool to use for worker threads; may be null
   */
  public SettableHtmlUnitControlsFinder(HtmlPageIndex aHtmlPageIndex, ThreadPoolExecutor aThreadPool) {
    super(aHtmlPageIndex, aThreadPool);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.finder.IdentifierBasedHtmlUnitControlsFinder#find(java.util.List)
   */
  @Override
  public WeightedControlList find(List<SecretString> aSearch) {
    WeightedControlList tmpFoundControls = new WeightedControlList();

    // special case to support some search engines
    if (aSearch.isEmpty()) {
      for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
        if (tmpHtmlElement.isDisplayed()) {
          if (tmpHtmlElement instanceof HtmlTextInput) {
            tmpFoundControls.add(new HtmlUnitInputText((HtmlTextInput) tmpHtmlElement),
                WeightedControlList.FoundType.BY_ID, 0, // no coverage
                htmlPageIndex.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundControls;
          }
          if (tmpHtmlElement instanceof HtmlPasswordInput) {
            tmpFoundControls.add(new HtmlUnitInputPassword((HtmlPasswordInput) tmpHtmlElement),
                WeightedControlList.FoundType.BY_ID, 0, // no coverage
                htmlPageIndex.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundControls;
          }
          if (tmpHtmlElement instanceof HtmlTextArea) {
            tmpFoundControls.add(new HtmlUnitTextArea((HtmlTextArea) tmpHtmlElement),
                WeightedControlList.FoundType.BY_ID, 0, // no coverage
                htmlPageIndex.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundControls;
          }
          if (tmpHtmlElement instanceof HtmlFileInput) {
            tmpFoundControls.add(new HtmlUnitInputFile((HtmlFileInput) tmpHtmlElement),
                WeightedControlList.FoundType.BY_ID, 0, // no coverage
                htmlPageIndex.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundControls;
          }
        }
      }
    }

    return super.find(aSearch);
  }
}
