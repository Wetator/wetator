/*
 * Copyright (c) 2008-2025 wetator.org
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

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.htmlunit.Page;
import org.htmlunit.TextPage;
import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.XHtmlPage;
import org.junit.Test;
import org.wetator.backend.IBrowser.ContentType;

/**
 * @author rbri
 * @author frank.danek
 */
public class ContentTypeUtilTest {

  @Test
  public void getContentType_null() {
    final ContentType tmpType = ContentTypeUtil.getContentType(null);
    assertEquals(ContentType.OTHER, tmpType);
  }

  @Test
  public void getContentType_byPageType_html() {
    final Page tmpPage = mock(HtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentType_byPageType_xhtml() {
    final Page tmpPage = mock(XHtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentType_byPageType_text() {
    final Page tmpPage = mock(TextPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    assertEquals(ContentType.TEXT, tmpType);
  }

  @Test
  public void getContentType_byResponse_supported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("text/css");

    final Page tmpPage = mock(Page.class);
    when(tmpPage.getWebResponse()).thenReturn(tmpResponse);

    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    assertEquals(ContentType.CSS, tmpType);
  }

  @Test
  public void getContentType_byResponse_notSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");

    final Page tmpPage = mock(Page.class);
    when(tmpPage.getWebResponse()).thenReturn(tmpResponse);

    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    assertEquals(ContentType.OTHER, tmpType);
  }

  @Test
  public void getFileSuffix_null() {
    String tmpSuffix = ContentTypeUtil.getFileSuffix((Page) null);
    assertEquals("bin", tmpSuffix);

    tmpSuffix = ContentTypeUtil.getFileSuffix((WebResponse) null);
    assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byPageType_html() {
    final Page tmpPage = mock(HtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byPageType_xhtml() {
    final Page tmpPage = mock(XHtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byPageType_text() {
    final Page tmpPage = mock(TextPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    assertEquals("csv", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byResponse_supported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("text/css");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("css", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byResponse_supported_contentDisposition() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("text/css");
    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.pdf;");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("css", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byResponse_notSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getFileSuffix_byResponse_notSupported_contentDisposition() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=\"quot.pdf\";");
    String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("pdf", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=\"quot.pdf\"");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("pdf", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.pdf;");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("pdf", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.pdf");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("pdf", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("bin", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("bin", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("bin", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getContentTypeForFileName() {
    ContentType tmpType = ContentTypeUtil.getContentTypeForFileName(null);
    assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("");
    assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc");
    assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.def");
    assertEquals(ContentType.OTHER, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.pdf");
    assertEquals(ContentType.PDF, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.XLS");
    assertEquals(ContentType.XLS, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.xlsx");
    assertEquals(ContentType.XLSX, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.DOC");
    assertEquals(ContentType.DOC, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.docx");
    assertEquals(ContentType.DOCX, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.rtf");
    assertEquals(ContentType.RTF, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("test.zip");
    assertEquals(ContentType.ZIP, tmpType);
  }
}
