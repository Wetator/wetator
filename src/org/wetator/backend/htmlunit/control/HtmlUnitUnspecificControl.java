/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.backend.htmlunit.control;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.backend.htmlunit.util.HtmlElementUtil;

import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

/**
 * This is the implementation of a {@link HtmlUnitBaseControl} for so far not supported elements.
 *
 * @param <T> the type of the {@link HtmlElement}.
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class HtmlUnitUnspecificControl<T extends HtmlElement> extends HtmlUnitBaseControl<HtmlElement> {

  private static final Logger LOG = LogManager.getLogger(HtmlUnitUnspecificControl.class);

  /**
   * The constructor.
   *
   * @param anHtmlElement the {@link HtmlElement} from the backend
   */
  public HtmlUnitUnspecificControl(final T anHtmlElement) {
    super(anHtmlElement);
    if (LOG.isDebugEnabled()) {
      LOG.debug("HtmlUnitUnspecificControl for " + anHtmlElement + " created.");
    }
  }

  @Override
  public String getDescribingText() {
    final HtmlElement tmpHtmlElement = getHtmlElement();

    if (tmpHtmlElement instanceof HtmlBody) {
      return HtmlElementUtil.getDescribingTextForHtmlBody((HtmlBody) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlHiddenInput) {
      return HtmlElementUtil.getDescribingTextForHtmlHiddenInput((HtmlHiddenInput) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlLabel) {
      return HtmlElementUtil.getDescribingTextForHtmlLabel((HtmlLabel) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlParagraph) {
      return HtmlElementUtil.getDescribingTextForHtmlParagraph((HtmlParagraph) tmpHtmlElement);
    }
    if (tmpHtmlElement instanceof HtmlSpan) {
      return HtmlElementUtil.getDescribingTextForHtmlSpan((HtmlSpan) tmpHtmlElement);
    }

    // handle things that are not implemented at the moment
    // @formatter:off
    final StringBuilder tmpResult = new StringBuilder("[Unknown HtmlElement '")
        .append(tmpHtmlElement.getClass())
        .append('\'');
    // @formatter:on

    addId(tmpResult, tmpHtmlElement);
    addName(tmpResult, tmpHtmlElement);

    tmpResult.append(']');
    return tmpResult.toString();
  }

  private static void addId(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpId = anHtmlElement.getAttribute("id");
    if (StringUtils.isNotEmpty(tmpId)) {
      aStringBuilder.append(" (id='");
      aStringBuilder.append(tmpId);
      aStringBuilder.append("')");
    }
  }

  private static void addName(final StringBuilder aStringBuilder, final HtmlElement anHtmlElement) {
    final String tmpName = anHtmlElement.getAttribute("name");
    if (StringUtils.isNotEmpty(tmpName)) {
      aStringBuilder.append(" (name='");
      aStringBuilder.append(tmpName);
      aStringBuilder.append("')");
    }
  }
}
