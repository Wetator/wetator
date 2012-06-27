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
import org.wetator.core.IScripter;
import org.wetator.exception.InvalidInputException;

/**
 * Tests for {@link LegacyXMLScripter}.
 * 
 * @author tobwoerk
 * @author frank.danek
 */
public class LegacyXMLScripterTest {

  @Test
  public void unsupportedExtension() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/excel.xls");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'excel.xls' not supported by LegacyXMLScripter. Extension is not '.wet' or '.xml'.",
        tmpResult.getMessage());
  }

  @Test
  public void fileNotFound() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/doesNotExist.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'doesNotExist.xml' not supported by LegacyXMLScripter. Could not find file.",
        tmpResult.getMessage());
  }

  @Test
  public void emptyXML() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/empty.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'empty.xml' not supported by LegacyXMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void emptyWET() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/empty.wet");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'empty.wet' not supported by LegacyXMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void unsupportedXML() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/xml.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'xml.xml' not supported by LegacyXMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void unsupportedWET() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/xml.wet");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'xml.wet' not supported by LegacyXMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void supportedXML() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/legacyXML.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);
  }

  @Test
  public void supportedWET() {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/legacyXML.wet");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);
  }

  @Test(expected = InvalidInputException.class)
  public void malformed() throws InvalidInputException {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/legacyXMLMalformed.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpLegacyXMLScripter.script(tmpFile);
  }

  @Test
  public void scriptXML() throws InvalidInputException {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/legacyXML.xml");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpLegacyXMLScripter.script(tmpFile);

    List<Command> tmpCommands = tmpLegacyXMLScripter.getCommands();
    Assert.assertEquals(10, tmpCommands.size());

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
    Assert.assertEquals(" testValue ", tmpCommand.getSecondParameter().getValue());

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
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-set", tmpCommand.getName());
    Assert.assertEquals("inputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals(" testValue ", tmpCommand.getSecondParameter().getValue());

    tmpCommand = tmpCommands.get(8);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());

    tmpCommand = tmpCommands.get(9);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
  }

  @Test
  public void scriptWET() throws InvalidInputException {
    LegacyXMLScripter tmpLegacyXMLScripter = new LegacyXMLScripter();
    File tmpFile = new File("test/java/org/wetator/test/resource/legacyXML.wet");

    IScripter.IsSupportedResult tmpResult = tmpLegacyXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpLegacyXMLScripter.script(tmpFile);

    List<Command> tmpCommands = tmpLegacyXMLScripter.getCommands();
    Assert.assertEquals(10, tmpCommands.size());

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
    Assert.assertEquals(" testValue ", tmpCommand.getSecondParameter().getValue());

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
    Assert.assertFalse(tmpCommand.isComment());
    Assert.assertEquals("assert-set", tmpCommand.getName());
    Assert.assertEquals("inputText_Name", tmpCommand.getFirstParameter().getValue());
    Assert.assertEquals(" testValue ", tmpCommand.getSecondParameter().getValue());

    tmpCommand = tmpCommands.get(8);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());

    tmpCommand = tmpCommands.get(9);
    Assert.assertTrue(tmpCommand.isComment());
    Assert.assertEquals("", tmpCommand.getName());
  }
}
