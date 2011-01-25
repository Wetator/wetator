/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.backend.htmlunit.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.exception.AssertionFailedException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class PageUtilTest {

  @Test
  public void testCheckAnchor_EmptyPage() throws IOException {
    String tmpHtmlCode = "<html><body>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    try {
      PageUtil.checkAnchor("ref", tmpHtmlPage);
      Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      Assert.assertEquals("No id/anchor found for ref 'ref'.", e.getMessage());
    }
  }

  @Test
  public void testCheckAnchor_NoAnchor() throws IOException {
    String tmpHtmlCode = "<html>" + "<head>" + "<title>Page Title</title>" + "</head>" + "<body>"
        + "<p>Paragraph 1</p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    try {
      PageUtil.checkAnchor("ref", tmpHtmlPage);
      Assert.fail("AssertionFailedException expected");
    } catch (AssertionFailedException e) {
      Assert.assertEquals("No id/anchor found for ref 'ref'.", e.getMessage());
    }
  }

  @Test
  public void testCheckAnchor_ById() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><head>" + "<title>Page Title</title></head>" + "<body>"
        + "<p>Paragraph 1 <a id='ref'>Anchor</a></p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    PageUtil.checkAnchor("ref", tmpHtmlPage);
  }

  @Test
  public void testCheckAnchor_ByName() throws IOException, AssertionFailedException {
    String tmpHtmlCode = "<html><head>" + "<title>Page Title</title></head>" + "<body>"
        + "<p>Paragraph 1 <a name='ref'>Anchor</a></p>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    PageUtil.checkAnchor("ref", tmpHtmlPage);
  }
}
