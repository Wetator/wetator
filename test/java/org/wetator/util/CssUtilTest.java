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
public class CssUtilTest {

  @Test
  public void escapeIdent() {
    escapeIdent("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

    escapeIdent("\\9\\20\\d\\20\\a", "\t \r \n");

    escapeIdent("\\31test", "1test");
    escapeIdent("\\31 Fest", "1Fest");
    escapeIdent("\\31 ast", "1ast");
    escapeIdent("\\31 0ast", "10ast");

    escapeIdent("f\\3atest", "f:test");
    escapeIdent("f\\3a Fest", "f:Fest");
    escapeIdent("f\\3a\\3arest", "f::rest");

    escapeIdent("\\23test", "#test");

    escapeIdent("", null);
    escapeIdent("ab\\7f d", "ab\u007Fd");
  }

  private void escapeIdent(final String anExpected, final String anInput) {
    org.junit.Assert.assertEquals(anExpected, CssUtil.escapeIdent(anInput));
  }
}
