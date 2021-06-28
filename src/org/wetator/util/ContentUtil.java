/*
 * Copyright (c) 2008-2021 wetator.org
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
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.wetator.backend.IBrowser.ContentType;
import org.wetator.backend.htmlunit.util.ContentTypeUtil;

/**
 * ContentUtil contains some useful helpers for content conversion handling.
 *
 * @author rbri
 * @author frank.danek
 */
public final class ContentUtil {
  private static final Logger LOG = LogManager.getLogger(ContentUtil.class);

  private static final String MORE = " ...";

  /**
   * Converts a text document into a (normalized) string.
   *
   * @param aContent the input
   * @param aMaxLength the maximum length
   * @return the normalized content string
   */
  public static String getTxtContentAsString(final String aContent, final int aMaxLength) {
    final NormalizedString tmpResult = new NormalizedString(aContent);
    if (tmpResult.length() > aMaxLength) {
      return tmpResult.substring(0, aMaxLength) + MORE;
    }
    return tmpResult.toString();
  }

  /**
   * Converts a text document from an {@link InputStream} into a (normalized) string.
   *
   * @param anInputStream the input
   * @param aCharset the input stream encoding
   * @param aMaxLength the maximum length
   * @return the normalized content string
   * @throws IOException in case of errors
   */
  public static String getTxtContentAsString(final InputStream anInputStream, final Charset aCharset,
      final int aMaxLength) throws IOException {
    if (anInputStream == null) {
      return "";
    }

    final NormalizedString tmpResult = new NormalizedString();
    final Reader tmpReader = new InputStreamReader(anInputStream, aCharset); // NOPMD
    final char[] tmpCharBuffer = new char[1024];

    boolean tmpContinue;
    do {
      final int tmpChars = tmpReader.read(tmpCharBuffer);
      tmpResult.append(tmpCharBuffer, tmpChars);
      tmpContinue = tmpChars > 0 && tmpResult.length() <= aMaxLength;
    } while (tmpContinue);
    if (tmpResult.length() > aMaxLength) {
      return tmpResult.substring(0, aMaxLength) + MORE;
    }
    return tmpResult.toString();
  }

  /**
   * Retrieves the content of a PDF document as a (normalized) string.
   *
   * @param anInputStream the input
   * @param aMaxLength the maximum length
   * @return the normalized content string
   * @throws IOException in case of errors
   */
  public static String getPdfContentAsString(final InputStream anInputStream, final int aMaxLength) throws IOException {
    if (anInputStream == null) {
      return "";
    }

    try (PDDocument tmpDocument = PDDocument.load(anInputStream)) {
      final AccessPermission tmpPermissions = tmpDocument.getCurrentAccessPermission();
      if (!tmpPermissions.canExtractContent()) {
        throw new IOException("Content extraction forbidden for the given PDF document.");
      }

      final PDFTextStripper tmpStripper = new PDFTextStripper();
      final String tmpContentAsText = tmpStripper.getText(tmpDocument);
      final NormalizedString tmpResult = new NormalizedString(tmpContentAsText);
      if (tmpResult.length() > aMaxLength) {
        return tmpResult.substring(0, aMaxLength) + MORE;
      }
      return tmpResult.toString();
    }
  }

  /**
   * Retrieves the title of (the metadata of) a PDF document as a (normalized) string.
   *
   * @param anInputStream the input
   * @return the normalized title string
   * @throws IOException in case of errors
   */
  public static String getPdfTitleAsString(final InputStream anInputStream) throws IOException {
    if (anInputStream == null) {
      return "";
    }

    try (PDDocument tmpDocument = PDDocument.load(anInputStream)) {
      final PDDocumentInformation tmpInfo = tmpDocument.getDocumentInformation();
      if (null != tmpInfo) {
        final NormalizedString tmpResult = new NormalizedString(tmpInfo.getTitle());
        return tmpResult.toString();
      }
      return "";
    }
  }

