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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.exception.WetatorException;

/**
 * @author tobwoerk
 * @author frank.danek
 */
public class LegacyXMLScripterTest {

  /**
   * @throws WetatorException if something goes wrong
   */
  @Test
  public void supported() throws WetatorException {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/junit.wet");

    Assert.assertTrue(tmpLegacyXMLScripter.isSupported(tmpFile));

    tmpLegacyXMLScripter.script(tmpFile);

    List<Command> tmpCommands = tmpLegacyXMLScripter.getCommands();
    Assert.assertEquals(9, tmpCommands.size());

    Command tmpCommand = tmpCommands.get(0);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
    Assert.assertEquals("Just a comment", tmpCommand.getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(1);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("open-url", tmpCommand.getName());
    Assert.assertEquals("set.html", tmpCommand.getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(2);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("Wetator / Set", tmpCommand.getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(3);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("set", tmpCommand.getName());
    Assert.assertEquals("inputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("testValue", tmpCommand.getSecondParameter().getValue());

    tmpCommand = tmpCommands.get(4);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("click-on", tmpCommand.getName());
    Assert.assertEquals("InputTextNameTest", tmpCommand.getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(5);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("click-on", tmpCommand.getName());
    Assert.assertEquals("Just another comment", tmpCommand.getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(6);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("GET Parameters Key Value inputText_Name testValue InputTextNameTest OK", tmpCommand
        .getFirstParameter().getValue());

    tmpCommand = tmpCommands.get(7);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
  }

  /**
   * @throws WetatorException if something goes wrong
   */
  @Test
  public void unsupported() throws WetatorException {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    Assert.assertFalse(tmpLegacyXMLScripter.isSupported(new File("test/java/org/wetator/test/resource/junit2.xml")));
  }
}
