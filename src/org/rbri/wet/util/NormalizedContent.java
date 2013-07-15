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

/**
 * Stores Strings in a normalized way.
 * All whitespace is reduced to one; LineBreak is always \n
 *
 * @author rbri
 */
public final class NormalizedContent {
    private static final String BLANK = " ";

    private StringBuilder content;

    public NormalizedContent() {
        content = new StringBuilder();
    }

    public NormalizedContent(String aString) {
        this();
        append(aString);
    }

    public void append(final String aString) {
        if (null == aString) {
            return;
        }
        if (aString.length() < 1) {
            return;
        }

        boolean tmpBlank = (content.length() == 0) || isWhitespace(content.charAt(content.length() -1));

        for (int i = 0; i < aString.length(); i++) {
            char tmpChar = aString.charAt(i);
            if (isWhitespace(tmpChar))  {
                if (!tmpBlank) {
                    tmpBlank = true;
                    // don't use tmpChar here,
                    // we replace all whitespace with a blank
                    content.append(BLANK);
                }
            } else {
                tmpBlank = false;
                content.append(tmpChar);
            }
        }
    }

    public String substring(int aStartPos, int anEndPos) {
        return content.substring(aStartPos, anEndPos);
    }

    public int length() {
        return content.length();
    }

    public String toString() {
        return content.toString();
    }

    private boolean isWhitespace(char aChar) {
        // char 160 not detected as whitespace by java
        return (Character.isWhitespace(aChar) || (char) 160 == aChar);
    }
}
