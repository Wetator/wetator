/*
 * Copyright (c) 2008-2026 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
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
import org.wetator.test.junit.BrowserRunner.Browsers;
import org.wetator.test.junit.BrowserRunner.IBrowserTest;

/**
 * @author frank.danek
 */
@RunWith(BrowserRunner.class)
public class BrowserRunnerTest implements IBrowserTest {

  private static boolean firstFound;
  private static boolean firstFound2;

  private BrowserType browserType;

  @Test
  public void test() {
    Assert.assertNull(browserType);
  }

  @Test
  @Browsers
  public void testDefault() {
    Assert.assertEquals(BrowserType.FIREFOX_ESR, browserType);
  }

  @Test
  @Browsers(BrowserType.FIREFOX_ESR)
  public void testSingleFirefoxEsr() {
    Assert.assertEquals(BrowserType.FIREFOX_ESR, browserType);
  }

  @Test
  @Browsers(BrowserType.FIREFOX)
  public void testSingleFirefox() {
    Assert.assertEquals(BrowserType.FIREFOX, browserType);
  }

  @Test
  @Browsers(BrowserType.CHROME)
  public void testSingleChrome() {
    Assert.assertEquals(BrowserType.CHROME, browserType);
  }

  @Test
  @Browsers(BrowserType.EDGE)
  public void testSingleEdge() {
    Assert.assertEquals(BrowserType.EDGE, browserType);
  }

  @Test
  @Browsers({ BrowserType.FIREFOX_ESR, BrowserType.CHROME })
  public void testMultiple() {
    if (!firstFound) {
      Assert.assertEquals(BrowserType.FIREFOX_ESR, browserType);
      firstFound = true;
    } else {
      Assert.assertEquals(BrowserType.CHROME, browserType);
    }
  }

  @Test
  @Browsers({ BrowserType.FIREFOX, BrowserType.EDGE })
  public void testMultiple2() {
    if (!firstFound2) {
      Assert.assertEquals(BrowserType.FIREFOX, browserType);
      firstFound2 = true;
    } else {
      Assert.assertEquals(BrowserType.EDGE, browserType);
    }
  }

  @Override
  public void setBrowser(final BrowserType aBrowserType) {
    browserType = aBrowserType;
  }
}
