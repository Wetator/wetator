/*
 * Copyright (c) 2008-2015 wetator.org
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


package org.wetator;

import org.junit.Test;

/**
 * Test cases for class Version.
 * 
 * @author rbri
 */
public class VersionTest {

  @Test
  public void versionPattern() {
    org.junit.Assert.assertTrue("wetator.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertTrue("wetator-0.11.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertTrue("wetator-0.1.1.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertTrue("wetator-1.7.3.jar".matches(Version.WETATOR_JAR_PATTERN));

    org.junit.Assert.assertTrue("wetator-0.11-SNAPSHOT.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertTrue("wetator-0.1.1-SNAPSHOT.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertTrue("wetator-1.7.3-SNAPSHOT.jar".matches(Version.WETATOR_JAR_PATTERN));

    org.junit.Assert.assertFalse("wetator-ant.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertFalse("wetator-ext.jar".matches(Version.WETATOR_JAR_PATTERN));
  }
}
