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


package org.wetator.scriptcreator;

import java.util.List;

import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;

/**
 * The interface for all script writers.
 * 
 * @author tobwoerk
 */
public interface WetScriptCreator {

  /**
   * Creates a script from the previously set command list with the given fileName.
   * 
   * @throws WetException in case of errors
   */
  public void createScript() throws WetException;

  /**
   * Sets the file this script writer works on.
   * 
   * @param aCommandList the commands that should be in the script
   * @throws WetException in case of error
   */
  public void setCommands(List<WetCommand> aCommandList) throws WetException;

  /**
   * @param aFileName the fileName to set (name only expected, without extension)
   */
  public void setFileName(String aFileName);

  /**
   * @param anOutputDir the outputDir to set
   */
  public void setOutputDir(String anOutputDir);
}
