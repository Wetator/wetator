/*
 * Copyright (c) 2008-2011 Ronald Brill
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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.WetCommand;
import org.wetator.exception.WetException;

/**
 * @author rbri
 */
public class ExcelScripterTest {

  @Test
  public void test() throws WetException {
    ExcelScripter tmpExcelScripter = new ExcelScripter();
    tmpExcelScripter.setFile(new File("test/java/org/wetator/test/resource/junit.xls"));

    List<WetCommand> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(15, tmpCommands.size());

    int tmpPos = 0;
    WetCommand tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("Aktion", tmpCommand.getName());
    Assert.assertEquals("Parameter", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("opt. Parameter", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("a simple string", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("1", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("1234567", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("12,4", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("12,3000", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("12,99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    // POI Bug - the current locale is ignored
    Assert.assertEquals("01/04/99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("1-Apr-99", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("April 2011", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Title", tmpCommand.getName());
    Assert.assertEquals("25,7", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }
}
