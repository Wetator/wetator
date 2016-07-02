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

import static org.mockito.Matchers.*;
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
    final Page page = mock(HtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(page);
    Assert.assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentTypeXHtml() {
    final Page page = mock(XHtmlPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(page);
    Assert.assertEquals(ContentType.HTML, tmpType);
  }

  @Test
  public void getContentTypeText() {
    final Page page = mock(TextPage.class);
    final ContentType tmpType = ContentTypeUtil.getContentType(page);
    Assert.assertEquals(ContentType.TEXT, tmpType);
  }

  @Test
  public void getContentTypeFromResponseSupported() {
    final WebResponse response = mock(WebResponse.class);
    when(response.getContentType()).thenReturn("text/css");

    final Page page = mock(Page.class);
    when(page.getWebResponse()).thenReturn(response);

    final ContentType tmpType = ContentTypeUtil.getContentType(page);
    Assert.assertEquals(ContentType.CSS, tmpType);
  }

  @Test
  public void getContentTypeFromResponseNotSupported() {
    final WebResponse response = mock(WebResponse.class);
    when(response.getContentType()).thenReturn("application/wetator");

    final Page page = mock(Page.class);
    when(page.getWebResponse()).thenReturn(response);

    final ContentType tmpType = ContentTypeUtil.getContentType(page);
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
    final Page page = mock(HtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(page);
    Assert.assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffixXHtml() {
    final Page page = mock(XHtmlPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(page);
    Assert.assertEquals("html", tmpSuffix);
  }

  @Test
  public void getFileSuffixText() {
    final Page page = mock(TextPage.class);
    final String tmpSuffix = ContentTypeUtil.getFileSuffix(page);
    Assert.assertEquals("csv", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseSupported() {
    final WebResponse response = mock(WebResponse.class);
    when(response.getContentType()).thenReturn("text/css");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("css", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseNotSupported() {
    final WebResponse response = mock(WebResponse.class);
    when(response.getContentType()).thenReturn("application/wetator");

    final String tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("bin", tmpSuffix);
  }

  @Test
  public void getFileSuffixFromResponseNotSupportedContentDisposition() {
    final WebResponse response = mock(WebResponse.class);
    when(response.getContentType()).thenReturn("application/wetator");
    when(response.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.pdf;");

    String tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("pdf", tmpSuffix);

    when(response.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot.");
    tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("bin", tmpSuffix);

    when(response.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=quot");
    tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("bin", tmpSuffix);

    when(response.getResponseHeaderValue(anyString())).thenReturn("attachment; filename=");
    tmpSuffix = ContentTypeUtil.getFileSuffix(response);
    Assert.assertEquals("bin", tmpSuffix);
  }
}
