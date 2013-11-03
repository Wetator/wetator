/*
 * Copyright (c) 2008-2013 wetator.org
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
import java.util.LinkedList;
import java.util.List;

import javax.swing.JWindow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wetator.core.IProgressListener;
import org.wetator.core.WetatorEngine;
import org.wetator.gui.DialogUtil;
import org.wetator.progresslistener.StdOutProgressListener;

/**
 * The command line interface for the Wetator.
 * 
 * @author rbri
 * @author tobwoerk
 * @author frank.danek
 */
public final class Wetator {

  private static final Log LOG = LogFactory.getLog(Wetator.class);

  /**
   * The start point for the command line call.
   * 
   * @param anArgsArray the command line arguments
   */
  public static void main(final String[] anArgsArray) {

    String tmpConfigFileName = null;
    File tmpLogFile = null;
    final List<String> tmpFileNames = new LinkedList<String>();
    // parse the command line
    for (int i = 0; i < anArgsArray.length; i++) {
      final String tmpArg = anArgsArray[i].trim();
      if ("-log".equals(tmpArg)) {
        final FileAppender tmpFileAppender = new FileAppender();
        tmpFileAppender.setName("w_file");
        tmpLogFile = new File("wetator.log");
        tmpFileAppender.setFile(tmpLogFile.getAbsolutePath());
        tmpFileAppender.setLayout(new PatternLayout("%5p [%5.5t] (%25.25F:%5.5L) - %m%n"));
        tmpFileAppender.setAppend(false);
        tmpFileAppender.activateOptions();
        Logger.getRootLogger().addAppender(tmpFileAppender);

        for (String tmpLog : new String[] { "org.wetator", "com.gargoylesoftware.htmlunit.javascript.DebugFrameImpl",
            "com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine", "org.apache.http.wire" }) {
          final Logger tmpLogger = LogManager.getLogger(tmpLog);
          tmpLogger.setLevel(Level.TRACE);
        }
      } else if ("-p".equals(tmpArg) && i < (anArgsArray.length - 1)) {
        tmpConfigFileName = anArgsArray[i + 1];
        i++;
      } else {
        tmpFileNames.add(tmpArg);
      }
    }

    LOG.info(Version.getFullProductName());
    LOG.info("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());
    if (null != tmpLogFile) {
      LOG.info("    Log file: " + tmpLogFile.getAbsolutePath());
    }

    final IProgressListener tmpProgressListener = new StdOutProgressListener();

    final JWindow tmpWindow = new JWindow();
    try {
      final WetatorEngine tmpWetatorEngine = new WetatorEngine();
      tmpWetatorEngine.addProgressListener(tmpProgressListener);

      if (null != tmpConfigFileName) {
        tmpWetatorEngine.setConfigFileName(tmpConfigFileName);
      }
      tmpWetatorEngine.init();
      if (null != tmpLogFile) {
        tmpWetatorEngine.getConfiguration().enableLog();
      }

      if (tmpFileNames.isEmpty()) {
        String tmpPropertyKey = null;
        final File tmpConfigFile = tmpWetatorEngine.getConfigFile();
        if (null != tmpConfigFile) {
          tmpPropertyKey = Integer.toString(tmpConfigFile.getAbsolutePath().hashCode());
        }
        final File[] tmpFiles = DialogUtil.chooseFiles(tmpWindow, tmpPropertyKey);
        if (null == tmpFiles || tmpFiles.length < 1) {
          System.exit(0);
        }

        for (int i = 0; i < tmpFiles.length; i++) {
          final File tmpFile = tmpFiles[i];
          tmpWetatorEngine.addTestCase(tmpFile.getName(), tmpFile);
        }
      } else {
        for (final String tmpFileName : tmpFileNames) {
          final File tmpFile = new File(tmpFileName);
          tmpWetatorEngine.addTestCase(tmpFileName, tmpFile);
        }
      }

      tmpWetatorEngine.executeTests();
      // SearchPattern.dumpStatistics();
    } catch (final Throwable e) {
      System.out.println("Wetator execution failed: " + e.getMessage());
      LOG.fatal("Wetator execution failed:", e);
      // System.exit is needed because we have started swing
      System.exit(1);
    } finally {
      tmpWindow.dispose();
    }
    System.exit(0);
  }

  /**
   * This class should not be instantiated.
   */
  private Wetator() {
    // nothing
  }
}
