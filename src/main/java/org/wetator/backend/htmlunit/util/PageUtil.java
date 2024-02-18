/*
 * Copyright (c) 2008-2024 wetator.org
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

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.XHtmlPage;

/**
 * Util class for page handling.
 *
 * @author rbri
 * @author frank.danek
 */
public final class PageUtil {

  /**
   * Helper for tests.
   *
   * @param anHtmlCode the html source of the page
   * @param aPageConsumer the consumer that uses the page
   * @throws IOException in case of problems
   */
  public static void consumeHtmlPage(final String anHtmlCode, final ThrowingConsumer<HtmlPage> aPageConsumer)
      throws IOException {
    // Take care: this has to be in sync with our default browser
    consumeHtmlPage(BrowserVersion.FIREFOX_ESR, anHtmlCode, aPageConsumer);
  }

  /**
   * Helper for tests.
   *
   * @param aBrowserVersion the browser to simulate
   * @param anHtmlCode the html source of the page
   * @param aPageConsumer the consumer that uses the page
   * @throws IOException in case of problems
   */
  public static void consumeHtmlPage(final BrowserVersion aBrowserVersion, final String anHtmlCode,
      final ThrowingConsumer<HtmlPage> aPageConsumer) throws IOException {
    try (WebClient tmpWebClient = new WebClient(aBrowserVersion)) {
      final HtmlPage tmpPage = tmpWebClient.loadHtmlCodeIntoCurrentWindow(anHtmlCode);
      aPageConsumer.accept(tmpPage);
    }
  }

  /**
   * Helper for tests.
   *
   * @param anXHtmlCode the XHtml source of the page
   * @param aPageConsumer the consumer that uses the page
   * @throws IOException in case of problems
   */
  public static void consumeXHtmlPage(final String anXHtmlCode, final ThrowingConsumer<XHtmlPage> aPageConsumer)
      throws IOException {
    consumeXHtmlPage(BrowserVersion.getDefault(), anXHtmlCode, aPageConsumer);
  }

  /**
   * Helper for tests.
   *
   * @param aBrowserVersion the browser to simulate
   * @param anXHtmlCode the XHtml source of the page
   * @param aPageConsumer the consumer that uses the page
   * @throws IOException in case of problems
   */
  public static void consumeXHtmlPage(final BrowserVersion aBrowserVersion, final String anXHtmlCode,
      final ThrowingConsumer<XHtmlPage> aPageConsumer) throws IOException {
    try (WebClient tmpWebClient = new WebClient(aBrowserVersion)) {
      final XHtmlPage tmpPage = tmpWebClient.loadXHtmlCodeIntoCurrentWindow(anXHtmlCode);
      aPageConsumer.accept(tmpPage);
    }
  }

  /**
   * Private constructor to be invisible.
   */
  private PageUtil() {
    super();
  }
}