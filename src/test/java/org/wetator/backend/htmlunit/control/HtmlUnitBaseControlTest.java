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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.htmlunit.html.HtmlElement;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitBaseControlTest {

  @Test
  public void getUniqueSelector_Id() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<button id='myId'></button>"
        + "</div>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitUnspecificControl<>(tmpHtmlPage.getHtmlElementById("myId"));

      assertEquals("#myId", tmpControl.getUniqueSelector());
    });
  }

  @Test
  public void getUniqueSelector_IdWithColon() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<button id='f:myId'></button>"
        + "</div>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitUnspecificControl<>(
          tmpHtmlPage.getHtmlElementById("f:myId"));

      assertEquals("#f\\3amyId", tmpControl.getUniqueSelector());
    });
  }

  @Test
  public void getUniqueSelector_General() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div>"
        + "<a name='myAnchor'>test</a>"
        + "</div>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
          tmpHtmlPage.getAnchorByName("myAnchor"));

      assertEquals("body>div:nth-of-type(1)>a:nth-of-type(1)", tmpControl.getUniqueSelector());
    });
  }

  @Test
  public void getUniqueSelector_ParentWithId() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='parent'>"
        + "<a name='myAnchor'>test</a>"
        + "</div>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
          tmpHtmlPage.getAnchorByName("myAnchor"));

      assertEquals("#parent>a:nth-of-type(1)", tmpControl.getUniqueSelector());
    });
  }

  @Test
  public void getUniqueSelector_ParentWithId_II() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<div id='parent'>"
        + "<div>"
        + "<a name='myAnchor'>test</a>"
        + "</div>"
        + "</div>"
        + "</body></html>";
    // @formatter:on
    PageUtil.consumeHtmlPage(tmpHtmlCode, tmpHtmlPage -> {

      final HtmlUnitBaseControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
          tmpHtmlPage.getAnchorByName("myAnchor"));

      assertEquals("#parent>div:nth-of-type(1)>a:nth-of-type(1)", tmpControl.getUniqueSelector());
    });
  }
}
