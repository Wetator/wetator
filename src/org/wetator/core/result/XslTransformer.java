/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.core.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class transforms the output.
 * 
 * @author rbri
 */
public final class XslTransformer {
  private static final Log LOG = LogFactory.getLog(XslTransformer.class);

  private static final String IMAGES_DIRECTORY = "images";
  private File wetResultFile;

  /**
   * Constructor
   * 
   * @param aWetResultFile the name of the report xml file
   */
  public XslTransformer(final File aWetResultFile) {
    wetResultFile = aWetResultFile;
  }

  /**
   * Transforms the result xml file to various output files. The stylesheets are
   * read from the configured location.
   * 
   * @param aListOfXslFileNames the names of the xsl files for transformation
   * @param anOutputDirectory the directory to write to
   */
  public void transform(final Iterable<String> aListOfXslFileNames, final File anOutputDirectory) {
    for (String tmpXslFileName : aListOfXslFileNames) {
      final File tmpXslFile = new File(tmpXslFileName);

      // TODO determine file type (based on template name)
      final File tmpResultFile = new File(anOutputDirectory, tmpXslFile.getName() + ".html");

      try {
        final StreamSource tmpXlsStreamSource = new StreamSource(tmpXslFile);
        final Transformer tmpTransformer = TransformerFactory.newInstance().newTransformer(tmpXlsStreamSource);

        final StreamSource tmpXmlStreamSource = new StreamSource(wetResultFile);

        final FileWriter tmpFileWriter = new FileWriter(tmpResultFile);
        final BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        final StreamResult tmpStreamResult = new StreamResult(tmpBufferedWriter);

        tmpTransformer.transform(tmpXmlStreamSource, tmpStreamResult);
        tmpBufferedWriter.close();

        copyImages(tmpXslFile.getParentFile(), anOutputDirectory);

        LOG.info("Report written to " + tmpResultFile.getAbsolutePath());
      } catch (final TransformerConfigurationException e) {
        LOG.error("Problem loading XSL-Template '" + tmpXslFile.getAbsolutePath() + "'. Aborting.", e);
      } catch (final TransformerException e) {
        LOG.error("Problem applying XSL-Template '" + tmpXslFile.getAbsolutePath() + "'. Aborting.", e);
      } catch (final IOException e) {
        LOG.error("Problem writing Report '" + tmpResultFile.getAbsolutePath() + "'. Aborting.", e);
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
    final File tmpSourceDir = new File(aSourceDir, IMAGES_DIRECTORY);
    final File tmpTargetDir = new File(aTargetDir, IMAGES_DIRECTORY);

    copyFiles(tmpSourceDir, tmpTargetDir);
  }

  /**
   * Copies the content from one folder to another folder.
   * 
   * @param aSourceDir the directory to copy from
   * @param aTargetDir the directory to copy to
   * @throws IOException in case of problems
   */
  protected void copyFiles(final File aSourceDir, final File aTargetDir) throws IOException {
    if (aTargetDir.exists()) {
      // do not copy anything
      return;
    }

    aTargetDir.mkdirs();

    final File[] tmpImageFiles = aSourceDir.listFiles();
    if (null == tmpImageFiles) {
      return;
    }

    // copy each file from the list
    for (int i = 0; i < tmpImageFiles.length; i++) {
      final File tmpSourceFile = tmpImageFiles[i];

      final String tmpSourceFileName = tmpSourceFile.getName();
      if (null != tmpSourceFileName && tmpSourceFileName.startsWith(".")) {
        // ignore files starting with '.'
      } else if (tmpSourceFile.isDirectory()) {
        final File tmpTargetSubDir = new File(aTargetDir, tmpSourceFile.getName());
        copyFiles(tmpSourceFile, tmpTargetSubDir);
      } else {
        final File tmpTargetFile = new File(aTargetDir, tmpSourceFile.getName());

        try {
          final FileInputStream tmpIn = new FileInputStream(tmpSourceFile);
          try {
            final FileOutputStream tmpOut = new FileOutputStream(tmpTargetFile);
            try {
              final byte[] tmpBuffer = new byte[1024];
              int tmpBytes = 0;
              while ((tmpBytes = tmpIn.read(tmpBuffer)) > -1) {
                tmpOut.write(tmpBuffer, 0, tmpBytes);
              }
            } finally {
              tmpOut.close();
            }
          } finally {
            tmpIn.close();
          }
        } catch (final IOException e) {
          LOG.error("Can't copy '" + tmpSourceFile.getAbsolutePath() + "'. File ignored.", e);
        }
      }
    }
  }
}
