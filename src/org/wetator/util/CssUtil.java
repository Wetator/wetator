/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.util;

/**
 * CssUtil contains some useful helpers for CSS.
 *
 * @author rbri
 */
public final class CssUtil {

  /**
   * The constructor.
   */
  private CssUtil() {
    super();
  }

  /**
   * Escape the the given string. For use as css identifier.<br>
   *
   * @param aString the String to be normalized or null
   * @return a new String
   */
  public static String escapeIdent(final String aString) {
    if (aString == null) {
      return "";
    }

    char tmpChar;
    final int tmpLength = aString.length();

    final StringBuilder tmpResult = new StringBuilder();

    // we have some kind of optimization here
    // if there is no special character inside, then we
    // are returning the original one
    int i = 0;
    while (i < tmpLength) {
      tmpChar = aString.charAt(i);

      if (tmpChar == 0) {
        // If the character is NULL (U+0000), then throw an InvalidCharacterError exception and terminate these steps
        // TODO ignore for the moment
      } else if (tmpChar < 0x001F || tmpChar == 0x007F) {
        // If the character is in the range [\1-\1f] (U+0001 to U+001F) or is U+007F, then the character escaped as code
        // point.
        tmpResult.append(aString.substring(0, i));
        tmpResult.append('\\');
        tmpResult.append(Integer.toString(tmpChar, 16));
        i++;
        break;
      } else if (i == 0 && tmpChar >= 0x0030 && tmpChar <= 0x0039) {
        // If the character is the first character and is in the range [0-9] (U+0030 to U+0039), then the character
        // escaped as code point.
        tmpResult.append(aString.substring(0, i));
        tmpResult.append('\\');
        tmpResult.append(Integer.toString(tmpChar, 16));
        i++;
        break;
      } else if (tmpChar >= 0x0080 || tmpChar == 0x002D || tmpChar == 0x005F || tmpChar >= 0x0030 && tmpChar <= 0x0039
          || tmpChar >= 0x0041 && tmpChar <= 0x005A || tmpChar >= 0x0061 && tmpChar <= 0x007a) {
        // If the character is not handled by one of the above rules and is greater than or equal to U+0080,
        // is "-" (U+002D) or "_" (U+005F), or is in one of the ranges [0-9] (U+0030 to U+0039),
        // [A-Z] (U+0041 to U+005A), or \[a-z] (U+0061 to U+007A), then the character itself.
      } else {
        // Otherwise, the escaped character.
        tmpResult.append(aString.substring(0, i));
        tmpResult.append('\\');
        tmpResult.append(Integer.toString(tmpChar, 16));
        i++;
        break;
      }
      i++;
    }

    boolean tmpEscaped = true;
    while (i < tmpLength) {
      tmpChar = aString.charAt(i);

      if (tmpChar == 0) {
        // If the character is NULL (U+0000), then throw an InvalidCharacterError exception and terminate these steps
        // TODO ignore for the moment
      } else if (tmpChar < 0x001F || tmpChar == 0x007F) {
        // If the character is in the range [\1-\1f] (U+0001 to U+001F) or is U+007F, then the character escaped as code
        // point.
        tmpResult.append('\\');
        tmpResult.append(Integer.toString(tmpChar, 16));
        tmpEscaped = true;
      } else if (tmpChar >= 0x0080 || tmpChar == 0x002D || tmpChar == 0x005F || tmpChar >= 0x0030 && tmpChar <= 0x0039
          || tmpChar >= 0x0041 && tmpChar <= 0x005A || tmpChar >= 0x0061 && tmpChar <= 0x007a) {
        // If the character is not handled by one of the above rules and is greater than or equal to U+0080,
        // is "-" (U+002D) or "_" (U+005F), or is in one of the ranges [0-9] (U+0030 to U+0039),
        // [A-Z] (U+0041 to U+005A), or \[a-z] (U+0061 to U+007A), then the character itself.
        if (tmpEscaped && (tmpChar >= 0x0030 && tmpChar <= 0x0039 || tmpChar >= 0x0041 && tmpChar <= 0x0046
            || tmpChar >= 0x0061 && tmpChar <= 0x0066)) {
          tmpResult.append(' ');
        }
        tmpResult.append(tmpChar);
        tmpEscaped = false;
      } else {
        // Otherwise, the escaped character.
        tmpResult.append('\\');
        tmpResult.append(Integer.toString(tmpChar, 16));
        tmpEscaped = true;
      }
      i++;
    }

    if (tmpResult.length() < 1) {
      return aString;
    }
    return tmpResult.toString();
  }
}
