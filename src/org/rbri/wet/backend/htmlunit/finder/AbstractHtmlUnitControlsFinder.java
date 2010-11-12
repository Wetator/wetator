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

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.util.HtmlPageIndex;
import org.rbri.wet.util.SecretString;

/**
 * The base class for all HtmlUnit finders.<br />
 * A finder is used to find all {@link org.rbri.wet.backend.control.Control}s matching a given search. These controls
 * are returned as a {@link WeightedControlList}.
 * 
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitControlsFinder {

  /**
   * The index of the page.
   */
  protected HtmlPageIndex htmlPageIndex;

  /**
   * The constructor.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page
   */
  public AbstractHtmlUnitControlsFinder(HtmlPageIndex aHtmlPageIndex) {
    htmlPageIndex = aHtmlPageIndex;
  }

  /**
   * Returns all {@link org.rbri.wet.backend.control.Control}s on the page matching the given search.
   * 
   * @param aSearch the search
   * @return the list of matching controls
   */
  public abstract WeightedControlList find(List<SecretString> aSearch);
}