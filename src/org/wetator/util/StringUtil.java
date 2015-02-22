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


package org.wetator.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * StringUtil contains some useful extensions to java.lang.String. Due to the
 * fact, that this class cannot be extended, all methods herein are defined
 * as static and contain the receiver as a first argument.
 * 
 * @author rbri
 */
public final class StringUtil {

  /**
   * Extract all fields in a String given a list of delimiter characters and
   * an optional escape character usable to escape field delimits.
   * <P>
   * Example:
   * 
   * <PRE>
   *    extractStrings("1\\n\\n2\\n3\\\\n4", "\\n", '\\') ->
   *    returns 4 strings: "1", "", "2" and "3\\n4"
   * </PRE>
   * 
   * Example (escape with no delimiter):
   * 
   * <PRE>
   *    extractStrings("123\\c4", "\\n", '\\') ->
   *    returns 1 string: "123\\c4"
   * </PRE>
   * 
   * Example (escape with escape):
   * 
   * <PRE>
   *    extractStrings("123\\\\4", "\\n", '\\') ->
   *    returns 1 string: "123\\4"
   * </PRE>
   * 
   * </p>
   * 
   * @param aReceiver is the string to be split into tokens
   * @param aDelimiter is a String
   * @param anEscapeChar an optional character, that can be used as an escape
   *        character inside receiver to make delimiter characters ignored.
   *        Specify -1 here to not use escape characters.
   * @return a list, if there are no parts alway an empty list is returned
   */
  public static List<String> extractStrings(final String aReceiver, final String aDelimiter, final int anEscapeChar) {
    final List<String> tmpResult = new LinkedList<String>();
    StringBuilder tmpCurrentToken;
    int tmpIndex;
    int tmpSize;
    int tmpDelimiterSize;
    char tmpChar;

    if (null == aReceiver) {
      return tmpResult;
    }

    tmpSize = aReceiver.length();
    if (tmpSize < 1) {
      return tmpResult;
    }

    tmpDelimiterSize = aDelimiter.length();
    if (tmpDelimiterSize == 1 && aDelimiter.charAt(0) == anEscapeChar) {
      throw new IllegalArgumentException("Delimiter must be different from escape char.");
    }

    tmpCurrentToken = new StringBuilder();

    for (tmpIndex = 0; tmpIndex < tmpSize; tmpIndex++) {
      if (aReceiver.startsWith(aDelimiter, tmpIndex)) {
        tmpResult.add(tmpCurrentToken.toString());
        tmpCurrentToken = new StringBuilder();
        tmpIndex = tmpIndex + tmpDelimiterSize - 1;
      } else {
        tmpChar = aReceiver.charAt(tmpIndex);

        if (anEscapeChar == tmpChar) {
          tmpIndex++;

          if (tmpIndex < tmpSize) {
            if (aReceiver.startsWith(aDelimiter, tmpIndex)) {
              tmpCurrentToken.append(aReceiver.charAt(tmpIndex));
            } else if (anEscapeChar == aReceiver.charAt(tmpIndex)) {
              tmpCurrentToken.append((char) anEscapeChar);
            } else {
              tmpCurrentToken.append((char) anEscapeChar);
              tmpCurrentToken.append(aReceiver.charAt(tmpIndex));
            }
          } else {
            tmpCurrentToken.append((char) anEscapeChar);
          }
        } else {
          tmpCurrentToken.append(tmpChar);
        }
      }
    }

    tmpResult.add(tmpCurrentToken.toString());

    return tmpResult;
  }

  /**
   * @param aDate the date to format
   * @return the formatted date as string
   */
  public static String formatDate(final Date aDate) {
    if (null == aDate) {
      return null;
    }

    return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT).format(aDate);
  }

  /**
   * Split the given string into fixed length parts.
   * 
   * @param aText the given text
   * @param aSize the size of the parts
   * @return an list of parts
   */
  public static List<String> split(final String aText, final int aSize) {
    if (null == aText) {
      return Collections.emptyList();
    }

    final int tmpLength = aText.length();
    final List<String> tmpResult = new ArrayList<String>((tmpLength + aSize - 1) / aSize);

    for (int i = 0; i < tmpLength; i += aSize) {
      tmpResult.add(aText.substring(i, Math.min(tmpLength, i + aSize)));
    }
    return tmpResult;
  }

  /**
   * Private constructor to be invisible.
   */
  private StringUtil() {
    super();
  }
}
