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
import org.wetator.core.Command;
import org.wetator.exception.WetatorException;
import org.wetator.gui.DialogUtil;
import org.wetator.scriptcreator.IScriptCreator;
import org.wetator.scriptcreator.LegacyXmlScriptCreator;
import org.wetator.scriptcreator.XmlScriptCreator;
import org.wetator.scripter.ExcelScripter;
import org.wetator.scripter.IScripter;
import org.wetator.scripter.LegacyXmlScripter;
import org.wetator.scripter.XmlScripter;

/**
 * The command line interface for converting test scripts.
 * 
 * @author tobwoerk
 * @author frank.danek
 */
public final class WetatorScriptConverter {

  private static final Log LOG = LogFactory.getLog(WetatorScriptConverter.class);;

  private IScripter scripter;
  private IScriptCreator creator;

  private List<File> inputFiles;

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

    final WetatorScriptConverter tmpConverter = new WetatorScriptConverter();
    try {
      final Scripter tmpScripter = Scripter.valueOf(tmpScripterType.toUpperCase());
      final IScripter tmpIScripter = tmpScripter.getScripter();
      final ScriptCreator tmpScriptCreator = ScriptCreator.valueOf(tmpScriptCreatorType.toUpperCase());
      final IScriptCreator tmpCreator = tmpScriptCreator.getScriptCreator();
      tmpCreator.setOutputDir(tmpOutputDir);
      if (tmpCreator instanceof LegacyXmlScriptCreator && anArgsArray.length == 5) {
        final String tmpDtd = anArgsArray[3] + " \"" + anArgsArray[4] + "\"";
        LOG.info("Using DTD '" + tmpDtd + "'.");
        ((LegacyXmlScriptCreator) tmpCreator).setDtd(tmpDtd);
      }
      tmpConverter.setScripter(tmpIScripter);
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
    } catch (final WetatorException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.exit(0);
  }

  /**
   * The constructor.
   */
  private WetatorScriptConverter() {
    inputFiles = new LinkedList<File>();
  }

  /**
   * @throws WetatorException in case of errors
   */
  public void convert() throws WetatorException {
    for (File tmpInputFile : inputFiles) {
      LOG.trace("Converting '" + tmpInputFile.getAbsolutePath() + "'...");
      scripter.setFile(tmpInputFile);
      final List<Command> tmpCommands = scripter.getCommands();

      final String tmpFileName = tmpInputFile.getName().substring(0, tmpInputFile.getName().lastIndexOf('.'));
      creator.setFileName(tmpFileName);
      creator.setCommands(tmpCommands);
      creator.createScript();
      LOG.trace("Converted '" + tmpInputFile.getAbsolutePath() + "'...");
    }
  }

  /**
   * @return the scripter
   */
  public IScripter getScripter() {
    return scripter;
  }

  /**
   * @param aScripter
   *        the scripter to set
   */
  public void setScripter(final IScripter aScripter) {
    scripter = aScripter;
  }

  /**
   * @return the creator
   */
  public IScriptCreator getCreator() {
    return creator;
  }

  /**
   * @param aCreator
   *        the creator to set
   */
  public void setCreator(final IScriptCreator aCreator) {
    creator = aCreator;
  }

  /**
   * @param aFile
   *        the file to add
   * @throws WetatorException
   *         if aFile does not exist
   */
  public void addTestFile(final File aFile) throws WetatorException {
    if (!aFile.exists()) {
      throw new WetatorException("The file '" + aFile.getAbsolutePath() + "' does not exist.");
    }
    inputFiles.add(aFile);
  }

  /**
   * Scripter enum.
   * 
   * @author tobwoerk
   */
  public enum Scripter {
    /**
     * Legacy XML.
     */
    LEGACY_XML(new LegacyXmlScripter()),
    /**
     * XML.
     */
    XML(new XmlScripter()),
    /**
     * Excel.
     */
    XLS(new ExcelScripter());

    private IScripter scripter;

    private Scripter(final IScripter aIScripter) {
      scripter = aIScripter;
    }

    /**
     * @return the scripter
     */
    public IScripter getScripter() {
      return scripter;
    }
  }

  /**
   * Script creator enum.
   * 
   * @author tobwoerk
   */
  public enum ScriptCreator {
    /**
     * Legacy XML.
     */
    LEGACY_XML(new LegacyXmlScriptCreator()),
    /**
     * XML.
     */
    XML(new XmlScriptCreator());

    private IScriptCreator scriptCreator;

    private ScriptCreator(final IScriptCreator aIScriptCreator) {
      scriptCreator = aIScriptCreator;
    }

    /**
     * @return the script creator
     */
    public IScriptCreator getScriptCreator() {
      return scriptCreator;
    }
  }
}
