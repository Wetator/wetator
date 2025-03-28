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

import java.util.concurrent.ThreadPoolExecutor;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;

/**
 * In addition to the {@link IdentifierBasedHtmlUnitControlsFinder} this finder has a special support for empty
 * searches. This is needed for some search engines which have no label in front of their input field. Currently the
 * following settable {@link org.wetator.backend.control.IControl}s are supported:
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
  public SettableHtmlUnitControlsFinder(final HtmlPageIndex aHtmlPageIndex, final ThreadPoolExecutor aThreadPool) {
    super(aHtmlPageIndex, aThreadPool);
  }

  @Override
  public WeightedControlList find(final WPath aWPath) {
    final WeightedControlList tmpFoundControls = new WeightedControlList();

    // special case to support some search engines
    if (aWPath.isEmpty()) {
      for (final HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
        if (tmpHtmlElement instanceof HtmlTextInput) {
          tmpFoundControls.add(new HtmlUnitInputText((HtmlTextInput) tmpHtmlElement),
              WeightedControlList.FoundType.BY_ID, 0, // no deviation
              htmlPageIndex.getTextBefore(tmpHtmlElement).length(), // distance from page start
              htmlPageIndex.getPosition(tmpHtmlElement).getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
              htmlPageIndex.getIndex(tmpHtmlElement));
        }
        if (tmpHtmlElement instanceof HtmlPasswordInput) {
          tmpFoundControls.add(new HtmlUnitInputPassword((HtmlPasswordInput) tmpHtmlElement),
              WeightedControlList.FoundType.BY_ID, 0, // no deviation
              htmlPageIndex.getTextBefore(tmpHtmlElement).length(), // distance from page start
              htmlPageIndex.getPosition(tmpHtmlElement).getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
              htmlPageIndex.getIndex(tmpHtmlElement));
        }
        if (tmpHtmlElement instanceof HtmlTextArea) {
          tmpFoundControls.add(new HtmlUnitTextArea((HtmlTextArea) tmpHtmlElement), WeightedControlList.FoundType.BY_ID,
              0, // no deviation
              htmlPageIndex.getTextBefore(tmpHtmlElement).length(), // distance from page start
              htmlPageIndex.getPosition(tmpHtmlElement).getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
              htmlPageIndex.getIndex(tmpHtmlElement));
        }
        if (tmpHtmlElement instanceof HtmlFileInput) {
          tmpFoundControls.add(new HtmlUnitInputFile((HtmlFileInput) tmpHtmlElement),
              WeightedControlList.FoundType.BY_ID, 0, // no deviation
              htmlPageIndex.getTextBefore(tmpHtmlElement).length(), // distance from page start
              htmlPageIndex.getPosition(tmpHtmlElement).getStartPos(), htmlPageIndex.getHierarchy(tmpHtmlElement),
              htmlPageIndex.getIndex(tmpHtmlElement));
        }
      }
      return tmpFoundControls;
    }

    return super.find(aWPath);
  }
}
