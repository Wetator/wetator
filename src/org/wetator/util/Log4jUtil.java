/*
 * Copyright (c) 2008-2016 wetator.org
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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Simple helper used as the only class having direct references to
 * Log4J. This makes wetator independent of log4j as long as it is not
 * used by some feature.
 *
 * @author rbri
 */
public final class Log4jUtil {

  /**
   * The constructor.
   */
  private Log4jUtil() {
    super();
  }

  /**
   * @see org.wetator.Wetator#main(String[])
   * @param aLogFile the log target
   */
  public static void configureLog(final File aLogFile) {
    final FileAppender tmpFileAppender = new FileAppender();
    tmpFileAppender.setName("w_file");
    tmpFileAppender.setFile(aLogFile.getAbsolutePath());
    tmpFileAppender.setLayout(new PatternLayout("%5p [%5.5t] (%25.25F:%5.5L) - %m%n"));
    tmpFileAppender.setAppend(false);
    tmpFileAppender.activateOptions();
    Logger.getRootLogger().addAppender(tmpFileAppender);

    for (final String tmpLog : new String[] { "org.wetator", "com.gargoylesoftware.htmlunit.javascript.DebugFrameImpl",
        "com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine", "org.apache.http.wire" }) {
      final Logger tmpLogger = LogManager.getLogger(tmpLog);
      tmpLogger.setLevel(Level.TRACE);
    }
  }
}
