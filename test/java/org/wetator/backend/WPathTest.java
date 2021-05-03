/*
 * Copyright (c) 2008-2021 wetator.org
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


package org.wetator.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class WPathTest {

  protected WetatorConfiguration config;

  /**
   * Creates a Wetator configuration.
   */
  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  @Test(expected = InvalidInputException.class)
  public void null1() throws Exception {
    new WPath(null, config);
  }

  @Test
  public void empty() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString(null), config);

    assertTrue(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertNull(tmpWPath.getLastNode());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString(""), config);

    assertTrue(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertNull(tmpWPath.getLastNode());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void lastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathEmptyLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString(">"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("> last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathEmptyLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path >"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathLastNodeNoWhiteSpace() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path>last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void multiplePathLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path1 > path2 > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(2, tmpWPath.getPathNodes().size());
    assertEquals("path1", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("path2", tmpWPath.getPathNodes().get(1).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(0, tmpWPath.getTableCoordinates().size());
    assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void table() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("[x;y]"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertNull(tmpWPath.getLastNode());
    assertEquals(1, tmpWPath.getTableCoordinates().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void tableLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("[x;y] > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(1, tmpWPath.getTableCoordinates().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void multipleTableLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("[x1;y1] > [x2;y2] > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(0, tmpWPath.getPathNodes().size());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(2, tmpWPath.getTableCoordinates().size());
    assertEquals("[x1;y1]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals("[x2;y2]", tmpWPath.getTableCoordinates().get(1).toString());
    assertEquals(2, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x2;y2]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
    assertEquals("[x1;y1]", tmpWPath.getTableCoordinatesReversed().get(1).toString());
  }

  @Test
  public void pathTableLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path > [x;y] > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(1, tmpWPath.getTableCoordinates().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void tablePathLastNode() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("[x;y] > path > last"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(1, tmpWPath.getPathNodes().size());
    assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("last", tmpWPath.getLastNode().getValue());
    assertEquals(1, tmpWPath.getTableCoordinates().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test(expected = InvalidInputException.class)
  public void tablePathTableLastNode() throws Exception {
    new WPath(new SecretString("[x;y] > path > [x;y] > last"), config);
  }

  @Test
  public void pathTablePathTable() throws Exception {
    final WPath tmpWPath = new WPath(new SecretString("path1 > [x1;y1] > path2 > [x2;y2]"), config);

    assertFalse(tmpWPath.isEmpty());
    assertEquals(2, tmpWPath.getPathNodes().size());
    assertEquals("path1", tmpWPath.getPathNodes().get(0).getValue());
    assertEquals("path2", tmpWPath.getPathNodes().get(1).getValue());
    assertNull(tmpWPath.getLastNode());
    assertEquals(2, tmpWPath.getTableCoordinates().size());
    assertEquals("[x1;y1]", tmpWPath.getTableCoordinates().get(0).toString());
    assertEquals("[x2;y2]", tmpWPath.getTableCoordinates().get(1).toString());
    assertEquals(2, tmpWPath.getTableCoordinatesReversed().size());
    assertEquals("[x2;y2]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
    assertEquals("[x1;y1]", tmpWPath.getTableCoordinatesReversed().get(1).toString());
  }
}
