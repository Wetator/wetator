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


package org.wetator.scripter.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.wetator.exception.ImplementationException;
import org.wetator.scripter.ParseException;
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

  @Test(expected = ImplementationException.class)
  public void nullSchemaMap() throws IOException, SAXException, ParseException {
    new ModelBuilder(null, null);
  }

  @Test(expected = ImplementationException.class)
  public void emptySchemaMap() throws IOException, SAXException, ParseException {
    new ModelBuilder(new ArrayList<XMLSchema>(), null);
  }

  @Test(expected = ParseException.class)
  public void invalidSchema() throws IOException, SAXException, ParseException {
    List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
    tmpSchemas.add(new XMLSchema("http://www.wetator.org/xsd/invalid-command-set",
        "test/java/org/wetator/test/resource/invalid-command-set.xsd"));
    new ModelBuilder(tmpSchemas, null);
  }

  @Test
  public void noCommandSets() throws IOException, SAXException, ParseException {
    List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
    tmpSchemas.add(new XMLSchema("http://www.wetator.org/xsd/test-case", "test-case-1.0.0.xsd"));
    Assert.assertEquals(0, new ModelBuilder(tmpSchemas, null).getCommandTypes().size());
  }

  @Test(expected = ParseException.class)
  public void unknownCommanddSet() throws IOException, SAXException, ParseException {
    List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
    tmpSchemas.add(new XMLSchema("http://www.wetator.org/xsd/unknown", "unknown.xsd"));
    new ModelBuilder(tmpSchemas, null);
  }

  @Test
  public void junitTestCommandSet() throws IOException, SAXException, ParseException {
    List<XMLSchema> tmpSchemas = new ArrayList<XMLSchema>();
    tmpSchemas.add(new XMLSchema("http://www.wetator.org/xsd/test-case", "test-case-1.0.0.xsd"));
    tmpSchemas.add(new XMLSchema("http://www.wetator.org/xsd/junit-test-command-set",
        "test/java/org/wetator/test/resource/junit-test-command-set.xsd"));

    List<CommandType> tmpCommandTypes = new ModelBuilder(tmpSchemas, null).getCommandTypes();

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