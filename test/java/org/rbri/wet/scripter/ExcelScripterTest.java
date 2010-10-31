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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;

/**
 * @author rbri
 */
public class ExcelScripterTest {

  @Test
  public void test() throws WetException {
    ExcelScripter tmpExcelScripter = new ExcelScripter();
    tmpExcelScripter.setFile(new File("test/excel/assert_content.xls"));

    List<WetCommand> tmpCommands = tmpExcelScripter.getCommands();
    Assert.assertEquals(73, tmpCommands.size());

    WetCommand tmpCommand = tmpCommands.get(0);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("Aktion", tmpCommand.getName());

    tmpCommand = tmpCommands.get(10);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("Comment", tmpCommand.getName());

    tmpCommand = tmpCommands.get(54);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("Assert Fail", tmpCommand.getName());
  }
}
