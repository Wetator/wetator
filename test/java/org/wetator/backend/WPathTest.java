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


package org.wetator.backend;

import java.util.ArrayList;
import java.util.Arrays;

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
    WPath tmpWPath = new WPath(new ArrayList<SecretString>());

    Assert.assertTrue(tmpWPath.isEmpty());
    Assert.assertEquals(0, tmpWPath.getRawPath().size());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getRawPath().size());
    Assert.assertEquals("", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void lastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getRawPath().size());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathEmptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("", false), new SecretString("", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getRawPath().size());
    Assert.assertEquals("", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void emptyPathLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("", false), new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getRawPath().size());
    Assert.assertEquals("", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathEmptyLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("path", false), new SecretString("", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getRawPath().size());
    Assert.assertEquals("path", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void pathLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("path", false), new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getRawPath().size());
    Assert.assertEquals("path", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals(1, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void multiplePathLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("path1", false), new SecretString("path2", false),
        new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(3, tmpWPath.getRawPath().size());
    Assert.assertEquals("path1", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("path2", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(2).getValue());
    Assert.assertEquals(2, tmpWPath.getPathNodes().size());
    Assert.assertEquals("path1", tmpWPath.getPathNodes().get(0).getValue());
    Assert.assertEquals("path2", tmpWPath.getPathNodes().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(0, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals(0, tmpWPath.getTableCoordinatesReversed().size());
  }

  @Test
  public void table() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("[x;y]", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(1, tmpWPath.getRawPath().size());
    Assert.assertEquals("[x;y]", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertNull(tmpWPath.getLastNode());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void tableLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("[x;y]", false), new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(2, tmpWPath.getRawPath().size());
    Assert.assertEquals("[x;y]", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals(0, tmpWPath.getPathNodes().size());
    Assert.assertEquals("last", tmpWPath.getLastNode().getValue());
    Assert.assertEquals(1, tmpWPath.getTableCoordinates().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinates().get(0).toString());
    Assert.assertEquals(1, tmpWPath.getTableCoordinatesReversed().size());
    Assert.assertEquals("[x;y]", tmpWPath.getTableCoordinatesReversed().get(0).toString());
  }

  @Test
  public void multipleTableLastNode() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("[x1;y1]", false), new SecretString("[x2;y2]", false),
        new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(3, tmpWPath.getRawPath().size());
    Assert.assertEquals("[x1;y1]", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("[x2;y2]", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(2).getValue());
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
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("path", false), new SecretString("[x;y]", false),
        new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(3, tmpWPath.getRawPath().size());
    Assert.assertEquals("path", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("[x;y]", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(2).getValue());
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
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("[x;y]", false), new SecretString("path", false),
        new SecretString("last", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(3, tmpWPath.getRawPath().size());
    Assert.assertEquals("[x;y]", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("path", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals("last", tmpWPath.getRawPath().get(2).getValue());
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
    new WPath(Arrays.asList(new SecretString("[x;y]", false), new SecretString("path", false), new SecretString(
        "[x;y]", false), new SecretString("last", false)));
  }

  @Test
  public void pathTablePathTable() throws Exception {
    WPath tmpWPath = new WPath(Arrays.asList(new SecretString("path1", false), new SecretString("[x1;y1]", false),
        new SecretString("path2", false), new SecretString("[x2;y2]", false)));

    Assert.assertFalse(tmpWPath.isEmpty());
    Assert.assertEquals(4, tmpWPath.getRawPath().size());
    Assert.assertEquals("path1", tmpWPath.getRawPath().get(0).getValue());
    Assert.assertEquals("[x1;y1]", tmpWPath.getRawPath().get(1).getValue());
    Assert.assertEquals("path2", tmpWPath.getRawPath().get(2).getValue());
    Assert.assertEquals("[x2;y2]", tmpWPath.getRawPath().get(3).getValue());
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
