/*
 * Copyright (c) wetator.org
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

import hudson.Util;
import hudson.util.IOException2;
import hudson.util.XStream2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.XppReader;

/**
 * This is an extension of the {@link hudson.XmlFile} using GZIP compression.<br/>
 * It is needed because the class XmlFile is final and the stream chain is hard coded (at different places). :-(<br/>
 * An automatic fall back to an XML file is included, if the given (.gz) file is not found.
 * 
 * @see hudson.XmlFile
 * @author Kohsuke Kawaguchi
 * @author frank.danek
 */
public class GZIPXMLFile {

  private static final Logger LOGGER = Logger.getLogger(GZIPXMLFile.class.getName());
  /**
   * {@link XStream} instance is supposed to be thread-safe.
   */
  private static final XStream DEFAULT_XSTREAM = new XStream2();
  private static final SAXParserFactory JAXP = SAXParserFactory.newInstance();
  static {
    JAXP.setNamespaceAware(true);
  }

  private final XStream xs;
  private final File file;

  /**
   * The constructor.<br/>
   * It uses the default {@link XStream}.
   * 
   * @param aFile the file to work with
   */
  public GZIPXMLFile(File aFile) {
    this(DEFAULT_XSTREAM, aFile);
  }

  /**
   * The constructor.
   * 
   * @param anXStream the {@link XStream} to use
   * @param aFile the file to work with
   */
  public GZIPXMLFile(XStream anXStream, File aFile) {
    xs = anXStream;
    file = aFile;
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  private InputStream getInputStream() throws FileNotFoundException, IOException {
    if (file.exists()) {
      LOGGER.fine("Reading " + file);
      return new GZIPInputStream(new FileInputStream(file));
    }
    String tmpFileName = file.getName();
    tmpFileName = tmpFileName.replace(".gz", ".xml");
    File tmpFile = new File(file.getParentFile(), tmpFileName);
    LOGGER.fine("File " + file + " does not exist. Trying " + tmpFile);
    return new FileInputStream(tmpFile);
  }

  /**
   * Loads the contents of this file into a new object.
   * 
   * @return the loaded object
   * @throws IOException in case of problems reading the file
   */
  public Object read() throws IOException {
    Reader tmpReader = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
    try {
      return xs.fromXML(tmpReader);
    } catch (StreamException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (ConversionException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (Error e) {// mostly reflection errors
      throw new IOException2("Unable to read " + file, e);
    } finally {
      tmpReader.close();
    }
  }

  /**
   * Loads the contents of this file into an existing object.
   * 
   * @param anObject the object to load into
   * @return the unmarshalled object. Usually the same as <tt>anObject</tt>, but would be different
   *         if the XML representation is completely new.
   * @throws IOException in case of problems reading the file
   */
  public Object unmarshal(Object anObject) throws IOException {
    Reader tmpReader = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
    try {
      return xs.unmarshal(new XppReader(tmpReader), anObject);
    } catch (StreamException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (ConversionException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (Error e) {// mostly reflection errors
      throw new IOException2("Unable to read " + file, e);
    } finally {
      tmpReader.close();
    }
  }

  /**
   * Writes the given object to this file.
   * 
   * @param anObject the object to write
   * @throws IOException in case of problems writing the file
   */
  public void write(Object anObject) throws IOException {
    mkdirs();
    AtomicGZIPFileWriter tmpWriter = new AtomicGZIPFileWriter(file);
    try {
      tmpWriter.write("<?xml version='1.0' encoding='UTF-8'?>\n");
      xs.toXML(anObject, tmpWriter);
      tmpWriter.commit();
    } catch (StreamException e) {
      throw new IOException2(e);
    } finally {
      tmpWriter.abort();
    }
  }

  /**
   * @return true if this file exists, false otherwise
   */
  public boolean exists() {
    return file.exists();
  }

  /**
   * Deletes this file.
   */
  public void delete() {
    file.delete();
  }

  /**
   * Creates all parent directories.
   */
  public void mkdirs() {
    file.getParentFile().mkdirs();
  }

  /**
   * Opens a {@link Reader} that loads XML.
   * This method uses {@link #sniffEncoding() the right encoding},
   * not just the system default encoding.
   * 
   * @return a {@link Reader} using the right encoding
   * @throws IOException in case of problems reading the file
   */
  public Reader readRaw() throws IOException {
    return new InputStreamReader(getInputStream(), sniffEncoding());
  }

  /**
   * @return the XML file read as a string
   * @throws IOException in case of problems reading the file
   */
  public String asString() throws IOException {
    StringWriter tmpWriter = new StringWriter();
    writeRawTo(tmpWriter);
    return tmpWriter.toString();
  }

  /**
   * Writes the raw XML to the given {@link Writer}.
   * Writer will not be closed by the implementation.
   * 
   * @param aWriter the {@link Writer} to write to
   * @throws IOException in case of problems writing the file
   */
  public void writeRawTo(Writer aWriter) throws IOException {
    Reader tmpReader = readRaw();
    try {
      Util.copyStream(tmpReader, aWriter);
    } finally {
      tmpReader.close();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return file.toString();
  }

  /**
   * Parses the beginning of the file and determines the encoding.
   * 
   * @return the found encoding. always non-null.
   * @throws IOException if failed to detect encoding.
   */
  public String sniffEncoding() throws IOException {
    class Eureka extends SAXException {
      private static final long serialVersionUID = 2323097540149546635L;

      final String encoding;

      public Eureka(String anEncoding) {
        encoding = anEncoding;
      }
    }
    try {
      JAXP.newSAXParser().parse(file, new DefaultHandler() {
        private Locator locator;

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        @Override
        public void setDocumentLocator(Locator aLocator) {
          locator = aLocator;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#startDocument()
         */
        @Override
        public void startDocument() throws SAXException {
          attempt();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
         *      org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String anURI, String aLocalName, String aQName, Attributes anAttributes)
            throws SAXException {
          attempt();
          // if we still haven't found it at the first start element,
          // there's something wrong.
          throw new Eureka(null);
        }

        private void attempt() throws Eureka {
          if (locator == null) {
            return;
          }
          if (locator instanceof Locator2) {
            Locator2 tmpLocator2 = (Locator2) locator;
            String tmpEncoding = tmpLocator2.getEncoding();
            if (tmpEncoding != null) {
              throw new Eureka(tmpEncoding);
            }
          }
        }
      });
      // can't reach here
      throw new AssertionError();
    } catch (Eureka e) {
      if (e.encoding == null) {
        throw new IOException("Failed to detect encoding of " + file);
      }
      return e.encoding;
    } catch (SAXException e) {
      throw new IOException2("Failed to detect encoding of " + file, e);
    } catch (ParserConfigurationException e) {
      throw new AssertionError(e); // impossible
    }
  }
}
