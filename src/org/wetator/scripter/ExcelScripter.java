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


package org.wetator.scripter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.exception.WetatorException;
import org.wetator.util.NormalizedString;

/**
 * Scripter for excel files.
 * 
 * @author rbri
 */
public final class ExcelScripter implements IScripter {
  private static final Log LOG = LogFactory.getLog(ExcelScripter.class);

  private static final String EXCEL_FILE_EXTENSION = ".xls";
  private static final int COMMENT_COLUMN_NO = 0;
  private static final int COMMAND_NAME_COLUMN_NO = 1;
  private static final int FIRST_PARAM_COLUMN_NO = 2;
  private static final int SECOND_PARAM_COLUMN_NO = 3;
  private static final int THIRD_PARAM_COLUMN_NO = 4;

  private File file;
  private List<Command> commands;

  /**
   * Standard constructor.
   */
  public ExcelScripter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#script(java.io.File)
   */
  @Override
  public void script(final File aFile) throws WetatorException {
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
   * @see org.wetator.core.IScripter#isSupported(java.io.File)
   */
  @Override
  public boolean isSupported(final File aFile) {
    String tmpFileName;
    boolean tmpResult;

    tmpFileName = aFile.getName().toLowerCase();
    tmpResult = tmpFileName.endsWith(EXCEL_FILE_EXTENSION);

    return tmpResult;
  }

  private List<Command> readCommands() throws WetatorException {
    final List<Command> tmpResult = new LinkedList<Command>();

    final InputStream tmpInputStream;
    try {
      tmpInputStream = new FileInputStream(getFile().getAbsoluteFile());
    } catch (final FileNotFoundException e) {
      throw new WetatorException("File '" + getFile().getAbsolutePath() + "' not available.", e);
    }
    try {
      final HSSFWorkbook tmpWorkbook = new HSSFWorkbook(tmpInputStream);
      final FormulaEvaluator tmpFormulaEvaluator = tmpWorkbook.getCreationHelper().createFormulaEvaluator();

      int tmpSheetNo = -1;
      for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
        final String tmpSheetName = tmpWorkbook.getSheetName(i);
        if (!StringUtils.isEmpty(tmpSheetName)) {
          if (tmpSheetName.toLowerCase().contains("test")) {
            tmpSheetNo = i;
            break;
          }
        }
      }

      if (tmpSheetNo < 0) {
        throw new WetatorException("No test sheet found in file '" + getFile().getAbsolutePath() + "'.");
      }

      final HSSFSheet tmpSheet = tmpWorkbook.getSheetAt(tmpSheetNo);

      for (int tmpLine = 0; tmpLine <= tmpSheet.getLastRowNum(); tmpLine++) {
        HSSFRow tmpRow;
        String tmpCommentString;
        boolean tmpCommentFlag;
        String tmpCommandName;
        Parameter tmpParameter;

        tmpRow = tmpSheet.getRow(tmpLine);
        // strange case but it really happens
        if (null != tmpRow) {
          tmpCommentString = readCellContentAsString(tmpRow, COMMENT_COLUMN_NO, tmpFormulaEvaluator);
          tmpCommentFlag = StringUtils.isNotEmpty(tmpCommentString);

          tmpCommandName = readCellContentAsString(tmpRow, COMMAND_NAME_COLUMN_NO, tmpFormulaEvaluator);
        // normalize command name
        tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase();
          tmpCommandName = new NormalizedString(tmpCommandName).toString();

          // empty command means comment
          if (tmpCommentFlag && StringUtils.isEmpty(tmpCommandName)) {
            tmpCommandName = "Comment";
          }

          if (!StringUtils.isEmpty(tmpCommandName)) {
            final Command tmpCommand = new Command(tmpCommandName, tmpCommentFlag);

            tmpParameter = readCellContentAsParameter(tmpRow, FIRST_PARAM_COLUMN_NO, tmpFormulaEvaluator);
            if (null != tmpParameter) {
              tmpCommand.setFirstParameter(tmpParameter);
            }

            tmpParameter = readCellContentAsParameter(tmpRow, SECOND_PARAM_COLUMN_NO, tmpFormulaEvaluator);
            if (null != tmpParameter) {
              tmpCommand.setSecondParameter(tmpParameter);
            }

            tmpParameter = readCellContentAsParameter(tmpRow, THIRD_PARAM_COLUMN_NO, tmpFormulaEvaluator);
            if (null != tmpParameter) {
              tmpCommand.setThirdParameter(tmpParameter);
            }

            tmpCommand.setLineNo(tmpLine + 1);

            tmpResult.add(tmpCommand);
          }
        }
      }

      return tmpResult;
    } catch (final IOException e) {
      throw new WetatorException("IO Problem reading file '" + getFile().getAbsolutePath() + "'.", e);
    } finally {
      try {
        tmpInputStream.close();
      } catch (final IOException e) {
        throw new WetatorException("IO Problem closing file '" + getFile().getAbsolutePath() + "'.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#getCommands()
   */
  @Override
  public List<Command> getCommands() {
    return commands;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }

  private String readCellContentAsString(final HSSFRow aRow, final int aColumnsNo,
      final FormulaEvaluator aFormulaEvaluator) throws WetatorException {

    final HSSFCell tmpCell = aRow.getCell(aColumnsNo);
    if (null == tmpCell) {
      return null;
    }

    final DataFormatter tmpDataFormatter = new DataFormatter(Locale.getDefault());
    try {
      final String tmpResult = tmpDataFormatter.formatCellValue(tmpCell, aFormulaEvaluator);
      return tmpResult;
    } catch (final NotImplementedException e) {
      String tmpMsg = e.getMessage();
      if (null != e.getCause()) {
        tmpMsg = tmpMsg + " (" + e.getCause().toString() + ")";
      }
      LOG.error(tmpMsg);
      final String tmpResult = tmpDataFormatter.formatCellValue(tmpCell, null);
      return tmpResult;
    }
  }

  private Parameter readCellContentAsParameter(final HSSFRow aRow, final int aColumnsNo,
      final FormulaEvaluator aFormulaEvaluator) throws WetatorException {
    String tmpContent;

    tmpContent = readCellContentAsString(aRow, aColumnsNo, aFormulaEvaluator);
    if (StringUtils.isEmpty(tmpContent)) {
      return null;
    }

    return new Parameter(tmpContent);
  }
}
