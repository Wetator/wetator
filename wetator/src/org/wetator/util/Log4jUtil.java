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


package org.wetator.util;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * We do not want to have a direct dependency to the log4j implementation but only the API.<br>
 * Because of that this utility class centralizes all the direct dependencies. Exception:
 * <code>Log4jProgressListener</code>.<br>
 * <br>
 * Use this class with the fact in mind that the log4j implementation is an optional dependency for the default Wetator
 * execution and may not be in the class path, e.g. use feature toggles or reflection (and handle exceptions).
 *
 * @author rbri
 * @author frank.danek
 */
public final class Log4jUtil {

  /**
   * Configures the Wetator debug logging feature using the given log file.
   *
   * @see org.wetator.Wetator#main(String[])
   * @param aLogFile the target log file
   */
  public static void configureDebugLogging(final File aLogFile) {
    final LoggerContext tmpContext = (LoggerContext) LogManager.getContext(false);
    final Configuration tmpConfig = tmpContext.getConfiguration();

    final PatternLayout tmpLayout = PatternLayout.newBuilder().withConfiguration(tmpConfig)
        .withPattern("%5p [%5.5t] (%25.25F:%5.5L) - %m%n").build();

    final FileAppender tmpFileAppender = FileAppender.newBuilder().withName("WetatorDebugAppender")
        .withFileName(aLogFile.getAbsolutePath()).withLayout(tmpLayout).withAppend(false).build();
    tmpFileAppender.start();
    tmpConfig.addAppender(tmpFileAppender);

    tmpConfig.getRootLogger().addAppender(tmpFileAppender, null, null);

    for (final String tmpLog : new String[] { "org.wetator", "com.gargoylesoftware.htmlunit.javascript.DebugFrameImpl",
        "com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine", "org.apache.http.wire" }) {
      tmpConfig.getLoggerConfig(tmpLog).setLevel(Level.TRACE);
    }
  }

  private Log4jUtil() {
    super();
  }
}
