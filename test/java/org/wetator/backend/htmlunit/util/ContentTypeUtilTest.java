/*
 * Copyright (c) 2008-2021 wetator.org
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.IBrowser.ContentType;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.XHtmlPage;

/**
 * @author rbri
 */
public class ContentTypeUtilTest {

  @Test
  public void getContentTypeForFileName() {
    ContentType tmpType = ContentTypeUtil.getContentTypeForFileName(null);
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("");
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc");
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.def");
    Assert.assertEquals(ContentType.OTHER, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.pdf");
    Assert.assertEquals(ContentType.PDF, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.XLS");
    Assert.assertEquals(ContentType.XLS, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.xlsx");
    Assert.assertEquals(ContentType.XLSX, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.docx");
    Assert.assertEquals(ContentType.DOCX, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("test.zip");
    Assert.assertEquals(ContentType.ZIP, tmpType);
  }

  @Test
  public void getContentTypeNull() {
    final ContentType tmpType = ContentTypeUtil.getContentType(null);
    Assert.assertEquals(ContentType.OTHER, tmpType);
  }

  @Test
  public void getContentTypeHtml() {
    final Page tmpPage = mock(HtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    Assert.assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentTypeXHtml() {
    final Page tmpPage = mock(XHtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    Assert.assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentTypeText() {
    final Page tmpPage = mock(TextPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    Assert.assertEquals(ContentType.TEXT, tmpType);
  }

  @Test
  public void getContentTypeFromResponseSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("text/css");

    final Page tmpPage = mock(Page.class);
    when(tmpPage.getWebResponse()).thenReturn(tmpResponse);

    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    Assert.assertEquals(ContentType.CSS, tmpType);
  }

  @Test
  public void getContentTypeFromResponseNotSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");

    final Page tmpPage = mock(Page.class);
    when(tmpPage.getWebResponse()).thenReturn(tmpResponse);

    final ContentType tmpType = ContentTypeUtil.getContentType(tmpPage);
    Assert.assertEquals(ContentType.OTHER, tmpType);
  }

  @Test
  public void getFileSuffixNull() {
    String tmpSuffix = ContentTypeUtil.getFileSuffix((Page) null);
    Assert.assertEquals("bin", tmpSuffix);

    tmpSuffix = ContentTypeUtil.getFileSuffix((WebResponse) null);
    Assert.assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getFileSuffixHtml() {
    final Page tmpPage = mock(HtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    Assert.assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffixXHtml() {
    final Page tmpPage = mock(XHtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    Assert.assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffixText() {
    final Page tmpPage = mock(TextPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpPage);
    Assert.assertEquals("csv", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("text/css");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("css", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseNotSupported() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseNotSupportedContentDisposition() {
    final WebResponse tmpResponse = mock(WebResponse.class);
    when(tmpResponse.getContentType()).thenReturn("application/wetator");
    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.pdf;");

    String tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("pdf", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("bin", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("bin", tmpSuffix);

    when(tmpResponse.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=");
    tmpSuffix = ContentTypeUtil.getFileSuffix(tmpResponse);
    Assert.assertEquals("bin", tmpSuffix);
  }
}
