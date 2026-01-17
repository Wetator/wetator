/*
 * Copyright (c) 2008-2025 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.gui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.wetator.exception.BackendException;

public class InputDialog {
  private JDialog dlgInput;
  private LayoutManager layout;
  private JLabel lblInput;
  private JTextField fldInput;
  private JButton btnSubmit;

  private String result;

  /**
   * Default constructor
   *
   * @throws BackendException if this is running in headless mode
   */
  public InputDialog() throws BackendException {
    super();
    if (GraphicsEnvironment.isHeadless()) {
      throw new BackendException("Using the input dialog is not possible in java headless mode.");
    }
  }

  /**
   * Shows the dialog and returns the input.
   *
   * @param aHint the label for the input field
   * @param aSecretInputFlag true if the input is secret
   * @return the value or null if cancelled
   * @throws BackendException in case of errors
   */
  public static String captureInput(final String aHint, final boolean aSecretInputFlag) throws BackendException {
    final InputDialog tmpHandler = new InputDialog();

    tmpHandler.dlgInput = new JDialog();
    tmpHandler.dlgInput.setModal(true);
    tmpHandler.dlgInput.setAlwaysOnTop(true);
    tmpHandler.layout = new GridBagLayout();
    tmpHandler.lblInput = new JLabel();
    if (aSecretInputFlag) {
      tmpHandler.fldInput = new JPasswordField();
    } else {
      tmpHandler.fldInput = new JTextField();
    }
    tmpHandler.btnSubmit = new JButton();

    tmpHandler.addWidgets();
    tmpHandler.handleInput(aHint);
    return tmpHandler.result;
  }

  /**
   * Sets layout of the controls
   */
  private void addWidgets() {
    dlgInput.getContentPane().setLayout(layout);
    final GridBagConstraints tmpGBC = new GridBagConstraints();
    tmpGBC.gridx = 0;
    tmpGBC.gridy = 0;
    tmpGBC.anchor = GridBagConstraints.WEST;
    tmpGBC.insets.left = 5;
    tmpGBC.insets.right = 5;
    tmpGBC.insets.bottom = 7;
    tmpGBC.insets.top = 15;
    dlgInput.getContentPane().add(lblInput, tmpGBC);
    tmpGBC.ipady = 7;
    tmpGBC.gridx = 1;
    tmpGBC.insets.bottom = 0;
    tmpGBC.insets.top = 0;
    tmpGBC.fill = GridBagConstraints.HORIZONTAL;
    tmpGBC.insets.top = 7;
    dlgInput.getContentPane().add(fldInput, tmpGBC);
    tmpGBC.insets.top = 7;
    tmpGBC.ipady = 2;
    tmpGBC.gridx = 0;
    tmpGBC.gridy = 1;
    tmpGBC.gridwidth = 2;
    tmpGBC.fill = GridBagConstraints.NONE;
    tmpGBC.anchor = GridBagConstraints.CENTER;
    tmpGBC.insets.bottom = 5;
    dlgInput.getContentPane().add(btnSubmit, tmpGBC);
  }

  /**
   * Handles ant input request
   *
   * @param aHint the label for the input field
   */
  public void handleInput(final String aHint) {
    addWidgets();
    setEventListeners();
    dlgInput.setTitle("Define variable");
    lblInput.setText(aHint);
    fldInput.setPreferredSize(lblInput.getPreferredSize());
    btnSubmit.setText("Submit");
    dlgInput.pack();
    dlgInput.setLocation(getDialogLocation().width, getDialogLocation().height);
    dlgInput.setVisible(true);
  }

  /**
   * Adds action listener
   */
  private void setEventListeners() {
    btnSubmit.addActionListener(anEvent -> {
      result = fldInput.getText();
      dlgInput.dispose();
    });
    fldInput.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(final KeyEvent anEvent) {
        if (anEvent.getKeyCode() == KeyEvent.VK_ENTER) {
          result = fldInput.getText();
          dlgInput.dispose();
        }
        if (anEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
          dlgInput.dispose();
        }
      }
    });
  }

  /**
   * Calculates the desired location of the dialog on the screen By default it
   * is in the center of the screen
   *
   * @return dialog location on the screen
   */
  private Dimension getDialogLocation() {
    final Dimension tmpScreenDim = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension tmpDlgInputDim = dlgInput.getSize();
    final int tmpDlgInputX = (int) ((tmpScreenDim.getWidth() - tmpDlgInputDim.getWidth()) / 2);
    final int tmpDlgInputY = (int) ((tmpScreenDim.getHeight() - tmpDlgInputDim.getHeight()) / 2);
    return new Dimension(tmpDlgInputX, tmpDlgInputY);
  }
}
