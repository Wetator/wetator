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


package org.wetator.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.wetator.backend.IBrowser.ContentType;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;

/**
 * ContentUtil contains some useful helpers for content conversion handling.
 * 
 * @author rbri
 */
public final class ContentUtil {
  private static final Log LOG = LogFactory.getLog(ContentUtil.class);

  private static final int MAX_LENGTH = 4000;
  private static final String MORE = " ...";

  /**
   * Converts a text document to string.
   * 
   * @param aContent the input
   * @return the normalizes content string
   */
  public static String getTxtContentAsString(final String aContent) {
    final NormalizedString tmpResult = new NormalizedString(aContent);
    if (tmpResult.length() > MAX_LENGTH) {
      return tmpResult.substring(0, MAX_LENGTH) + MORE;
    }
    return tmpResult.toString();
  }

  /**
   * Converts an InputStream to string.
   * 
   * @param anInputStream the input
   * @param anEncoding the input stream encoding
   * @return the normalizes content string
   * @throws IOException in case of error
   */
  public static String getTxtContentAsString(final InputStream anInputStream, final String anEncoding)
      throws IOException {
    final NormalizedString tmpResult = new NormalizedString();
    final Reader tmpReader = new InputStreamReader(anInputStream, anEncoding);
    final char[] tmpCharBuffer = new char[1024];

    int tmpChars = 0;
    boolean tmpContinue = false;
    do {
      tmpChars = tmpReader.read(tmpCharBuffer);
      tmpResult.append(tmpCharBuffer, tmpChars);
      tmpContinue = tmpChars > 0 && tmpResult.length() <= MAX_LENGTH;
    } while (tmpContinue);
    if (tmpResult.length() > MAX_LENGTH) {
      return tmpResult.substring(0, MAX_LENGTH) + MORE;
    }
    return tmpResult.toString();
  }

  /**
   * Converts a pdf document to string.
   * 
   * @param anInputStream the input
   * @return the normalizes content string
   * @throws IOException in case of io errors
   */
  public static String getPdfContentAsString(final InputStream anInputStream) throws IOException {
    PDDocument tmpDocument;
    tmpDocument = PDDocument.load(anInputStream);
    try {
      final PDFTextStripper tmpStripper = new PDFTextStripper();
      final String tmpContentAsText = tmpStripper.getText(tmpDocument);
      final NormalizedString tmpResult = new NormalizedString(tmpContentAsText);
      if (tmpResult.length() > MAX_LENGTH) {
        return tmpResult.substring(0, MAX_LENGTH) + MORE;
      }
      return tmpResult.toString();
    } finally {
      tmpDocument.close();
    }
  }

  /**
   * Converts a rtf document to string.
   * 
   * @param anInputStream the input
   * @return the normalizes content string
   * @throws IOException in case of io errors
   * @throws BadLocationException if parsing goes wrong
   */
  public static String getRtfContentAsString(final InputStream anInputStream) throws IOException, BadLocationException {
    final RTFEditorKit tmpRtfEditorKit = new RTFEditorKit();
    final Document tmpDocument = tmpRtfEditorKit.createDefaultDocument();
    tmpRtfEditorKit.read(anInputStream, tmpDocument, 0);
    // don't get the whole document
    final int tmpLength = Math.min(tmpDocument.getLength(), MAX_LENGTH);
    final NormalizedString tmpResult = new NormalizedString(tmpDocument.getText(0, tmpLength));
    if (tmpDocument.getLength() > MAX_LENGTH) {
      tmpResult.append(MORE);
    }
    return tmpResult.toString();
  }

