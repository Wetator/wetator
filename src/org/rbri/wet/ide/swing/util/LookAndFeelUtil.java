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


package org.rbri.wet.ide.swing.util;

import java.awt.Color;
import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * 
 * @author rbri
 */
public class LookAndFeelUtil {

    public static Color getActiveTitleForegroundColor() {
        return UIManager.getColor("InternalFrame.activeTitleForeground");
    }

    public static Color getActiveTitleBackgroundColor() {
        return UIManager.getColor("InternalFrame.activeTitleBackground");
    }

    public static Color getControlColor() {
        return UIManager.getColor("control");
    }

    public static Color getControlLtHighlightColor() {
        return UIManager.getColor("controlLtHighlight");
    }

    public static Color getControlShadowColor() {
        return UIManager.getColor("controlShadow");
    }

    public static void main(String[] anArgsArray) {
        UIDefaults tmpUIDefaults = UIManager.getLookAndFeelDefaults();
        Enumeration<Object> tmpKeys = tmpUIDefaults.keys();
        while (tmpKeys.hasMoreElements()) {
            Object tmpKey = tmpKeys.nextElement();
            Object tmpValue = tmpUIDefaults.get(tmpKey);
            if (null != tmpValue) {
                System.out.println(tmpKey.getClass() + " " + tmpValue.getClass());
            }
            System.out.println(tmpKey + " " + tmpValue);
        }
    }
}