  /**
   * Retrieves the content of an RTF document as a (normalized) string.
   *
   * @param anInputStream the input
   * @param aMaxLength the maximum length
   * @return the normalized content string
   * @throws IOException in case of errors
   * @throws BadLocationException if parsing goes wrong
   */
  public static String getRtfContentAsString(final InputStream anInputStream, final int aMaxLength)
      throws IOException, BadLocationException {
    if (anInputStream == null) {
      return "";
    }

    final RTFEditorKit tmpRtfEditorKit = new RTFEditorKit();
    final Document tmpDocument = tmpRtfEditorKit.createDefaultDocument();
    tmpRtfEditorKit.read(anInputStream, tmpDocument, 0);
    // don't get the whole document
    int tmpLength = Math.min(tmpDocument.getLength(), aMaxLength);
    NormalizedString tmpResult = new NormalizedString(tmpDocument.getText(0, tmpLength));
    while (tmpResult.length() <= aMaxLength && tmpLength < tmpDocument.getLength()) {
      tmpResult.append(tmpDocument.getText(tmpLength, 1));
      tmpLength++;
    }
    if (tmpResult.length() > aMaxLength) {
      tmpResult = new NormalizedString(tmpResult.substring(0, tmpResult.length() - 1));
    }
    if (tmpDocument.getLength() > tmpLength) {
      tmpResult.append(MORE);
    }
    return tmpResult.toString();
  }

  /**
   * Retrieves the content of a Word document as a (normalized) string.
   *
   * @param anInputStream the input
   * @param aMaxLength the maximum length
   * @return the normalized content string
   * @throws IOException in case of errors
   * @throws InvalidFormatException in case of file has not the expected format
   */
  public static String getWordContentAsString(final InputStream anInputStream, final int aMaxLength)
      throws IOException, InvalidFormatException {
    if (anInputStream == null) {
      return "";
    }

    try (POITextExtractor tmpExtractor = ExtractorFactory.createExtractor(anInputStream)) {
      // the ExtractorFactory supports all file formats included in POI
      // but here we only want to support docx and doc -> we have to check ourself
      if (!(tmpExtractor instanceof XWPFWordExtractor || tmpExtractor instanceof WordExtractor)) {
        throw new InvalidFormatException("Your InputStream was not a Word document");
      }

      NormalizedString tmpResult = new NormalizedString(tmpExtractor.getText());

      if (tmpResult.length() <= aMaxLength) {
        return tmpResult.toString();
      }

      tmpResult = new NormalizedString(tmpResult.substring(0, aMaxLength));
      tmpResult.append(MORE);
      return tmpResult.toString();
    } catch (final IllegalArgumentException | OpenXML4JException | XmlException e) {
      if (e.getCause() instanceof InvalidFormatException) {
        throw (InvalidFormatException) e.getCause();
      }
      final InvalidFormatException tmpEx = new InvalidFormatException(e.getMessage());
      tmpEx.initCause(e);
      throw tmpEx;
    }
  }

