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


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.io.IOException;
import java.util.List;

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlIdentifierTest {

  protected AbstractHtmlUnitControlIdentifier identifier;

  protected WeightedControlList identify(String aHtmlCode, String anHtmlElementId, List<SecretString> aSearch)
      throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    HtmlElement tmpHtmlElement = tmpHtmlPage.getElementById(anHtmlElementId);

    identifier.initialize(tmpHtmlPageIndex);
    return identifier.identify(aSearch, tmpHtmlElement);
  }

  protected WeightedControlList identify(String aHtmlCode, String anHtmlElementIdOrName, int anIndex,
      List<SecretString> aSearch) throws IOException {
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(aHtmlCode);
    HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    List<HtmlElement> tmpHtmlElements = tmpHtmlPage.getElementsByIdAndOrName(anHtmlElementIdOrName);
    HtmlElement tmpHtmlElement = tmpHtmlElements.get(anIndex);

    identifier.initialize(tmpHtmlPageIndex);
    return identifier.identify(aSearch, tmpHtmlElement);
  }

}