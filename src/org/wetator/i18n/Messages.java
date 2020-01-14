/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class provides all messages that are used by the wetator.
 *
 * @author rbri
 * @author frank.danek
 */
public final class Messages {

  /**
   * Returns the message for the given message key from the resource bundle 'org.wetator.Messages'. Additionally the
   * values from the given parameters are applied to the found message (see {@link MessageFormat}).
   *
   * @param aMessageKey the message key
   * @param aParameters the message parameters
   * @return the message
   */
  public static String getMessage(final String aMessageKey, final Object... aParameters) {
    // TODO move the messages file to the root level
    final ResourceBundle tmpMessages = ResourceBundle.getBundle("org.wetator.Messages", Locale.ROOT);

    StringBuilder tmpMessageResource;
    try {
      tmpMessageResource = new StringBuilder(tmpMessages.getString(aMessageKey));
    } catch (final MissingResourceException e) {
      tmpMessageResource = new StringBuilder("Unknown message key ''");
      tmpMessageResource.append(aMessageKey).append("''");
      if (null != aParameters && aParameters.length > 0) {
        tmpMessageResource.append(" (param(s): ");
        for (int i = 0; i < aParameters.length; i++) {
          tmpMessageResource.append(" ''{");
          tmpMessageResource.append(Integer.toString(i));
          tmpMessageResource.append("}''");
        }
        tmpMessageResource.append(')');
      }
      tmpMessageResource.append('.');
    }
    final MessageFormat tmpMessageFormat = new MessageFormat(tmpMessageResource.toString(), Locale.ENGLISH);
    return tmpMessageFormat.format(aParameters);
  }

  /**
   * This class should not be instantiated.
   */
  private Messages() {
    // nothing
  }
}