/*
 * Copyright (c) 2008-2017 wetator.org
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
import java.net.URL;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.XHtmlPage;

/**
 * Util class for page handling.
 *
 * @author rbri
 */
public final class PageUtil {

  /**
   * Helper for tests.
   *
   * @param anHtmlCode the html source of the page
   * @return the HtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static HtmlPage constructHtmlPage(final String anHtmlCode) throws IOException {
    return constructHtmlPage(BrowserVersion.getDefault(), anHtmlCode);
  }

  /**
   * Helper for tests.
   *
   * @param aBrowserVersion the browser to simulate
   * @param anHtmlCode the html source of the page
   * @return the HtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static HtmlPage constructHtmlPage(final BrowserVersion aBrowserVersion, final String anHtmlCode)
      throws IOException {
    final StringWebResponse tmpResponse = new StringWebResponse(anHtmlCode,
        new URL("http://www.wetator.org/test.html"));
    final WebClient tmpWebClient = new WebClient(aBrowserVersion);
    try {
      final HtmlPage tmpPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());
      return tmpPage;
    } finally {
      tmpWebClient.close();
    }
  }

  /**
   * Helper for tests.
   *
   * @param anXHtmlCode the XHtml source of the page
   * @return the XHtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static XHtmlPage constructXHtmlPage(final String anXHtmlCode) throws IOException {
    return constructXHtmlPage(BrowserVersion.getDefault(), anXHtmlCode);
  }

  /**
   * Helper for tests.
   *
   * @param aBrowserVersion the browser to simulate
   * @param anXHtmlCode the XHtml source of the page
   * @return the XHtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static XHtmlPage constructXHtmlPage(final BrowserVersion aBrowserVersion, final String anXHtmlCode)
      throws IOException {
    final StringWebResponse tmpResponse = new StringWebResponse(anXHtmlCode,
        new URL("http://www.wetator.org/test.xhtml"));
    final WebClient tmpWebClient = new WebClient(aBrowserVersion);
    try {
      final XHtmlPage tmpPage = HTMLParser.parseXHtml(tmpResponse, tmpWebClient.getCurrentWindow());
      return tmpPage;
    } finally {
      tmpWebClient.close();
    }
  }

  /**
   * Private constructor to be invisible.
   */
  private PageUtil() {
    super();
  }
}