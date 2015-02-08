/*
 * Copyright (c) 2008-2015 wetator.org
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
public class XMLUtilTest {

  @Test
  public void normalizeBodyValue_NormalChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
        tmpXMLUtil.normalizeBodyValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }

  @Test
  public void normalizeBodyValue_SpecialChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("&lt;&gt;&amp;", tmpXMLUtil.normalizeBodyValue("<>&"));
  }

  @Test
  public void normalizeBodyValue_ForbiddenChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
        + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F"
        + "\u0020\u0010";

    org.junit.Assert.assertEquals("\t\r\n ", tmpXMLUtil.normalizeBodyValue(tmpInput));
  }

  @Test
  public void normalizeBodyValue_Empty() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("", tmpXMLUtil.normalizeBodyValue(null));
  }

  @Test
  public void normalizeBody_EscapeMany() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&lt;de&lt;", tmpXMLUtil.normalizeBodyValue("ab<de<"));
  }

  @Test
  public void normalizeBody_EscapeOne() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&gt;de", tmpXMLUtil.normalizeBodyValue("ab>de"));
  }

  @Test
  public void normalizeBody_EscapeAmpersand() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&amp;de", tmpXMLUtil.normalizeBodyValue("ab&de"));
  }

  @Test
  public void normalizeBody_EscapeEuro() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("11 &#8364;", tmpXMLUtil.normalizeBodyValue("11 \u20AC"));
  }

  @Test
  public void normalizeBodyValueChar_Empty() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("", tmpXMLUtil.normalizeBodyValue(null));
  }

  @Test
  public void normalizeAttributeValue_NormalChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
        tmpXMLUtil.normalizeAttributeValue("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  }

  @Test
  public void normalizeAttributeValue_SpecialChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("&lt;&gt;&amp;&quot;&apos;", tmpXMLUtil.normalizeAttributeValue("<>&\"'"));
  }

  @Test
  public void normalizeAttributeValue_ForbiddenChars() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    String tmpInput = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u0009\r\u000B\u000C\n\u000E\u000F"
        + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F"
        + "\u0020\u0010";

    org.junit.Assert.assertEquals("\t\r\n ", tmpXMLUtil.normalizeAttributeValue(tmpInput));
  }

  @Test
  public void normalizeAttributeValue_EscapeMany() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&gt;de&lt;", tmpXMLUtil.normalizeAttributeValue("ab>de<"));
  }

  @Test
  public void normalizeAttributeValue_EscapeOne() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&amp;de", tmpXMLUtil.normalizeAttributeValue("ab&de"));
  }

  @Test
  public void normalizeAttributeValue_EscapeQuote() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&quot;de", tmpXMLUtil.normalizeAttributeValue("ab\"de"));
  }

  @Test
  public void normalizeAttributeValue_EscapeApostroph() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("ab&apos;de", tmpXMLUtil.normalizeAttributeValue("ab'de"));
  }

  @Test
  public void normalizeAttributeValue_Null() {
    XMLUtil tmpXMLUtil = new XMLUtil();
    org.junit.Assert.assertEquals("", tmpXMLUtil.normalizeAttributeValue(null));
  }
}
