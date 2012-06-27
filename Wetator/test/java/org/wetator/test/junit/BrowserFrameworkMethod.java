/*
 * Copyright (c) 2008-2012 wetator.org
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
import org.wetator.backend.IBrowser.BrowserType;

/**
 * A special subclass of a {@link FrameworkMethod} to transport a {@link BrowserType}.
 * 
 * @author frank.danek
 */
public class BrowserFrameworkMethod extends FrameworkMethod {

  private BrowserType browserType;

  /**
   * @param aMethod the method to wrap
   * @param aBrowserType the browser to run the method for
   */
  public BrowserFrameworkMethod(Method aMethod, BrowserType aBrowserType) {
    super(aMethod);

    browserType = aBrowserType;
  }

  /**
   * @return the browser
   */
  public BrowserType getBrowser() {
    return browserType;
  }

}
