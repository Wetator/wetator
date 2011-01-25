/*
 * Copyright (c) 2008-2011 wetator.org
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

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

/**
 * ContentUtil contains some useful helpers for content conversion handling.
 * 
 * @author rbri
 */
public final class ContentUtil {

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
      return tmpResult.toString();
    } finally {
      tmpDocument.close();
    }
  }

  /**
   * Converts an xls document to string.
   * 
   * @param anInputStream the input
   * @return the normalizes content string
   * @throws IOException in case of io errors
   */
  public static String getXlsContentAsString(final InputStream anInputStream) throws IOException {
    final NormalizedString tmpResult = new NormalizedString();
    final HSSFWorkbook tmpWorkbook = new HSSFWorkbook(anInputStream);

    for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
      final HSSFSheet tmpSheet = tmpWorkbook.getSheetAt(i);
      tmpResult.append("[");
      tmpResult.append(tmpSheet.getSheetName());
      tmpResult.append("] ");

      for (int tmpRowNum = 0; tmpRowNum <= tmpSheet.getLastRowNum(); tmpRowNum++) {
        final HSSFRow tmpRow = tmpSheet.getRow(tmpRowNum);
        if (null != tmpRow) {
          for (int tmpCellNum = 0; tmpCellNum <= tmpRow.getLastCellNum(); tmpCellNum++) {
            final String tmpCellValue = readCellContentAsString(tmpRow, tmpCellNum);
            if (null != tmpCellValue) {
              tmpResult.append(tmpCellValue);
              tmpResult.append(" ");
            }
          }
          tmpResult.append(" ");
        }
      }
    }

    return tmpResult.toString();
  }

  private static String readCellContentAsString(final HSSFRow aRow, final int aColumnsNo) {
    String tmpResult = null;
    HSSFCell tmpCell;
    int tmpCellType;

    tmpCell = aRow.getCell(aColumnsNo);
    if (null == tmpCell) {
      return tmpResult;
    }

    tmpCellType = tmpCell.getCellType();

    switch (tmpCellType) {
      case Cell.CELL_TYPE_BLANK:
        tmpResult = "";
        break;
      case Cell.CELL_TYPE_STRING:
        tmpResult = tmpCell.getRichStringCellValue().getString();
        break;
      case Cell.CELL_TYPE_NUMERIC:
        tmpResult = "" + tmpCell.getNumericCellValue();
        break;

      // deal with the other possible cases
      case Cell.CELL_TYPE_BOOLEAN:
        // ignore
      case Cell.CELL_TYPE_ERROR:
        // ignore
      case Cell.CELL_TYPE_FORMULA:
        // ignore
      default:
        // ignore
    }
    return tmpResult;
  }

  /**
   * Private constructor to be invisible.
   */
  private ContentUtil() {
    super();
  }
}