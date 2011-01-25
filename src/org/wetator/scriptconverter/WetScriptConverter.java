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


package org.wetator.scriptconverter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;
import org.wetator.scriptcreator.WetScriptCreator;
import org.wetator.scripter.WetScripter;

/**
 * The converter for wetator test scripts. To use it set a scripter and a creator first.<br/>
 * Then - with addTestFile() - add the test files to convert and call convert() afterwards.
 * 
 * @author tobwoerk
 */
public class WetScriptConverter {

  private static final Log LOG = LogFactory.getLog(WetScriptConverter.class);

  private WetScripter scripter;
  private WetScriptCreator creator;

  private List<File> inputFiles;

  /**
   * The constructor.
   */
  public WetScriptConverter() {
    inputFiles = new LinkedList<File>();
  }

  /**
   * @throws WetException in case of errors
   */
  public void convert() throws WetException {
    for (File tmpInputFile : inputFiles) {
      LOG.trace("Converting '" + tmpInputFile.getAbsolutePath() + "'...");
      scripter.setFile(tmpInputFile);
      final List<WetCommand> tmpCommands = scripter.getCommands();

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
  public WetScripter getScripter() {
    return scripter;
  }

  /**
   * @param aScripter
   *        the scripter to set
   */
  public void setScripter(final WetScripter aScripter) {
    scripter = aScripter;
  }

  /**
   * @return the creator
   */
  public WetScriptCreator getCreator() {
    return creator;
  }

  /**
   * @param aCreator
   *        the creator to set
   */
  public void setCreator(final WetScriptCreator aCreator) {
    creator = aCreator;
  }

  /**
   * @param aFile
   *        the file to add
   * @throws WetException
   *         if aFile does not exist
   */
  public void addTestFile(final File aFile) throws WetException {
    if (!aFile.exists()) {
      throw new WetException("The file '" + aFile.getAbsolutePath() + "' does not exist.");
    }
    inputFiles.add(aFile);
  }
}
