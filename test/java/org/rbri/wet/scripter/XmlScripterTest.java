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
 * @author tobwoerk
 */
public class XmlScripterTest extends TestCase {

    /**
     * @param args the args-array
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * @return the suite
     */
    public static Test suite() {
        return new TestSuite(XmlScripterTest.class);
    }

    /**
     * @throws WetException if something goes wrong
     */
    public void test() throws WetException {
        XmlScripter tmpXmlScripter = new XmlScripter();
        tmpXmlScripter.setFile(new File("test/java/org/rbri/wet/resource/junit.xml"));

        List<WetCommand> tmpCommands = tmpXmlScripter.getCommands();
        assertEquals(9, tmpCommands.size());

        WetCommand tmpCommand = tmpCommands.get(0);
        assertTrue(tmpCommand.isComment());
        assertEquals("Comment", tmpCommand.getName());

        tmpCommand = tmpCommands.get(1);
        assertFalse(tmpCommand.isComment());
        assertEquals("Open Url", tmpCommand.getName());

        tmpCommand = tmpCommands.get(2);
        assertFalse(tmpCommand.isComment());
        assertEquals("Assert Title", tmpCommand.getName());

        tmpCommand = tmpCommands.get(3);
        assertFalse(tmpCommand.isComment());
        assertEquals("Set", tmpCommand.getName());
        assertEquals("testValue", tmpCommand.getSecondParameter().getValue());

        tmpCommand = tmpCommands.get(4);
        assertFalse(tmpCommand.isComment());
        assertEquals("Click On", tmpCommand.getName());

        tmpCommand = tmpCommands.get(5);
        assertTrue(tmpCommand.isComment());
        assertEquals("Click On", tmpCommand.getName());

        tmpCommand = tmpCommands.get(6);
        assertFalse(tmpCommand.isComment());
        assertEquals("Assert Content", tmpCommand.getName());

        tmpCommand = tmpCommands.get(7);
        assertTrue(tmpCommand.isComment());
        assertEquals("Comment", tmpCommand.getName());

        tmpCommand = tmpCommands.get(8);
        assertTrue(tmpCommand.isComment());
        assertEquals("Comment", tmpCommand.getName());
    }
}