  /**
   * Converts an InputStream to string.
   * 
   * @param anInputStream the input
   * @param anEncoding the input stream encoding
   * @param aXlsLocale the locale used for xls formating
   * @return the normalizes content string
   * @throws IOException in case of error
   */
  public static String getZipContentAsString(final InputStream anInputStream, final String anEncoding,
      final Locale aXlsLocale) throws IOException {
    final NormalizedString tmpResult = new NormalizedString();
    final ZipInputStream tmpZipInput = new ZipInputStream(anInputStream);

    ZipEntry tmpZipEntry = tmpZipInput.getNextEntry();
    while (null != tmpZipEntry) {
      tmpResult.append("[");
      tmpResult.append(tmpZipEntry.getName());
      tmpResult.append(" ");
      tmpResult.append(Long.toString(tmpZipEntry.getSize()));
      tmpResult.append("] ");

      final ContentType tmpType = ContentTypeUtil.getContentTypeForFileName(tmpZipEntry.getName());
      if (ContentType.PDF == tmpType) {
        try {
          tmpResult.append(getPdfContentAsString(new CloseIgnoringInputStream(tmpZipInput)));
        } catch (final IOException e) {
          throw new IOException("Can't convert the zipped pdf '" + tmpZipEntry.getName() + "' into text.", e);
        }
      } else if (ContentType.XLS == tmpType) {
        try {
          tmpResult.append(getXlsContentAsString(new CloseIgnoringInputStream(tmpZipInput), aXlsLocale));
        } catch (final IOException e) {
          throw new IOException("Can't convert the zipped xls '" + tmpZipEntry.getName() + "' into text.", e);
        }
      } else if (ContentType.RTF == tmpType) {
        try {
          tmpResult.append(getRtfContentAsString(new CloseIgnoringInputStream(tmpZipInput)));
        } catch (final IOException e) {
          throw new IOException("Can't convert the zipped rtf '" + tmpZipEntry.getName() + "' into text.", e);
        } catch (final BadLocationException e) {
          throw new IOException("Can't convert the zipped rtf '" + tmpZipEntry.getName() + "' into text.", e);
        }
      } else {
        try {
          tmpResult.append(getTxtContentAsString(new CloseIgnoringInputStream(tmpZipInput), anEncoding));
        } catch (final IOException e) {
          throw new IOException("Can't convert the zipped content '" + tmpZipEntry.getName() + "' into text.", e);
        }
      }

      tmpZipEntry = tmpZipInput.getNextEntry();
      if (null != tmpZipEntry) {
        tmpResult.append(" ");
      }
    }

    return tmpResult.toString();
  }

  /**
   * Converts an xls document to string.
   * 
   * @param anInputStream the input
   * @param aLocale the locale for formating
   * @return the normalizes content string
   * @throws IOException in case of io errors
   */
  public static String getXlsContentAsString(final InputStream anInputStream, final Locale aLocale) throws IOException {
    final NormalizedString tmpResult = new NormalizedString();
    final HSSFWorkbook tmpWorkbook = new HSSFWorkbook(anInputStream);
    final FormulaEvaluator tmpFormulaEvaluator = tmpWorkbook.getCreationHelper().createFormulaEvaluator();

    Locale tmpLocale = aLocale;
    if (null == tmpLocale) {
      tmpLocale = Locale.getDefault();
    }

    for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
      final HSSFSheet tmpSheet = tmpWorkbook.getSheetAt(i);
      tmpResult.append("[");
      tmpResult.append(tmpSheet.getSheetName());
      tmpResult.append("] ");

      for (int tmpRowNum = 0; tmpRowNum <= tmpSheet.getLastRowNum(); tmpRowNum++) {
        final HSSFRow tmpRow = tmpSheet.getRow(tmpRowNum);
        if (null != tmpRow) {
          for (int tmpCellNum = 0; tmpCellNum <= tmpRow.getLastCellNum(); tmpCellNum++) {
            final String tmpCellValue = readCellContentAsString(tmpRow, tmpCellNum, tmpFormulaEvaluator, tmpLocale);
            if (null != tmpCellValue) {
              tmpResult.append(tmpCellValue);
              tmpResult.append(" ");
            }
          }

          // check after each row
          if (tmpResult.length() > MAX_LENGTH) {
            return tmpResult.substring(0, MAX_LENGTH) + MORE;
          }

          tmpResult.append(" ");
        }
      }
    }

