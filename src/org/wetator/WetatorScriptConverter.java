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


package org.wetator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JWindow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.exception.InvalidInputException;
import org.wetator.gui.DialogUtil;
import org.wetator.scriptcreator.IScriptCreator;
import org.wetator.scriptcreator.LegacyXMLScriptCreator;
import org.wetator.scriptcreator.XMLScriptCreator;
import org.wetator.scripter.ExcelScripter;
import org.wetator.scripter.LegacyXMLScripter;
import org.wetator.scripter.XMLScripter;

/**
 * The command line interface for converting test scripts.
 *
 * @author tobwoerk
 * @author frank.danek
 * @author rbri
 */
public final class WetatorScriptConverter {

  private static final Logger LOG = LogManager.getLogger(WetatorScriptConverter.class);

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
    System.out.println(Version.getFullProductName()); // NOPMD
    System.out.println("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " " // NOPMD
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    if (null == anArgsArray || anArgsArray.length < 3) {
      System.err.println("Parameters: <scripter> <script creator> <outputDir> (<dtd type> <dtd>)"); // NOPMD
      System.err.println("example1: xls xml /Users/me/tests"); // NOPMD
      System.err.println("example2: xls xml /Users/me/tests SYSTEM testcase.dtd"); // NOPMD
      System.err.println("example1: xls legacy_xml /Users/me/tests"); // NOPMD
      System.exit(1);
      return;
    }
    final String tmpScripterType = anArgsArray[0];
    final String tmpScriptCreatorType = anArgsArray[1];
    final String tmpOutputDir = anArgsArray[2];
    System.out.println("Starting converter using scripter '" + tmpScripterType + "', script creator '" // NOPMD
        + tmpScriptCreatorType + "' and output directory '" + tmpOutputDir + "'.");

    final WetatorScriptConverter tmpConverter = new WetatorScriptConverter();
    final JWindow tmpWindow = new JWindow();
    try {
      final Scripter tmpScripter = Scripter.valueOf(tmpScripterType.toUpperCase(Locale.ROOT));
      final IScripter tmpIScripter = tmpScripter.getScripter();
      final ScriptCreator tmpScriptCreator = ScriptCreator.valueOf(tmpScriptCreatorType.toUpperCase(Locale.ROOT));
      final IScriptCreator tmpCreator = tmpScriptCreator.getScriptCreator();
      tmpCreator.setOutputDir(tmpOutputDir);
      if (tmpCreator instanceof LegacyXMLScriptCreator && anArgsArray.length == 5) {
        final String tmpDtd = anArgsArray[3] + " \"" + anArgsArray[4] + "\"";
        LOG.info("Using DTD '" + tmpDtd + "'.");
        ((LegacyXMLScriptCreator) tmpCreator).setDtd(tmpDtd);
      }
      tmpConverter.setScripter(tmpIScripter);
      tmpConverter.setCreator(tmpCreator);
      final File[] tmpFiles = DialogUtil.chooseFiles(tmpWindow, null);
      if (null == tmpFiles || tmpFiles.length < 1) {
        System.exit(0);
      }

      for (final File tmpFile : tmpFiles) {
        tmpConverter.addTestFile(tmpFile);
      }

      System.out.println("Begin converting..."); // NOPMD
      tmpConverter.convert();
      System.out.println("Converting successfully completed."); // NOPMD
    } catch (final Exception e) {
      e.printStackTrace(); // NOPMD
      System.exit(1);
    } finally {
      tmpWindow.dispose();
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
   * @throws InvalidInputException in case of an invalid file
   */
  public void convert() throws InvalidInputException {
    for (final File tmpInputFile : inputFiles) {
      System.out.print("    Converting '" + tmpInputFile.getAbsolutePath() + "'..."); // NOPMD
      scripter.script(tmpInputFile);
      final List<Command> tmpCommands = scripter.getCommands();

      final String tmpFileName = tmpInputFile.getName().substring(0, tmpInputFile.getName().lastIndexOf('.'));
      creator.setFileName(tmpFileName);
      creator.setCommands(tmpCommands);
      creator.createScript();
      System.out.println(" done"); // NOPMD
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
   * @throws InvalidInputException
   *         if aFile does not exist
   */
  public void addTestFile(final File aFile) throws InvalidInputException {
    if (!aFile.exists()) {
      throw new InvalidInputException("The file '" + aFile.getAbsolutePath() + "' does not exist.");
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
    LEGACY_XML(new LegacyXMLScripter()),
    /**
     * XML.
     */
    XML(new XMLScripter()),
    /**
     * Excel.
     */
    XLS(new ExcelScripter());

    private IScripter scripter;

    /**
     * @param aIScripter the {@link IScripter} to use
     */
    Scripter(final IScripter aIScripter) {
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
    LEGACY_XML(new LegacyXMLScriptCreator()),
    /**
     * XML.
     */
    XML(new XMLScriptCreator());

    private IScriptCreator scriptCreator;

    /**
     * @param aIScriptCreator the {@link IScriptCreator} to use
     */
    ScriptCreator(final IScriptCreator aIScriptCreator) {
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
