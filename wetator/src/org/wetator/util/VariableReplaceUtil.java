/*
 * Copyright (c) 2008-2013 wetator.org
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

import java.util.List;

import org.wetator.core.Variable;

/**
 * Helpers to replace the variables in a String.
 * 
 * @author rbri
 */
public final class VariableReplaceUtil {

  private static final String VAR_START_SEQ = "${";
  private static final String VAR_END_SEQ = "}";

  /**
   * Replace all place holders in aStringWithPlaceholders.
   * 
   * @param aStringWithPlaceholders the string with the place holders
   * @param aVariables the values for the place holders
   * @param aForPrintFlag indicated, if the replacement is done for any kind of output; if yes secrets are not visible
   * @return the string after the replacement
   */
  public static String replaceVariables(final String aStringWithPlaceholders, final List<Variable> aVariables,
      final boolean aForPrintFlag) {
    if (null == aStringWithPlaceholders) {
      return aStringWithPlaceholders;
    }
    if (null == aVariables || aVariables.isEmpty()) {
      return aStringWithPlaceholders;
    }

    final StringBuilder tmpResult = new StringBuilder(aStringWithPlaceholders);

    int tmpStartPos = 0;
    int tmpVarStartPos = tmpResult.indexOf(VAR_START_SEQ, tmpStartPos);
    int tmpVarEndPos;

    while (tmpVarStartPos > -1) {
      tmpVarEndPos = tmpResult.indexOf(VAR_END_SEQ, tmpVarStartPos);
      if (tmpVarEndPos < 0) {
        return tmpResult.toString();
      }

      final String tmpVarName = tmpResult.substring(tmpVarStartPos + VAR_START_SEQ.length(), tmpVarEndPos);

      for (final Variable tmpVariable : aVariables) {
        if (tmpVarName.equals(tmpVariable.getName())) {
          String tmpValue;
          if (aForPrintFlag) {
            tmpValue = tmpVariable.getValue().toString();
          } else {
            tmpValue = tmpVariable.getValue().getValue();
          }

          // replace
          tmpResult.replace(tmpVarStartPos, tmpVarEndPos + VAR_END_SEQ.length(), tmpValue);

          // found; move our startpos only to the start of the replace
          tmpStartPos = tmpVarStartPos;

          break; // first with the correct name wins
        }

        // done with this; move our startpos
        tmpStartPos = tmpVarStartPos + VAR_START_SEQ.length();
      }

      tmpVarStartPos = tmpResult.indexOf(VAR_START_SEQ, tmpStartPos);
    }

    return tmpResult.toString();
  }

  /**
   * Private constructor to be invisible.
   */
  private VariableReplaceUtil() {
    super();
  }
}