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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A simple helper to write formated to a writer
 * 
 * @author rbri
 */
public final class Output {
  private static final String NEW_LINE = "\n";

  private Writer writer;
  private StringBuffer currentIndent = new StringBuffer();
  private boolean newLineComing;
  private final String indent;

  /**
   * Constructor.
   * 
   * @param aWriter the writer to write to
   * @param anIndent String to be used for indenting (e.g. "", " ", " ", "\t")
   */
  public Output(Writer aWriter, String anIndent) {
    writer = new BufferedWriter(aWriter);
    indent = anIndent;
  }

  /**
   * Write the char.
   * 
   * @param aChar the char to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output print(char aChar) throws IOException {
    writeNewLineIfNeeded();
    writer.write(aChar);

    return this;
  }

  /**
   * Write the String.
   * 
   * @param aString the string to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output print(String aString) throws IOException {
    if (null != aString) {
      writeNewLineIfNeeded();
      writer.write(aString);
    }

    return this;
  }

  /**
   * Write the string on a new line.
   * 
   * @param aString the string to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output println(String aString) throws IOException {
    writeNewLineIfNeeded();
    writer.write(aString);
    newLineComing = true;

    return this;
  }

  /**
   * Writes the given string without inserting
   * a line break.
   * 
   * @param aString the string to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output printDirect(String aString) throws IOException {
    writer.write(aString);

    return this;
  }

  /**
   * Start a newline.
   * 
   * @return this (for convenience)
   */
  public Output println() {
    newLineComing = true;

    return this;
  }

  /**
   * Flush
   * 
   * @return this (for convenience)
   * @throws IOException in case of error
   */
  public Output flush() throws IOException {
    writer.flush();

    return this;
  }

  /**
   * Indent the following.
   */
  public void indent() {
    currentIndent.append(indent);
  }

  /**
   * clear indent
   */
  public void unindent() {
    currentIndent.setLength(currentIndent.length() - indent.length());
  }

  /**
   * Helper to write a newline.
   * 
   * @throws IOException in case of problems
   */
  private void writeNewLineIfNeeded() throws IOException {
    if (newLineComing) {
      writer.write(NEW_LINE);
      writer.write(currentIndent.toString());
      newLineComing = false;
    }
  }
}
