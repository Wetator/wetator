/*
 * Copyright (c) 2008-2025 wetator.org
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

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests for the {@link LocalEntityResolver}.
 *
 * @author frank.danek
 * @author tobwoerk
 */
public class LocalEntityResolverTest {

  @Test
  public void unknownAllIds() throws SAXException, IOException {
    Assert.assertNull(new LocalEntityResolver().resolveEntity("unknown", "unknown"));
  }

  @Test
  public void unknownPublicId() throws SAXException, IOException {
    Assert.assertNull(new LocalEntityResolver().resolveEntity("unknown", "test-case-1.0.0.xsd"));
  }

  @Test
  public void unknownSystemId() throws SAXException, IOException {
    Assert.assertNull(new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/test-case", "unknown"));
  }

  @Test
  public void exactLocal() throws SAXException, IOException {
    final InputSource tmpInputSource = new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/test-case",
        "test-case-1.0.0.xsd");
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpInputSource.getPublicId());
    Assert.assertTrue(tmpInputSource.getSystemId().endsWith("xsd/test-case-1.0.0.xsd"));
    Assert.assertNotNull(tmpInputSource.getByteStream());
  }

  @Test
  public void slashLocal() throws SAXException, IOException {
    final InputSource tmpInputSource = new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/test-case",
        "something/test-case-1.0.0.xsd");
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpInputSource.getPublicId());
    Assert.assertTrue(tmpInputSource.getSystemId().endsWith("xsd/test-case-1.0.0.xsd"));
    Assert.assertNotNull(tmpInputSource.getByteStream());
  }

  @Test
  public void backslashLocal() throws SAXException, IOException {
    final InputSource tmpInputSource = new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/test-case",
        "something\\test-case-1.0.0.xsd");
    Assert.assertEquals("http://www.wetator.org/xsd/test-case", tmpInputSource.getPublicId());
    Assert.assertTrue(tmpInputSource.getSystemId().endsWith("xsd/test-case-1.0.0.xsd"));
    Assert.assertNotNull(tmpInputSource.getByteStream());
  }

  @Test
  public void file() throws SAXException, IOException {
    final InputSource tmpInputSource = new LocalEntityResolver().resolveEntity(
        "http://www.wetator.org/xsd/junit-test-command-set", "src/test/resources/junit-test-command-set.xsd");
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpInputSource.getPublicId());
    Assert.assertTrue(tmpInputSource.getSystemId().endsWith("test/resources/junit-test-command-set.xsd"));
  }

  @Test
  public void unknownFile() throws SAXException, IOException {
    Assert.assertNull(new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/junit-test-command-set",
        "never/found/junit-test-command-set.xsd"));
  }

  @Test
  public void url() throws SAXException, IOException {
    final InputSource tmpInputSource = new LocalEntityResolver().resolveEntity(
        "http://www.wetator.org/xsd/junit-test-command-set", "http://www.wetator.org/xsd/junit-test-command-set.xsd");
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set", tmpInputSource.getPublicId());
    Assert.assertEquals("http://www.wetator.org/xsd/junit-test-command-set.xsd", tmpInputSource.getSystemId());
  }

  @Test
  public void invalidUrl() throws SAXException, IOException {
    Assert.assertNull(new LocalEntityResolver().resolveEntity("http://www.wetator.org/xsd/junit-test-command-set",
        "h1t2t3p://www.wetator.org/xsd/junit-test-command-set.xsd"));
  }
}
