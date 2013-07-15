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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

/**
 * @author rbri
 */
public class BottomRightShadowBorder extends AbstractBorder {

  private static final long serialVersionUID = 2591247213491253371L;

  private static final Insets INSETS = new Insets(1, 1, 3, 3);

  @Override
  public Insets getBorderInsets(Component aComponent) {
    return INSETS;
  }

  @Override
  public void paintBorder(Component aComponent, Graphics aGraphics, int anX, int aY, int aWidth, int aHeight) {
    Color tmpShadowColor = UIManager.getColor("controlShadow");
    if (tmpShadowColor == null) {
      tmpShadowColor = Color.GRAY;
    }

    Color tmpLightShadowColor = new Color(tmpShadowColor.getRed(), tmpShadowColor.getGreen(), tmpShadowColor.getBlue(),
        170);
    Color tmpLighterShadowColor = new Color(tmpShadowColor.getRed(), tmpShadowColor.getGreen(), tmpShadowColor
        .getBlue(), 70);

    aGraphics.translate(anX, aY);

    // first a line around the whole box
    aGraphics.setColor(tmpShadowColor);
    aGraphics.fillRect(0, 0, aWidth - 3, 1);
    aGraphics.fillRect(0, 0, 1, aHeight - 3);
    aGraphics.fillRect(aWidth - 3, 1, 1, aHeight - 4);
    aGraphics.fillRect(1, aHeight - 3, aWidth - 4, 1);

    aGraphics.setColor(tmpLightShadowColor);
    // the last dots of the previous lines
    aGraphics.fillRect(aWidth - 3, aHeight - 3, 1, 1);
    aGraphics.fillRect(aWidth - 3, 0, 1, 1);
    aGraphics.fillRect(0, aHeight - 3, 1, 1);
    // next line
    aGraphics.fillRect(aWidth - 2, 1, 1, aHeight - 3);
    aGraphics.fillRect(1, aHeight - 2, aWidth - 3, 1);

    aGraphics.setColor(tmpLighterShadowColor);
    // the last dots of the previous lines
    aGraphics.fillRect(aWidth - 2, aHeight - 2, 1, 1);
    aGraphics.fillRect(aWidth - 2, 0, 1, 1);
    aGraphics.fillRect(0, aHeight - 2, 1, 1);
    // next line
    aGraphics.fillRect(aWidth - 1, 1, 1, aHeight - 2);
    aGraphics.fillRect(1, aHeight - 1, aWidth - 2, 1);

    aGraphics.translate(-anX, -aY);
  }
}
