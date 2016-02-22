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


package org.wetator.backend.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstraction for a sequence of keystrokes.
 *
 * @author rbri
 */
public class KeySequence {
  private List<Key> keys = new ArrayList<>();

  /**
   * Supported keys.
   */
  public static final class Key {
    public static final Key KEY_RETURN = new Key();

    private char character;

    private Key() {
    }

    Key(char aChar) {
      character = aChar;
    }

    public char getChar() {
      return character;
    }
  }

  public static KeySequence parse(String aKeySequenceString) {
    KeySequence tmpSequence = new KeySequence();

    for (int i = 0; i < aKeySequenceString.length(); i++) {
      char tmpChar = aKeySequenceString.charAt(i);

      if (tmpChar == '\\') {
        i++;
        if (i == aKeySequenceString.length()) {
          throw new IllegalArgumentException("Invalid escape at pos " + --i);
        }
        tmpSequence.type(aKeySequenceString.charAt(i));
      } else if (tmpChar == '[') {
        int tmpEndPos = aKeySequenceString.indexOf(']', i);
        if (tmpEndPos == -1) {
          throw new IllegalArgumentException("Invalid special key definition; closing ']' missing.");
        }

        String tmpKeyName = aKeySequenceString.substring(i + 1, tmpEndPos);
        if ("ENTER".equals(tmpKeyName)) {
          tmpSequence.pressKey(Key.KEY_RETURN);
        } else {
          throw new IllegalArgumentException("Unsupported key '" + tmpKeyName + "'");
        }
        i = tmpEndPos;
      } else {
        tmpSequence.type(tmpChar);
      }
    }
    return tmpSequence;
  }

  /**
   * Types the specified character.
   *
   * @param aChar the character
   */
  public void type(final char aChar) {
    keys.add(new Key(aChar));
  }

  /**
   * Press the specified special key.
   *
   * @param aKey the key
   */
  public void pressKey(final Key aKey) {
    keys.add(aKey);
  }

  public List<Key> getKeys() {
    return Collections.unmodifiableList(keys);
  }
}