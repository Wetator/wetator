/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.gui;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;
import org.wetator.Wetator;
import org.wetator.i18n.Messages;

/**
 * Utility class to display a file selector dialog.
 * 
 * @author rbri
 */
public final class DialogUtil {

  private static final String LAST_DIR = "lastDir";

  /**
   * Helper for displaying a file selector dialog.
   * 
   * @param aPropertyKey a special key for looking up the start directory.
   * @return the files
   */
  public static File chooseFile(final String aPropertyKey) {
    File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(aPropertyKey, false);
    if (null == tmpResult) {
      return null;
    }
    return tmpResult[0];
  }

  /**
   * Helper for displaying a file selector dialog.
   * 
   * @param aPropertyKey a special key for looking up the start directory.
   * @return the files
   */
  public static File[] chooseFiles(final String aPropertyKey) {
    File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(aPropertyKey, true);

    return tmpResult;
  }

  /**
   * Displays a file selector dialog using swing.
   * 
   * @param aPropertyKey a special key for looking up the start directory.
   * @param aMultiSelectionFlag if true multiple files can be selected.
   * @return the selected files.
   */
  protected static File[] chooseFilesSwing(final String aPropertyKey, final boolean aMultiSelectionFlag) {
    final Preferences tmpPreferences = Preferences.userNodeForPackage(Wetator.class);

    String tmpLastDirName = null;

    if (null != aPropertyKey) {
      tmpLastDirName = tmpPreferences.get(LAST_DIR + "_" + aPropertyKey, "");
      final File tmpLastDir = new File(tmpLastDirName);
      if (!tmpLastDir.exists() || !tmpLastDir.isDirectory()) {
        tmpLastDirName = null;
      }
    }

    if (StringUtils.isBlank(tmpLastDirName)) {
      tmpLastDirName = tmpPreferences.get(LAST_DIR, "");
    }

    File tmpLastDir = new File(tmpLastDirName);
    if (!tmpLastDir.exists() || !tmpLastDir.isDirectory()) {
      tmpLastDir = null;
    }

    final JFileChooser tmpFileChooser = new JFileChooser();
    tmpFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    tmpFileChooser.setMultiSelectionEnabled(aMultiSelectionFlag);
    tmpFileChooser.setDialogTitle(Messages.getMessage("fileChooserTitle", null));
    tmpFileChooser.setCurrentDirectory(tmpLastDir);

    final int tmpChooserAction = tmpFileChooser.showOpenDialog(null);

    switch (tmpChooserAction) {
      case JFileChooser.APPROVE_OPTION:
        if (aMultiSelectionFlag) {
          final File[] tmpSelectedFiles = tmpFileChooser.getSelectedFiles();

          if (tmpSelectedFiles.length > 0) {
            tmpPreferences.put(LAST_DIR, tmpSelectedFiles[0].getParentFile().getAbsolutePath());
            if (null != aPropertyKey) {
              tmpPreferences.put(LAST_DIR + "_" + aPropertyKey, tmpSelectedFiles[0].getParentFile().getAbsolutePath());
            }
          }

          return tmpSelectedFiles;
        }
        final File tmpSelectedFile = tmpFileChooser.getSelectedFile();
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