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

import org.rbri.wet.backend.WetBackend.ContentType;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;



/**
 * Utils for content type handling
 *
 * @author rbri
 */

public class ContentTypeUtil {

    public static ContentType getContentType(Page aPage) {
        if (aPage instanceof HtmlPage) {
            return ContentType.HTML;
        }
        if (aPage instanceof TextPage) {
            return ContentType.TEXT;
        }

        WebResponse tmpWebResponse = aPage.getWebResponse();
        String tmpContentType = tmpWebResponse.getContentType();

        if ("application/pdf".equalsIgnoreCase(tmpContentType)) {
            return ContentType.PDF;
        }

        if ("application/vnd.ms-excel".equalsIgnoreCase(tmpContentType)) {
            return ContentType.XLS;
        }
        return ContentType.OTHER;
    }


    public static String getFileSuffix(Page aPage) {
        ContentType tmpContentType = getContentType(aPage);
        String tmpResult;

        switch (tmpContentType) {
        case HTML:
            tmpResult = "html";
            break;
        case TEXT:
            tmpResult = "txt";
            break;
        case PDF:
            tmpResult = "pdf";
            break;
        case XLS:
            tmpResult = "xls";
            break;
        default:
            tmpResult = "bin";
            break;
        }
        return tmpResult;
    }

}
