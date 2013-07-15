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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * 
 * @author rbri
 */
public class PanelWithTitle extends JPanel {

    private static final long serialVersionUID = 5340038095512843981L;

    private JLabel titleLabel;
    private GradientPanel titleGradientPanel;
    private JPanel headerPanel;

    public PanelWithTitle(String aTitle, JToolBar aToolBar) {
        super(new BorderLayout());

        titleLabel = new JLabel(aTitle, JLabel.LEADING);
        titleLabel.setBackground(LookAndFeelUtil.getActiveTitleBackgroundColor());
        titleLabel.setForeground(LookAndFeelUtil.getActiveTitleForegroundColor());
        titleLabel.setOpaque(false);

        titleGradientPanel = new GradientPanel();
        titleGradientPanel.setLayout(new BorderLayout());
        titleGradientPanel.setBackground(new Color(44, 73, 135));
        titleGradientPanel.add(titleLabel, BorderLayout.WEST);
        titleGradientPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 1));

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleGradientPanel, BorderLayout.CENTER);
        // setToolBar(aToolBar);
        headerPanel.setBorder(new TopLeftRaisedBorder());
        headerPanel.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        setBorder(new BottomRightShadowBorder());
    }
}
