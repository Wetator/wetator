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


package org.wetator.test.junit;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.wetator.backend.WetBackend.Browser;

/**
 * A special subclass of a {@link FrameworkMethod} to transport a {@link Browser}.
 * 
 * @author frank.danek
 */
public class BrowserFrameworkMethod extends FrameworkMethod {

  private Browser browser;

  /**
   * @param aMethod the method to wrap
   * @param aBrowser the browser to run the method for
   */
  public BrowserFrameworkMethod(Method aMethod, Browser aBrowser) {
    super(aMethod);

    browser = aBrowser;
  }

  /**
   * @return the browser
   */
  public Browser getBrowser() {
    return browser;
  }

}
