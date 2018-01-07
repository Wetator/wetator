/*
 * Copyright (c) 2008-2018 wetator.org
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

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JWindow;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.Wetator;
import org.wetator.i18n.Messages;
import org.wetator.util.StringUtil;

/**
 * Utility class to display a file selector dialog.
 *
 * @author rbri
 * @author frank.danek
 */
public final class DialogUtil {
  private static final Logger LOG = LogManager.getLogger(DialogUtil.class);

  private static final char FILE_SEPARATOR = ';';
  private static final String LAST_DIR = "lastDir";
  private static final String LAST_FILES = "lastFiles";
  private static final String LAST_X = "lastX";
  private static final String LAST_Y = "lastY";
  private static final String LAST_WIDTH = "lastWidth";
  private static final String LAST_HEIGHT = "lastHeight";

  /**
   * Helper for displaying a file selector dialog.
   *
   * @param aWindow the root JWindow (to see something in the window
   *        switch list (Atl+Tab)
   * @param aPropertyKey a special key for looking up the start directory.
   * @return the files
   */
  public static File chooseFile(final JWindow aWindow, final String aPropertyKey) {
    final File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(aWindow, aPropertyKey, false);
    if (null == tmpResult) {
      return null;
    }
    return tmpResult[0];
  }

  /**
   * Helper for displaying a file selector dialog.
   *
   * @param aWindow the root JWindow (to see something in the window
   *        switch list (Atl+Tab)
   * @param aPropertyKey a special key for looking up the start directory.
   * @return the files
   */
  public static File[] chooseFiles(final JWindow aWindow, final String aPropertyKey) {
    final File[] tmpResult;
    // we can make this configurable later
    tmpResult = chooseFilesSwing(aWindow, aPropertyKey, true);

    return tmpResult;
  }

  /**
   * Displays a file selector dialog using swing.
   *
   * @param aWindow the root JWindow (to see something in the window
   *        switch list (Atl+Tab)
   * @param aPropertyKey a special key for looking up the start directory.
   * @param aMultiSelectionFlag if true multiple files can be selected.
   * @return the selected files.
   */
  private static File[] chooseFilesSwing(final JWindow aWindow, final String aPropertyKey,
      final boolean aMultiSelectionFlag) {
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

    final LookAndFeel tmpCurrentLook = UIManager.getLookAndFeel();
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final Exception e) { // NOPMD
      // ignore, fall back to the default
    }

    final JFileChooser tmpFileChooser = new PlaceableFileChooser(tmpPreferences);
    tmpFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    tmpFileChooser.setMultiSelectionEnabled(aMultiSelectionFlag);
    tmpFileChooser.setDialogTitle(Messages.getMessage("fileChooserTitle"));
    tmpFileChooser.setCurrentDirectory(tmpLastDir);

    final File[] tmpOldFiles = restoreFiles(tmpPreferences, tmpLastDir);
    try {
      tmpFileChooser.setSelectedFiles(tmpOldFiles);
    } catch (final Exception e) {
      // seems to happen sometimes
      LOG.error(e.getMessage(), e);
    }

    try {
      // reset
      UIManager.setLookAndFeel(tmpCurrentLook);
    } catch (final Exception e) { // NOPMD
      // ignore
    }

    final int tmpChooserAction = tmpFileChooser.showOpenDialog(aWindow);
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

