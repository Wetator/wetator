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


package org.wetator.jenkins.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import hudson.Util;

/**
 * This is an extension of the {@link hudson.util.AtomicFileWriter} using GZIP compression.<br/>
 * It is needed because the field 'core' is final and the stream chain is hard coded within the constructor. :-(
 *
 * @see hudson.util.AtomicFileWriter
 * @author Kohsuke Kawaguchi
 * @author frank.danek
 */
public class AtomicGZIPFileWriter extends Writer {

  private final Writer core;
  private final File temporaryFile;
  private final File destinationFile;

  /**
   * The constructor.<br>
   * Writes with UTF-8 encoding.
   *
   * @param aFile the file to write
   */
  public AtomicGZIPFileWriter(File aFile) throws IOException {
    this(aFile, "UTF-8");
  }

  /**
   * The constructor.<br>
   * Writes with the given encoding.
   *
   * @param aFile the file to write
   * @param anEncoding
   *        the file encoding to write. If null, platform default encoding is chosen.
   */
  public AtomicGZIPFileWriter(File aFile, String anEncoding) throws IOException {
    File tmpDirectory = aFile.getParentFile();
    try {
      tmpDirectory.mkdirs();
      temporaryFile = File.createTempFile("atomic", null, tmpDirectory);
    } catch (IOException e) {
      throw new IOException("Failed to create a temporary file in " + tmpDirectory, e);
    }
    destinationFile = aFile;
    if (anEncoding == null) {
      anEncoding = Charset.defaultCharset().name();
    }
    core = new BufferedWriter(
        new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(temporaryFile)), anEncoding));
  }

  @Override
  public void write(int aChar) throws IOException {
    core.write(aChar);
  }

  @Override
  public void write(String aString, int anOffset, int aLength) throws IOException {
    core.write(aString, anOffset, aLength);
  }

  @Override
  public void write(char aChars[], int anOffset, int aLength) throws IOException {
    core.write(aChars, anOffset, aLength);
  }

  @Override
  public void flush() throws IOException {
    core.flush();
  }

  @Override
  public void close() throws IOException {
    core.close();
  }

  /**
   * When the write operation failed, call this method to
   * leave the original file intact and remove the temporary file.
   * This method can be safely invoked from the "finally" block, even after
   * the {@link #commit()} is called, to simplify coding.
   */
  public void abort() throws IOException {
    close();
    temporaryFile.delete();
  }

  public void commit() throws IOException {
    close();
    if (destinationFile.exists()) {
      try {
        Util.deleteFile(destinationFile);
      } catch (IOException x) {
        temporaryFile.delete();
        throw x;
      }
    }
    temporaryFile.renameTo(destinationFile);
  }

  @Override
  protected void finalize() throws Throwable {
    // one way or the other, temporary file should be deleted.
    close();
    temporaryFile.delete();

    super.finalize();
  }

  /**
   * Until the data is committed, this file captures
   * the written content.
   */
  public File getTemporaryFile() {
    return temporaryFile;
  }
}
