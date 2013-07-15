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


package org.wetator;

import java.net.URL;
import java.util.jar.Manifest;

/**
 * A small class to maintain the version information.
 * 
 * @author rbri
 */
public final class Version {

  /**
   * The build.
   */
  public static final String BUILD = "2010121601";

  /**
   * A simple main function to be able to ask for the version from a command line.
   * 
   * @param anArgsArray ignored
   */
  public static void main(final String[] anArgsArray) {
    System.out.println(getFullProductName());
  }

  /**
   * @return the full name with the complete version information.
   */
  public static String getFullProductName() {
    return getProductName() + " Version " + getVersion() + " Build " + getBuild();
  }

  /**
   * @return the product name.
   */
  public static String getProductName() {
    return readFromManifest("Application-Name", "Wetator");
  }

  /**
   * @return the version.
   */
  public static String getVersion() {
    final String tmpVersion = readFromManifest("Version", "local build");
    return tmpVersion.replaceAll("_", ".");
  }

  /**
   * @return the build number.
   */
  public static String getBuild() {
    return BUILD;
  }

  /**
   * This class should not be instantiated.
   */
  private Version() {
    // nothing
  }

  private static String readFromManifest(final String anAttributeName, final String aDefault) {
    final Class<?> tmpClass = Version.class;
    final String tmpClassName = tmpClass.getSimpleName();
    final String tmpClassFileName = tmpClassName + ".class";
    final String tmpPathToThisClass = tmpClass.getResource(tmpClassFileName).toExternalForm();

    final int tmpPos = tmpPathToThisClass.indexOf('!');
    final StringBuilder tmpPathToManifest = new StringBuilder(tmpPathToThisClass.substring(0, tmpPos + 1));
    tmpPathToManifest.append("/META-INF/MANIFEST.MF");
    Manifest tmpManifest;
    try {
      tmpManifest = new Manifest(new URL(tmpPathToManifest.toString()).openStream());
      final String tmpValue = tmpManifest.getAttributes("Application").getValue(anAttributeName);
      return tmpValue;
    } catch (final Exception e) {
      return aDefault;
    }
  }
}
