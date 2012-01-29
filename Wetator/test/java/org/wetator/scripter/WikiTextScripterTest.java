/*
 * Copyright (c) 2008-2012 wetator.org
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
 * @author rbri
 */
public class WikiTextScripterTest {

  @Test
  public void supported() throws WetatorException {
    WikiTextScripter tmpScripter = new WikiTextScripter();
    tmpScripter.script(new File("test/java/org/wetator/test/resource/junit.wett"));

    List<Command> tmpCommands = tmpScripter.getCommands();
    Assert.assertEquals(8, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("Just a comment", tmpCommand.getName());
    Assert.assertNull(tmpCommand.getFirstParameter());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("open-url", tmpCommand.getName());
    Assert.assertEquals("set.html", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-title", tmpCommand.getName());
    Assert.assertEquals("Wetator / Set", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("set", tmpCommand.getName());
    Assert.assertEquals("inputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("testValue", tmpCommand.getSecondParameter().getValue());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("click-on", tmpCommand.getName());
    Assert.assertEquals("InputTextNameTest", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("click-on", tmpCommand.getName());
    Assert.assertEquals("Just another comment", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("GET Parameters Key Value inputText_Name testValue InputTextNameTest OK", tmpCommand
        .getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-set", tmpCommand.getName());
    Assert.assertEquals("InputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("testValue", tmpCommand.getSecondParameter().getValue());
  }
}
