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

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * 
 * @author rbri
 */
public class FlatSplitPane extends JSplitPane {

    private static final long serialVersionUID = 5819956633477127079L;

    public FlatSplitPane(int anOrientation, boolean aContinuousLayoutFlag) {
        super(anOrientation, aContinuousLayoutFlag);

        this.setBorder(BorderFactory.createEmptyBorder());

        SplitPaneUI tmpSplitPaneUI = this.getUI();
        if (tmpSplitPaneUI instanceof BasicSplitPaneUI) {
            BasicSplitPaneUI basicUI = (BasicSplitPaneUI) tmpSplitPaneUI;
            basicUI.getDivider().setBorder(BorderFactory.createEmptyBorder());
        }
    }

}
