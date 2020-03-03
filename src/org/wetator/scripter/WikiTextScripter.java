/*
 * Copyright (c) 2008-2020 wetator.org
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
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.OctalUnescaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
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
  private static final String NEW_LINE = System.getProperty("line.separator");

  private static final String FILE_EXTENSION = ".wett";
  private static final String COMMENT_LINE = "#";
  private static final String COMMENT_LINE2 = "//";
  private static final String CONTINUATION = "\\";
  private static final String SEPARATOR = "||";

  private static final int COMMAND_NAME_COLUMN_NO = 0;
  private static final int FIRST_PARAM_COLUMN_NO = 1;
  private static final int SECOND_PARAM_COLUMN_NO = 2;
  private static final int THIRD_PARAM_COLUMN_NO = 3;

  // @formatter:off
  private static final CharSequenceTranslator UNESCAPER =
      new AggregateTranslator(
          new OctalUnescaper(),     // .between('\1', '\377'),
          new UnicodeUnescaper()
      );
  // @formatter:on

  private File file;
  private List<Command> commands;

  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }

  @Override
  public IScripter.IsSupportedResult isSupported(final File aFile) {
    // first check the file extension
    final String tmpFileName = aFile.getName().toLowerCase(Locale.ROOT);
    if (!tmpFileName.endsWith(FILE_EXTENSION)) {
      return new IScripter.IsSupportedResult("File '" + aFile.getName()
          + "' not supported by WikiTextScripter. Extension is not '" + FILE_EXTENSION + "'.");
    }

    // second check the file accessibility
    if (!aFile.exists() || !aFile.isFile()) {
      return new IScripter.IsSupportedResult(
          "File '" + aFile.getName() + "' not supported by WikiTextScripter. Could not find file.");
    }
    if (!aFile.canRead()) {
      return new IScripter.IsSupportedResult(
          "File '" + aFile.getName() + "' not supported by WikiTextScripter. Could not read file.");
    }

    return IScripter.IS_SUPPORTED;
  }

  @Override
  public void script(final File aFile) throws InvalidInputException {
    file = aFile;

    commands = readCommands();
  }

  private List<Command> readCommands() throws InvalidInputException {
    final List<Command> tmpResult = new LinkedList<>();

    try (LineIterator tmpLines = FileUtils.lineIterator(file.getAbsoluteFile(), "UTF-8")) {
      int tmpLineNo = 0;
      while (tmpLines.hasNext()) {
        tmpLineNo++;
        String tmpLine = tmpLines.next().trim();
        while (tmpLine.endsWith(CONTINUATION)) {
          tmpLine = tmpLine.substring(0, tmpLine.length() - CONTINUATION.length());
          tmpLine = StringUtils.stripEnd(tmpLine, "\t");
          tmpLine = tmpLine + NEW_LINE; // NOPMD
          if (tmpLines.hasNext()) {
            tmpLine = tmpLine + StringUtils.stripEnd(tmpLines.next(), null); // NOPMD
            tmpLineNo++;
          }
        }

        // ignore blank lines
        if (StringUtils.isBlank(tmpLine)) {
          continue;
        }

        tmpLine = UNESCAPER.translate(tmpLine);

        boolean tmpComment = false;
        if (tmpLine.startsWith(COMMENT_LINE)) {
          tmpComment = true;
          tmpLine = tmpLine.substring(1);
        } else if (tmpLine.startsWith(COMMENT_LINE2)) {
          tmpComment = true;
          tmpLine = tmpLine.substring(2);
        }

        String[] tmpParts = StringUtils.splitByWholeSeparator(tmpLine, SEPARATOR);

        String tmpCommandName = "";
        if (tmpParts.length > COMMAND_NAME_COLUMN_NO) {
          tmpCommandName = tmpParts[COMMAND_NAME_COLUMN_NO];
          tmpCommandName = tmpCommandName.trim();
        }
        // normalize command name
        if (StringUtils.isNotEmpty(tmpCommandName) && !(tmpComment && tmpParts.length < 2)) {
          tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase(Locale.ROOT);
        }
        tmpCommandName = new NormalizedString(tmpCommandName).toString();

        // empty command means comment
        if (tmpComment && (StringUtils.isEmpty(tmpCommandName) || tmpParts.length < 2)) {
          tmpCommandName = "comment";
          if (tmpParts.length == 1) {
            tmpCommandName = "";
            tmpParts = new String[] { tmpCommandName, tmpParts[0] };
          }
        }

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
      return tmpResult;
    } catch (final FileNotFoundException e) {
      throw new InvalidInputException("Could not find file '" + FilenameUtils.normalize(file.getAbsolutePath()) + "'.",
          e);
    } catch (final IOException e) {
      throw new ResourceException("Could not read file '" + FilenameUtils.normalize(file.getAbsolutePath()) + "'.", e);
    }
  }

  @Override
  public List<Command> getCommands() {
    return commands;
  }
}
