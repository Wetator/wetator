/*
 * Copyright (c) 2008-2016 wetator.org
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.wetator.backend.IBrowser.ContentType;

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
  private static final Map<String, ContentType> CONTENT_TYPES = new HashMap<String, ContentType>();
  private static final Map<ContentType, String> FILE_EXTENSIONS = new HashMap<ContentType, String>();

  static {
    define(ContentType.HTML, "html", "text/html");
    define(ContentType.CSS, "css", "text/css");
    define(ContentType.JAVASCRIPT, "js", "text/javascript", "application/x-javascript");
    define(ContentType.TEXT, "txt", "text/plain");

    define(ContentType.XML, "xml", "text/xml");
    define(ContentType.PDF, "pdf", "application/pdf");
    define(ContentType.TEXT, "csv", "text/csv");

    // Excel
    define(ContentType.XLS, "xls", "application/vnd.ms-excel");
    define(ContentType.XLSX, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    // Word
    define(ContentType.DOCX, "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    // RTF
    define(ContentType.RTF, "rtf", "application/rtf", "text/rtf", "text/richtext", "text/enriched");
    // images
    define(ContentType.PNG, "png", "image/png");
    define(ContentType.GIF, "gif", "image/gif");
    define(ContentType.BMP, "bmp", "image/bmp");
    define(ContentType.JPEG, "jpeg", "image/jpeg");
    define(ContentType.SVG, "svg", "image/svg+xml");
    // zip
    define(ContentType.ZIP, "zip", "application/zip");
  }

  private static void define(final ContentType aContentType, final String aFileExtension,
      final String... aContentTypeStrings) {
    FILE_EXTENSIONS.put(aContentType, aFileExtension);
    for (final String tmpContentType : aContentTypeStrings) {
      CONTENT_TYPES.put(tmpContentType.toLowerCase(Locale.ROOT), aContentType);
    }
  }

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
    return getContentType(tmpContentType);
  }

  /**
   * @param aContentType The content type string.
   * @return The content type.
   */
  public static ContentType getContentType(final String aContentType) {
    final ContentType tmpContentType = CONTENT_TYPES.get(aContentType.toLowerCase(Locale.ROOT));
    if (null == tmpContentType) {
      return ContentType.OTHER;
    }
    return tmpContentType;
  }

  /**
   * @param aPage The {@link Page} to get the (default) file suffix for.
   * @return The file suffix.
   */
  public static String getFileSuffix(final Page aPage) {
    final ContentType tmpContentType = getContentType(aPage);
    return getFileSuffix(tmpContentType);
  }

  /**
   * @param aContentType The content type string.
   * @return The file suffix.
   */
  public static String getFileSuffix(final String aContentType) {
    final ContentType tmpContentType = getContentType(aContentType);
    return getFileSuffix(tmpContentType);
  }

  /**
   * @param aContentType The content type.
   * @return The file suffix.
   */
  public static String getFileSuffix(final ContentType aContentType) {
    final String tmpResult = FILE_EXTENSIONS.get(aContentType);
    if (null == tmpResult) {
      return "bin";
    }
    return tmpResult;
  }

  /**
   * @param aFileName The file name.
   * @return The content type matching the file suffix.
   */
  public static ContentType getContentTypeForFileName(final String aFileName) {
    if (null == aFileName) {
      return ContentType.OTHER;
    }
    for (final Map.Entry<ContentType, String> tmpEntry : FILE_EXTENSIONS.entrySet()) {
      if (aFileName.toLowerCase(Locale.ROOT).endsWith(tmpEntry.getValue())) {
        return tmpEntry.getKey();
      }
    }
    return ContentType.OTHER;
  }
}
