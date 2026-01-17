/*
 * Copyright (c) 2008-2025 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.scripter;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.exception.InvalidInputException;

/**
 * Tests for {@link ExcelScripter}.
 *
 * @author rbri
 * @author frank.danek
 * @author tobwoerk
 */
public class ExcelScripterTest {

  @Test
  public void unsupportedExtension() {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();
    final File tmpFile = new File("src/test/resources/legacyXML.xml");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertNotSame(IScripter.IS_SUPPORTED, tmpResult);

    Assert.assertEquals("File 'legacyXML.xml' not supported by ExcelScripter. Extension is not '.xls' or '.xlsx'.",
        tmpResult.getMessage());
  }

  @Test
  public void fileNotFound() {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();
    final File tmpFile = new File("src/test/resources/doesNotExist.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertNotSame(IScripter.IS_SUPPORTED, tmpResult);

    Assert.assertEquals("File 'doesNotExist.xls' not supported by ExcelScripter. Could not find file.",
        tmpResult.getMessage());
  }

  @Test
  public void supported() {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();
    final File tmpFile = new File("src/test/resources/excel.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);
  }

  @Test(expected = InvalidInputException.class)
  public void malformed() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();
    final File tmpFile = new File("src/test/resources/excelMalformed.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);
  }

  @Test(expected = InvalidInputException.class)
  public void empty() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();
    final File tmpFile = new File("src/test/resources/excelMalformed.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);
  }

  @Test
  public void xlsScriptDe() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();

    final Properties tmpProps = new Properties();
    tmpProps.put("wetator.scripter.excel.locale", "de");
    tmpExcelScripter.initialize(tmpProps);

    final File tmpFile = new File("src/test/resources/excel.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(17, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("aktion", tmpCommand.getName());
    Assert.assertEquals("Parameter", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("opt. Parameter", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("a simple string", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1234567", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,4", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,3000", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,99 €", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Bug - the current locale is ignored
    Assert.assertEquals("01/04/99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());

    // different JDK's
    final String tmpValue = tmpCommand.getFirstParameter().getValue();
    Assert.assertTrue(tmpValue, "1-Apr.-99".equals(tmpValue) || "1-Apr-99".equals(tmpValue));
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("April 2011", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("25,7", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("ARBEITSTAG(DATE(YEAR(TODAY()),MONTH(TODAY())+1,0),-10, )",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }

  @Test
  public void xlsxScriptDe() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();

    final Properties tmpProps = new Properties();
    tmpProps.put("wetator.scripter.excel.locale", "de");
    tmpExcelScripter.initialize(tmpProps);

    final File tmpFile = new File("src/test/resources/excel.xlsx");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(17, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("aktion", tmpCommand.getName());
    Assert.assertEquals("Parameter", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("opt. Parameter", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("a simple string", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1234567", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,4", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,3000", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12,99 €", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Bug - the current locale is ignored
    Assert.assertEquals("01/04/99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());

    // different JDK's
    final String tmpValue = tmpCommand.getFirstParameter().getValue();
    Assert.assertTrue(tmpValue, "1-Apr.-99".equals(tmpValue) || "1-Apr-99".equals(tmpValue));
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("April 2011", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("25,7", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Issue
    // Assert.assertEquals("ARBEITSTAG(DATE(YEAR(TODAY()),MONTH(TODAY())+1,0),-10, )",
    // tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("POI seems to have a bug here // =ARBEITSTAG(DATUM(JAHR(HEUTE()); MONAT(HEUTE())+1; 0);-10;)",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }

  @Test
  public void xlsScriptEn() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();

    final Properties tmpProps = new Properties();
    tmpProps.put("wetator.scripter.excel.locale", "en");
    tmpExcelScripter.initialize(tmpProps);

    final File tmpFile = new File("src/test/resources/excel.xls");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(17, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("aktion", tmpCommand.getName());
    Assert.assertEquals("Parameter", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("opt. Parameter", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("a simple string", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1234567", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.4", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.3000", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.99 €", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Bug - the current locale is ignored
    Assert.assertEquals("01/04/99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1-Apr-99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("April 2011", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("25.7", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("ARBEITSTAG(DATE(YEAR(TODAY()),MONTH(TODAY())+1,0),-10, )",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }

  @Test
  public void xlsxScriptEn() throws InvalidInputException {
    final ExcelScripter tmpExcelScripter = new ExcelScripter();

    final Properties tmpProps = new Properties();
    tmpProps.put("wetator.scripter.excel.locale", "en");
    tmpExcelScripter.initialize(tmpProps);

    final File tmpFile = new File("src/test/resources/excel.xlsx");

    final IScripter.IsSupportedResult tmpResult = tmpExcelScripter.isSupported(tmpFile);
      Assert.assertSame(IScripter.IS_SUPPORTED, tmpResult);

    tmpExcelScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(17, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("aktion", tmpCommand.getName());
    Assert.assertEquals("Parameter", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("opt. Parameter", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("a simple string", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1234567", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.4", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.3000", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("12.99 €", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Bug - the current locale is ignored
    Assert.assertEquals("01/04/99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("1-Apr-99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("April 2011", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("25.7", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    // POI Issue
    // Assert.assertEquals("ARBEITSTAG(DATE(YEAR(TODAY()),MONTH(TODAY())+1,0),-10, )",
    // tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("POI seems to have a bug here // =ARBEITSTAG(DATUM(JAHR(HEUTE()); MONAT(HEUTE())+1; 0);-10;)",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }
}
