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


package org.rbri.wet.backend.htmlunit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitFinderDelegatorTest {

  @Test
  public void testConstructorNullPage() {
    try {
      new HtmlUnitFinderDelegator(null);
      Assert.fail("NullPointerException expected.");
    } catch (NullPointerException e) {
      Assert.assertEquals("HtmlPage can't be null", e.getMessage());
    }
  }
}
