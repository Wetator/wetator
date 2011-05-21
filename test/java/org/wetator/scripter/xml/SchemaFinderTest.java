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
import java.io.StringReader;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for the {@link SchemaFinder}.
 * 
 * @author frank.danek
 */
public class SchemaFinderTest {

  @Test(expected = XMLStreamException.class)
  public void emptyInput() throws IOException, XMLStreamException {
    new SchemaFinder(new StringReader(""));
  }

  @Test
  public void noSchema() throws IOException, XMLStreamException {
    Assert.assertTrue(new SchemaFinder(new StringReader("<xml/>")).getSchemas().isEmpty());
  }

  @Test
  public void defaultSchemaWithoutLocation() throws IOException, XMLStreamException {
    List<XMLSchema> tmpSchemas = new SchemaFinder(new StringReader(
        "<content xmlns='http://www.wetator.org/xsd/test-case' />")).getSchemas();
    Assert.assertEquals(0, tmpSchemas.size());
  }

  @Test
  public void defaultSchema() throws IOException, XMLStreamException {
    List<XMLSchema> tmpSchemas = new SchemaFinder(new StringReader("<content " //
        + "xmlns='http://www.wetator.org/xsd/default' " //
        + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " //
        + "xsi:schemaLocation='http://www.wetator.org/xsd/default default.xsd' />")).getSchemas();
    Assert.assertEquals(1, tmpSchemas.size());
    XMLSchema tmpSchema = tmpSchemas.get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/default", tmpSchema.getNamespace());
    Assert.assertEquals(null, tmpSchema.getPrefix());
    Assert.assertEquals("default.xsd", tmpSchema.getLocation());
  }

  @Test
  public void prefixSchema() throws IOException, XMLStreamException {
    List<XMLSchema> tmpSchemas = new SchemaFinder(new StringReader("<content " //
        + "xmlns:d='http://www.wetator.org/xsd/default' " //
        + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " //
        + "xsi:schemaLocation='http://www.wetator.org/xsd/default default.xsd' />")).getSchemas();
    Assert.assertEquals(1, tmpSchemas.size());
    XMLSchema tmpSchema = tmpSchemas.get(0);
    Assert.assertEquals("http://www.wetator.org/xsd/default", tmpSchema.getNamespace());
    Assert.assertEquals("d", tmpSchema.getPrefix());
    Assert.assertEquals("default.xsd", tmpSchema.getLocation());
  }
}