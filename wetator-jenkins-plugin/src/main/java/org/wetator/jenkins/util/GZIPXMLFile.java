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

  public GZIPXMLFile(File file) {
    this(DEFAULT_XSTREAM, file);
  }

  public GZIPXMLFile(XStream xs, File file) {
    this.xs = xs;
    this.file = file;
  }

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
   */
  public Object read() throws IOException {
    Reader r = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
    try {
      return xs.fromXML(r);
    } catch (StreamException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (ConversionException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (Error e) {// mostly reflection errors
      throw new IOException2("Unable to read " + file, e);
    } finally {
      r.close();
    }
  }

  /**
   * Loads the contents of this file into an existing object.
   * 
   * @return
   *         The unmarshalled object. Usually the same as <tt>o</tt>, but would be different
   *         if the XML representation is completely new.
   */
  public Object unmarshal(Object o) throws IOException {
    Reader r = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
    try {
      return xs.unmarshal(new XppReader(r), o);
    } catch (StreamException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (ConversionException e) {
      throw new IOException2("Unable to read " + file, e);
    } catch (Error e) {// mostly reflection errors
      throw new IOException2("Unable to read " + file, e);
    } finally {
      r.close();
    }
  }

  public void write(Object o) throws IOException {
    mkdirs();
    AtomicGZIPFileWriter w = new AtomicGZIPFileWriter(file);
    try {
      w.write("<?xml version='1.0' encoding='UTF-8'?>\n");
      xs.toXML(o, w);
      w.commit();
    } catch (StreamException e) {
      throw new IOException2(e);
    } finally {
      w.abort();
    }
  }

  public boolean exists() {
    return file.exists();
  }

  public void delete() {
    file.delete();
  }

  public void mkdirs() {
    file.getParentFile().mkdirs();
  }

  @Override
  public String toString() {
    return file.toString();
  }

  /**
   * Opens a {@link Reader} that loads XML.
   * This method uses {@link #sniffEncoding() the right encoding},
   * not just the system default encoding.
   */
  public Reader readRaw() throws IOException {
    return new InputStreamReader(getInputStream(), sniffEncoding());
  }

  /**
   * Returns the XML file read as a string.
   */
  public String asString() throws IOException {
    StringWriter w = new StringWriter();
    writeRawTo(w);
    return w.toString();
  }

  /**
   * Writes the raw XML to the given {@link Writer}.
   * Writer will not be closed by the implementation.
   */
  public void writeRawTo(Writer w) throws IOException {
    Reader r = readRaw();
    try {
      Util.copyStream(r, w);
    } finally {
      r.close();
    }
  }

  /**
   * Parses the beginning of the file and determines the encoding.
   * 
   * @throws IOException
   *         if failed to detect encoding.
   * @return
   *         always non-null.
   */
  public String sniffEncoding() throws IOException {
    class Eureka extends SAXException {
      private static final long serialVersionUID = 2323097540149546635L;

      final String encoding;

      public Eureka(String encoding) {
        this.encoding = encoding;
      }
    }
    try {
      JAXP.newSAXParser().parse(file, new DefaultHandler() {
        private Locator loc;

        @Override
        public void setDocumentLocator(Locator locator) {
          this.loc = locator;
        }

        @Override
        public void startDocument() throws SAXException {
          attempt();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          attempt();
          // if we still haven't found it at the first start element,
          // there's something wrong.
          throw new Eureka(null);
        }

        private void attempt() throws Eureka {
          if (loc == null)
            return;
          if (loc instanceof Locator2) {
            Locator2 loc2 = (Locator2) loc;
            String e = loc2.getEncoding();
            if (e != null)
              throw new Eureka(e);
          }
        }
      });
      // can't reach here
      throw new AssertionError();
    } catch (Eureka e) {
      if (e.encoding == null)
        throw new IOException("Failed to detect encoding of " + file);
      return e.encoding;
    } catch (SAXException e) {
      throw new IOException2("Failed to detect encoding of " + file, e);
    } catch (ParserConfigurationException e) {
      throw new AssertionError(e); // impossible
    }
  }
}
