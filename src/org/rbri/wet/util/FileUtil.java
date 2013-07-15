/*
 * Copyright (c) 2008-2010 Ronald Brill
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


package org.rbri.wet.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.exception.WetException;



/**
 * FileUtil contains some useful extensions to work with files.
 *
 * @author rbri
 */
public class FileUtil {


    /**
     * Creates the specified directory if needed 
     *
     * @param anOutputDirPath the name of the directoy
     * @return the directory file object
     * @throws WetException in case of problems 
     */
    public static File createOutputDir(String anOutputDirPath) throws WetException {
        if (StringUtils.isEmpty(anOutputDirPath)) {
            // I18n
            throw new WetException("No output dir specified");
        }

        File tmpDir = new File(anOutputDirPath);
        if (tmpDir.exists()) {
            if (tmpDir.isFile()) {
                // I18n
                throw new WetException("There is already a file ('" + tmpDir.getAbsolutePath() + "' with the same name as the configured"
                        + "directory. Please change the configured directory or rename the file.");
            }
        } else {
            if (!tmpDir.mkdirs()) {
                // I18n
                throw new WetException("Can't create the directory ('" + tmpDir.getAbsolutePath() + "'. Please change the configuration.");
            }
        }
        return tmpDir;
    }
}
