/*
 * Copyright (c) 2008-2017 wetator.org
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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.exception.InvalidInputException;

/**
 * Tests for {@link WikiTextScripter}.
 *
 * @author rbri
 * @author frank.danek
 */
public class WikiTextScripterTest {

  @Test
  public void unsupportedExtension() {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/legacyXML.xml");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'legacyXML.xml' not supported by WikiTextScripter. Extension is not '.wett'.",
        tmpResult.getMessage());
  }

  @Test
  public void fileNotFound() {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/doesNotExist.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'doesNotExist.wett' not supported by WikiTextScripter. Could not find file.",
        tmpResult.getMessage());
  }

  @Test
  public void supported() {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/wikiText.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);
  }

  @Test
  public void script() throws InvalidInputException {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/wikiText.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpScripter.getCommands();
    Assert.assertEquals(8, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
    Assert.assertEquals("Just a comment", tmpCommand.getFirstParameter().getValue());
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
    Assert.assertEquals("GET Parameters Key Value inputText_Name testValue InputTextNameTest OK",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-set", tmpCommand.getName());
    Assert.assertEquals("InputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals("testValue", tmpCommand.getSecondParameter().getValue());
  }

  @Test
  public void continuationLines() throws InvalidInputException {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/continuationLines.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpScripter.getCommands();
    Assert.assertEquals(4, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
    Assert.assertEquals("Just a comment", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("open-url", tmpCommand.getName());
    Assert.assertEquals("set.html", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("continuing-comment assert-title", tmpCommand.getName());
    Assert.assertEquals("Wetator / Set", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("describe", tmpCommand.getName());

    String tmpValue = tmpCommand.getFirstParameter().getValue();
    tmpValue = StringUtils.replace(tmpValue, "\r\n", "\n");
    Assert.assertEquals("text 1  \n\ntext 3", tmpValue);
    Assert.assertNull(tmpCommand.getSecondParameter());
  }

  @Test
  public void utf8() throws InvalidInputException {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/utf8.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpScripter.getCommands();
    Assert.assertEquals(6, tmpCommands.size());

    int tmpPos = 0;
    Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("!\"#$%&'()*+,-./0123456789:;<=>?@", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("abcdefghijklmnopqrstuvwxyz\u00e4\u00f6\u00fc\u00df",
        tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("\u00ec\u00ed\u00ee\u00ef", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertTrue(tmpCommand.isComment());

    tmpPos++;
    tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("\u0430\u0431\u0432\u0433", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }

  @Test
  public void unicode() throws InvalidInputException {
    final WikiTextScripter tmpScripter = new WikiTextScripter();
    final File tmpFile = new File("test/java/org/wetator/test/resource/unicode.wett");

    final IScripter.IsSupportedResult tmpResult = tmpScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpScripter.getCommands();
    Assert.assertEquals(1, tmpCommands.size());

    final int tmpPos = 0;
    final Command tmpCommand = tmpCommands.get(tmpPos);
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-content", tmpCommand.getName());
    Assert.assertEquals("\u00ec \u00ed \u00ee \u00ef", tmpCommand.getFirstParameter().getValue());
    Assert.assertNull(tmpCommand.getSecondParameter());
  }
}
