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

import org.wetator.exception.InvalidInputException;

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
 * @author tobwoerk
 */
public interface IScripter {

  /**
   * @param aConfiguration the configuration to use for initialization
   * @throws org.wetator.exception.ConfigurationException in case of problems during initialization
   */
  public void initialize(Properties aConfiguration);

  /**
   * The result for the isSupported method call.
   * This offers a way to transport some info message.
   */
  public static final class IsSupportedResult {
    private String message;

    /**
     * Constructor.
     * 
     * @param aMessage the message
     */
    public IsSupportedResult(final String aMessage) {
      message = aMessage;
    }

    /**
     * Getter for the message.
     * 
     * @return the message
     */
    public String getMessage() {
      return message;
    }
  }

  /**
   * Success as singleton.
   */
  public static final IsSupportedResult IS_SUPPORTED = new IsSupportedResult(null);

  /**
   * @param aFile the file to check
   * @return IS_SUPPORTED if this scripter is able to handle this file otherwise an
   *         IsSupportedResult containing a detailed description.
   * @throws InvalidInputException in case of an invalid file
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   */
  public IsSupportedResult isSupported(File aFile) throws InvalidInputException;

  /**
   * Scripts the given file by reading all commands.
   * 
   * @param aFile the file
   * @throws InvalidInputException in case of an invalid file
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   */
  public void script(File aFile) throws InvalidInputException;

  /**
   * @return the complete list of commands.
   */
  public List<Command> getCommands();
}
