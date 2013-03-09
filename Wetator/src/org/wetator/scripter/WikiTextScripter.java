/*
 * Copyright (c) 2008-2013 wetator.org
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.exception.InvalidInputException;
import org.wetator.exception.ResourceException;
import org.wetator.util.NormalizedString;

/**
 * Scripter for wiki text files.
 * 
 * @author rbri
 * @author frank.danek
 */
public final class WikiTextScripter implements IScripter {

  private static final String FILE_EXTENSION = ".wett";
  private static final String COMMENT_LINE = "#";
  private static final String COMMENT_LINE2 = "//";
  private static final String SEPARATOR = "||";

  private static final int COMMAND_NAME_COLUMN_NO = 0;
  private static final int FIRST_PARAM_COLUMN_NO = 1;
  private static final int SECOND_PARAM_COLUMN_NO = 2;
  private static final int THIRD_PARAM_COLUMN_NO = 3;

  private File file;
  private List<Command> commands;

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#isSupported(java.io.File)
   */
  @Override
  public IScripter.IsSupportedResult isSupported(final File aFile) {
    // first check the file extension
    final String tmpFileName = aFile.getName().toLowerCase();
    if (!tmpFileName.endsWith(FILE_EXTENSION)) {
      return new IScripter.IsSupportedResult("File '" + aFile.getName()
          + "' not supported by WikiTextScripter. Extension is not '" + FILE_EXTENSION + "'.");
    }

    // second check the file accessibility
    if (!aFile.exists() || !aFile.isFile()) {
      return new IScripter.IsSupportedResult("File '" + aFile.getName()
          + "' not supported by WikiTextScripter. Could not find file.");
    }
    if (!aFile.canRead()) {
      return new IScripter.IsSupportedResult("File '" + aFile.getName()
          + "' not supported by WikiTextScripter. Could not read file.");
    }

    return IScripter.IS_SUPPORTED;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#script(java.io.File)
   */
  @Override
  public void script(final File aFile) throws InvalidInputException {
    file = aFile;

    commands = readCommands();
  }

  private List<Command> readCommands() throws InvalidInputException {
    final List<Command> tmpResult = new LinkedList<Command>();

    try {
      final LineIterator tmpLines = FileUtils.lineIterator(file.getAbsoluteFile(), "UTF-8");
      try {
        int tmpLineNo = 0;
        while (tmpLines.hasNext()) {
          tmpLineNo++;
          String tmpLine = tmpLines.next().trim();

          // ignore blank lines
          if (StringUtils.isBlank(tmpLine)) {
            continue;
          }

          boolean tmpComment = false;
          if (tmpLine.startsWith(COMMENT_LINE)) {
            tmpComment = true;
            tmpLine = tmpLine.substring(1);
          } else if (tmpLine.startsWith(COMMENT_LINE2)) {
            tmpComment = true;
            tmpLine = tmpLine.substring(2);
          }

          final String[] tmpParts = StringUtils.splitByWholeSeparator(tmpLine, SEPARATOR);

          String tmpCommandName = "";
          if (tmpParts.length > COMMAND_NAME_COLUMN_NO) {
            tmpCommandName = tmpParts[COMMAND_NAME_COLUMN_NO];
            tmpCommandName = tmpCommandName.trim();
          }
          // normalize command name
          if (StringUtils.isNotEmpty(tmpCommandName) && !(tmpComment && tmpParts.length < 2)) {
            tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase();
          }
          tmpCommandName = new NormalizedString(tmpCommandName).toString();

          // empty command means comment
          if (tmpComment && StringUtils.isEmpty(tmpCommandName)) {
            tmpCommandName = "Comment";
          }

          if (!StringUtils.isEmpty(tmpCommandName)) {
            final Command tmpCommand = new Command(tmpCommandName, tmpComment);

            String tmpParameter = null;
            if (tmpParts.length > FIRST_PARAM_COLUMN_NO) {
              tmpParameter = tmpParts[FIRST_PARAM_COLUMN_NO];
              tmpParameter = tmpParameter.trim();
            }
            if (null != tmpParameter) {
              tmpCommand.setFirstParameter(new Parameter(tmpParameter));
            }

            tmpParameter = null;
            if (tmpParts.length > SECOND_PARAM_COLUMN_NO) {
              tmpParameter = tmpParts[SECOND_PARAM_COLUMN_NO];
              tmpParameter = tmpParameter.trim();
            }
            if (null != tmpParameter) {
              tmpCommand.setSecondParameter(new Parameter(tmpParameter));
            }

            tmpParameter = null;
            if (tmpParts.length > THIRD_PARAM_COLUMN_NO) {
              tmpParameter = tmpParts[THIRD_PARAM_COLUMN_NO];
              tmpParameter = tmpParameter.trim();
            }
            if (null != tmpParameter) {
              tmpCommand.setThirdParameter(new Parameter(tmpParameter));
            }

            tmpCommand.setLineNo(tmpLineNo);

            tmpResult.add(tmpCommand);
          }
        }
        return tmpResult;
      } finally {
        tmpLines.close();
      }
    } catch (final FileNotFoundException e) {
      throw new InvalidInputException("Could not find file '" + file.getAbsolutePath() + "'.", e);
    } catch (final IOException e) {
      throw new ResourceException("Could not read file '" + file.getAbsolutePath() + "'.", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#getCommands()
   */
  @Override
  public List<Command> getCommands() {
    return commands;
  }
}
