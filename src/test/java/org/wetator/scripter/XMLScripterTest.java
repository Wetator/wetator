/*
 * Copyright (c) 2008-2023 wetator.org
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.exception.InvalidInputException;
import org.wetator.scripter.xml.XMLSchema;

/**
 * Tests for {@link XMLScripter}.
 *
 * @author frank.danek
 */
public class XMLScripterTest {

  @Test
  public void unsupportedExtension() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/excel.xls");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'excel.xls' not supported by XMLScripter. Extension is not '.wet' or '.xml'.",
        tmpResult.getMessage());
  }

  @Test
  public void fileNotFound() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/doesNotExist.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'doesNotExist.xml' not supported by XMLScripter. Could not find file.",
        tmpResult.getMessage());
  }

  @Test
  public void emptyFileXML() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/empty.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'empty.xml' not supported by XMLScripter. Could not parse file.", tmpResult.getMessage());
  }

  @Test
  public void emptyFileWET() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/empty.wet");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'empty.wet' not supported by XMLScripter. Could not parse file.", tmpResult.getMessage());
  }

  @Test
  public void unsupportedFileXML() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/legacyXML.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'legacyXML.xml' not supported by XMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void unsupportedFileWET() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/legacyXML.wet");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED != tmpResult);

    Assert.assertEquals("File 'legacyXML.wet' not supported by XMLScripter. Could not parse file.",
        tmpResult.getMessage());
  }

  @Test
  public void supportedFileXML() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/xml.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);
  }

  @Test
  public void supportedFileWET() {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/xml.wet");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);
  }

  @Test
  public void schemasNoDefaultFromFile() throws InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/emptyNoDefault.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpXMLScripter.script(tmpFile);

    Assert.assertEquals(2, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void schemasWrongDefaultFromFile() throws InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/emptyWrongDefault.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpXMLScripter.script(tmpFile);

    Assert.assertEquals(2, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void schemasFromFile() throws InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/emptyMultipleSchemas.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpXMLScripter.script(tmpFile);

    Assert.assertEquals(4, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(2);
    Assert.assertEquals("http://www.wetator.org/xsd/incubator-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("inc", tmpSchema.getPrefix());
    Assert.assertEquals("incubator-command-set-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(3);
    Assert.assertEquals("http://www.wetator.org/xsd/test-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("tst", tmpSchema.getPrefix());
    Assert.assertEquals("test-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void scriptFile() throws InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final File tmpFile = new File("src/test/resources/xml.xml");

    final IScripter.IsSupportedResult tmpResult = tmpXMLScripter.isSupported(tmpFile);
    Assert.assertTrue(IScripter.IS_SUPPORTED == tmpResult);

    tmpXMLScripter.script(tmpFile);

    final List<Command> tmpCommands = tmpXMLScripter.getCommands();
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
    Assert.assertEquals("GET Parameters Key Value inputText_Name testValue InputTextNameTest OK",
        tmpCommand.getFirstParameter().getValue());

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
  public void emptyContent() throws FileNotFoundException, IOException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/empty.xml"),
        StandardCharsets.UTF_8);
    Assert.assertFalse(tmpXMLScripter.isSupported(tmpContent));
  }

  @Test
  public void unsupportedContent() throws FileNotFoundException, IOException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/legacyXML.wet"),
        StandardCharsets.UTF_8);
    Assert.assertFalse(tmpXMLScripter.isSupported(tmpContent));
  }

  @Test
  public void supportedContent() throws FileNotFoundException, IOException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/xml.xml"),
        StandardCharsets.UTF_8);
    Assert.assertTrue(tmpXMLScripter.isSupported(tmpContent));
  }

  @Test
  public void schemasNoDefaultFromContent() throws FileNotFoundException, IOException, InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/emptyNoDefault.xml"),
        StandardCharsets.UTF_8);

    tmpXMLScripter.script(tmpContent, null);

    Assert.assertEquals(2, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void schemasWrongDefaultFromContent() throws FileNotFoundException, IOException, InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/emptyWrongDefault.xml"),
        StandardCharsets.UTF_8);

    tmpXMLScripter.script(tmpContent, null);

    Assert.assertEquals(2, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void schemasFromContent() throws FileNotFoundException, IOException, InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/emptyMultipleSchemas.xml"),
        StandardCharsets.UTF_8);

    tmpXMLScripter.script(tmpContent, null);

    Assert.assertEquals(4, tmpXMLScripter.getSchemas().size());

    XMLSchema tmpSchema = tmpXMLScripter.getSchemas().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("test-case-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/default-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default-command-set-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(2);
    Assert.assertEquals("http://www.wetator.org/xsd/incubator-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("inc", tmpSchema.getPrefix());
    Assert.assertEquals("incubator-command-set-1.0.0.xsd", tmpSchema.getLocation());

    tmpSchema = tmpXMLScripter.getSchemas().get(3);
    Assert.assertEquals("http://www.wetator.org/xsd/test-command-set", tmpSchema.getNamespace());
    Assert.assertEquals("tst", tmpSchema.getPrefix());
    Assert.assertEquals("test-command-set-1.0.0.xsd", tmpSchema.getLocation());
  }

  @Test
  public void scriptContent() throws FileNotFoundException, IOException, InvalidInputException {
    final XMLScripter tmpXMLScripter = new XMLScripter();
    final String tmpContent = IOUtils.toString(new FileInputStream("src/test/resources/xml.xml"),
        StandardCharsets.UTF_8);
    tmpXMLScripter.script(tmpContent, null);

    final List<Command> tmpCommands = tmpXMLScripter.getCommands();
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
    Assert.assertEquals("GET Parameters Key Value inputText_Name testValue InputTextNameTest OK",
        tmpCommand.getFirstParameter().getValue());

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
