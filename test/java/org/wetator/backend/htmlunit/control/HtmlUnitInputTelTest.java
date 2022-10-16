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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.wetator.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTelInput;

/**
 * @author rbri
 */
public class HtmlUnitInputTelTest {
  @Test
  public void isDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' id='myId' name='MyName' value='value' disabled='disabled'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlTelInput tmpTel = (HtmlTelInput) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitInputTel tmpControl = new HtmlUnitInputTel(tmpTel);

    assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_Not() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' id='myId' name='MyName' value='value'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlTelInput tmpTel = (HtmlTelInput) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitInputTel tmpControl = new HtmlUnitInputTel(tmpTel);

    assertFalse(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_NotVisible() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' style='visible: none' id='myId' name='MyName' value='value' disabled='disabled'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlTelInput tmpTel = (HtmlTelInput) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitInputTel tmpControl = new HtmlUnitInputTel(tmpTel);

    assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnlyAndDisabled() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' id='myId' name='MyName' value='value' readonly='readonly' disabled='disabled'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlTelInput tmpTel = (HtmlTelInput) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitInputTel tmpControl = new HtmlUnitInputTel(tmpTel);

    assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void isDisabled_ReadOnly() throws IOException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input type='tel' id='myId' name='MyName' value='value' readonly='readonly'/>"
        + "</form>"
        + "</body></html>";
    // @formatter:on
    final HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    final HtmlTelInput tmpTel = (HtmlTelInput) tmpHtmlPage.getHtmlElementById("myId");
    final HtmlUnitInputTel tmpControl = new HtmlUnitInputTel(tmpTel);

    assertTrue(tmpControl.isDisabled(null));
  }
}