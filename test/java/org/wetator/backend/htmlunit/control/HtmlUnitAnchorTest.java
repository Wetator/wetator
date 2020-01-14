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


package org.wetator.backend.htmlunit.control;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitAnchorTest {

  @Test
  public void isDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<a id='myId'>test</a>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlAnchor tmpImage = (HtmlAnchor) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(tmpImage);

    try {
      tmpControl.isDisabled(null);
      Assert.fail("UnsupportedOperationException expected");
    } catch (final org.wetator.exception.UnsupportedOperationException e) {
      Assert.assertEquals(
          "The HTML element [HtmlAnchor 'test' (id='myId')] does not support the disabled state/property.",
          e.getMessage());
    }
  }
}
