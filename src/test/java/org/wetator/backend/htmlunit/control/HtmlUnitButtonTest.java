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


package org.wetator.backend.htmlunit.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.htmlunit.html.HtmlButton;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;

/**
 * @author rbri
 */
public class HtmlUnitButtonTest {
  @Test
  public void isDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' name='MyName' disabled='disabled'>Test</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlButton tmpText = (HtmlButton) tmpHtmlPage.getHtmlElementById("myId");
      final HtmlUnitButton tmpControl = new HtmlUnitButton(tmpText);

      assertTrue(tmpControl.isDisabled(null));
    });
  }

  @Test
  public void isDisabled_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' name='MyName'>Test</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlButton tmpText = (HtmlButton) tmpHtmlPage.getHtmlElementById("myId");
      final HtmlUnitButton tmpControl = new HtmlUnitButton(tmpText);

      assertFalse(tmpControl.isDisabled(null));
    });
  }

  @Test
  public void isDisabled_NotVisible() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button style='visible: none' id='myId' name='MyName' disabled='disabled'>Test</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlButton tmpText = (HtmlButton) tmpHtmlPage.getHtmlElementById("myId");
      final HtmlUnitButton tmpControl = new HtmlUnitButton(tmpText);

      assertTrue(tmpControl.isDisabled(null));
    });
  }
}
