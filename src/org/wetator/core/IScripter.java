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


package org.wetator.core;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.wetator.exception.WetatorException;

/**
 * The interface for scripters.<br/>
 * Scripters are responsible for reading an input file and parsing the commands.<br/>
 * Scripters are reused for many files. The flow is:
 * <ol>
 * <li>isSupported()</li>
 * <li>script()</li>
 * <li>getCommands()</li>
 * </ol>
 * 
 * @author rbri
 * @author frank.danek
 */
public interface IScripter {

  /**
   * @param aFile the file to check
   * @return true if this scripter is able to handle this file otherwise false
   */
  public boolean isSupported(File aFile);

  /**
   * Scripts the given file by reading all commands.
   * 
   * @param aFile the file
   * @throws WetatorException in case of error
   */
  public void script(File aFile) throws WetatorException;

  /**
   * @return the complete list of commands.
   */
  public List<Command> getCommands();

  /**
   * @param aConfiguration the configuration to use for initialization
   */
  public void initialize(Properties aConfiguration);
}
