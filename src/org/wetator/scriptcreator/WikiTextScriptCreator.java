/*
 * Copyright (c) 2008-2021 wetator.org
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Command;

/**
 * Creates a Wetator test script in Wiki format from the given commands in the given
 * output directory.
 *
 * @author rbri
 */
public class WikiTextScriptCreator implements IScriptCreator {

  private List<Command> commands;
  private String fileName;
  private File outputDir;

  @Override
  public void createScript() {
    try {
      final File tmpFile = new File(outputDir, fileName + ".wett");
      try (Writer tmpWriter = Files.newBufferedWriter(tmpFile.toPath(), StandardCharsets.UTF_8)) {
        for (final Command tmpCommand : commands) {
          if (tmpCommand.isComment()) {
            tmpWriter.write("#");
            if (StringUtils.isNotBlank(tmpCommand.getName())) {
              tmpWriter.write(" ");
              tmpWriter.write(tmpCommand.getName());
            }
            if (tmpCommand.getFirstParameter() != null
                && StringUtils.isNotBlank(tmpCommand.getFirstParameter().getValue())) {
              tmpWriter.write(" ");
              tmpWriter.write(tmpCommand.getFirstParameter().getValue());
            }
            if (tmpCommand.getSecondParameter() != null
                && StringUtils.isNotBlank(tmpCommand.getSecondParameter().getValue())) {
              tmpWriter.write(" ");
              tmpWriter.write(tmpCommand.getSecondParameter().getValue());
            }
          } else {
            if (tmpCommand.getFirstParameter() != null) {
              tmpWriter.write(StringUtils.rightPad(tmpCommand.getName(), 20));
              tmpWriter.write(" || ");
              tmpWriter.write(tmpCommand.getFirstParameter().getValue());
              if (tmpCommand.getSecondParameter() != null) {
                tmpWriter.write("  || ");
                tmpWriter.write(tmpCommand.getSecondParameter().getValue());
              }
            } else {
              tmpWriter.write(tmpCommand.getName());
            }
          }
          tmpWriter.write(System.lineSeparator());
        }
      } catch (final FileNotFoundException e) {
        final FileNotFoundException tmpException = new FileNotFoundException(
            "Can't create output file '" + (tmpFile.getAbsolutePath()) + "'.");
        tmpException.initCause(e);
        throw tmpException;
      }
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      e.printStackTrace(); // NOPMD
    }
  }

  @Override
  public void setCommands(final List<Command> aCommandList) {
    commands = aCommandList;
  }

  @Override
  public void setFileName(final String aFileName) {
    fileName = aFileName;
  }

  @Override
  public void setOutputDir(final String anOutputDir) {
    outputDir = new File(anOutputDir);
  }
}
