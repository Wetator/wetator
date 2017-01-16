/*
 * Copyright (c) 2008-2017 wetator.org
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple helper to write formated to a writer.
 *
 * @author rbri
 */
public final class Output {
  private static final String NEW_LINE = System.getProperty("line.separator");

  private Writer writer;
  private StringBuffer currentIndent;
  private boolean afterNewLine;
  private final String indent;

  /**
   * Constructor.
   *
   * @param aWriter the writer to write to
   * @param anIndent String to be used for indenting (e.g. "", " ", " ", "\t")
   */
  public Output(final Writer aWriter, final String anIndent) {
    writer = new BufferedWriter(aWriter);
    indent = anIndent;
    currentIndent = new StringBuffer();
  }

  /**
   * Write the char.
   *
   * @param aChar the char to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output print(final char aChar) throws IOException {
    writeIndentIfNeeded();
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
  public Output print(final String aString) throws IOException {
    if (null != aString) {
      writeIndentIfNeeded();
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
  public Output println(final String aString) throws IOException {
    // no need to indent whitespace
    if (afterNewLine && StringUtils.isBlank(aString)) {
      writer.write(NEW_LINE); // to be sure to do not remove any whitespace
      return this;
    }

    writeIndentIfNeeded();
    writer.write(aString);
    writer.write(NEW_LINE);
    afterNewLine = true;

    return this;
  }

  /**
   * Start a newline.
   *
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output println() throws IOException {
    writer.write(NEW_LINE);
    afterNewLine = true;

    return this;
  }

  /**
   * Write the string; we know, this string already ends with a newline.
   *
   * @param aString the string to be written
   * @return this (for convenience)
   * @throws IOException in case of problems
   */
  public Output printStringWithNewLine(final String aString) throws IOException {
    writeIndentIfNeeded();
    writer.write(aString);
    afterNewLine = true;

    return this;
  }

  /**
   * Flushes the output.
   *
   * @return this (for convenience)
   * @throws IOException in case of error
   */
  public Output flush() throws IOException {
    writer.flush();

    return this;
  }

  /**
   * Closes the output. Makes a {@link #flush()} first.
   *
   * @return this (for convenience)
   * @throws IOException in case of error
   */
  public Output close() throws IOException {
    writer.flush();
    writer.close();

    return this;
  }

  /**
   * Indent the following.
   *
   * @return this (for convenience)
   */
  public Output indent() {
    currentIndent.append(indent);

    return this;
  }

  /**
   * Clear the indent.
   *
   * @return this (for convenience)
   */
  public Output unindent() {
    currentIndent.setLength(Math.max(0, currentIndent.length() - indent.length()));

    return this;
  }

  /**
   * Helper to write a newline.
   *
   * @throws IOException in case of problems
   */
  private void writeIndentIfNeeded() throws IOException {
    if (afterNewLine) {
      writer.write(currentIndent.toString());
      afterNewLine = false;
    }
  }
}
