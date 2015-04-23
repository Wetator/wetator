/*
 * Copyright (c) 2008-2015 wetator.org
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

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitUnspecificControlTest {

  @Test
  public void isDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button disabled='disabled' id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlUnitUnspecificControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
        tmpHtmlPage.getHtmlElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlUnitUnspecificControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
        tmpHtmlPage.getHtmlElementById("myId"));

    Assert.assertFalse(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_NotVisible() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<button style='visible: none' id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>"
        + "</button>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlUnitUnspecificControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
        tmpHtmlPage.getHtmlElementById("myId"));

    Assert.assertFalse(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnlyAndDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='text' id='myId' name='MyName' value='value' readonly='readonly' disabled='disabled'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlUnitUnspecificControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
        tmpHtmlPage.getHtmlElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnly() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='text' id='myId' name='MyName' value='value' readonly='readonly'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlUnitUnspecificControl<?> tmpControl = new HtmlUnitUnspecificControl<HtmlElement>(
        tmpHtmlPage.getHtmlElementById("myId"));

    // isReadonly is availabe for HtmlInput elements only;
    // because we have separate classes for all input elements,
    // we have no nee to handle this generic
    Assert.assertFalse(tmpControl.isDisabled(null));
  }
}
