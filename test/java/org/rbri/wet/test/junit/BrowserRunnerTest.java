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


package org.rbri.wet.test.junit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rbri.wet.backend.WetBackend.Browser;
import org.rbri.wet.test.junit.BrowserRunner.BrowserTest;
import org.rbri.wet.test.junit.BrowserRunner.Browsers;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class BrowserRunnerTest implements BrowserTest {

  private static boolean firstFound;

  private Browser browser;

  @Test
  public void test() {
    Assert.assertNull(browser);
  }

  @Test
  @Browsers
  public void testDefault() {
    Assert.assertEquals(Browser.FIREFOX_3_6, browser);
  }

  @Test
  @Browsers(Browser.INTERNET_EXPLORER_8)
  public void testSingle() {
    Assert.assertEquals(Browser.INTERNET_EXPLORER_8, browser);
  }

  @Test
  @Browsers( { Browser.FIREFOX_3, Browser.INTERNET_EXPLORER_8 })
  public void testMultiple() {
    if (!firstFound) {
      Assert.assertEquals(Browser.FIREFOX_3, browser);
      firstFound = true;
    } else {
      Assert.assertEquals(Browser.INTERNET_EXPLORER_8, browser);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.test.junit.BrowserRunner.BrowserTest#setBrowser(org.rbri.wet.backend.WetBackend.Browser)
   */
  @Override
  public void setBrowser(Browser aBrowser) {
    browser = aBrowser;
  }
}
