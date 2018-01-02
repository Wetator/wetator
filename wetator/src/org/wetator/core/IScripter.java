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


package org.wetator.core;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.wetator.exception.InvalidInputException;

/**
 * The interface for scripters.<br>
 * Scripters are responsible for reading an input file and parsing the commands.<br>
 * Scripters are reused for many files. The flow is:
 * <ol>
 * <li>{@link #isSupported(File)}</li>
 * <li>{@link #script(File)}</li>
 * <li>{@link #getCommands()}</li>
 * </ol>
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public interface IScripter {

  /**
   * This result is used to signal that a scripter supports the given file.<br>
   * For performance reason always compare to this result when checking.
   */
  IsSupportedResult IS_SUPPORTED = new IsSupportedResult(null);

  /**
   * @param aConfiguration the configuration to use for initialization
   * @throws org.wetator.exception.ConfigurationException in case of problems during initialization
   */
  void initialize(Properties aConfiguration);

  /**
   * @param aFile the file to check
   * @return {@link #IS_SUPPORTED} if this scripter is able to handle the given file otherwise an
   *         {@link IsSupportedResult} containing a detailed description, why the file is not supported
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   */
  IsSupportedResult isSupported(File aFile);

  /**
   * Scripts the given file by reading all commands.
   *
   * @param aFile the file
   * @throws InvalidInputException in case of an invalid file
   * @throws org.wetator.exception.ResourceException in case of problems reading the file
   */
  void script(File aFile) throws InvalidInputException;

  /**
   * @return the complete list of commands.
   */
  List<Command> getCommands();

  /**
   * The result for the {@link IScripter#isSupported(File)} method call.<br>
   * This offers a way to transport some info message.
   */
  final class IsSupportedResult {

    private String message;

    /**
     * The constructor.
     *
     * @param aMessage the message
     */
    public IsSupportedResult(final String aMessage) {
      message = aMessage;
    }

    /**
     * @return the message
     */
    public String getMessage() {
      return message;
    }
  }
}
