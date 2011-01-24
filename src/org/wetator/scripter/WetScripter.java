/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.scripter;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;

/**
 * The interface for scripters.
 * Scripters are responsible for reading an input file
 * and parsing the commands.<br>
 * Sripters are reused for many files. The flow is:
 * <ol>
 * <li>isSupported()
 * <li>setFile()
 * <li>getCommands()
 * <ol>
 * 
 * @author rbri
 */
public interface WetScripter {

  /**
   * Sets the file this scripter works on.
   * Also this method must read the whole list of commands.
   * 
   * @param aFile the file
   * @throws WetException in case of error
   */
  public void setFile(File aFile) throws WetException;

  /**
   * Returns true, if this scripter is able to handle this
   * file.
   * 
   * @param aFile the file to check
   * @return true or false
   */
  public boolean isSupported(File aFile);

  /**
   * Returns the complete list of commands.
   * 
   * @return the complete list of commands.
   */
  public List<WetCommand> getCommands();

  /**
   * @param aConfiguration the configuration to use for initialization
   */
  public void initialize(Properties aConfiguration);
}
