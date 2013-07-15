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


package org.rbri.wet.util;

import java.util.List;
import java.util.Locale;



/**
 * An object that stores a variable.
 *  
 * @author rbri
 */
public final class SecretString {
    private static final String SECRET_PRINT = "****";

    private String value;
    private String valueForPrint;
    
    
    public static String toString(List<SecretString> aSecretStringList) {
        StringBuilder tmpResult = new StringBuilder();

        boolean tmpIsNotFirst = false;
        for (SecretString tmpSecretString : aSecretStringList) {
            if (tmpIsNotFirst) {
                tmpResult.append(", ");
            } else {
                tmpIsNotFirst = true;
            }
            tmpResult.append(tmpSecretString.toString());
        }

        return tmpResult.toString();
    }
    
    
    /**
     * Constructor.
     */
    public SecretString(String aValue, boolean aSecretFlag) {
        this(aValue, SECRET_PRINT);

        if (!aSecretFlag) {
            valueForPrint = aValue;
        }
    }


    public SecretString(String aValue, String aValueForPrint) {
        super();

        value = aValue;
        valueForPrint = aValueForPrint;
    }


    public String getValue() {
        return value;
    }
    
    
    public void prefixWith(String aValuePrefix) {
        prefixWith(aValuePrefix, aValuePrefix);
    }
    
    
    public void prefixWith(String aValuePrefix, String aValueForPrintPrefix) {
        value = aValuePrefix + value;
        valueForPrint = aValueForPrintPrefix + valueForPrint;
    }
    
    
    @Override
    public String toString() {
        return valueForPrint;
    }
    

    public SearchPattern getSearchPattern() {
        return new SearchPattern(getValue());
    }
    

    public boolean startsWith(String aPrefix) {
        return value.startsWith(aPrefix);
    }


    public boolean startsWith(String aPrefix, int anOffset) {
        return value.startsWith(aPrefix, anOffset);
    }
    

    public String toLowerCase(Locale aLocale) {
        return value.toLowerCase(aLocale);
    }
    

    public SecretString trim() {
        value = value.trim();
        valueForPrint = valueForPrint.trim();

        return this;
    }
}
