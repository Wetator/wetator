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

import org.apache.commons.lang3.StringUtils;
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
    if (aPage == null) {
      return ContentType.OTHER;
    }
    if (aPage instanceof HtmlPage) {
      return ContentType.HTML;
    }
    if (aPage instanceof TextPage) {
      return ContentType.TEXT;
    }

    return getContentType(aPage.getWebResponse());
  }

  /**
   * @param aWebResponse The WebResponse.
   * @return The content type.
   */
  private static ContentType getContentType(final WebResponse aWebResponse) {
    if (null == aWebResponse) {
      return ContentType.OTHER;
    }
    final String tmpContentTypeString = aWebResponse.getContentType();

    final ContentType tmpContentType = CONTENT_TYPES.get(tmpContentTypeString.toLowerCase(Locale.ROOT));
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
    if (aPage != null) {
      return getFileSuffix(tmpContentType, aPage.getWebResponse());
    }
    return getFileSuffix(tmpContentType, null);
  }

  /**
   * @param aWebResponse The WebResponse.
   * @return The file suffix.
   */
  public static String getFileSuffix(final WebResponse aWebResponse) {
    final ContentType tmpContentType = getContentType(aWebResponse);
    return getFileSuffix(tmpContentType, aWebResponse);
  }

  /**
   * @param aContentType The content type.
   * @param aWebResponse The WebResponse.
   * @return The file suffix.
   */
  private static String getFileSuffix(final ContentType aContentType, final WebResponse aWebResponse) {
    final String tmpResult = FILE_EXTENSIONS.get(aContentType);

    // content type is not known, have a look at the content disposition header
    if (null == tmpResult) {
      String tmpFileName = getSuggestedFilename(aWebResponse);
      if (StringUtils.isNotBlank(tmpFileName)) {
        tmpFileName = tmpFileName.trim();
        final int tmpDotPos = tmpFileName.lastIndexOf(".");
        if (tmpDotPos > -1 && tmpDotPos < tmpFileName.length() - 1) {
          tmpFileName = tmpFileName.substring(tmpDotPos + 1);
          return tmpFileName;
        }
      }
      return "bin";
    }
    return tmpResult;
  }

  /**
   * Returns the attachment's filename, as suggested by the <tt>Content-Disposition</tt>
   * header, or {@code null} if no filename was suggested.
   *
   * @param aContentType The content type.
   * @return the attachment's suggested filename, or {@code null} if none was suggested
   */
  private static String getSuggestedFilename(final WebResponse aWebResponse) {
    if (aWebResponse == null) {
      return null;
    }

    final String tmpDisp = aWebResponse.getResponseHeaderValue("Content-Disposition");
    if (StringUtils.isBlank(tmpDisp)) {
      return null;
    }

    int tmpStart = tmpDisp.indexOf("filename=");
    if (tmpStart == -1) {
      return null;
    }
    tmpStart += "filename=".length();
    if (tmpStart >= tmpDisp.length()) {
      return null;
    }

    int tmpEnd = tmpDisp.indexOf(';', tmpStart);
    if (tmpEnd == -1) {
      tmpEnd = tmpDisp.length();
    }
    if (tmpDisp.charAt(tmpStart) == '"' && tmpDisp.charAt(tmpEnd - 1) == '"') {
      tmpStart++;
      tmpEnd--;
    }
    return tmpDisp.substring(tmpStart, tmpEnd);
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
