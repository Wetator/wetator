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
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;




/**
 * Util class for page handling.
 *
 * @author rbri
 */
public class PageUtil {
    
    // private static final Log LOG = LogFactory.getLog(PageUtil.class);


    public static void waitForThreads(final SgmlPage aHtmlPage) {
        // TODO make max wait time configurable
        JavaScriptJobManager tmpJobManager = aHtmlPage.getEnclosingWindow().getJobManager();

        // execute all immediate jobs 
        tmpJobManager.waitForJobsStartingBefore(1000); // one second
    }

    
    /**
     * Helper for tests
     */
    public static HtmlPage constructPage(final String aHtmlCode) throws IOException {
        StringWebResponse tmpResponse = new StringWebResponse(aHtmlCode, new URL("http://www.rbri.org/wet/test.html"));
        WebClient tmpWebClient = new WebClient();
        HtmlPage tmpPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

        return tmpPage;
    }
}