  /**
   * Retrieves the content of an Excel document as a (normalized) string.
   *
   * @param anInputStream the input
   * @param aLocale the locale for formatting
   * @param aMaxLength the maximum length
   * @return the normalized content string
   * @throws IOException in case of errors
   * @throws InvalidFormatException in case of file has not the expected format
   */
  public static String getExcelContentAsString(final InputStream anInputStream, final Locale aLocale,
      final int aMaxLength) throws IOException, InvalidFormatException {
    if (anInputStream == null) {
      return "";
    }

    final NormalizedString tmpResult = new NormalizedString();

    try (Workbook tmpWorkbook = WorkbookFactory.create(anInputStream)) {
      final FormulaEvaluator tmpFormulaEvaluator = tmpWorkbook.getCreationHelper().createFormulaEvaluator();

      Locale tmpLocale = aLocale;
      if (null == tmpLocale) {
        tmpLocale = Locale.getDefault();
      }

      for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
        final Sheet tmpSheet = tmpWorkbook.getSheetAt(i);
        tmpResult.append("[");
        tmpResult.append(tmpSheet.getSheetName());
        tmpResult.append("] ");

        for (int tmpRowNum = 0; tmpRowNum <= tmpSheet.getLastRowNum(); tmpRowNum++) {
          final Row tmpRow = tmpSheet.getRow(tmpRowNum);
          if (null != tmpRow) {
            for (int tmpCellNum = 0; tmpCellNum <= tmpRow.getLastCellNum(); tmpCellNum++) {
              final String tmpCellValue = readCellContentAsString(tmpRow, tmpCellNum, tmpFormulaEvaluator, tmpLocale);
              if (null != tmpCellValue) {
                tmpResult.append(tmpCellValue);
                tmpResult.append(" ");
              }
            }

            // check after each row
            if (tmpResult.length() > aMaxLength) {
              return tmpResult.substring(0, aMaxLength) + MORE;
            }

            tmpResult.append(" ");
          }
        }
      }
    } catch (final IllegalArgumentException | POIXMLException e) {
      if (e.getCause() instanceof InvalidFormatException) {
        throw (InvalidFormatException) e.getCause();
      }
      final InvalidFormatException tmpEx = new InvalidFormatException(e.getMessage());
      tmpEx.initCause(e);
      throw tmpEx;
    }
    return tmpResult.toString();
  }

  /**
   * Reads the content of an Excel cell and converts it into the string
   * visible in the Excel sheet.
   *
   * @param aRow the row
   * @param aColumnNo the column
   * @param aFormulaEvaluator the {@link FormulaEvaluator}
   * @param aLocale used for parsing and formatting
   * @return the displayed string
   */
  public static String readCellContentAsString(final Row aRow, final int aColumnNo,
      final FormulaEvaluator aFormulaEvaluator, final Locale aLocale) {
    final Cell tmpCell = aRow.getCell(aColumnNo);
    if (null == tmpCell) {
      return null;
    }

    final DataFormatter tmpDataFormatter = new DataFormatter(aLocale);
    try {
      return tmpDataFormatter.formatCellValue(tmpCell, aFormulaEvaluator);
    } catch (final NotImplementedException e) {
      final StringBuilder tmpMsg = new StringBuilder(e.getMessage());
      if (null != e.getCause()) {
        tmpMsg.append(" (").append(e.getCause().toString()).append(')');
      }
      LOG.error(tmpMsg.toString());
      return tmpDataFormatter.formatCellValue(tmpCell, null);
    }
  }

  /**
   * Retrieves the content of a ZIP file as a (normalized) string.
   *
   * @param anInputStream the input
   * @param aCharset the input stream encoding
   * @param anExcelLocale the locale used for Excel formating (if a content is an Excel document)
   * @param aMaxLength the maximum length of each files content
   * @return the normalized content string
   * @throws IOException in case of error
   */
  public static String getZipContentAsString(final InputStream anInputStream, final Charset aCharset,
      final Locale anExcelLocale, final int aMaxLength) throws IOException {
    final NormalizedString tmpResult = new NormalizedString();
    final ZipInputStream tmpZipInput = new ZipInputStream(anInputStream); // NOPMD

    ZipEntry tmpZipEntry = tmpZipInput.getNextEntry();
    while (null != tmpZipEntry) {
      tmpResult.append("[");
      tmpResult.append(tmpZipEntry.getName());
      tmpResult.append("] ");

      final ContentType tmpType = ContentTypeUtil.getContentTypeForFileName(tmpZipEntry.getName());
      if (ContentType.PDF == tmpType) {
        try {
          tmpResult.append(getPdfContentAsString(new CloseIgnoringInputStream(tmpZipInput), aMaxLength));
        } catch (final IOException e) {
          throw new IOException(
              "Can't convert the zipped pdf '" + tmpZipEntry.getName() + "' into text (reason: " + e.toString() + ").");
        }
      } else if (ContentType.XLS == tmpType || ContentType.XLSX == tmpType) {
        try {
          tmpResult
              .append(getExcelContentAsString(new CloseIgnoringInputStream(tmpZipInput), anExcelLocale, aMaxLength));
        } catch (final IOException | InvalidFormatException e) {
          throw new IOException(
              "Can't convert the zipped xls '" + tmpZipEntry.getName() + "' into text (reason: " + e.toString() + ").");
        }
      } else if (ContentType.DOC == tmpType || ContentType.DOCX == tmpType) {
        try {
          tmpResult.append(getWordContentAsString(new CloseIgnoringInputStream(tmpZipInput), aMaxLength));
        } catch (final IOException | InvalidFormatException e) {
          throw new IOException(
              "Can't convert the zipped doc '" + tmpZipEntry.getName() + "' into text (reason: " + e.toString() + ").");
        }
      } else if (ContentType.RTF == tmpType) {
        try {
          tmpResult.append(getRtfContentAsString(new CloseIgnoringInputStream(tmpZipInput), aMaxLength));
        } catch (final IOException | BadLocationException e) {
          throw new IOException(
              "Can't convert the zipped rtf '" + tmpZipEntry.getName() + "' into text (reason: " + e.toString() + ").");
        }
      } else {
        try {
          tmpResult.append(getTxtContentAsString(new CloseIgnoringInputStream(tmpZipInput), aCharset, aMaxLength));
        } catch (final IOException e) {
          throw new IOException("Can't convert the zipped content '" + tmpZipEntry.getName() + "' into text (reason: "
              + e.toString() + ").");
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
   * Tests if the text is 'readable'.
   *
   * @param aText the input
   * @return <code>true</code>, if the input contains enough characters
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
   * This tries to determine the locale based on the 'accept-language' header.<br>
   * If the submitted locale is unknown (ISO639) then this returns <code>null</code>.<br>
   * <b>Warning!</b> Currently ignores the given weights (if any).
   *
   * @param anAcceptLanguageHeader the header value
   * @return the {@link Locale} or <code>null</code> if the provided information is not parsable
   */
  public static Locale determineLocaleFromRequestHeader(final String anAcceptLanguageHeader) {
    if (null == anAcceptLanguageHeader) {
      return null;
    }

    final Iterator<String> tmpLanguages = StringUtil.extractStrings(anAcceptLanguageHeader, ",", -1).iterator();
    while (tmpLanguages.hasNext()) {
      final List<String> tmpLanguageDescriptor = StringUtil.extractStrings(tmpLanguages.next(), ";", -1);
      if (tmpLanguageDescriptor.isEmpty()) {
        return null;
      }

      final String tmpLocaleString = tmpLanguageDescriptor.get(0);

      final List<String> tmpLocaleDescriptor = StringUtil.extractStrings(tmpLocaleString, "-", -1);
      if (tmpLocaleDescriptor.isEmpty()) {
        break;
      }

      final String[] tmpISO639 = Locale.getISOLanguages();
      final String tmpLanguage = tmpLocaleDescriptor.get(0).toLowerCase(Locale.ENGLISH);
      for (final String tmpISO639Language : tmpISO639) {
        if (tmpISO639Language.equals(tmpLanguage)) {
          // found a valid language
          // check the country
          String tmpCountry3166 = "";
          if (tmpLocaleDescriptor.size() > 1) {
            final String tmpCountry = tmpLocaleDescriptor.get(1).toUpperCase(Locale.ENGLISH);
            final String[] tmpISO3166 = Locale.getISOCountries();
            for (final String tmpISO3166Country : tmpISO3166) {
              if (tmpISO3166Country.equals(tmpCountry)) {
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
