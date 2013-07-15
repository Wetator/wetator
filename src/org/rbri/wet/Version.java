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
public class Version {

    public static final String PRODUCT_NAME = "Wetator";
    public static final String VERSION = "0.9.1";
    public static final String BUILD = "2010052801";

    public static void main(String[] anArgsArray) {
        System.out.println(getFullProductName());
    }

    public static String getFullProductName() {
        return getProductName() + " Version " + getVersion() + " Build " + getBuild();
    }

    public static String getProductName() {
        return PRODUCT_NAME;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getBuild() {
        return BUILD;
    }
}