    return tmpResult.toString();
  }

  /**
   * Reads the content of an excel cell and converts it into the string
   * visible in the excel sheet.
   * 
   * @param aRow the row
   * @param aColumnsNo the column
   * @param aFormulaEvaluator the formula Evaluator
   * @param aLocale used for parsing and formating
   * @return the display string
   */
  public static String readCellContentAsString(final HSSFRow aRow, final int aColumnsNo,
      final FormulaEvaluator aFormulaEvaluator, final Locale aLocale) {
    final HSSFCell tmpCell = aRow.getCell(aColumnsNo);
    if (null == tmpCell) {
      return null;
    }

    final DataFormatter tmpDataFormatter = new DataFormatter(aLocale);
    try {
      final String tmpResult = tmpDataFormatter.formatCellValue(tmpCell, aFormulaEvaluator);
      return tmpResult;
    } catch (final NotImplementedException e) {
      final StringBuilder tmpMsg = new StringBuilder(e.getMessage());
      if (null != e.getCause()) {
        tmpMsg.append(" (");
        tmpMsg.append(e.getCause().toString());
        tmpMsg.append(')');
      }
      LOG.error(tmpMsg.toString());
      final String tmpResult = tmpDataFormatter.formatCellValue(tmpCell, null);
      return tmpResult;
    }
  }

  /**
   * Tests if the text is 'readable'.
   * 
   * @param aText the input
   * @return true, if the input contains enough characters
   */
  public static boolean isTxt(final String aText) {
    int tmpCharCount = 0;
    for (int i = 0; i < aText.length(); i++) {
      final char tmpChar = aText.charAt(i);
      if (Character.isLetterOrDigit(tmpChar)) {
        tmpCharCount++;
      }
    }
    return (tmpCharCount * 1d / aText.length()) > 0.7;
  }

  /**
   * This tries to determine the locale based on the 'accept-language' header. <br>
   * If the submitted locale is unknown (ISO639) then this returns null. <br>
   * 
   * @param anAcceptLanguageHeader the header value
   * @return the locale or null if the provided information is not parsable
   */
  public static Locale determineLocaleFromRequestHeader(final String anAcceptLanguageHeader) {
    if (null == anAcceptLanguageHeader) {
      return null;
    }

    final Iterator<String> tmpLanguages = StringUtil.extractStrings(anAcceptLanguageHeader, ",", -1).iterator();
    while (tmpLanguages.hasNext()) {
      final List<String> tmpLanguageDescriptor = StringUtil.extractStrings(tmpLanguages.next(), ";", -1);
      if (tmpLanguageDescriptor.size() < 1) {
        return null;
      }

      final String tmpLocaleString = tmpLanguageDescriptor.get(0);

      final List<String> tmpLocaleDescriptor = StringUtil.extractStrings(tmpLocaleString, "-", -1);
      if (tmpLocaleDescriptor.size() < 1) {
        break;
      }

      final String[] tmpISO639 = Locale.getISOLanguages();
      final String tmpLanguage = tmpLocaleDescriptor.get(0).toLowerCase(Locale.ENGLISH);
      for (int i = 0; i < tmpISO639.length; i++) {
        if (tmpISO639[i].equals(tmpLanguage)) {
          // found a valid language
          // check the country
          String tmpCountry3166 = "";
          if (tmpLocaleDescriptor.size() > 1) {
            final String tmpCountry = tmpLocaleDescriptor.get(1).toUpperCase(Locale.ENGLISH);
            final String[] tmpISO3166 = Locale.getISOCountries();
            for (int j = 0; j < tmpISO3166.length; j++) {
              if (tmpISO3166[j].equals(tmpCountry)) {
                tmpCountry3166 = tmpCountry;
              }
            }
          }
          return new Locale(tmpLanguage, tmpCountry3166);
        }
      }
    }
    return null;
  }

  /**
   * Private constructor to be invisible.
   */
  private ContentUtil() {
    super();
  }

  /**
   * Helper for reading from zip input stream.
   */
  private static final class CloseIgnoringInputStream extends FilterInputStream {

    private CloseIgnoringInputStream(final InputStream anInputStream) {
      super(anInputStream);
    }

    @Override
    public void close() throws IOException {
      // ignore
    }
  }
}