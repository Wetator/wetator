/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JWindow;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.core.WetatorEngine;
import org.wetator.gui.DialogUtil;
import org.wetator.progresslistener.StdOutProgressListener;
import org.wetator.util.Log4jUtil;

/**
 * The command line interface for the Wetator.
 *
 * @author rbri
 * @author tobwoerk
 * @author frank.danek
 */
public final class Wetator {

  private static final Logger LOG = LogManager.getLogger(Wetator.class);

  /**
   * The start point for the command line call.
   *
   * @param anArgsArray the command line arguments
   */
  public static void main(final String[] anArgsArray) {
    String tmpConfigFileName = null;
    String tmpVariablesFileName = null;
    File tmpDebugLogFile = null;
    boolean tmpAppendResults = false;
    final List<String> tmpFileNames = new LinkedList<>();

    // parse the command line
    for (int i = 0; i < anArgsArray.length; i++) {
      final String tmpArg = anArgsArray[i].trim();
      if ("-log".equals(tmpArg)) {
        tmpDebugLogFile = new File("wetator.log");
        Log4jUtil.configureDebugLogging(tmpDebugLogFile);
      } else if ("-append".equals(tmpArg)) {
        tmpAppendResults = true;
      } else if ("-p".equals(tmpArg) && i < (anArgsArray.length - 1)) {
        tmpConfigFileName = anArgsArray[i + 1];
        i++;
      } else if ("-var".equals(tmpArg) && i < (anArgsArray.length - 1)) {
        tmpVariablesFileName = anArgsArray[i + 1];
        i++;
      } else {
        tmpFileNames.add(tmpArg);
      }
    }

    LOG.info(Version.getFullProductName());
    LOG.info("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());
    if (null != tmpDebugLogFile) {
      LOG.info("    Debug log file: " + FilenameUtils.normalize(tmpDebugLogFile.getAbsolutePath()));
    }

    final StdOutProgressListener tmpProgressListener = new StdOutProgressListener();

    try {
      final WetatorEngine tmpWetatorEngine = new WetatorEngine();
      try {
        tmpWetatorEngine.addProgressListener(tmpProgressListener);

        if (null != tmpConfigFileName) {
          tmpWetatorEngine.setConfigFileName(tmpConfigFileName);
        }
        if (null != tmpVariablesFileName) {
          tmpWetatorEngine.setVariablesFileName(tmpVariablesFileName);
        }
        tmpWetatorEngine.init();

        // adjust config from cmd parameters
        if (tmpAppendResults) {
          if (tmpWetatorEngine.getConfiguration().isDistinctOutputEnabled()) {
            System.out.println(// NOPMD
                "Wetator execution failed because the 'append' command line flag can't be used togethere with the 'distinctOutput' setting.");
            LOG.fatal(
                "Wetator execution failed because the 'append' command line flag can't be used togethere with the 'distinctOutput' setting.");
            System.exit(1);
          }

          tmpWetatorEngine.getConfiguration().enableAppendResults();
        }
        if (null != tmpDebugLogFile) {
          tmpWetatorEngine.getConfiguration().enableDebugLogging();
        }

        if (tmpFileNames.isEmpty()) {
          String tmpPropertyKey = null;
          final File tmpConfigFile = tmpWetatorEngine.getConfigFile();
          if (null != tmpConfigFile) {
            tmpPropertyKey = Integer.toString(tmpConfigFile.getAbsolutePath().hashCode());
          }

          final JWindow tmpWindow = new JWindow();
          try {
            final File[] tmpFiles = DialogUtil.chooseFiles(tmpWindow, tmpPropertyKey);
            if (null == tmpFiles || tmpFiles.length < 1) {
              System.exit(0);
            }
            for (final File tmpFile : tmpFiles) {
              tmpWetatorEngine.addTestCase(tmpFile.getName(), tmpFile);
            }
          } finally {
            tmpWindow.dispose();
          }
        } else {
          final File tmpCurrentDir = new File(".");
          for (final String tmpFileName : tmpFileNames) {
            File tmpSearchFile = new File(tmpFileName);
            if (tmpSearchFile.isAbsolute()) {
              tmpWetatorEngine.addTestCase(tmpFileName, tmpSearchFile);
            } else {
              tmpSearchFile = new File(tmpCurrentDir, tmpFileName);
              if (tmpSearchFile.exists()) {
                tmpWetatorEngine.addTestCase(tmpFileName, tmpSearchFile);
              } else {
                final File tmpDir = tmpSearchFile.getParentFile();
                if (tmpDir != null && tmpDir.exists()) {
                  final FileFilter tmpFilter = new WildcardFileFilter(tmpSearchFile.getName());
                  final File[] tmpFiles = tmpDir.listFiles(tmpFilter);
                  if (tmpFiles != null) {
                    for (final File tmpFile : tmpFiles) {
                      tmpWetatorEngine.addTestCase(tmpFile.getName(), tmpFile);
                    }
                  }
                }
              }
            }
          }
        }

        tmpWetatorEngine.executeTests();
      } finally {
        tmpWetatorEngine.shutdown();
      }
      // SearchPattern.dumpStatistics();

      int tmpExitCode = 0;
      if (tmpProgressListener.getTestCountError() > 0 || tmpProgressListener.getTestCountFailure() > 0) {
        tmpExitCode = 1;
      }
      System.exit(tmpExitCode);

    } catch (final Throwable e) {
      System.out.println("Wetator execution failed: " + e.getMessage()); // NOPMD
      LOG.fatal("Wetator execution failed:", e);

      // System.exit is needed because we have started swing
      System.exit(1);
    }
  }

  /**
   * This class should not be instantiated.
   */
  private Wetator() {
    // nothing
  }
}
