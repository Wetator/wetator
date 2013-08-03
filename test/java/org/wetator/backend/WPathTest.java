/*
 * Copyright (c) 2008-2013 wetator.org
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

import org.junit.Assert;
import org.junit.Test;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author frank.danek
 */
public class WPathTest {

  @Test(expected = InvalidInputException.class)
  public void null1() throws Exception {
    new WPath(null);
  }

  @Test
  public void empty() throws Exception {
    WPath tmpWPath = new WPath(new SecretString(null));

    Assert.assertTrue(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString(""));

    Assert.assertTrue(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void lastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathEmptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString(","));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString(",last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathEmptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("path,"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("path,last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void multiplePathLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("path1,path2,last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path1", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("path2", tmpWPath.getPathNodes().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void table() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("[x;y]"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void tableLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("[x;y],last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void multipleTableLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("[x1;y1],[x2;y2],last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(2, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x1;y1]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals("[x2;y2]", tmpWPath.getTableCoordinates().get(1).toString());
    Assert.assertEquals(2, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x2;y2]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
    Assert.assertEquals("[x1;y1]", tmpWPath.getTableCoordinatesReversed().get(1).toString());
  }

  @Test
  public void pathTableLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("path,[x;y],last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void tablePathLastNode() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("[x;y],path,last"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test(expected = InvalidInputException.class)
  public void tablePathTableLastNode() throws Exception {
    new WPath(new SecretString("[x;y],path,[x;y],last"));
  }

  @Test
  public void pathTablePathTable() throws Exception {
    WPath tmpWPath = new WPath(new SecretString("path1,[x1;y1],path2,[x2;y2]"));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path1", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("path2", tmpWPath.getPathNodes().get(1).getValue());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(2, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x1;y1]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals("[x2;y2]", tmpWPath.getTableCoordinates().get(1).toString());
    Assert.assertEquals(2, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x2;y2]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
    Assert.assertEquals("[x1;y1]", tmpWPath.getTableCoordinatesReversed().get(1).toString());
  }
}
