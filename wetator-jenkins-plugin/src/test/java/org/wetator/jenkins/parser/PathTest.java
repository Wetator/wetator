/*
 * Copyright (c) 2008-2014 wetator.org
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


package org.wetator.jenkins.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author frank.danek
 */
public class PathTest {

  @Test
  public void empty() {
    Path tmpPath = new Path();
    Assert.assertEquals("", tmpPath.toString());
  }

  @Test
  public void push() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    Assert.assertEquals("/test", tmpPath.toString());
  }

  @Test
  public void pop() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    Assert.assertEquals("test", tmpPath.pop());
    Assert.assertEquals("", tmpPath.toString());
  }

  @Test
  public void level2() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    tmpPath.push("foo");
    Assert.assertEquals("/test/foo", tmpPath.toString());
    Assert.assertEquals("foo", tmpPath.pop());
    Assert.assertEquals("/test", tmpPath.toString());
    Assert.assertEquals("test", tmpPath.pop());
    Assert.assertEquals("", tmpPath.toString());
  }

  @Test
  public void matchesEmpty() {
    Path tmpPath = new Path();
    Assert.assertTrue(tmpPath.matches(""));
  }

  @Test
  public void matches() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    Assert.assertTrue(tmpPath.matches("/test"));
  }

  @Test
  public void startsWithEmpty() {
    Path tmpPath = new Path();
    Assert.assertTrue(tmpPath.startsWith(""));
  }

  @Test
  public void startsWith() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    tmpPath.push("foo");
    Assert.assertTrue(tmpPath.startsWith("/test"));
    Assert.assertFalse(tmpPath.startsWith("/foo"));
  }

  @Test
  public void endsWithEmpty() {
    Path tmpPath = new Path();
    Assert.assertTrue(tmpPath.endsWith(""));
  }

  @Test
  public void endsWith() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    tmpPath.push("foo");
    Assert.assertTrue(tmpPath.endsWith("/foo"));
    Assert.assertFalse(tmpPath.endsWith("/test"));
  }

  @Test
  public void isEmpty() {
    Path tmpPath = new Path();
    Assert.assertTrue(tmpPath.isEmpty());
  }

  @Test
  public void isEmptyNotEmpty() {
    Path tmpPath = new Path();
    tmpPath.push("test");
    Assert.assertFalse(tmpPath.isEmpty());
  }
}
