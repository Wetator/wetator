/*
 * Copyright (c) 2008-2014 wetator.org
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


package org.wetator.ant;

import java.io.IOException;
import java.io.Writer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.wetator.progresslistener.StdOutProgressListener;
import org.wetator.util.Output;

/**
 * Simple progress listener that writes to the ant output system.
 * Developer note:<br>
 * Ant supports only the output of a whole line; we have to do some
 * dirty tricks to show something meaningful
 * 
 * @author rbri
 */
public final class AntOutProgressListener extends StdOutProgressListener {

  /**
   * A wrapper around a {@link Writer}.
   */
  private static class AntWriter extends Writer {
    private Task task;

    public AntWriter(final Task aTask) {
      task = aTask;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
      // ignore
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
      // ignore
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(final char[] aCbuf, final int aOff, final int aLen) throws IOException {
      // remove the trailing line feeds
      String tmpOutput = String.valueOf(aCbuf, aOff, aLen);
      tmpOutput = tmpOutput.replaceAll("\\s+$", "");
      task.log(tmpOutput, Project.MSG_INFO);
    }

  }

  private static final int PRINT_AFTER_SECONDS = 4;
  private StringBuilder printBuffer;
  private long lastPrint;

  /**
   * The constructor.
   * 
   * @param aWetator the wetator this executes
   */
  public AntOutProgressListener(final Wetator aWetator) {
    super();
    output = new Output(new AntWriter(aWetator), "  ");
    printBuffer = new StringBuilder();
    lastPrint = System.currentTimeMillis();
  }

  @Override
  protected void print(final String aString) {
    if (System.currentTimeMillis() - lastPrint > 1000 * PRINT_AFTER_SECONDS) {
      println(aString);
    } else {
      printBuffer.append(aString);
    }
  }

  @Override
  protected void println(final String aString) {
    printBuffer.append(aString);
    super.println(printBuffer.toString());
    lastPrint = System.currentTimeMillis();
    printBuffer.setLength(0);
  }
}
