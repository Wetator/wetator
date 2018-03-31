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


package org.wetator.progresslistener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class transforms the output.
 *
 * @author rbri
 * @author frank.danek
 */
public final class XSLTransformer {
  private static final Logger LOG = LogManager.getLogger(XSLTransformer.class);

  private static final String RESOURCES_DIRECTORY = "resources";
  private File xmlResultFile;

  /**
   * The constructor.
   *
   * @param aXMLResultFile the name of the report xml file
   */
  public XSLTransformer(final File aXMLResultFile) {
    xmlResultFile = aXMLResultFile;
  }

  /**
   * Transforms the result xml file to various output files. The stylesheets are
   * read from the configured location.
   *
   * @param aListOfXslFileNames the names of the xsl files for transformation
   * @param anOutputDirectory the directory to write to
   */
  public void transform(final Iterable<String> aListOfXslFileNames, final File anOutputDirectory) {
    for (final String tmpXslFileName : aListOfXslFileNames) {
      final File tmpXslFile = new File(tmpXslFileName);

      // TODO determine file type (based on template name)
      final File tmpResultFile = new File(anOutputDirectory, tmpXslFile.getName() + ".html");

      try {
        final StreamSource tmpXlsStreamSource = new StreamSource(tmpXslFile);

        final TransformerFactory tmpTransformerFactory = TransformerFactory.newInstance();
        tmpTransformerFactory.setErrorListener(new ErrorListener() {
          @Override
          public void warning(final TransformerException anException) throws TransformerException {
            LOG.warn("Problem parsing XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath()) + "' ("
                + anException.getMessage() + ").");
          }

          @Override
          public void fatalError(final TransformerException anException) throws TransformerException {
            LOG.error("Parsing XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath()) + "' failed ("
                + anException.getMessage() + ").");
          }

          @Override
          public void error(final TransformerException anException) throws TransformerException {
            LOG.error("Problem parsing XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath())
                + "' failed (" + anException.getMessage() + ").");
          }
        });
        final Transformer tmpTransformer = tmpTransformerFactory.newTransformer(tmpXlsStreamSource);
        // if building the transformer fails, then
        // we got null here (instead of an exception)
        if (null == tmpTransformer) {
          LOG.error("Problem parsing XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath())
              + "'. Aborting.");
          return;
        }

        final StreamSource tmpXmlStreamSource = new StreamSource(xmlResultFile);

        try (final OutputStream tmpFileOutputStream = Files.newOutputStream(tmpResultFile.toPath());
            final BufferedOutputStream tmpBufferedOutputStream = new BufferedOutputStream(tmpFileOutputStream)) {
          final StreamResult tmpStreamResult = new StreamResult(tmpBufferedOutputStream);

          tmpTransformer.transform(tmpXmlStreamSource, tmpStreamResult);
          tmpBufferedOutputStream.close();

          copyImages(tmpXslFile.getParentFile(), anOutputDirectory);

          LOG.info("Report written to " + FilenameUtils.normalize(tmpResultFile.getAbsolutePath()));
        }
      } catch (final TransformerConfigurationException e) {
        LOG.error(
            "Problem loading XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath()) + "'. Aborting.",
            e);
      } catch (final TransformerException e) {
        LOG.error(
            "Problem applying XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath()) + "'. Aborting.",
            e);
      } catch (final IOException e) {
        LOG.error(
            "Problem writing Report '" + FilenameUtils.normalize(tmpResultFile.getAbsolutePath()) + "'. Aborting.", e);
      } catch (final Exception e) {
        LOG.error(
            "Problem applying XSL-Template '" + FilenameUtils.normalize(tmpXslFile.getAbsolutePath()) + "'. Aborting.",
            e);
      }
    }
  }

  /**
   * This method is called after the transformation are done.
   * It copies the folder "images" from the folder,
   * where the stylesheet is located to the folder where
   * the output is written to.
   * If "images" already exists, nothing is copied.
   *
   * @param aSourceDir the directory to copy from
   * @param aTargetDir the directory to copy to
   * @throws IOException in case of problems
   */
  public void copyImages(final File aSourceDir, final File aTargetDir) throws IOException {
    final File tmpSourceDir = new File(aSourceDir, RESOURCES_DIRECTORY);
    final File tmpTargetDir = new File(aTargetDir, RESOURCES_DIRECTORY);

    copyFiles(tmpSourceDir, tmpTargetDir);
  }

  /**
   * Copies the content from one folder to another folder.
   *
   * @param aSourceDir the directory to copy from
   * @param aTargetDir the directory to copy to
   * @throws IOException in case of problems
   */
  private void copyFiles(final File aSourceDir, final File aTargetDir) throws IOException {
    if (aTargetDir.exists()) {
      // do not copy anything
      return;
    }

    if (!aTargetDir.mkdirs()) {
      LOG.error("Can't create '" + FilenameUtils.normalize(aTargetDir.getAbsolutePath()) + "'.");
      return;
    }

    final File[] tmpImageFiles = aSourceDir.listFiles();
    if (null == tmpImageFiles) {
      return;
    }

    // copy each file from the list
    for (final File tmpSourceFile : tmpImageFiles) {
      final String tmpSourceFileName = tmpSourceFile.getName();
      if (null != tmpSourceFileName && tmpSourceFileName.startsWith(".")) { // NOPMD
        // ignore files starting with '.'
      } else if (tmpSourceFile.isDirectory()) {
        final File tmpTargetSubDir = new File(aTargetDir, tmpSourceFile.getName());
        copyFiles(tmpSourceFile, tmpTargetSubDir);
      } else {
        final File tmpTargetFile = new File(aTargetDir, tmpSourceFile.getName());

        try (InputStream tmpIn = Files.newInputStream(tmpSourceFile.toPath());
            OutputStream tmpOut = Files.newOutputStream(tmpTargetFile.toPath())) {
          final byte[] tmpBuffer = new byte[1024];
          int tmpBytes = 0;
          while ((tmpBytes = tmpIn.read(tmpBuffer)) > -1) {
            tmpOut.write(tmpBuffer, 0, tmpBytes);
          }
        } catch (final IOException e) {
          LOG.error("Can't copy '" + FilenameUtils.normalize(tmpSourceFile.getAbsolutePath()) + "'. File ignored.", e);
        }
      }
    }
  }
}
