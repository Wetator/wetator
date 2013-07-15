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


package org.rbri.wet.gui;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.rbri.wet.Wetator;
import org.rbri.wet.i18n.Messages;

/**
 * Utility class to display a file selector dialog.
 * 
 * @author rbri
 */
public final class DialogUtil {

  private static final String LAST_DIR = "lastDir";

  /**
   * Helper for displaying a file selector dialog
   * 
   * @return the files
   */
  public static File chooseFile() {
    File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(false);
    if (null == tmpResult) {
      return null;
    }
    return tmpResult[0];
  }

  /**
   * Helper for displaying a file selector dialog
   * 
   * @return the files
   */
  public static File[] chooseFiles() {
    File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(true);

    return tmpResult;
  }

  /**
   * Displays a file selector dialog using swing.
   * 
   * @param aMultiSelectionFlag if true multiple files can be selected.
   * @return the selected files.
   */
  protected static File[] chooseFilesSwing(boolean aMultiSelectionFlag) {
    Preferences tmpPreferences = Preferences.userNodeForPackage(Wetator.class);
    String tmpLastDirName = tmpPreferences.get(LAST_DIR, "");

    File tmpLastDir = new File(tmpLastDirName);
    if (!tmpLastDir.exists() || !tmpLastDir.isDirectory()) {
      tmpLastDir = null;
    }

    JFileChooser tmpFileChooser = new JFileChooser();
    tmpFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    tmpFileChooser.setMultiSelectionEnabled(aMultiSelectionFlag);
    tmpFileChooser.setDialogTitle(Messages.getMessage("fileChooserTitle", null));
    tmpFileChooser.setCurrentDirectory(tmpLastDir);

    // TODO File[] tmpSelFiles = new File[1];
    // tmpSelFiles[0] = new File("open_url.xls");
    // tmpFileChooser.setSelectedFiles(tmpSelFiles);

    int tmpChooserAction = tmpFileChooser.showOpenDialog(null);

    switch (tmpChooserAction) {
      case JFileChooser.APPROVE_OPTION:
        if (aMultiSelectionFlag) {
          File[] tmpSelectedFiles = tmpFileChooser.getSelectedFiles();

          if (tmpSelectedFiles.length > 0) {
            tmpPreferences.put(LAST_DIR, tmpSelectedFiles[0].getParentFile().getAbsolutePath());
          }

          return tmpSelectedFiles;
        }
        File tmpSelectedFile = tmpFileChooser.getSelectedFile();
        if (null == tmpSelectedFile) {
          return null;
        }
        return new File[] { tmpSelectedFile };
      case JFileChooser.CANCEL_OPTION:
        return null;
      default:
        return null;
    }
  }

  /**
   * This class should not be instantiated.
   */
  private DialogUtil() {
    // nothing
  }
}