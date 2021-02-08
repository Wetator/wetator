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


package org.wetator.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

/**
 * FileUtil contains some useful extensions to work with files.
 *
 * @author rbri
 * @author frank.danek
 */
public final class FileUtil {

  /**
   * Creates the specified directory if needed.
   *
   * @param anOutputDir the name of the directory
   * @throws IOException in case of problems creating the output directory
   */
  public static void createOutputDir(final File anOutputDir) throws IOException {
    if (null == anOutputDir) {
      // I18n
      throw new IllegalArgumentException("No output directory specified.");
    }

    if (anOutputDir.exists()) {
      if (anOutputDir.isFile()) {
        // I18n
        throw new IOException("There is already a file ('" + FilenameUtils.normalize(anOutputDir.getAbsolutePath())
            + "' with the same name as the configured"
            + " directory. Please change the configured directory or rename the file.");
      }
    } else {
      if (!anOutputDir.mkdirs()) {
        // I18n
        throw new IOException("Can't create the directory ('" + FilenameUtils.normalize(anOutputDir.getAbsolutePath())
            + "'. Please change the configuration.");
      }
    }
  }

  /**
   * Private constructor to be invisible.
   */
  private FileUtil() {
    super();
  }
}
