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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class AllTests {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {

        TestSuite suite = new TestSuite("All Wetator backend htmlunit tests");

        suite.addTest(org.rbri.wet.backend.htmlunit.util.AllTests.suite());

        suite.addTest(HtmlElementUtilTest.suite());
//        suite.addTest(HtmlUnitControlFinderAllBodyControlsForTextTest.suite());

        suite.addTest(HtmlUnitControlFinderTest.suite());
        suite.addTest(HtmlUnitControlFinderGetAllClickablesTest.suite());
        suite.addTest(HtmlUnitControlFinderGetAllElementsForTextTest.suite());
        suite.addTest(HtmlUnitControlFinderGetAllOtherControlsTest.suite());
        suite.addTest(HtmlUnitControlFinderGetAllSetablesTest.suite());
        suite.addTest(HtmlUnitControlFinderGetAllSelectablesTest.suite());
        suite.addTest(HtmlUnitControlFinderGetFirstClickableTextElementTest.suite());
        suite.addTest(HtmlUnitControlTest.suite());

        return suite;
    }
}
