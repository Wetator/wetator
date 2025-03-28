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


package org.wetator.test;

import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.test.junit.BrowserRunner.IBrowserTest;

/**
 * Base test class for all tests that methods need to be run with different browsers.
 *
 * @author frank.danek
 */
public abstract class AbstractBrowserTest implements IBrowserTest {

  private BrowserType browserType;

  /**
   * @return the browser
   */
  public BrowserType getBrowser() {
    return browserType;
  }

  @Override
  public void setBrowser(final BrowserType aBrowserType) {
    browserType = aBrowserType;
  }
}
