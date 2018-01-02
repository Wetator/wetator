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


package org.wetator.ant;

import java.io.IOException;
import java.io.Writer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * A wrapper around a {@link Writer}.
 *
 * @author rbri
 */
class AntWriter extends Writer {
  private Task task;

  /**
   * Ctor.
   *
   * @param aTask the ant task for calling the log function
   */
  AntWriter(final Task aTask) {
    task = aTask;
  }

  @Override
  public void close() throws IOException {
    // ignore
  }

  @Override
  public void flush() throws IOException {
    // ignore
  }

  @Override
  public void write(final char[] aCbuf, final int aOff, final int aLen) throws IOException {
    // remove the trailing line feeds
    String tmpOutput = String.valueOf(aCbuf, aOff, aLen);
    tmpOutput = tmpOutput.replaceAll("\\s+$", "");
    task.log(tmpOutput, Project.MSG_INFO);
  }
}