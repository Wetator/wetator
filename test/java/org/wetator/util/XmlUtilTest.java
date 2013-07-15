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


package org.wetator.util;

import org.junit.Test;

/**
 * @author rbri
 */
public class XmlUtilTest {

  @Test
  public void testNormalizeBodyValue_NormalChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", tmpXmlUtil
        .normalizeBodyValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }

  @Test
  public void testNormalizeBodyValue_SpecialChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("&lt;&gt;&amp;", tmpXmlUtil.normalizeBodyValue("<>&"));
  }

  @Test
  public void testNormalizeBodyValue_ForbiddenChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
        + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F"
        + "\u0020\u0010";

    org.junit.Assert.assertEquals("\t\r\n ", tmpXmlUtil.normalizeBodyValue(tmpInput));
  }

  @Test
  public void testNormalizeBodyValue_Empty() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("", tmpXmlUtil.normalizeBodyValue(null));
  }

  @Test
  public void testNormalizeBody_EscapeMany() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&lt;de&lt;", tmpXmlUtil.normalizeBodyValue("ab<de<"));
  }

  @Test
  public void testNormalizeBody_EscapeOne() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&gt;de", tmpXmlUtil.normalizeBodyValue("ab>de"));
  }

  @Test
  public void testNormalizeBody_EscapeAmpersand() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&amp;de", tmpXmlUtil.normalizeBodyValue("ab&de"));
  }

  @Test
  public void testNormalizeBody_EscapeEuro() {
    XmlUtil tmpXmlUtil = new XmlUtil("iso-8859-1");
    org.junit.Assert.assertEquals("11 &#8364;", tmpXmlUtil.normalizeBodyValue("11 \u20AC"));
  }

  @Test
  public void testNormalizeBodyValueChar_Empty() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("", tmpXmlUtil.normalizeBodyValue(null));
  }

  @Test
  public void testNormalizeAttributeValue_NormalChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", tmpXmlUtil
        .normalizeAttributeValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }

  @Test
  public void testNormalizeAttributeValue_SpecialChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("&lt;&gt;&amp;&quot;&apos;", tmpXmlUtil.normalizeAttributeValue("<>&\"'"));
  }

  @Test
  public void testNormalizeAttributeValue_ForbiddenChars() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
        + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F"
        + "\u0020\u0010";

    org.junit.Assert.assertEquals("\t\r\n ", tmpXmlUtil.normalizeAttributeValue(tmpInput));
  }

  @Test
  public void testNormalizeAttributeValue_EscapeMany() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&gt;de&lt;", tmpXmlUtil.normalizeAttributeValue("ab>de<"));
  }

  @Test
  public void testNormalizeAttributeValue_EscapeOne() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&amp;de", tmpXmlUtil.normalizeAttributeValue("ab&de"));
  }

  @Test
  public void testNormalizeAttributeValue_EscapeQuote() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&quot;de", tmpXmlUtil.normalizeAttributeValue("ab\"de"));
  }

  @Test
  public void testNormalizeAttributeValue_EscapeApostroph() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("ab&apos;de", tmpXmlUtil.normalizeAttributeValue("ab'de"));
  }

  @Test
  public void testNormalizeAttributeValue_Null() {
    XmlUtil tmpXmlUtil = new XmlUtil("UTF-8");
    org.junit.Assert.assertEquals("", tmpXmlUtil.normalizeAttributeValue(null));
  }
}
