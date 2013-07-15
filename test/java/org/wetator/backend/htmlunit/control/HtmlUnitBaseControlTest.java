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


package org.wetator.backend.htmlunit.control;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.exception.AssertionFailedException;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitBaseControlTest {

  @Test
  public void isDisabled() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button disabled='disabled' id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitBaseControl<HtmlElement>(tmpHtmlPage.getElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_Not() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>" + "<button id='myId' type='button' name='MyName'>"
        + "<p>ButtonWithText</p>" + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitBaseControl<HtmlElement>(tmpHtmlPage.getElementById("myId"));

    Assert.assertFalse(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_NotVisible() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button style='visible: none' id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>"
        + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitBaseControl<HtmlElement>(tmpHtmlPage.getElementById("myId"));

    Assert.assertFalse(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnlyAndDisabled() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='text' id='myId' name='MyName' value='value' readonly='readonly' disabled='disabled'/>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitBaseControl<HtmlElement>(tmpHtmlPage.getElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabledGeneric_ReadOnly() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='text' id='myId' name='MyName' value='value' readonly='readonly'/>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitBaseControl<HtmlElement>(tmpHtmlPage.getElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnly() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<input type='text' id='myId' name='MyName' value='value' readonly='readonly'/>" + "</form>"
        + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlTextInput tmpTextInput = (HtmlTextInput) tmpHtmlPage.getElementById("myId");
    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitInputText(tmpTextInput);

    Assert.assertTrue(tmpControl.isDisabled(null));
  }
}
