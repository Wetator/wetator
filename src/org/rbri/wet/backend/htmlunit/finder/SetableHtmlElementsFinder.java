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
import org.rbri.wet.backend.htmlunit.control.SetableHtmlUnitControl;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public class SetableHtmlElementsFinder extends IdentifierBasedElementsFinder {

  /**
   * @param aHtmlPage the page to work on
   * @param aDomNodeText the {@link DomNodeText} index of the page
   * @param aThreadPool the thread pool to use for worker threads
   */
  public SetableHtmlElementsFinder(HtmlPage aHtmlPage, DomNodeText aDomNodeText, ThreadPoolExecutor aThreadPool) {
    super(aHtmlPage, aDomNodeText, aThreadPool);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.finder.IdentifierBasedElementsFinder#find(java.util.List)
   */
  @Override
  public WeightedControlList find(List<SecretString> aSearch) {
    WeightedControlList tmpFoundElements = new WeightedControlList();

    // special case to support some search engines
    if (aSearch.isEmpty()) {
      for (HtmlElement tmpHtmlElement : domNodeText.getAllVisibleHtmlElements()) {
        if (tmpHtmlElement.isDisplayed()) {
          if ((tmpHtmlElement instanceof HtmlTextInput) || (tmpHtmlElement instanceof HtmlPasswordInput)
              || (tmpHtmlElement instanceof HtmlTextArea) || (tmpHtmlElement instanceof HtmlFileInput)) {
            tmpFoundElements.add(new SetableHtmlUnitControl(tmpHtmlElement), WeightedControlList.FoundType.BY_ID, 0, // no
                // coverage
                domNodeText.getTextBefore(tmpHtmlElement).length()); // distance from page start
            return tmpFoundElements;
          }
        }
      }
    }

    return super.find(aSearch);
  }
}
