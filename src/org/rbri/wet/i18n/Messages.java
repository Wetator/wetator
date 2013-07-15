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


package org.rbri.wet.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;



/**
 * This class provides all messages that are used by WeT.
 * 
 * @author rbri
 */
public final class Messages {

    public static final String getMessage(String aMessageKey, Object[] aParameterArray) {
        // TODO move the messages file to the root level
        ResourceBundle tmpMessages = ResourceBundle.getBundle("org.rbri.wet.Messages");
        
        String tmpMessageResource;
        try {
            tmpMessageResource = tmpMessages.getString(aMessageKey);
        } catch (MissingResourceException e) {
            tmpMessageResource = "Unknown message key ''" + aMessageKey + "''";
            if ((null != aParameterArray) && (aParameterArray.length > 0)) {
                tmpMessageResource = tmpMessageResource + " (param(s): ";
                for (int i = 0; i < aParameterArray.length; i++) {
                    tmpMessageResource = tmpMessageResource + " ''{" + i + "}''"; 
                }
                tmpMessageResource = tmpMessageResource + ")";
            }
            tmpMessageResource = tmpMessageResource + ".";
        }
        MessageFormat tmpMessageFormat = new MessageFormat(tmpMessageResource);
        String tmpResult = tmpMessageFormat.format(aParameterArray);
        
        return tmpResult;
    }
}