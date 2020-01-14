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


package org.wetator.progresslistener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.wetator.core.Command;
import org.wetator.core.IProgressListener;
import org.wetator.core.Parameter;
import org.wetator.core.TestCase;
import org.wetator.core.WetatorConfiguration;
import org.wetator.core.WetatorContext;
import org.wetator.core.WetatorEngine;
import org.wetator.exception.AssertionException;
import org.wetator.util.Output;

/**
 * This {@link IProgressListener} implements the Wetator's retrospect feature.<br>
 * It
 * <ul>
 * <li>catches and stores both Wetator {@link Command}s (by implementing the {@link IProgressListener} interface) and
 * logging
 * output (by implementing Log4j's {@link Appender} interface) of a configured number of {@link Command}s in the correct
 * order</li>
 * <li>dumps the stored info in case of a failure or an error</li>
 * </ul>
 * <strong>ATTENTION!</strong> Sets the log level of some loggers to <code>trace</code> (see
 * {@link #addAsTraceAppender()}).
 *
 * @author rbri
 * @author frank.danek
 */
public class Log4jProgressListener extends AbstractAppender implements IProgressListener {

  private static final Logger LOG = LogManager.getLogger(Log4jProgressListener.class);

  private int commandCount;
  private List<CommandEvents> commandEvents = new LinkedList<>();
  private CommandEvents currentEvents;

  private File outputDir;
  private String testCase;
  private String browser;

  /**
   * The constructor.
   *
   * @param aCommandCount the number of {@link Command}s to store
   */
  public Log4jProgressListener(final int aCommandCount) {
    super("WetatorLog4jProgressListener", null,
        PatternLayout.newBuilder().withPattern("%5p [%5.5t] (%25.25F:%5.5L) - %m%n").build(), true,
        Property.EMPTY_ARRAY);

    commandCount = aCommandCount;

    final Command tmpCommand = new Command("--startup--", false);
    currentEvents = new CommandEvents(tmpCommand);

    start();

    addAsTraceAppender();
  }

  /**
   * Adds this instance as {@link Appender} for
   * <ul>
   * <li><code>org.apache.http.wire</code></li>
   * <li><code>org.wetator</code></li>
   * </ul>
   * and sets the log level of these loggers to <code>trace</code>.
   */
  protected void addAsTraceAppender() {
    final LoggerContext tmpContext = (LoggerContext) LogManager.getContext(false); // NOPMD

    addAsTraceAppender(tmpContext, "org.apache.http.wire");
    addAsTraceAppender(tmpContext, "org.wetator");
  }

  /**
   * Adds this instance as {@link Appender} to the given logger and sets it's level to <code>trace</code>.
   *
   * @param aContext the {@link LoggerContext}
   * @param aLoggerName the logger name
   */
  protected void addAsTraceAppender(final LoggerContext aContext, final String aLoggerName) {
    final LoggerConfig tmpLogger = aContext.getConfiguration().getLoggerConfig(aLoggerName);
    tmpLogger.setLevel(Level.TRACE);
    tmpLogger.addAppender(this, null, null);
  }

  @Override
  public void append(final LogEvent aLogEvent) {
    // we need to initialize the source here (during the original log call)
    // later (in dump()) the context is not available anymore
    aLogEvent.getSource();
    currentEvents.getEvents().add(aLogEvent);
  }

  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();
    outputDir = tmpConfiguration.getOutputDir();
  }

  @Override
  public void start(final WetatorEngine aWetatorEngine) {
  }

  @Override
  public void testCaseStart(final TestCase aTestCase) {
    testCase = aTestCase.getName();
  }

  @Override
  public void testRunStart(final String aBrowserName) {
    browser = aBrowserName;
    commandEvents.clear();
  }

  @Override
  public void testFileStart(final String aFileName) {
  }

  @Override
  public void executeCommandStart(final WetatorContext aContext, final Command aCommand) {
    if (aCommand.isComment()) {
      return;
    }
    synchronized (commandEvents) {
      if (commandEvents.size() == commandCount) {
        commandEvents.remove(0);
      }
      currentEvents = new CommandEvents(aCommand);
      commandEvents.add(currentEvents);
    }
  }

  @Override
  public void executeCommandSuccess() {
  }

  @Override
  public void executeCommandIgnored() {
  }

  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    dump();
  }

  @Override
  public void executeCommandError(final Throwable aThrowable) {
    dump();
  }

  @Override
  public void executeCommandEnd() {
  }

  @Override
  public void testFileEnd() {
  }

  @Override
  public void testRunIgnored() {
  }

  @Override
  public void testRunEnd() {
  }

  @Override
  public void testCaseEnd() {
  }

  @Override
  public void end(final WetatorEngine aWetatorEngine) {
  }

  @Override
  public void htmlDescribe(final String aHtmlDescription) {
  }

  @Override
  public void responseStored(final String aResponseFileName) {
  }

  @Override
  public void highlightedResponse(final String aResponseFileName) {
  }

  @Override
  public void error(final Throwable aThrowable) {
    aThrowable.printStackTrace(); // NOPMD
  }

  @Override
  public void warn(final String aMessageKey, final Object[] aParameters, final String aDetails) {
  }

  @Override
  public void info(final String aMessageKey, final Object... aParameters) {
  }

  /**
   * The worker that does the real output.
   */
  protected void dump() {
    String tmpFileName = StringUtils.replace(testCase, File.separator, "__");
    tmpFileName = "wire_" + tmpFileName + "_" + browser; // NOPMD
    String tmpSuffix = "";
    int tmpCount = 0;
    File tmpResultFile;
    do {
      tmpResultFile = new File(outputDir, tmpFileName + tmpSuffix + ".txt");
      tmpSuffix = "_" + tmpCount++;
    } while (tmpResultFile.exists());

    try (Writer tmpWriter = new FileWriterWithEncoding(tmpResultFile, StandardCharsets.UTF_8)) {
      final Output tmpOutput = new Output(tmpWriter, "    ");
      final Layout<? extends Serializable> tmpLayout = getLayout();
      for (final CommandEvents tmpEvents : commandEvents) {
        tmpOutput.println("******************************************");
        tmpOutput.print("* ");
        tmpOutput.println(tmpEvents.getCommand().getName());
        Parameter tmpParam = tmpEvents.getCommand().getFirstParameter();
        if (null != tmpParam) {
          tmpOutput.print("*   ");
          tmpOutput.println(tmpParam.getValue());
        }
        tmpParam = tmpEvents.getCommand().getSecondParameter();
        if (null != tmpParam) {
          tmpOutput.print("*   ");
          tmpOutput.println(tmpParam.getValue());
        }
        tmpOutput.println("******************************************");
        tmpOutput.indent();

        for (final LogEvent tmpEvent : tmpEvents.getEvents()) {
          if (tmpLayout == null) {
            tmpOutput.println(tmpEvent.getMessage().toString());
          } else {
            tmpOutput.printStringWithNewLine(tmpLayout.toSerializable(tmpEvent).toString());
          }
        }

        tmpOutput.unindent();
      }
      tmpOutput.flush();
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    } finally {
      commandEvents.clear();
    }
  }

  /**
   * Container to store all {@link LogEvent}s that were logged while executing the given {@link Command}.
   */
  private static final class CommandEvents {
    private Command command;
    private List<LogEvent> events = new LinkedList<>();

    private CommandEvents(final Command aCommand) {
      command = aCommand;
    }

    private Command getCommand() {
      return command;
    }

    private List<LogEvent> getEvents() {
      return events;
    }
  }
}
