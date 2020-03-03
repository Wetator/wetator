/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.backend.htmlunit;

import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Our own listener. We like to inform about javascript errors without
 * stopping the processing.
 *
 * @author rbri
 * @author frank.danek
 */
public final class JavaScriptErrorListener implements com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener {
  private HtmlUnitBrowser htmlUnitBrowser;

  /**
   * Constructor.
   *
   * @param aHtmlUnitBrowser the browser this listener informs
   */
  public JavaScriptErrorListener(final HtmlUnitBrowser aHtmlUnitBrowser) {
    htmlUnitBrowser = aHtmlUnitBrowser;
  }

  @Override
  public void loadScriptError(final HtmlPage aHtmlPage, final URL aScriptUrl, final Exception anException) {
    htmlUnitBrowser.addFailure("javascriptLoadError",
        new String[] { aScriptUrl.toExternalForm(), aHtmlPage.getUrl().toExternalForm(), anException.getMessage() },
        anException);
  }

  @Override
  public void malformedScriptURL(final HtmlPage aHtmlPage, final String aUrl,
      final MalformedURLException aMalformedURLException) {
    htmlUnitBrowser.addFailure("javascriptLoadError",
        new String[] { aUrl, aHtmlPage.getUrl().toExternalForm(), aMalformedURLException.getMessage() },
        aMalformedURLException);
  }

  @Override
  public void scriptException(final HtmlPage aHtmlPage, final ScriptException aScriptException) {
    htmlUnitBrowser.addFailure("javascriptError", new String[] { aScriptException.getMessage() }, aScriptException);
  }

  @Override
  public void timeoutError(final HtmlPage aHtmlPage, final long aAllowedTime, final long aExecutionTime) {
    htmlUnitBrowser.addFailure("javascriptTimeoutError",
        new Object[] { aAllowedTime, aExecutionTime, aHtmlPage.getUrl().toExternalForm() }, null);
  }

  @Override
  public void warn(final String aMessage, final String aSourceName, final int aLine, final String aLineSource,
      final int aLineOffset) {
    // ignore for now
  }
}