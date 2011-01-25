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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.exception.WetException;
import org.wetator.gui.DialogUtil;
import org.wetator.scriptconverter.WetScriptConverter;
import org.wetator.scriptcreator.ScriptCreator;
import org.wetator.scriptcreator.WetScriptCreator;
import org.wetator.scriptcreator.XmlScriptCreator;
import org.wetator.scripter.Scripter;
import org.wetator.scripter.WetScripter;

/**
 * The command line interface for converting test scripts.
 * 
 * @author tobwoerk
 */
public final class WetatorScriptConverter {

  private static final Log LOG = LogFactory.getLog(WetatorScriptConverter.class);;

  /**
   * The start point for the command line call.
   * 
   * @param anArgsArray
   *        the command line arguments
   */
  public static void main(final String[] anArgsArray) {
    LOG.info(Version.getFullProductName());
    LOG.info("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    if (null == anArgsArray || anArgsArray.length < 3) {
      System.err.println("Parameters: <scripter> <script creator> <outputDir> (<dtd type> <dtd>)");
      System.err.println("example1: xls xml /Users/me/tests");
      System.err.println("example2: xls xml /Users/me/tests SYSTEM testcase.dtd");
      System.exit(1);
      return;
    }
    final String tmpScripterType = anArgsArray[0];
    final String tmpScriptCreatorType = anArgsArray[1];
    final String tmpOutputDir = anArgsArray[2];
    LOG.info("Starting converter using scripter '" + tmpScripterType + "', script creator '" + tmpScriptCreatorType
        + " and output directory '" + tmpOutputDir + "'.");

    final WetScriptConverter tmpConverter = new WetScriptConverter();
    try {
      final Scripter tmpScripter = Scripter.valueOf(tmpScripterType.toUpperCase());
      final WetScripter tmpWetScripter = tmpScripter.getWetScripter();
      final ScriptCreator tmpScriptCreator = ScriptCreator.valueOf(tmpScriptCreatorType.toUpperCase());
      final WetScriptCreator tmpCreator = tmpScriptCreator.getWetScriptCreator();
      tmpCreator.setOutputDir(tmpOutputDir);
      if (tmpCreator instanceof XmlScriptCreator && anArgsArray.length == 5) {
        final String tmpDtd = anArgsArray[3] + " \"" + anArgsArray[4] + "\"";
        LOG.info("Using DTD '" + tmpDtd + "'.");
        ((XmlScriptCreator) tmpCreator).setDtd(tmpDtd);
      }
      tmpConverter.setScripter(tmpWetScripter);
      tmpConverter.setCreator(tmpCreator);
      final File[] tmpFiles = DialogUtil.chooseFiles();
      if (null == tmpFiles || (tmpFiles.length < 1)) {
        return;
      }

      for (int i = 0; i < tmpFiles.length; i++) {
        tmpConverter.addTestFile(tmpFiles[i]);
      }

      LOG.info("Begin converting...");
      tmpConverter.convert();
      LOG.info("Converting successfully completed.");
    } catch (final WetException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.exit(0);
  }

  /**
   * This class should not be instantiated.
   */
  private WetatorScriptConverter() {
    // nothing
  }
}
