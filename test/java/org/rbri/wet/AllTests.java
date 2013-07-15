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


package org.rbri.wet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rbri
 */
public class AllTests extends TestCase {
  public static void main(String[] anArgsArray) {
    System.out.println(org.rbri.wet.Version.getFullProductName());

    // System.out.println();
    // System.out.println(" Classpath: " +
    // System.getProperty("java.class.path"));
    // System.out.println();

    // Logger.setLogService(new PrintStreamLogService());
    // Logger.setLevelInfo();

    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {

    TestSuite tmpSuite = new TestSuite("All Wetator tests");

    tmpSuite.addTest(org.rbri.wet.backend.AllTests.suite());
    tmpSuite.addTest(org.rbri.wet.core.AllTests.suite());
    tmpSuite.addTest(org.rbri.wet.commandset.AllTests.suite());
    tmpSuite.addTest(org.rbri.wet.i18n.AllTests.suite());
    tmpSuite.addTest(org.rbri.wet.scripter.AllTests.suite());
    tmpSuite.addTest(org.rbri.wet.util.AllTests.suite());

    return tmpSuite;
  }
}