          storeFiles(tmpPreferences, tmpSelectedFiles);
          return tmpSelectedFiles;
        }
        final File tmpSelectedFile = tmpFileChooser.getSelectedFile();
        if (null == tmpSelectedFile) {
          return null;
        }
        storeFiles(tmpPreferences, new File[] { tmpSelectedFile });
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

  /**
   * Helper subclass of JFileChooser to make the dialog placeable.
   */
  private static class PlaceableFileChooser extends JFileChooser {
    private static final long serialVersionUID = 6223316475899932034L;

    private final Preferences preferences;
    private JDialog jDialog;

    /**
     * Constructor.
     *
     * @param aPreferences the preferences for read/write
     */
    PlaceableFileChooser(final Preferences aPreferences) {
      preferences = aPreferences;
    }

    @Override
    protected JDialog createDialog(final Component aParent) throws HeadlessException {
      jDialog = super.createDialog(aParent);
      restoreOptions(preferences, jDialog);
      return jDialog;
    }

    @Override
    public int showOpenDialog(final Component aParent) throws HeadlessException {
      final int tmpResult = super.showOpenDialog(aParent);
      storeOptions(preferences, jDialog);
      return tmpResult;
    }
  }

  /**
   * Store frame location and size.
   *
   * @param aPreferences the preferences to write to
   * @param aDialog the dialog you like to store
   */
  public static void storeOptions(final Preferences aPreferences, final JDialog aDialog) {
    if (null == aDialog) {
      return;
    }

    final Rectangle tmpBounds = aDialog.getBounds();

    aPreferences.put(LAST_X, Integer.toString((int) tmpBounds.getX()));
    aPreferences.put(LAST_Y, Integer.toString((int) tmpBounds.getY()));
    aPreferences.put(LAST_WIDTH, Integer.toString((int) tmpBounds.getWidth()));
    aPreferences.put(LAST_HEIGHT, Integer.toString((int) tmpBounds.getHeight()));
  }

  /**
   * Restore frame location and size.
   *
   * @param aPreferences the preferences to write to
   * @param aDialog the dialog you like to store
   */
  public static void restoreOptions(final Preferences aPreferences, final JDialog aDialog) {

    final int tmpX = Integer.parseInt(aPreferences.get(LAST_X, "-1"));
    final int tmpY = Integer.parseInt(aPreferences.get(LAST_Y, "-1"));
    final int tmpWidth = Integer.parseInt(aPreferences.get(LAST_WIDTH, "-1"));
    final int tmpHeight = Integer.parseInt(aPreferences.get(LAST_HEIGHT, "-1"));

    final Rectangle tmpBounds = new Rectangle(tmpX, tmpY, tmpWidth, tmpHeight);

    final GraphicsEnvironment tmpGEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice[] tmpDevices = tmpGEnv.getScreenDevices();
    for (final GraphicsDevice tmpGraphicsDevice : tmpDevices) {
      final GraphicsConfiguration[] tmpConfigs = tmpGraphicsDevice.getConfigurations();
      for (final GraphicsConfiguration tmpGraphicsConfiguration : tmpConfigs) {
        final Rectangle2D tmpVisibleRect = tmpGraphicsConfiguration.getBounds().createIntersection(tmpBounds);
        if (tmpVisibleRect.getHeight() > 100 && tmpVisibleRect.getWidth() > 100) {
          aDialog.setBounds(tmpBounds);
          return;
        }
      }
    }
  }

  /**
   * Store frame location and size.
   *
   * @param aPreferences the preferences to write to
   * @param aFiles the files to store
   */
  public static void storeFiles(final Preferences aPreferences, final File... aFiles) {
    if (null == aFiles) {
      return;
    }

    final StringBuilder tmpFiles = new StringBuilder();
    for (final File tmpFile : aFiles) {
      tmpFiles.append(tmpFile.getAbsolutePath());
      tmpFiles.append(FILE_SEPARATOR);
    }

    final List<String> tmpParts = StringUtil.split(tmpFiles.toString(), Preferences.MAX_VALUE_LENGTH);
    aPreferences.put(LAST_FILES, Integer.toString(tmpParts.size()));
    for (int i = 0; i < tmpParts.size(); i++) {
      aPreferences.put(LAST_FILES + i, tmpParts.get(i));
    }
  }

  /**
   * Restore frame location and size.
   *
   * @param aPreferences the preferences to write to
   * @param aDir the the directory we are working on
   * @return the list of remembered files that are still available
   */
  public static File[] restoreFiles(final Preferences aPreferences, final File aDir) {
    if (aDir == null) {
      return new File[0];
    }

    final StringBuilder tmpLastFilesBuilder = new StringBuilder();
    int tmpPartsCount = 1;
    try {
      tmpPartsCount = Integer.parseInt(aPreferences.get(LAST_FILES, "1"));
    } catch (final NumberFormatException e) { // NOPMD
      // ignore
    }

    for (int i = 0; i < tmpPartsCount; i++) {
      tmpLastFilesBuilder.append(aPreferences.get(LAST_FILES + i, ""));
    }
    final String[] tmpFiles = StringUtils.split(tmpLastFilesBuilder.toString(), FILE_SEPARATOR);

    final List<File> tmpResult = new LinkedList<File>();
    final String tmpCurrentDir = aDir.getAbsolutePath();
    for (final String tmpString : tmpFiles) {
      final File tmpFile = new File(tmpString);
      if (tmpFile.exists() && tmpFile.getAbsolutePath().startsWith(tmpCurrentDir)) {
        tmpResult.add(tmpFile);
      }
    }
    return tmpResult.toArray(new File[0]);
  }
}