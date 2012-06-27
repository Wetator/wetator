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


package org.wetator.core;

import java.io.File;

/**
 * Our test case.
 * The test case has a name and a file with all the commands.
 * The commands are executed for all browsers.
 * 
 * @author rbri
 */
public final class TestCase {
  private final String name;
  private final File file;

  /**
   * Constructor.
   * 
   * @param aName the name of the test case (e.g. the name of the file without the path)
   * @param aFile the file containing the test definition
   */
  public TestCase(final String aName, final File aFile) {
    name = aName;
    file = aFile;
  }

  /**
   * @return the name of this test case
   */
  public String getName() {
    return name;
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }
}
