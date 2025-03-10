/*
 * Copyright (c) 2008-2025 wetator.org
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.ContentUtil;
import org.wetator.util.NormalizedString;

/**
 * Scripter for excel files.
 *
 * @author rbri
 * @author frank.danek
 */
public final class ExcelScripter implements IScripter {

  private static final Logger LOG = LogManager.getLogger(ExcelScripter.class);

  private static final String PROPERTY_PREFIX = WetatorConfiguration.PROPERTY_PREFIX + "scripter.excel.";
  private static final String PROPERTY_LOCALE = PROPERTY_PREFIX + "locale";

  private static final String EXCEL_FILE_EXTENSION_XLS = ".xls";
  private static final String EXCEL_FILE_EXTENSION_XLSX = ".xlsx";
  private static final int COMMENT_COLUMN_NO = 0;
  private static final int COMMAND_NAME_COLUMN_NO = 1;
  private static final int FIRST_PARAM_COLUMN_NO = 2;
  private static final int SECOND_PARAM_COLUMN_NO = 3;
  private static final int THIRD_PARAM_COLUMN_NO = 4;

  private File file;
  private List<Command> commands;
  private Locale locale = Locale.getDefault();

  @Override
  public void initialize(final Properties aConfiguration) {
    final String tmpPropLocale = aConfiguration.getProperty(PROPERTY_LOCALE);

    if (StringUtils.isEmpty(tmpPropLocale)) {
      return;
    }

    try {
      locale = LocaleUtils.toLocale(tmpPropLocale);
    } catch (final Exception e) {
      LOG.error("Property '" + PROPERTY_LOCALE + "=" + tmpPropLocale + "' is not a valid locale; using default '"
          + locale + "instead (" + e.getMessage() + ").");
    }
  }

  @Override
  public IScripter.IsSupportedResult isSupported(final File aFile) {
    // first check the file extension
    final String tmpFileName = aFile.getName().toLowerCase(Locale.ROOT);
    if (!tmpFileName.endsWith(EXCEL_FILE_EXTENSION_XLS) && !tmpFileName.endsWith(EXCEL_FILE_EXTENSION_XLSX)) {
      return new IScripter.IsSupportedResult(
          "File '" + aFile.getName() + "' not supported by ExcelScripter. Extension is not '" + EXCEL_FILE_EXTENSION_XLS
              + "' or '" + EXCEL_FILE_EXTENSION_XLSX + "'.");
    }

    // second check the file accessibility
    if (!aFile.exists() || !aFile.isFile()) {
      return new IScripter.IsSupportedResult(
          "File '" + aFile.getName() + "' not supported by ExcelScripter. Could not find file.");
    }
    if (!aFile.canRead()) {
      return new IScripter.IsSupportedResult(
          "File '" + aFile.getName() + "' not supported by ExcelScripter. Could not read file.");
    }

    return IScripter.IS_SUPPORTED;
  }

  @Override
  public void script(final File aFile) throws InvalidInputException {
    file = aFile;

    commands = readCommands();
  }

  private List<Command> readCommands() throws InvalidInputException {
    final List<Command> tmpResult = new LinkedList<>();

    try (InputStream tmpInputStream = Files.newInputStream(file.toPath())) {
      try (Workbook tmpWorkbook = WorkbookFactory.create(tmpInputStream)) {
        int tmpSheetNo = -1;
        for (int i = 0; i < tmpWorkbook.getNumberOfSheets(); i++) {
          final String tmpSheetName = tmpWorkbook.getSheetName(i);
          if (StringUtils.isNotEmpty(tmpSheetName) && tmpSheetName.toLowerCase(Locale.ROOT).contains("test")) {
            tmpSheetNo = i;
            break;
          }
        }

        if (tmpSheetNo < 0) {
          throw new InvalidInputException(
              "No test sheet found in file '" + FilenameUtils.normalize(file.getAbsolutePath()) + "'.");
        }

        final Sheet tmpSheet = tmpWorkbook.getSheetAt(tmpSheetNo);
        final FormulaEvaluator tmpFormulaEvaluator = tmpWorkbook.getCreationHelper().createFormulaEvaluator();

        for (int tmpLine = 0; tmpLine <= tmpSheet.getLastRowNum(); tmpLine++) {
          final Row tmpRow;
          final String tmpCommentString;
          final boolean tmpCommentFlag;
          String tmpCommandName;

          tmpRow = tmpSheet.getRow(tmpLine);
          // strange case but it really happens
          if (null != tmpRow) {
            tmpCommentString = ContentUtil.readCellContentAsString(tmpRow, COMMENT_COLUMN_NO, tmpFormulaEvaluator,
                locale);
            tmpCommentFlag = StringUtils.isNotEmpty(tmpCommentString);

            tmpCommandName = ContentUtil.readCellContentAsString(tmpRow, COMMAND_NAME_COLUMN_NO, tmpFormulaEvaluator,
                locale);
            // normalize command name
            if (StringUtils.isNotEmpty(tmpCommandName)) {
              tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase(Locale.ROOT);
            }
            tmpCommandName = new NormalizedString(tmpCommandName).toString();

            // empty command means comment
            if (tmpCommentFlag && StringUtils.isEmpty(tmpCommandName)) {
              tmpCommandName = "Comment";
            }

            if (!StringUtils.isEmpty(tmpCommandName)) {
              final Command tmpCommand = new Command(tmpCommandName, tmpCommentFlag);

              Parameter tmpParameter = readCellContentAsParameter(tmpRow, FIRST_PARAM_COLUMN_NO, tmpFormulaEvaluator);
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
      }

      return tmpResult;
    } catch (final FileNotFoundException e) {
      throw new InvalidInputException("Could not find file '" + FilenameUtils.normalize(file.getAbsolutePath()) + "'.",
          e);
    } catch (final IOException e) {
      throw new InvalidInputException(
          "Error parsing file '" + FilenameUtils.normalize(file.getAbsolutePath()) + "' (" + e.getMessage() + ").", e);
    }
  }

  private Parameter readCellContentAsParameter(final Row aRow, final int aColumnsNo,
      final FormulaEvaluator aFormulaEvaluator) {
    final String tmpContent;

    tmpContent = ContentUtil.readCellContentAsString(aRow, aColumnsNo, aFormulaEvaluator, locale);
    if (StringUtils.isEmpty(tmpContent)) {
      return null;
    }

    return new Parameter(tmpContent);
  }

  @Override
  public List<Command> getCommands() {
    return commands;
  }
}
