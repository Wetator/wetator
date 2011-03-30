/*
 * Copyright (c) 2008-2011 wetator.org
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

import org.wetator.backend.WetBackend.ContentType;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Utility class for content type handling.
 * 
 * @author rbri
 */
public final class ContentTypeUtil {

  /**
   * This class should not be instantiated.
   */
  private ContentTypeUtil() {
    // nothing
  }

  /**
   * @param aPage The {@link Page} to get the content type for.
   * @return The content type.
   */
  public static ContentType getContentType(final Page aPage) {
    if (aPage instanceof HtmlPage) {
      return ContentType.HTML;
    }
    if (aPage instanceof TextPage) {
      return ContentType.TEXT;
    }

    final WebResponse tmpWebResponse = aPage.getWebResponse();
    final String tmpContentType = tmpWebResponse.getContentType();

    if ("application/pdf".equalsIgnoreCase(tmpContentType)) {
      return ContentType.PDF;
    }

    if ("application/vnd.ms-excel".equalsIgnoreCase(tmpContentType)) {
      return ContentType.XLS;
    }

    // RTF
    if ("application/rtf".equalsIgnoreCase(tmpContentType)) {
      return ContentType.RTF;
    }
    if ("text/rtf".equalsIgnoreCase(tmpContentType)) {
      return ContentType.RTF;
    }
    if ("text/richtext".equalsIgnoreCase(tmpContentType)) {
      return ContentType.RTF;
    }
    if ("text/enriched".equalsIgnoreCase(tmpContentType)) {
      return ContentType.RTF;
    }

    return ContentType.OTHER;
  }

  /**
   * @param aPage The {@link Page} to get the (default) file suffix for.
   * @return The file suffix.
   */
  public static String getFileSuffix(final Page aPage) {
    final ContentType tmpContentType = getContentType(aPage);
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
