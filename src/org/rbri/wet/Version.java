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

/**
 * A small class to maintain the version information.
 * 
 * @author rbri
 */
public final class Version {

  /**
   * The product name.
   */
  public static final String PRODUCT_NAME = "Wetator";
  /**
   * The version.
   */
  public static final String VERSION = "0.9.2";
  /**
   * The build.
   */
  public static final String BUILD = "2010080701";

  /**
   * A simple main function to be able to ask for the version from a command line.
   * 
   * @param anArgsArray ignored
   */
  public static void main(String[] anArgsArray) {
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
    return PRODUCT_NAME;
  }

  /**
   * @return the version.
   */
  public static String getVersion() {
    return VERSION;
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
}
