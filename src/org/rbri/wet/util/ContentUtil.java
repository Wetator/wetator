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

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.rbri.wet.exception.AssertionFailedException;

/**
 * ContentUtil contains some useful helpers for content conversion handling.
 *
 * @author rbri
 */
public class ContentUtil {

    /**
     * Converts a pdf document to string
     *
     * @param anInputStream the input
     * @return a String
     * @throws AssertionFailedException if conversion fails
     * @throws IOException
     */
    public static String getPdfContentAsString(InputStream anInputStream) throws IOException {
        PDDocument tmpDocument;
        tmpDocument = PDDocument.load(anInputStream);
        try {
            PDFTextStripper tmpStripper = new PDFTextStripper();
            String tmpContentAsText = tmpStripper.getText(tmpDocument);
            NormalizedContent tmpResult = new NormalizedContent();
            tmpResult.append(tmpContentAsText);
            return tmpResult.toString().trim();
        } finally {
            tmpDocument.close();
        }
    }

    /**
     * Converts a xls document to string
     *
     * @param anInputStream the input
     * @return a String
     * @throws AssertionFailedException if conversion fails
     * @throws IOException
     */
    public static String getXlsContentAsString(InputStream anInputStream) throws IOException {
        NormalizedContent tmpResult = new NormalizedContent();
        HSSFWorkbook tmpWorkbook = new HSSFWorkbook(anInputStream);

        for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
            HSSFSheet tmpSheet = tmpWorkbook.getSheetAt(i);
            tmpResult.append("[");
            tmpResult.append(tmpSheet.getSheetName());
            tmpResult.append("] ");

            for (int tmpRowNum = 0; tmpRowNum <= tmpSheet.getLastRowNum(); tmpRowNum++) {
                HSSFRow tmpRow = tmpSheet.getRow(tmpRowNum);
                if (null != tmpRow) {
                    for (int tmpCellNum = 0; tmpCellNum <= tmpRow.getLastCellNum(); tmpCellNum++) {
                        String tmpCellValue = readCellContentAsString(tmpRow, tmpCellNum);
                        if (null != tmpCellValue) {
                            tmpResult.append(tmpCellValue);
                            tmpResult.append(" ");
                        }
                    }
                    tmpResult.append(" ");
                }
            }
        }

        return tmpResult.toString().trim();
    }


    public static String readCellContentAsString(HSSFRow aRow, int aColumnsNo) {
        String tmpResult = null;
        HSSFCell tmpCell;
        int tmpCellType;

        tmpCell = aRow.getCell(aColumnsNo);
        if (null == tmpCell) {
            return tmpResult;
        }

        tmpCellType = tmpCell.getCellType();

        switch (tmpCellType) {
        case HSSFCell.CELL_TYPE_BLANK:
            tmpResult = "";
            break;
        case HSSFCell.CELL_TYPE_STRING:
            tmpResult = tmpCell.getRichStringCellValue().getString();
            break;
        case HSSFCell.CELL_TYPE_NUMERIC:
            tmpResult = "" + tmpCell.getNumericCellValue();
            break;

        // deal with the other possible cases
        case HSSFCell.CELL_TYPE_BOOLEAN:
            // ignore
        case HSSFCell.CELL_TYPE_ERROR:
            // ignore
        case HSSFCell.CELL_TYPE_FORMULA:
            // ignore
        default:
            // ignore
        }
        return tmpResult;
    }
}
