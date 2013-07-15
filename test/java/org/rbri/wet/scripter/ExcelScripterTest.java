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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;

/**
 * @author rbri
 */
public class ExcelScripterTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(ExcelScripterTest.class);
    }

    public void test() throws WetException {
        ExcelScripter tmpExcelScripter = new ExcelScripter();
        tmpExcelScripter.setFile(new File("test/excel/assert_content.xls"));

        List<WetCommand> tmpCommands = tmpExcelScripter.getCommands();
        assertEquals(55, tmpCommands.size());

        WetCommand tmpCommand = tmpCommands.get(0);
        assertTrue(tmpCommand.isComment());
        assertEquals("Aktion", tmpCommand.getName());

        tmpCommand = tmpCommands.get(10);
        assertTrue(tmpCommand.isComment());
        assertEquals("Comment", tmpCommand.getName());

        tmpCommand = tmpCommands.get(54);
        assertFalse(tmpCommand.isComment());
        assertEquals("Assert Fail", tmpCommand.getName());
    }
}
