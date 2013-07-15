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


package org.rbri.wet.backend.htmlunit.util;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.XHtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

/**
 * Util class for page handling.
 * 
 * @author rbri
 */
public final class PageUtil {

  /**
   * Wait for finishing of all jobs scheduled for execution
   * in the next 1s
   * 
   * @param aSgmlPage the page
   */
  public static void waitForThreads(final SgmlPage aSgmlPage) {
    // TODO make max wait time configurable
    JavaScriptJobManager tmpJobManager = aSgmlPage.getEnclosingWindow().getJobManager();

    // execute all immediate jobs
    tmpJobManager.waitForJobsStartingBefore(1000); // one second
  }

  /**
   * Helper for tests
   * 
   * @param anHtmlCode the html source of the page
   * @return the HtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static HtmlPage constructHtmlPage(final String anHtmlCode) throws IOException {
    StringWebResponse tmpResponse = new StringWebResponse(anHtmlCode, new URL("http://www.rbri.org/wet/test.html"));
    WebClient tmpWebClient = new WebClient();
    HtmlPage tmpPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

    return tmpPage;
  }

  /**
   * Helper for tests
   * 
   * @param anXHtmlCode the XHtml source of the page
   * @return the XHtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static XHtmlPage constructXHtmlPage(final String anXHtmlCode) throws IOException {
    StringWebResponse tmpResponse = new StringWebResponse(anXHtmlCode, new URL("http://www.rbri.org/wet/test.xhtml"));
    WebClient tmpWebClient = new WebClient();
    XHtmlPage tmpPage = HTMLParser.parseXHtml(tmpResponse, tmpWebClient.getCurrentWindow());

    return tmpPage;
  }

  /**
   * Private constructor to be invisible
   */
  private PageUtil() {
    super();
  }
}