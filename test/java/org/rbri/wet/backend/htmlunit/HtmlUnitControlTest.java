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


package org.rbri.wet.backend.htmlunit;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.rbri.wet.backend.htmlunit.util.PageUtil;
import org.rbri.wet.exception.AssertionFailedException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class HtmlUnitControlTest {

  @Test
  public void testIsDisabled() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button disabled='disabled' id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>" + "</button>"
        + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitControl tmpControl = new HtmlUnitControl(tmpHtmlPage.getElementById("myId"));

    Assert.assertTrue(tmpControl.isDisabled(null));
  }

  @Test
  public void testIsDisabled_Not() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><body>" + "<form action='test'>"
        + "<button style='visible: none' id='myId' type='button' name='MyName'>" + "<p>ButtonWithText</p>"
        + "</button>" + "</form>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    HtmlUnitControl tmpControl = new HtmlUnitControl(tmpHtmlPage.getElementById("myId"));

    Assert.assertFalse(tmpControl.isDisabled(null));
  }
}
