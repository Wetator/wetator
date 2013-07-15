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


package org.wetator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.core.WetatorEngine;
import org.wetator.core.IProgressListener;
import org.wetator.exception.WetatorException;
import org.wetator.gui.DialogUtil;
import org.wetator.progresslistener.StdOutProgressListener;

/**
 * The command line interface for the Wetator.
 * 
 * @author rbri
 */
public final class Wetator {

  private static final Log LOG = LogFactory.getLog(Wetator.class);

  /**
   * The start point for the command line call.
   * 
   * @param anArgsArray the command line arguments
   */
  public static void main(final String[] anArgsArray) {
    LOG.info(Version.getFullProductName());
    LOG.info("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    final IProgressListener tmpProgressListener = new StdOutProgressListener();

    String tmpConfigFileName = null;
    final List<String> tmpFileNames = new LinkedList<String>();
    // parse the command line
    for (int i = 0; i < anArgsArray.length; i++) {
      final String tmpArg = anArgsArray[i].trim();
      if ("-p".equals(tmpArg) && i < (anArgsArray.length - 1)) {
        tmpConfigFileName = anArgsArray[i + 1];
        i++;
      } else {
        tmpFileNames.add(tmpArg);
      }
    }

    WetatorEngine tmpWetatorEngine;
    try {
      tmpWetatorEngine = new WetatorEngine();
      tmpWetatorEngine.addProgressListener(tmpProgressListener);

      if (null != tmpConfigFileName) {
        tmpWetatorEngine.setConfigFileName(tmpConfigFileName);
      }
      tmpWetatorEngine.init();

      if (tmpFileNames.isEmpty()) {
        final File[] tmpFiles = DialogUtil.chooseFiles();
        if (null == tmpFiles || (tmpFiles.length < 1)) {
          return;
        }

        for (int i = 0; i < tmpFiles.length; i++) {
          tmpWetatorEngine.addTestFile(tmpFiles[i]);
        }
      } else {
        for (String tmpFileName : tmpFileNames) {
          tmpWetatorEngine.addTestFile(new File(tmpFileName));
        }
      }

      tmpWetatorEngine.executeTests();
      // SearchPattern.dumpStatistics();
    } catch (final WetatorException e) {
      System.out.println("Wetator execution failed: " + e.getMessage());
      LOG.warn("Wetator execution failed:", e);
      // System.exit is needed because we have started swing
      System.exit(1);
    } catch (final Throwable e) {
      System.out.println("Wetator execution failed: " + e.getMessage());
      LOG.fatal("Wetator execution failed:", e);
      // System.exit is needed because we have started swing
      System.exit(1);
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
