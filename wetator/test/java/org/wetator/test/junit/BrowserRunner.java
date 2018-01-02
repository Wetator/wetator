/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.wetator.backend.IBrowser.BrowserType;

/**
 * This custom runner implements browser parameterized tests. When running a test class, each method is run for all
 * {@link BrowserType}s annotated by {@link Browsers}.<br>
 * Only work correctly if the test class implements {@link IBrowserTest}.<br>
 * For example, write:
 *
 * <pre>
 * &#064;RunWith(BrowserRunner.class)
 * public class SomeTest implements BrowserTest {
 *
 *   &#064;Test
 *   &#064;Browsers({ Browser.FIREFOX_52 })
 *   public void test() {
 *     // your test method that is run with Firefox 38
 *   }
 * }
 * </pre>
 *
 * @author frank.danek
 */
public class BrowserRunner extends BlockJUnit4ClassRunner {

  /**
   * The constructor.
   *
   * @param aKlass the class to run
   * @throws InitializationError if the test class is malformed.
   */
  public BrowserRunner(final Class<?> aKlass) throws InitializationError {
    super(aKlass);
  }

  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    final List<FrameworkMethod> tmpMethods = super.computeTestMethods();
    if (!IBrowserTest.class.isAssignableFrom(getTestClass().getJavaClass())) {
      // the test class not implements BrowserTest
      return tmpMethods;
    }

    // the test class implements BrowserTest -> we have to check the test methods for Browsers
    // annotations
    final List<FrameworkMethod> tmpBrowserMethods = new ArrayList<FrameworkMethod>(tmpMethods.size());

    for (FrameworkMethod tmpMethod : tmpMethods) {
      final Browsers tmpBrowsers = tmpMethod.getAnnotation(Browsers.class);
      if (tmpBrowsers != null) {
        // we found a Browsers annotation -> we add one instance of the test method for each browser to the result
        for (BrowserType tmpBrowserType : tmpBrowsers.value()) {
          tmpBrowserMethods.add(new BrowserFrameworkMethod(tmpMethod.getMethod(), tmpBrowserType));
        }
      } else {
        // we found no Browser annotation -> just add the test method to the result
        tmpBrowserMethods.add(tmpMethod);
      }
    }
    return tmpBrowserMethods;
  }

  @Override
  protected Statement methodInvoker(final FrameworkMethod aMethod, final Object aTest) {
    if (aMethod instanceof BrowserFrameworkMethod && aTest instanceof IBrowserTest) {
      // we have a BrowserFrameworkMethod and an implementation of BrowserTest -> set the browser of the test to the
      // browser given by the BrowserFrameworkMethod
      final BrowserFrameworkMethod tmpBrowserMethod = (BrowserFrameworkMethod) aMethod;
      final IBrowserTest tmpWebServerTest = (IBrowserTest) aTest;
      tmpWebServerTest.setBrowser(tmpBrowserMethod.getBrowser());
    }
    return super.methodInvoker(aMethod, aTest);
  }

  /**
   * This annotation marks a test method that should be run with specific browsers.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public static @interface Browsers {

    /**
     * The browsers the test should be run with.
     */
    BrowserType[] value() default { };
  }

  /**
   * Implement this interface to be able to use the {@link BrowserRunner}.
   *
   * @author frank.danek
   */
  public interface IBrowserTest {

    /**
     * @param aBrowserType the browser to set
     */
    void setBrowser(BrowserType aBrowserType);

  }
}
