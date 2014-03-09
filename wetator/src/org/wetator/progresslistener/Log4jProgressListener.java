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


package org.wetator.progresslistener;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
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
 * Log4jAppender, that also works as progress listener.
 * The appender saves the log of some steps for dumping
 * in case of failure.
 * 
 * @author rbri
 */
public class Log4jProgressListener extends AppenderSkeleton implements IProgressListener {

  private static final Log LOG = LogFactory.getLog(XMLResultWriter.class);

  private File outputDir;
  private String testCase;
  private String browser;

  private Level baseLevel;
  private List<Appender> baseAppenders;

  private int commandCount;
  private List<CommandEvents> commandEvents;
  private CommandEvents currentEvents;

  /**
   * The constructor.
   * 
   * @param aCommandCount the number of commands to hold
   * @param anAppenders the already defined appenders for forwarding
   * @param aLevel the level defined so far for forwarding
   */
  public Log4jProgressListener(final int aCommandCount, @SuppressWarnings("rawtypes") final Enumeration anAppenders,
      final Level aLevel) {
    commandCount = aCommandCount;
    commandEvents = new LinkedList<CommandEvents>();

    baseLevel = aLevel;
    baseAppenders = new LinkedList<Appender>();
    while (anAppenders.hasMoreElements()) {
      final Appender tmpAppender = (Appender) anAppenders.nextElement();
      baseAppenders.add(tmpAppender);
    }

    setLayout(new PatternLayout("[%5.5t] %m%n"));
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.log4j.Appender#close()
   */
  @Override
  public void close() {
    commandEvents = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.log4j.Appender#requiresLayout()
   */
  @Override
  public boolean requiresLayout() {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  @Override
  protected void append(final LoggingEvent aLoggingEvent) {
    // aLoggingEvent.getLocationInformation();
    currentEvents.events.add(aLoggingEvent);

    if (baseLevel == null || aLoggingEvent.getLevel().isGreaterOrEqual(baseLevel)) {
      for (Appender tmpAppender : baseAppenders) {
        tmpAppender.doAppend(aLoggingEvent);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#init(WetatorEngine)
   */
  @Override
  public void init(final WetatorEngine aWetatorEngine) {
    final WetatorConfiguration tmpConfiguration = aWetatorEngine.getConfiguration();
    outputDir = tmpConfiguration.getOutputDir();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#start(WetatorEngine)
   */
  @Override
  public void start(final WetatorEngine aWetatorEngine) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseStart(org.wetator.core.TestCase)
   */
  @Override
  public void testCaseStart(final TestCase aTestCase) {
    testCase = aTestCase.getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(final String aBrowserName) {
    browser = aBrowserName;
    commandEvents = new LinkedList<CommandEvents>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(final String aFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandStart(org.wetator.core.WetatorContext,
   *      org.wetator.core.Command)
   */
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

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandIgnored()
   */
  @Override
  public void executeCommandIgnored() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandFailure(org.wetator.exception.AssertionException)
   */
  @Override
  public void executeCommandFailure(final AssertionException anAssertionException) {
    dump();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(final Throwable aThrowable) {
    dump();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunIgnored()
   */
  @Override
  public void testRunIgnored() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#end(WetatorEngine)
   */
  @Override
  public void end(final WetatorEngine aWetatorEngine) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(final String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#error(java.lang.Throwable)
   */
  @Override
  public void error(final Throwable aThrowable) {
    aThrowable.printStackTrace();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#warn(String, String[], Throwable)
   */
  @Override
  public void warn(final String aMessageKey, final String[] aParameterArray, final Throwable aThrowable) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(final String aMessageKey, final String[] aParameterArray) {
  }

  /**
   * The worker that does the real output.
   */
  protected void dump() {
    final String tmpFileName = "wire_" + testCase + "_" + browser;
    String tmpSuffix = "";
    int tmpCount = 0;
    File tmpResultFile;
    do {
      tmpResultFile = new File(outputDir, tmpFileName + tmpSuffix + ".txt");
      tmpSuffix = "_" + tmpCount++;
    } while (tmpResultFile.exists());

    try {
      final Writer tmpWriter = new FileWriterWithEncoding(tmpResultFile, "UTF-8");
      try {
        final Output tmpOutput = new Output(tmpWriter, "    ");
        final Layout tmpLayout = getLayout();
        for (CommandEvents tmpEvents : commandEvents) {
          tmpOutput.println("******************************************");
          tmpOutput.print("* ");
          tmpOutput.println(tmpEvents.command.getName());
          Parameter tmpParam = tmpEvents.command.getFirstParameter();
          if (null != tmpParam) {
            tmpOutput.print("*   ");
            tmpOutput.println(tmpParam.getValue());
          }
          tmpParam = tmpEvents.command.getSecondParameter();
          if (null != tmpParam) {
            tmpOutput.print("*   ");
            tmpOutput.println(tmpParam.getValue());
          }
          tmpOutput.println("******************************************");
          tmpOutput.indent();

          for (LoggingEvent tmpEvent : tmpEvents.events) {
            if (tmpLayout == null) {
              tmpOutput.println(tmpEvent.getMessage().toString());
            } else {
              tmpOutput.printStringWithNewLine(tmpLayout.format(tmpEvent));
            }
          }

          tmpOutput.unindent();
        }
        tmpOutput.flush();
      } finally {
        commandEvents.clear();

        tmpWriter.close();
      }
    } catch (final IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private static class CommandEvents {
    private Command command;
    private List<LoggingEvent> events;

    private CommandEvents(final Command aCommand) {
      super();
      command = aCommand;
      events = new LinkedList<LoggingEvent>();
    }
  }
}
