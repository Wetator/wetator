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


package org.wetator.test.junit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.test.junit.BrowserRunner.BrowserTest;
import org.wetator.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class BrowserRunnerTest implements BrowserTest {

  private static boolean firstFound;

  private BrowserType browserType;

  @Test
  public void test() {
    Assert.assertNull(browserType);
  }

  @Test
  @Browsers
  public void testDefault() {
    Assert.assertEquals(BrowserType.FIREFOX_3_6, browserType);
  }

  @Test
  @Browsers(BrowserType.INTERNET_EXPLORER_8)
  public void testSingle() {
    Assert.assertEquals(BrowserType.INTERNET_EXPLORER_8, browserType);
  }

  @Test
  @Browsers( { BrowserType.FIREFOX_3, BrowserType.INTERNET_EXPLORER_8 })
  public void testMultiple() {
    if (!firstFound) {
      Assert.assertEquals(BrowserType.FIREFOX_3, browserType);
      firstFound = true;
    } else {
      Assert.assertEquals(BrowserType.INTERNET_EXPLORER_8, browserType);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.test.junit.BrowserRunner.BrowserTest#setBrowser(org.wetator.backend.IBrowser.BrowserType)
   */
  @Override
  public void setBrowser(BrowserType aBrowserType) {
    browserType = aBrowserType;
  }
}
