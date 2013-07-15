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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * 
 * @author rbri
 */
public class GradientPanel extends JPanel {

    private static final long serialVersionUID = -4051448566901726204L;
    private Direction gradientDirection;

    public enum Direction { LEFT2RIGHT, BOTTOM2TOP };


    public GradientPanel() {
        super();
    }

    public void paintComponent(Graphics aGraphics) {
        super.paintComponent(aGraphics);
        if (!isOpaque()) {
            return;
        }

        Graphics2D tmpGraphics2D = (Graphics2D) aGraphics;
        Color tmpControlColor = UIManager.getColor("control");
        int tmpWidth = getWidth();
        int tmpHeight = getHeight();

        // store original paint for restore later
        Paint tmpOrgPaint = tmpGraphics2D.getPaint();

        GradientPaint tmpGradientPaint;
        Direction tmpDirection = getGradientDirection();
        if (null == tmpDirection) {
            tmpDirection = Direction.LEFT2RIGHT;
        }
        switch (tmpDirection) {
        case BOTTOM2TOP:
            tmpGradientPaint = new GradientPaint(0.0F, tmpHeight, getBackground(), 0.0F, 0.0F, tmpControlColor);
            break;
        default:
            // LEFT2RIGHT is default
            tmpGradientPaint = new GradientPaint(0.0F, 0.0F, getBackground(), tmpWidth, 0.0F, tmpControlColor);
        }

        tmpGraphics2D.setPaint(tmpGradientPaint);
        tmpGraphics2D.fillRect(0, 0, tmpWidth, tmpHeight);
        tmpGraphics2D.setPaint(tmpOrgPaint);
    }


    public Direction getGradientDirection() {
        return gradientDirection;
    }


    public void setGradientDirection(Direction aDirection) {
        gradientDirection = aDirection;
    }

}
