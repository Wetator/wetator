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


package org.rbri.wet.scripter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.NormalizedString;

/**
 * Scripter for excel files
 * 
 * @author rbri
 */
public final class ExcelScripter implements WetScripter {

  private static final String EXCEL_FILE_EXTENSION = ".xls";
  private static final int COMMENT_COLUMN_NO = 0;
  private static final int COMMAND_NAME_COLUMN_NO = 1;
  private static final int FIRST_PARAM_COLUMN_NO = 2;
  private static final int SECOND_PARAM_COLUMN_NO = 3;
  private static final int THIRD_PARAM_COLUMN_NO = 4;

  private File file;
  private HSSFWorkbook workbook;
  private InputStream inputStream;
  private HSSFSheet sheet;
  private List<WetCommand> commands;

  /**
   * Standard constructor.
   */
  public ExcelScripter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#setFile(java.io.File)
   */
  @Override
  public void setFile(File aFile) throws WetException {
    file = aFile;

    commands = readCommands();
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#isSupported(java.io.File)
   */
  @Override
  public boolean isSupported(File aFile) {
    String tmpFileName;
    boolean tmpResult;

    tmpFileName = aFile.getName().toLowerCase();
    tmpResult = tmpFileName.endsWith(EXCEL_FILE_EXTENSION);

    return tmpResult;
  }

  private List<WetCommand> readCommands() throws WetException {
    List<WetCommand> tmpResult = new LinkedList<WetCommand>();

    try {
      inputStream = new FileInputStream(getFile().getAbsoluteFile());
    } catch (FileNotFoundException e) {
      throw new WetException("File '" + getFile().getAbsolutePath() + "' not available.", e);
    }
    try {
      workbook = new HSSFWorkbook(inputStream);

      int tmpSheetNo = -1;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        String tmpSheetName = workbook.getSheetName(i);
        if (!StringUtils.isEmpty(tmpSheetName)) {
          if (tmpSheetName.toLowerCase().contains("test")) {
            tmpSheetNo = i;
            break;
          }
        }
      }

      if (tmpSheetNo < 0) {
        throw new WetException("No test sheet found in file '" + getFile().getAbsolutePath() + "'.");
      }

      sheet = workbook.getSheetAt(tmpSheetNo);

      for (int tmpLine = 0; tmpLine <= sheet.getLastRowNum(); tmpLine++) {
        HSSFRow tmpRow;
        String tmpCommentString;
        boolean tmpCommentFlag;
        String tmpCommandName;
        Parameter tmpParameter;

        tmpRow = sheet.getRow(tmpLine);

        tmpCommentString = readCellContentAsString(tmpRow, COMMENT_COLUMN_NO);
        tmpCommentFlag = StringUtils.isNotEmpty(tmpCommentString);

        tmpCommandName = readCellContentAsString(tmpRow, COMMAND_NAME_COLUMN_NO);
        tmpCommandName = new NormalizedString(tmpCommandName).toString();

        // empty command means comment
        if (tmpCommentFlag && StringUtils.isEmpty(tmpCommandName)) {
          tmpCommandName = "Comment";
        }

        if (!StringUtils.isEmpty(tmpCommandName)) {
          WetCommand tmpCommand = new WetCommand(tmpCommandName, tmpCommentFlag);

          tmpParameter = readCellContentAsParameter(tmpRow, FIRST_PARAM_COLUMN_NO);
          if (null != tmpParameter) {
            tmpCommand.setFirstParameter(tmpParameter);
          }

          tmpParameter = readCellContentAsParameter(tmpRow, SECOND_PARAM_COLUMN_NO);
          if (null != tmpParameter) {
            tmpCommand.setSecondParameter(tmpParameter);
          }

          tmpParameter = readCellContentAsParameter(tmpRow, THIRD_PARAM_COLUMN_NO);
          if (null != tmpParameter) {
            tmpCommand.setThirdParameter(tmpParameter);
          }

          tmpCommand.setLineNo(tmpLine + 1);

          tmpResult.add(tmpCommand);
        }
      }

      return tmpResult;
    } catch (IOException e) {
      throw new WetException("IO Problem reading file '" + getFile().getAbsolutePath() + "'.", e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw new WetException("IO Problem closing file '" + getFile().getAbsolutePath() + "'.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#getCommands()
   */
  @Override
  public List<WetCommand> getCommands() {
    return commands;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.scripter.WetScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(Properties aConfiguration) {
    // nothing to do
  }

  private String readCellContentAsString(HSSFRow aRow, int aColumnsNo) throws WetException {
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
      // for convenience support numbers also
      case Cell.CELL_TYPE_NUMERIC:
        tmpResult = "" + tmpCell.getNumericCellValue();
        break;

      // deal with the other possible cases
      case Cell.CELL_TYPE_BOOLEAN:
        throw new WetException("Unsupported cell type 'boolean' row: " + aRow.getRowNum() + " column: " + aColumnsNo
            + " (file: '" + getFile().getAbsolutePath() + "').");
      case Cell.CELL_TYPE_ERROR:
        throw new WetException("Unsupported cell type 'error' row: " + aRow.getRowNum() + " column: " + aColumnsNo
            + " (file: '" + getFile().getAbsolutePath() + "').");
      case Cell.CELL_TYPE_FORMULA:
        throw new WetException("Unsupported cell type 'formula' row: " + aRow.getRowNum() + " column: " + aColumnsNo
            + " (file: '" + getFile().getAbsolutePath() + "').");
      default:
        throw new WetException("Unsupported cell type '" + tmpCellType + "' row: " + aRow.getRowNum() + " column: "
            + aColumnsNo + " (file: '" + getFile().getAbsolutePath() + "').");
    }
    return tmpResult;
  }

  private Parameter readCellContentAsParameter(HSSFRow aRow, int aColumnsNo) throws WetException {
    String tmpContent;

    tmpContent = readCellContentAsString(aRow, aColumnsNo);
    if (StringUtils.isEmpty(tmpContent)) {
      return null;
    }

    return new Parameter(tmpContent);
  }
}
