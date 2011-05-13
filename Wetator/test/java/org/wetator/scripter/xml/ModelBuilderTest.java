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


package org.wetator.scripter.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.wetator.exception.WetException;
import org.wetator.scripter.xml.model.CommandType;
import org.wetator.scripter.xml.model.ParameterType;
import org.xml.sax.SAXException;

/**
 * Tests for the {@link ModelBuilder}.
 * 
 * @author frank.danek
 * @author tobwoerk
 */
public class ModelBuilderTest {

  @Test(expected = WetException.class)
  public void nullSchemaMap() throws SAXException, IOException {
    new ModelBuilder((HashMap<String, XMLSchema>) null);
  }

  @Test(expected = WetException.class)
  public void emptySchemaMap() throws SAXException, IOException {
    new ModelBuilder(new HashMap<String, XMLSchema>());
  }

  @Test(expected = WetException.class)
  public void invalidSchema() throws SAXException, IOException {
    Map<String, XMLSchema> tmpSchemas = new HashMap<String, XMLSchema>();
    tmpSchemas.put("http://www.wetator.org/xsd/something", new XMLSchema("http://www.wetator.org/xsd/something",
        "test/java/org/wetator/test/resource/something.xsd"));
    new ModelBuilder(tmpSchemas);
  }

  @Test
  public void noCommandSets() throws SAXException, IOException {
    Map<String, XMLSchema> tmpSchemas = new HashMap<String, XMLSchema>();
    tmpSchemas.put("http://www.wetator.org/xsd/test-case", new XMLSchema("http://www.wetator.org/xsd/test-case",
        "test-case-1.0.0.xsd"));
    Assert.assertEquals(0, new ModelBuilder(tmpSchemas).getCommandTypes().size());
  }

  @Test(expected = WetException.class)
  public void unknownCommandSet1() throws SAXException, IOException {
    Map<String, XMLSchema> tmpSchemas = new HashMap<String, XMLSchema>();
    tmpSchemas.put("http://www.wetator.org/xsd/unknown", new XMLSchema("http://www.wetator.org/xsd/unknown",
        "unknown.xsd"));
    new ModelBuilder(tmpSchemas);
  }

  @Test
  public void junitTestCommandSet() throws SAXException, IOException {
    Map<String, XMLSchema> tmpSchemas = new HashMap<String, XMLSchema>();
    tmpSchemas.put("http://www.wetator.org/xsd/test-case", new XMLSchema("http://www.wetator.org/xsd/test-case",
        "test-case-1.0.0.xsd"));
    tmpSchemas.put("http://www.wetator.org/xsd/junit-test-command-set", new XMLSchema(
        "http://www.wetator.org/xsd/junit-test-command-set",
        "test/java/org/wetator/test/resource/junit-test-command-set.xsd"));

    List<CommandType> tmpCommandTypes = new ModelBuilder(tmpSchemas).getCommandTypes();

    Assert.assertEquals(3, tmpCommandTypes.size());

    CommandType tmpCommandType = tmpCommandTypes.get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpCommandType.getNamespace());
    Assert.assertEquals("junit-command1", tmpCommandType.getName());
    Assert.assertEquals("The first junit test command.", tmpCommandType.getDocumentation());
    Assert.assertEquals(0, tmpCommandType.getParameterTypes().size());

    tmpCommandType = tmpCommandTypes.get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpCommandType.getNamespace());
    Assert.assertEquals("junit-command2", tmpCommandType.getName());
    Assert.assertEquals("The second junit test command.", tmpCommandType.getDocumentation());
    Assert.assertEquals(1, tmpCommandType.getParameterTypes().size());
    ParameterType tmpParameterType = tmpCommandType.getParameterTypes().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpParameterType.getNamespace());
    Assert.assertEquals("param1", tmpParameterType.getName());
    Assert.assertEquals("The first param of the second command.", tmpParameterType.getDocumentation());
    Assert.assertFalse(tmpParameterType.isOptional());

    tmpCommandType = tmpCommandTypes.get(2);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpCommandType.getNamespace());
    Assert.assertEquals("junit-command3", tmpCommandType.getName());
    Assert.assertEquals("The third junit test command.", tmpCommandType.getDocumentation());
    Assert.assertEquals(2, tmpCommandType.getParameterTypes().size());
    tmpParameterType = tmpCommandType.getParameterTypes().get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpParameterType.getNamespace());
    Assert.assertEquals("param1", tmpParameterType.getName());
    Assert.assertEquals("The first param of the third command.", tmpParameterType.getDocumentation());
    Assert.assertFalse(tmpParameterType.isOptional());
    tmpParameterType = tmpCommandType.getParameterTypes().get(1);
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpParameterType.getNamespace());
    Assert.assertEquals("param2", tmpParameterType.getName());
    Assert.assertEquals("The second param of the third command.", tmpParameterType.getDocumentation());
    Assert.assertTrue(tmpParameterType.isOptional());
  }
}
